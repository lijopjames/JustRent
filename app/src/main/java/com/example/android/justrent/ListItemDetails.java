package com.example.android.justrent;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ListItemDetails extends AppCompatActivity implements Response.Listener<String> {

    TextView producttype,pbrand,ownername,rentdetails,city,description;
    Button contactowner;
    ImageView image;

    String listid,useremail;
    final String TAG = this.getClass().getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_item_details);

        producttype = (TextView)findViewById(R.id.datefrom);
        pbrand = (TextView)findViewById(R.id.dateuntil);
        ownername = (TextView)findViewById(R.id.malname);
        rentdetails = (TextView)findViewById(R.id.rentdata);
        city = (TextView)findViewById(R.id.locationdataa);
        description = (TextView)findViewById(R.id.desc);

        contactowner =(Button)findViewById(R.id.button);
        image = (ImageView)findViewById(R.id.himage);

        listid = getIntent().getStringExtra("productid");
        Ip i=new Ip();
        String ip=i.getIp();

        String url = "http://"+ip+"/rentalapplication/listingsdetails.php";
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, url,this, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"error while reading",Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("productid",listid);
                return params;
            }


        };

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);






    }

    @Override
    public void onResponse(String response) {

        Log.d(TAG,response);

        JSONArray array = null;
        try {
            array = new JSONArray(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < array.length(); i++) {
            JSONObject row = null;
            try {
                row = array.getJSONObject(i);
                String ptype = row.getString("producttype");
                String pbrandname = row.getString("productbrand");
                String owner = row.getString("username");
                String rentamount = row.getString("productrent");
                String location = row.getString("location");
                String imageurl = row.getString("producturl");
                useremail = row.getString("useremail");
                String descp = row.getString("productdescn");

                producttype.setText(ptype);
                pbrand.setText(pbrandname);
                ownername.setText(owner);
                rentdetails.setText(rentamount);
                city.setText(location);
                description.setText(descp);
                Ip isecond =new Ip();
                String ip=isecond.getIp();
                Picasso.with(getApplicationContext()).load("http://"+ip+"/rentalapplication/"+imageurl).into(image);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        contactowner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL,new String[]{ useremail});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Requesting Further Details about the Product");
                intent.putExtra(Intent.EXTRA_TEXT,"Hi, I am Intrested in the listed product and I would like to know more Information.");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);

                }

            }
        });

    }
}
