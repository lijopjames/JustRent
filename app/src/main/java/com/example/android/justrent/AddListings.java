package com.example.android.justrent;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddListings extends AppCompatActivity implements View.OnClickListener {

    EditText producttype,pbrand,rentamount,location,desc,imagename;
    Button uploadimage,updatebutton;
    String namea,emaila;
    Spinner status;
    String[] stat = new String[] {"Active", "Inactive"};
    ImageView uploadedimage;
    private final int IMG_REQUEST = 1;
    private Bitmap bitmap;
    Uri path;
    AlertDialog.Builder builder;
    final String TAG = this.getClass().getSimpleName();
    Ip i=new Ip();
    String ip=i.getIp();
    private String UploadUrl = "http://"+ip+"/rentalapplication/postproduct.php";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_listings);

        producttype = (EditText)findViewById(R.id.producttype);
        pbrand = (EditText)findViewById(R.id.pbrand);
        rentamount = (EditText)findViewById(R.id.rentamount);
        location = (EditText)findViewById(R.id.location);
        desc = (EditText)findViewById(R.id.desc);
        imagename = (EditText)findViewById(R.id.imagename);
        status = (Spinner)findViewById(R.id.status);


        uploadimage = (Button)findViewById(R.id.uploadimage);
        updatebutton = (Button)findViewById(R.id.updatebutton);

        uploadedimage = (ImageView)findViewById(R.id.uploadedimage);


        SharedPreferences prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
        namea = prefs.getString("name", "UNKNOWN");
        emaila =prefs.getString("email","UNKNOWN");


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddListings.this,
                android.R.layout.simple_spinner_item,stat);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        status.setAdapter(adapter);


        uploadimage.setOnClickListener(this);
        updatebutton.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.uploadimage:
                selectImage();
                break;

            case R.id.updatebutton:
                uploadImage();
                break;
        }

    }


    private void selectImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMG_REQUEST);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==IMG_REQUEST && resultCode == RESULT_OK && data!=null){
            path = data.getData();
            //getRealPathFromURI(path);
            //Toast.makeText(HomeActivity.this, getRealPathFromURI(path),Toast.LENGTH_LONG).show();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),path);
                uploadedimage.setImageBitmap(bitmap);
                uploadedimage.setVisibility(View.VISIBLE);
                imagename.setVisibility(View.VISIBLE);



            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }




    private void uploadImage(){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, UploadUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG,response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    final String stringresponse = jsonObject.getString("response");

                    builder = new AlertDialog.Builder(AddListings.this);
                    builder.setTitle("Message");
                    builder.setMessage(stringresponse);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            Intent intent = new Intent(AddListings.this,HomeActivity.class);
                            startActivity(intent);


                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("image",imageToString(bitmap));
                params.put("name",imagename.getText().toString().trim());
                params.put("producttype",producttype.getText().toString().trim());
                params.put("pbrand",pbrand.getText().toString().trim());
                params.put("rentamount",rentamount.getText().toString().trim());
                params.put("location",location.getText().toString().trim());
                params.put("desc",desc.getText().toString().trim());
                params.put("username",namea);
                params.put("useremail",emaila);
                params.put("status",status.getSelectedItem().toString());
                return params;
            }
        };

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);


    }

    private String imageToString(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] imgbyte = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgbyte,Base64.DEFAULT);

    }














}
