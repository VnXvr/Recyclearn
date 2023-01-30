package com.example.recyclearn;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class BuyerLogIn extends AppCompatActivity implements View.OnClickListener {

    private TextView buyercreate;
    private Button login;
    private EditText Buyer_InputEmail, Buyer_InputPassword;
    private TextView btn_notbuyer, btn_forgotps;

    FirebaseUser user;
    FirebaseFirestore fStore;
    String userID;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    DocumentReference documentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_log_in);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        login = findViewById(R.id.btn_loginb);
        login.setOnClickListener(this);

        btn_notbuyer = (TextView) findViewById(R.id.btn_notabuyer);
        btn_notbuyer.setOnClickListener(this);

        btn_forgotps = (TextView) findViewById(R.id.btn_forgotpsb);
        btn_forgotps.setOnClickListener(this);


        buyercreate = (TextView) findViewById(R.id.buyercreate);
        buyercreate.setOnClickListener(this);

        Buyer_InputEmail = (EditText) findViewById(R.id.Buyer_InputEmail);
        Buyer_InputPassword = (EditText) findViewById(R.id.Buyer_InputPassword);
        progressBar = (ProgressBar) findViewById(R.id.progresBar);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buyercreate:
                startActivity(new Intent(this, BuyerCreateAccount.class));
                break;

            case R.id.btn_notabuyer:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.btn_forgotpsb:
                startActivity(new Intent(this, BuyerForgotPassword.class));

            case R.id.btn_loginb:
                userBuyerLogin();
                break;
        }
    }

    //drawer backbutton
    @Override
    public void onBackPressed() {
            Intent alis = new Intent(getBaseContext(),MainActivity.class);
            alis.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // YUNG CODE PARA DI NA BUMALIK ULIT SA ACTIVITY AFTER NAG LOG OUT YUNG USER-------------
            startActivity(alis);


    }
    private void userBuyerLogin() {
        String email = Buyer_InputEmail.getText().toString().trim();
        String password = Buyer_InputPassword.getText().toString().trim();

        if(email.isEmpty()){
            Buyer_InputEmail.setError("Email is required!");
            Buyer_InputEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Buyer_InputEmail.setError("Please enter a valid email!");
            Buyer_InputEmail.requestFocus();
            return;
        }
        if(password.isEmpty()){
            Buyer_InputPassword.setError("Password is required!");
            Buyer_InputPassword.requestFocus();
            return;
        }
        if(password.length()<6){
            Buyer_InputPassword.setError("Min password length is 6 characters!");
            Buyer_InputPassword.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    FirebaseUser User_Seller = mAuth.getInstance().getCurrentUser();
                    userID = mAuth.getCurrentUser().getUid();
                    user = mAuth.getInstance().getCurrentUser();

                    if (User_Seller.isEmailVerified()) {
                        documentReference = fStore.collection("Buyer_Users").document(userID);
                        documentReference.addSnapshotListener(BuyerLogIn.this, new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                                //Will Redirect to Admindashboard based on the specific email coded below
                                if(user.isEmailVerified() && user.getEmail().equals("monz12272@gmail.com")){
                                    Intent admind = new Intent(BuyerLogIn.this, AdminDashboard.class);
                                    startActivity(admind);
                                }
                                else if(!user.isEmailVerified()){
                                    user.sendEmailVerification();
                                    Toast.makeText(BuyerLogIn.this, "Check your email to verify your account", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    //redirect to Buyer Dashboard profile
                                    Intent intent = new Intent(BuyerLogIn.this, BuyerDashboard.class);
                                    startActivity(intent);
                                }
                            }
                        });
                    } else {
                        User_Seller.sendEmailVerification();
                        Toast.makeText(BuyerLogIn.this, "Check your email to verify your account!", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }

                }else {
                    Toast.makeText(BuyerLogIn.this, "Failed to login! PLease check your credentials", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }


            }
        });
    }
}