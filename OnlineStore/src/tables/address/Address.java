package tables.address;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import tables.Table;

/**
 * Class that manages all data related to the address and address_relation
 * tables
 *
 * @author karsten
 */
public class Address extends Table {

    private int id;
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String apartmentNumber;
    private List<AddressType> types;

    /**
     * Initializes an Address object
     *
     * @param rs results set query of address joined with address_relation table
     */
    public Address(ResultSet rs) {
        try {
            id = rs.getInt("address_id");
            street = rs.getString("street");
            city = rs.getString("city");
            state = rs.getString("state");
            zipCode = rs.getString("zip_code");
            apartmentNumber = rs.getString("apt_number");

            types = new ArrayList<>();
            do {
                types.add(AddressType.valueOf(rs.getString("type").toUpperCase()));
            } while (rs.next());
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    /**
     * Gets an Address object based on the address's ID
     *
     * @param id address id
     * @return Address object
     */
    public static Address fromID(int id) {
        String query
                = "SELECT a.address_id, street, city, state, zip_code, apt_number, type FROM address a "
                + "INNER JOIN address_relation ar "
                + "ON a.address_id = ar.address_id "
                + "WHERE a.address_id = ?";

        ResultSet rs = select(query, id);

        return new Address(rs);
    }

    public int getId() {
        return id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        String query
                = "UPDATE address "
                + "SET street=? "
                + "WHERE address_id=?;";

        // Run the update
        update(query, street, id);

        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        String query
                = "UPDATE address "
                + "SET city=? "
                + "WHERE address_id=?;";

        // Run the update
        update(query, city, id);

        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        String query
                = "UPDATE address "
                + "SET state=? "
                + "WHERE address_id=?;";

        // Run the update
        update(query, state, id);

        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        String query
                = "UPDATE address "
                + "SET zip_code=? "
                + "WHERE address_id=?;";

        // Run the update
        update(query, zipCode, id);

        this.zipCode = zipCode;
    }

    public String getApartmentNumber() {
        return apartmentNumber;
    }

    public void setApartmentNumber(String apartmentNumber) {
        String query
                = "UPDATE address "
                + "SET apt_number=? "
                + "WHERE address_id=?;";

        // Run the update
        update(query, apartmentNumber, id);

        this.apartmentNumber = apartmentNumber;
    }

    public List<AddressType> getTypes() {
        return new ArrayList<>(types);
    }

    /**
     * Sets the types of addresses the address is
     *
     * @param types the types of addresses the address is
     * @throws IllegalArgumentException no types are passed
     */
    public void setTypes(List<AddressType> types) throws IllegalArgumentException {
        if (types.isEmpty()) {
            throw new IllegalArgumentException("Types cannot be empty");
        }

        // Ensures no repeated types
        List<AddressType> unqiueTypes = types.stream().distinct().collect(Collectors.toList());

        System.out.println(unqiueTypes);

        // Finds which types to add and remove
        List<AddressType> toRemove
                = this.types.stream()
                        .filter(e -> !unqiueTypes.contains(e))
                        .distinct()
                        .collect(Collectors.toList());

        List<AddressType> toAdd
                = unqiueTypes.stream()
                        .filter(e -> !this.types.contains(e))
                        .distinct()
                        .collect(Collectors.toList());

        // Removes the types that are not needed
        for (AddressType addyType : toRemove) {
            String query
                    = "DELETE FROM address_relation "
                    + "WHERE type=Cast(? AS address_type) "
                    + "AND address_id=?";

            update(query, addyType.name().toLowerCase(), id);
        }

        // Adds the types that are needed
        for (AddressType addyType : toAdd) {
            String query
                    = "INSERT INTO address_relation (type, address_id) "
                    + "VALUES "
                    + " (Cast(? AS address_type), ?)";

            update(query, addyType.name().toLowerCase(), id);
        }

        this.types = new ArrayList(unqiueTypes);
    }

    /**
     * Returns whether the given address is of a given type
     *
     * @param type address type
     * @return whether or not the address is of that type
     */
    public boolean isType(AddressType type) {
        return types.contains(type);
    }

    @Override
    public String toString() {
        return "Address{" + "id=" + id + ", street=" + street + ", city=" + city + ", state=" + state + ", zipCode=" + zipCode + ", apartmentNumber=" + apartmentNumber + ", types=" + types + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Address)) {
            return false;
        }

        Address oAddress = (Address) o;

        return oAddress.id == id;
    }
}
