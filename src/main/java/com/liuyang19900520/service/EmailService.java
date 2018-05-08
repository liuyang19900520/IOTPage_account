package com.liuyang19900520.service;

public interface EmailService {

    void sendSimpleMessage( String to, String subject, String text);

    void sendMessageWithAttachment(
            String to, String subject, String text, String pathToAttachment);
}
