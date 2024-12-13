package tables.order;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import tables.Table;
import tables.address.Address;
import tables.customer.Customer;
import tables.payment.PaymentInfo;
import tables.product.Product;

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
    private List<OrderItem> items;

    protected Order(ResultSet rs) {
        try {
            // Get properties
            orderId = rs.getInt("order_id");
            customerId = rs.getInt("customer_id");
            paymentId = rs.getInt("payment_id");
            orderDate = rs.getDate("order_date");
            shippingId = rs.getInt("shipping_id");
            status = OrderStatus.valueOf(rs.getString("order_status").toUpperCase());

            // Get items
            items = new ArrayList<>();
            do {
                int id = rs.getInt("order_item_id");

                // Value is null
                if (id == 0) {
                    break;
                }

                items.add(OrderItem.fromID(id));
            } while (rs.next());
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
                = "SELECT co.order_id, customer_id, payment_id, order_date, shipping_id, order_status, order_item_id FROM customer_order co "
                + "LEFT JOIN order_item oi ON co.order_id = oi.order_id "
                + "WHERE co.order_id = ?";

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

        int id = insert(insertQuery, customerId, paymentId, shippingId, status.name().toLowerCase(), orderDate);

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

        int id = insert(insertQuery, customerId, paymentId, shippingId, status.name().toLowerCase());

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

        int id = insert(insertQuery, customerId, paymentId, shippingId, orderDate);

        return fromID(id);
    }
    
    /**
     * Deletes the order and all its associated items
     */
    public void deleteOrder() {
        clearItems();
        getPayment().deletePayment();
        
        String query 
                = "DELETE FROM customer_order "
                + "WHERE order_id = ?";
        
        delete(query, orderId);
    }

    public int getOrderId() {
        return orderId;
    }

    public Date getOrderDate() {
        return new Date(orderDate.getTime());
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
    
    public Customer getCustomer() {
        return Customer.fromID(customerId);
    }

    public int getPaymentId() {
        return paymentId;
    }
    
    public PaymentInfo getPayment() {
        return PaymentInfo.fromID(paymentId);
    }
    
    public double getTotalPrice() {
        double total = 0;
        
        for (OrderItem item: items) {
            total += item.getUnitPrice() * item.getQuantity();
        }
        
        return total;
    }

    public List<OrderItem> getItems() {
        return new ArrayList<>(items);
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
    
    public void setShippingAddress(Address shippingAddress) {
        setShippingId(shippingAddress.getId());
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
    
    public void setCustomer(Customer customer) {
        setCustomerId(customer.getID());
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
    
    public void setPayment(PaymentInfo paymentInfo) {
        setPaymentId(paymentInfo.getPaymentId());
    }

    /**
     * Gets whether or not there are items on the order
     *
     * @return has items
     */
    public boolean hasItems() {
        return !items.isEmpty();
    }

    /**
     * Deletes all items from the order
     */
    public void clearItems() {
        for (OrderItem item : items) {
            item.deleteItem();
        }

        items = new ArrayList<>();
    }

    /**
     * Adds a product to order items
     *
     * @param product product object
     * @param quantity quantity of product
     */
    public void addItem(Product product, int quantity) {
        OrderItem orderItem = OrderItem.createItem(orderId, product, quantity);

        items.add(orderItem);
    }

    /**
     * Removes the last added item from the list
     *
     * @return whether or not an item was removed
     */
    public boolean removeItem() {
        if (items.isEmpty()) {
            return false;
        }

        OrderItem orderItem = OrderItem.fromID(items.getLast().getOrderItemId());

        orderItem.deleteItem();
        items.remove(orderItem);

        return true;
    }

    /**
     * Removes the first instance of the product from the order's items
     *
     * @param product product object
     * @return boolean value of whether a item was removed
     */
    public boolean removeItem(Product product) {
        List<Integer> ids = items.stream().map(e -> e.getProductId()).collect(Collectors.toList());

        int productId = product.getID();

        if (ids.contains(productId)) {
            OrderItem orderItem = items.get(ids.indexOf(productId));

            orderItem.deleteItem();

            items.remove(ids.indexOf(productId));

            return true;
        }

        return false;
    }
    
    public boolean removeItem(OrderItem orderItem) {
        if (items.contains(orderItem)) {
            orderItem.deleteItem();
            items.remove(orderItem);
            return true;
        }
        
        return false;
    }

    @Override
    public String toString() {
        return "Order{" + "orderId=" + orderId + ", customerId=" + customerId + ", paymentId=" + paymentId + ", orderDate=" + orderDate + ", shippingId=" + shippingId + ", status=" + status + ", items=" + items + '}';
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
        final Order other = (Order) obj;
        return this.orderId == other.orderId;
    }
}
