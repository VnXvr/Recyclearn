package com.example.recyclearn;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Seller_Adapter extends  RecyclerView.Adapter<Seller_Adapter.MyViewHolder>{

    Context context;
    ArrayList<User_Seller> arrayList;
    String strDate;
    DateFormat dateFormat;

    public Seller_Adapter(Context context, ArrayList<User_Seller> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public Seller_Adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.sellertransaction,parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Seller_Adapter.MyViewHolder holder, int position) {

        User_Seller user = arrayList.get(position);
        holder.fullName.setText(user.sender);
        holder.sellerKgInput.setText(user.kgOfBottlesSent);
        holder.sellerPoints.setText(user.pointsEarned);

        //convert timestamp to string and display it through set text
        Date creationDate = user.createdAt;
        dateFormat = new SimpleDateFormat("MM/dd/yyyy, hh:mm:ss aa");
        strDate = dateFormat.format(creationDate);
        holder.createdAt.setText(strDate);

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView fullName,sellerKgInput, sellerPoints, createdAt;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            fullName = itemView.findViewById(R.id.buyerName);
            sellerKgInput = itemView.findViewById(R.id.kilo);
            sellerPoints = itemView.findViewById(R.id.points);
            createdAt = itemView.findViewById(R.id.DateTime);
        }
    }
}
