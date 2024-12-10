package tables.address;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import tables.Table;

/**
 * Class that manages all data related to the address and address_relation
 * tables
 *
 * @author karsten
 */
public class Address extends Table {

    private static Map<Integer, Address> cache = new HashMap<>();

    private int addressId;
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private String apartmentNumber;
    private List<AddressType> types;

    /**
     * Initializes an Address object
     *
     * @param rs results set query of address joined with address_relation table
     */
    protected Address(ResultSet rs) {
        try {
            addressId = rs.getInt("address_id");
            street = rs.getString("street");
            city = rs.getString("city");
            state = rs.getString("state");
            zipCode = rs.getString("zip_code");
            country = rs.getString("country");
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
        if (cache.containsKey(id)) {
            return cache.get(id);
        }

        String query
                = "SELECT a.address_id, street, city, state, zip_code, apt_number, country, type FROM address a "
                + "INNER JOIN address_relation ar "
                + "ON a.address_id = ar.address_id "
                + "WHERE a.address_id = ?";

        ResultSet rs = select(query, id);
        Address result = new Address(rs);

        cache.put(id, result);

        return result;
    }

    /**
     * Method that create a new Address in the database
     *
     * @param street street and building code
     * @param city city
     * @param state state
     * @param zipCode zip code or postal code
     * @param country country
     * @param aptNumber apartment number
     * @param types list of the ways this address will be used
     * @return address object
     */
    public static Address createAddress(String street, String city, String state, String zipCode, String country, String aptNumber, List<AddressType> types) {
        String insertQuery
                = "INSERT INTO address (street, city, state, country, zip_code, apt_number) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        int id = insert(insertQuery, street, city, state, country, zipCode, aptNumber);

        String insertType
                = "INSERT INTO address_relation (address_id, type) "
                + "VALUES (?, Cast(? AS address_type))";

        for (AddressType type : types) {
            insert(insertType, id, type.name().toLowerCase());
        }

        return fromID(id);
    }

    /**
     * Method that create a new Address in the database
     *
     * @param street street and building code
     * @param city city
     * @param state state
     * @param country country
     * @param zipCode zip code or postal code
     * @param types list of the ways this address will be used
     * @return address object
     */
    public static Address createAddress(String street, String city, String state, String zipCode, String country, List<AddressType> types) {
        return createAddress(street, city, state, zipCode, country, null, types);
    }

    public int getId() {
        return addressId;
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
        update(query, street, addressId);

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
        update(query, city, addressId);

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
        update(query, state, addressId);

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
        update(query, zipCode, addressId);

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
        update(query, apartmentNumber, addressId);

        this.apartmentNumber = apartmentNumber;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        String query
                = "UPDATE address "
                + "SET country=? "
                + "WHERE address_id=?;";

        // Run the update
        update(query, apartmentNumber, addressId);

        this.country = country;
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

            update(query, addyType.name().toLowerCase(), addressId);
        }

        // Adds the types that are needed
        for (AddressType addyType : toAdd) {
            String query
                    = "INSERT INTO address_relation (type, address_id) "
                    + "VALUES "
                    + " (Cast(? AS address_type), ?)";

            update(query, addyType.name().toLowerCase(), addressId);
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
        return "Address{" + "id=" + addressId + ", street=" + street + ", city=" + city + ", state=" + state + ", zipCode=" + zipCode + ", country=" + country + ", apartmentNumber=" + apartmentNumber + ", types=" + types + '}';
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
        final Address other = (Address) obj;
        return this.addressId == other.addressId;
    }

}
