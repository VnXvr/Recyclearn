package com.example.recyclearn;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SellerLeaderboards extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    public DrawerLayout drawerLayout;
    public NavigationView navigationView;
    public TextView navHeadername;

    public CircleImageView nav_profseller;

    private FirebaseUser user;
    FirebaseFirestore fStore;
    private String userID;
    private FirebaseAuth fAuth;
    private StorageReference storageReference;
    private CircleImageView Seller_Profile,user1,user2;
    private ImageView btn_drawer;
    static String userIDD;

    ProgressDialog progressDialog;

    RecyclerView recyclerView;
    ArrayList<User_SellerLeader> leaderArrayList;
    Leaderboards_Adapter leaderboards_adapter;
    FirebaseFirestore db;

    ConstraintLayout contentView;



    //Set animation when drawer is being dragged
    static final float END_SCALE = 0.8f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_leaderboards);
//drawer
        drawerLayout = findViewById(R.id.seller_drawer_layout2);
        navigationView = findViewById(R.id.nav_view2);
        btn_drawer = findViewById(R.id.btn_drawer);
        navigationView.setItemIconTintList(null);

        contentView = findViewById(R.id.leaderboardContent);
        navigationSellerDrawer();

        user = FirebaseAuth.getInstance().getCurrentUser();
        fStore = FirebaseFirestore.getInstance();
        userID = user.getUid();

        recyclerView = findViewById(R.id.SellerLeaderboards);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        leaderArrayList = new ArrayList<User_SellerLeader>();
        leaderboards_adapter = new Leaderboards_Adapter(SellerLeaderboards.this, leaderArrayList);

        recyclerView.setAdapter(leaderboards_adapter);
        leaderboards_adapter.notifyDataSetChanged();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data...");
        progressDialog.show();

        EventChangeListener();

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
        storageReference = FirebaseStorage.getInstance().getReference().child("user_seller/"+ fAuth.getInstance().getCurrentUser().getUid()+"/profile.jpg");
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(nav_profseller);
            }
        });
//display profile picture

        fAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        Seller_Profile =(CircleImageView) findViewById(R.id.Seller_Profile1);

        StorageReference profileRef1 = storageReference.child("user_seller/"+ fAuth.getCurrentUser().getUid()+"/profile.jpg");
        profileRef1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(Seller_Profile);
            }
        });

    }

    private void EventChangeListener() {
        fStore.collection("Seller_Users")
                .orderBy("sellerPoints", Query.Direction.DESCENDING).limit(10).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if(error != null){

                            if(progressDialog.isShowing())
                                progressDialog.dismiss();
                            Log.e("FireStore Error", error.getMessage());
                            return;
                        }
                        for(DocumentChange dc : value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED)
                                leaderArrayList.add(dc.getDocument().toObject(User_SellerLeader.class));

                            /*if(User_SellerLeader.fullName == null){
                                Toast.makeText(SellerLeaderboards.this, "Null din lods", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            fStore.collection("Seller_Users").whereEqualTo("fullName", User_SellerLeader.fullName).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                                    // Getting the User ID Of seller based off of the name scanned through QR
                                    for(DocumentSnapshot snapshot: snapshotList)
                                        Leaderboards_Adapter.userIDD = snapshot.getId();
                                    //Toast.makeText(SellerLeaderboards.this, userIDD, Toast.LENGTH_SHORT).show();
                                }
                            });*/
                        }
                        leaderboards_adapter.notifyDataSetChanged();

                        if(progressDialog.isShowing())
                            progressDialog.dismiss();
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
                Intent intent = new Intent(SellerLeaderboards.this, SellerDashboard.class);
                startActivity(intent);
                break;
            case  R.id.nav_sellerprofile:
                Intent intent1 = new Intent(SellerLeaderboards.this, SellerProfile.class);
                startActivity(intent1);
                break;
            case R.id.nav_sellerredeem:
                Intent redeem = new Intent(SellerLeaderboards.this, SellerRedeem.class);
                startActivity(redeem);
                break;
            case R.id.nav_sellerlocation:
                Intent intent2 = new Intent(SellerLeaderboards.this, SellerLocation.class);
                startActivity(intent2);
                break;
            case R.id.nav_sellerSecurity:
                Intent security = new Intent(SellerLeaderboards.this, Seller_Security.class);
                startActivity(security);
                break;
            case R.id.nav_sellerinfo:
                Intent intent3 = new Intent(SellerLeaderboards.this, SellerAboutUs.class);
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
    public void opensellerprofile(){
        Intent intent = new Intent(SellerLeaderboards.this, SellerProfile.class);
        startActivity(intent);
    }
}