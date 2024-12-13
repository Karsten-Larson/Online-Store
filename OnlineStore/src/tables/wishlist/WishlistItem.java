package tables.wishlist;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import tables.Table;
import tables.product.Product;

/**
 * Class that manages all data related to the order item table
 *
 * @author karsten
 */
public class WishlistItem extends Table {

    private static final Map<Integer, WishlistItem> cache = new HashMap<>();

    private int wishlistItemId;
    private int wishlistId;
    private int productId;
    private int quantity;

    /**
     * Creates an OrderItem from a ResultSet
     *
     * @param rs ResultSet row representing the OrderItem
     */
    protected WishlistItem(ResultSet rs) {
        try {
            wishlistItemId = rs.getInt("wishlist_item_id");
            wishlistId = rs.getInt("wishlist_id");
            productId = rs.getInt("product_id");
            quantity = rs.getInt("quantity");

        } catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public static WishlistItem fromID(int id) {
        if (cache.containsKey(id)) {
            return cache.get(id);
        }

        String query
                = "SELECT wishlist_item_id, wishlist_id, product_id, quantity FROM wishlist_items "
                + "WHERE wishlist_item_id = ?";

        ResultSet rs = select(query, id);
        WishlistItem result = new WishlistItem(rs);

        cache.put(id, result);

        return result;
    }

    /**
     * Method that create a new WishlistItem in the database
     *
     * @param wishlistId wishlist id
     * @param productId product id
     * @param quantity quantity
     * @return WishlistItem instance
     */
    public static WishlistItem createItem(int wishlistId, int productId, int quantity) {
        String insertQuery
                = "INSERT INTO wishlist_items (wishlist_id, product_id, quantity) "
                + "VALUES (?, ?, ?)";

        int id = insert(insertQuery, wishlistId, productId, quantity);

        return fromID(id);
    }

    /**
     * Method that create a new WishlistItem in the database
     *
     * @param wishlistId order id
     * @param product product
     * @param quantity quantity
     * @return WishlistItem instance
     */
    public static WishlistItem createItem(int wishlistId, Product product, int quantity) {
        return WishlistItem.createItem(wishlistId, product.getID(), quantity);
    }

    /**
     * Deletes an order item from the database
     */
    public void deleteItem() {
        String query
                = "DELETE FROM wishlist_items "
                + "WHERE wishlist_item_id = ?";

        delete(query, wishlistItemId);
    }

    public int getWishlistItemId() {
        return wishlistItemId;
    }

    public int getWishlistId() {
        return wishlistId;
    }

    public Wishlist getWishlist() {
        return Wishlist.fromID(wishlistId);
    }

    public int getProductId() {
        return productId;
    }

    public Product getProduct() {
        return Product.fromID(productId);
    }

    public int getQuantity() {
        return quantity;
    }

    public void setWishlistId(int wishlistId) {
        String query
                = "UPDATE wishlist_items "
                + "SET wishlist_id=? "
                + "WHERE wishlist_item_id=?;";

        // Run the update
        update(query, wishlistId, wishlistItemId);

        this.wishlistId = wishlistId;
    }

    public void setWishlist(Wishlist wishlist) {
        setWishlistId(wishlist.getWishlistId());
    }

    public void setProductId(int productId) {
        String query
                = "UPDATE wishlist_items "
                + "SET product_id=? "
                + "WHERE wishlist_item_id=?;";

        // Run the update
        update(query, productId, wishlistItemId);

        this.productId = productId;
    }

    public void setProduct(Product product) {
        setProductId(product.getID());
    }

    public void setQuantity(int quantity) {
        String query
                = "UPDATE wishlist_items "
                + "SET quantity=? "
                + "WHERE wishlist_item_id=?;";

        // Run the update
        update(query, quantity, wishlistItemId);

        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "WishlistItem{" + "wishlistItemId=" + wishlistItemId + ", wishlistId=" + wishlistId + ", productId=" + productId + ", quantity=" + quantity + '}';
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
        final WishlistItem other = (WishlistItem) obj;
        return this.wishlistItemId == other.wishlistItemId;
    }
}
