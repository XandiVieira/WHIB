package com.relyon.whib;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.relyon.whib.modelo.Product;
import com.relyon.whib.modelo.Util;
import com.relyon.whib.util.IabBroadcastReceiver;
import com.relyon.whib.util.IabHelper;
import com.relyon.whib.util.IabResult;
import com.relyon.whib.util.Inventory;
import com.relyon.whib.util.Purchase;

import java.util.Date;
import java.util.List;

import static com.relyon.whib.util.Constants.SKU_WHIB_MONTHLY;
import static com.relyon.whib.util.Constants.SKU_WHIB_SIXMONTH;
import static com.relyon.whib.util.Constants.SKU_WHIB_YEARLY;
import static com.relyon.whib.util.Constants.base64EncodedPublicKey;

public class RecyclerViewProductAdapter extends RecyclerView.Adapter<RecyclerViewProductAdapter.ViewHolder> implements IabBroadcastReceiver.IabBroadcastListener {

    private List<Product> elements;
    private StorageReference storageReference;
    private Context context;
    private Activity activity;

    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10001;
    // The helper object
    IabHelper mHelper;
    // Provides purchase notification while this app is running
    IabBroadcastReceiver mBroadcastReceiver;
    static final String TAG = "StoreActivity";

    public RecyclerViewProductAdapter(List<Product> elements, Context context, Activity activity) {
        this.elements = elements;
        this.context = context;
        this.activity = activity;
        storageReference = FirebaseStorage.getInstance().getReference();
        // Create the helper, passing it our context and the public key to verify signatures with
        Log.d(TAG, "Creating IAB helper.");
        mHelper = new IabHelper(context, base64EncodedPublicKey);

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(true);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        Log.d(TAG, "Starting setup.");
        mHelper.startSetup(result -> {
            Log.d(TAG, "Setup finished.");

            if (!result.isSuccess()) {
                // Oh noes, there was a problem.
                complain("Problem setting up in-app billing: " + result);
                return;
            }

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            mBroadcastReceiver = new IabBroadcastReceiver(this);
            IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
            context.registerReceiver(mBroadcastReceiver, broadcastFilter);

            // IAB is fully set up. Now, let's get an inventory of stuff we own.
            Log.d(TAG, "Setup successful. Querying inventory.");
            try {
                mHelper.queryInventoryAsync(mGotInventoryListener);
            } catch (IabHelper.IabAsyncInProgressException e) {
                complain("Error querying inventory. Another async operation in progress.");
            }
        });
    }

    private void complain(String message) {
        Log.e(TAG, "**** whib Error: " + message);
        alert("Error: " + message);
    }

    private void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(context);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_store, parent, false);
        return new ViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewProductAdapter.ViewHolder holder, int position) {
        Product product = elements.get(position);

        storageReference.child("images/" + product.getTitle()).getDownloadUrl().addOnSuccessListener(uri -> Glide.with(context).load(uri).into(holder.image));

        holder.title.setText(product.getTitle());
        holder.description.setText(product.getDescription());
        if (product.getPrice() == 0) {
            holder.price.setText(context.getResources().getString(R.string.free));
        } else {
            holder.price.setText("R$ " + String.format("%.2f", product.getPrice()));
        }
        holder.price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mHelper.launchPurchaseFlow(activity, elements.get(position).getItemSKU(), IabHelper.ITEM_TYPE_INAPP,
                            null, RC_REQUEST, mPurchaseFinishedListener, "");
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Error launching purchase flow. Another async operation in progress.");
                }
            }
        });
    }

    // Callback for when a purchase is finished
    private IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
                complain("Error purchasing: " + result);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                complain("Error purchasing. Authenticity verification failed.");
                return;
            }

            Log.d(TAG, "Purchase successful.");

            Toast.makeText(context, "Compra realizada com sucesso.", Toast.LENGTH_SHORT).show();

            for (Product product : elements) {
                if (product.getItemSKU().equals(purchase.getSku())) {
                    product.setQuantity(5);
                    product.setPurchaseDate(new Date().getTime());
                    Util.getUser().getProducts().add(product);
                    Util.mUserDatabaseRef.child(Util.getUser().getUserUID()).child("products").push().setValue(product);
                }
            }
        }
    };

    @Override
    public int getItemCount() {
        return elements.size();
    }

    @Override
    public void receivedBroadcast() {
        Log.d(TAG, "Received broadcast notification. Querying inventory.");
        try {
            mHelper.queryInventoryAsync(mGotInventoryListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            complain("Error querying inventory. Another async operation in progress.");
        }
    }

    // Listener that's called when we finish querying the items and subscriptions we own
    private IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) {
                return;
            }

            // Is it a failure?
            if (result.isFailure()) {
                complain("Failed to query inventory: " + result);
                return;
            }

            Log.d(TAG, "Query inventory was successful.");

            // First find out which subscription is auto renewing
            Purchase whibMonthly = inventory.getPurchase(SKU_WHIB_MONTHLY);
            Purchase whibSixMonth = inventory.getPurchase(SKU_WHIB_SIXMONTH);
            Purchase whibYearly = inventory.getPurchase(SKU_WHIB_YEARLY);

            Log.d(TAG, "Initial inventory query finished; enabling main UI.");
        }
    };

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private TextView title;
        private TextView description;
        private Button price;

        ViewHolder(View rowView) {
            super(rowView);
            image = rowView.findViewById(R.id.image);
            title = rowView.findViewById(R.id.title);
            description = rowView.findViewById(R.id.description);
            price = rowView.findViewById(R.id.price);
        }
    }

    private boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();
        return true;
    }
}