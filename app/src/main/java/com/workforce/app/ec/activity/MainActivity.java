package com.workforce.app.ec.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
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
import com.crashlytics.android.Crashlytics;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.workforce.app.ec.R;
import com.workforce.app.ec.adapter.MenuAdapter;
import com.workforce.app.ec.clases.Ordenes;
import com.workforce.app.ec.clases.Spinner.MaterialSpinner;
import com.workforce.app.ec.clases.User;
import com.workforce.app.ec.config.AppPreferences;
import com.workforce.app.ec.config.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView mRecyclerView_main;
    private String[] TITLES = new String[8];
    private int[] ICONS = new int[8];
    private ActionBarDrawerToggle mDrawerToggle;

    private int PROFILE = R.drawable.ic_user;
    private MenuAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DrawerLayout Drawer;
    private String TAG = MainActivity.class.getName();
    private String name="Usuario";
    private static FirebaseUser user;


    private SweetAlertDialog pDialog;
    private AppPreferences appPreferences;
    private DatabaseReference databaseUsers;
    public static User Utemp;
    private String provider;
    private String imagen;
    public static int validate_phone = 1;

    private SearchView searchView;

    public static ArrayList<Ordenes> mListServicios;
    private ArrayList<Ordenes> mListServiciosFilter;
    private ServiciesRecycleAdapter mServiciesAdapter;
    private RecyclerView mServiciosRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);


        appPreferences = new AppPreferences(MainActivity.this);

        /* toolbar*/
        toolbar = (Toolbar) findViewById(R.id.toolbaruser);

        TextView title = (TextView) findViewById(R.id.txtTitle);

        title.setText(getString(R.string.app_name));

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSupportActionBar().setHomeAsUpIndicator(getDrawable(R.drawable.ic_list_white_24dp));
        } else {
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_list_white_24dp));
        }

        /*main menu*/
        mRecyclerView_main = (RecyclerView) findViewById(R.id.RecyclerView_main); // Assigning the RecyclerView Object to the xml View
        mRecyclerView_main.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size


        /* menu main*/
        TITLES[0] = getString(R.string.help);
        TITLES[1] = getString(R.string.miserviciopen);
        TITLES[2] = getString(R.string.miubicacionescer);
        TITLES[3] = getString(R.string.micontactos);
        TITLES[4] = getString(R.string.condition);
        TITLES[5] = getString(R.string.exit);

        ICONS[0] = R.drawable.ic_help;
        ICONS[1] = R.drawable.ic_help;
        ICONS[2] = R.drawable.ic_help;
        ICONS[3] = R.drawable.ic_help;
        ICONS[4] = R.drawable.ic_help;
        ICONS[5] = R.drawable.ic_help;





        user = FirebaseAuth.getInstance().getCurrentUser();


        if(appPreferences.getUser().equals("")) {
            String[] temp = user.getEmail().split("@");
            name = temp[0];
            appPreferences.setUser(name);
        }

        appPreferences.setActualizar("1");




        databaseUsers = FirebaseDatabase.getInstance().getReference("users");
        //databaseUsers.keepSynced(true);

        Query userquery = databaseUsers
                .orderByChild("email").equalTo(user.getEmail());

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor(getString(R.string.colorAccent)));
        pDialog.setTitleText(getResources().getString(R.string.auth));
        pDialog.setCancelable(false);
        pDialog.show();


        userquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    Utemp = postSnapshot.getValue(User.class);
                    databaseUsers.removeEventListener(this);


                    if (user != null) {
                        List<String> listProvider = user.getProviders();
                        provider = listProvider.get(0);

                        if(Utemp.getUrl_imagen()!=null){

                            if(!Utemp.getUrl_imagen().equals(""))
                            {
                                imagen=Utemp.getUrl_imagen().toString();
                            }else
                            {
                                if (user.getPhotoUrl() != null) {
                                    imagen = user.getPhotoUrl().toString();
                                }
                            }

                        }else {
                            // User is signed in
                            if (user.getPhotoUrl() != null) {
                                imagen = user.getPhotoUrl().toString();
                            }
                        }



                        if (Utemp == null) {
                            try {
                                name = user.getEmail().toString();
                            } catch (Exception e) {
                                if (user.getDisplayName() != null) {
                                    name = user.getDisplayName().toString();
                                }
                            }
                        } else {
                            if (!Utemp.getName().equals("")) {
                                name = Utemp.getName() + " " + Utemp.getLastname();
                            }
                        }


                        if (imagen == null) {
                            imagen = "";
                        }
                        if (name == null) {
                            name = "";
                        }
                    }




                }
                menu();
                validaTask(user.getEmail());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });


        /*update user */
        if (appPreferences.getFlag().equals("1")) {
            if(Utemp!=null) {
                databaseUsers.child(Utemp.getId()).child("firebase_code").setValue(appPreferences.getFirebasetoken());
                appPreferences.setFlag("0");
            }
        }

        //tema
        FirebaseMessaging.getInstance().subscribeToTopic("wegoWorkForce");


        mServiciosRecyclerView = (RecyclerView) findViewById(R.id.ordenes);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, 1);

        mServiciosRecyclerView.setLayoutManager(layoutManager);
        mServiciesAdapter = new ServiciesRecycleAdapter();
        mServiciosRecyclerView.setAdapter(mServiciesAdapter);

        mListServicios = new ArrayList<Ordenes>();
        mListServiciosFilter = new ArrayList<Ordenes>();


        searchView=(SearchView) findViewById(R.id.busqueda);

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



    }

    public void menu()
    {


        mAdapter = new MenuAdapter(TITLES, ICONS, name, PROFILE, imagen, MainActivity.this);       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)

        mRecyclerView_main.setAdapter(mAdapter);                              // Setting the adapter to RecyclerView

        mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager

        mRecyclerView_main.setLayoutManager(mLayoutManager);

        Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);        // Drawer object Assigned to the view
        mDrawerToggle = new android.support.v7.app.ActionBarDrawerToggle(this, Drawer, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // code here will execute once the drawer is opened( As I dont want anything happened whe drawer is
                // open I am not going to put anything here)
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Code here will execute once drawer is closed
            }

        }; // Drawer Toggle Object Made

        Drawer.addDrawerListener(mDrawerToggle);

        mRecyclerView_main.addOnItemTouchListener(new Constants.RecyclerTouchListener(getApplicationContext(), mRecyclerView_main, new Constants.ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, final int position) {

                Intent intent;

                switch (position) {
                    case 1:

                        intent = new Intent(MainActivity.this,AyudaActivity.class);
                        startActivity(intent);

                        break;
                    case 2:
                        //intent = new Intent(MainActivity.this,MyServiciesActivity.class);
                        //startActivity(intent);

                        break;
                    case 3:

                        //intent = new Intent(MainActivity.this,LocationActivity.class);
                        //intent.putExtra("select","0");
                        //startActivity(intent);

                        break;
                    case 4:
                       // intent = new Intent(MainActivity.this,ContactActivity.class);
                        //intent.putExtra("select","0");
                        //startActivity(intent);

                        break;

                    case 5:

                        intent = new Intent(MainActivity.this,TermsActivity.class);
                        startActivity(intent);


                        break;

                    case 6:

                        pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.NORMAL_TYPE);
                        pDialog.setTitleText(getResources().getString(R.string.app_name));
                        pDialog.setContentText(getResources().getString(R.string.msg_exit));
                        pDialog.setConfirmText(getString(R.string.yes));
                        pDialog.setCancelText(getString(R.string.no));
                        pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                                signOut();
                                finish();
                            }
                        });
                        pDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.cancel();
                            }
                        });
                        pDialog.show();

                        break;


                    default:

                        break;
                }


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mDrawerToggle.onDrawerClosed(mRecyclerView_main);
                        Drawer.closeDrawers();
                    }
                }, 200);


            }
        }));
    }



    //sign out method
    public void signOut() {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
    }


    @Override
    public void onResume() {
        super.onResume();



        if(appPreferences.getActualizar().equals("1")){

            if(mAdapter!=null) {
                mAdapter.setName(appPreferences.getUser());
                mAdapter.setImagen(Utemp.getUrl_imagen());
                mAdapter.notifyItemChanged(0);
                appPreferences.setActualizar("0");
            }
        }


    }

    public void message(String phone)
    {

        if(phone.equals(""))
        {
            pDialog= new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE);
            pDialog.setTitleText(getResources().getString(R.string.app_name));
            pDialog.setContentText(getResources().getString(R.string.complete_perfil));
            pDialog.setConfirmText(getResources().getString(R.string.ok));
            pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sDialog) {
                    sDialog.dismiss();
                    Intent intent = new Intent(MainActivity.this, PerfilActivity.class);
                    startActivity(intent);

                }
            });
            pDialog.show();

        }else
        {
            MainActivity.validate_phone = 0;
        }


    }

    private void asignar(final int idOrden,final int position)
    {
        final JSONObject[] res = {null};
        //Showing the progress dialog


        Constants.deleteCache(MainActivity.this);

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

                                    pDialog= new SweetAlertDialog(MainActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                                    pDialog.setTitleText(getResources().getString(R.string.app_name));
                                    pDialog.setContentText(Constants.Decrypt(res[0].getString("message")));
                                    pDialog.setConfirmText(getResources().getString(R.string.ok));
                                    pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();
                                            mServiciesAdapter.removeItem(position);

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


                                pDialog= new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE);
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
                        Toast.makeText(MainActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();


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

        RequestQueue volleyQueue = Volley.newRequestQueue(this);
        volleyQueue.add(stringRequest);
        DiskBasedCache cache = new DiskBasedCache(getCacheDir(), 500 * 1024 * 1024);
        volleyQueue = new RequestQueue(cache, new BasicNetwork(new HurlStack()));
        volleyQueue.start();

    }


    private void validaTask(String email){

        final JSONObject[] res = {null};
        //Showing the progress dialog


        Constants.deleteCache(MainActivity.this);

        final String finalEmail = email;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SERVER+"validaUser/format/json",
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
                                    JSONObject mObj = mObjResp[0].getJSONObject(0);

                                    appPreferences.setUserId(Constants.Decrypt(mObj.getString("id_persona")));
                                    mObj = mObjResp[0].getJSONObject(1);
                                    appPreferences.setImagen(mObj.getString("imagen"));

                                    mObj = mObjResp[0].getJSONObject(2);

                                    appPreferences.setUser(Constants.Decrypt(mObj.getString("nombres")));

                                    mObj = mObjResp[0].getJSONObject(3);


                                    final String phone =  mObj.getString("telefono");
                                    pDialog.dismiss();

                                    for (int x = 4; x < mObjResp[0].length(); x++) {
                                        mObj = mObjResp[0].getJSONObject(x);


                                        final JSONObject finalMObj = mObj;
                                        final int finalX = x;
                                        final int cantidad =  mObjResp[0].length();
                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {


                                                    mListServicios.add(new Ordenes(Integer.parseInt(Constants.Decrypt(finalMObj.getString("id"))), Constants.Decrypt(finalMObj.getString("cliente")), Constants.Decrypt(finalMObj.getString("servicio")), Integer.parseInt(Constants.Decrypt(finalMObj.getString("estado"))), Constants.Decrypt(finalMObj.getString("fecha")), Constants.Decrypt(finalMObj.getString("costo")), Constants.Decrypt(finalMObj.getString("direccion")), Constants.Decrypt(finalMObj.getString("longitud")),Constants.Decrypt(finalMObj.getString("latitud"))));

                                                    } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                                if( finalX == (cantidad -1)){
                                                    message(phone);
                                                }

                                                mListServiciosFilter = mListServicios;
                                                mServiciesAdapter.notifyItemChanged(finalX);


                                            }
                                        });





                                    }








                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }else
                            {
                                pDialog.dismiss();


                                pDialog= new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE);
                                pDialog.setTitleText(getResources().getString(R.string.app_name));
                                pDialog.setContentText(Constants.Decrypt(res[0].getString("message")));
                                pDialog.setConfirmText(getResources().getString(R.string.ok));
                                pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();
                                        FirebaseAuth.getInstance().signOut();
                                        LoginManager.getInstance().logOut();
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
                        Toast.makeText(MainActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();


                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters

                try {
                    params.put("email", Constants.Encrypt(finalEmail));
                    params.put("token", appPreferences.getFirebasetoken());
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



    /* adapter*/

    public class ServiciesRecycleAdapter extends RecyclerView.Adapter<ServiciesRecycleHolder>   implements Filterable {
        private int lastPosition = -1;

        @Override
        public  ServiciesRecycleHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_servicios, viewGroup, false);
            setAnimation(v,i);
            return new  ServiciesRecycleHolder(v);
        }


        @Override
        public void onBindViewHolder(final  ServiciesRecycleHolder productHolder, final int i) {

            productHolder.mtxtNombre.setText(mListServiciosFilter.get(i).getCliente());
            productHolder.mtxtFecha.setText(mListServiciosFilter.get(i).getFecha());
            productHolder.mtxtServicio.setText(mListServiciosFilter.get(i).getServicio());


            productHolder.mContenedor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE);
                    pDialog.setTitleText(getResources().getString(R.string.app_name));
                    pDialog.setContentText(getString(R.string.asignar));
                    pDialog.setConfirmText(getResources().getString(R.string.yes));
                    pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            asignar(mListServiciosFilter.get(i).getId(),i);

                        }
                    });
                    pDialog.setCancelText(getString(R.string.no));
                    pDialog.show();




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
                    animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.zoom_back_in);
                } else {
                    animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.zoom_forward_in);
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
        public CardView mContenedor;




        public  ServiciesRecycleHolder(View itemView) {
            super(itemView);
            mtxtNombre = (TextView) itemView.findViewById(R.id.txtNombre);
            mtxtFecha = (TextView) itemView.findViewById(R.id.txtFecha);
            mtxtServicio = (TextView) itemView.findViewById(R.id.txtServicio);
            mContenedor = (CardView) itemView.findViewById(R.id.contenedor);

        }
    }




}
