package tables.customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tables.Table;
import tables.order.Order;
import tables.order.OrderStatus;
import tables.wishlist.Wishlist;

/**
 * Class that manages all data related to the Customer table.
 *
 * @author Patrick Arbach
 */
public class Customer extends Table {

    private static final Map<Integer, Customer> cache = new HashMap<>();

    private int id;
    private String firstname;
    private String lastname;
    private String phone;
    private String email;
    private List<Order> orders;
    private List<Wishlist> wishlists;

    protected Customer(ResultSet rs) {
        try {
            id = rs.getInt("customer_id");
            firstname = rs.getString("firstname");
            lastname = rs.getString("lastname");
            email = rs.getString("email_address");
            phone = rs.getString("phone_number");

            // Get items
            orders = new ArrayList<>();
            wishlists = new ArrayList<>();

            do {
                int orderId = rs.getInt("order_id");
                int wishlistId = rs.getInt("wishlist_id");

                // Value is null
                if (orderId != 0) {
                    Order order = Order.fromID(orderId);

                    if (!orders.contains(order)) {
                        orders.add(order);
                    }
                }

                if (wishlistId != 0) {
                    Wishlist wishlist = Wishlist.fromID(wishlistId);

                    if (!wishlists.contains(wishlist)) {
                        wishlists.add(wishlist);
                    }
                }

            } while (rs.next());

        } catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    /**
     * Gets a Customer from an ID
     *
     * @param id id
     * @return Customer
     */
    public static Customer fromID(int id) {
        if (cache.containsKey(id)) {
            return cache.get(id);
        }

        String query
                = "SELECT c.customer_id, firstname, lastname, email_address, phone_number, co.order_id, w.wishlist_id "
                + "FROM customer c "
                + "LEFT JOIN customer_order co ON c.customer_id = co.customer_id "
                + "LEFT JOIN wishlist w ON c.customer_id = w.customer_id "
                + "WHERE c.customer_id=?";

        ResultSet rs = select(query, id);
        Customer result = new Customer(rs);

        cache.put(id, result);

        return result;
    }

    /**
     * Creates a new Customer
     *
     * @param fname first name
     * @param lname last name
     * @param email email address
     * @param phone phone number
     * @return Customer
     */
    public static Customer createCustomer(String fname, String lname, String email, String phone) {
        String insertQuery
                = "INSERT INTO Customer (firstname, lastname, email_address, phone_number) "
                + "VALUES (?, ?, ?, ?)";

        int id = insert(insertQuery, fname, lname, email, phone);

        return fromID(id);
    }

    /**
     * Deletes the Customer
     */
    public void deleteCustomer() {
        clearOrders();
        clearWishlists();

        String query
                = "DELETE FROM Customer "
                + "WHERE customer_id=?";

        delete(query, id);
    }
    
    public void clearOrders() {
        while (!orders.isEmpty()) {
            removeOrder(orders.getLast());
        }
    }
    public void clearWishlists() {
        while (!wishlists.isEmpty()) {
            removeWishlist(wishlists.getFirst());
        }
    }

    public static List<Customer> getAllCustomers() {
        String query
                = "SELECT c.customer_id "
                + "FROM customer c ";

        ResultSet rs = select(query);

        return mapIDs(rs, Customer::fromID);
    }

    public int getID() {
        return id;
    }

    public String getFirstName() {
        return firstname;
    }

    public String getLastName() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public void setFirstName(String fname) throws SQLException {
        String query = "UPDATE Customer "
                + "SET firstname=? "
                + "WHERE customer_id=?";

        update(query, fname, id);

        this.firstname = fname;
    }

    public void setLastName(String lname) throws SQLException {
        String query = "UPDATE Customer "
                + "SET lastname=? "
                + "WHERE customer_id=?";

        update(query, lname, id);

        this.lastname = lname;
    }

    public void setEmail(String email) throws SQLException {
        String query = "UPDATE Customer "
                + "SET email_address=? "
                + "WHERE customer_id=?";

        update(query, email, id);

        this.email = email;

    }

    public void setPhone(String phone) throws SQLException {
        String query = "UPDATE Customer "
                + "SET phone_number=? "
                + "WHERE customer_id=?";

        update(query, phone, id);

        this.phone = phone;
    }

    public List<Order> getOrders() {
        return new ArrayList<>(orders);
    }
    
    public void addOrder(int paymentId, int shippingId, OrderStatus status) {
        orders.add(Order.createOrder(id, paymentId, shippingId, status));
    }
    
    public void removeOrder(Order order) {
        orders.remove(order);
        
        order.deleteOrder();
    }

    public List<Wishlist> getWishlists() {
        return new ArrayList<>(wishlists);
    }
    
    public void addWishlist(String name) {
        wishlists.add(Wishlist.createWishlist(id, name));
    }
    
    public void removeWishlist(Wishlist wishlist) {
        wishlists.remove(wishlist);
        
        wishlist.deleteWishlist();
    }

    @Override
    public String toString() {
        return "Customer{" + "id=" + id + ", firstname=" + firstname + ", lastname=" + lastname + ", phone=" + phone + ", email=" + email + ", orders=" + orders + ", wishlists=" + wishlists + '}';
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
        final Customer other = (Customer) obj;
        return this.id == other.id;
    }
}
