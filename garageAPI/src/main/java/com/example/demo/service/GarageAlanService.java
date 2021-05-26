package com.example.demo.service;

import com.example.demo.exception.GarajboyutNotFoundException;
import com.example.demo.model.garagealan;
import com.example.demo.repository.garagealanrepo;
import com.example.demo.repository.garagerepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GarageAlanService {
    @Autowired private garagealanrepo garagealanrepo;
    @Autowired private garagerepo garagerepo;

    // Service içerisinde bulunan Garaj class ı tanımlandı.
    private Garaj garaj = new Garaj();

    // updateAlanByAlan() metodu veri tabanında bulunan değeri güncellendi.
    public void updateAlanByAlan(garagealan newgaragealan){
        garagealan oldAlan = getAlanByAlan(getAlan());
        oldAlan.setAlan(newgaragealan.getAlan());

        garagealanrepo.save(oldAlan);
    }

    // Veritabanında bulunan garaj boyutunu getirildi.
    public garagealan getAlanByAlan(int alan) {
        return garagealanrepo.findByAlan(alan)
                .orElseThrow(() -> new GarajboyutNotFoundException("Belirlenmiş bir garaj boyutu bulunamadı: " + alan));
    }

    // Veritabanın da bulunan garajalanının değeri liste değişkenine atandı.
    public int getAlan(){
        List<garagealan> liste = garagealanrepo.findAll();
        return liste.get(0).getAlan();
    }
}
