package com.example.demo.global.exception;

import lombok.Getter;

@Getter
public class BookException extends RuntimeException{
    private final int statusCode;

    public BookException(int statusCode, String message){
        super(message);
        this.statusCode = statusCode;
    }

}
