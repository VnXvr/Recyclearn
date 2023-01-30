package com.example.recyclearn;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AdminDashboard extends AppCompatActivity {

    public Button btn_Create,btn_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        btn_Create = findViewById(R.id.btn_Create);
        btn_Create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent create = new Intent(AdminDashboard.this,BuyerRegister.class);
                startActivity(create);
            }
        });
        btn_logout = findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent out = new Intent(AdminDashboard.this, BuyerLogIn.class);
                startActivity(out);
                finish();
            }
        });
    }
}