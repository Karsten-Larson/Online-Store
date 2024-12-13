package tables.distributor;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tables.Table;
import tables.address.Address;
import tables.product.Product;

/**
 * Class that manages all data related to the Distributor table.
 *
 * @author Patrick Arbach
 */
public class Distributor extends Table {

    private static final Map<Integer, Distributor> cache = new HashMap<>();

    private int id;
    private String phone;
    private int addressID;
    private List<Product> products;

    protected Distributor(ResultSet rs) {
        try {
            id = rs.getInt("distributor_id");
            phone = rs.getString("distributor_phone");
            addressID = rs.getInt("address_id");

            // Get items
            products = new ArrayList<>();
            do {
                int id = rs.getInt("product_id");

                // Value is null
                if (id == 0) {
                    break;
                }

                products.add(Product.fromID(id));
            } while (rs.next());
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    /**
     * Gets a Distributor from an ID
     *
     * @param id id
     * @return Distributor
     */
    public static Distributor fromID(int id) {
        if (cache.containsKey(id)) {
            return cache.get(id);
        }

        String query
                = "SELECT d.distributor_id, distributor_phone, address_id, p.product_id "
                + "FROM distributor d "
                + "LEFT JOIN product p ON p.distributor_id = d.distributor_id "
                + "WHERE d.distributor_id=?";

        ResultSet rs = select(query, id);
        Distributor result = new Distributor(rs);

        cache.put(id, result);

        return result;
    }

    /**
     * Creates a new Distributor
     *
     * @param phone phone number
     * @param addressID address id
     * @return distributor
     */
    public static Distributor createDistributor(String phone, int addressID) {
        String insertQuery
                = "INSERT INTO Distributor (distributor_phone, address_id) "
                + "VALUES (?, ?)";

        int id = insert(insertQuery, phone, addressID);

        return fromID(id);
    }

    /**
     * Creates a new Distributor
     *
     * @param phone phone number
     * @param address address
     * @return distributor
     */
    public static Distributor createDistributor(String phone, Address address) {
        return createDistributor(phone, address.getId());
    }
    
    public static List<Distributor> getAllDistributors() {
        String query
                = "SELECT distributor_id "
                + "FROM Distributor ";

        ResultSet rs = select(query);

        return mapIDs(rs, Distributor::fromID);
    }

    /**
     * Deletes the Distributor
     */
    public void deleteDistributor() {
        clearProducts();

        String query
                = "DELETE FROM Distributor "
                + "WHERE customer_id=?";

        delete(query, id);
    }

    public int getID() {
        return id;
    }

    public String getPhone() {
        return phone;
    }

    public int getAddressID() {
        return addressID;
    }

    public Address getAddress() {
        return Address.fromID(addressID);
    }

    public void setPhone(String phone) throws SQLException {
        String query = "UPDATE Distributor "
                + "SET phone=? "
                + "WHERE distributor_id=?";

        update(query, phone, id);

        this.phone = phone;
    }

    public void setAddressID(int addressID) throws SQLException {
        String query = "UPDATE Distributor "
                + "SET address_id=? "
                + "WHERE distributor_id=?";

        update(query, addressID, id);

        this.addressID = addressID;
    }

    public List<Product> getProducts() {
        return new ArrayList(products);
    }

    public void addProduct(String name, String description, int quantity, double price) {
        Product p = Product.createProduct(name, description, quantity, price, id);

        products.add(p);
    }

    public void removeProduct(Product product) {
        product.deleteProduct();

        products.remove(product);
    }
    
    public void clearProducts() {
        while (!products.isEmpty()) {
            removeProduct(products.getFirst());
        }
    }

    @Override
    public String toString() {
        return "Distributor{" + "id=" + id + ", phone=" + phone + ", addressID=" + addressID + ", products=" + products + '}';
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
        final Distributor other = (Distributor) obj;
        return this.id == other.id;
    }
}
