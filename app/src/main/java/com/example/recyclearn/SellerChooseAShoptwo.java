package com.example.recyclearn;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class SellerChooseAShoptwo extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public DrawerLayout drawerLayout;
    public NavigationView navigationView;
    public TextView navHeadername;

    public CircleImageView nav_profseller;

    private FirebaseUser user;
    FirebaseFirestore fStore;
    private String userID;
    private FirebaseAuth fAuth;
    private StorageReference storageReference;
    private Button btn_back;

    private CircleImageView Seller_Profile;
    private ImageView btn_drawer;

    ConstraintLayout contentView;

    //Set animation when drawer is being dragged
    static final float END_SCALE = 0.8f;

    public TextView shopname, kgresult05, kgresult1, kgresult2, kgresult25, kgresult3, kgresult35, kgresult4, kgresult45, kgresult5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_choose_ashoptwo);

        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back = new Intent(SellerChooseAShoptwo.this, SellerChooseAShop.class);
                startActivity(back);
            }
        });

//drawer
        drawerLayout = findViewById(R.id.seller_drawer_layout2);
        navigationView = findViewById(R.id.nav_view2);
        btn_drawer = findViewById(R.id.btn_drawer);
        navigationView.setItemIconTintList(null);

        contentView = findViewById(R.id.redeemContent);
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

                //buyer display name
                Intent intent = getIntent();
                String shopid2 = intent.getStringExtra("BUYERTWO");
                final TextView UsernameShop = findViewById(R.id.shopname);

                DocumentReference documentReference2 = fStore.collection("Buyer_Users").document(shopid2);
                documentReference2.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        String shopname = value.getString("username"); //get buyer name
                        UsernameShop.setText(shopname);

                        final TextView kgresult05 = findViewById(R.id.kgresult05);
                        final TextView kgresult1 = findViewById(R.id.kgresult1);
                        final TextView kgresult15 = findViewById(R.id.kgresult15);
                        final TextView kgresult2 = findViewById(R.id.kgresult2);
                        final TextView kgresult25 = findViewById(R.id.kgresult2_5);
                        final TextView kgresult3 = findViewById(R.id.kgresult3);
                        final TextView kgresult35 = findViewById(R.id.kgresult3_5);
                        final TextView kgresult4 = findViewById(R.id.kgresult4);
                        final TextView kgresult45 = findViewById(R.id.kgresult4_5);
                        final TextView kgresult5 = findViewById(R.id.kgresult5);
                        final TextView kgresult55 = findViewById(R.id.kgresult5_5);
                        final TextView kgresult6 = findViewById(R.id.kgresult6);
                        final TextView kgresult65 = findViewById(R.id.kgresult6_5);
                        final TextView kgresult7 = findViewById(R.id.kgresult7);
                        final TextView kgresult75 = findViewById(R.id.kgresult7_5);
                        final TextView kgresult8 = findViewById(R.id.kgresult8);
                        final TextView kgresult85 = findViewById(R.id.kgresult8_5);
                        final TextView kgresult9 = findViewById(R.id.kgresult9);
                        final TextView kgresult95 = findViewById(R.id.kgresult9_5);
                        final TextView kgresult10 = findViewById(R.id.kgresult10);


                        DocumentReference documentReference3 = fStore.collection("Buyer_Users").document(shopid2);
                        documentReference3.collection("Points_Per_KG").document("PointsKG").addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                String zerofive = value.getString("0_5 Kilogram");
                                kgresult05.setText(zerofive);
                                String one = value.getString("1 Kilogram");
                                kgresult1.setText(one);
                                String onefive = value.getString("1_5 Kilogram");
                                kgresult15.setText(onefive);
                                String two = value.getString("2 Kilogram");
                                kgresult2.setText(two);
                                String twofive = value.getString("2_5 Kilogram");
                                kgresult25.setText(twofive);
                                String three = value.getString("3 Kilogram");
                                kgresult3.setText(three);
                                String threefive = value.getString("3_5 Kilogram");
                                kgresult35.setText(threefive);
                                String four = value.getString("4 Kilogram");
                                kgresult4.setText(four);
                                String fourfive = value.getString("4_5 Kilogram");
                                kgresult45.setText(fourfive);
                                String five = value.getString("5 Kilogram");
                                kgresult5.setText(five);
                                String fivefive = value.getString("5_5 Kilogram");
                                kgresult55.setText(fivefive);
                                String six = value.getString("6 Kilogram");
                                kgresult6.setText(six);
                                String sixfive = value.getString("6_5 Kilogram");
                                kgresult65.setText(sixfive);
                                String seven = value.getString("7 Kilogram");
                                kgresult7.setText(seven);
                                String sevenfive = value.getString("7_5 Kilogram");
                                kgresult75.setText(sevenfive);
                                String eight = value.getString("8 Kilogram");
                                kgresult8.setText(eight);
                                String eightfive = value.getString("8_5 Kilogram");
                                kgresult85.setText(eightfive);
                                String nine = value.getString("9 Kilogram");
                                kgresult9.setText(nine);
                                String ninefive = value.getString("9_5 Kilogram");
                                kgresult95.setText(ninefive);
                                String ten = value.getString("10 Kilogram");
                                kgresult10.setText(ten);
                            }
                        });
                    }
                });
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

    }




    private void navigationSellerDrawer() {
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
// navigation menu items declaration
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_sellerinfo);
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
                final float diffScaleoffset = slideOffset * (1 - END_SCALE);
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
                Intent intent = new Intent(SellerChooseAShoptwo.this, SellerDashboard.class);
                startActivity(intent);
                break;
            case R.id.nav_sellerprofile:
                Intent intent1 = new Intent(SellerChooseAShoptwo.this, SellerProfile.class);
                startActivity(intent1);
                break;
            case R.id.nav_sellerredeem:
                Intent redeem = new Intent(SellerChooseAShoptwo.this, SellerRedeem.class);
                startActivity(redeem);
                break;
            case R.id.nav_sellerlocation:
                Intent intent2 = new Intent(SellerChooseAShoptwo.this, SellerLocation.class);
                startActivity(intent2);
                break;
            case R.id.nav_sellerSecurity:
                Intent security = new Intent(SellerChooseAShoptwo.this, Seller_Security.class);
                startActivity(security);
                break;
            case R.id.nav_sellerinfo:
                Intent intent3 = new Intent(SellerChooseAShoptwo.this, SellerAboutUs.class);
                startActivity(intent3);
                break;
            case R.id.nav_sellerlogout:
                // YUNG CODE PARA DI NA BUMALIK ULIT SA ACTIVITY AFTER NAG LOG OUT YUNG USER-------------
                Intent intent4 = new Intent(getBaseContext(), MainActivity.class);
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
        Intent intent = new Intent(SellerChooseAShoptwo.this, SellerProfile.class);
        startActivity(intent);
    }
}