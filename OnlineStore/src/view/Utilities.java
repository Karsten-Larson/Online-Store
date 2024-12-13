package view;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import tables.address.Address;
import tables.address.AddressType;
import tables.customer.Customer;
import tables.distributor.Distributor;
import tables.order.Order;
import tables.order.OrderItem;
import tables.payment.PaymentInfo;
import static view.Mappings.*;

/**
 * Class that will handle all command line interactions
 *
 * @author karsten
 */
public class Utilities {

    private static final Scanner scanner = new Scanner(System.in);
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX
            = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern VALID_PHONE_NUMBER_REGEX
            = Pattern.compile("\\d{10}|(?:\\d{3}-){2}\\d{4}|\\(\\d{3}\\)\\d{3}-?\\d{4}");
    private static final Pattern VALID_DATE_REGEX
            = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");

    public static Scanner getScanner() {
        return scanner;
    }

    public static String getString(String message, String errorMessage, Predicate<String> validation) {
        while (true) {
            System.out.print(message);

            try {
                String input = scanner.nextLine();

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

    public static String getString(String message, int maxChars) {
        return getString(message,
                "Too many characters. Input must be " + maxChars + " or less characters.",
                t -> t.length() <= maxChars);
    }

    public static String getString(String message, int maxChars, String formattingMessage, Predicate<String> validation) {
        return getString(message,
                "Too many characters. Input must be " + maxChars + " or less characters and " + formattingMessage,
                t -> t.length() <= maxChars && validation.test(t));

    }

    public static double getDouble(String message, String errorMessage, Predicate<Double> validation) {
        while (true) {
            System.out.print(message);

            try {
                double input = scanner.nextInt();
                scanner.nextLine();

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

    public static double getDouble(String message, double minValue) {
        return getInt(message,
                "Inputted value must be in range at least " + minValue + ".",
                ((t) -> minValue <= t));
    }

    public static double getDouble(String message, double minValue, double maxValue) {
        if (minValue > maxValue) {
            throw new IllegalArgumentException("MinValue must be less than MaxValue");
        }

        return getInt(message,
                "Inputted value must be in range [" + minValue + ", " + maxValue + "]",
                ((t) -> minValue <= t && t <= maxValue));
    }

    public static int getInt(String message, String errorMessage, Predicate<Integer> validation) {
        while (true) {
            System.out.print(message);

            try {
                int input = scanner.nextInt();
                scanner.nextLine();

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

    public static int getInt(String message, int minValue) {
        return getInt(message,
                "Inputted value must be at least " + minValue + ".",
                ((t) -> minValue <= t));
    }

    public static boolean getConfirmation() {
        return getConfirmation("Do you want to continue (y/n): ");
    }

    public static boolean getConfirmation(String message) {
        String input = getString(message, "Response must be (y/n).", t -> "yn".contains(t.toLowerCase()));

        return input.equalsIgnoreCase("y");
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

        if (index == 0) {
            return null;
        }

        return items.get(index - 1);
    }

    public static <T> T selectItem(List<T> items, Function<T, String> map) {
        return selectItem(items, map, "Select an item: ");
    }

    public static <T> T selectItem(List<T> items) {
        return selectItem(items, (o) -> o.toString());
    }

    private static Address createGenericAddress() {
        return Address.createAddress(
                getString("Street: ", 100),
                getString("City: ", 50),
                getString("State: ", 50),
                getString("Zipcode: ", 10),
                getString("Country: ", 50),
                getString("Apartment Number: ", 20),
                new ArrayList<>()
        );
    }

    public static Address createAddress() {
        Address address = createGenericAddress();

        String addressType = getString("Address type (billing, shipping, or both): ", "Must be on of the listed types.",
                t -> t.equalsIgnoreCase("billing") || t.equalsIgnoreCase("shipping") || t.equalsIgnoreCase("both")).toUpperCase();

        if (addressType.equals("BOTH")) {
            address.addType(AddressType.SHIPPING);
            address.addType(AddressType.BILLING);
        } else {
            address.addType(AddressType.valueOf(addressType));
        }

        return address;
    }

    public static Address createDistributorAddress() {
        Address address = createGenericAddress();

        address.addType(AddressType.DISTRIBUTOR);

        return address;
    }

    public static Address createBillingAddress() {
        System.out.println("Billing Address: ");
        Address address = createGenericAddress();

        address.addType(AddressType.BILLING);

        return address;
    }

    public static Address createShippingAddress() {
        System.out.println("Shipping Address: ");
        Address address = createGenericAddress();

        address.addType(AddressType.SHIPPING);

        return address;
    }

    public static void createProduct(Distributor distributor) {
        distributor.addProduct(
                getString("Name: ", 50),
                getString("Description: ", 255),
                getInt("Quantity Avaliable: ", 0),
                getDouble("Unit Price: ", 0)
        );
    }

    public static Customer createCustomer() {
        return Customer.createCustomer(
                getString("First Name: ", 50),
                getString("Last Name: ", 50),
                getString("Email Address: ", 100, "have a valid email address.", t -> VALID_EMAIL_ADDRESS_REGEX.matcher(t).matches()),
                getString("Phone Number: ", 15, "have a valid phone number.", t -> VALID_PHONE_NUMBER_REGEX.matcher(t).matches())
        );
    }

    public static Distributor createDistributor() {
        return Distributor.createDistributor(
                getString("Phone Number: ", 15, "have a valid phne number.", t -> VALID_PHONE_NUMBER_REGEX.matcher(t).matches()),
                createDistributorAddress()
        );
    }

    public static PaymentInfo createPaymentInfo() {
        return PaymentInfo.createPayment(createBillingAddress().getId(), getString("First Name: ", 50), getString("Last Name: ", 50),
                getInt("Card Number: ", 0),
                Date.valueOf(getString("Enter a date yyyy-mm-dd: ",
                        "Invalid date format.",
                        t -> VALID_DATE_REGEX.matcher(t).matches())),
                getInt("CVV: ", 0));

    }

    public static void editOrderItemQuantity(OrderItem orderItem) {
        int quantity = orderItem.getProduct().getQuantity();

        orderItem.setQuantity(getInt("Enter new quantity [0," + quantity + "]: ", 0, quantity));
    }

    public static Order selectOrder(Customer customer) {
        return Utilities.selectItem(customer.getOrders(), MAP_ORDER, "Select an order to add an item to (0 to exit): ");
    }

    public static OrderItem selectOrderItem(Order order) {
        return Utilities.selectItem(order.getItems(), MAP_ORDER_ITEM, "Select an item to delete from the order (0 to exit): ");
    }
}
