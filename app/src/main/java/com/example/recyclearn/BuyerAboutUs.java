package com.example.recyclearn;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BuyerAboutUs extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    public DrawerLayout drawerLayout;
    public NavigationView navigationView;
    public TextView navHeadername;

    public CircleImageView nav_profbuyer;

    private FirebaseUser user;
    FirebaseFirestore fStore;
    private String userID;
    private FirebaseAuth fAuth;
    private StorageReference storageReference;

    private CircleImageView Buyer_Profile;

    private ImageView btn_drawer;

    ConstraintLayout contentView;

    //Set animation when drawer is being dragged
    static final float END_SCALE = 0.8f;

    RecyclerView recyclerView;
    List<Versions> versionsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_about_us);

        recyclerView = findViewById(R.id.recycleView);

        initData();
        setRecyclerView();


        //drawer
        drawerLayout = findViewById(R.id.buyer_drawer_layout2);
        navigationView = findViewById(R.id.nav_view2);
        btn_drawer = findViewById(R.id.btn_drawer);
        navigationView.setItemIconTintList(null);

        contentView = findViewById(R.id.bredeemContent);
        navigationBuyerDrawer();

        user = FirebaseAuth.getInstance().getCurrentUser();
        fStore = FirebaseFirestore.getInstance();
        userID = user.getUid();

// display onclick on sellerprofile
        Buyer_Profile = (CircleImageView) findViewById(R.id.Buyer_Profile1);
        Buyer_Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openbuyerprofile();
            }
        });

//Display user info in drawer
        DocumentReference documentReference = fStore.collection("Buyer_Users").document(userID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                String username = value.getString("usersname");
                navHeadername = navigationView.getHeaderView(0).findViewById(R.id.nav_usernamebuyer);
                navHeadername.setText(username);
            }
        });

// display profile picture in navigation drawer
        nav_profbuyer = navigationView.getHeaderView(0).findViewById(R.id.nav_buyerprofile2);
        storageReference = FirebaseStorage.getInstance().getReference().child("user_buyer/" + fAuth.getInstance().getCurrentUser().getUid() + "/profile.jpg");
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(nav_profbuyer);
            }
        });
//display profile picture

        fAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        Buyer_Profile = (CircleImageView) findViewById(R.id.Buyer_Profile1);

        StorageReference profileRef1 = storageReference.child("user_buyer/" + fAuth.getCurrentUser().getUid() + "/profile.jpg");
        profileRef1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(Buyer_Profile);
            }
        });
    }

        private void setRecyclerView() {
            VersionsAdapter versionsAdapter = new VersionsAdapter(versionsList);
            recyclerView.setAdapter(versionsAdapter);
            recyclerView.setHasFixedSize(true);
        }

        private void initData() {
            versionsList = new ArrayList<>();

            versionsList.add(new Versions("What is RecylEarn?","An app that can convert plastic bottles into cash/vouchers that may motivate and encourage residents of Baguio City to properly segregate their plastic bottles."));
            versionsList.add(new Versions("How to earn points?","You can earn points by surrendering plastic bottles into our nearest drop-off points. Points to be earned depends on how many kg you can surrender. Each kg has its own equivalent points."));
            versionsList.add(new Versions("How to deposit plastic to the drop-off points?","1. Tap the “Trade a bottle” page on the dashboard.\n2. Tap the “Continue” button and from here, enter the number of kg of bottles you want to sell.\n3. After entering the desired number of kg, tap “Confirm”.\n4. Tap “Generate” button to generate a qr code based on the number of kg that you entered\n5. After generating the qr code, you can now go to our drop-off points and surrender your bottles."));
            versionsList.add(new Versions("How to redeem rewards?","Something Went Wrong!"));
            versionsList.add(new Versions("What type of plastic materials do you accept?","To see the information of plastic bottles that we accept, Tap “Trade a bottle” on the dashboard and also from here, you can view example images of accepted bottles by taping “Examples of accepted bottles”"));
            versionsList.add(new Versions("How to find drop-off points?","Tap the “Find the nearest place” page on the dashboard to view nearby drop-off points.\nBy clicking the images provided below the drop-off points, it will show you its location from the google maps where you can set the route when going there."));


        }


    private void navigationBuyerDrawer() {
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
// navigation menu items declaration
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_buyerinfo);
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
            case R.id.nav_buyerhome:
                Intent intent = new Intent(BuyerAboutUs.this, BuyerDashboard.class);
                startActivity(intent);
                break;
            case  R.id.nav_buyerprofile:
                Intent intent1 = new Intent(BuyerAboutUs.this, BuyerProfile.class);
                startActivity(intent1);
                break;
            case R.id.nav_buyerinfo:
                break;
            case R.id.nav_buyerSecurity:
                Intent security = new Intent(BuyerAboutUs.this, Buyer_Security.class);
                startActivity(security);
                break;
            case R.id.nav_buyerlogout:
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
    public void openbuyerprofile(){
        Intent intent = new Intent(BuyerAboutUs.this, BuyerProfile.class);
        startActivity(intent);
    }
}