package com.example.recyclearn;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class BuyerProfile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    public DrawerLayout drawerLayout;
    public NavigationView navigationView;
    public TextView navHeadername;
    public CircleImageView nav_profbuyer;

    private FirebaseUser user;
    FirebaseFirestore fStore;
    private String userID;
    private FirebaseAuth fAuth;
    private StorageReference storageReference;

    private CircleImageView profilepic, headerprofilepic, nav_profile;
    private ImageView changeprofilepic;

    private ImageView btn_drawer;
    private ImageView update;

    public Button refresh;
    public static String setLimit;
    EditText limit;

    ConstraintLayout contentView;

    //Set animation when drawer is being dragged
    static final float END_SCALE = 0.8f;


    RecyclerView recyclerView;
    ArrayList<User_Buyer> buyerArrayList;
    Buyer_Adapter buyer_adapter;
    FirebaseFirestore db;

    ProgressDialog progressDialog;

    String username;
    String fullname;
    String points;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_profile);
        drawerLayout = findViewById(R.id.buyer_drawer_layout1);
        navigationView = findViewById(R.id.nav_view1);
        btn_drawer = findViewById(R.id.btn_drawer);
        navigationView.setItemIconTintList(null);

        refresh = findViewById(R.id.REFRESH);
        limit = findViewById(R.id.setLimit);

        update = findViewById(R.id.edit);

        // display onclick on UpdateProfile
        update = (ImageView) findViewById(R.id.edit);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openbuyerupdateprofile();
            }

        });



        contentView = findViewById(R.id.bprofileContent);
        navigationBuyerDrawer();

        recyclerView = findViewById(R.id.BuyerTransaction);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        buyerArrayList = new ArrayList<User_Buyer>();
        buyer_adapter = new Buyer_Adapter(BuyerProfile.this, buyerArrayList);

        recyclerView.setAdapter(buyer_adapter);
        buyer_adapter.notifyDataSetChanged();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data...");
        progressDialog.show();

//displayname
        user = FirebaseAuth.getInstance().getCurrentUser();
        fStore = FirebaseFirestore.getInstance();
        userID = user.getUid();

        final TextView usernameTextView = (TextView) findViewById(R.id.usernamebuyer);
        final TextView fullnameTextView = (TextView) findViewById(R.id.fullnamebuyer);

        DocumentReference documentReference = fStore.collection("Buyer_Users").document(userID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                username = value.getString("username");
                fullname = value.getString("fullName");
                usernameTextView.setText(username);
                fullnameTextView.setText(fullname);

                //Display user info in drawer
                navHeadername = navigationView.getHeaderView(0).findViewById(R.id.nav_usernamebuyer);
                navHeadername.setText(username);

                EventChangeListener();
            }

            private void clear() {
                int size = buyerArrayList.size();
                buyerArrayList.clear();
                buyer_adapter.notifyItemRangeRemoved(0, size);
            }

            private void EventChangeListener() {

                fStore.collection("Buyer_Users").document(userID).collection("transactions").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()) {
                                    if(progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    fStore.collection("Buyer_Transactions").whereEqualTo("sender", fullname)
                                            .orderBy("createdAt", Query.Direction.DESCENDING).limit(Integer.parseInt(limit.getText().toString())).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                @Override
                                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                                    for(DocumentChange dc : value.getDocumentChanges()) {
                                                        if (dc.getType() == DocumentChange.Type.ADDED)
                                                            buyerArrayList.add(dc.getDocument().toObject(User_Buyer.class));
                                                        buyer_adapter.notifyDataSetChanged();
                                                    }
                                                }
                                            });
                                }
                            }
                        });
                refresh.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clear();
                        setLimit = limit.getText().toString();
                        closeKeyBoard();

                        if(setLimit.equals("0"))
                            clear();
                        else if(TextUtils.isEmpty(limit.getText().toString())){
                            limit.setError("Filed is required");
                            return;
                        } else
                            EventChangeListener();
                    }
                });

            }
        });

// dispaly profile picture in navigation drawer
        nav_profbuyer = navigationView.getHeaderView(0).findViewById(R.id.nav_buyerprofile2);
        storageReference = FirebaseStorage.getInstance().getReference().child("user_buyer/" + fAuth.getInstance().getCurrentUser().getUid() + "/profile.jpg");
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(nav_profbuyer);
            }
        });
//change profile
        profilepic = findViewById(R.id.profilepicbuyer);
        headerprofilepic = findViewById(R.id.profilepicbuyer1);
        changeprofilepic = findViewById(R.id.btn_changeprofilepicbuyer);
        fAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        StorageReference profileRef1 = storageReference.child("user_buyer/" + fAuth.getCurrentUser().getUid() + "/profile.jpg");
        profileRef1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(headerprofilepic);
            }
        });
//change profile
        StorageReference profileRef = storageReference.child("user_buyer/" + fAuth.getCurrentUser().getUid() + "/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profilepic);
            }
        });

        changeprofilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1000);
            }
        });

    }

    private void navigationBuyerDrawer() {
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
// navigation menu items declaration
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_buyerprofile);

        btn_drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        animateNavigationDrawer();
        closeKeyBoard();

    }

    private void closeKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();
                //profilepic.setImageURI(imageUri);

                uploadImagetoFirebase(imageUri);
            }
        }
    }

    private void uploadImagetoFirebase(Uri imageUri) {
        final StorageReference fileRef = storageReference.child("user_buyer/" + fAuth.getCurrentUser().getUid() + "/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.get().load(uri).into(profilepic);
                            }
                        });

                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(BuyerProfile.this, "Image Uploaded Fail!", Toast.LENGTH_LONG).show();
                    }
                });
    }

    //drawer back button
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            super.onBackPressed();
        }
        else{
            Intent balik = new Intent(BuyerProfile.this, BuyerDashboard.class);
            startActivity(balik);
            super.onBackPressed();
        }

    }

    //drawer elements
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuitem) {

        switch (menuitem.getItemId()) {
            case R.id.nav_buyerhome:
                Intent intent = new Intent(BuyerProfile.this, BuyerDashboard.class);
                startActivity(intent);
                break;
            case R.id.nav_buyerprofile:
                break;
            case R.id.nav_buyerinfo:
                Intent intent3 = new Intent(BuyerProfile.this, BuyerAboutUs.class);
                startActivity(intent3);
                break;
            case R.id.nav_buyerSecurity:
                Intent security = new Intent(BuyerProfile.this, Buyer_Security.class);
                startActivity(security);
                break;
            case R.id.nav_buyerlogout:
                Intent intent4 = new Intent(BuyerProfile.this, MainActivity.class);
                startActivity(intent4);
                break;
            // case R.id.: "dito ilalagay yung pag na click tas ma direct to other activity"
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void openbuyerupdateprofile() {
        Intent oks = new Intent(BuyerProfile.this, BuyerUpdateProfile.class);
        startActivity(oks);
    }


}