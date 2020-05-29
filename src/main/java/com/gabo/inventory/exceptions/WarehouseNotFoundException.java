package com.gabo.inventory.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class WarehouseNotFoundException extends RuntimeException {

    public WarehouseNotFoundException(String id) {

        super("There was no warehouse with id: " + id);
    }

}
