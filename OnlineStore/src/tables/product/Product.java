package tables.product;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tables.Table;

/**
 * Class that manages all data related to the Product table.
 *
 * @author Owen Sailer
 */
public class Product extends Table {

    private static final Map<Integer, Product> cache = new HashMap<>();

    private int id;
    private String name;
    private String description;
    private int quantity;
    private double unitPrice;
    private int distributorId;
    private List<ProductType> categories;

    protected Product(ResultSet rs) {
        try {
            id = rs.getInt("product_id");
            name = rs.getString("product_name");
            description = rs.getString("product_description");
            quantity = rs.getInt("product_quantity");
            unitPrice = rs.getDouble("current_unit_price");
            distributorId = rs.getInt("distributor_id");

            // Get categories
            categories = new ArrayList<>();
            do {
                int id = rs.getInt("category_id");

                // Value is null
                if (id == 0) {
                    break;
                }

                categories.add(ProductType.fromID(id));
            } while (rs.next());
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    /**
     * Gets a Product from an ID
     * @param id id
     * @return Product
     */
    public static Product fromID(int id) {
        if (cache.containsKey(id)) {
            return cache.get(id);
        }

        String query
                = "SELECT p.product_id, product_name, product_description, product_quantity, current_unit_price, distributor_id, pt.category_id "
                + "FROM product p "
                + "LEFT JOIN product_category pc ON pc.product_id = p.product_id "
                + "LEFT JOIN product_type pt ON pt.category_id = pc.category_id "
                + "WHERE p.product_id=?";

        ResultSet rs = select(query, id);
        Product result = new Product(rs);

        cache.put(id, result);

        return result;
    }

    /**
     * Creates a new Product
     * @param name name
     * @param description description
     * @param quantity quantity
     * @param unitPrice unit price
     * @param distributorId distributor id
     * @return Product
     */
    public static Product createProduct(String name, String description, int quantity, double unitPrice, int distributorId) {
        String insertQuery
                = "INSERT INTO product (product_name, product_description, product_quantity, current_unit_price, distributor_id) "
                + "VALUES (?, ?, ?, ?, ?)";

        int id = insert(insertQuery, name, description, quantity, unitPrice, distributorId);

        return fromID(id);
    }
    
    /**
     * Deletes the order and all its associated items
     */
    public void deleteProduct() {
        throw new UnsupportedOperationException("This method is not implemented yet. PriceChange object must exist first");
        
//        clearCategories();
//        
//        String query 
//                = "DELETE FROM product " 
//                + "WHERE product_id=?";
//        
//        delete(query, id);
    }

    public int getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean isInStock() {
        return quantity > 0;
    }

    public double getCurrentUnitPrice() {
        return unitPrice;
    }

    public Integer getDistributorId() {
        return distributorId;
    }

    public List<ProductType> getCategories() {
        return new ArrayList<>(categories);
    }

    public void setUnitPrice(double unitPrice) throws SQLException {
        String query = "UPDATE product "
                + "SET current_unit_price=? "
                + "WHERE product_id=?";

        update(query, unitPrice, id);

        this.unitPrice = unitPrice;
    }

    public void setQuantity(int quantity) throws SQLException {
        String query
                = "UPDATE product "
                + "SET product_quantity=? "
                + "WHERE product_id=?";

        update(query, quantity, id);

        this.quantity = quantity;
    }

    public void setName(String name) throws SQLException {
        String query
                = "UPDATE product "
                + "SET product_name=? "
                + "WHERE product_id=?";

        update(query, name, id);

        this.name = name;
    }

    public void setDescription(String description) throws SQLException {
        String query
                = "UPDATE product "
                + "SET product_description=? "
                + "WHERE product_id=?";

        update(query, description, id);

        this.description = description;
    }

    public void setDistributorId(Integer distributorId) throws SQLException {
        String query
                = "UPDATE product "
                + "SET distributor_id=? "
                + "WHERE product_id=?";

        update(query, distributorId, id);

        this.distributorId = distributorId;
    }

    /**
     * Adds a category to the product
     *
     * @param name category name
     */
    public void addCategory(String name) {
        ProductType result;

        try {
            result = ProductType.fromName(name);
        } catch (RuntimeException ex) {
            result = ProductType.createProductType(name);
        }

        addCategory(result);
    }

    /**
     * Adds a category to the product
     *
     * @param productType productType
     */
    public void addCategory(ProductType productType) {
        String insertQuery
                = "INSERT INTO product_category (product_id, category_id) "
                + "VALUES (?, ?)";

        insert(insertQuery, id, productType.getCategoryId());

        categories.add(productType);
    }

    /**
     * Removes a category from the product
     *
     * @param name product name
     */
    public void removeCategory(String name) {
        ProductType result = ProductType.fromName(name);

        removeCategory(result);
    }

    /**
     * Removes a category from the product
     *
     * @param productType productType
     */
    public void removeCategory(ProductType productType) {
        String deleteQuery
                = "DELETE FROM product_category "
                + "WHERE product_id=? "
                + "AND category_id=?";

        delete(deleteQuery, id, productType.getCategoryId());
        categories.remove(productType);
    }
    
    /**
     * Removes all categories from an item
     */
    public void clearCategories() {
        for (ProductType category: categories) {
            removeCategory(category);
        }
    }

    @Override
    public String toString() {
        return "Product{" + "id=" + id + ", name=" + name + ", description=" + description + ", quantity=" + quantity + ", unitPrice=" + unitPrice + ", distributorId=" + distributorId + ", categories=" + categories + '}';
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
        final Product other = (Product) obj;
        return this.id == other.id;
    }
}
