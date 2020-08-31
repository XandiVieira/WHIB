package com.relyon.whib.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.material.tabs.TabLayout;
import com.relyon.whib.R;
import com.relyon.whib.adapter.SectionPagerAdapter;
import com.relyon.whib.dialog.DialogCongratsSubscription;
import com.relyon.whib.dialog.DialogFinalWarn;
import com.relyon.whib.util.Util;
import com.relyon.whib.util.Constants;
import com.relyon.whib.util.SelectSubscription;

public class SettingsActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler, SelectSubscription {

    private BillingProcessor billingProcessor;

    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        billingProcessor = new BillingProcessor(this, getResources().getString(R.string.google_license_key), this);
        billingProcessor.initialize();

        SectionPagerAdapter mSectionsPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());

        ViewPager mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        setLayoutAttributes();

        if (getIntent().hasExtra(Constants.SHOW_LAST_WARN) && getIntent().getBooleanExtra(Constants.SHOW_LAST_WARN, false)) {
            DialogFinalWarn warn = new DialogFinalWarn(this);
            warn.show();
        }

        back.setOnClickListener(v -> onBackPressed());
    }

    private void setLayoutAttributes() {
        back = findViewById(R.id.back);
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
    public void onProductPurchased(@NonNull String productId, TransactionDetails details) {
        Util.mUserDatabaseRef.child(Util.getUser().getUserUID()).child(Constants.DATABASE_REF_EXTRA).setValue(true);
        Util.getUser().setExtra(true);
        new DialogCongratsSubscription(this).show();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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