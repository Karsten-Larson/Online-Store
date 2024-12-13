package tables.product;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tables.Table;

/**
 * Class that manages all data related to the ProductType table.
 *
 * @author Owen Sailer
 */
public class ProductType extends Table {

    private static Map<Integer, ProductType> cache = new HashMap<>();

    private int categoryId;
    private String categoryName;

    /**
     * Creates an OrderItem from a ResultSet
     *
     * @param rs ResultSet row representing the OrderItem
     */
    protected ProductType(ResultSet rs) {
        try {
            categoryId = rs.getInt("category_id");
            categoryName = rs.getString("category_name");
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public static ProductType fromID(int id) {
        if (cache.containsKey(id)) {
            return cache.get(id);
        }

        String query
                = "SELECT category_id, category_name FROM product_type "
                + "WHERE category_id = ?";

        ResultSet rs = select(query, id);
        ProductType result = new ProductType(rs);

        cache.put(id, result);

        return result;
    }
    
    public static List<ProductType> getAllProductTypes() {
        String query
                = "SELECT category_id "
                + "FROM product_type ";

        ResultSet rs = select(query);

        return mapIDs(rs, ProductType::fromID);
    }
    
    public void removeProductType(int id) {
        String deleteQuery
                = "DELETE FROM product_category "
                + "WHERE product_id=? "
                + "AND category_id=?";

        delete(deleteQuery, id, categoryId);
    }

    /**
     * Gets a ProductType by the category's name
     *
     * @param name category name
     * @return product type
     */
    public static ProductType fromName(String name) {

        String query
                = "SELECT category_id, category_name FROM product_type "
                + "WHERE category_name = ?";

        ResultSet rs = select(query, name);

        try {
            return fromID(rs.getInt("category_id"));
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public static ProductType createProductType(String name) {
        String insertQuery
                = "INSERT INTO product_type (category_name) "
                + "VALUES (?)";

        int id = insert(insertQuery, name);

        return fromID(id);
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        String query
                = "UPDATE product_type "
                + "SET category_name=? "
                + "WHERE category_id=?;";

        // Run the update
        update(query, categoryName, categoryId);

        this.categoryName = categoryName;
    }

    @Override
    public String toString() {
        return "ProductType{" + "categoryId=" + categoryId + ", categoryName=" + categoryName + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ProductType other = (ProductType) obj;
        return this.categoryId == other.categoryId;
    }

}
