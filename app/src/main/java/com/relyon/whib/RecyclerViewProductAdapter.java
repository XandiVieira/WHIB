package com.relyon.whib;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.relyon.whib.util.SelectSubscription;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class RecyclerViewProductAdapter extends RecyclerView.Adapter<RecyclerViewProductAdapter.ViewHolder> implements RewardedVideoAdListener {

    private List<Product> elements;
    private StorageReference storageReference;
    private Context context;
    private RewardedVideoAd mRewardedVideoAd;
    private int selected;
    private Product product;
    private SelectSubscription listener;

    public RecyclerViewProductAdapter(List<Product> elements, Context context, Activity activity) {
        this.elements = elements;
        this.context = context;

        listener = (SelectSubscription) activity;

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
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_store, parent, false);
        return new ViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewProductAdapter.ViewHolder holder, int position) {
        product = elements.get(position);

        storageReference.child("images/" + product.getItemSKU() + ".png").getDownloadUrl().addOnSuccessListener(uri -> Glide.with(context).load(uri).into(holder.image));

        if (product.getPrice() == 0) {
            holder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_video));
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
                listener.onChoose(product.getItemSKU());
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