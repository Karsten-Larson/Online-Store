package view;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import tables.customer.Customer;
import tables.order.Order;
import tables.order.OrderItem;
import tables.product.Inventory;
import tables.product.Product;

/**
 * Class that handles all the interactive Menus
 *
 * @author karsten
 */
public class Menus {

    public static void homeMenu() {
        List<String> names = new ArrayList<>();
        List<Runnable> actions = new ArrayList<>();

        names.add("Customer View");
        actions.add(Menus::customerView);
        names.add("Distributor View");
        actions.add(Menus::distributorView);
        names.add("Create Customer");
        actions.add(Menus::createCustomer);
        names.add("Create Distributor");
        actions.add(Menus::createDistributor);
        names.add("Exit Program");
        actions.add(() -> System.out.println("Exiting program"));

        Utilities.selectAction(names, actions);
    }

    public static void customerView() {
        Function<Customer, String> map = (c) -> c.getFirstName() + " " + c.getLastName();
        Customer customer = Utilities.selectItem(Customer.getAllCustomers(), map, "Select a customer to login as (0 to exit): ");

        if (customer == null) {
            homeMenu();
            return;
        }

        List<String> names = new ArrayList<>();
        List<Consumer<Customer>> actions = new ArrayList<>();

        names.add("View Products");
        actions.add(Menus::customerViewProducts);
        names.add("View Orders");
        actions.add(Menus::customerViewOrders);
        names.add("View Wishlists");
        actions.add(Menus::customerViewWishlists);
        names.add("Back to Home");
        actions.add((c) -> homeMenu());

        Utilities.selectAction(names, actions, customer);
    }

    public static void customerViewProducts(Customer customer) {
        Function<Product, String> map = (p) -> p.getName() + " - $" + p.getCurrentUnitPrice();
        Product product = Utilities.selectItem(Inventory.listAllProducts(), map, "Select a product to view (0 to exit): ");

        if (product == null) {
            customerView();
            return;
        }

        customerDetailedViewProducts(customer, product);
    }

    public static void customerDetailedViewProducts(Customer customer, Product product) {
        System.out.println("Product ID: " + product.getID());
        System.out.println("Product Name: " + product.getName());
        System.out.println("Unit Price: " + product.getCurrentUnitPrice());
        System.out.println("Description: " + product.getDescription());
        System.out.println("Quantity In Stock: " + product.getQuantity());
        System.out.println("Distributor ID: " + product.getDistributorId() + "\n");

        List<String> names = new ArrayList<>();
        List<BiConsumer<Customer, Product>> actions = new ArrayList<>();

        names.add("Add to Order");
        actions.add(Menus::addToOrder);
        names.add("Add to Wishlist");
        actions.add(Menus::addToWishlist);
        names.add("Back to Home Menu");
        actions.add((c, p) -> homeMenu());

        Utilities.selectAction(names, actions, customer, product);
    }

    public static void addToOrder(Customer customer, Product product) {
        if (product.getQuantity() == 0) {
            System.out.println("Cannot add a product with none in stock");
            customerDetailedViewProducts(customer, product);
        }

        Function<Order, String> map = (o) -> "ID: " + o.getOrderId() + "; Total Price: " + o.getTotalPrice();
        Order order = Utilities.selectItem(customer.getOrders(), map, "Select an order to add an item to (0 to exit): ");

        if (order == null) {
            customerDetailedViewProducts(customer, product);
            return;
        }

        order.addItem(product, Utilities.getInt("Enter desired quantity [1, " + product.getQuantity() + "]: ", 1, product.getQuantity()));
        System.out.println("Item successfully added");

        customerDetailedViewProducts(customer, product);
    }

    public static void addToWishlist(Customer customer, Product product) {
        throw new UnsupportedOperationException("This method is not implemented yet.");
    }

    public static void customerViewOrders(Customer customer) {
        Function<Order, String> map = (o) -> "ID: " + o.getOrderId() + "; Total Price: " + o.getTotalPrice();
        Order order = Utilities.selectItem(customer.getOrders(), map, "Select an order to view (0 to exit): ");

        if (order == null) {
            customerView();
            return;
        }

        System.out.println("Order ID: " + order.getOrderId());
        System.out.println("Order Date: " + order.getOrderDate());
        System.out.println("Order Total Price: " + order.getTotalPrice());
        System.out.println("Items");
        for (OrderItem item : order.getItems()) {
            System.out.println("- Name: " + item.getProduct().getName() + "; Quantity Ordered: " + item.getQuantity() + "; Unit Price: " + item.getUnitPrice());
        }

        customerView();
    }

    public static void customerViewWishlists(Customer customer) {
        throw new UnsupportedOperationException("This method is not implemented yet.");
    }

    public static void distributorView() {
        throw new UnsupportedOperationException("This method is not implemented yet.");
    }

    public static void createCustomer() {
        throw new UnsupportedOperationException("This method is not implemented yet.");
    }

    public static void createDistributor() {
        throw new UnsupportedOperationException("This method is not implemented yet.");
    }
}
