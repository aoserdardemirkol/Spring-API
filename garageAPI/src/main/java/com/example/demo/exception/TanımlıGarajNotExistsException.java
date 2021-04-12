package com.example.demo.exception;

public class TanımlıGarajNotExistsException extends RuntimeException{
    public TanımlıGarajNotExistsException(String msg){
        super(msg);
    }
}
