package com.example.recyclearn;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SellerTradeGenQR extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public DrawerLayout drawerLayout;
    public NavigationView navigationView;
    public TextView navHeadername, btn_location;

    public CircleImageView nav_profseller;

    private FirebaseUser user;
    FirebaseFirestore fStore;
    private String userID;
    private FirebaseAuth fAuth;
    private StorageReference storageReference;


    private CircleImageView Seller_Profile;
    private ImageView btn_drawer;

    public Button btn_genqr, viewPointsPerKG;
    public static EditText kgInput1;
    public String name;

    boolean isAllFieldsChecked = false;
    boolean samp = false;

    ConstraintLayout contentView;

    //Set animation when drawer is being dragged
    static final float END_SCALE = 0.8f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_trade_gen_qr);
//drawer
        drawerLayout = findViewById(R.id.seller_drawer_layout);
        navigationView = findViewById(R.id.nav_view2);
        btn_drawer = findViewById(R.id.btn_drawer);
        navigationView.setItemIconTintList(null);

        contentView = findViewById(R.id.qrContent);
        navigationSellerDrawer();

        user = FirebaseAuth.getInstance().getCurrentUser();
        fStore = FirebaseFirestore.getInstance();
        userID = user.getUid();


// display onclick on sellerprofile
        Seller_Profile = (CircleImageView) findViewById(R.id.Seller_Profile1);
        Seller_Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                opensellerprofile();
            }
        });

//Display user info in drawer
        DocumentReference documentReference = fStore.collection("Seller_Users").document(userID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                String username = value.getString("username");
                navHeadername = navigationView.getHeaderView(0).findViewById(R.id.nav_usernameseller);
                navHeadername.setText(username);

            }
        });

// display profile picture in navigation drawer
        nav_profseller = navigationView.getHeaderView(0).findViewById(R.id.nav_sellerprofile2);
        storageReference = FirebaseStorage.getInstance().getReference().child("user_seller/" + fAuth.getInstance().getCurrentUser().getUid() + "/profile.jpg");
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(nav_profseller);
            }
        });
//display profile picture

        fAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        Seller_Profile = (CircleImageView) findViewById(R.id.Seller_Profile1);

        StorageReference profileRef1 = storageReference.child("user_seller/" + fAuth.getCurrentUser().getUid() + "/profile.jpg");
        profileRef1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(Seller_Profile);
            }
        });

        // go back to location
        btn_location = findViewById(R.id.btn_location);
        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backLocation();
            }
        });

        //view PointsPerKG ACTIVITY

//QR input
        kgInput1 = findViewById(R.id.kgInput);

        btn_genqr = findViewById(R.id.btn_genqr);
        btn_genqr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permision();
                String kg = kgInput1.getText().toString();

                // Apply changes on kg of seller
                Map<String, Object> user = new HashMap<>();
                user.put("sellerKgInput", kg);
                DocumentReference documentReference = fStore.collection("Seller_Users").document(userID);
                documentReference.update(user);
            }
        });
    }
    //Qr input with validation
    public boolean goToGen() {

        String input0 = kgInput1.getText().toString().trim();

        if (input0.isEmpty()) {
            kgInput1.setError("Input is required");
            kgInput1.requestFocus();
            return false;
        }
        if (Double.parseDouble(kgInput1.getText().toString().trim()) > 10) {
            kgInput1.setError("Maximum of 10` KG");
            kgInput1.requestFocus();
            return false;
        }
        if (Double.parseDouble(kgInput1.getText().toString().trim()) < 0.5) {
            kgInput1.setError("Mininum of 0.5 KG");
            kgInput1.requestFocus();
            return false;
        }
        if (Double.parseDouble(kgInput1.getText().toString().trim()) == 0) {
            kgInput1.setError("Mininum of 0.5 KG");
            kgInput1.requestFocus();
            return false;
        }
        return true;
    }

    public void permision() {
        isAllFieldsChecked = goToGen();
        if (isAllFieldsChecked) {
            Intent sender = new Intent(SellerTradeGenQR.this, SellertradeGenerate.class);
            sender.putExtra("KEY_SENDER", kgInput1.getText().toString());
            startActivity(sender);
        }
    }

    private void navigationSellerDrawer() {
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

// navigation menu items declaration
        navigationView.setNavigationItemSelectedListener(this);
        btn_drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        animateNavigationDrawer();
    }

    //Code para dun sa animation ng Drawer
    private void animateNavigationDrawer() {
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

                //Scale the view on current slide offset
                final float diffScaleoffset =  slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaleoffset;
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);

                //Translate the View, accounting for the scaled wdith
                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = contentView.getWidth() * diffScaleoffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                contentView.setTranslationX(xTranslation);

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    // back button on drawer navigation
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    //drawer link to other activity
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuitem) {

        switch (menuitem.getItemId()) {
            case R.id.nav_sellerhome:
                Intent intent = new Intent(SellerTradeGenQR.this, SellerDashboard.class);
                startActivity(intent);
                break;
            case R.id.nav_sellerprofile:
                Intent intent1 = new Intent(SellerTradeGenQR.this, SellerProfile.class);
                startActivity(intent1);
                break;
            case R.id.nav_sellerredeem:
                Intent redeem = new Intent(SellerTradeGenQR.this, SellerRedeem.class);
                startActivity(redeem);
                break;
            case R.id.nav_sellerlocation:
                Intent intent2 = new Intent(SellerTradeGenQR.this, SellerLocation.class);
                startActivity(intent2);
                break;
            case R.id.nav_sellerSecurity:
                Intent security = new Intent(SellerTradeGenQR.this, Seller_Security.class);
                startActivity(security);
                break;
            case R.id.nav_sellerinfo:
                Intent intent3 = new Intent(SellerTradeGenQR.this, SellerAboutUs.class);
                startActivity(intent3);
                break;
            case R.id.nav_sellerlogout:
                // YUNG CODE PARA DI NA BUMALIK ULIT SA ACTIVITY AFTER NAG LOG OUT YUNG USER-------------
                Intent intent4 = new Intent(getBaseContext(),MainActivity.class);
                intent4.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // YUNG CODE PARA DI NA BUMALIK ULIT SA ACTIVITY AFTER NAG LOG OUT YUNG USER-------------
                startActivity(intent4);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // Open seller profile
    public void opensellerprofile() {
        Intent intent = new Intent(SellerTradeGenQR.this, SellerProfile.class);
        startActivity(intent);
    }

    public void backLocation() {
        Intent backLocation = new Intent(SellerTradeGenQR.this, SellerLocation.class);
        startActivity(backLocation);
    }



    }




