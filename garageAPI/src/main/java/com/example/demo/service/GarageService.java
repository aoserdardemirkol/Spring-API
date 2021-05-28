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


}
