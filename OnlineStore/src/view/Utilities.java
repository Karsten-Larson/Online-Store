package view;

import java.util.List;
import java.util.Scanner;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Class that will handle all command line interactions
 *
 * @author karsten
 */
public class Utilities {

    private static Scanner scanner = new Scanner(System.in);

    public static Scanner getScanner() {
        return scanner;
    }

    public static int getInt(String message, String errorMessage, Predicate<Integer> validation) {
        while (true) {
            System.out.print(message);

            try {
                int input = scanner.nextInt();

                // Determines if the input passes
                if (!validation.test(input)) {
                    System.out.println(errorMessage);
                    continue;
                }

                return input;
            } catch (Exception ex) {
                System.out.println(errorMessage);
            }
        }
    }

    public static int getInt(String message, int minValue, int maxValue) {
        if (minValue > maxValue) {
            throw new IllegalArgumentException("MinValue must be less than MaxValue");
        }

        return getInt(message,
                "Inputted value must be in range [" + minValue + ", " + maxValue + "]",
                ((t) -> minValue <= t && t <= maxValue));
    }

    public static <T> T selectItem(List<T> items, Function<T, String> map) {
        int counter = 1;

        for (T item : items) {
            System.out.println(counter + ": " + map.apply(item));
            counter++;
        }

        return items.get(getInt("Select an item: ", 1, items.size()) - 1);
    }

    public static <T> T selectItem(List<T> items) {
        return selectItem(items, (o) -> o.toString());
    }
}
