package com.example.recyclearn;
import java.util.Date;
public class User_Seller {

   public String receiver, sender, kgOfBottlesSent, pointsEarned;
    Date createdAt;

    public User_Seller(){

    }

    public User_Seller(String receiver, String sender, String kgOfBottlesSent, String pointsEarned, Date createdAt) {
        this.receiver = receiver;
        this.sender = sender;
        this.kgOfBottlesSent = kgOfBottlesSent;
        this.pointsEarned = pointsEarned;
        this.createdAt = createdAt;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getKgOfBottlesSent() {
        return kgOfBottlesSent;
    }

    public void setKgOfBottlesSent(String kgOfBottlesSent) {
        this.kgOfBottlesSent = kgOfBottlesSent;
    }

    public String getPointsEarned() {
        return pointsEarned;
    }

    public void setPointsEarned(String pointsEarned) {
        this.pointsEarned = pointsEarned;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
