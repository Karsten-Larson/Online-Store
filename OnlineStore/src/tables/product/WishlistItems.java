package tables.product;

import java.util.HashMap;
import java.util.Map;
import tables.Table;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kaitlyn
 */
public class WishlistItems extends Table{

    private static Map<Integer, WishlistItems> cache = new HashMap<>();
    
    private int wishlistItemId;
    private int wishlistId;
    private int productId;
    private int quantity;
    private List<WishlistItems> items;
    
    protected WishlistItems(ResultSet rs){
        try{
            wishlistItemId = rs.getInt("wishlist_item_id");
            wishlistId = rs.getInt("wishlist_id");
            productId = rs.getInt("product_id");
            quantity = rs.getInt("quantity");
            
            items = new ArrayList<>();
            do {
                int id = rs.getInt("wishlist_item_id");

                // Value is null
                if (id == 0) {
                    break;
                }
                items.add(WishlistItems.fromID(id));
            } while (rs.next());
        }
        catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
    
    public static WishlistItems fromID(int id) {
        if (cache.containsKey(id)) {
            return cache.get(id);
        }
        String query = "SELECT wishlist_item_id, wi.wishlist_id, wi.product_id, quantity FROM wishlist_items wi"
                + "RIGHT JOIN wishlist ON wi.wishlist_id = wishlist.wishlist_id"
                + "WHERE wi.wishlist_id = ?";
        ResultSet rs = select(query, id);
        WishlistItems result = new WishlistItems(rs);
        
        cache.put(id, result);
        return result;
    }
        
        
    //add item to wishlist 
    public static WishlistItems addItem(int wishlist_id, int product_id, int quantity){
        String addQuery = "INSERT INTO wishlist_items (wishlist_id,product_id,quantity)"
            + "VALUES (?,?,?)";
        
        int id = insert(addQuery, wishlist_id, product_id, quantity);
        return fromID(id);
    }
    
    
    //remove item
    public void removeItem(int wishlist_id, int product_id){
        String removeQuery = "DELETE FROM wishlist_items"
            + "WHERE wishlist_id = ? AND product_id = ?";
        
        delete(removeQuery, wishlistId, productId);
    }
    //update item
    
    public void updateQuantity(int wishlist_id, int product_id, int quantity){
        String updateQuery = "UPDATE wishlist_items"
                            + "SET quantity = ?"
                            + "WHERE order_id =? AND product_id = ?";
        update(updateQuery, quantity, wishlistId, productId);
        this.quantity = quantity;
    }
    //view wishlist
    
    public WishlistItems viewWishlist(int wishlist_id, int product_id){
        String viewQuery = "SELECT * FROM wishlist_items"
                            + "WHERE order_id = ? AND product_id = ?";
        
        int id = insert(viewQuery, wishlist_id, product_id);
        return fromID(id);
    }
}
