package com.example.demo.service;

import com.example.demo.repository.garagealanrepo;
import com.example.demo.repository.garagerepo;
import com.example.demo.exception.AracNotFoundException;
import com.example.demo.model.garage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GarageService {
    @Autowired private garagerepo garagerepo;
    @Autowired private garagealanrepo garagealanrepo;

    private Garaj garaj = new Garaj();

    // Gönderilen değerler ile araç bilgilerinin güncellemesi sağlandı.
    public void updateIslemById(int id, int tip, String plaka) {
        // ıd değeri getAracById() metoduna gönderildi ve dönen değerler oldArac değerine eşitlendi.
        garage oldArac = getAracById(id);

        // Eski aracın bilgileri güncellendi.
        oldArac.setTip(tip);
        oldArac.setPlaka(plaka);
        // Araç bilgileri getAracTip() metodu ikler getirildi.
        oldArac.setAlan(getAracTip(tip, plaka).getAlan());
        // Yeni bilgiler kaydedildi.
        garagerepo.save(oldArac);
    }

    // Girilen id değerine göre garagerepo dan araç bilgileri getirildi.
    public garage getAracById(int id) {
        /// Eğer değer yok ise hata verildi.
        return garagerepo.findById(id)
                .orElseThrow(() -> new AracNotFoundException("Girilen id ye ait araç bulunamadı: " + id));
    }

    // AracFabrikasının alt metotlarından girilen arac tipine göre bilgiler getirildi.
    public Arac getAracTip(int tip, String plaka){
        return AracFabrikasi.getArac(tip, plaka);
    }
}
