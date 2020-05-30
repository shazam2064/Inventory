package com.gabo.inventory.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UnitNotFoundException extends RuntimeException {

    public UnitNotFoundException(String id) {

        super("There was no unit with id: " + id);
    }

}
