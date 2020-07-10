package com.relyon.whib;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
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
import com.relyon.whib.modelo.Product;
import com.relyon.whib.modelo.User;
import com.relyon.whib.modelo.Util;
import com.relyon.whib.util.SelectSubscription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StoreActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler, SelectSubscription {

    private RecyclerView productsRV;
    private List<Product> productList = new ArrayList<>();
    private BillingProcessor billingProcessor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        billingProcessor = new BillingProcessor(getApplicationContext(), getString(R.string.google_license_key), this);
        billingProcessor.initialize();

        productsRV = findViewById(R.id.products);
        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent intent;
        if (Util.getServer() != null) {
            intent = new Intent(getApplicationContext(), TimelineActivity.class);
        } else {
            intent = new Intent(getApplicationContext(), MainActivity.class);
        }
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Util.mDatabaseRef.child("product").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Product product = snap.getValue(Product.class);
                    if (product != null) {
                        productList.add(product);
                    }
                }
                LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                RecyclerViewProductAdapter adapter = new RecyclerViewProductAdapter(productList, getApplicationContext(), StoreActivity.this);
                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(productsRV.getContext(),
                        layoutManager.getOrientation());
                productsRV.addItemDecoration(dividerItemDecoration);
                productsRV.setLayoutManager(layoutManager);
                productsRV.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
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
        Toast.makeText(getApplicationContext(), "Ops! Houve um erro, por favor tente novamente.", Toast.LENGTH_SHORT).show();
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
                User user = dataSnapshot.getValue(User.class);
                Product myProduct = null;
                if (user != null) {
                    if (user.getProducts() == null) {
                        user.setProducts(new HashMap<>());
                    }
                    myProduct = product.isContained(user.getProducts());
                    if (myProduct != null) {
                        myProduct.setQuantity(myProduct.getQuantity() + (quantity * 5));
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