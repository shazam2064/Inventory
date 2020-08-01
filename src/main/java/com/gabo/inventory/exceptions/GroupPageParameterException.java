package com.gabo.inventory.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "One of the page or the size filter were not provided")
public class GroupPageParameterException extends RuntimeException {
}
