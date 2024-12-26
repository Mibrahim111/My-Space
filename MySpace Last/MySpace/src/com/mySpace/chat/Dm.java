package com.mySpace.chat;


import com.mySpace.user.models.User;

import java.io.Serializable;
import java.util.Scanner;

public class Dm extends Chat implements Serializable {
    private User receiver;
    // Constructor
    public Dm(User user1, User user2) {

        participants.put(user1.getUserName(),user1);
        participants.put(user1.getUserName(), user2);
    }

    public Dm(String user1, String user2) {
    }




    @Override
    public void displayChatInfo() {
        System.out.println("Direct Message between: " +
                participants.get(0).getData().getName() + " and " +
                participants.get(1).getData().getName());
    }

    public void displayMessages() {
        if (messages.isEmpty()) {
            System.out.println("No messages in this chat yet.");
        } else {
            System.out.println("Messages in this Dm:");
            for (Message message : messages) {
                System.out.println(message);
            }
        }
    }


    public void startChat() {
        Scanner scanner = new Scanner(System.in);
        boolean chatActive = true;

        System.out.println("Welcome to the Dm! Type 'exit' to end the chat.");
        displayChatInfo();

        while (chatActive) {
            // Ask for username
            System.out.println("Enter your username (" + participants.get(0).getData().getName() + " or " +
                    participants.get(1).getData().getName() + "):");
            String username = scanner.nextLine();

            // Validate the sender
            User sender = null;
            for (User user : participants.values()) {
                if (user.getData().getName().equalsIgnoreCase(username)) {
                    sender = user;
                    break;
                }
            }
            if (sender == null) {
                System.out.println("Invalid username! Try again.");
                continue;
            }

            // Get the message
            System.out.println(sender.getData().getName() + ", enter your message (type 'exit' to quit):");
            String message = scanner.nextLine();

            if (message.equalsIgnoreCase("exit")) {
                chatActive = false;
                break;
            }

            // Send the message
            sendMessageFromUser(sender, message);

            // Display updated chat messages

        }

        System.out.println("Chat session ended.");
    }


    public void sendMessageFromUser(User sender, String content) {
        if (participants.containsKey(sender.getUserName())) {
            Message message = new Message(content, sender);
            messages.add(message);
        } else {
            System.out.println(sender.getData().getName() + " is not part of this Dm.");
        }
    }



}

