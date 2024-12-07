package com.fit.userservice.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
@Getter
@Setter
public class CommonException extends RuntimeException{
    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    public CommonException(String code, String message, HttpStatus httpStatus) {
        super(message);
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }


}
