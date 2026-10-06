package com.ecoroute.util;

import java.util.Scanner;

/**
 * Shared console I/O helpers: validated input reading and consistent
 * banner / divider printing for the terminal dashboard.
 */
public final class ConsoleUtil {

    private static final Scanner SCANNER = new Scanner(System.in);

    private ConsoleUtil() {
    }

    public static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = SCANNER.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value < min || value > max) {
                    System.out.printf("Please enter a number between %d and %d.%n", min, max);
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid number, please try again.");
            }
        }
    }

    public static String readNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = SCANNER.nextLine().trim();
            if (!line.isEmpty()) {
                return line;
            }
            System.out.println("Input cannot be empty.");
        }
    }

    public static String readLocationId(String prompt, com.ecoroute.repository.LocationRepository repo) {
        while (true) {
            String id = readNonEmptyString(prompt).toUpperCase();
            if (repo.exists(id)) {
                return id;
            }
            System.out.println("Unknown location id. Type MAP to view the campus map codes, or try again.");
        }
    }

    public static void printHeader(String title) {
        String bar = "=".repeat(Math.max(60, title.length() + 10));
        System.out.println(bar);
        System.out.println("  " + title);
        System.out.println(bar);
    }

    public static void printDivider() {
        System.out.println("-".repeat(70));
    }

    public static void pause() {
        System.out.print("\nPress ENTER to continue...");
        SCANNER.nextLine();
    }
}
