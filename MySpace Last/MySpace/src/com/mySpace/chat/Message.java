package com.mySpace.chat;

import com.mySpace.user.models.User;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message implements Serializable {

    private String content;       // text or media
    private User sender;       // the sender
    private LocalDateTime timestamp;  // the date and the time

    public Message(String content, User sender) {
        this.content = content;
        this.sender = sender;
        this.timestamp = LocalDateTime.now(); // Automatically set the current timestamp
    }

    private String generateTimestamp() {
        // Generate the current timestamp in the desired format
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy\\MM\\dd - H : mm");
        return now.format(formatter);
    }

    // Getters
    public String getContent() {
        return content;
    }

    public User getSender() {
        return sender;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    // To display the message in a readable format
    @Override
    public String toString() {
        return "[" + timestamp + "] " + sender.getData().getName() + ": " + content;
    }
}
