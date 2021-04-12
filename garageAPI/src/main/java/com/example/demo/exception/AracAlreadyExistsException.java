package com.example.demo.exception;

public class AracAlreadyExistsException extends RuntimeException{
    public AracAlreadyExistsException(String msg){
        super(msg);
    }
}
