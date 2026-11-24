package com.joysistvi.recordingapp.view;

import com.joysistvi.recordingapp.model.User;
import com.joysistvi.recordingapp.service.UserService;

import java.util.List;
import java.util.Scanner;

public class UserView {
    private final UserService userService;
    private final Scanner scanner;

    public UserView(UserService userService) {
        this.userService = userService;
        this.scanner = new Scanner(System.in);
    }

    public void displayAllUsers() {
        List<User> users = userService.getAllActiveUsers();
        printUserTable("Active Users", users);
    }

    public void promptCreateUser() {
        System.out.print("\nEnter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter password (min 6 chars): ");
        String password = scanner.nextLine();

        if (userService.createUser(username, password, email)) {
            System.out.println("User registered successfully!");
            displayAllUsers();
        }
    }

    public void promptUpdateUser() {
        displayAllUsers();
        Integer id = readInt("\nEnter User ID to update: ");
        if (id == null) {
            return;
        }
        System.out.print("Enter new username: ");
        String username = scanner.nextLine();
        System.out.print("Enter new email: ");
        String email = scanner.nextLine();

        if (userService.updateUser(id, username, email)) {
            System.out.println("User ID " + id + " updated successfully!");
            displayAllUsers();
        } else if (id > 0 && username != null && !username.trim().isEmpty()
                && email != null && email.contains("@") && email.contains(".")) {
            System.out.println("No active user found with ID " + id + ".");
        }
    }

    public void promptDeleteUser() {
        displayAllUsers();
        Integer id = readInt("\nEnter User ID to delete permanently: ");
        if (id == null) {
            return;
        }

        if (userService.deleteUser(id)) {
            System.out.println("User ID " + id + " deleted successfully!");
            displayAllUsers();
        } else if (id > 0) {
            System.out.println("Could not delete User ID " + id + " (not found or owns playlists).");
        }
    }

    public void promptArchiveUser() {
        displayAllUsers();
        Integer id = readInt("\nEnter User ID to archive: ");
        if (id == null) {
            return;
        }

        if (userService.archiveUser(id)) {
            System.out.println("User ID " + id + " archived successfully!");
            displayAllUsers();
        } else if (id > 0) {
            System.out.println("No active user found with ID " + id + ".");
        }
    }

    public void promptRestoreUser() {
        List<User> archivedUsers = userService.getAllArchivedUsers();
        if (archivedUsers.isEmpty()) {
            System.out.println("\nNo archived users available to restore.");
            return;
        }

        printUserTable("Archived Users", archivedUsers);
        Integer id = readInt("\nEnter Archived User ID to restore: ");
        if (id == null) {
            return;
        }

        if (userService.restoreUser(id)) {
            System.out.println("User ID " + id + " restored successfully!");
            displayAllUsers();
        } else if (id > 0) {
            System.out.println("No archived user found with ID " + id + ".");
        }
    }

    public void promptSearchUsers() {
        System.out.print("\nEnter username search keyword: ");
        String keyword = scanner.nextLine();

        List<User> results = userService.searchUsers(keyword);
        if (keyword != null && !keyword.trim().isEmpty()) {
            printUserTable("Search Results: \"" + keyword.trim() + "\"", results);
        }
    }

    public void promptLoginUser() {
        System.out.print("\nEnter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        User loggedInUser = userService.loginUser(username, password);
        if (loggedInUser != null) {
            System.out.println("Login successful! Welcome, "
                    + loggedInUser.getUsername() + " (" + loggedInUser.getRole() + ").");
        }
    }

    private void printUserTable(String title, List<User> users) {
        System.out.println("\n----- " + title + " -----");
        if (users.isEmpty()) {
            System.out.println("No records found.");
            return;
        }
        System.out.printf("%-4s %-18s %-25s %-8s%n", "ID", "Username", "Email", "Role");
        for (User user : users) {
            System.out.printf("%-4d %-18s %-25s %-8s%n",
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole());
        }
    }

    private Integer readInt(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input: Please enter a valid integer.");
            return null;
        }
    }
}