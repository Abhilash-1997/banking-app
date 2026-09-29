package com.novabank.shared.api.error;

/**
 * Base type for domain errors that map to RFC 7807 problem details.
 */
public class NovaBankException extends RuntimeException {

    private final String type;
    private final int status;

    public NovaBankException(String type, int status, String message) {
        super(message);
        this.type = type;
        this.status = status;
    }

    public String type() {
        return type;
    }

    public int status() {
        return status;
    }
}
