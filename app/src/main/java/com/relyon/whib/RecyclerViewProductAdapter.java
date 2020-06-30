package com.relyon.whib;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.relyon.whib.modelo.Product;
import com.relyon.whib.modelo.User;
import com.relyon.whib.modelo.Util;
import com.relyon.whib.util.IabBroadcastReceiver;
import com.relyon.whib.util.IabHelper;
import com.relyon.whib.util.IabResult;
import com.relyon.whib.util.Inventory;
import com.relyon.whib.util.Purchase;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.relyon.whib.util.Constants.base64EncodedPublicKey;

public class RecyclerViewProductAdapter extends RecyclerView.Adapter<RecyclerViewProductAdapter.ViewHolder> implements IabBroadcastReceiver.IabBroadcastListener, RewardedVideoAdListener {

    private List<Product> elements;
    private StorageReference storageReference;
    private Context context;
    private Activity activity;
    private RewardedVideoAd mRewardedVideoAd;
    private int selected;

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

        List<String> testDeviceIds = Collections.singletonList("3DF6979E4CCB56C2A91510C1A9BCC253");
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();

        MobileAds.setRequestConfiguration(configuration);
        MobileAds.initialize(context, context.getString(R.string.admob_app_id));
        // Use an activity context to get the rewarded video instance.
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(context);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();

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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewProductAdapter.ViewHolder holder, int position) {
        Product product = elements.get(position);

        storageReference.child("images/" + product.getTitle()).getDownloadUrl().addOnSuccessListener(uri -> Glide.with(context).load(uri).into(holder.image));

        if (product.getPrice() == 0) {
            holder.icon.setImageDrawable(context.getDrawable(R.drawable.ic_video));
        } else {
            holder.icon.setVisibility(View.GONE);
        }
        holder.title.setText(product.getTitle());
        holder.description.setText(product.getDescription());
        if (product.getPrice() == 0) {
            holder.price.setText(context.getResources().getString(R.string.free));
        } else {
            holder.price.setText("R$ " + String.format("%.2f", product.getPrice()));
        }
        holder.buy.setOnClickListener(v -> {
            if (product.getPrice() == 0) {
                selected = position;
                if (mRewardedVideoAd.isLoaded()) {
                    mRewardedVideoAd.show();
                } else {
                    Toast.makeText(context, "Não há videos disponíveis, por favor tente novamente!", Toast.LENGTH_LONG).show();
                }
            } else {
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
                    Util.getUser().getProducts().put(product.getTitle(), product);
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

            Log.d(TAG, "Initial inventory query finished; enabling main UI.");
        }
    };

    @Override
    public void onRewardedVideoAdLoaded() {

    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        loadRewardedVideoAd();
        Toast.makeText(context, "Parabén, você ganhou um pacote de figurinhas.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        Product product = elements.get(selected);
        product.setQuantity(5);
        rewardItem(product, rewardItem);
    }

    private void rewardItem(Product product, RewardItem rewardItem) {
        Util.mUserDatabaseRef.child(Util.getUser().getUserUID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Product myProduct = null;
                if (user != null) {
                    if (user.getProducts() == null) {
                        user.setProducts(new HashMap<>());
                    }
                    myProduct = product.isContained(user.getProducts());
                    if (myProduct != null) {
                        myProduct.setQuantity(myProduct.getQuantity() + (rewardItem.getAmount() * 5));
                    } else {
                        myProduct = product;
                    }
                    Util.getUser().setProducts(user.getProducts());
                }
                Util.mUserDatabaseRef.child(Util.getUser().getUserUID()).removeEventListener(this);
                Util.mUserDatabaseRef.child(Util.getUser().getUserUID()).child("products").child(myProduct.getProductUID()).setValue(myProduct);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }

    @Override
    public void onRewardedVideoCompleted() {

    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private TextView title;
        private TextView description;
        private TextView price;
        private LinearLayout buy;
        private ImageView icon;

        ViewHolder(View rowView) {
            super(rowView);
            image = rowView.findViewById(R.id.image);
            title = rowView.findViewById(R.id.title);
            description = rowView.findViewById(R.id.description);
            price = rowView.findViewById(R.id.price);
            buy = rowView.findViewById(R.id.buy);
            icon = rowView.findViewById(R.id.icon);
        }
    }

    private boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();
        return true;
    }

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-1676578761693318/3017939298",
                new AdRequest.Builder().build());
    }
}