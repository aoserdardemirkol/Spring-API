package com.example.demo.exception;

public class AracNotFoundException extends RuntimeException{
    public AracNotFoundException(String msg){
        super(msg);
    }
}
