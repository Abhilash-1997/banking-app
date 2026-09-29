package com.novabank.config.web;

import com.novabank.shared.api.error.NovaBankException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;

@RestControllerAdvice
class NovaBankExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NovaBankException.class)
    ProblemDetail handleNovaBank(NovaBankException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(
                HttpStatus.valueOf(ex.status()), ex.getMessage());
        detail.setType(URI.create(ex.type()));
        detail.setTitle("NovaBank error");
        return detail;
    }
}
