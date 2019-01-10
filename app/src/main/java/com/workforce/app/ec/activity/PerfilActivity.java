package com.workforce.app.ec.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.workforce.app.ec.R;
import com.workforce.app.ec.clases.City;
import com.workforce.app.ec.clases.EstadoCivil;
import com.workforce.app.ec.clases.Identificacion;
import com.workforce.app.ec.clases.ImagenCircular.CircleImageView;
import com.workforce.app.ec.clases.Spinner.MaterialSpinner;
import com.workforce.app.ec.config.AppPreferences;
import com.workforce.app.ec.config.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;



public class PerfilActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener,IPickResult{

    private String TAG = PerfilActivity.class.getName();
    private static AppPreferences app;

    private EditText txtIdentificacion,txtNombres,txtApellidos,txtFecha,txttelefono,txtpass,txtnewpass,txtemail;

    private TextInputLayout textInputLayout8,textInputLayout7;

    private CircleImageView img;

    private static final int PICK_FROM_CAMERA = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int PICK_FROM_FILE = 3;
    private File outPutFile = null;
    private String mCurrentPhotoPath;
    private Bitmap bitmap;
    private String image = "";
    private Button save;
    private SweetAlertDialog pDialog;
    private MaterialSpinner tipo_identificacion,estado_civil,ciudad,genero;



    private ArrayAdapter<String> dataAdapter_tipo_identificacion;
    private ArrayAdapter<String> dataAdapter_ciudad;
    private ArrayAdapter<String> dataAdapter_estado;
    private ArrayAdapter<String> dataAdapter_genero;


    private ArrayList<String> list_tipo_identificacion;
    private ArrayList<String> list_estado_civil;
    private ArrayList<String> list_cuidad;
    private ArrayList<String> list_genero;


    private ArrayList<EstadoCivil> arrayEstadoCivil;
    private ArrayList<City> arrayCity;
    private ArrayList<Identificacion> arrayIdentificacion;

    private int select_tipo_identificacion=0;
    private int select_ciudad=0;
    private int select_estado=0;
    private int select_genero=0;


    private Toolbar toolbar;

    private FirebaseUser user;

    private String telefono= "";

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks= null;

    private FrameLayout fragment;
    private boolean changeEmail=false;
    private String provider="";

