package utils;

import java.util.Scanner;

public class InputHelper {
    private static final Scanner scanner = new Scanner(System.in);

    public static int getInteger(String message, int minimum) {
        int value;
        do {
            System.out.print(message);
            while (!scanner.hasNextInt()) {
                System.out.print("Invalid input. Try again: ");
                scanner.next();
            }
            value = scanner.nextInt();
            scanner.nextLine();
        } while (value < minimum);
        return value;
    }

    public static String getText(String message) {
        System.out.print(message);
        return scanner.nextLine();
    }
}
