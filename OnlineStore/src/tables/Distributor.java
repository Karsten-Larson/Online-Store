package tables.distributor;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import tables.Table;

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

    protected Distributor(ResultSet rs) {
        try {
            id = rs.getInt("distributor_id");
            phone = rs.getString("phone");
            addressID= rs.getInt("address_id");

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
                = "SELECT d.distributor_id, phone, address_id "
                + "FROM distributor d "
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
                = "INSERT INTO Distributor (phone, address_id) "
                + "VALUES (?, ?)";

        int id = insert(insertQuery, phone, addressID);

        return fromID(id);
    }

    /**
     * Deletes the Distributor
     */
    public void deleteDistributor() {
        throw new UnsupportedOperationException("This method is not implemented yet.");

//        clearCategories();
//
//        String query
//                = "DELETE FROM Distributor "
//                + "WHERE customer_id=?";
//
//        delete(query, id);
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

    @Override
    public String toString() {
        return "Distributor{" + "id=" + id + ", phone=" + phone + "address_id= " + addressID + '}';
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
