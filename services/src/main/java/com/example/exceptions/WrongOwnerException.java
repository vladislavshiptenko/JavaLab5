package com.example.exceptions;

public class WrongOwnerException extends Exception {
    public WrongOwnerException(String message) {
        super(message);
    }
}
