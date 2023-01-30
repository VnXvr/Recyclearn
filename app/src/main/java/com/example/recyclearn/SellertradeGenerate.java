package com.example.recyclearn;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.squareup.picasso.Picasso;

import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class SellertradeGenerate extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public DrawerLayout drawerLayout;
    public NavigationView navigationView;
    public TextView navHeadername;

    public CircleImageView nav_profseller;

    private FirebaseUser user;
    FirebaseFirestore fStore;
    private String userID;
    private FirebaseAuth fAuth;
    private StorageReference storageReference;

    private CircleImageView Seller_Profile;
    private ImageView btn_drawer;


    public ImageView qrOutput;

    public Button btn_genqr,btn_continue;
    public TextView kgInput0,wrongkg;
    static String sText, sName, sIdNum, idNum;

    ConstraintLayout contentView;

    //Set animation when drawer is being dragged
    static final float END_SCALE = 0.8f;

    public static final String SELLER_INPUT = "SELLER_INPUT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sellertrade_generate);

//retrive firebase fullname
        user = FirebaseAuth.getInstance().getCurrentUser();
        fStore = FirebaseFirestore.getInstance();
        userID = user.getUid();

        final TextView fullnametextview = findViewById(R.id.selleridname);
        final TextView idnumbertextview = findViewById(R.id.selleridnumber);

        DocumentReference documentReference = fStore.collection("Seller_Users").document(userID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                String fullName = value.getString("fullName");
                String username = value.getString("username");
                String phoneNumber = value.getString("phoneNumber");
                String location = value.getString("location");
                String email = value.getString("email");
                String sellerPoints = value.getString("sellerPoints");
                String sellerKG = value.getString("sellerKgInput");

                fullnametextview.setText(fullName);
                idnumbertextview.setText(userID);

                //Display user info in drawer
                navHeadername = navigationView.getHeaderView(0).findViewById(R.id.nav_usernameseller);
                navHeadername.setText(fullName);
            }
        });

//// qr
        btn_genqr = findViewById(R.id.generatebutton);
        qrOutput = findViewById(R.id.qrImage);
        kgInput0 = findViewById(R.id.kgInput);

        Intent receiverIntent = getIntent();
        String receivedValue = receiverIntent.getStringExtra("KEY_SENDER");
        kgInput0.setText(receivedValue);

        btn_genqr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = shuffleString(fullnametextview.getText().toString().trim());
                String sName = fullnametextview.getText().toString().trim();
                sName +=","+input;
                Toast.makeText(SellertradeGenerate.this, sName, Toast.LENGTH_SHORT).show();

                MultiFormatWriter writer = new MultiFormatWriter();
                try {
                    BitMatrix matrix = writer.encode(sName, BarcodeFormat.QR_CODE, 500, 500);

                    BarcodeEncoder encoder = new BarcodeEncoder();

                    Bitmap bitmap = encoder.createBitmap(matrix);

                    qrOutput.setImageBitmap(bitmap);

                } catch (WriterException e) {
                    e.printStackTrace();
                }
                sName = input;
            }

            private String shuffleString(String string) {
                // store the string length to a variable for sake of comfort
                final int length = string.length();
                // use mutable StringBuilder for easy String manipulations
                final StringBuilder stringBuilder = new StringBuilder(string);
                // initialize the Random object generator once and use many times
                // as you don't want to initialize with each loop and throw it away
                final Random random = new Random();

                // define the number of iterations
                // to repeat as many times as the string is long is more than enough
                for (int i=0; i<length; i++) {
                    // pick two random indices to be swapped
                    final int firstIndex = random.nextInt(length);
                    final int secondIndex = random.nextInt(length);
                    // remember the swapped characters
                    // otherwise it would end up in a mess and duplicated characters
                    final char firstChar = stringBuilder.charAt(firstIndex);
                    final char secondChar = stringBuilder.charAt(secondIndex);
                    // perform the swap: basically set the characters to their positions
                    stringBuilder.setCharAt(firstIndex, secondChar);
                    stringBuilder.setCharAt(secondIndex, firstChar);
                }
                // and see the result
                return stringBuilder.toString();
            }
        });

//drawer
        drawerLayout = findViewById(R.id.seller_drawer_layout2);
        navigationView = findViewById(R.id.nav_view2);
        btn_drawer = findViewById(R.id.btn_drawer);
        navigationView.setItemIconTintList(null);

        contentView = findViewById(R.id.qr_generatorContent);
        navigationSellerDrawer();

// display onclick on sellerprofile
        Seller_Profile = (CircleImageView) findViewById(R.id.Seller_Profile1);
        Seller_Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                opensellerprofile();
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

        wrongkg = findViewById(R.id.wrongkg);
        wrongkg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wrongQuantity();
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
                Intent intent = new Intent(SellertradeGenerate.this, SellerDashboard.class);
                startActivity(intent);
                break;
            case  R.id.nav_sellerprofile:
                Intent intent1 = new Intent(SellertradeGenerate.this, SellerProfile.class);
                startActivity(intent1);
                break;
            case R.id.nav_sellerredeem:
                Intent redeem = new Intent(SellertradeGenerate.this, SellerRedeem.class);
                startActivity(redeem);
                break;
            case R.id.nav_sellerlocation:
                Intent intent2 = new Intent(SellertradeGenerate.this, SellerLocation.class);
                startActivity(intent2);
                break;
            case R.id.nav_sellerSecurity:
                Intent security = new Intent(SellertradeGenerate.this, Seller_Security.class);
                startActivity(security);
                break;
            case R.id.nav_sellerinfo:
                Intent intent3 = new Intent(SellertradeGenerate.this, SellerAboutUs.class);
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
        Intent intent = new Intent(SellertradeGenerate.this, SellerProfile.class);
        startActivity(intent);
    }
    public void wrongQuantity(){
        Intent goback = new Intent(SellertradeGenerate.this,SellerTradeGenQR.class);
        startActivity(goback);
    }
}