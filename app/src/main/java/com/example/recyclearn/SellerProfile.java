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

public class SellerProfile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public DrawerLayout drawerLayout;
    public NavigationView navigationView;
    public TextView navHeadername;
    public CircleImageView nav_profseller;

    public Button refresh;
    public static String setLimit;
    EditText limit;

    private FirebaseUser user;
    FirebaseFirestore fStore;
    private String userID;
    private FirebaseAuth fAuth;
    private StorageReference storageReference;

    private CircleImageView profilepic, headerprofilepic, nav_profile;
    private ImageView changeprofilepic;

    private ImageView btn_drawer;

    private ImageView palit;

    ConstraintLayout contentView;

    //Set animation when drawer is being dragged
    static final float END_SCALE = 0.8f;

    RecyclerView recyclerView;
    ArrayList<User_Seller> sellerArrayList;
    Seller_Adapter seller_adapter;
    FirebaseFirestore db;

    ProgressDialog progressDialog;

    String username;
    String fullname;
    String points;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_profile);

        drawerLayout = findViewById(R.id.seller_drawer_layout1);
        navigationView = findViewById(R.id.nav_view1);
        btn_drawer = findViewById(R.id.btn_drawer);
        navigationView.setItemIconTintList(null);
        palit = findViewById(R.id.edit);

        refresh = findViewById(R.id.REFRESH);
        limit = findViewById(R.id.setLimit);

        contentView = findViewById(R.id.profileContent);
        navigationSellerDrawer();

        recyclerView = findViewById(R.id.SellerTransaction);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        sellerArrayList = new ArrayList<User_Seller>();
        seller_adapter = new Seller_Adapter(SellerProfile .this, sellerArrayList);

        recyclerView.setAdapter(seller_adapter);
        seller_adapter.notifyDataSetChanged();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data...");
        progressDialog.show();



// display onclick on UpdateProfile
        palit = (ImageView) findViewById(R.id.edit);
        palit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                opensellerupdateprofile();
            }
        });



//displayname
        user = FirebaseAuth.getInstance().getCurrentUser();
        fStore = FirebaseFirestore.getInstance();
        userID = user.getUid();

        TextView usernameTextView = findViewById(R.id.usernameseller);
        TextView fullnameTextView = findViewById(R.id.fullnameseller);
        TextView sellerPoints = findViewById(R.id.profPoints);

        DocumentReference documentReference = fStore.collection("Seller_Users").document(userID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                username = value.getString("username");
                fullname = value.getString("fullName");
                points = value.getString("sellerPoints");
                usernameTextView.setText(username);
                fullnameTextView.setText(fullname);
                sellerPoints.setText(points);

                //Display user info in drawer
                navHeadername = navigationView.getHeaderView(0).findViewById(R.id.nav_usernameseller);
                navHeadername.setText(username);

                EventChangeListener();
            }
        });



// dispaly profile picture in navigation drawer
        nav_profseller = navigationView.getHeaderView(0).findViewById(R.id.nav_sellerprofile2);
        storageReference = FirebaseStorage.getInstance().getReference().child("user_seller/" + fAuth.getInstance().getCurrentUser().getUid() + "/profile.jpg");

        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(nav_profseller);
            }
        });
//change profile
        profilepic = findViewById(R.id.profilepicseller);
        headerprofilepic = findViewById(R.id.profilepicseller1);
        changeprofilepic = findViewById(R.id.btn_changeprofilepicseller);
        fAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        StorageReference profileRef1 = storageReference.child("user_seller/" + fAuth.getCurrentUser().getUid() + "/profile.jpg");
        profileRef1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(headerprofilepic);
            }
        });
//change profile
        StorageReference profileRef = storageReference.child("user_seller/" + fAuth.getCurrentUser().getUid() + "/profile.jpg");
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

        animateNavigationDrawer();
    }

    private void closeKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void clear() {
        int size = sellerArrayList.size();
        sellerArrayList.clear();
        seller_adapter.notifyItemRangeRemoved(0, size);
    }

    private void EventChangeListener() {
        fStore.collection("Seller_Users").document(userID).collection("transactions").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            if(progressDialog.isShowing())
                                progressDialog.dismiss();
                            fStore.collection("Seller_Transactions").whereEqualTo("receiver", fullname)
                                    .orderBy("createdAt", Query.Direction.DESCENDING).limit(Integer.parseInt(limit.getText().toString())).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                            for(DocumentChange dc : value.getDocumentChanges()) {
                                                if (dc.getType() == DocumentChange.Type.ADDED)
                                                    sellerArrayList.add(dc.getDocument().toObject(User_Seller.class));
                                                seller_adapter.notifyDataSetChanged();
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

    private void navigationSellerDrawer() {
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
// navigation menu items declaration
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_sellerprofile);
        btn_drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else drawerLayout.openDrawer(GravityCompat.START);
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
        final StorageReference fileRef = storageReference.child("user_seller/" + fAuth.getCurrentUser().getUid() + "/profile.jpg");
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
                        Toast.makeText(SellerProfile.this, "Image Uploaded Fail!", Toast.LENGTH_LONG).show();
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
        }else {
            Intent balik = new Intent(SellerProfile.this, SellerDashboard.class);
            startActivity(balik);
            super.onBackPressed();
        }

    }

    //drawer elements
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuitem) {

        switch (menuitem.getItemId()) {
            case R.id.nav_sellerhome:
                Intent intent = new Intent(SellerProfile.this, SellerDashboard.class);
                startActivity(intent);
                break;
            case R.id.nav_sellerprofile:
                break;
            case R.id.nav_sellerredeem:
                Intent redeem = new Intent(SellerProfile.this, SellerRedeem.class);
                startActivity(redeem);
                break;
            case R.id.nav_sellerlocation:
                Intent intent2 = new Intent(SellerProfile.this, SellerLocation.class);
                startActivity(intent2);
                break;
            case R.id.nav_sellerSecurity:
                Intent security = new Intent(SellerProfile.this, Seller_Security.class);
                startActivity(security);
                break;
            case R.id.nav_sellerinfo:
                Intent intent3 = new Intent(SellerProfile.this, SellerAboutUs.class);
                startActivity(intent3);
                break;
            case R.id.nav_sellerlogout:
                // YUNG CODE PARA DI NA BUMALIK ULIT SA ACTIVITY AFTER NAG LOG OUT YUNG USER-------------
                Intent intent4 = new Intent(getBaseContext(),MainActivity.class);
                intent4.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // YUNG CODE PARA DI NA BUMALIK ULIT SA ACTIVITY AFTER NAG LOG OUT YUNG USER-------------
                startActivity(intent4);
                break;
            // case R.id.: "dito ilalagay yung pag na click tas ma direct to other activity"
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // Open Update profile
    public void opensellerupdateprofile(){
        Intent oks = new Intent(SellerProfile.this, SellerUpdateProfile.class);
        startActivity(oks);

    }

}