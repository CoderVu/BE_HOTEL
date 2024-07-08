package com.example.booking_hotel.exception;

import java.sql.SQLException;

public class PhotoRetrievalException extends Throwable {
    public PhotoRetrievalException(String message) {
        super(message);
    }

    public PhotoRetrievalException(String errorRetrievingPhoto, SQLException e) {
        super(errorRetrievingPhoto, e);
    }
}
