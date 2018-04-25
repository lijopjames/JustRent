package com.example.android.justrent;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FeedBack extends AppCompatActivity {

    String namea,emaila;
    TextView name,email;
    EditText feedback;
    Button done;
    AlertDialog.Builder builder;
    final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        SharedPreferences prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
        namea = prefs.getString("name", "UNKNOWN");
        emaila =prefs.getString("email","UNKNOWN");
        name = (TextView)findViewById(R.id.datefrom);
        email = (TextView)findViewById(R.id.dateuntil);
        feedback = (EditText)findViewById(R.id.feedback);
        done = (Button)findViewById(R.id.button);
        name.setText(namea);
        email.setText(emaila);


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Ip i=new Ip();
                String ip=i.getIp();
                String url = "http://"+ip+"/rentalapplication/feedback.php";
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
                            if (code.equals("update_true")){

                                builder = new AlertDialog.Builder(FeedBack.this);
                                builder.setTitle("Message");
                                builder.setMessage(message);
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        Intent intent = new Intent(FeedBack.this,HomeActivity.class);
                                        startActivity(intent);

                                    }
                                });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();

                            }else if (code.equals("update_false")){

                                Toast.makeText(FeedBack.this,message,Toast.LENGTH_SHORT).show();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof NoConnectionError) {
                            Toast.makeText(FeedBack.this,"no connection",Toast.LENGTH_LONG).show();
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
                        params.put("name",namea);
                        params.put("email",emaila);
                        params.put("feedback",feedback.getText().toString());
                        Log.d(TAG,namea);
                        return params;
                    }


                };

                MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);





            }
        });





    }
}
