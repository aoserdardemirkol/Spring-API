package com.example.demo.repository;

import com.example.demo.model.garage;
import liquibase.datatype.core.VarcharType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface garagerepo extends JpaRepository<garage, Integer> {
    // id dışındaki veriler ile veritabanından istenilen değer bulunması veya silinmesi için değerler tanımlandı
    Optional<garage> findByPlaka(String plaka);
    Optional<garage> deleteByPlaka(String plaka);

    // garage tablosunda araçların toplam alanı getirilmesi için @Query tanımlandı.
    @Query(value = "SELECT sum(alan) FROM garage")
    public int sumAlan();
}
