package tables.product;

import tables.Table;
import java.util.List;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
/**
 *
 * @author Kaitlyn
 */
public class PriceChange extends Table {
    private static final Map<Integer, PriceChange> cache = new HashMap<>();
    
    private int priceChangeId;
    private int productId;
    private double newProductPrice;
    private double oldProductPrice;
    private String priceChageDate;
    
    protected PriceChange(ResultSet rs){
        
    }
    //list all price changes
    public static List<PriceChange> listPriceChanges() {
        String query = "SELECT * FROM product_price_change";
        
        ResultSet rs = select(query);
        
        return map(rs, PriceChange::new);
    }    
        
    //List all price changes for product
    public static List<PriceChange> listPriceChangeForProduct(){
        String query = "SELECT * FROM product_price_change"
                + "WHERE product_id = ?";
        
        ResultSet rs = select(query);
        
        return map(rs, PriceChange::new);
    }
    //list all price changes on date
    public static List<PriceChange> listPriceChangeForDate(){
        String query = "SELECT * FROM product_price_change"
                + "WHERE price_change_date = ?";
        
        ResultSet rs = select(query);
        
        return map(rs, PriceChange::new);
    }
    //list largest price change
    
    public static PriceChange listLargestPriceChange(){
        String query = "SELECT MAX(new_product_price - old_product_price) FROM product_price_change";
        
        ResultSet rs = select(query);
        return new PriceChange(rs);
    }
        
}
