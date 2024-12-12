package view;

import java.util.List;
import java.util.Scanner;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
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

    public static void selectAction(List<String> names, List<Runnable> actions) {
        if (names.size() != actions.size()) {
            throw new IllegalArgumentException("Name must be one-to-one to actions");
        }

        int counter = 1;

        for (String name : names) {
            System.out.println(counter + ": " + name);
            counter++;
        }

        actions.get(getInt("Select an action: ", 1, names.size()) - 1).run();
    }
    
    public static <T> void selectAction(List<String> names, List<Consumer<T>> actions, T param) {
        if (names.size() != actions.size()) {
            throw new IllegalArgumentException("Name must be one-to-one to actions");
        }

        int counter = 1;

        for (String name : names) {
            System.out.println(counter + ": " + name);
            counter++;
        }

        actions.get(getInt("Select an action: ", 1, names.size()) - 1).accept(param);
    }
    
    public static <K, V> void selectAction(List<String> names, List<BiConsumer<K, V>> actions, K param1, V param2) {
        if (names.size() != actions.size()) {
            throw new IllegalArgumentException("Name must be one-to-one to actions");
        }

        int counter = 1;

        for (String name : names) {
            System.out.println(counter + ": " + name);
            counter++;
        }

        actions.get(getInt("Select an action: ", 1, names.size()) - 1).accept(param1, param2);
    }

    public static <T> T selectItem(List<T> items, Function<T, String> map, String message) {
        int counter = 1;

        for (T item : items) {
            System.out.println(counter + ": " + map.apply(item));
            counter++;
        }
        
        int index = getInt(message, 0, items.size());
        
        if (index == 0) return null;

        return items.get(index - 1);
    }

    public static <T> T selectItem(List<T> items, Function<T, String> map) {
        return selectItem(items, map, "Select an item: ");
    }

    public static <T> T selectItem(List<T> items) {
        return selectItem(items, (o) -> o.toString());
    }
}
