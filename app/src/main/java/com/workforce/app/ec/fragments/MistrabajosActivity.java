package com.workforce.app.ec.fragments;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
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
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.workforce.app.ec.R;
import com.workforce.app.ec.activity.MainActivity;
import com.workforce.app.ec.activity.OrdenesActivity;
import com.workforce.app.ec.clases.GPS;
import com.workforce.app.ec.clases.Ordenes;
import com.workforce.app.ec.config.AppPreferences;
import com.workforce.app.ec.config.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.facebook.FacebookSdk.getCacheDir;

public class MistrabajosActivity extends Fragment {

    private SearchView searchView;

    public static ArrayList<Ordenes> mListServicios;
    private ArrayList<Ordenes> mListServiciosFilter;
    private ServiciesRecycleAdapter mServiciesAdapter;
    private RecyclerView mServiciosRecyclerView;
    private SweetAlertDialog pDialog;
    private String TAG = TrabajosActivity.class.getName();

    private AppPreferences appPreferences;
    private ProgressBar progressBar;

    private GPS gps = null;
    private String lat,log;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.activity_mistrabajos, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {


        appPreferences = new AppPreferences(getContext());

        gps = new GPS(getContext());
        if (!gps.canGetLocation()) {

        }else {
            //gps.city();
            lat = String.valueOf(gps.getLatitude());
            log = String.valueOf(gps.getLongitude());
        }

        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        mServiciosRecyclerView = (RecyclerView) v.findViewById(R.id.ordenes);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, 1);

        mServiciosRecyclerView.setLayoutManager(layoutManager);
        mServiciesAdapter = new ServiciesRecycleAdapter();
        mServiciosRecyclerView.setAdapter(mServiciesAdapter);

        mListServicios = new ArrayList<Ordenes>();
        mListServiciosFilter = new ArrayList<Ordenes>();


        searchView=(SearchView) v.findViewById(R.id.busqueda);

