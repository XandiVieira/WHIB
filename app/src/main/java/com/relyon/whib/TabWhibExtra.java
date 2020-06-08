package com.relyon.whib;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.relyon.whib.modelo.Advantage;
import com.relyon.whib.modelo.Util;
import com.relyon.whib.util.IabBroadcastReceiver;
import com.relyon.whib.util.IabHelper;
import com.relyon.whib.util.IabResult;
import com.relyon.whib.util.Inventory;
import com.relyon.whib.util.Purchase;

import java.util.ArrayList;
import java.util.List;

import static com.relyon.whib.util.Constants.SKU_WHIB_MONTHLY;
import static com.relyon.whib.util.Constants.SKU_WHIB_SIXMONTH;
import static com.relyon.whib.util.Constants.SKU_WHIB_YEARLY;
import static com.relyon.whib.util.Constants.base64EncodedPublicKey;

public class TabWhibExtra extends Fragment implements IabBroadcastReceiver.IabBroadcastListener, DialogInterface.OnClickListener {

    private ListView resourceLV;
    private ArrayList<Advantage> advantages;

    static final String TAG = "MainActivity";

    // Does the user have an active subscription to the whib plan?
    boolean mSubscribedTowhib = false;

    // Will the subscription auto-renew?
    boolean mAutoRenewEnabled = false;

    // Tracks the currently owned subscription, and the options in the Manage dialog
    String mWhibSku = "";
    String mFirstChoiceSku = "";
    String mSecondChoiceSku = "";
    String mThirdChoiceSku = "";
    String mFourthChoiceSku = "";

    // Used to select between subscribing on a monthly, three month, six month or yearly basis
    String mSelectedSubscriptionPeriod = "";

    // SKU for our subscription

    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10001;


    // The helper object
    IabHelper mHelper;

