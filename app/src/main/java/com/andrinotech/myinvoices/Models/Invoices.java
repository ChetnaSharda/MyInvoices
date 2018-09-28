package com.andrinotech.myinvoices.Models;

public class Invoices {
    int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    String title, Date, invoiceType, shopName, Location, Comment;
    byte[] image;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public Invoices() {

    }

    public Invoices(int id,String title, String date, String invoiceType, String shopName, String location, String comment, byte[] image) {
        this.id=id;
        this.title = title;
        Date = date;
        this.invoiceType = invoiceType;
        this.shopName = shopName;
        Location = location;
        Comment = comment;
        this.image = image;
    }
}