    private static DatabaseReference databaseUsers;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_perfil);


        user = FirebaseAuth.getInstance().getCurrentUser();

        app = new AppPreferences(getApplicationContext());
        databaseUsers = FirebaseDatabase.getInstance().getReference("users");

        List<String> listProvider =user.getProviders();
        provider=listProvider.get(0);
        if(!provider.equals("password")){

            textInputLayout7 = (TextInputLayout) findViewById(R.id.textInputLayout7);
            textInputLayout8 = (TextInputLayout) findViewById(R.id.textInputLayout8);
            textInputLayout7.setVisibility(View.GONE);
            textInputLayout8.setVisibility(View.GONE);

        }



        /* toolbar*/
        toolbar = (Toolbar) findViewById(R.id.toolbaruser);

        TextView title = (TextView) findViewById(R.id.txtTitle);

        title.setText(getString(R.string.perfil));

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSupportActionBar().setHomeAsUpIndicator(getDrawable(R.drawable.ic_arrow));
        } else {
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_arrow));
        }

        txtIdentificacion= (EditText) findViewById(R.id.txtIdentificacion);
        txtNombres= (EditText) findViewById(R.id.txtNombres);
        txtApellidos= (EditText) findViewById(R.id.txtApellidos);
        txtFecha= (EditText) findViewById(R.id.txtFecha);
        txttelefono= (EditText) findViewById(R.id.txttelefono);
        txtpass= (EditText) findViewById(R.id.txtpass);
        txtnewpass= (EditText) findViewById(R.id.txtnewpass);
        txtemail= (EditText) findViewById(R.id.txtemail);

        fragment= (FrameLayout) findViewById(R.id.fragment);



        img=(CircleImageView) findViewById(R.id.imgPerfil);

        tipo_identificacion=(MaterialSpinner) findViewById(R.id.tipo_identificacion);
        estado_civil=(MaterialSpinner) findViewById(R.id.estado_civil);
        ciudad=(MaterialSpinner) findViewById(R.id.ciudad);
        genero=(MaterialSpinner) findViewById(R.id.genero);






        save=(Button) findViewById(R.id.save);


        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showFileChooser();

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(tipo_identificacion.getSelectedItemPosition()==0){

                    tipo_identificacion.setError(getResources().getString(R.string.error_tipo_identificacion));
                    return;

                }


                if(txtIdentificacion.getText().toString().trim().equals("")){

                    txtIdentificacion.setError(getResources().getString(R.string.error_identificacion));
                    return;

                }

                if(genero.getSelectedItemPosition()==0){

                    genero.setError(getResources().getString(R.string.error_genero));
                    return;

                }



                if(txtNombres.getText().toString().trim().equals("")){

                    txtNombres.setError(getResources().getString(R.string.error_nombres));
                    return;

                }


                if(txtApellidos.getText().toString().trim().equals("")){

                    txtApellidos.setError(getResources().getString(R.string.error_apellido));
                    return;

                }

                if(!Constants.validatPhone(txttelefono.getText().toString().trim())){

                    txttelefono.setError(getResources().getString(R.string.error_telefono));
                    return;

                }


                if(provider.equals("password")){
                    if(!txtpass.getText().toString().equals(""))
                    {
                        if(txtnewpass.getText().toString().trim().length()<6)
                        {
                            txtnewpass.setError(getResources().getString(R.string.error_newpass));
                            return;
                        }
                    }else
                    {
                        if(!txtnewpass.getText().toString().trim().equals("") && txtpass.getText().toString().equals("")){
                            txtpass.setError(getResources().getString(R.string.error_pass));
                            return;
                        }

                    }
                }



                if(!telefono.equals(txttelefono.getText().toString().trim()))
                {
                    validate_phone();
                }else
                {
                    if(provider.equals("password")){
                        change_password();
                    }else
                    {
                        saveTask(image);
                    }

                }

                if(estado_civil.getSelectedItemPosition()==0){

                    estado_civil.setError(getResources().getString(R.string.error_estado_civil));
                    return;

                }

                if(ciudad.getSelectedItemPosition()==0){

                    ciudad.setError(getResources().getString(R.string.error_ciudad));
                    return;

                }



                if(txtFecha.getText().toString().trim().equals("")){

                    txtFecha.setError(getResources().getString(R.string.error_fecha));
                    return;

                }

            }
        });



        outPutFile = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");

        txtFecha.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Calendar now = Calendar.getInstance();
                    DatePickerDialog dpd = DatePickerDialog.newInstance(
                            PerfilActivity.this,
                            now.get(Calendar.YEAR),
                            now.get(Calendar.MONTH),
                            now.get(Calendar.DAY_OF_MONTH)
                    );
                    dpd.show(getFragmentManager(), "Datepickerdialog");
                }
                Log.i("click text", "kakak");
                return false;
            }
        });

        dataTask();


    }


    private void carga_combo()
    {


        if(list_tipo_identificacion.size()>0)
        {
            // Creating adapter for spinner
            dataAdapter_tipo_identificacion= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_tipo_identificacion);

            // Drop down layout style - list view with radio button
            dataAdapter_tipo_identificacion.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // attaching data adapter to spinner
            tipo_identificacion.setAdapter(dataAdapter_tipo_identificacion);



            tipo_identificacion.setSelection(select_tipo_identificacion+1);


        }



                        if(list_estado_civil.size()>0)
                        {
                            // Creating adapter for spinner
                            dataAdapter_estado = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_estado_civil);

                            // Drop down layout style - list view with radio button
                            dataAdapter_estado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            // attaching data adapter to spinner
                            estado_civil.setAdapter(dataAdapter_estado);



                                estado_civil.setSelection(select_estado+1);


                        }

                        if(list_cuidad.size()>0)
                        {
                            // Creating adapter for spinner
                            dataAdapter_ciudad= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_cuidad);

                            // Drop down layout style - list view with radio button
                            dataAdapter_ciudad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            // attaching data adapter to spinner
                            ciudad.setAdapter(dataAdapter_ciudad);



                                ciudad.setSelection(select_ciudad+1);

                        }
                        list_genero = new ArrayList<String>();
                        list_genero.add("Masculino");
                        list_genero.add("Femenino");

                        // Creating adapter for spinner
                        dataAdapter_genero= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_genero);

                        // Drop down layout style - list view with radio button
                        dataAdapter_genero.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        // attaching data adapter to spinner
                        genero.setAdapter(dataAdapter_genero);



                        genero.setSelection(select_genero+1);





    }


    private void dataTask(){
        //Showing the progress dialog
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor(getString(R.string.colorAccent)));
        pDialog.setTitleText(getResources().getString(R.string.auth));
        pDialog.setCancelable(true);
        pDialog.show();

        //Constants.deleteCache(getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SERVER+"getDataPerfil/format/json",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responde) {
                        Log.d(TAG, responde);

                        //Showing toast message of the response

                        JSONObject res= null;
                        try {
                            res = new JSONObject(responde);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {

                            if(res.getString("result").equals("OK") ){
                                JSONArray mObjResp = res.getJSONArray("data");
                                final JSONObject mObj = mObjResp.getJSONObject(0);



                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {

                                            String nombres = Constants.Decrypt(mObj.getString("nombres"));
                                            String apellidos = Constants.Decrypt(mObj.getString("apellidos"));

                                            txtNombres.setText(nombres);
                                            txtApellidos.setText(apellidos);

                                            if(nombres.trim().length()!=0)
                                            {
                                                txtNombres.setEnabled(false);
                                            }

                                            if(apellidos.trim().length()!=0)
                                            {
                                                txtApellidos.setEnabled(false);
                                            }

                                            txtFecha.setText(Constants.Decrypt(mObj.getString("fecha_nacimiento")));
                                            txtIdentificacion.setText(Constants.Decrypt(mObj.getString("identificacion")));

                                            telefono =  Constants.Decrypt(mObj.getString("telefono"));
                                            telefono = "0"+telefono;
                                            txttelefono.setText(telefono);

                                            select_tipo_identificacion = Integer.parseInt(Constants.Decrypt(mObj.getString("tipo_identificacion")));
                                            select_estado = Integer.parseInt(Constants.Decrypt(mObj.getString("estado_civil")));
                                            select_ciudad = Integer.parseInt(Constants.Decrypt(mObj.getString("ciudad_id")));
                                            select_genero = Integer.parseInt(Constants.Decrypt(mObj.getString("genero")));



                                            //String imagen= mObj.getString("imagen");

                                            if(! MainActivity.Utemp.getUrl_imagen().trim().equals(""))
                                            {
                                                Glide.with(getApplicationContext())
                                                        .load(MainActivity.Utemp.getUrl_imagen())
                                                        .fitCenter()
                                                        .into(img);
                                            }











                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });


                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {

                                        JSONArray list = null;
                                        try {
                                            list = new JSONArray(mObj.getString("list_tipo_identificacion"));

                                        list_tipo_identificacion = new ArrayList<String>();
                                        arrayIdentificacion = new ArrayList<Identificacion>();

                                        for (int x=0;x<list.length();x++)
                                        {
                                            JSONObject temp= list.getJSONObject(x);

                                            list_tipo_identificacion.add(Constants.Decrypt(temp.getString("nombre")));
                                            arrayIdentificacion.add(new Identificacion(Integer.parseInt(Constants.Decrypt(temp.getString("id"))),Constants.Decrypt(temp.getString("nombre"))));

                                            if (Integer.parseInt(Constants.Decrypt(temp.getString("id"))) == select_tipo_identificacion)
                                            {
                                                select_tipo_identificacion = x;
                                            }

                                        }



                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }


                                    }
                                });


                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {

                                        try {

                                        JSONArray list2 = new JSONArray(mObj.getString("list_estado_civil"));
                                        list_estado_civil = new ArrayList<String>();
                                        arrayEstadoCivil = new ArrayList<EstadoCivil>();
                                        for (int x=0;x<list2.length();x++)
                                        {
                                            JSONObject temp = list2.getJSONObject(x);

                                            list_estado_civil.add(Constants.Decrypt(temp.getString("nombre")));
                                            arrayEstadoCivil.add(new EstadoCivil(Integer.parseInt(Constants.Decrypt(temp.getString("id"))),Constants.Decrypt(temp.getString("nombre"))));

                                            if (Integer.parseInt(Constants.Decrypt(temp.getString("id"))) == select_estado)
                                            {
                                                select_estado = x;
                                            }



                                        }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }



                                    }
                                });



                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {

                                        try {

                                            JSONArray list3 = new JSONArray(mObj.getString("list_ciudad"));
                                            list_cuidad = new ArrayList<String>();
                                            arrayCity = new ArrayList<City>();
                                            for (int x=0;x<list3.length();x++)
                                            {
                                                JSONObject temp = list3.getJSONObject(x);

                                                list_cuidad.add(Constants.Decrypt(temp.getString("nombre")));
                                                arrayCity.add(new City(Integer.parseInt(Constants.Decrypt(temp.getString("id"))),Constants.Decrypt(temp.getString("nombre"))));

                                                if (Integer.parseInt(Constants.Decrypt(temp.getString("id"))) == select_ciudad)
                                                {
                                                    select_ciudad = x;
                                                }

                                            }

                                            carga_combo();




                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }



                                    }
                                });

                                pDialog.dismiss();





                            }else
                            {
                                pDialog.dismiss();


                                pDialog = new SweetAlertDialog(PerfilActivity.this, SweetAlertDialog.ERROR_TYPE);
                                pDialog.setTitleText(getResources().getString(R.string.app_name));
                                pDialog.setContentText(res.getString("message"));
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
                        } catch (JSONException e) {
                            pDialog.dismiss();
                            Log.d(TAG, e.toString());
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        pDialog.dismiss();

                        if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
                            VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                            volleyError = error;
                        }

                        //Showing toast
                        Log.d(TAG, volleyError.toString());
                        Toast.makeText(PerfilActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();


                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters

                try {
                    params.put("userid", Constants.Encrypt(app.getUserId()));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //returning parameters
                return params;
            }
        };


        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue volleyQueue = Volley.newRequestQueue(this);
        volleyQueue.add(stringRequest);
        DiskBasedCache cache = new DiskBasedCache(getCacheDir(), 500 * 1024 * 1024);
        volleyQueue = new RequestQueue(cache, new BasicNetwork(new HurlStack()));
        volleyQueue.start();




    }

    private void showEmail()
    {
        pDialog = new SweetAlertDialog(PerfilActivity.this, SweetAlertDialog.ERROR_TYPE);
        pDialog.setTitleText(getResources().getString(R.string.app_name));
        pDialog.setContentText(getResources().getString(R.string.update_email));
        pDialog.setConfirmText(getResources().getString(R.string.ok));
        pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                sDialog.dismissWithAnimation();
                return;

            }
        });
        pDialog.show();
    }

    private void change_email()
    {

        if(!user.getEmail().equals(txtemail.getText().toString().trim()))
        {

            if(txtpass.getText().toString().trim().length()==0)
            {
                pDialog = new SweetAlertDialog(PerfilActivity.this, SweetAlertDialog.ERROR_TYPE);
                pDialog.setTitleText(getResources().getString(R.string.app_name));
                pDialog.setContentText(getResources().getString(R.string.error_need_pass));
                pDialog.setConfirmText(getResources().getString(R.string.ok));
                pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        return;

                    }
                });
                pDialog.show();
            }else
            {
                AuthCredential credential = EmailAuthProvider
                        .getCredential(user.getEmail(), txtpass.getText().toString().trim());


                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {


                                    user.updateEmail(txtemail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {



                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "Password updated");
                                                changeEmail=true;
                                                saveTask(image);
                                            } else {

                                                pDialog = new SweetAlertDialog(PerfilActivity.this, SweetAlertDialog.ERROR_TYPE);
                                                pDialog.setTitleText(getResources().getString(R.string.app_name));
                                                pDialog.setContentText(getResources().getString(R.string.error_change_email));
                                                pDialog.setConfirmText(getResources().getString(R.string.ok));
                                                pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(SweetAlertDialog sDialog) {
                                                        sDialog.dismissWithAnimation();
                                                        return;

                                                    }
                                                });
                                                pDialog.show();

                                            }


                                        }
                                    });
                                } else {
                                    pDialog = new SweetAlertDialog(PerfilActivity.this, SweetAlertDialog.ERROR_TYPE);
                                    pDialog.setTitleText(getResources().getString(R.string.app_name));
                                    pDialog.setContentText(getResources().getString(R.string.error_newpass_update));
                                    pDialog.setConfirmText(getResources().getString(R.string.ok));
                                    pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();
                                            return;

                                        }
                                    });
                                    pDialog.show();
                                }
                            }
                        });
            }


        }else
        {
            saveTask(image);
        }




    }

    private  void change_password()
    {
        if(!txtpass.getText().toString().trim().equals("") && !txtnewpass.getText().toString().trim().equals(""))
        {

            AuthCredential credential = EmailAuthProvider
                    .getCredential(user.getEmail(), txtpass.getText().toString().trim());


            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                user.updatePassword(txtnewpass.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "Password updated");

                                            saveTask(image);

                                        } else {

                                            pDialog = new SweetAlertDialog(PerfilActivity.this, SweetAlertDialog.ERROR_TYPE);
                                            pDialog.setTitleText(getResources().getString(R.string.app_name));
                                            pDialog.setContentText(getResources().getString(R.string.error_newpass_update));
                                            pDialog.setConfirmText(getResources().getString(R.string.ok));
                                            pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    sDialog.dismissWithAnimation();
                                                    return;

                                                }
                                            });
                                            pDialog.show();

                                        }
                                    }
                                });
                            } else {
                                pDialog = new SweetAlertDialog(PerfilActivity.this, SweetAlertDialog.ERROR_TYPE);
                                pDialog.setTitleText(getResources().getString(R.string.app_name));
                                pDialog.setContentText(getResources().getString(R.string.error_newpass_update));
                                pDialog.setConfirmText(getResources().getString(R.string.ok));
                                pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();
                                        return;

                                    }
                                });
                                pDialog.show();
                            }
                        }
                    });


        }else
        {

            saveTask(image);


        }
    }


    private void validate_phone()
    {
        String num= txttelefono.getText().toString().trim();
        num= Constants.PREFIJO_PHONE +num.substring(1);


        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor(getString(R.string.colorAccent)));
        pDialog.setTitleText(getResources().getString(R.string.validate_num));
        pDialog.setCancelable(false);
        pDialog.show();

            //fragment.setVisibility(View.VISIBLE);

            mCallBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                    Log.d("JEJE", "onVerificationCompleted:" + phoneAuthCredential);
                    pDialog.dismiss();

                    telefono=txttelefono.getText().toString().trim();
                    MainActivity.validate_phone = 0;
                    change_password();

                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
                    Log.w("JEJE", "onVerificationFailed", e);
                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        Log.d("JEJE", "INVALID REQUEST");
                    } else if (e instanceof FirebaseTooManyRequestsException) {
                        Log.d("JEJE", "Too many Request");
                    }
                    pDialog.dismiss();

                    pDialog = new SweetAlertDialog(PerfilActivity.this, SweetAlertDialog.ERROR_TYPE);
                    pDialog.setTitleText(getResources().getString(R.string.app_name));
                    pDialog.setContentText(getResources().getString(R.string.error_validate_num));
                    pDialog.setConfirmText(getResources().getString(R.string.ok));
                    pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();

                        }
                    });
                    pDialog.show();

                }
                @Override
                public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    Log.d("JEJE", "onCodeSent:" + s);
                    loadVerification(s, txttelefono.getText().toString().trim());
                }

                @Override
                public void onCodeAutoRetrievalTimeOut(String s)
                {
                    pDialog.dismiss();

                    pDialog = new SweetAlertDialog(PerfilActivity.this, SweetAlertDialog.ERROR_TYPE);
                    pDialog.setTitleText(getResources().getString(R.string.app_name));
                    pDialog.setContentText(getResources().getString(R.string.error_validate_num));
                    pDialog.setConfirmText(getResources().getString(R.string.ok));
                    pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();

                        }
                    });
                    pDialog.show();
                }






            };


            verifyPhone(num,mCallBacks);

    }



    private void saveTask(final String image){
        //Showing the progress dialog


        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor(getString(R.string.colorAccent)));
        pDialog.setTitleText(getResources().getString(R.string.auth));
        pDialog.setCancelable(true);
        pDialog.show();

        //Constants.deleteCache(getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SERVER+"saveImagen/format/json",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responde) {
                        Log.d(TAG, responde);

                        //Showing toast message of the response

                        JSONObject res= null;
                        try {
                            res = new JSONObject(responde);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {

                            if(res.getString("result").equals("OK") ){
                                JSONArray mObjResp = res.getJSONArray("data");
                                JSONObject mObj = mObjResp.getJSONObject(0);

                                app.setUser(txtNombres.getText().toString().trim()+" "+ txtApellidos.getText().toString().trim());

                                databaseUsers.child(MainActivity.Utemp.getId()).child("name").setValue(txtNombres.getText().toString().trim());
                                databaseUsers.child(MainActivity.Utemp.getId()).child("lastname").setValue(txtApellidos.getText().toString().trim());


                                if(!image.equals("")) {
                                    databaseUsers.child(MainActivity.Utemp.getId()).child("url_imagen").setValue(mObj.getString("imagen"));

                                    MainActivity.Utemp.setUrl_imagen(mObj.getString("imagen"));
                                }



                                app.setActualizar("1");

                                pDialog.dismiss();

                                pDialog= new SweetAlertDialog(PerfilActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                                pDialog.setTitleText(getResources().getString(R.string.app_name));
                                pDialog.setContentText(res.getString("message"));
                                pDialog.setConfirmText(getResources().getString(R.string.ok));
                                pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();

                                        finish();

                                    }
                                });
                                pDialog.show();



                            }else
                            {
                                pDialog.dismiss();


                                pDialog= new SweetAlertDialog(PerfilActivity.this, SweetAlertDialog.ERROR_TYPE);
                                pDialog.setTitleText(getResources().getString(R.string.app_name));
                                pDialog.setContentText(res.getString("message"));
                                pDialog.setConfirmText(getResources().getString(R.string.ok));
                                pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();
                                        FirebaseAuth.getInstance().signOut();
                                        LoginManager.getInstance().logOut();
                                    }
                                });
                                pDialog.show();



                            }
                        } catch (JSONException e) {
                            pDialog.dismiss();
                            Log.d(TAG, e.toString());
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        pDialog.dismiss();

                        if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
                            VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                            volleyError = error;
                        }

                        //Showing toast
                        Log.d(TAG, volleyError.toString());
                        Toast.makeText(PerfilActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();


                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters


                params.put("image", image);
                try {
                    params.put("userid", Constants.Encrypt(app.getUserId()));
                    params.put("nombres", Constants.Encrypt(txtNombres.getText().toString()));
                    params.put("apellidos", Constants.Encrypt(txtApellidos.getText().toString()));
                    params.put("identificacion", Constants.Encrypt(txtIdentificacion.getText().toString()));
                    params.put("fecha_nacimiento", Constants.Encrypt(txtFecha.getText().toString()));
                    params.put("telefono", Constants.Encrypt(txttelefono.getText().toString()));
                    params.put("origen_mod",Constants.Encrypt(Constants.getIPAddress(true)));

                   /* params.put("tipo_identificacion", Constants.Encrypt(tipo_identificacion));
                    params.put("estado_civil", Constants.Encrypt(estado_civil));
                    params.put("ciudad_id", Constants.Encrypt(ciudad));*/


                    params.put("tipo_identificacion", Constants.Encrypt("1"));
                    params.put("estado_civil", Constants.Encrypt("1"));
                    params.put("ciudad_id", Constants.Encrypt("1"));
                    params.put("cargo", Constants.Encrypt("2"));

                } catch (Exception e) {
                    e.printStackTrace();
                }



                //returning parameters
                return params;
            }
        };


        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue volleyQueue = Volley.newRequestQueue(this);
        volleyQueue.add(stringRequest);
        DiskBasedCache cache = new DiskBasedCache(getCacheDir(), 500 * 1024 * 1024);
        volleyQueue = new RequestQueue(cache, new BasicNetwork(new HurlStack()));
        volleyQueue.start();

    }

    public void verifyPhone(String phoneNumber, PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                45,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallback
    }



    public void loadVerification(String codeID, String phone){
        Verification verification = new Verification();
        Bundle args = new Bundle();
        args.putString(Verification.ARGS_PHONE, phone);
        args.putString(Verification.ARGS_VER_CODE, codeID);
        verification.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, verification).commit();
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        monthOfYear = (++monthOfYear);
        String month = monthOfYear <= 9 ? "0" + monthOfYear : "" + monthOfYear;
        String day = dayOfMonth <= 9 ? "0" + dayOfMonth : "" + dayOfMonth;

        String date = year + "-" + month + "-" + day;
        txtFecha.setText(date);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == PICK_FROM_FILE) && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            //bitmap = ProcessImage.compressImage(filePath, getApplicationContext(), null);
            //Getting the Bitmap from Gallery
            performCrop(filePath);

        }
        if (requestCode == PICK_FROM_CAMERA && resultCode == RESULT_OK) {

            Uri imageUri = Uri.parse(mCurrentPhotoPath);
            // ScanFile so it will be appeared on Gallery
            MediaScannerConnection.scanFile(PerfilActivity.this,
                    new String[]{imageUri.getPath()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            performCrop(uri);
                        }
                    });


        }

        if(requestCode==CROP_FROM_CAMERA) {
            try {
                if(outPutFile.exists()){
                    //bitmap = decodeFile(outPutFile);

                    InputStream ims = new FileInputStream(outPutFile);
                    bitmap= BitmapFactory.decodeStream(ims);

                    //imagen.setImageBitmap(bitmap);

                    image = Constants.getStringImage(bitmap);
                    img.setImageBitmap(bitmap);

                    //imagen.setVisibility(View.VISIBLE);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Error while save image", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }

    public void showFileChooser() {

        PickImageDialog.build(new PickSetup()
                .setTitle(getResources().getString(R.string.image))
                .setTitleColor(getResources().getColor(R.color.colorPrimaryText))
                .setCameraButtonText(getResources().getString(R.string.camera))
                .setGalleryButtonText(getResources().getString(R.string.sd))
                .setButtonTextColor(getResources().getColor(R.color.colorPrimaryText))
                .setBackgroundColor(getResources().getColor(R.color.colorIcons))
                .setCancelText(getResources().getString(R.string.cancelar))
                .setCancelTextColor(getResources().getColor(R.color.colorPrimaryText))
                .setGalleryIcon(R.drawable.ic_perm_media_black_24dp)
                .setCameraIcon(R.drawable.ic_photo_camera_black_24dp)

        ).show(getSupportFragmentManager());


    }

    @Override
    public void onPickResult(PickResult r) {
        if (r.getError() == null) {
            //If you want the Uri.
            //Mandatory to refresh image from Uri.
            //getImageView().setImageURI(null);

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            r.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(),  r.getBitmap(), "temp", null);
            performCrop(Uri.parse(path));

        } else {
            //Handle possible errors
            //TODO: do what you have to do with r.getError();
            Toast.makeText(this, r.getError().getMessage(), Toast.LENGTH_LONG).show();
        }
    }



    private void performCrop(Uri uri) {

        int x=dpToPx(280);
        int y=dpToPx(280);

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", x);
        intent.putExtra("outputY", y);
        //intent.putExtra("scale", true);
        intent.putExtra("noFaceDetection", true);
        //intent.putExtra("return-data", true);
        //Create output file here
        try {
            /*mImageCaptureUri = FileProvider.getUriForFile(AddPlatoActivity.this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    createImageFile());*/
            outPutFile =createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }



        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outPutFile));
        startActivityForResult(intent, CROP_FROM_CAMERA);
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".png",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    @Override
    public void onBackPressed() {


        validate();




    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                validate();


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();



    }

    private  void validate()
    {
        if(MainActivity.validate_phone == 0 ) {
            super.onBackPressed();
            finish();
        }else{

            pDialog= new SweetAlertDialog(PerfilActivity.this, SweetAlertDialog.ERROR_TYPE);
            pDialog.setTitleText(getResources().getString(R.string.app_name));
            pDialog.setContentText(getResources().getString(R.string.complete_perfil));
            pDialog.setConfirmText(getResources().getString(R.string.ok));
            pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sDialog) {
                    sDialog.dismissWithAnimation();

                }
            });
            pDialog.show();

        }
    }




}
