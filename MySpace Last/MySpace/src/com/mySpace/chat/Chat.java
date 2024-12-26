package com.mySpace.chat;



import com.mySpace.user.models.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class Chat implements Serializable {
    private static final long serialVersionUID = 1L;
    protected String name; // Add a name for the chat
    protected ArrayList<Message> messages;
    protected Map<String, User> participants;
    protected int ID;
    private static int numOfChats = 0;

    public Chat() {
        messages = new ArrayList<>();
        participants = new HashMap<>();
        numOfChats++;
        ID = numOfChats;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public static int getNumOfChats() {
        return numOfChats;
    }

    public String getName() {
        return name;
    }


    public static void setNumOfChats(int numOfChats) {
        Chat.numOfChats = numOfChats;
    }

    public void sendMessage(User sender, String content) {
        if (participants.containsKey(sender.getUserName())) {
            Message message = new Message(content, sender);
            messages.add(message);
        } else {
            System.out.println("Sender is not part of this chat.");
        }
    }

    @Override
    public Chat clone() {
        try {
            return (Chat) super.clone(); // Perform shallow copy
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // Should not happen since we implement Cloneable
        }
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public Map<String, User> getParticipants() {
        return participants;
    }

    public void setParticipants(Map<String, User> participants) {
        this.participants = participants;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Check if the user is part of the chat by username
    public boolean isParticipant(User user) {
        return participants.containsKey(user.getUserName());
    }

    public abstract void displayChatInfo();

    public void addMessage(Message message) {
        // Add the new message to the group’s message list
        this.messages.add(message);
    }
}
