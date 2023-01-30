package com.example.recyclearn;
import java.util.Date;
public class User_Buyer{

    public String receiver, sender, kgOfBottlesReceived, pointsGiven;
    Date createdAt;

    public User_Buyer(){

    }

    public User_Buyer(String receiver, String sender, String kgOfBottlesReceived, String pointsGiven, Date createdAt) {
        this.receiver = receiver;
        this.sender = sender;
        this.kgOfBottlesReceived = kgOfBottlesReceived;
        this.pointsGiven = pointsGiven;
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

    public String getKgOfBottlesReceived() {
        return kgOfBottlesReceived;
    }

    public void setKgOfBottlesReceived(String kgOfBottlesReceived) {
        this.kgOfBottlesReceived = kgOfBottlesReceived;
    }

    public String getPointsGiven() {
        return pointsGiven;
    }

    public void setPointsGiven(String pointsGiven) {
        this.pointsGiven = pointsGiven;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

}
