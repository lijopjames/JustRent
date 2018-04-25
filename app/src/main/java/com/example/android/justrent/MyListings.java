package com.example.android.justrent;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amigold.fundapter.BindDictionary;
import com.amigold.fundapter.FunDapter;
import com.amigold.fundapter.extractors.StringExtractor;
import com.amigold.fundapter.interfaces.DynamicImageLoader;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kosalgeek.android.json.JsonConverter;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.android.justrent.R.id.email;

public class MyListings extends AppCompatActivity implements Response.Listener<String> {

    final String TAG = this.getClass().getSimpleName();
    ListView listView;
    String namea;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_listings);
        SharedPreferences prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
        namea = prefs.getString("name", "UNKNOWN");

        Ip i=new Ip();
        String ip=i.getIp();
        String url = "http://"+ip+"/rentalapplication/myrentlist.php";
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, url,this, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"error while reading",Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("name",namea);
                return params;
            }


        };

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
        listView = (ListView)findViewById(R.id.lst1);
    }

    @Override
    public void onResponse(String response) {

        Log.d(TAG,response);

        if (response.equals("no_data")){

            Toast.makeText(getApplicationContext(),"No LISTINGS to show",Toast.LENGTH_SHORT).show();



        }else {

            ArrayList<ListJava> jsonObject = new JsonConverter<ListJava>().toArrayList(response,ListJava.class);
            BindDictionary<ListJava> dictionary = new BindDictionary<>();
            dictionary.addStringField(R.id.producttype, new StringExtractor<ListJava>() {
                @Override
                public String getStringValue(ListJava listss, int position) {
                    return listss.producttype;
                }
            });

            dictionary.addStringField(R.id.productbrand, new StringExtractor<ListJava>() {
                @Override
                public String getStringValue(ListJava listss, int position) {
                    return listss.productbrand;
                }
            });


            dictionary.addStringField(R.id.productid, new StringExtractor<ListJava>() {
                @Override
                public String getStringValue(ListJava listss, int position) {
                    return listss.listingid;
                }
            });


            dictionary.addStringField(R.id.locationname, new StringExtractor<ListJava>() {
                @Override
                public String getStringValue(ListJava listss, int position) {
                    return listss.location;
                }
            });


            dictionary.addStringField(R.id.productrate, new StringExtractor<ListJava>() {
                @Override
                public String getStringValue(ListJava listss, int position) {
                    return listss.productrent;
                }
            });

            dictionary.addStringField(R.id.ownername, new StringExtractor<ListJava>() {
                @Override
                public String getStringValue(ListJava listss, int position) {
                    return listss.username;
                }
            });



            dictionary.addDynamicImageField(R.id.productimage, new StringExtractor<ListJava>() {
                @Override
                public String getStringValue(ListJava listss, int position) {
                    return listss.producturl;
                }
            }, new DynamicImageLoader() {
                @Override
                public void loadImage(String url, ImageView view) {

                    Ip i=new Ip();
                    String ip=i.getIp();
                    Picasso.with(getApplicationContext()).load("http://"+ip+"/rentalapplication/"+ url).into(view);

                }
            });



            FunDapter<ListJava> funDapter = new FunDapter<>(getApplicationContext(),jsonObject,R.layout.customlayout1,dictionary);
            listView.setAdapter(funDapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    final String selected =  ((TextView)view.findViewById(R.id.productid)).getText().toString();
                    builder = new AlertDialog.Builder(MyListings.this);
                    builder.setMessage("Are you sure to DELETE?");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            Ip ii=new Ip();
                            String ip=ii.getIp();

                            String url = "http://"+ip+"/rentalapplication/deletelist.php";
                            final StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.d(TAG,response);
                                    try {

                                        JSONObject jsonObject = new JSONObject(response);
                                        JSONArray jsonArray = jsonObject.getJSONArray("server_response");
                                        JSONObject JO = jsonArray.getJSONObject(0);
                                        String code = JO.getString("code");
                                        String message = JO.getString("message");
                                        if (code.equals("delete_true")){

                                            Toast.makeText(MyListings.this,message,Toast.LENGTH_SHORT).show();



                                        }else if (code.equals("delete_false")){

                                            Toast.makeText(MyListings.this,message,Toast.LENGTH_SHORT).show();

                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    if (error instanceof NoConnectionError) {
                                        Toast.makeText(MyListings.this,"no connection",Toast.LENGTH_LONG).show();
                                    } else if (error instanceof AuthFailureError) {
                                        //TODO
                                    } else if (error instanceof ServerError) {
                                        //TODO
                                    } else if (error instanceof NetworkError) {
                                        //TODO
                                    } else if (error instanceof ParseError) {
                                        //TODO
                                    }
                                    //Toast.makeText(getApplicationContext(),"error while reading",Toast.LENGTH_SHORT).show();
                                }
                            }){
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String,String> params = new HashMap<>();
                                    params.put("productid",selected);
                                    Log.d(TAG,selected);
                                    return params;
                                }


                            };

                            MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);


                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                    //Toast.makeText(getApplicationContext(),selected,Toast.LENGTH_SHORT).show();

                }
            });

        }


        //Toast.makeText(BusList.this,response,Toast.LENGTH_SHORT).show();


    }
}
