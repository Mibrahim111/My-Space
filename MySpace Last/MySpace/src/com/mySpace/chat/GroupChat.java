package com.mySpace.chat;



import com.mySpace.user.models.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GroupChat extends Chat implements Serializable {
    private String groupName;
    private List<User> admins;
    private List<User> members;

    // Constructor
    public GroupChat(String groupName, User loggedInUser, List<User> selectedUsers) {
        this.groupName = groupName;  // Here the groupName parameter is being assigned to the instance variable
        this.admins = new ArrayList<>();
        this.members = new ArrayList<>();

        // Add loggedInUser as an admin
        this.admins.add(loggedInUser);

        // Add loggedInUser and the selected users to the members list
        this.members.add(loggedInUser);  // Add the logged-in user to the members list
        this.members.addAll(selectedUsers);  // Add the selected users to the members list
    }


    // Getters and Setters
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<User> getAdmins() {
        return admins;
    }

    public List<User> getMembers() {
        return members;
    }

    // Method to add a participant to the group
    public void addParticipant(User user) {
        if (!members.contains(user)) {
            members.add(user);
            System.out.println(user.getData().getName() + " has been added to the group chat.");
        } else {
            System.out.println(user.getData().getName() + " is already a participant in the group chat.");
        }
    }

    // Method to add a participant as an admin
    public void addAdmin(User user) {
        if (members.contains(user)) {
            if (!admins.contains(user)) {
                admins.add(user);
                System.out.println(user.getData().getName() + " has been added as an admin.");
            } else {
                System.out.println(user.getData().getName() + " is already an admin.");
            }
        } else {
            System.out.println(user.getData().getName() + " is not a member of the group chat.");
        }
    }

    // Method to remove a participant from the group
    public void removeParticipant(User user) {
        if (members.contains(user)) {
            members.remove(user);
            System.out.println(user.getData().getName() + " has been removed from the group chat.");
        } else {
            System.out.println(user.getData().getName() + " is not a participant in the group chat.");
        }
    }

    // Method to remove an admin from the group
    public void removeAdmin(User user) {
        if (admins.contains(user)) {
            admins.remove(user);
            System.out.println(user.getData().getName() + " has been removed as an admin.");
        } else {
            System.out.println(user.getData().getName() + " is not an admin.");
        }
    }

    // Method to display information about the group chat
    @Override
    public void displayChatInfo() {
        System.out.println("Group Chat: " + groupName);
        System.out.println("Admins:");
        for (User admin : admins) {
            System.out.println("- " + admin.getData().getName());
        }
        System.out.println("Participants:");
        for (User member : members) {
            System.out.println("- " + member.getData().getName());
        }
    }

    // Method to get the number of members (including admins)
    public int getNumberOfMembers() {
        return this.members.size();
    }

    // Method to check if a user is an admin
    public boolean isAdmin(User user) {
        return admins.contains(user);
    }

    // Method to check if a user is a member (non-admin participant)
    public boolean isMember(User user) {
        return members.contains(user) && !admins.contains(user);
    }

    @Override
    public String toString() {
        return this.groupName;  // Return the group name as the string representation
    }

    public void addMessage(Message message) {
        // Add the new message to the group’s message list
        this.messages.add(message);
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

}
