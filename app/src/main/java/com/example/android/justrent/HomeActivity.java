package com.example.android.justrent;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class HomeActivity extends AppCompatActivity {

    LinearLayout listings,addlistings,profile,feedback;
    EditText searchfield;
    Button searchbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        listings = (LinearLayout)findViewById(R.id.listings);
        addlistings = (LinearLayout)findViewById(R.id.addlistings);
        profile = (LinearLayout)findViewById(R.id.profile);
        feedback = (LinearLayout)findViewById(R.id.feedback);
        searchfield = (EditText)findViewById(R.id.edittext_layout);
        searchbutton = (Button)findViewById(R.id.searchbutton);


        listings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this,Listings.class);
                startActivity(intent);
            }
        });

        addlistings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this,AddListings.class);
                startActivity(intent);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this,Profile.class);
                startActivity(intent);
            }
        });


        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this,FeedBack.class);
                startActivity(intent);
            }
        });


        searchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this,SearchResults.class);
                intent.putExtra("cityname",searchfield.getText().toString());
                startActivity(intent);
            }
        });








    }
}
