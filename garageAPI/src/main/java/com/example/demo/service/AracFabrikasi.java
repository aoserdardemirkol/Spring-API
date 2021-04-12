package com.example.demo.service;

public class AracFabrikasi {
    public static Arac getArac(int tip, String plaka) {
        Arac arac;

        String type = String.valueOf(tip);
        switch (type) {
            case "1" -> arac = new Bisiklet(plaka);
            case "2" -> arac = new Motorsiklet(plaka);
            case "3" -> arac = new Araba(plaka);
            case "4" -> arac = new Kamyonet(plaka);
            case "5" -> arac = new Otobus(plaka);
            case "6" -> arac = new Tir(plaka);
            default -> throw new RuntimeException("yok");
        }

        return arac;
    }
}

