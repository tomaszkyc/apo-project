package com.tomaszkyc.app.core.exception;

public class NotYetImplementedException extends BaseException {
    public NotYetImplementedException(String msg) {
        super("Not yet implemented. Details: " + msg);
    }
}
