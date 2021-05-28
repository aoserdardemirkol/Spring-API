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
import com.example.demo.service.GarageService;
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
    // Repository tanımlandı.
    @Autowired private garagerepo garagerepo;
    @Autowired private garagealanrepo garagealanrepo;

    // Service içerisinde bulunan Garaj class ı tanımlandı.
    private Garaj garaj = new Garaj();

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

    // Veritabanın da bulunan garajalanının değeri liste değişkenine atandı.
    public int getAlan(){
        List<garagealan> liste = garagealanrepo.findAll();
        return liste.get(0).getAlan();
    }

    // Get isteği ile kullanıcıya bütüm garaj içerisinde bulunan araçların bilgileri getirildi.
    @GetMapping
    @ApiOperation(value = "Bütün kullanıcıları listeler")
    public ResponseEntity<List<garage>> getArac() {
        return new ResponseEntity<>(garagerepo.findAll(), OK);
    }

    // Get isteği ile girilen id ye ait olan aracın bilgileri getirildi.
    // orElseThrow ile eğer girilen id ye ait araç yok ise hata döndürüldü.
    // ilk getArac metodunda aşşağıdaki kod ile tek metod halinde istenilen değerlere ulaşılabilir.
    // if (name == null) {
    //            return new ResponseEntity<>(garagerepo.findAll(), OK) ;
    //        } else {
    //            return new ResponseEntity<>(garagerepo.findById(id), OK);
    //        }

    @GetMapping(path="/{id}")
    @ApiOperation(value = "Girilen id değerine göre aracın bilgilerini getirir")
    public ResponseEntity<garage> getArac(@PathVariable int id){
        return new ResponseEntity<>(garagerepo.findById(id)
                .orElseThrow(() -> new AracNotFoundException("Girilen id ye ait araç bulunamadı: " + id)), OK);
    }

    // Post isteği ile createIslem metoduyla yeni araç girişleri yapılabilir
    // @RequestBody garage garage ile garage modelinden @JsonIgnore ile ayrılmamış veriler getirilerek istek atma kolaylaştırıldı.
    // Ayrıca garagerequest nesnesi oluşturularak girilmesi istenilen veriler kısıtlanabilir.
    @ApiOperation(value = "Araç girişi yapılır")
    @PostMapping("/giris")
    public ResponseEntity<garage> createIslem(@RequestBody garage garage){
        // Veri girişi yapılmadan önce veritabanında bulunan garagealan tablosunda değer olup olmadığı kontrol edildi.
        startFirst();
        // Plaka ya göre daha önceden aracın giriş yapıp yapmadığı kontrol edildimesi için garagerepo.findByPlaka() tanımlandı
        Optional<garage> garageByPlaka = garagerepo.findByPlaka(garage.getPlaka());

        // throw new GirisNotAcceptableException
        // throw new AracAlreadyExistsException
        // throw new TanımlıGarajNotExistsException gibi exceptionlar özelleştirldi.
        if (getAracTip(garage.getTip(), garage.getPlaka()).getAlan() > garaj.getGarajBoyut())
            throw new GirisNotAcceptableException("Garajda yer yok önce çıkış yapılmalı");
        else if (garageByPlaka.isPresent())
            throw new AracAlreadyExistsException(garage.getPlaka() + " plakalı araç zaten garajda...");
        else if(garagealanrepo.count()==0)
            throw new TanımlıGarajNotExistsException("Tanımlı garaj boyutu yok");
        else {
            // Veritabanında bulunan garage tablosuna veri girişi yapılacağından dolayı tanımlandı.
            garage newGarage = new garage();

            // garage tablosunda bulunan değerler atandı.
            // garage.getTip() metodu ile veriler soyut AracFabrikasina veriler gönderilerek
            // AracFabrikasının alt metotlarından girilen arac tipine göre kapladığı alan belirlendi.
            newGarage.setAlan(getAracTip(garage.getTip(), garage.getPlaka()).getAlan());
            newGarage.setPlaka(garage.getPlaka());
            newGarage.setTip(garage.getTip());

            // Giriş yapıldıktan sonra garaj sınıfından boyut girilen boyut düşürüldü.
            garaj.setGarajBoyut(garaj.getGarajBoyut() - newGarage.getAlan());

            return new ResponseEntity<>(garagerepo.save(newGarage), OK);
        }
    }

    // girilen id ve diğer bilgilere göre aracın bilgileri güncellendi.
    // @PathVariable yerine @RequestParam da kullanılabilir.
    @ApiOperation(value = "Girilen id değerinin verileri güncellenebilir")
    @PutMapping("/update/{id}/{tip}/{plaka}")
    public ResponseEntity<garage> updateIslem(@PathVariable int id, @PathVariable int tip, @PathVariable String plaka) {
        if (getAracTip(tip, plaka).getAlan() > garaj.getGarajBoyut())
            // Eğer güncellenen araç boyutun değiştiğinde yeterli yer kalmıyor ise hata verildi.
            throw new GirisNotAcceptableException("Garajda yer yok önce çıkış yapılmalı");
        else {
            // Eğer hata yok ise updateIslemById() metoduna veriler gönderilerek güncelleme sağlandı.
            updateIslemById(id, tip, plaka);
            // Güncelleme yapıldıktan sora araç boyutunun güncellenmesi için metot çağırıldı.
            startFirst();
            return new ResponseEntity<>(OK);
        }
    }

    // Girilen plaka değerine göre araç veritabanından silindi.
    @ApiOperation(value = "Girilen plaka değerini siler Araç çıkışı yapılır.")
    @DeleteMapping("/delete/{plaka}")
    @Transactional
    public ResponseEntity<Void> deleteAracByPlaka(@PathVariable String plaka) {
        // Girilen plaka değeri findByPlaka() ile aranarak garageByPlaka() değerine eşitlendi.
        Optional<garage> garageByPlaka = garagerepo.findByPlaka(plaka);
        // Eğer plaka değeri veritabanında kayıtlı ise değer deleteByPlaka() ile silindi.
        if (garageByPlaka.isPresent()){
            garagerepo.deleteByPlaka(plaka);
            return new ResponseEntity<>(OK);
        }
        // Eğer değer veritabanında yok ise hata döndürüldü.
        else
            throw new AracNotFoundException("Silmek istediğiniz araç garajda değil");
    }

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

    // ExceptionHandler metodları
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
