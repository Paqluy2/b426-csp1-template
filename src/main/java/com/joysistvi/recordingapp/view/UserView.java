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

    public void handleMenu(User currentUser) {
        if (currentUser == null || !currentUser.isAdmin()) {
            System.out.println("\nAccess Denied: Admin privileges are required for User Management.");
            return;
        }

        while (true) {
            System.out.println("\n=== USER MANAGEMENT MENU ===");
            System.out.println("1. View All Active");
            System.out.println("2. Create / Register");
            System.out.println("3. Update");
            System.out.println("4. Search");
            System.out.println("5. Archive (Soft Delete)");
            System.out.println("6. Restore Archived");
            System.out.println("7. Delete (Permanent)");
            System.out.println("0. Back");

            int choice = promptForInt("Choose an option: ");
            switch (choice) {
                case 1 -> displayAllUsers();
                case 2 -> promptCreateUser();
                case 3 -> promptUpdateUser();
                case 4 -> promptSearchUsers();
                case 5 -> promptArchiveUser();
                case 6 -> promptRestoreUser();
                case 7 -> promptDeleteUser();
                case 0 -> {
                    return;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
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
        }
    }

    public void promptUpdateUser() {
        displayAllUsers();
        int id = promptForInt("\nEnter User ID to update: ");
        System.out.print("Enter new username: ");
        String username = scanner.nextLine();
        System.out.print("Enter new email: ");
        String email = scanner.nextLine();

        if (userService.updateUser(id, username, email)) {
            System.out.println("User ID " + id + " updated successfully!");
            displayAllUsers();
        }
    }

    public void promptSearchUsers() {
        System.out.print("\nEnter username search keyword: ");
        String keyword = scanner.nextLine();

        List<User> results = userService.searchUsers(keyword);
        if (!results.isEmpty()) {
            printUserTable("Search Results", results);
        } else if (keyword != null && !keyword.trim().isEmpty()) {
            System.out.println("No active users found matching \"" + keyword.trim() + "\".");
        }
    }

    public void promptArchiveUser() {
        displayAllUsers();
        int id = promptForInt("\nEnter User ID to archive: ");

        if (userService.archiveUser(id)) {
            System.out.println("User ID " + id + " archived successfully!");
            displayAllUsers();
        }
    }

    public void promptRestoreUser() {
        List<User> archivedUsers = userService.getAllArchivedUsers();
        if (archivedUsers.isEmpty()) {
            System.out.println("\nNo archived users available to restore.");
            return;
        }

        printUserTable("Archived Users", archivedUsers);
        int id = promptForInt("\nEnter Archived User ID to restore: ");

        if (userService.restoreUser(id)) {
            System.out.println("User ID " + id + " restored successfully!");
            displayAllUsers();
        }
    }

    public void promptDeleteUser() {
        displayAllUsers();
        int id = promptForInt("\nEnter User ID to delete permanently: ");

        if (userService.deleteUser(id)) {
            System.out.println("User ID " + id + " deleted successfully!");
            displayAllUsers();
        }
    }

    public User promptLoginUser() {
        System.out.print("\nEnter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        User loggedInUser = userService.loginUser(username, password);
        if (loggedInUser != null) {
            System.out.println("Login successful! Welcome, "
                    + loggedInUser.getUsername() + " (" + loggedInUser.getRole() + ").");
        }
        return loggedInUser;
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

    private int promptForInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
            }
        }
    }
}