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

public class Buyer_Adapter extends RecyclerView.Adapter<Buyer_Adapter.MyViewHolder> {

    Context context;
    ArrayList<User_Buyer> arrayList;
    String strDate;
    DateFormat dateFormat;

    public Buyer_Adapter(Context context, ArrayList<User_Buyer> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public Buyer_Adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.buyertransaction,parent, false);
        return new Buyer_Adapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Buyer_Adapter.MyViewHolder holder, int position) {
        User_Buyer user = arrayList.get(position);
        holder.fullName.setText(user.receiver);
        holder.sellerKgInput.setText(user.kgOfBottlesReceived);
        holder.sellerPoints.setText(user.pointsGiven);

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
            fullName = itemView.findViewById(R.id.sellerName);
            sellerKgInput = itemView.findViewById(R.id.kilo);
            sellerPoints = itemView.findViewById(R.id.points);
            createdAt = itemView.findViewById(R.id.DateTime);
        }
    }
}
