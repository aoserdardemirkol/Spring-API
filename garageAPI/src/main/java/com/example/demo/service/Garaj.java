package com.example.demo.service;

import org.springframework.stereotype.Service;

@Service
public class Garaj {
    private int garajBoyut;

    public Garaj(){

    }
    public Garaj(int garajBoyut) {
        this.garajBoyut = garajBoyut;
    }

    public int getGarajBoyut() {
        return garajBoyut;
    }
    public void setGarajBoyut(int garajBoyut) {
        this.garajBoyut = garajBoyut;
    }
}
