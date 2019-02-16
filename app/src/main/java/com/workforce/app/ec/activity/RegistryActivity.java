package com.workforce.app.ec.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.workforce.app.ec.R;
import com.workforce.app.ec.config.AppPreferences;
import com.workforce.app.ec.config.Constants;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class RegistryActivity extends AppCompatActivity {

    private EditText txtEmail,txtPass;
    private static AppPreferences app;
    private FirebaseAuth mAuth;
    private static final String TAG = RegistryActivity.class.getSimpleName();
    private SweetAlertDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_registro);



        app = new AppPreferences(getApplicationContext());
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        txtEmail= (EditText) findViewById(R.id.txtMotivo);
        txtPass= (EditText) findViewById(R.id.txtpass);


    }

    public void clear()
    {
        txtEmail.setText("");
        txtPass.setText("");

    }


    public void registro(View v)
    {
        if(txtEmail.getText().toString().equals("") || !Constants.validateEmail(txtEmail.getText().toString()))
        {
            txtEmail.setError(getString(R.string.error_mail));
            return ;
        }
        if(txtPass.getText().toString().equals(""))
        {
            txtPass.setError(getString(R.string.error_pass));
            return ;
        }
        if(txtPass.getText().toString().trim().length()<6)
        {
            txtPass.setError(getString(R.string.error_pass_lenght));
            return ;
        }


        pDialog = new SweetAlertDialog(RegistryActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        //pDialog.getProgressHelper().setBarColor(Color.parseColor(getString(R.string.colorAccent)));
        pDialog.setTitleText(getResources().getString(R.string.auth));
        pDialog.setCancelable(true);
        pDialog.show();

        mAuth.createUserWithEmailAndPassword(txtEmail.getText().toString(), txtPass.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                        if (task.isSuccessful()) {
                            pDialog.dismiss();
                            /* correo verificacion */
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user.isEmailVerified() == false) {
                                user.sendEmailVerification();

                                pDialog =new SweetAlertDialog(RegistryActivity.this, SweetAlertDialog.ERROR_TYPE);
                                pDialog.setTitleText(getResources().getString(R.string.app_name));
                                pDialog.setContentText(getResources().getString(R.string.user_create));
                                pDialog.setConfirmText(getResources().getString(R.string.ok));
                                pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                sDialog.dismissWithAnimation();
                                                finish();
                                            }
                                        });
                                pDialog.show();

                            }
                        } else {

                                if(task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                                {
                                    pDialog.dismiss();

                                    pDialog =new SweetAlertDialog(RegistryActivity.this, SweetAlertDialog.ERROR_TYPE);
                                    pDialog.setTitleText(getResources().getString(R.string.app_name));
                                    pDialog.setContentText(getResources().getString(R.string.error_login_pass));
                                    pDialog.setConfirmText(getResources().getString(R.string.ok));
                                    pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    sDialog.dismissWithAnimation();
                                                    clear();
                                                }
                                            });
                                    pDialog.show();

                                     return;
                                }

                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    pDialog.dismiss();


                                    pDialog =new SweetAlertDialog(RegistryActivity.this, SweetAlertDialog.ERROR_TYPE);
                                    pDialog.setTitleText(getResources().getString(R.string.app_name));
                                    pDialog.setContentText(getResources().getString(R.string.error_user));
                                    pDialog.setConfirmText(getResources().getString(R.string.ok));
                                    pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    sDialog.dismissWithAnimation();
                                                    clear();
                                                }
                                            });
                                    pDialog.show();


                                    return;
                                }

                                if(task.getException() instanceof FirebaseAuthWeakPasswordException)
                                {
                                    pDialog.dismiss();

                                    pDialog =new SweetAlertDialog(RegistryActivity.this, SweetAlertDialog.ERROR_TYPE);
                                    pDialog.setTitleText(getResources().getString(R.string.app_name));
                                    pDialog.setContentText(getResources().getString(R.string.error_pass_debil));
                                    pDialog.setConfirmText(getResources().getString(R.string.ok));
                                    pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    sDialog.dismissWithAnimation();
                                                    clear();
                                                }
                                            });
                                    pDialog.show();

                                    return;
                                }



                        }

                        // ...
                    }
                });


    }


}
