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
    @Autowired
    private garagealanrepo garagealanrepo;
    @Autowired
    private garagerepo garagerepo;

    Garaj garaj = new Garaj();
    garagealan newgaragealan = new garagealan();

    public int getAlan(){
        List<garagealan> liste = garagealanrepo.findAll();
        return liste.get(0).getAlan();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startFirst(){
        if (garagealanrepo.count() != 0) {

            if (garagerepo.count() != 0)
                garaj.setGarajBoyut(getAlan() - garagerepo.sumAlan());
            else
                garaj.setGarajBoyut(getAlan());
        }
    }

    @PostMapping("/alan/{alan}")
    @ApiOperation(value = "Garaj boyutu belirlenir")
    public ResponseEntity<String> setAlan(@PathVariable int alan){
        if (alan >= 5 && alan <= 50) {

            if (garagealanrepo.count() == 0) {
                garaj.setGarajBoyut(alan);
                newgaragealan.setAlan(alan);
                garagealanrepo.save(newgaragealan);
                return new ResponseEntity<>(OK);
            } else
                throw new GarajboyutAlreadyExistsException("Garaj boyutu zaten belirlenmiş. Değiştirmek için update fonksiyonunu kullanın...");
        }
        else
            throw new GirisNotAcceptableException("Garaj Boyutu 5-50 arasında olmalıdır...");
    }

    @PutMapping("/alanupdate/{alan}")
    @ApiOperation(value = "Garaj boyutu güncellenir")
    public ResponseEntity<String> updateAlan(@PathVariable int alan){
        if (alan >= 5 && alan <= 50) {
            newgaragealan.setAlan(alan);

            if (garagerepo.count() != 0) {

                if (alan < garagerepo.sumAlan()) {
                    return new ResponseEntity<>("Garaj araçların toplam boyutundan daha küçük olamaz", NOT_ACCEPTABLE);
                } else {
                    garaj.setGarajBoyut(alan - garagerepo.sumAlan());
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

    public void updateAlanByAlan(garagealan newgaragealan){
        garagealan oldAlan = getAlanByAlan(getAlan());
        oldAlan.setAlan(newgaragealan.getAlan());

        garagealanrepo.save(oldAlan);
    }

    public garagealan getAlanByAlan(int alan) {
        return garagealanrepo.findByAlan(alan)
                .orElseThrow(() -> new GarajboyutNotFoundException("Belirlenmiş bir garaj boyutu bulunamadı: " + alan));
    }

    @DeleteMapping("/alansil")
    @ApiOperation(value = "Garaj boyutu ve giriş yapılan araçlar silinir")
    public ResponseEntity<Void> deleteAlan(){
        garagealanrepo.deleteAll();
        garagerepo.deleteAll();
        return new ResponseEntity<>(OK);
    }

    @ExceptionHandler(GarajboyutAlreadyExistsException.class)
    public ResponseEntity<String> handleGarajboyutAlreadyExistException(GarajboyutAlreadyExistsException ex){
        return new ResponseEntity<>(ex.getMessage(), CONFLICT);
    }

    @ExceptionHandler(GarajboyutNotFoundException.class)
    public ResponseEntity<String> handleGarajboyutNotFoundException(GarajboyutNotFoundException ex){
        return new ResponseEntity<>(ex.getMessage(), NOT_FOUND);
    }
}
