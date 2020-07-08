package com.relyon.whib;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.relyon.whib.modelo.Util;

import java.util.ArrayList;
import java.util.List;

import static com.relyon.whib.util.Constants.SKU_WHIB_MONTHLY;
import static com.relyon.whib.util.Constants.SKU_WHIB_SIXMONTH;
import static com.relyon.whib.util.Constants.SKU_WHIB_YEARLY;

public class DialogChooseSubscription extends DialogFragment {

    public Dialog d;
    private static final int NUM_PAGES = 4;
    private ViewPager mPager;
    private LinearLayout border1;
    private LinearLayout border2;
    private LinearLayout border3;
    private LinearLayout background;
    private Button confirm;
    private int color;
    private Thread runLayout;
    private List<SkuDetails> skuDetails;
    private BillingClient billingClient;

    // Tracks the currently owned subscription, and the options in the Manage dialog
    String mFirstChoiceSku = SKU_WHIB_MONTHLY;
    String mSecondChoiceSku = SKU_WHIB_SIXMONTH;
    String mThirdChoiceSku = SKU_WHIB_YEARLY;

    // Used to select between subscribing on a monthly, three month, six month or yearly basis
    String mSelectedSubscriptionPeriod = mSecondChoiceSku;

    public DialogChooseSubscription(Context context) {

        PurchasesUpdatedListener purchasesUpdatedListener = (billingResult, purchases) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                    && purchases != null) {
                for (Purchase purchase : purchases) {
                    handlePurchase(purchase);
                }
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                Toast.makeText(getContext(), "Parece que você mudou de ideia. Tente novamente caso queira ser extra", Toast.LENGTH_LONG).show();
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
                Toast.makeText(getContext(), "Parece que você já assinou a versão extra e pode usufruir das vantagens", Toast.LENGTH_LONG).show();
            }
        };
        billingClient = BillingClient.newBuilder(context)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();
    }

    public static DialogChooseSubscription newInstance(Context context) {
        DialogChooseSubscription frag = new DialogChooseSubscription(context);
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        if (dialog.getWindow() != null) {
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        return dialog;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle bundle) {
        View inflate = inflater.inflate(R.layout.dialog_choose_subscribe, parent);
        // Set to adjust screen height automatically, when soft keyboard appears on screen
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        List<String> skuList = new ArrayList<>();
        skuList.add(SKU_WHIB_MONTHLY);
        skuList.add(SKU_WHIB_SIXMONTH);
        skuList.add(SKU_WHIB_YEARLY);
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS);
        billingClient.querySkuDetailsAsync(params.build(),
                (billingResult, skuDetailsList) -> skuDetails = skuDetailsList);
        return inflate;
    }

    private void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
            }
        }
    }

    private AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener = billingResult -> {
        Util.getUser().setExtra(true);
        Util.mUserDatabaseRef.child(Util.getUser().getUserUID()).child("extra").setValue(true);
    };

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView noThanks = view.findViewById(R.id.noThanks);
        confirm = view.findViewById(R.id.confirm);
        border1 = view.findViewById(R.id.border1);
        border2 = view.findViewById(R.id.border2);
        border3 = view.findViewById(R.id.border3);
        background = view.findViewById(R.id.background);

        noThanks.setOnClickListener(v -> dismiss());

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = view.findViewById(R.id.pager);
        /*
      The pager adapter, which provides the pages to the view pager widget.
     */
        PagerAdapter mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                handleColors();
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        confirm.setOnClickListener(v -> {
            runLayout.interrupt();

            if (TextUtils.isEmpty(mSelectedSubscriptionPeriod)) {
                // The user has not changed from the default selection
                mSelectedSubscriptionPeriod = mFirstChoiceSku;
            }

            if (skuDetails != null) {
                for (SkuDetails skuDetails : skuDetails) {
                    if (skuDetails.getSku().equals(mSelectedSubscriptionPeriod)) {
                        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                                .setSkuDetails(skuDetails)
                                .build();
                        billingClient.launchBillingFlow(getActivity(), billingFlowParams).getResponseCode();
                    }
                }
            }

            // Reset the dialog options
            mSelectedSubscriptionPeriod = "";
            mFirstChoiceSku = "";
            mSecondChoiceSku = "";
        });

        border1.setOnClickListener(v -> {
            mSelectedSubscriptionPeriod = mFirstChoiceSku;
            changeColor(border1);
            border2.setBackground(getContext().getResources().getDrawable(R.drawable.corner_white));
            border3.setBackground(getContext().getResources().getDrawable(R.drawable.corner_white));
        });

        border2.setOnClickListener(v -> {
            mSelectedSubscriptionPeriod = mSecondChoiceSku;
            border1.setBackground(getContext().getResources().getDrawable(R.drawable.corner_white));
            changeColor(border2);
            border3.setBackground(getContext().getResources().getDrawable(R.drawable.corner_white));
        });

        border3.setOnClickListener(v -> {
            mSelectedSubscriptionPeriod = mThirdChoiceSku;
            border1.setBackground(getContext().getResources().getDrawable(R.drawable.corner_white));
            border2.setBackground(getContext().getResources().getDrawable(R.drawable.corner_white));
            changeColor(border3);
        });

        runLayout = new Thread(new Runnable() {
            @Override
            public void run() {
                //Esse métedo será executado a cada período, ponha aqui a sua lógica
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            Handler handler = new Handler(); //contador de tempo
                            handler.postDelayed(this, 4000); //o exemplo 2000 = 2 segundos
                            if (mPager.getCurrentItem() == 3) {
                                mPager.setCurrentItem(0);
                            } else {
                                mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                            }
                            if (getContext() != null) {
                                handleColors();
                            }
                        }
                    });
                }
            }
        });
        runLayout.start();
    }

    public void handleColors() {
        if (mPager.getCurrentItem() == 0) {
            background.setBackground(getContext().getResources().getDrawable(R.drawable.background_gradient_green));
            confirm.setBackground(getContext().getResources().getDrawable(R.drawable.background_gradient_green_horizontal));
            color = R.drawable.rounded_dark_green;
        } else if (mPager.getCurrentItem() == 1) {
            background.setBackground(getContext().getResources().getDrawable(R.drawable.background_gradient_orange));
            confirm.setBackground(getContext().getResources().getDrawable(R.drawable.background_gradient_orange_horizontal));
            color = R.drawable.rounded_accent;
        } else if (mPager.getCurrentItem() == 2) {
            background.setBackground(getContext().getResources().getDrawable(R.drawable.background_gradient_blue));
            confirm.setBackground(getContext().getResources().getDrawable(R.drawable.background_gradient_blue_horizontal));
            color = R.drawable.rounded_dark_blue;
        } else if (mPager.getCurrentItem() == 3) {
            background.setBackground(getContext().getResources().getDrawable(R.drawable.background_gradient_purple));
            confirm.setBackground(getContext().getResources().getDrawable(R.drawable.background_gradient_purple_horizontal));
            color = R.drawable.rounded_dark_purple;
        }
        if (mSelectedSubscriptionPeriod.equals(mFirstChoiceSku)) {
            border1.setBackground(getContext().getResources().getDrawable(color));
        } else if (mSelectedSubscriptionPeriod.equals(mSecondChoiceSku)) {
            border2.setBackground(getContext().getResources().getDrawable(color));
        } else if (mSelectedSubscriptionPeriod.equals(mThirdChoiceSku)) {
            border3.setBackground(getContext().getResources().getDrawable(color));
        }
    }

    private void changeColor(LinearLayout option) {
        if (mPager.getCurrentItem() == 0) {
            option.setBackground(getContext().getResources().getDrawable(R.drawable.rounded_dark_green));
        } else if (mPager.getCurrentItem() == 1) {
            option.setBackground(getContext().getResources().getDrawable(R.drawable.rounded_accent));
        } else if (mPager.getCurrentItem() == 2) {
            option.setBackground(getContext().getResources().getDrawable(R.drawable.rounded_dark_blue));
        } else if (mPager.getCurrentItem() == 3) {
            option.setBackground(getContext().getResources().getDrawable(R.drawable.rounded_dark_purple));
        }
    }

    private static class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {

                case 1:
                    return new FragmentAdvantage2();

                case 2:
                    return new FragmentAdvantage3();

                case 3:
                    return new FragmentAdvantage4();

                default:
                    return new FragmentAdvantage1();
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}