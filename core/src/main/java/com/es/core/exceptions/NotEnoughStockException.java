package com.es.core.exceptions;

public class NotEnoughStockException extends RuntimeException{

    public NotEnoughStockException(String message) {
        super(message);
    }

    public NotEnoughStockException() {
        super();
    }
}
