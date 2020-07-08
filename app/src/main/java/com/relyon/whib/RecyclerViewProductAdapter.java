package com.relyon.whib;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.relyon.whib.util.Constants.AMEI;
import static com.relyon.whib.util.Constants.APROVE;
import static com.relyon.whib.util.Constants.BURRO;
import static com.relyon.whib.util.Constants.CONGRATS;
import static com.relyon.whib.util.Constants.DEFINITION;
import static com.relyon.whib.util.Constants.DISLIKE;
import static com.relyon.whib.util.Constants.LACRADA;
import static com.relyon.whib.util.Constants.LIKE;
import static com.relyon.whib.util.Constants.LOL;
import static com.relyon.whib.util.Constants.REFLEXAO;

public class RecyclerViewProductAdapter extends RecyclerView.Adapter<RecyclerViewProductAdapter.ViewHolder> implements RewardedVideoAdListener {

    private List<Product> elements;
    private StorageReference storageReference;
    private Context context;
    private List<SkuDetails> skuDetails;
    private RewardedVideoAd mRewardedVideoAd;
    private int selected;
    private Activity activity;
    private Product product;
    private BillingClient billingClient;

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

        PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> purchases) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                        && purchases != null) {
                    for (Purchase purchase : purchases) {
                        handlePurchase(purchase);
                    }
                } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                    Toast.makeText(context, "Parece que você mudou de ideia. Tente novamente.", Toast.LENGTH_LONG).show();
                } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED && purchases != null) {
                    for (Purchase purchase : purchases) {
                        handlePurchase(purchase);
                    }
                }
            }
        };
        billingClient = BillingClient.newBuilder(context)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();

        storageReference = FirebaseStorage.getInstance().getReference();

        List<String> skuList = new ArrayList<>();
        skuList.add(LIKE);
        skuList.add(DISLIKE);
        skuList.add(AMEI);
        skuList.add(APROVE);
        skuList.add(LACRADA);
        skuList.add(LOL);
        skuList.add(DEFINITION);
        skuList.add(CONGRATS);
        skuList.add(BURRO);
        skuList.add(REFLEXAO);

        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
        billingClient.querySkuDetailsAsync(params.build(),
                (billingResult, skuDetailsList) -> skuDetails = skuDetailsList);
    }

    private void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
                if (Util.getUser().getProducts() == null) {
                    Util.getUser().setProducts(new HashMap<>());
                }
                if (Util.getUser().getProducts().get(purchase.getSku()) != null) {
                    Util.getUser().getProducts().get(purchase.getSku()).setQuantity(Util.getUser().getProducts().get(purchase.getSku()).getQuantity() + 5);
                    Util.mUserDatabaseRef.child(Util.getUser().getUserUID()).child("products").child(purchase.getSku()).child("quantity").setValue(Util.getUser().getProducts().get(purchase.getSku()).getQuantity());
                } else {
                    if (product != null) {
                        product.setQuantity(5);
                        Util.getUser().getProducts().put(purchase.getSku(), product);
                        Util.mUserDatabaseRef.child(Util.getUser().getUserUID()).child("products").child(purchase.getSku()).setValue(product);
                    }
                }
            }
        }
    }

    private AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener = billingResult -> {
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_store, parent, false);
        return new ViewHolder(rowView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewProductAdapter.ViewHolder holder, int position) {
        product = elements.get(position);

        storageReference.child("images/" + product.getItemSKU() + ".png").getDownloadUrl().addOnSuccessListener(uri -> Glide.with(context).load(uri).into(holder.image));

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
            } else if (skuDetails != null){
                for (SkuDetails skuDetails : skuDetails) {
                    if (skuDetails.getSku().equals(product.getItemSKU())) {
                        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                                .setSkuDetails(skuDetails)
                                .build();
                        billingClient.launchBillingFlow(activity, billingFlowParams).getResponseCode();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return elements.size();
    }


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
                if (myProduct != null) {
                    Util.mUserDatabaseRef.child(Util.getUser().getUserUID()).child("products").child(myProduct.getProductUID()).setValue(myProduct);
                }
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

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-1676578761693318/3017939298",
                new AdRequest.Builder().build());
    }
}