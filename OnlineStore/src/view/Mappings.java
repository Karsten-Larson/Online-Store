package view;

import java.util.List;
import java.util.function.Function;
import tables.address.Address;
import tables.customer.Customer;
import tables.distributor.Distributor;
import tables.order.Order;
import tables.order.OrderItem;
import tables.payment.PaymentInfo;
import tables.product.Product;
import tables.product.ProductType;
import tables.wishlist.Wishlist;
import tables.wishlist.WishlistItem;

/**
 *
 * @author karsten
 */
public class Mappings {

    public final static Function<OrderItem, String> MAP_ORDER_ITEM = (i) -> String.format("Name: %s; Quantity: %d; Unit Price: %.2f", i.getProduct().getName(), i.getQuantity(), i.getUnitPrice());
    public final static Function<WishlistItem, String> MAP_WISHLIST_ITEM = (w) -> String.format("Name: %s; Quantity: %d; Unit Price: %.2f", w.getProduct().getName(), w.getQuantity(), w.getProduct().getCurrentUnitPrice());
    public final static Function<Order, String> MAP_ORDER = (o) -> String.format("Order ID: %d; Order Total Price: %.2f",
            o.getOrderId(), o.getTotalPrice());
    public final static Function<ProductType, String> MAP_PRODUCT_TYPE = (p) -> String.format("%s", p.getCategoryName());
    public final static Function<Customer, String> MAP_CUSTOMER = (c) -> String.format("%s %s", c.getFirstName(), c.getLastName());
    public final static Function<Distributor, String> MAP_DISTRIBUTOR = (d) -> String.format("Distributor ID: %d", d.getID());
    public final static Function<Product, String> MAP_PRODUCT = (p) -> String.format("Name: %s; Unit Price: %.2f; Categories: %s", p.getName(), p.getCurrentUnitPrice(), Mappings.MAP_PRODUCT_TYPES.apply(p.getCategories()));
    public final static Function<Wishlist, String> MAP_WISHLIST = (w) -> String.format("Name: %s; Order Total Price: %.2f", w.getName(), w.getTotalPrice());
    public final static Function<List<ProductType>, String> MAP_PRODUCT_TYPES = (l) -> {
        StringBuilder categories = new StringBuilder();

        for (ProductType t : l) {
            categories.append(MAP_PRODUCT_TYPE.apply(t));

            if (l.getLast() != t) {
                categories.append(", ");
            }
        }

        return categories.toString();
    };

    // Detailed Mappings
    public final static Function<Address, String> DETAILED_MAP_ADDRESS = (a) -> String.format("Address ID: %d\nStreet: %s\nCity: %s\nState: %s\nCountry: %s\nZip Code: %s\nApartment Number: %s\n",
            a.getId(), a.getStreet(), a.getCity(), a.getState(), a.getCountry(), a.getZipCode(), a.getApartmentNumber());
    public final static Function<PaymentInfo, String> DETAILED_PAYMENT_INFO = (p) -> String.format("Payment ID: %d\nBilling Address: \n%s\nFirst Name: %s\nLast Name: %s\nCard Number: %d\nExpiration Date: %s\nCVV: %d\n",
            p.getPaymentId(), DETAILED_MAP_ADDRESS.apply(p.getBillAddress()), p.getFirstName(), p.getLastName(), p.getCardNumber(), p.getExpDate().toString(), p.getCvv());
    public final static Function<Order, String> DETAILED_MAP_ORDER = (o)
            -> {
        StringBuilder items = new StringBuilder();

        for (OrderItem item : o.getItems()) {
            items.append("- ");
            items.append(MAP_ORDER_ITEM.apply(item));
            items.append("\n");
        }

        return String.format("Order ID: %d\nOrder Date: %s\nOrder Status: %s\nOrder Total Price: %.2f\nItems:\n%s\nPayment Info:\n%s\n",
                o.getOrderId(), o.getOrderDate().toString(), o.getStatus().toString(), o.getTotalPrice(), items.toString(), DETAILED_PAYMENT_INFO.apply(o.getPayment()));
    };
    public final static Function<Distributor, String> DETAILED_MAP_DISTRIBUTOR = (d) -> String.format("Distributor ID: %s\nAddress:\n%s", d.getID(), DETAILED_MAP_ADDRESS.apply(d.getAddress()));
    public final static Function<Wishlist, String> DETAILED_MAP_WISHLIST_FUNCTION = (o)
            -> {
        StringBuilder items = new StringBuilder();

        for (WishlistItem item : o.getItems()) {
            items.append("- ");
            items.append(MAP_WISHLIST_ITEM.apply(item));
            items.append("\n");
        }

        return String.format("Wishlist ID: %d\nName: %s\nOrder Total Price: %.2f\nItems:\n%s",
                o.getWishlistId(), o.getName(), o.getTotalPrice(), items.toString());
    };
    public final static Function<Product, String> DETAILED_MAP_PRODUCT = (p)
            -> String.format("Product ID: %d\nName; %s\nDescription: %s\nCategories: %s\nUnit Price: %.2f\nStock Left: %d\nIn Stock: %b\nDistributor ID: %d",
                    p.getID(), p.getName(), p.getDescription(), MAP_PRODUCT_TYPES.apply(p.getCategories()), p.getCurrentUnitPrice(), p.getQuantity(), p.isInStock(), p.getDistributorId());
    public final static Function<Customer, String> DETAILED_MAP_CUSTOMER = (c)
            -> String.format("Customer ID: %d\nFirst Name; %s\nLast Name: %s\nEmail: %s\nPhone Number: %s\n# Orders: %d\n# Wishlists: %d",
                    c.getID(), c.getFirstName(), c.getLastName(), c.getEmail(), c.getPhone(), c.getOrders().size(), c.getWishlists().size());
    public final static Function<List<Product>, String> DETAILED_MAP_PRODUCTS = (l) -> {
        StringBuilder items = new StringBuilder();

        for (Product item : l) {
            items.append("- ");
            items.append(MAP_PRODUCT.apply(item));
            items.append("\n");
        }
        
        return items.toString();
    };
}
