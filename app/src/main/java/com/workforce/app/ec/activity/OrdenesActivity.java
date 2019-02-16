package com.workforce.app.ec.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.workforce.app.ec.R;
import com.workforce.app.ec.clases.GPS;
import com.workforce.app.ec.clases.Ordenes;
import com.workforce.app.ec.config.AppPreferences;
import com.workforce.app.ec.config.Constants;
import com.workforce.app.ec.fragments.TrabajosActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.facebook.FacebookSdk.getCacheDir;

public class OrdenesActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener  {

    private Toolbar toolbar;
    private String TAG = TrabajosActivity.class.getName();
    private SweetAlertDialog pDialog;
    private Ordenes orden = new Ordenes();

    private TextView txtServicio,txtCliente,txtFecha,txtCosto,txtContacto,txtUbicacion,txtDistancia;
    private LinearLayout conCalificacion,conImagen,conTerminar,conAsignar,conFinalizar;

    private RatingBar ratingBar;
    private ImageView img1,img2,imgPhone,imgMapa;

    private Button btnAsignar,btnEjecutar,btnCancelar,btnFinalizar;

    private AppPreferences appPreferences;
    private String lat=null,log=null;
    private GPS gps = null;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private GoogleApiClient googleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordenes);

        /* toolbar*/
        toolbar = (Toolbar) findViewById(R.id.toolbaruser);

        TextView title = (TextView) findViewById(R.id.txtTitle);

        title.setText(getString(R.string.orden));

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSupportActionBar().setHomeAsUpIndicator(getDrawable(R.drawable.ic_arrow));
        } else {
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_arrow));
        }

        appPreferences = new AppPreferences(OrdenesActivity.this);

        googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();

        gps = new GPS(OrdenesActivity.this);
        if (!gps.canGetLocation()) {
            //gps.showSettingsAlert();
            settingsrequest();
        }else {
            //gps.city();
            lat = String.valueOf(gps.getLatitude());
            log = String.valueOf(gps.getLongitude());
        }


        txtServicio = (TextView) findViewById(R.id.txtServicio);
        txtDistancia = (TextView) findViewById(R.id.txtDistancia);
        txtCliente = (TextView) findViewById(R.id.txtCliente);
        txtFecha = (TextView) findViewById(R.id.txtFecha);
        txtCosto = (TextView) findViewById(R.id.txtCosto);
        txtContacto = (TextView) findViewById(R.id.txtContacto);
        txtUbicacion = (TextView) findViewById(R.id.txtUbicacion);

        conCalificacion =(LinearLayout)  findViewById(R.id.conCalificacion);
        conImagen =(LinearLayout)  findViewById(R.id.conImagen);
        conTerminar =(LinearLayout)  findViewById(R.id.conTerminar);
        conAsignar =(LinearLayout)  findViewById(R.id.conAsignar);
        conFinalizar =(LinearLayout)  findViewById(R.id.conFinalizar);

        ratingBar =(RatingBar)  findViewById(R.id.ratingBar);

        img1 =(ImageView)  findViewById(R.id.img1);
        img2 =(ImageView)  findViewById(R.id.img2);

        imgPhone =(ImageView)  findViewById(R.id.imgPhone);
        imgMapa =(ImageView)  findViewById(R.id.imgMapa);

        btnAsignar =(Button)  findViewById(R.id.btnAsignar);
        btnEjecutar =(Button)  findViewById(R.id.btnEjecutar);
        btnCancelar =(Button)  findViewById(R.id.btnCancelar);
        btnFinalizar =(Button)  findViewById(R.id.btnFinalizar);

        txtDistancia.setText(getIntent().getStringExtra("distancia"));

        laodTask(getIntent().getStringExtra("id"));

        btnAsignar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pDialog = new SweetAlertDialog(OrdenesActivity.this, SweetAlertDialog.WARNING_TYPE);
                pDialog.setTitleText(getResources().getString(R.string.app_name));
                pDialog.setContentText(getString(R.string.asignar));
                pDialog.setConfirmText(getResources().getString(R.string.yes));
                pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        asignar(Integer.parseInt(getIntent().getStringExtra("id")));

                    }
                });
                pDialog.setCancelText(getString(R.string.no));
                pDialog.show();
            }
        });

        btnEjecutar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ejecucion(Integer.parseInt(getIntent().getStringExtra("id")));


            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cancelar();

            }
        });

        btnFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalizar(Integer.parseInt(getIntent().getStringExtra("id")));
            }
        });

        imgPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", "0"+orden.getTelefono(), null)));
            }
        });

        imgMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String uri = "http://maps.google.com/maps?saddr=" + orden.getLatitud() + "," + orden.getLongitud() + "&daddr=" + lat + "," + log;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);

            }
        });




    }


    private void laodTask(final String id){

        final JSONObject[] res = {null};
        //Showing the progress dialog


        Constants.deleteCache(OrdenesActivity.this);

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor(getString(R.string.colorAccent)));
        pDialog.setTitleText(getResources().getString(R.string.auth));
        pDialog.setCancelable(false);
        pDialog.show();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SERVER+"getServicios/format/json",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responde) {
                        Log.d(TAG, responde);

                        //Showing toast message of the response


                        try {
                            res[0] = new JSONObject(responde);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            pDialog.dismiss();
                        }

                        try {

                            if(res[0].getString("result").equals("OK")) {




                                final JSONArray[] mObjResp = {null};

                                try {

                                    mObjResp[0] = res[0].getJSONArray("data");

                                    final JSONObject[] mObj = new JSONObject[1];




                                    for (int x = 0; x < mObjResp[0].length(); x++) {


                                        mObj[0] = mObjResp[0].getJSONObject(x);


                                        final JSONObject finalMObj =  mObj[0];

                                        try {


                                            orden = new Ordenes(Integer.parseInt(Constants.Decrypt(finalMObj.getString("id"))), Constants.Decrypt(finalMObj.getString("cliente")), Constants.Decrypt(finalMObj.getString("servicio")), Integer.parseInt(Constants.Decrypt(finalMObj.getString("estado"))), Constants.Decrypt(finalMObj.getString("fecha")), Constants.Decrypt(finalMObj.getString("costo")), Constants.Decrypt(finalMObj.getString("direccion")), Constants.Decrypt(finalMObj.getString("longitud")),Constants.Decrypt(finalMObj.getString("latitud")),Constants.Decrypt(finalMObj.getString("telefono")),Constants.Decrypt(finalMObj.getString("piso")),Constants.Decrypt(finalMObj.getString("departamento")),Integer.parseInt(Constants.Decrypt(finalMObj.getString("calificacion"))),finalMObj.getString("imagen1"),finalMObj.getString("imagen2"));

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    }

                                    txtServicio.setText(orden.getServicio());
                                    txtCliente.setText(orden.getCliente());
                                    txtFecha.setText(orden.getFecha());
                                    txtCosto.setText("$ "+orden.getCosto());
                                    txtContacto.setText("0"+orden.getTelefono());
                                    txtUbicacion.setText(orden.getDireccion());

                                    Log.e("estado",String.valueOf(orden.getEstado()));

                                    if(orden.getEstado()== 1){
                                        txtContacto.setVisibility(View.GONE);
                                        txtUbicacion.setVisibility(View.GONE);
                                        imgPhone.setVisibility(View.GONE);
                                        imgMapa.setVisibility(View.GONE);
                                    }

                                    if (orden.getCalificacion()!= 0)
                                    {
                                        ratingBar.setNumStars(orden.getCalificacion());
                                        conCalificacion.setVisibility(View.VISIBLE);
                                    }else
                                    {
                                        conCalificacion.setVisibility(View.GONE);
                                    }

                                    Log.e("lo",String.valueOf(orden.getEstado()));

                                    if(orden.getEstado()==2)
                                    {
                                        conTerminar.setVisibility(View.VISIBLE);
                                    }else if (orden.getEstado()==1){

                                        conAsignar.setVisibility(View.VISIBLE);
                                    }else if (orden.getEstado()==4){
                                        conAsignar.setVisibility(View.GONE);
                                        conTerminar.setVisibility(View.GONE);
                                        conFinalizar.setVisibility(View.VISIBLE);
                                    }else if(orden.getEstado()==3)
                                    {
                                        conAsignar.setVisibility(View.GONE);
                                        conTerminar.setVisibility(View.GONE);
                                        conFinalizar.setVisibility(View.GONE);
                                        //conFinalizar.setVisibility(View.VISIBLE);
                                    }
                                    else
                                    {
                                        conTerminar.setVisibility(View.GONE);
                                    }

                                    if (orden.getImagen1().equals("") && orden.getImagen2().equals(""))
                                    {
                                        conImagen.setVisibility(View.GONE);
                                    }else
                                    {

                                        if (orden.getEstado()!=1) {
                                            Glide.with(OrdenesActivity.this).load(orden.getImagen1())
                                                    .thumbnail(1.0f)
                                                    .crossFade()
                                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                    .into(img1);

                                            Log.e("imagen1", orden.getImagen1());

                                            Glide.with(OrdenesActivity.this).load(orden.getImagen2())
                                                    .thumbnail(1.0f)
                                                    .crossFade()
                                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                    .into(img2);

                                            Log.e("imagen2", orden.getImagen2());

                                            conImagen.setVisibility(View.VISIBLE);
                                        }else{
                                            conImagen.setVisibility(View.GONE);
                                        }

                                    }



                                    pDialog.dismiss();









                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }else
                            {



                                pDialog= new SweetAlertDialog(OrdenesActivity.this, SweetAlertDialog.ERROR_TYPE);
                                pDialog.setTitleText(getResources().getString(R.string.app_name));
                                pDialog.setContentText(Constants.Decrypt(res[0].getString("message")));
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


                        if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
                            VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                            volleyError = error;
                        }

                        //Showing toast
                        Log.d(TAG, volleyError.toString());
                        Toast.makeText(OrdenesActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();


                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters

                try {
                    params.put("id", Constants.Encrypt(id));
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

        RequestQueue volleyQueue = Volley.newRequestQueue(OrdenesActivity.this);
        volleyQueue.add(stringRequest);
        DiskBasedCache cache = new DiskBasedCache(getCacheDir(), 500 * 1024 * 1024);
        volleyQueue = new RequestQueue(cache, new BasicNetwork(new HurlStack()));
        volleyQueue.start();




    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //onBackPressed();
                finish();
                //------------
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void asignar(final int idOrden)
    {
        final JSONObject[] res = {null};
        //Showing the progress dialog


        Constants.deleteCache(OrdenesActivity.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SERVER+"asignar_orden/format/json",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responde) {
                        Log.d(TAG, responde);

                        //Showing toast message of the response


                        try {
                            res[0] = new JSONObject(responde);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            pDialog.dismiss();
                        }

                        try {

                            if(res[0].getString("result").equals("OK")) {




                                final JSONArray[] mObjResp = {null};

                                try {
                                    mObjResp[0] = res[0].getJSONArray("data");

                                    pDialog.dismiss();

                                    pDialog= new SweetAlertDialog(OrdenesActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                                    pDialog.setTitleText(getResources().getString(R.string.app_name));
                                    pDialog.setContentText(Constants.Decrypt(res[0].getString("message")));
                                    pDialog.setConfirmText(getResources().getString(R.string.ok));
                                    pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();

                                            conAsignar.setVisibility(View.GONE);
                                            conTerminar.setVisibility(View.VISIBLE);

                                        }
                                    });
                                    pDialog.show();







                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }else
                            {
                                pDialog.dismiss();


                                pDialog= new SweetAlertDialog(OrdenesActivity.this, SweetAlertDialog.ERROR_TYPE);
                                pDialog.setTitleText(getResources().getString(R.string.app_name));
                                pDialog.setContentText(Constants.Decrypt(res[0].getString("message")));
                                pDialog.setConfirmText(getResources().getString(R.string.ok));
                                pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();

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
                        Toast.makeText(OrdenesActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();


                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters

                try {
                    params.put("userid",Constants.Encrypt(appPreferences.getUserId()));
                    params.put("idorden", Constants.Encrypt(String.valueOf(idOrden)));
                    params.put("origen_mod",Constants.Encrypt(Constants.getIPAddress(true)));
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

        RequestQueue volleyQueue = Volley.newRequestQueue(OrdenesActivity.this);
        volleyQueue.add(stringRequest);
        DiskBasedCache cache = new DiskBasedCache(getCacheDir(), 500 * 1024 * 1024);
        volleyQueue = new RequestQueue(cache, new BasicNetwork(new HurlStack()));
        volleyQueue.start();

    }

    private void ejecucion(final int idOrden)
    {
        final JSONObject[] res = {null};
        //Showing the progress dialog


        Constants.deleteCache(OrdenesActivity.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SERVER+"ejecutar_orden/format/json",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responde) {
                        Log.d(TAG, responde);

                        //Showing toast message of the response


                        try {
                            res[0] = new JSONObject(responde);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            pDialog.dismiss();
                        }

                        try {

                            if(res[0].getString("result").equals("OK")) {




                                final JSONArray[] mObjResp = {null};

                                try {
                                    mObjResp[0] = res[0].getJSONArray("data");

                                    pDialog.dismiss();

                                    pDialog= new SweetAlertDialog(OrdenesActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                                    pDialog.setTitleText(getResources().getString(R.string.app_name));
                                    pDialog.setContentText(Constants.Decrypt(res[0].getString("message")));
                                    pDialog.setConfirmText(getResources().getString(R.string.ok));
                                    pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();

                                            conTerminar.setVisibility(View.GONE);
                                            conFinalizar.setVisibility(View.VISIBLE);


                                        }
                                    });
                                    pDialog.show();







                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }else
                            {
                                pDialog.dismiss();


                                pDialog= new SweetAlertDialog(OrdenesActivity.this, SweetAlertDialog.ERROR_TYPE);
                                pDialog.setTitleText(getResources().getString(R.string.app_name));
                                pDialog.setContentText(Constants.Decrypt(res[0].getString("message")));
                                pDialog.setConfirmText(getResources().getString(R.string.ok));
                                pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();

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
                        Toast.makeText(OrdenesActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();


                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters

                try {
                    params.put("userid",Constants.Encrypt(appPreferences.getUserId()));
                    params.put("idorden", Constants.Encrypt(String.valueOf(idOrden)));
                    params.put("origen_mod",Constants.Encrypt(Constants.getIPAddress(true)));
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

        RequestQueue volleyQueue = Volley.newRequestQueue(OrdenesActivity.this);
        volleyQueue.add(stringRequest);
        DiskBasedCache cache = new DiskBasedCache(getCacheDir(), 500 * 1024 * 1024);
        volleyQueue = new RequestQueue(cache, new BasicNetwork(new HurlStack()));
        volleyQueue.start();

    }

    private void cancelar(final int idOrden,final String observacion)
    {
        final JSONObject[] res = {null};
        //Showing the progress dialog


        Constants.deleteCache(OrdenesActivity.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SERVER+"cancelar_orden/format/json",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responde) {
                        Log.d(TAG, responde);

                        //Showing toast message of the response


                        try {
                            res[0] = new JSONObject(responde);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            pDialog.dismiss();
                        }

                        try {

                            if(res[0].getString("result").equals("OK")) {




                                final JSONArray[] mObjResp = {null};

                                try {
                                    mObjResp[0] = res[0].getJSONArray("data");

                                    pDialog.dismiss();

                                    pDialog= new SweetAlertDialog(OrdenesActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                                    pDialog.setTitleText(getResources().getString(R.string.app_name));
                                    pDialog.setContentText(Constants.Decrypt(res[0].getString("message")));
                                    pDialog.setConfirmText(getResources().getString(R.string.ok));
                                    pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();

                                            conTerminar.setVisibility(View.GONE);


                                        }
                                    });
                                    pDialog.show();







                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }else
                            {
                                pDialog.dismiss();


                                pDialog= new SweetAlertDialog(OrdenesActivity.this, SweetAlertDialog.ERROR_TYPE);
                                pDialog.setTitleText(getResources().getString(R.string.app_name));
                                pDialog.setContentText(Constants.Decrypt(res[0].getString("message")));
                                pDialog.setConfirmText(getResources().getString(R.string.ok));
                                pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();

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
                        Toast.makeText(OrdenesActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();


                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters

                try {
                    params.put("userid",Constants.Encrypt(appPreferences.getUserId()));
                    params.put("idorden", Constants.Encrypt(String.valueOf(idOrden)));
                    params.put("origen_mod",Constants.Encrypt(Constants.getIPAddress(true)));
                    params.put("observacion",Constants.Encrypt(observacion));
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

        RequestQueue volleyQueue = Volley.newRequestQueue(OrdenesActivity.this);
        volleyQueue.add(stringRequest);
        DiskBasedCache cache = new DiskBasedCache(getCacheDir(), 500 * 1024 * 1024);
        volleyQueue = new RequestQueue(cache, new BasicNetwork(new HurlStack()));
        volleyQueue.start();

    }


    private void finalizar(final int idOrden){
        final JSONObject[] res = {null};
        //Showing the progress dialog


        Constants.deleteCache(OrdenesActivity.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SERVER+"finalizar_orden/format/json",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responde) {
                        Log.d(TAG, responde);

                        //Showing toast message of the response


                        try {
                            res[0] = new JSONObject(responde);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            pDialog.dismiss();
                        }

                        try {

                            if(res[0].getString("result").equals("OK")) {




                                final JSONArray[] mObjResp = {null};

                                try {
                                    mObjResp[0] = res[0].getJSONArray("data");

                                    pDialog.dismiss();

                                    pDialog= new SweetAlertDialog(OrdenesActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                                    pDialog.setTitleText(getResources().getString(R.string.app_name));
                                    pDialog.setContentText(Constants.Decrypt(res[0].getString("message")));
                                    pDialog.setConfirmText(getResources().getString(R.string.ok));
                                    pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();

                                            conFinalizar.setVisibility(View.GONE);


                                        }
                                    });
                                    pDialog.show();







                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }else
                            {
                                pDialog.dismiss();


                                pDialog= new SweetAlertDialog(OrdenesActivity.this, SweetAlertDialog.ERROR_TYPE);
                                pDialog.setTitleText(getResources().getString(R.string.app_name));
                                pDialog.setContentText(Constants.Decrypt(res[0].getString("message")));
                                pDialog.setConfirmText(getResources().getString(R.string.ok));
                                pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();

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
                        Toast.makeText(OrdenesActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();


                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters

                try {
                    params.put("userid",Constants.Encrypt(appPreferences.getUserId()));
                    params.put("idorden", Constants.Encrypt(String.valueOf(idOrden)));
                    params.put("origen_mod",Constants.Encrypt(Constants.getIPAddress(true)));
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

        RequestQueue volleyQueue = Volley.newRequestQueue(OrdenesActivity.this);
        volleyQueue.add(stringRequest);
        DiskBasedCache cache = new DiskBasedCache(getCacheDir(), 500 * 1024 * 1024);
        volleyQueue = new RequestQueue(cache, new BasicNetwork(new HurlStack()));
        volleyQueue.start();
    }



    public void settingsrequest()
    {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(OrdenesActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
// Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        lat = String.valueOf(gps.getLatitude());
                        log = String.valueOf(gps.getLongitude());

                        break;
                    case Activity.RESULT_CANCELED:
                        settingsrequest();//keep asking if imp or do whatever
                        break;
                }
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    public void cancelar()
    {

        final Dialog settingsDialog = new Dialog(this);
        settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View v=getLayoutInflater().inflate(R.layout.item_cancelar
                , null);

        Button btnclose=(Button) v.findViewById(R.id.close);
        btnclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsDialog.dismiss();
            }
        });

        final EditText txtmotivo=(EditText) v.findViewById(R.id.txtMotivo);

        Button btnsend=(Button) v.findViewById(R.id.send);
        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!txtmotivo.getText().toString().equals("")) {

                    settingsDialog.dismiss();
                    cancelar(Integer.parseInt(getIntent().getStringExtra("id")),txtmotivo.getText().toString());
                }else
                {
                    txtmotivo.setError(getString(R.string.motivo_ingrese));
                }
            }
        });

        settingsDialog.setContentView(v);
        settingsDialog.show();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(settingsDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        settingsDialog.show();
        settingsDialog.getWindow().setAttributes(lp);
    }
}
