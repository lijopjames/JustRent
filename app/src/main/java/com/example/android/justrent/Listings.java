package com.example.android.justrent;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amigold.fundapter.BindDictionary;
import com.amigold.fundapter.FunDapter;
import com.amigold.fundapter.extractors.StringExtractor;
import com.amigold.fundapter.interfaces.DynamicImageLoader;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kosalgeek.android.json.JsonConverter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Listings extends AppCompatActivity implements Response.Listener<String> {

    String start = "1";
    final String TAG = this.getClass().getSimpleName();
    ListView listView;
    EditText searchfield;
    Button searchbutton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listings);
        Ip i=new Ip();
        String ip=i.getIp();
        String url = "http://"+ip+"/rentalapplication/rentlist.php";
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, url,this, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"error while reading",Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("start",start);
                return params;
            }


        };

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
        listView = (ListView)findViewById(R.id.lst1);
        searchfield = (EditText)findViewById(R.id.search);
        searchbutton = (Button)findViewById(R.id.searchbutton);
        searchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Listings.this,ProductSearch.class);
                intent.putExtra("productname",searchfield.getText().toString());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResponse(String response) {



        Log.d(TAG,response);

        if (response.equals("no_data")){

            Toast.makeText(getApplicationContext(),"No data to show",Toast.LENGTH_SHORT).show();



        }else {

            //Toast.makeText(BusList.this,response,Toast.LENGTH_SHORT).show();
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

            dictionary.addStringField(R.id.stat, new StringExtractor<ListJava>() {
                @Override
                public String getStringValue(ListJava listss, int position) {
                    return listss.status;
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

                    String selected =  ((TextView)view.findViewById(R.id.productid)).getText().toString();
                    Intent intent = new Intent(Listings.this,ListItemDetails.class);
                    intent.putExtra("productid",selected);
                    startActivity(intent);

                }
            });

        }




    }
}
