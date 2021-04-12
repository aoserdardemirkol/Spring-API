package com.example.demo.controller;

import com.example.demo.exception.GirisNotAcceptableException;
import com.example.demo.exception.TanımlıGarajNotExistsException;
import com.example.demo.model.garage;
import com.example.demo.model.garagealan;
import com.example.demo.repository.garagealanrepo;
import com.example.demo.repository.garagerepo;
import com.example.demo.exception.AracAlreadyExistsException;
import com.example.demo.exception.AracNotFoundException;
import com.example.demo.service.Arac;
import com.example.demo.service.AracFabrikasi;
import com.example.demo.service.Garaj;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.*;

@RestController
@Api(value = "Garage API documentation")
public class GarageController {
    @Autowired
    private garagerepo garagerepo;
    @Autowired
    private garagealanrepo garagealanrepo;

    Garaj garaj = new Garaj();
    garage newGarage = new garage();

    @EventListener(ApplicationReadyEvent.class)
    public void startFirst(){
        if (garagealanrepo.count() != 0) {
            if (garagerepo.count() != 0)
                garaj.setGarajBoyut(getAlan() - garagerepo.sumAlan());
            else
                garaj.setGarajBoyut(getAlan());
        }
    }

    public int getAlan(){
        List<garagealan> liste = garagealanrepo.findAll();
        return liste.get(0).getAlan();
    }

    @GetMapping
    @ApiOperation(value = "Bütün kullanıcıları listeler")
    public ResponseEntity<List<garage>> getArac() {
        return new ResponseEntity<>(garagerepo.findAll(), OK);
    }

    @GetMapping(path="/{id}")
    @ApiOperation(value = "Girilen id değerine göre aracın bilgilerini getirir")
    public ResponseEntity<garage> getArac(@PathVariable int id){
        return new ResponseEntity<>(garagerepo.findById(id)
                .orElseThrow(() -> new AracNotFoundException("Girilen id ye ait araç bulunamadı: " + id)), OK);
    }

    @ApiOperation(value = "Araç girişi yapılır")
    @PostMapping("/giris/{tip}/{plaka}")
    public ResponseEntity<garage> createIslem(@PathVariable int tip, @PathVariable String plaka){
        startFirst();
        Optional<garage> garageByPlaka = garagerepo.findByPlaka(plaka);

        if (getAracTip(tip, plaka).getAlan() > garaj.getGarajBoyut())
            throw new GirisNotAcceptableException("Garajda yer yok önce çıkış yapılmalı");
        else if (garageByPlaka.isPresent())
            throw new AracAlreadyExistsException(plaka + " plakalı araç zaten garajda...");
        else if(garagealanrepo.count()==0)
            throw new TanımlıGarajNotExistsException("Tanımlı garaj boyutu yok");
        else {
            newGarage.setAlan(getAracTip(tip, plaka).getAlan());
            newGarage.setPlaka(plaka);
            newGarage.setTip(tip);

            garaj.setGarajBoyut(garaj.getGarajBoyut() - newGarage.getAlan());

            return new ResponseEntity<>(garagerepo.save(newGarage), OK);
        }
    }

    public Arac getAracTip(int tip, String plaka){
        return AracFabrikasi.getArac(tip, plaka);
    }

    @ApiOperation(value = "Girilen id değerinin verileri güncellenebilir")
    @PutMapping("/update/{id}/{tip}/{plaka}")
    public ResponseEntity<garage> updateIslem(@PathVariable int id, @PathVariable int tip, @PathVariable String plaka) {
        if (getAracTip(tip, plaka).getAlan() > garaj.getGarajBoyut())
            throw new GirisNotAcceptableException("Garajda yer yok önce çıkış yapılmalı");
        else {
            updateIslemById(id, tip, plaka);
            startFirst();
            return new ResponseEntity<>(OK);
        }
    }

    public void updateIslemById(int id, int tip, String plaka) {
        garage oldArac = getAracById(id);

        oldArac.setTip(tip);
        oldArac.setPlaka(plaka);
        oldArac.setAlan(getAracTip(tip, plaka).getAlan());

        garagerepo.save(oldArac);
    }

    public garage getAracById(int id) {
        return garagerepo.findById(id)
                .orElseThrow(() -> new AracNotFoundException("Girilen id ye ait araç bulunamadı: " + id));
    }

    @ApiOperation(value = "Girilen plaka değerini siler Araç çıkışı yapılır.")
    @DeleteMapping("/delete/{plaka}")
    @Transactional
    public ResponseEntity<Void> deleteAracByPlaka(@PathVariable String plaka) {
        Optional<garage> garageByPlaka = garagerepo.findByPlaka(plaka);
        if (garageByPlaka.isPresent()){
            garagerepo.deleteByPlaka(plaka);
            return new ResponseEntity<>(OK);
        }
        else
            throw new AracNotFoundException("Silmek istediğiniz araç garajda değil");
    }

    @ExceptionHandler(AracNotFoundException.class)
    public ResponseEntity<String> handleAracNotFoundException(AracNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), NOT_FOUND);
    }

    @ExceptionHandler(AracAlreadyExistsException.class)
    public ResponseEntity<String> handleAracAlreadyExistException(AracAlreadyExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), CONFLICT);
    }

    @ExceptionHandler(TanımlıGarajNotExistsException.class)
    public ResponseEntity<String> handleTanımlıGarajNotExistsException(TanımlıGarajNotExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), NOT_FOUND);
    }

    @ExceptionHandler(GirisNotAcceptableException.class)
    public ResponseEntity<String> handleGirisNotAcceptableException(GirisNotAcceptableException ex){
        return new ResponseEntity<>(ex.getMessage(), NOT_ACCEPTABLE);
    }
}
