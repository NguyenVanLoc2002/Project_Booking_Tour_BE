package com.fit.tourservice.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class ValidateException extends RuntimeException {
    private final String code;
    private final Map<String, String> messageMap;
    private final HttpStatus httpStatus;


}
