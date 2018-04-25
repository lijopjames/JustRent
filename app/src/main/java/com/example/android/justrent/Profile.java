package com.example.android.justrent;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class Profile extends AppCompatActivity {

    TextView profilename,profileemail,profilenumber;
    RelativeLayout mylistings,logout;
    ImageView profilepic;
    String namea,emaila,phoneno,profilephoto;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profilename =(TextView)findViewById(R.id.profilename);
        profileemail =(TextView)findViewById(R.id.profileemail);
        profilenumber = (TextView)findViewById(R.id.profilenumber);
        mylistings =(RelativeLayout)findViewById(R.id.mylistings);

        profilepic = (ImageView)findViewById(R.id.profilepic);

        logout =(RelativeLayout) findViewById(R.id.profilelogout);

        SharedPreferences prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
        namea = prefs.getString("name", "UNKNOWN");
        emaila =prefs.getString("email","UNKNOWN");
        phoneno = prefs.getString("phone","UNKNOWN");
        profilephoto = prefs.getString("profilepic","UNKNOWN");

        profilename.setText(namea);
        profileemail.setText(emaila);
        profilenumber.setText(phoneno);

        Ip address=new Ip();
        String ip=address.getIp();

        Picasso.with(getApplicationContext()).load("http://"+ip+"/rentalapplication/"+profilephoto).into(profilepic);


        mylistings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Profile.this,MyListings.class);
                startActivity(intent);
            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                builder = new AlertDialog.Builder(Profile.this);
                builder.setMessage("Are you sure to Logout?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Intent intent = new Intent(Profile.this,LoginActivity.class);
                        startActivity(intent);

                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });









    }
}
