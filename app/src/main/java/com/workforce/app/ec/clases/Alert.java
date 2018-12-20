package com.workforce.app.ec.clases;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.workforce.app.ec.R;


public class Alert extends AlertDialog {

    private String message;
    private String btYesText;
    private String btNoText;
    private int icon=0;
    private View.OnClickListener btOkListener=null;
    private View.OnClickListener btNoListener=null;
    private Button btOk;
    private Button btNo;

    public Alert(Context context) {
        super(context);
    }

    protected Alert(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public Alert(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert);

        TextView txtmessage = (TextView) findViewById(R.id.message);
        //txtmessage.setCompoundDrawablesWithIntrinsicBounds(icon,0,0,0);
        txtmessage.setText(getMessage());

        btOk = (Button) findViewById(R.id.btnOk);
        btOk.setText(btYesText);
        btOk.setOnClickListener(btOkListener);


        btNo = (Button) findViewById(R.id.btnNo);
        btNo.setText(btNoText);
        btNo.setOnClickListener(btNoListener);

    }



    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getIcon() {
        return icon;
    }

    public void setPositveButton(String yes, View.OnClickListener onClickListener) {

        this.btYesText = yes;
        this.btOkListener = onClickListener;

    }

    public void setNegativeButton(String no, View.OnClickListener onClickListener) {


            this.btNoText = no;
            this.btNoListener = onClickListener;

        dismiss();

    }




}
