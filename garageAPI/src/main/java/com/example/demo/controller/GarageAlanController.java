package com.example.demo.controller;

import com.example.demo.exception.GarajboyutAlreadyExistsException;
import com.example.demo.exception.GarajboyutNotFoundException;
import com.example.demo.exception.GirisNotAcceptableException;
import com.example.demo.model.garagealan;
import com.example.demo.repository.garagealanrepo;
import com.example.demo.repository.garagerepo;
import com.example.demo.service.Garaj;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@Api(value = "Garage Alan API documentation")
public class GarageAlanController {
    // Repository tanımlandı.
    @Autowired private garagealanrepo garagealanrepo;
    @Autowired private garagerepo garagerepo;

    // Service içerisinde bulunan classlar tanımlandı.
    private Garaj garaj = new Garaj();

    // Model
    garagealan newgaragealan = new garagealan();

    // Uygulama başlatıldığında garajın boş olup olmadığı kontrol edildi.
    // service içerisinde bulunan Garaj clas ının GarajBoyut değişkenine değer atandı.
    @EventListener(ApplicationReadyEvent.class)
    public void startFirst(){
        if (garagealanrepo.count() != 0) {

            if (garagerepo.count() != 0)
                garaj.setGarajBoyut(getAlan() - garagerepo.sumAlan());
            else
                garaj.setGarajBoyut(getAlan());
        }
    }

    // Garaj Boyutu veri tabanında bulunan garagealan tablosuna eklendi.
    @PostMapping("/alan/{alan}")
    @ApiOperation(value = "Garaj boyutu belirlenir")
    public ResponseEntity<String> setAlan(@PathVariable int alan){
        // İstenilen proje de girilen garajboyutunun 5 ile 50 arasında olması istenildiğinden dolayı if ile kontrol edildi.
        if (alan >= 5 && alan <= 50) {
            // Garaj Boyutunun sürekli güncellenmemesi için eğer garaj boyutu belirlenmemiş ise belirlenmes sağlandı.
            if (garagealanrepo.count() == 0) {
                garaj.setGarajBoyut(alan);
                newgaragealan.setAlan(alan);
                garagealanrepo.save(newgaragealan);
                return new ResponseEntity<>(OK);
            }
            // Eğer Garaj boyutu belirlenmiş ise hata verildi.
            else
                throw new GarajboyutAlreadyExistsException("Garaj boyutu zaten belirlenmiş. Değiştirmek için update fonksiyonunu kullanın...");
        }
        // Eğer garaj boyutu belirlenen değerler içerisinde değil ise hata çevirildi.
        else
            throw new GirisNotAcceptableException("Garaj Boyutu 5-50 arasında olmalıdır...");
    }

    // @PutMapping ile araç boyutu güncellenmesi sağlandı.
    @PutMapping("/alanupdate/{alan}")
    @ApiOperation(value = "Garaj boyutu güncellenir")
    public ResponseEntity<String> updateAlan(@PathVariable int alan){
        // Girilen garajboyutunun 5 ile 50 arasında olması istenildiğinden dolayı if ile kontrol edildi.
        if (alan >= 5 && alan <= 50) {
            newgaragealan.setAlan(alan);
            // Eğer garajda araç var ise halihazırda garajda bulunan araçların kapladığı alandan daha küçük değer girmesi engellendi.
            if (garagerepo.count() != 0) {
                if (alan < garagerepo.sumAlan()) {
                    return new ResponseEntity<>("Garaj araçların toplam boyutundan daha küçük olamaz", NOT_ACCEPTABLE);
                } else {
                    garaj.setGarajBoyut(alan - garagerepo.sumAlan());
                    // updateAlanByAlan() metodu ile garaj boyutu güncellendi.
                    updateAlanByAlan(newgaragealan);
                    return new ResponseEntity<>(OK);
                }
            }
            else {
                updateAlanByAlan(newgaragealan);
                return new ResponseEntity<>(OK);
            }
        }
        else
            throw new GirisNotAcceptableException("Garaj Boyutu 5-50 arasında olmalıdır...");
    }

    // @DeleteMapping ile veritabanında bulunan garage ve garagealan tablosunda bulunan bütün değerler silindi.
    @DeleteMapping("/alansil")
    @ApiOperation(value = "Garaj boyutu ve giriş yapılan araçlar silinir")
    public ResponseEntity<Void> deleteAlan(){
        garagealanrepo.deleteAll();
        garagerepo.deleteAll();
        return new ResponseEntity<>(OK);
    }

    // Veritabanın da bulunan garajalanının değeri liste değişkenine atandı.
    public int getAlan(){
        List<garagealan> liste = garagealanrepo.findAll();
        return liste.get(0).getAlan();
    }

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

    // ExceptionHandler metodları
    @ExceptionHandler(GarajboyutAlreadyExistsException.class)
    public ResponseEntity<String> handleGarajboyutAlreadyExistException(GarajboyutAlreadyExistsException ex){
        return new ResponseEntity<>(ex.getMessage(), CONFLICT);
    }

    @ExceptionHandler(GarajboyutNotFoundException.class)
    public ResponseEntity<String> handleGarajboyutNotFoundException(GarajboyutNotFoundException ex){
        return new ResponseEntity<>(ex.getMessage(), NOT_FOUND);
    }

    @ExceptionHandler(GirisNotAcceptableException.class)
    public ResponseEntity<String> handleGirisNotAcceptableException(GirisNotAcceptableException ex){
        return new ResponseEntity<>(ex.getMessage(), NOT_FOUND);
    }
}