        //busqueda
        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mServiciesAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mServiciesAdapter.getFilter().filter(query);
                return false;
            }
        });

        progressBar.setVisibility(View.VISIBLE);
        mServiciosRecyclerView.setVisibility(View.GONE);


    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public void onResume() {
        super.onResume();

        loadTask();
    }

    /* adapter*/

    public class ServiciesRecycleAdapter extends RecyclerView.Adapter<ServiciesRecycleHolder>   implements Filterable {
        private int lastPosition = -1;

        @Override
        public ServiciesRecycleHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_servicios, viewGroup, false);
            setAnimation(v,i);
            return new ServiciesRecycleHolder(v);
        }


        @Override
        public void onBindViewHolder(final ServiciesRecycleHolder productHolder, final int i) {

            productHolder.mtxtNombre.setText(mListServiciosFilter.get(i).getCliente());
            productHolder.mtxtFecha.setText(mListServiciosFilter.get(i).getFecha());
            productHolder.mtxtServicio.setText(mListServiciosFilter.get(i).getServicio());
            productHolder.mtxtDistancia.setText(mListServiciosFilter.get(i).getDistancia());





            if (mListServiciosFilter.get(i).getEstado()==0){
                productHolder.mtxtEstado.setText("Cancelado");
            }else if (mListServiciosFilter.get(i).getEstado()==2){
                productHolder.mtxtEstado.setText("Asignado");

            }else if (mListServiciosFilter.get(i).getEstado()==5){
                productHolder.mtxtEstado.setText("En Proceso");
            }
            else if (mListServiciosFilter.get(i).getEstado()==1){
                productHolder.mtxtEstado.setText("Creado");
            }
            else if (mListServiciosFilter.get(i).getEstado()==4){
                productHolder.mtxtEstado.setText("Pen. Calif");
            }
            else{
                productHolder.mtxtEstado.setText("Terminado");
            }

            productHolder.mContenedor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE);
                    pDialog.setTitleText(getResources().getString(R.string.app_name));
                    pDialog.setContentText(getString(R.string.asignar));
                    pDialog.setConfirmText(getResources().getString(R.string.yes));
                    pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            //asignar(mListServiciosFilter.get(i).getId(),i);

                        }
                    });
                    pDialog.setCancelText(getString(R.string.no));
                    pDialog.show();

                }
            });


            productHolder.mImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getContext(), OrdenesActivity.class);
                    intent.putExtra("id",String.valueOf(mListServiciosFilter.get(i).getId()));
                    intent.putExtra("distancia",String.valueOf(mListServiciosFilter.get(i).getDistancia()));
                    startActivity(intent);

                }
            });

            setAnimation(productHolder.itemView, i);



        }


        @Override
        public int getItemCount() {
            return mListServiciosFilter.size();
        }

        public void removeItem(int position) {
            mListServiciosFilter.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mListServiciosFilter.size());
            //Signal.get().reset();


        }

        private void setAnimation(View viewToAnimate, int position) {
            // If the bound view wasn't previously displayed on screen, it's animated
            if (position > lastPosition) {
                Animation animation;
                if (position % 2 == 0) {
                    animation = AnimationUtils.loadAnimation(getContext(), R.anim.zoom_back_in);
                } else {
                    animation = AnimationUtils.loadAnimation(getContext(), R.anim.zoom_forward_in);
                }

                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            }
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    String charString = charSequence.toString();
                    if (charString.isEmpty()) {
                        mListServiciosFilter = mListServicios;
                    } else {
                        ArrayList<Ordenes> filteredList = new ArrayList<>();
                        for (Ordenes row : mListServicios) {

                            // name match condition. this might differ depending on your requirement
                            // here we are looking for name or phone number match


                            if (row.getCliente().toLowerCase().contains(charString.toLowerCase()) || row.getServicio().toLowerCase().contains(charString.toLowerCase()) || row.getDireccion().toLowerCase().contains(charString.toLowerCase()) || row.getFecha().toLowerCase().contains(charString.toLowerCase()))
                            {
                                filteredList.add(row);
                            }

                        }

                        mListServiciosFilter = filteredList;
                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = mListServiciosFilter;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    mListServiciosFilter = (ArrayList<Ordenes>) filterResults.values;
                    notifyDataSetChanged();
                }
            };
        }


    }

    public class  ServiciesRecycleHolder extends RecyclerView.ViewHolder {
        public TextView mtxtNombre;
        public TextView mtxtFecha;
        public TextView mtxtServicio;
        public TextView mtxtDistancia;
        public TextView mtxtEstado;
        public CardView mContenedor;
        public ImageView mImg;




        public  ServiciesRecycleHolder(View itemView) {
            super(itemView);
            mtxtNombre = (TextView) itemView.findViewById(R.id.txtNombre);
            mtxtFecha = (TextView) itemView.findViewById(R.id.txtFecha);
            mtxtServicio = (TextView) itemView.findViewById(R.id.txtServicio);
            mContenedor = (CardView) itemView.findViewById(R.id.contenedor);
            mtxtDistancia = (TextView) itemView.findViewById(R.id.txtDistancia);
            mtxtEstado = (TextView) itemView.findViewById(R.id.txtEstado);
            mImg = (ImageView) itemView.findViewById(R.id.img);

        }
    }


    private void loadTask(){

        final JSONObject[] res = {null};
        //Showing the progress dialog


        Constants.deleteCache(getContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SERVER+"getServicioAsignada/format/json",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responde) {
                        Log.d(TAG, responde);

                        //Showing toast message of the response


                        try {
                            res[0] = new JSONObject(responde);
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }

                        try {

                            if(res[0].getString("result").equals("OK")) {




                                final JSONArray[] mObjResp = {null};

                                try {
                                    mObjResp[0] = res[0].getJSONArray("data");

                                    final JSONObject[] mObj = new JSONObject[1];

                                    mListServicios.clear();


                                    for (int x = 0; x < mObjResp[0].length(); x++) {


                                        mObj[0] = mObjResp[0].getJSONObject(x);


                                        final JSONObject finalMObj =  mObj[0];


                                                try {




                                                    Ordenes orden =  new Ordenes(Integer.parseInt(Constants.Decrypt(finalMObj.getString("id"))), Constants.Decrypt(finalMObj.getString("cliente")), Constants.Decrypt(finalMObj.getString("servicio")), Integer.parseInt(Constants.Decrypt(finalMObj.getString("estado"))), Constants.Decrypt(finalMObj.getString("fecha")), Constants.Decrypt(finalMObj.getString("costo")), Constants.Decrypt(finalMObj.getString("direccion")), Constants.Decrypt(finalMObj.getString("longitud")),Constants.Decrypt(finalMObj.getString("latitud")));

                                                    orden.setDistancia(Constants.distancia(Double.parseDouble(lat), Double.parseDouble(log), Double.parseDouble(orden.getLatitud()), Double.parseDouble(orden.getLongitud())));


                                                    mListServicios.add(orden);

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }




                                    }

                                    mListServiciosFilter = mListServicios;
                                    progressBar.setVisibility(View.GONE);

                                    if (mListServiciosFilter.size()>0) {


                                        mServiciosRecyclerView.setVisibility(View.VISIBLE);

                                        mServiciesAdapter.notifyDataSetChanged();
                                    }else
                                    {
                                        pDialog= new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE);
                                        pDialog.setTitleText(getResources().getString(R.string.app_name));
                                        pDialog.setContentText(getResources().getString(R.string.noordenesasi));
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
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }else
                            {



                                pDialog= new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE);
                                pDialog.setTitleText(getResources().getString(R.string.app_name));
                                pDialog.setContentText(Constants.Decrypt(res[0].getString("message")));
                                pDialog.setConfirmText(getResources().getString(R.string.ok));
                                pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();
                                        FirebaseAuth.getInstance().signOut();
                                        LoginManager.getInstance().logOut();
                                        getActivity().finish();
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
                        Toast.makeText(getContext(), volleyError.toString(), Toast.LENGTH_LONG).show();


                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters

                try {
                    params.put("userid", appPreferences.getUserId());
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

        RequestQueue volleyQueue = Volley.newRequestQueue(getContext());
        volleyQueue.add(stringRequest);
        DiskBasedCache cache = new DiskBasedCache(getCacheDir(), 500 * 1024 * 1024);
        volleyQueue = new RequestQueue(cache, new BasicNetwork(new HurlStack()));
        volleyQueue.start();




    }


}
