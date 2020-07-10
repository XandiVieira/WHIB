package com.relyon.whib;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.material.tabs.TabLayout;
import com.relyon.whib.modelo.Util;
import com.relyon.whib.util.SelectSubscription;

public class SettingsActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler, SelectSubscription {

    private BillingProcessor billingProcessor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        billingProcessor = new BillingProcessor(getApplicationContext(), getResources().getString(R.string.google_license_key), this);
        billingProcessor.initialize();

        SectionPagerAdapter mSectionsPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        ImageView back = findViewById(R.id.back);

        back.setOnClickListener(v -> {
            Intent intent;
            if (Util.getServer() != null) {
                intent = new Intent(getApplicationContext(), TimelineActivity.class);
            } else {
                intent = new Intent(getApplicationContext(), MainActivity.class);
            }
            startActivity(intent);
        });
    }

    private void purchase(String sku) {
        billingProcessor.subscribe(this, sku);
    }

    @Override
    public void onChoose(String sku) {
        purchase(sku);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (billingProcessor.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroy() {
        if (billingProcessor != null) {
            billingProcessor.release();
        }
        super.onDestroy();
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        Util.mUserDatabaseRef.child(Util.getUser().getUserUID()).child("extra").setValue(true);
        Util.getUser().setExtra(true);
        Toast.makeText(getApplicationContext(), "Parabéns, você agora pode utilizar todos os recursos do WHIB", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {

    }

    @Override
    public void onBillingInitialized() {

    }
}