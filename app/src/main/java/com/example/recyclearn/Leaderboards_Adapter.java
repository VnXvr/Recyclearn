package com.example.recyclearn;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Leaderboards_Adapter extends RecyclerView.Adapter<Leaderboards_Adapter.MyViewHolder> {

    Context context;
    ArrayList<User_SellerLeader> leaderArrayList;
    final ArrayList<User_SellerLeader> images = new ArrayList<>();

    private StorageReference storageReference;
    private FirebaseAuth fAuth;
    private FirebaseUser user;
    FirebaseFirestore fStore;
    private String userID;


    String sellerURL;

    public Leaderboards_Adapter(Context context, ArrayList<User_SellerLeader> leaderArrayList) {
        this.context = context;
        this.leaderArrayList = leaderArrayList;
    }

    @NonNull
    @Override
    public Leaderboards_Adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.seller_leaderboards,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Leaderboards_Adapter.MyViewHolder holder, int position) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        User_SellerLeader user = leaderArrayList.get(position);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();


        fStore.collection("Seller_Users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    User_SellerLeader user = documentSnapshot.toObject(User_SellerLeader.class);
                     sellerURL = documentSnapshot.getString("imageUrl");

                    StorageReference profileRef1 = storageReference.child("user_seller/" + sellerURL + "/profile.jpg");
                    profileRef1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(holder.profile);
                        }
                    });
                }
            }
        });


        holder.fullName.setText(user.fullName);
        holder.sellerPoints.setText(user.sellerPoints.toString());




    }

    @Override
    public int getItemCount() {
        return leaderArrayList.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView fullName, sellerPoints;
        public CircleImageView profile, profile2;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            fullName = itemView.findViewById(R.id.sellerName);
            sellerPoints = itemView.findViewById(R.id.pointsSeller);
            profile = (CircleImageView) itemView.findViewById(R.id.sellerProfile);


        }
    }
}
