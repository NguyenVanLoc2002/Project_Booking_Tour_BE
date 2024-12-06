package com.fit.commonservice.common;

import lombok.*;
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
