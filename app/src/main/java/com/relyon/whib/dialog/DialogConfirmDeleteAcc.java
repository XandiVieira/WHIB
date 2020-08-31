package com.relyon.whib.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.fragment.app.FragmentActivity;

import com.relyon.whib.R;
import com.relyon.whib.util.Util;

public class DialogConfirmDeleteAcc extends Dialog implements View.OnClickListener {

    private FragmentActivity fragmentActivity;
    public Dialog dialog;

    public DialogConfirmDeleteAcc(FragmentActivity a) {
        super(a);
        this.fragmentActivity = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_delete_account);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.deleteBt:
                Util.setDelete(true);
                break;
            case R.id.cancelBt:
                fragmentActivity.closeContextMenu();
                break;
            default:
                break;
        }
        dismiss();
    }
}