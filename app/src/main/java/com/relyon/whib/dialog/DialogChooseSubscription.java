package com.relyon.whib.dialog;

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

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.relyon.whib.R;
import com.relyon.whib.fragment.FragmentAdvantage1;
import com.relyon.whib.fragment.FragmentAdvantage2;
import com.relyon.whib.fragment.FragmentAdvantage3;
import com.relyon.whib.fragment.FragmentAdvantage4;
import com.relyon.whib.util.SelectSubscription;

import static com.relyon.whib.util.Constants.SKU_WHIB_MONTHLY;
import static com.relyon.whib.util.Constants.SKU_WHIB_SIXMONTH;
import static com.relyon.whib.util.Constants.SKU_WHIB_YEARLY;

public class DialogChooseSubscription extends DialogFragment {

    public Dialog dialog;
    private static final int NUM_PAGES = 4;
    private int backgroundColor;
    private Thread changeLayoutThread;
    private SelectSubscription selectSubscriptionListener;
    private String selectedSubscriptionPeriod = SKU_WHIB_SIXMONTH;

    private ViewPager pager;
    private LinearLayout borderColor1;
    private LinearLayout borderColor2;
    private LinearLayout borderColor3;
    private LinearLayout background;
    private Button confirm;
    private TextView noThanks;

    public DialogChooseSubscription() {
    }

    public static DialogChooseSubscription newInstance() {
        DialogChooseSubscription frag = new DialogChooseSubscription();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = super.onCreateDialog(savedInstanceState);
        if (dialog.getWindow() != null) {
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        return dialog;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle bundle) {
        View inflate = inflater.inflate(R.layout.dialog_choose_subscribe, parent);
        setTransparentBackground();
        return inflate;
    }

    private void setTransparentBackground() {
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setLayoutAttributes(view);

        noThanks.setOnClickListener(v -> dismiss());

        setupPageAdapter();

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                changeLayoutColors();
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        confirm.setOnClickListener(v -> {
            changeLayoutThread.interrupt();

            if (TextUtils.isEmpty(selectedSubscriptionPeriod)) {
                selectedSubscriptionPeriod = SKU_WHIB_MONTHLY;
            }

            selectSubscriptionListener.onChoose(selectedSubscriptionPeriod);
            this.dismiss();
            selectedSubscriptionPeriod = "";
        });

        borderColor1.setOnClickListener(v -> {
            selectedSubscriptionPeriod = SKU_WHIB_MONTHLY;
            changeColor(borderColor1);
            borderColor2.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.corner_white, null));
            borderColor3.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.corner_white, null));
        });

        borderColor2.setOnClickListener(v -> {
            selectedSubscriptionPeriod = SKU_WHIB_SIXMONTH;
            borderColor1.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.corner_white, null));
            changeColor(borderColor2);
            borderColor3.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.corner_white, null));
        });

        borderColor3.setOnClickListener(v -> {
            selectedSubscriptionPeriod = SKU_WHIB_YEARLY;
            borderColor1.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.corner_white, null));
            borderColor2.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.corner_white, null));
            changeColor(borderColor3);
        });

        changeLayoutThread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            Handler handler = new Handler();
                            handler.postDelayed(this, 4000);
                            if (pager.getCurrentItem() == 3) {
                                pager.setCurrentItem(0);
                            } else {
                                pager.setCurrentItem(pager.getCurrentItem() + 1);
                            }
                            if (getContext() != null) {
                                changeLayoutColors();
                            }
                        }
                    });
                }
            }
        });
        changeLayoutThread.start();
    }

    private void setupPageAdapter() {
        PagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager());
        pager.setAdapter(pagerAdapter);
    }

    private void setLayoutAttributes(View view) {
        noThanks = view.findViewById(R.id.noThanks);
        confirm = view.findViewById(R.id.confirm);
        borderColor1 = view.findViewById(R.id.border1);
        borderColor2 = view.findViewById(R.id.border2);
        borderColor3 = view.findViewById(R.id.border3);
        background = view.findViewById(R.id.background);
        pager = view.findViewById(R.id.pager);
    }

    public void changeLayoutColors() {
        if (pager.getCurrentItem() == 0) {
            background.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.background_gradient_green, null));
            confirm.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.background_gradient_green_horizontal, null));
            backgroundColor = R.drawable.rounded_dark_green;
        } else if (pager.getCurrentItem() == 1) {
            background.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.background_gradient_orange, null));
            confirm.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.background_gradient_orange_horizontal, null));
            backgroundColor = R.drawable.rounded_accent;
        } else if (pager.getCurrentItem() == 2) {
            background.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.background_gradient_blue, null));
            confirm.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.background_gradient_blue_horizontal, null));
            backgroundColor = R.drawable.rounded_dark_blue;
        } else if (pager.getCurrentItem() == 3) {
            background.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.background_gradient_purple, null));
            confirm.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.background_gradient_purple_horizontal, null));
            backgroundColor = R.drawable.rounded_dark_purple;
        }
        switch (selectedSubscriptionPeriod) {
            case SKU_WHIB_MONTHLY:
                borderColor1.setBackground(ResourcesCompat.getDrawable(getResources(), backgroundColor, null));
                break;
            case SKU_WHIB_SIXMONTH:
                borderColor2.setBackground(ResourcesCompat.getDrawable(getResources(), backgroundColor, null));
                break;
            case SKU_WHIB_YEARLY:
                borderColor3.setBackground(ResourcesCompat.getDrawable(getResources(), backgroundColor, null));
                break;
        }
    }

    private void changeColor(LinearLayout option) {
        if (pager.getCurrentItem() == 0) {
            option.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_dark_green, null));
        } else if (pager.getCurrentItem() == 1) {
            option.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_accent, null));
        } else if (pager.getCurrentItem() == 2) {
            option.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_dark_blue, null));
        } else if (pager.getCurrentItem() == 3) {
            option.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_dark_purple, null));
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            selectSubscriptionListener = (SelectSubscription) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement SelectSubscriptionDialogListener");
        }
    }
}