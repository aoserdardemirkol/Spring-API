package com.example.demo.service;

public abstract class Arac {
    private int alan;
    private String ad;
    private String plaka;

    public Arac(int alan, String ad, String plaka) {
        this.alan = alan;
        this.ad = ad;
        this.plaka = plaka;
    }

    public int getAlan() {
        return alan;
    }

    public void setAlan(int alan) {
        this.alan = alan;
    }

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public String getPlaka() {
        return plaka;
    }

    public void setPlaka(String plaka) {
        this.plaka = plaka;
    }
}

