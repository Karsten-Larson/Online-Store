package tables.wishlist;

import java.util.HashMap;
import java.util.Map;
import tables.Table;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import tables.product.Product;

/**
 *
 * @author Kaitlyn
 */
public class Wishlist extends Table {

    private static final Map<Integer, Wishlist> cache = new HashMap<>();

    private int wishlistId;
    private int customerId;
    private String name;
    private List<WishlistItem> items;

    protected Wishlist(ResultSet rs) {
        try {
            wishlistId = rs.getInt("wishlist_id");
            customerId = rs.getInt("customer_id");
            name = rs.getString("wishlist_name");

            items = new ArrayList<>();
            do {
                int id = rs.getInt("wishlist_item_id");

                // Value is null
                if (id == 0) {
                    break;
                }
                items.add(WishlistItem.fromID(id));
            } while (rs.next());
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public static Wishlist fromID(int id) {
        if (cache.containsKey(id)) {
            return cache.get(id);
        }

        String query
                = "SELECT w.wishlist_id, wishlist_name, customer_id, wishlist_item_id FROM wishlist w "
                + "LEFT JOIN wishlist_items wi ON w.wishlist_id = wi.wishlist_id "
                + "WHERE w.wishlist_id = ?";

        ResultSet rs = select(query, id);
        Wishlist result = new Wishlist(rs);

        cache.put(id, result);
        return result;
    }

    public static Wishlist createWishlist(int customerId, String name) {
        String insertQuery
                = "INSERT INTO wishlist (customer_id, wishlist_name) "
                + "VALUES (?, ?)";

        int id = insert(insertQuery, customerId, name);

        return fromID(id);
    }

    public void deleteWishlist() {
        clearItems();

        String query
                = "DELETE FROM wishlist "
                + "WHERE wishlist_id=?";

        delete(query, wishlistId);
    }

    /**
     * Adds item to the wishlist
     *
     * @param product_id product ID
     * @param quantity quantity
     */
    public void addItem(int product_id, int quantity) {
        items.add(WishlistItem.createItem(wishlistId, Product.fromID(product_id), quantity));
    }

    /**
     * Adds item to the wishlist
     *
     * @param product product
     * @param quantity quantity
     */
    public void addItem(Product product, int quantity) {
        items.add(WishlistItem.createItem(wishlistId, product.getID(), quantity));
    }

    /**
     * Removes the last inputted wishlist item
     *
     * @return whether or not a wishlist item was removed
     */
    public boolean removeItem() {
        if (items.isEmpty()) {
            return false;
        }

        WishlistItem wishlistItem = WishlistItem.fromID(items.getLast().getWishlistItemId());

        wishlistItem.deleteItem();
        items.remove(wishlistItem);

        return true;
    }

    /**
     * Removes the last inputted wishlist item
     *
     * @param wishlistItem product to be removed from the wishlist
     * @return whether or not a wishlist item was removed
     */
    public boolean removeItem(WishlistItem wishlistItem) {
        wishlistItem.deleteItem();
        items.remove(wishlistItem);

        return true;
    }

    public double getTotalPrice() {
        double total = 0;

        for (WishlistItem item : items) {
            total += item.getProduct().getCurrentUnitPrice() * item.getQuantity();
        }

        return total;
    }

    /**
     * Clears and deletes all items from the wishlist
     */
    public void clearItems() {
        for (WishlistItem item : items) {
            item.deleteItem();
        }

        items = new ArrayList<>();
    }

    public int getWishlistId() {
        return wishlistId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public List<WishlistItem> getItems() {
        return new ArrayList<>(items);
    }

    public void setCustomerId(int customerId) {
        String query
                = "UPDATE wishlist "
                + "SET customer_id=? "
                + "WHERE wishlist_id=?;";

        // Run the update
        update(query, customerId, wishlistId);

        this.customerId = customerId;
    }

    public void setName(String name) {
        String query
                = "UPDATE wishlist "
                + "SET wishlist_name=? "
                + "WHERE wishlist_id=?;";

        // Run the update
        update(query, name, wishlistId);

        this.name = name;
    }

    @Override
    public String toString() {
        return "Wishlist{" + "wishlistId=" + wishlistId + ", customerId=" + customerId + ", name=" + name + ", items=" + items + '}';
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
        final Wishlist other = (Wishlist) obj;
        return this.wishlistId == other.wishlistId;
    }

}
