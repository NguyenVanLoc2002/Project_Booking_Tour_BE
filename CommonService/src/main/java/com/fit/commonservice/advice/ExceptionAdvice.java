package com.fit.commonservice.advice;

import com.fit.commonservice.common.CommonException;
import com.fit.commonservice.common.ErrorMessage;
import com.fit.commonservice.common.ValidateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class ExceptionAdvice {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleException(Exception ex) {
        log.error("Unknown internal server error: "+ex.getMessage());
        log.error("Exception class: "+ex.getClass());
        log.error("Exception cause: "+ex.getCause());
        return new ResponseEntity<>(new ErrorMessage("9999",ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CommonException.class)
    public ResponseEntity<ErrorMessage> handleCommonException(CommonException e) {
        log.error(String.format("Common error: %s %s %s", e.getCode(), e.getHttpStatus(), e.getMessage()));
        return new ResponseEntity<>(new ErrorMessage(e.getCode(), e.getMessage(), e.getHttpStatus()),e.getHttpStatus());
    }

    @ExceptionHandler(ValidateException.class)
    public ResponseEntity<Map<String, String>> handleValidateException(ValidateException e) {
        log.error(String.format("Validation error: %s %s %s", e.getCode(), e.getHttpStatus(), e.getMessage()));
        return new ResponseEntity<>(e.getMessageMap(), e.getHttpStatus());
    }
}
