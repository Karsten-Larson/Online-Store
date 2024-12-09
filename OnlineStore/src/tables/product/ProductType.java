package tables.product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import tables.DatabaseManager;

/**
 * Class that manages all data related to the ProductType table.
 * 
 * @author Owen Sailer
 */
public class ProductType {

    private int categoryId;
    private String categoryName;

    public ProductType() { }

    public ProductType(String categoryName) {
        this.categoryName = categoryName;
    }

    public ProductType(int categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public static List<Product> getProductsByCategoryID(int categoryID) throws SQLException {
        String select = "SELECT p.product_id, p.product_name, p.product_description, p.product_quantity, p.current_unit_price, p.distributor_id "
                      + "FROM product p "
                      + "JOIN product_category pc ON p.product_id = pc.product_id "
                      + "WHERE pc.category_id = ?";

        PreparedStatement stmtSelect = DatabaseManager.getConnection().prepareStatement(select);
        stmtSelect.setInt(1, categoryID);
        ResultSet rs = stmtSelect.executeQuery();

        List<Product> products = new ArrayList<>();
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


    public int insertNewProductCategory() throws SQLException {
        
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter category name: ");
        String newcategoryName = scanner.nextLine();

        this.setCategoryName(newcategoryName);

        String insertProductType = "INSERT INTO product_type (category_name) VALUES (?)";

        PreparedStatement stmt = DatabaseManager.getConnection().prepareStatement(insertProductType); 
        stmt.setString(1, this.getCategoryName());
        return stmt.executeUpdate();
        
    }


    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public String toString() {
        return "ProductType{" +
                "categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                '}';
    }
}
