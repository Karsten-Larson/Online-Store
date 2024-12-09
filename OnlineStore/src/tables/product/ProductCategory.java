package tables.product;

import java.sql.*;
import java.util.Scanner;
import tables.DatabaseManager;

/**
 * Class that manages all data related to the ProductCategory linking table.
 * 
 * @author Owen Sailer
 */
public class ProductCategory {

    private int categoryId;
    private int productId;

    public ProductCategory(int categoryId, int productId) {
        this.categoryId = categoryId;
        this.productId = productId;
    }

        public int insertAProductsCategory() throws SQLException {
            Scanner scan = new Scanner(System.in);

            System.out.print("Enter category ID for the product: ");
            int newcategoryId = scan.nextInt();
            System.out.print("Enter product ID: ");
            int newproductId = scan.nextInt();

            String insertProductCategory = "INSERT INTO product_category (category_id, product_id) VALUES (?, ?)";

            try (PreparedStatement stmt = DatabaseManager.getConnection().prepareStatement(insertProductCategory)) {
                stmt.setInt(1, newcategoryId);
                stmt.setInt(2, newproductId);

                return stmt.executeUpdate();
            }
        }

    public int removeAProductsCategory() throws SQLException {
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter the product ID to remove its category: ");
        int newproductId = scan.nextInt();

        String deleteProductCategory = "DELETE FROM product_category WHERE product_id = ?";

        try (PreparedStatement stmt = DatabaseManager.getConnection().prepareStatement(deleteProductCategory)) {
            stmt.setInt(1, newproductId);
            return stmt.executeUpdate();
        }
    }

    public int updateAProductsCategory() throws SQLException {
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter the product ID to update its category: ");
        int newproductId = scan.nextInt();

        System.out.print("Enter the new category ID for this product: ");
        int newcategoryId = scan.nextInt();

        String updateProductCategory = "UPDATE product_category SET category_id = ? WHERE product_id = ?";

        try (PreparedStatement stmt = DatabaseManager.getConnection().prepareStatement(updateProductCategory)) {
            stmt.setInt(1, newcategoryId);
            stmt.setInt(2, newproductId);
            return stmt.executeUpdate();
        }
    }


    public int getCategoryId() {
        return categoryId;
    }

    public int getProductId() {
        return productId;
    }

    @Override
    public String toString() {
        return "ProductCategory{" +
                "categoryId=" + categoryId +
                ", productId=" + productId +
                '}';
    }
}

