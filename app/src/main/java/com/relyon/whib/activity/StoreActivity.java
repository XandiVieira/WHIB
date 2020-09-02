package com.relyon.whib.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.relyon.whib.R;
import com.relyon.whib.adapter.SectionPagerStoreAdapter;
import com.relyon.whib.dialog.DialogCongratsSubscription;
import com.relyon.whib.dialog.DialogFinalWarn;
import com.relyon.whib.modelo.Product;
import com.relyon.whib.modelo.User;
import com.relyon.whib.util.Constants;
import com.relyon.whib.util.SelectSubscription;
import com.relyon.whib.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class StoreActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler, SelectSubscription {

    private Activity activity;
    private BillingProcessor billingProcessor;

    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private ImageView back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        activity = this;

        setLayoutAttributes();

        initBillingProcessor();

        setupPagerAdapter();

        if (getIntent().hasExtra(Constants.SHOW_LAST_WARN) && getIntent().getBooleanExtra(Constants.SHOW_LAST_WARN, false) && Util.getUser().isFirstTime()) {
            DialogFinalWarn warn = new DialogFinalWarn(this);
            warn.show();
        }

        back.setOnClickListener(v -> onBackPressed());
    }

    private void setLayoutAttributes() {
        mViewPager = findViewById(R.id.container);
        tabLayout = findViewById(R.id.tabs);
        back = findViewById(R.id.back);
    }

    private void initBillingProcessor() {
        billingProcessor = new BillingProcessor(this, getResources().getString(R.string.google_license_key), this);
        billingProcessor.initialize();
    }

    private void setupPagerAdapter() {
        SectionPagerStoreAdapter sectionsPagerAdapter = new SectionPagerStoreAdapter(getSupportFragmentManager(), getApplicationContext());
        mViewPager.setAdapter(sectionsPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        if (getIntent().hasExtra(Constants.SHOW_ADVANTAGES) && getIntent().getBooleanExtra(Constants.SHOW_ADVANTAGES, false)) {
            TabLayout.Tab tabWhibExtra = tabLayout.getTabAt(1);
            if (tabWhibExtra != null) {
                tabWhibExtra.select();
            }
        }
    }

    @Override
    public void onProductPurchased(@NonNull String productId, TransactionDetails details) {
        if (isSubscription(productId)) {
            Util.mUserDatabaseRef.child(Util.getUser().getUserUID()).child(Constants.DATABASE_REF_EXTRA).setValue(true);
            Util.getUser().setExtra(true);
            new DialogCongratsSubscription(this).show();
        } else {
            Util.mDatabaseRef.child(Constants.DATABASE_REF_PRODUCT).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        Product product = snap.getValue(Product.class);
                        if (product != null && product.getItemSKU().equals(productId)) {
                            rewardItem(product, Util.getUser().isExtra() ? 2 : 1);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private boolean isSubscription(String productId) {
        List<String> subscriptionOptions = new ArrayList<>();
        subscriptionOptions.add(Constants.SKU_WHIB_YEARLY);
        subscriptionOptions.add(Constants.SKU_WHIB_SIXMONTH);
        subscriptionOptions.add(Constants.SKU_WHIB_MONTHLY);
        return subscriptionOptions.contains(productId);
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        Toast.makeText(this, R.string.there_was_an_error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBillingInitialized() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (billingProcessor.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void rewardItem(Product product, int quantity) {
        Util.mUserDatabaseRef.child(Util.getUser().getUserUID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Util.mUserDatabaseRef.child(Util.getUser().getUserUID()).removeEventListener(this);
                User user = dataSnapshot.getValue(User.class);
                Product myProduct;
                if (user != null) {
                    if (user.getProducts() == null) {
                        user.setProducts(new HashMap<>());
                    }
                    myProduct = product.isContained(user.getProducts());
                    if (myProduct == null) {
                        myProduct = product;
                    }
                    myProduct.setQuantity(myProduct.getQuantity() + (quantity * 5));
                    if (user.getProducts().get(myProduct.getProductUID()) != null) {
                        Objects.requireNonNull(user.getProducts().get(myProduct.getProductUID())).setQuantity(myProduct.getQuantity());
                    } else {
                        user.getProducts().put(myProduct.getProductUID(), myProduct);
                    }
                    Util.getUser().setProducts(user.getProducts());
                    Util.mUserDatabaseRef.child(Util.getUser().getUserUID()).child(Constants.DATABASE_REF_PRODUCTS).child(myProduct.getProductUID()).setValue(myProduct);
                    Toast.makeText(activity, R.string.stickers_were_added_to_your_gallery, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        if (billingProcessor != null) {
            billingProcessor.release();
        }
        super.onDestroy();
    }

    private void purchase(String sku) {
        if (isSubscription(sku)) {
            billingProcessor.subscribe(this, sku);
        } else {
            billingProcessor.purchase(this, sku);
        }
    }

    @Override
    public void onChoose(String sku) {
        purchase(sku);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent intent;
        if (getIntent().hasExtra(Constants.CAME_FROM_PROFILE) && getIntent().getBooleanExtra(Constants.CAME_FROM_PROFILE, false)) {
            intent = new Intent(this, ProfileActivity.class);
        } else if (Util.getServer() != null) {
            intent = new Intent(this, TimelineActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        startActivity(intent);
    }
}