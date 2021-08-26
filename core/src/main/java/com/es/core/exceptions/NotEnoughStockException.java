package com.es.core.exceptions;

public class NotEnoughStockException extends RuntimeException {

    private Long id;

    public NotEnoughStockException(String message) {
        super(message);
    }

    public NotEnoughStockException(Long id, String message) {
        super(message);
        this.id = id;
    }

    public NotEnoughStockException() {
        super();
    }

    public Long getId() {
        return id;
    }
}
