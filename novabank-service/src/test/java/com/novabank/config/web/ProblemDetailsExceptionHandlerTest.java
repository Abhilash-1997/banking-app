package com.novabank.config.web;

import com.novabank.shared.api.error.NovaBankException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import static org.assertj.core.api.Assertions.assertThat;

class ProblemDetailsExceptionHandlerTest {

    @Test
    void mapsNovaBankExceptionToProblemDetail() {
        ProblemDetailsExceptionHandler handler = new ProblemDetailsExceptionHandler();
        NovaBankException ex = new NovaBankException(
                "https://novabank.example/problems/insufficient-funds",
                409,
                "Insufficient funds");

        ProblemDetail detail = handler.handleNovaBank(ex);

        assertThat(detail.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(detail.getDetail()).isEqualTo("Insufficient funds");
        assertThat(detail.getType()).hasToString("https://novabank.example/problems/insufficient-funds");
    }
}
