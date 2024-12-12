package tables.payment;

import tables.Table;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.sql.*;

/**
 *
 * @author Kaitlyn
 */
public class PaymentInfo extends Table{
    private static Map<Integer, PaymentInfo> cache = new HashMap<>();
    
    private int paymentId;
    private int customerId;
    private int billingAddressId;
    private String firstName;
    private String lastName;
    private int cardNumber;
    private String date;
    private int cvv;
    
    protected PaymentInfo(ResultSet rs){
        try{
            paymentId = rs.getInt("payment_id");
            customerId = rs.getInt("customer_id");
            billingAddressId = rs.getInt("billing_address_id");
            firstName = rs.getString("firstname");
            lastName = rs.getString("lastname");
            cardNumber = rs.getInt("card_number");
            date = rs.getString("exp_date");
            cvv = rs.getInt("cvv"); 
        }
        catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
    
     public static PaymentInfo fromID(int id) {
        if (cache.containsKey(id)) {
            return cache.get(id);
        }

        String query
                = "SELECT payment_id,customer_id, billing_address_id, firstname, lastname, card_number, exp_date, cvv "
                + "FROM payment_info "
                + "WHERE payment_id =?";

        ResultSet rs = select(query, id);
        PaymentInfo result = new PaymentInfo(rs);

        cache.put(id, result);

        return result;
    }
    
    //View Payment info
    public static List<PaymentInfo> viewPayment(){
        String query = "SELECT * FROM payment_info";
        
        ResultSet rs = select(query);
        
        return map(rs, PaymentInfo::new);
    }
    
    //add payment
    public PaymentInfo addPayment(int customer_id, int billing_address_id, String firstname, String lastname, int card_number, String exp_date, int cvv){
        String addQuery = "INSERT INTO payment_info(customer_id, billing_address_id, firstname, lastname, card_number, exp_date, cvv)"
                + "VALUES(?,?,?,?,?,?,?)";
        
        int id = insert(addQuery, customer_id, billing_address_id, firstname, lastname, card_number, exp_date, cvv);
        
        return fromID(id);
    }
    //remove payment
    public void removePayment(int payment_id){
        String removeQuery = "DELETE FROM payment_info"
                + "WHERE payment_id = ?";
        
        delete(removeQuery, paymentId);
    }
    //update payment
    public void updatePayment(int payment_id,int billing_address_id, int card_number, String exp_date, int cvv){
        String updateQuery = "UPDATE payment_info"
                + "SET billing_address_id = ?, card_number = ?, exp_date = ?, cvv = ?"
                + "WHERE payment_id = ?";
        
        update(updateQuery,payment_id, billing_address_id, card_number, exp_date, cvv);
        this.billingAddressId = billing_address_id;
        this.cardNumber = card_number;
        this.date = exp_date;
        this.cvv = cvv;
    }
    //view expired payment
    
    public List<PaymentInfo> viewExpiredPayment(){
        String expiredQuery = "SELECT * from payment_info"
                + "WHERE exp_date < NOW()";
        ResultSet rs = select(expiredQuery);
        
        return map(rs,PaymentInfo::new);
    }
}
