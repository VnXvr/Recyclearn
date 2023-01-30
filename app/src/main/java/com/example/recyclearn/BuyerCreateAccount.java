package com.example.recyclearn;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class BuyerCreateAccount extends AppCompatActivity {

    Button create;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_create_account);

        TextView link = (TextView) findViewById(R.id.link);
        link.setMovementMethod(LinkMovementMethod.getInstance());

        create = findViewById(R.id.btn_back);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(BuyerCreateAccount.this, BuyerLogIn.class);
                startActivity(back);
            }
        });


    }
}