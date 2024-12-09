package tables.order;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import tables.Table;

/**
 * Class that manages all data related to the order item table
 *
 * @author karsten
 */
class OrderItem extends Table {

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
    public static OrderItem createOrder(int orderId, int productId, int unitPrice, int quantity) {
        String insertQuery
                = "INSERT INTO order_item (order_id, product_id, unit_price, quantity) "
                + "VALUES (?, ?, ?, ?)";

        int id = updateRow(insertQuery, orderId, productId, unitPrice, quantity);

        return fromID(id);
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

}