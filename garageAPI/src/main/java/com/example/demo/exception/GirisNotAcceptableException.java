package com.example.demo.exception;

public class GirisNotAcceptableException extends RuntimeException{
    public GirisNotAcceptableException(String msg){
        super(msg);
    }
}
