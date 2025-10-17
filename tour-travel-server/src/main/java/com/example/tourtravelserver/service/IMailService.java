package com.example.tourtravelserver.service;

import com.example.tourtravelserver.entity.Booking;

public interface IMailService {
    void sendBookingConfirmation(Booking booking, String txnRef) throws Exception;
}
