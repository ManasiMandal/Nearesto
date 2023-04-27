package com.manasi.nearesto.modal;

import java.io.Serializable;
import java.util.ArrayList;

public class Order implements Serializable {
    private ArrayList<CartItem> items;
    private long timestamp, cancelledOn;

    private String user, status;

    public Order() {}

    public Order(ArrayList<CartItem> items, String user) {
        this.items     = items;
        this.timestamp = System.currentTimeMillis();
        this.user      = user;
        this.status    = "placed";
    }

    public ArrayList<CartItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<CartItem> items) {
        this.items = items;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getCancelledOn() {
        return cancelledOn;
    }

    public void setCancelledOn(long cancelledOn) {
        this.cancelledOn = cancelledOn;
    }

    public boolean isCancelled() {
        return getStatus().equals("cancelled");
    }
}
