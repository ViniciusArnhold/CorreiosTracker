package me.vinicius.correios.api;

class InvalidValueException extends RuntimeException {

    @SuppressWarnings({"WeakerAccess", "unused"})
    public InvalidValueException() {
        super();
    }

    public InvalidValueException(String message) {
        super(message);
    }
}

