package com.example.exceptions;

public class WrongCatIdException extends Exception {
    public WrongCatIdException(String message) {
        super(message);
    }
}
