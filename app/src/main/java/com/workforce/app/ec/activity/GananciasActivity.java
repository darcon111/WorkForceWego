package com.workforce.app.ec.activity;

import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import com.workforce.app.ec.R;
import com.workforce.app.ec.clases.Ordenes;
import com.workforce.app.ec.config.AppPreferences;
import com.workforce.app.ec.config.Constants;
import com.workforce.app.ec.fragments.TrabajosActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class GananciasActivity extends AppCompatActivity {


    private Toolbar toolbar;
    private String TAG = TrabajosActivity.class.getName();
    private SweetAlertDialog pDialog;
    private AppPreferences appPreferences;
    private TextView txtTrabajos,txtSaldos;
    private ArrayList<Ordenes> mListOrdenes;
    private double costo= 0;
    private int cantidad = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ganancias);

        /* toolbar*/
        toolbar = (Toolbar) findViewById(R.id.toolbaruser);

        TextView title = (TextView) findViewById(R.id.txtTitle);

        title.setText(getString(R.string.ganancias));

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSupportActionBar().setHomeAsUpIndicator(getDrawable(R.drawable.ic_arrow));
        } else {
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_arrow));
        }

        appPreferences = new AppPreferences(GananciasActivity.this);

        txtTrabajos = (TextView) findViewById(R.id.txtTrabajos);
        txtSaldos = (TextView) findViewById(R.id.txtSaldos);

        mListOrdenes = new ArrayList<Ordenes>();

        laodTask(appPreferences.getUserId());

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


    private void laodTask(final String id){

        final JSONObject[] res = {null};
        //Showing the progress dialog


        Constants.deleteCache(GananciasActivity.this);

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor(getString(R.string.colorAccent)));
        pDialog.setTitleText(getResources().getString(R.string.auth));
        pDialog.setCancelable(false);
        pDialog.show();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SERVER+"getGanancias/format/json",
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


                                    if (mObjResp[0]!=null) {
                                        if (mObjResp[0].length()>0) {
                                            for (int x = 0; x < mObjResp[0].length(); x++) {


                                                mObj[0] = mObjResp[0].getJSONObject(x);


                                                final JSONObject finalMObj = mObj[0];

                                                try {


                                                    Ordenes orden = new Ordenes(Integer.parseInt(Constants.Decrypt(finalMObj.getString("id"))), Constants.Decrypt(finalMObj.getString("cliente")), Constants.Decrypt(finalMObj.getString("servicio")), Integer.parseInt(Constants.Decrypt(finalMObj.getString("estado"))), Constants.Decrypt(finalMObj.getString("fecha")), Constants.Decrypt(finalMObj.getString("costo")), Constants.Decrypt(finalMObj.getString("direccion")), Constants.Decrypt(finalMObj.getString("longitud")), Constants.Decrypt(finalMObj.getString("latitud")), Constants.Decrypt(finalMObj.getString("telefono")), Constants.Decrypt(finalMObj.getString("piso")), Constants.Decrypt(finalMObj.getString("departamento")), Integer.parseInt(Constants.Decrypt(finalMObj.getString("calificacion"))), finalMObj.getString("imagen1"), finalMObj.getString("imagen2"));

                                                    costo += Double.parseDouble(orden.getCosto());
                                                    cantidad++;

                                                    mListOrdenes.add(orden);

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        }
                                    }

                                    txtSaldos.setText("$ "+String.valueOf(costo));
                                    txtTrabajos.setText(String.valueOf(cantidad));




                                    pDialog.dismiss();









                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }else
                            {



                                pDialog= new SweetAlertDialog(GananciasActivity.this, SweetAlertDialog.ERROR_TYPE);
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
                        Toast.makeText(GananciasActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();


                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters

                try {
                    params.put("userid", Constants.Encrypt(id));
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

        RequestQueue volleyQueue = Volley.newRequestQueue(GananciasActivity.this);
        volleyQueue.add(stringRequest);
        DiskBasedCache cache = new DiskBasedCache(getCacheDir(), 500 * 1024 * 1024);
        volleyQueue = new RequestQueue(cache, new BasicNetwork(new HurlStack()));
        volleyQueue.start();




    }
}
