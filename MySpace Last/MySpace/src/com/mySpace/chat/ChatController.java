package com.mySpace.chat;


import com.mySpace.user.models.User;
import com.utils.filesIO.MapIO;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ChatController {
    private List<Chat> chatList = new ArrayList<>();
    private List<User> allUsers;
    private User messenger;
    private Map<Integer, Chat> chatStorage;
    private static final String DIRECTORY_PATH = "chats/";


    public ChatController(User messenger) {
        ensureDirectoryExists();
        this.allUsers = new ArrayList<>();
        this.chatStorage = new HashMap<>();
        try {
            loadFromFile();
        } catch (IOException e) {
            System.err.println("Failed to load chats: " + e.getMessage());
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                saveToFile();
            } catch (IOException e) {
                System.err.println("Failed to save chats: " + e.getMessage());
            }
        }));
    }


    public List<Chat> getAllChats(User user) {
        return chatStorage.values().stream()
                .filter(chat -> chat.isParticipant(user))
                .collect(Collectors.toList());
    }


    public void addGroupChat(GroupChat groupChat) {
        chatStorage.put(groupChat.getID(), groupChat);
    }


    public Chat getByKey(Integer id) {
        return chatStorage.get(id);
    }

    // Add a chat to storage
    public void addChat(Chat chat) {
        chatStorage.put(chat.getID(), chat);
    }

    public User getMessenger() {
        return messenger;
    }

    public List<User> getAllUsers() {
        return allUsers;
    }

    public Map<Integer, Chat> getChatStorage() {
        return chatStorage;
    }

    private void saveToFile() throws IOException {
        synchronized (chatStorage) {
            MapIO<Integer, Chat> fileHandler = new MapIO<>();
            fileHandler.write(DIRECTORY_PATH, chatStorage);
        }
    }

    private void loadFromFile() throws IOException {
        MapIO<Integer, Chat> fileHandler = new MapIO<>();
        Map<Integer, Chat> loadedData = fileHandler.read(DIRECTORY_PATH);
        synchronized (chatStorage) {
            chatStorage.putAll(loadedData); // Merge loaded data into chatStorage
        }
    }

    public GroupChat createGroupChat(User loggedInUser, String groupName, List<User> selectedUsers) {
        // Create a new GroupChat and assign logged-in user and selected users as members
        GroupChat newGroupChat = new GroupChat("My New Group", loggedInUser, selectedUsers);

        newGroupChat.addParticipant(loggedInUser); // Add the logged-in user as the first participant
        for (User user : selectedUsers) {
            newGroupChat.addParticipant(user); // Add selected users as participants
        }

        newGroupChat.addAdmin(loggedInUser); // Admin can be the logged-in user

        // Add the group chat to chat storage
        chatStorage.put(newGroupChat.getID(), newGroupChat);

        return newGroupChat;
    }

    public Chat getChatWithUser(User sender, User recipient) {
        // Iterate over all chats and check if the user is a participant
        for (Chat chat : chatStorage.values()) {
            if (chat.isParticipant(sender) && chat.isParticipant(recipient)) {
                return chat; // Return the chat if both users are participants
            }
        }
        return null; // Return null if no chat exists
    }

    public void sendMessage(User sender, User recipient, String messageContent) {
        // Validate input
        if (sender == null || recipient == null || messageContent == null || messageContent.trim().isEmpty()) {
            throw new IllegalArgumentException("Sender, recipient, and message content must not be null or empty.");
        }

        // Ensure sender and recipient are not the same user
        if (sender.equals(recipient)) {
            throw new IllegalArgumentException("Sender and recipient cannot be the same.");
        }

        // Retrieve the chat between the sender and recipient, or create a new one
        Chat chat = getOrCreateChat(sender, recipient);

        // Use the `sendMessage` method in the Chat class to add the message
        try {
            chat.sendMessage(sender, messageContent); // Add the message to the chat

            synchronized (chatStorage) {
                addChat(chat); // Ensure the chat is updated in storage
                saveToFile();  // Save the updated storage to file
            }
        } catch (IOException e) {
            System.err.println("Failed to save chat data after sending message: " + e.getMessage());
        }
    }

    public Chat getOrCreateChat(User sender, User recipient) {
        Chat chat = getChatWithUser(sender, recipient);
        if (chat == null) {
            chat = new Dm(sender, recipient); // Assuming Dm is your direct message chat class
            addChat(chat); // Add the new chat to the storage
        }
        return chat;
    }

    public void saveChat(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }

        // Define the file name
        String directoryPath = "chats";
        String fileName = directoryPath + "/chats_" + user.getUserName() + ".dat";

        try {
            // Ensure the directory exists
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                if (directory.mkdir()) {
                    System.out.println("Created directory: " + directoryPath);
                } else {
                    throw new IOException("Failed to create directory: " + directoryPath);
                }
            }

            // Save the chat data
            MapIO<Integer, Chat> fileHandler = new MapIO<>();
            synchronized (chatStorage) {
                fileHandler.write(fileName, chatStorage);
            }
            System.out.println("Chats saved for user: " + user.getUserName());
        } catch (IOException e) {
            System.err.println("Failed to save chats for user " + user.getUserName() + ": " + e.getMessage());
        }
    }

    public void loadChat(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }

        // Define the file name
        String directoryPath = "chats";
        String fileName = directoryPath + "/chats_" + user.getUserName() + ".dat";

        try {
            // Ensure the directory exists (optional check for reading)
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                System.out.println("No chat directory found. Skipping loading chats.");
                return;
            }

            // Load the chat data
            MapIO<Integer, Chat> fileHandler = new MapIO<>();
            Map<Integer, Chat> loadedChats = fileHandler.read(fileName);

            if (loadedChats != null && !loadedChats.isEmpty()) {
                synchronized (chatStorage) {
                    chatStorage.putAll(loadedChats);
                }
                System.out.println("Successfully loaded chats for user: " + user.getUserName());
            } else {
                System.out.println("No saved chats found for user: " + user.getUserName());
            }
        } catch (IOException e) {
            System.err.println("Failed to load chats for user " + user.getUserName() + ": " + e.getMessage());
        }
    }

    private void ensureDirectoryExists() {
        File directory = new File(DIRECTORY_PATH);
        if (!directory.exists()) {
            boolean created = directory.mkdir();
            if (created) {
                System.out.println("Directory created at: " + DIRECTORY_PATH);
            } else {
                System.err.println("Failed to create directory: " + DIRECTORY_PATH);
            }
        }
    }

}
