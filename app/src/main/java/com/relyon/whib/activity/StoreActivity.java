package com.relyon.whib.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.relyon.whib.R;
import com.relyon.whib.adapter.RecyclerViewProductAdapter;
import com.relyon.whib.dialog.DialogFinalWarn;
import com.relyon.whib.modelo.Product;
import com.relyon.whib.modelo.User;
import com.relyon.whib.util.Util;
import com.relyon.whib.util.Constants;
import com.relyon.whib.util.SelectSubscription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class StoreActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler, SelectSubscription {

    private Activity activity;
    private List<Product> productList;
    private BillingProcessor billingProcessor;

    private RecyclerView productsRV;
    private ProgressBar progressBar;
    private ImageView back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        activity = this;

        billingProcessor = new BillingProcessor(this, getString(R.string.google_license_key), this);
        billingProcessor.initialize();

        setLayoutAttributes();

        retrieveProducts();

        if (getIntent().hasExtra(Constants.SHOW_LAST_WARN) && getIntent().getBooleanExtra(Constants.SHOW_LAST_WARN, false) && Util.getUser().isFirstTime()) {
            DialogFinalWarn warn = new DialogFinalWarn(this);
            warn.show();
        }

        back.setOnClickListener(v -> onBackPressed());
    }

    private void setLayoutAttributes() {
        productsRV = findViewById(R.id.products);
        progressBar = findViewById(R.id.progress_bar);
        back = findViewById(R.id.back);
    }

    private void retrieveProducts() {
        Util.mDatabaseRef.child(Constants.DATABASE_REF_PRODUCT).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList = new ArrayList<>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Product product = snap.getValue(Product.class);
                    if (product != null) {
                        productList.add(product);
                    }
                }
                setProductAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setProductAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
        RecyclerViewProductAdapter adapter = new RecyclerViewProductAdapter(productList, activity, StoreActivity.this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(productsRV.getContext(),
                layoutManager.getOrientation());
        productsRV.addItemDecoration(dividerItemDecoration);
        productsRV.setLayoutManager(layoutManager);
        productsRV.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
        productsRV.setVisibility(View.VISIBLE);
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

    @Override
    public void onProductPurchased(@NonNull String productId, TransactionDetails details) {
        for (Product product : productList) {
            if (product.getItemSKU().equals(productId)) {
                rewardItem(product, Util.getUser().isExtra() ? 2 : 1);
            }
        }
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        Toast.makeText(this, "Ops! Houve um erro, por favor tente novamente.", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(activity, "As figurinhas foram adicionadas à sua galeria com sucesso", Toast.LENGTH_LONG).show();
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
        billingProcessor.purchase(this, sku);
    }

    @Override
    public void onChoose(String sku) {
        purchase(sku);
    }
}