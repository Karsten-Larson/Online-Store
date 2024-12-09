package tables.order;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import tables.Table;
import tables.address.Address;

/**
 * Class that manages all data related to the order table
 *
 * @author karsten
 */
public class Order extends Table {

    private static Map<Integer, Order> cache = new HashMap<>();

    private int orderId;
    private int customerId;
    private int paymentId;
    private Date orderDate;
    private int shippingId;
    private OrderStatus status;

    public Order(ResultSet rs) {
        try {
            orderId = rs.getInt("order_id");
            customerId = rs.getInt("customer_id");
            paymentId = rs.getInt("payment_id");
            orderDate = rs.getDate("order_date");
            shippingId = rs.getInt("shipping_id");
            status = OrderStatus.valueOf(rs.getString("order_status").toUpperCase());
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    /**
     * Gets an Order object based on the order's ID
     *
     * @param id order id
     * @return order object
     */
    public static Order fromID(int id) {
        if (cache.containsKey(id)) {
            return cache.get(id);
        }

        String query
                = "SELECT order_id, customer_id, payment_id, order_date, shipping_id, order_status FROM customer_order "
                + "WHERE order_id = ?";

        ResultSet rs = select(query, id);
        Order result = new Order(rs);

        cache.put(id, result);

        return result;
    }

    /**
     * Method that create a new Order in the database
     *
     * @param customerId customer id
     * @param paymentId payment id
     * @param shippingId shipping id
     * @param status order status
     * @param orderDate date the order was placed
     * @return order object
     */
    public static Order createOrder(int customerId, int paymentId, int shippingId, OrderStatus status, Date orderDate) {
        String insertQuery
                = "INSERT INTO customer_order (customer_id, payment_id, shipping_id, order_status, order_date) "
                + "VALUES (?, ?, ?, Cast(? as order_status), ?)";

        int id = updateRow(insertQuery, customerId, paymentId, shippingId, status.name().toLowerCase(), orderDate);

        return fromID(id);
    }

    /**
     * Method that create a new Order in the database
     *
     * @param customerId customer id
     * @param paymentId payment id
     * @param shippingId shipping id
     * @param status order status
     * @return order object
     */
    public static Order createOrder(int customerId, int paymentId, int shippingId, OrderStatus status) {
        String insertQuery
                = "INSERT INTO customer_order (customer_id, payment_id, shipping_id, order_status) "
                + "VALUES (?, ?, ?, Cast(? as order_status))";

        int id = updateRow(insertQuery, customerId, paymentId, shippingId, status.name().toLowerCase());

        return fromID(id);
    }

    /**
     * Method that create a new Order in the database
     *
     * @param customerId customer id
     * @param paymentId payment id
     * @param shippingId shipping id
     * @param orderDate date the order was placed
     * @return order object
     */
    public static Order createOrder(int customerId, int paymentId, int shippingId, Date orderDate) {
        String insertQuery
                = "INSERT INTO customer_order (customer_id, payment_id, shipping_id, order_date) "
                + "VALUES (?, ?, ?, ?)";

        int id = updateRow(insertQuery, customerId, paymentId, shippingId, orderDate);

        return fromID(id);
    }

    public int getOrderId() {
        return orderId;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public int getShippingId() {
        return shippingId;
    }

    public Address getShippingAddress() {
        return Address.fromID(shippingId);
    }

    public OrderStatus getStatus() {
        return status;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setOrderDate(Date orderDate) {
        String query
                = "UPDATE customer_order "
                + "SET order_date=? "
                + "WHERE order_id=?;";

        // Run the update
        update(query, orderDate, orderId);

        this.orderDate = orderDate;
    }

    public void setShippingId(int shippingId) {
        String query
                = "UPDATE customer_order "
                + "SET shipping_id=? "
                + "WHERE order_id=?;";

        // Run the update
        update(query, shippingId, orderId);

        this.shippingId = shippingId;
    }

    public void setStatus(OrderStatus status) {
        String query
                = "UPDATE customer_order "
                + "SET order_status=Cast(? AS order_status) "
                + "WHERE order_id=?;";

        // Run the update
        System.out.println(status.name().toLowerCase());
        update(query, status.name().toLowerCase(), orderId);

        this.status = status;
    }

    public void setCustomerId(int customerId) {
        String query
                = "UPDATE customer_order "
                + "SET customer_id=? "
                + "WHERE order_id=?;";

        // Run the update
        update(query, customerId, orderId);

        this.customerId = customerId;
    }

    public void setPaymentId(int paymentId) {
        String query
                = "UPDATE customer_order "
                + "SET payment_id=? "
                + "WHERE order_id=?;";

        // Run the update
        update(query, paymentId, orderId);

        this.paymentId = paymentId;
    }

    @Override
    public String toString() {
        return "Order{" + "orderId=" + orderId + ", customerId=" + customerId + ", paymentId=" + paymentId + ", orderDate=" + orderDate + ", shippingId=" + shippingId + ", status=" + status + '}';
    }

}
