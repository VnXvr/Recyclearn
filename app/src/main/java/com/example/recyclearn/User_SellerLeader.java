package com.example.recyclearn;

public class User_SellerLeader {

    String fullName;
    String imageUrl;
    Double sellerPoints;

    public User_SellerLeader(){

    }


    public User_SellerLeader(String fullName, Double sellerPoints, String imageUrl ) {
        this.fullName = fullName;
        this.sellerPoints = sellerPoints;
        this.imageUrl  = imageUrl ;
    }



    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Double getSellerPoints() {
        return sellerPoints;
    }

    public void setSellerPoints(Double sellerPoints) {
        this.sellerPoints = sellerPoints;
    }
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
