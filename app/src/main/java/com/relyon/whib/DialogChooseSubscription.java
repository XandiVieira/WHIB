package com.relyon.whib;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.relyon.whib.util.IabBroadcastReceiver;
import com.relyon.whib.util.IabHelper;
import com.relyon.whib.util.IabResult;
import com.relyon.whib.util.Purchase;

import java.util.ArrayList;
import java.util.List;

import static com.relyon.whib.TabWhibExtra.TAG;
import static com.relyon.whib.util.Constants.SKU_WHIB_MONTHLY;
import static com.relyon.whib.util.Constants.SKU_WHIB_SIXMONTH;
import static com.relyon.whib.util.Constants.SKU_WHIB_YEARLY;

public class DialogChooseSubscription extends DialogFragment implements IabBroadcastReceiver.IabBroadcastListener {

    public Dialog d;
    private static final int NUM_PAGES = 4;
    private ViewPager mPager;
    private LinearLayout option1;
    private LinearLayout option2;
    private LinearLayout option3;

    // Does the user have an active subscription to the whib plan?
    boolean mSubscribedToWhib = false;

    // Will the subscription auto-renew?
    boolean mAutoRenewEnabled = false;

    // Tracks the currently owned subscription, and the options in the Manage dialog
    String mWhibSku = "";
    String mFirstChoiceSku = SKU_WHIB_MONTHLY;
    String mSecondChoiceSku = SKU_WHIB_SIXMONTH;
    String mThirdChoiceSku = SKU_WHIB_YEARLY;

    // Used to select between subscribing on a monthly, three month, six month or yearly basis
    String mSelectedSubscriptionPeriod = mThirdChoiceSku;

    // SKU for our subscription

    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10001;

    // The helper object
    IabHelper mHelper;

    // Provides purchase notification while this app is running
    IabBroadcastReceiver mBroadcastReceiver;

    public DialogChooseSubscription() {

    }

    public static DialogChooseSubscription newInstance(String title) {
        DialogChooseSubscription frag = new DialogChooseSubscription();
        Bundle args = new Bundle();
        args.putString("title", title);
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
        // Set to adjust screen height automatically, when soft keyboard appears on screen
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        return inflater.inflate(R.layout.dialog_choose_subscribe, parent);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView noThanks = view.findViewById(R.id.noThanks);
        Button confirm = view.findViewById(R.id.confirm);
        option1 = view.findViewById(R.id.option1);
        option2 = view.findViewById(R.id.option2);
        option3 = view.findViewById(R.id.option3);

        noThanks.setOnClickListener(v -> dismiss());

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = view.findViewById(R.id.pager);
        /*
      The pager adapter, which provides the pages to the view pager widget.
     */
        PagerAdapter mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        confirm.setOnClickListener(v -> {
            String payload = "";

            if (TextUtils.isEmpty(mSelectedSubscriptionPeriod)) {
                // The user has not changed from the default selection
                mSelectedSubscriptionPeriod = mFirstChoiceSku;
            }

            List<String> oldSkus = null;
            if (!TextUtils.isEmpty(mWhibSku)
                    && !mWhibSku.equals(mSelectedSubscriptionPeriod)) {
                // The user currently has a valid subscription, any purchase action is going to
                // replace that subscription
                oldSkus = new ArrayList<>();
                oldSkus.add(mWhibSku);
            }

            try {
                mHelper.launchPurchaseFlow(getActivity(), mSelectedSubscriptionPeriod, IabHelper.ITEM_TYPE_SUBS,
                        oldSkus, RC_REQUEST, mPurchaseFinishedListener, payload);
            } catch (IabHelper.IabAsyncInProgressException e) {
                complain("Error launching purchase flow. Another async operation in progress.");
            }
            // Reset the dialog options
            mSelectedSubscriptionPeriod = "";
            mFirstChoiceSku = "";
            mSecondChoiceSku = "";
        });

        option1.setOnClickListener(v -> {
            mSelectedSubscriptionPeriod = mFirstChoiceSku;
            option1.setBackground(getContext().getResources().getDrawable(R.drawable.rounded_accent));
            option2.setBackground(getContext().getResources().getDrawable(R.drawable.corner_white));
            option3.setBackground(getContext().getResources().getDrawable(R.drawable.corner_white));
        });

        option2.setOnClickListener(v -> {
            mSelectedSubscriptionPeriod = mSecondChoiceSku;
            option1.setBackground(getContext().getResources().getDrawable(R.drawable.corner_white));
            option2.setBackground(getContext().getResources().getDrawable(R.drawable.rounded_accent));
            option3.setBackground(getContext().getResources().getDrawable(R.drawable.corner_white));
        });

        option3.setOnClickListener(v -> {
            mSelectedSubscriptionPeriod = mThirdChoiceSku;
            option1.setBackground(getContext().getResources().getDrawable(R.drawable.corner_white));
            option2.setBackground(getContext().getResources().getDrawable(R.drawable.corner_white));
            option3.setBackground(getContext().getResources().getDrawable(R.drawable.rounded_accent));
        });

        new Thread(new Runnable() {
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
                        }
                    });
                }
            }
        }).start();
    }

    private void handleSelection() {
    }

    @Override
    public void receivedBroadcast() {
    }

    private void complain(String message) {
        Log.e(TAG, "**** whib Error: " + message);
        alert("Error: " + message);
    }

    private void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(getActivity());
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }

    private boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();
        return true;
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

            if (purchase.getSku().equals(SKU_WHIB_MONTHLY)
                    || purchase.getSku().equals(SKU_WHIB_SIXMONTH)
                    || purchase.getSku().equals(SKU_WHIB_YEARLY)) {
                // bought the rasbita subscription
                Log.d(TAG, "whib subscription purchased.");
                alert("Thank you for subscribing to whib!");
                mSubscribedToWhib = true;
                mAutoRenewEnabled = purchase.isAutoRenewing();
                mWhibSku = purchase.getSku();
            }
        }
    };

    // Called when consumption is complete
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);
            Log.d(TAG, "End consumption flow.");
        }
    };

    /**
     * A simple pager adapter that represents 4 ScreenSlidePageFragment objects, in
     * sequence.
     */
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