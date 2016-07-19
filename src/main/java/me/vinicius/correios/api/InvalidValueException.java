package me.vinicius.correios.api;

class InvalidValueException extends RuntimeException{

    @SuppressWarnings("unused")
    public InvalidValueException(){
        super();
    }
    InvalidValueException(String message){
        super(message);
    }
}
