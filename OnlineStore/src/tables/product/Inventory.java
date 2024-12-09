package tables.product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import tables.DatabaseManager;
/**
 * Class that contains functions to search product information.
 * 
 * @author Owen Sailer
 */
public class Inventory {

    // List all products
    public static List<Product> listAllProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM product";
        
        Statement stmt = DatabaseManager.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery(query);
        
        while (rs.next()) {
            products.add(new Product(
                rs.getInt("product_id"),
                rs.getString("product_name"),
                rs.getString("product_description"),
                rs.getInt("product_quantity"),
                rs.getDouble("current_unit_price"),
                rs.getObject("distributor_id", Integer.class)
            ));
        }
        
        return products;
    }

    // List all products in a customer's wishlist
    public static List<Product> listAllProductsInCustomerWishlist() throws SQLException {
        List<Product> products = new ArrayList<>();
        String query = "SELECT p.product_name, COUNT(wi.product_id) AS product_count FROM product p "
             + "JOIN wishlist_items wi ON p.product_id = wi.product_id "
             + "GROUP BY p.product_name";

        PreparedStatement stmt = DatabaseManager.getConnection().prepareStatement(query);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            products.add(new Product(
                rs.getString("product_name"), 
                rs.getInt("product_count")
            ));
        }

        return products;
    }
    
    // List all products in a customer's orders
    public static List<Product> listAllProductsInCustomerOrders() throws SQLException {
        List<Product> products = new ArrayList<>();
        String query = "SELECT p.product_name, COUNT(oi.product_id) AS product_count FROM product p "
             + "JOIN order_item oi ON p.product_id = oi.product_id "
             + "JOIN customer_order co ON oi.order_id = co.order_id "
             + "GROUP BY p.product_name";

        PreparedStatement stmt = DatabaseManager.getConnection().prepareStatement(query);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Product product = new Product(
                rs.getString("product_name"),
                rs.getInt("product_count")
            );
            products.add(product);
        }

        return products;
    }

    // Find the most purchased product
    public static Product mostPurchasedProduct() throws SQLException {
        String query = "SELECT p.*, COUNT(oi.product_id) AS purchase_count FROM product p "
             + "JOIN order_item oi ON p.product_id = oi.product_id "
             + "GROUP BY p.product_id "
             + "ORDER BY purchase_count DESC LIMIT 1";
        
        Statement stmt = DatabaseManager.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery(query);
        
        if (rs.next()) {
            return new Product(
                rs.getInt("product_id"),
                rs.getString("product_name"),
                rs.getString("product_description"),
                rs.getInt("product_quantity"),
                rs.getDouble("current_unit_price"),
                rs.getObject("distributor_id", Integer.class)
            );
        }
        
        return null;
    }

    // List all products in a specified price range
    public static List<Product> listAllProductsInPriceRange(double minPrice, double maxPrice) throws SQLException {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM product WHERE current_unit_price BETWEEN ? AND ?";
        
        PreparedStatement stmt = DatabaseManager.getConnection().prepareStatement(query);
        stmt.setDouble(1, minPrice);
        stmt.setDouble(2, maxPrice);
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            products.add(new Product(
                rs.getInt("product_id"),
                rs.getString("product_name"),
                rs.getString("product_description"),
                rs.getInt("product_quantity"),
                rs.getDouble("current_unit_price"),
                rs.getObject("distributor_id", Integer.class)
            ));
        }
        
        return products;
    }

    // List all products in a specified category
    public static List<Product> listAllProductsInCategory(int categoryId) throws SQLException {
        List<Product> products = new ArrayList<>();
        String query = "SELECT p.* FROM product p "
                     + "JOIN product_category pc ON p.product_id = pc.product_id "
                     + "WHERE pc.category_id = ?";
        
        PreparedStatement stmt = DatabaseManager.getConnection().prepareStatement(query);
        stmt.setInt(1, categoryId);
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            products.add(new Product(
                rs.getInt("product_id"),
                rs.getString("product_name"),
                rs.getString("product_description"),
                rs.getInt("product_quantity"),
                rs.getDouble("current_unit_price"),
                rs.getObject("distributor_id", Integer.class)
            ));
        }
        
        return products;
    }

    // Find the most expensive product
    public static Product mostExpensiveProduct() throws SQLException {
        String query = "SELECT * FROM product ORDER BY current_unit_price DESC LIMIT 1";
        
        Statement stmt = DatabaseManager.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery(query);
        
        if (rs.next()) {
            return new Product(
                rs.getInt("product_id"),
                rs.getString("product_name"),
                rs.getString("product_description"),
                rs.getInt("product_quantity"),
                rs.getDouble("current_unit_price"),
                rs.getObject("distributor_id", Integer.class)
            );
        }
        
        return null;
    }

    // Find the highest product in stock (with the most quantity)
    public static Product highestProductInStock() throws SQLException {
        String query = "SELECT * FROM product ORDER BY product_quantity DESC LIMIT 1";
        
        Statement stmt = DatabaseManager.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery(query);
        
        if (rs.next()) {
            return new Product(
                rs.getInt("product_id"),
                rs.getString("product_name"),
                rs.getString("product_description"),
                rs.getInt("product_quantity"),
                rs.getDouble("current_unit_price"),
                rs.getObject("distributor_id", Integer.class)
            );
        }
        
        return null;
    }
}
