package com.relyon.whib;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.relyon.whib.modelo.Util;

public class DialogConfirmDeleteAcc extends Dialog implements View.OnClickListener {

    private FragmentActivity c;
    public Dialog d;
    public TextView confirmDeletionMessage;


    public DialogConfirmDeleteAcc(FragmentActivity a) {
        super(a);
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.delete_account_dialog);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.deleteBt:
                Util.setDelete(true);
                break;
            case R.id.cancelBt:
                c.closeContextMenu();
                break;
            default:
                break;
        }
        dismiss();
    }
}
