package tables.product;

import java.sql.*;
import java.util.List;
import tables.Table;

/**
 * Class that contains functions to search product information.
 *
 * @author Owen Sailer
 */
public class Inventory extends Table {

    /**
     * List all products
     *
     * @return all products
     */
    public static List<Product> listAllProducts() {
        String query
                = "SELECT product_id FROM product p ";

        ResultSet rs = select(query);

        return mapIDs(rs, Product::fromID);
    }

    /**
     * List all products in a customer's wishlist
     *
     * @return wishlist product
     */
    public static List<Product> listAllProductsInCustomerWishlist() {
        String query
                = "SELECT DISTINCT p.product_id FROM product p "
                + "LEFT JOIN product_category pc ON pc.product_id = p.product_id "
                + "INNER JOIN wishlist_items wi ON p.product_id = wi.product_id";

        ResultSet rs = select(query);

        return mapIDs(rs, Product::fromID);
    }

    /**
     * List all products in a customer's orders
     *
     * @return customer order products
     */
    public static List<Product> listAllProductsInCustomerOrders() {
        String query
                = "SELECT DISTINCT p.product_id FROM product p "
                + "INNER JOIN order_item oi ON p.product_id = oi.product_id "
                + "INNER JOIN customer_order co ON oi.order_id = co.order_id";

        ResultSet rs = select(query);

        return mapIDs(rs, Product::fromID);
    }

    /**
     * Find the most purchased product
     *
     * @return most purchased product
     */
    public static Product mostPurchasedProduct() {
        String query
                = "SELECT DISTINCT p.product_id, COUNT(oi.product_id) AS purchase_count FROM product p "
                + "JOIN order_item oi ON p.product_id = oi.product_id "
                + "GROUP BY p.product_id "
                + "ORDER BY purchase_count DESC LIMIT 1";

        try {
            ResultSet rs = select(query);

            return mapIDs(rs, Product::fromID).get(0);
        } catch (RuntimeException ex) {
        }

        return null;
    }

    /**
     * List all products in a specified price range
     *
     * @param minPrice min price
     * @param maxPrice max price
     * @return all product in range
     */
    public static List<Product> listAllProductsInPriceRange(double minPrice, double maxPrice) {
        String query
                = "SELECT p.product_id FROM product p "
                + "WHERE current_unit_price BETWEEN ? AND ? "
                + "ORDER BY current_unit_price, product_id";

        ResultSet rs = select(query, minPrice, maxPrice);

        return mapIDs(rs, Product::fromID);
    }

    /**
     * List all products in a specified category
     *
     * @param categoryId category id
     * @return all product in category
     */
    public static List<Product> listAllProductsInCategory(int categoryId) {
        String query
                = "SELECT p.product_id FROM product p "
                + "INNER JOIN product_category pc ON p.product_id = pc.product_id "
                + "WHERE pc.category_id = ?";

        ResultSet rs = select(query, categoryId);

        return mapIDs(rs, Product::fromID);
    }

    /**
     * Find the most expensive product
     *
     * @return mot expensive product
     */
    public static Product mostExpensiveProduct() throws RuntimeException {
        String query = "SELECT product_id FROM product ORDER BY current_unit_price DESC LIMIT 1";

        try {
            ResultSet rs = select(query);

            return mapIDs(rs, Product::fromID).get(0);
        } catch (RuntimeException ex) {
        }

        return null;
    }

    // Find the highest product in stock (with the most quantity)
    public static Product highestProductInStock() throws RuntimeException {
        String query = "SELECT product_id FROM product ORDER BY product_quantity DESC LIMIT 1";

        try {
            ResultSet rs = select(query);

            return mapIDs(rs, Product::fromID).get(0);
        } catch (RuntimeException ex) {
        }

        return null;
    }
}
