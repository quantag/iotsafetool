package org.iotsafe.exception;

public class CardletException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CardletException(String message) {
        super(message);
    }
    
    public CardletException(Exception e) {
        super(e);
    }

}
