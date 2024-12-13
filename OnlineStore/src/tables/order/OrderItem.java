package tables.order;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import tables.Table;
import tables.product.Product;

/**
 * Class that manages all data related to the order item table
 *
 * @author karsten
 */
public class OrderItem extends Table {

    private static Map<Integer, OrderItem> cache = new HashMap<>();

    private int orderItemId;
    private int orderId;
    private int productId;
    private double unitPrice;
    private int quantity;

    /**
     * Creates an OrderItem from a ResultSet
     *
     * @param rs ResultSet row representing the OrderItem
     */
    protected OrderItem(ResultSet rs) {
        try {
            orderItemId = rs.getInt("order_item_id");
            orderId = rs.getInt("order_id");
            productId = rs.getInt("product_id");
            unitPrice = rs.getDouble("unit_price");
            quantity = rs.getInt("quantity");
            
            
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public static OrderItem fromID(int id) {
        if (cache.containsKey(id)) {
            return cache.get(id);
        }

        String query
                = "SELECT order_item_id, order_id, product_id, unit_price, quantity FROM order_item "
                + "WHERE order_item_id = ?";

        ResultSet rs = select(query, id);
        OrderItem result = new OrderItem(rs);

        cache.put(id, result);

        return result;
    }

    /**
     * Method that create a new OrderItem in the database
     *
     * @param orderId order id
     * @param productId product id
     * @param unitPrice unit price
     * @param quantity quantity
     * @return OrderItem instance
     */
    public static OrderItem createItem(int orderId, int productId, double unitPrice, int quantity) {
        String insertQuery
                = "INSERT INTO order_item (order_id, product_id, unit_price, quantity) "
                + "VALUES (?, ?, ?, ?)";

        int id = insert(insertQuery, orderId, productId, unitPrice, quantity);

        return fromID(id);
    }

    /**
     * Method that create a new OrderItem in the database
     *
     * @param orderId order id
     * @param prodcut product
     * @param quantity quantity
     * @return OrderItem instance
     */
    public static OrderItem createItem(int orderId, Product product, int quantity) {
        return OrderItem.createItem(orderId, product.getID(), product.getCurrentUnitPrice(), quantity);
    }
    
    /**
     * Deletes an order item from the database
     */
     public void deleteItem() {
        String query 
                = "DELETE FROM order_item "
                + "WHERE order_item_id = ?";
        
        delete(query, orderItemId);
    }

    public int getOrderItemId() {
        return orderItemId;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getProductId() {
        return productId;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }
    
    public Product getProduct() {
        return Product.fromID(productId);
    }

    public void setOrderId(int orderId) {
        String query
                = "UPDATE order_item "
                + "SET order_id=? "
                + "WHERE order_item_id=?;";

        // Run the update
        update(query, orderId, orderItemId);

        this.orderId = orderId;
    }

    public void setProductId(int productId) {
        String query
                = "UPDATE order_item "
                + "SET product_id=? "
                + "WHERE order_item_id=?;";

        // Run the update
        update(query, productId, orderItemId);

        this.productId = productId;
    }

    public void setUnitPrice(double unitPrice) {
        String query
                = "UPDATE order_item "
                + "SET unit_price=? "
                + "WHERE order_item_id=?;";

        // Run the update
        update(query, unitPrice, orderItemId);

        this.unitPrice = unitPrice;
    }

    public void setQuantity(int quantity) {
        String query
                = "UPDATE order_item "
                + "SET quantity=? "
                + "WHERE order_item_id=?;";

        // Run the update
        update(query, quantity, orderItemId);

        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "OrderItem{" + "orderItemId=" + orderItemId + ", orderId=" + orderId + ", productId=" + productId + ", unitPrice=" + unitPrice + ", quantity=" + quantity + '}';
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
        final OrderItem other = (OrderItem) obj;
        return this.orderItemId == other.orderItemId;
    }

}