    // Provides purchase notification while this app is running
    IabBroadcastReceiver mBroadcastReceiver;


    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_whib_extra, container, false);

        resourceLV = rootView.findViewById(R.id.resourceLV);
        Button subscribe = rootView.findViewById(R.id.signWhibExtraBT);

        subscribe.setOnClickListener(v -> onSubscribeButtonClicked());

        Util.mAdvantagesDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                advantages = new ArrayList<>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    advantages.add(snap.getValue(Advantage.class));
                }
                ArrayAdapter adapter = new AdvantageAdapter(getContext(), advantages);
                resourceLV.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // Create the helper, passing it our context and the public key to verify signatures with
        Log.d(TAG, "Creating IAB helper.");
        mHelper = new IabHelper(getContext(), base64EncodedPublicKey);

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

            mBroadcastReceiver = new IabBroadcastReceiver(TabWhibExtra.this);
            IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
            getContext().registerReceiver(mBroadcastReceiver, broadcastFilter);

            // IAB is fully set up. Now, let's get an inventory of stuff we own.
            Log.d(TAG, "Setup successful. Querying inventory.");
            try {
                mHelper.queryInventoryAsync(mGotInventoryListener);
            } catch (IabHelper.IabAsyncInProgressException e) {
                complain("Error querying inventory. Another async operation in progress.");
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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

            // First find out which subscription is auto renewing
            Purchase whibMonthly = inventory.getPurchase(SKU_WHIB_MONTHLY);
            Purchase whibSixMonth = inventory.getPurchase(SKU_WHIB_SIXMONTH);
            Purchase whibYearly = inventory.getPurchase(SKU_WHIB_YEARLY);
            if (whibMonthly != null && whibMonthly.isAutoRenewing()) {
                mWhibSku = SKU_WHIB_MONTHLY;
                mAutoRenewEnabled = true;
            } else if (whibSixMonth != null && whibSixMonth.isAutoRenewing()) {
                mWhibSku = SKU_WHIB_SIXMONTH;
                mAutoRenewEnabled = true;
            } else if (whibYearly != null && whibYearly.isAutoRenewing()) {
                mWhibSku = SKU_WHIB_YEARLY;
                mAutoRenewEnabled = true;
            } else {
                mWhibSku = "";
                mAutoRenewEnabled = false;
            }

            // The user is subscribed if either subscription exists, even if neither is auto
            // renewing
            mSubscribedTowhib = (whibMonthly != null && verifyDeveloperPayload(whibMonthly))
                    || (whibSixMonth != null && verifyDeveloperPayload(whibSixMonth))
                    || (whibYearly != null && verifyDeveloperPayload(whibYearly));
            Log.d(TAG, "User " + (mSubscribedTowhib ? "HAS" : "DOES NOT HAVE")
                    + " infinite gas subscription.");

            updateUi();
            setWaitScreen(false);
            Log.d(TAG, "Initial inventory query finished; enabling main UI.");
        }
    };

    @Override
    public void receivedBroadcast() {
        // Received a broadcast notification that the inventory of items has changed
        Log.d(TAG, "Received broadcast notification. Querying inventory.");
        try {
            mHelper.queryInventoryAsync(mGotInventoryListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            complain("Error querying inventory. Another async operation in progress.");
        }
    }

    // "Subscribe to whib" button clicked. Explain to user, then start purchase
    // flow for subscription.
    private void onSubscribeButtonClicked() {
        if (!mHelper.subscriptionsSupported()) {
            complain("Subscriptions not supported on your device yet. Sorry!");
            return;
        }

        CharSequence[] options;
        if (!mSubscribedTowhib || !mAutoRenewEnabled) {
            // Both subscription options should be available
            options = new CharSequence[3];
            options[0] = getString(R.string.subscription_period_monthly);
            options[1] = getString(R.string.subscription_period_sixmonth);
            options[2] = getString(R.string.subscription_period_yearly);
            mFirstChoiceSku = SKU_WHIB_MONTHLY;
            mThirdChoiceSku = SKU_WHIB_SIXMONTH;
            mFourthChoiceSku = SKU_WHIB_YEARLY;
        } else {
            // This is the subscription upgrade/downgrade path, so only one option is valid
            options = new CharSequence[3];
            switch (mWhibSku) {
                case SKU_WHIB_MONTHLY:
                    // Give the option to upgrade below
                    options[1] = getString(R.string.subscription_period_sixmonth);
                    options[2] = getString(R.string.subscription_period_yearly);
                    mSecondChoiceSku = SKU_WHIB_SIXMONTH;
                    mThirdChoiceSku = SKU_WHIB_YEARLY;
                    break;
                case SKU_WHIB_SIXMONTH:
                    // Give the option to upgrade or downgrade below
                    options[0] = getString(R.string.subscription_period_monthly);
                    options[2] = getString(R.string.subscription_period_yearly);
                    mFirstChoiceSku = SKU_WHIB_MONTHLY;
                    mThirdChoiceSku = SKU_WHIB_YEARLY;
                    break;
                default:
                    // Give the option to upgrade or downgrade below
                    options[0] = getString(R.string.subscription_period_monthly);
                    options[2] = getString(R.string.subscription_period_sixmonth);
                    mSecondChoiceSku = SKU_WHIB_SIXMONTH;
                    mThirdChoiceSku = SKU_WHIB_YEARLY;
                    break;
            }
            mFourthChoiceSku = "";
        }

        int titleResId;
        if (!mSubscribedTowhib) {
            titleResId = R.string.subscription_period_prompt;
        } else if (!mAutoRenewEnabled) {
            titleResId = R.string.subscription_resignup_prompt;
        } else {
            titleResId = R.string.subscription_update_prompt;
        }

        /*AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(titleResId)
                .setSingleChoiceItems(options, 0, this)
                .setPositiveButton(R.string.subscription_prompt_continue, this)
                .setNegativeButton(R.string.subscription_prompt_cancel, this);
        AlertDialog dialog = builder.create();
        dialog.show();*/
        FragmentManager fm = getFragmentManager();
        DialogChooseSubscription dialog = DialogChooseSubscription.newInstance("Some Title");
        if (fm != null) {
            dialog.show(fm, "fragment_edit_name");
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int id) {
        if (id == 0 /* First choice item */) {
            mSelectedSubscriptionPeriod = mFirstChoiceSku;
        } else if (id == 1 /* Second choice item */) {
            mSelectedSubscriptionPeriod = mSecondChoiceSku;
        } else if (id == 2) {
            mSelectedSubscriptionPeriod = mThirdChoiceSku;
        } else if (id == 3) {
            mSelectedSubscriptionPeriod = mFourthChoiceSku;
        } else if (id == DialogInterface.BUTTON_POSITIVE /* continue button */) {

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

            setWaitScreen(true);
            try {
                mHelper.launchPurchaseFlow(getActivity(), mSelectedSubscriptionPeriod, IabHelper.ITEM_TYPE_SUBS,
                        oldSkus, RC_REQUEST, mPurchaseFinishedListener, payload);
            } catch (IabHelper.IabAsyncInProgressException e) {
                complain("Error launching purchase flow. Another async operation in progress.");
                setWaitScreen(false);
            }
            // Reset the dialog options
            mSelectedSubscriptionPeriod = "";
            mFirstChoiceSku = "";
            mSecondChoiceSku = "";
        } else if (id != DialogInterface.BUTTON_NEGATIVE) {
            // There are only four buttons, this should not happen
            Log.e(TAG, "Unknown button clicked in subscription dialog: " + id);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }

    /**
     * Verifies the developer payload of a purchase.
     */
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
                setWaitScreen(false);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                complain("Error purchasing. Authenticity verification failed.");
                setWaitScreen(false);
                return;
            }

            Log.d(TAG, "Purchase successful.");

            if (purchase.getSku().equals(SKU_WHIB_MONTHLY)
                    || purchase.getSku().equals(SKU_WHIB_SIXMONTH)
                    || purchase.getSku().equals(SKU_WHIB_YEARLY)) {
                // bought the rasbita subscription
                Log.d(TAG, "whib subscription purchased.");
                alert("Thank you for subscribing to whib!");
                mSubscribedTowhib = true;
                mAutoRenewEnabled = purchase.isAutoRenewing();
                mWhibSku = purchase.getSku();
                updateUi();
                setWaitScreen(false);
            }
        }
    };

    // Called when consumption is complete
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);

            updateUi();
            setWaitScreen(false);
            Log.d(TAG, "End consumption flow.");
        }
    };


    // We're being destroyed. It's important to dispose of the helper here!
    @Override
    public void onDestroy() {
        super.onDestroy();

        // very important:
        if (mBroadcastReceiver != null) {
            getActivity().unregisterReceiver(mBroadcastReceiver);
        }

        // very important:
        Log.d(TAG, "Destroying helper.");
        if (mHelper != null) {
            mHelper.disposeWhenFinished();
            mHelper = null;
        }
    }

    // updates UI to reflect model
    private void updateUi() {

        /*ImageView subscribeButton = (ImageView) findViewById(R.id.rasbita_subscribe);
        if (mSubscribedTowhib) {
            Intent intent = new Intent(getContext(), NextSubjectVoting.class);
            startActivity(intent);
            getActivity().finish();
        } else {
            // The user does not have rabista subscription"
            subscribeButton.setImageResource(R.drawable.logo50);
        }*/
    }

    // Enables or disables the "please wait" screen.
    private void setWaitScreen(boolean set) {
        //findViewById(R.id.screen_main).setVisibility(set ? View.GONE : View.VISIBLE);
    }

    private void complain(String message) {
        Log.e(TAG, "**** whib Error: " + message);
        alert("Error: " + message);
    }

    private void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(getContext());
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }
}