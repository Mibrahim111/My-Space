package com.mySpace.user.models;

public enum Gender {
    MALE, FEMALE;


    public static void displayOptions() {
        for (Gender gender : Gender.values()) {
            System.out.println(gender.ordinal() + 1 + ". " + gender);
        }
    }

    // Method to get a Gender value from an integer input
    public static Gender fromInt(int option) {
        if (option > 0 && option <= Gender.values().length) {
            return Gender.values()[option - 1];
        }
        throw new IllegalArgumentException("Invalid option. Please choose a valid number.");
    }

    public static Gender fromString(String gender) {
        for (Gender g : Gender.values()) {
            if (g.name().equalsIgnoreCase(gender)) {
                return g;
            }
        }
        throw new IllegalArgumentException("Invalid gender value: " + gender);
    }
}
