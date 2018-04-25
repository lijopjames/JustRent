package com.example.android.justrent;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    EditText name,email,cellno,password,conformpassword,imagename;
    Button register,login,profilepic;
    ImageView uploadedimage;
    private final int IMG_REQUEST = 1;
    private Bitmap bitmap;
    Uri path;
    AlertDialog.Builder builder;
    final String TAG = this.getClass().getSimpleName();
    Ip i=new Ip();
    String ip=i.getIp();
    private String UploadUrl = "http://"+ip+"/rentalapplication/registerwithimage.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = (EditText)findViewById(R.id.name);
        email = (EditText)findViewById(R.id.email);
        cellno = (EditText)findViewById(R.id.cellno);
        password = (EditText)findViewById(R.id.password);
        conformpassword = (EditText)findViewById(R.id.conform_password);
        imagename = (EditText)findViewById(R.id.imagename);

        register = (Button)findViewById(R.id.register);
        login = (Button)findViewById(R.id.login);
        profilepic = (Button)findViewById(R.id.profilepic);

        uploadedimage = (ImageView)findViewById(R.id.uploadedimage);

        profilepic.setOnClickListener(this);
        register.setOnClickListener(this);




        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });



    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.profilepic:
                selectImage();
                break;

            case R.id.register:
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

                    builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setTitle("Message");
                    builder.setMessage(stringresponse);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
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
                params.put("registername",name.getText().toString());
                params.put("email",email.getText().toString());
                params.put("password",password.getText().toString());
                params.put("cellno",cellno.getText().toString());
                Log.d(TAG,name.getText().toString());
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
