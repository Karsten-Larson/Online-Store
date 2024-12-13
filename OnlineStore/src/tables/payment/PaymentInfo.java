package tables.payment;

import tables.Table;
import java.util.HashMap;
import java.util.Map;
import java.sql.*;
import tables.address.Address;
import tables.customer.Customer;

/**
 *
 * @author Kaitlyn
 */
public class PaymentInfo extends Table {

    private static Map<Integer, PaymentInfo> cache = new HashMap<>();

    private int paymentId;
    private int billingAddressId;
    private String firstName;
    private String lastName;
    private int cardNumber;
    private Date expDate;
    private int cvv;

    protected PaymentInfo(ResultSet rs) {
        try {
            paymentId = rs.getInt("payment_id");
            billingAddressId = rs.getInt("billing_address_id");
            firstName = rs.getString("firstname");
            lastName = rs.getString("lastname");
            cardNumber = rs.getInt("card_number");

            expDate = rs.getDate("exp_date");
            cvv = rs.getInt("cvv");
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public static PaymentInfo fromID(int id) {
        if (cache.containsKey(id)) {
            return cache.get(id);
        }

        String query
                = "SELECT payment_id, billing_address_id, firstname, lastname, card_number, exp_date, cvv "
                + "FROM payment_info "
                + "WHERE payment_id =?";

        ResultSet rs = select(query, id);
        PaymentInfo result = new PaymentInfo(rs);

        cache.put(id, result);

        return result;
    }

    /**
     * Add new Payment information
     *
     * @param billing_address_id billing address id
     * @param firstname first name
     * @param lastname last name
     * @param card_number card number
     * @param exp_date expiration expDate
     * @param cvv cvv
     * @return PaymentInfo object
     */
    public static PaymentInfo createPayment(int billing_address_id, String firstname, String lastname, int card_number, Date exp_date, int cvv) {
        String insertQuery
                = "INSERT INTO payment_info (billing_address_id, firstname, lastname, card_number, exp_date, cvv) "
                + "VALUES(?,?,?,?,?,?)";

        int id = insert(insertQuery, billing_address_id, firstname, lastname, card_number, exp_date, cvv);

        return fromID(id);
    }

    public void deletePayment() {
        String query
                = "DELETE FROM payment_info "
                + "WHERE payment_id=?";

        delete(query, paymentId);
    }

    public int getPaymentId() {
        return paymentId;
    }

    public int getBillingAddressId() {
        return billingAddressId;
    }

    public Address getBillAddress() {
        return Address.fromID(billingAddressId);
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getCardNumber() {
        return cardNumber;
    }

    public Date getExpDate() {
        return new Date(expDate.getTime());
    }

    public int getCvv() {
        return cvv;
    }

    public void setBillingAddressId(int billingAddressId) {
        String query
                = "UPDATE payment_info "
                + "SET billing_address_id=? "
                + "WHERE payment_id=?;";

        // Run the update
        update(query, paymentId, billingAddressId);

        this.billingAddressId = billingAddressId;
    }

    public void setBillingAddress(Address billingAddress) {
        setBillingAddressId(billingAddress.getId());
    }

    public void setFirstName(String firstName) {
        String query
                = "UPDATE payment_info "
                + "SET firstname=? "
                + "WHERE payment_id=?;";

        // Run the update
        update(query, paymentId, firstName);

        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        String query
                = "UPDATE payment_info "
                + "SET lastname=? "
                + "WHERE payment_id=?;";

        // Run the update
        update(query, paymentId, lastName);

        this.lastName = lastName;
    }

    public void setCardNumber(int cardNumber) {
        String query
                = "UPDATE payment_info "
                + "SET card_number=? "
                + "WHERE payment_id=?;";

        // Run the update
        update(query, paymentId, cardNumber);

        this.cardNumber = cardNumber;
    }

    public void setExpDate(Date expDate) {
        String query
                = "UPDATE payment_info "
                + "SET exp_date=? "
                + "WHERE payment_id=?;";

        // Run the update
        update(query, paymentId, expDate);

        this.expDate = expDate;
    }

    public void setCvv(int cvv) {
        String query
                = "UPDATE payment_info "
                + "SET cvv=? "
                + "WHERE payment_id=?;";

        // Run the update
        update(query, paymentId, cvv);

        this.cvv = cvv;
    }

    @Override
    public String toString() {
        return "PaymentInfo{" + "paymentId=" + paymentId + ", billingAddressId=" + billingAddressId + ", firstName=" + firstName + ", lastName=" + lastName + ", cardNumber=" + cardNumber + ", expDate=" + expDate + ", cvv=" + cvv + '}';
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
        final PaymentInfo other = (PaymentInfo) obj;
        return this.paymentId == other.paymentId;
    }
}
