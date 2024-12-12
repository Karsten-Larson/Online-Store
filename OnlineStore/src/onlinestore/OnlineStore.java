package onlinestore;

import tables.product.Inventory;
import tables.product.Product;
import view.Utilities;

/**
 *
 * @author karsten
 */
public class OnlineStore {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        /* 
        This file will be changing until the final objects are finalized.
        It's okay to delete this stuff 
         */
//        Address a = Address.fromID(1);
//        System.out.println(a);
//        a.setCity("St. Paul");
//        a.setState("Minnesota");
//        System.out.println(a);
//        Order o = Order.fromID(1);
//        System.out.println(o);
//        o.deleteOrder();
//        System.out.println(o);
//        if (o.removeItem()) {
//            System.out.println(o);
//        }
//        Product p = Product.fromID(2);
//        System.out.println(p);
//        p.deleteProduct();
//        System.out.println(p);
//        p.addCategory("Electronics");
//        System.out.println(p);
//            Utilities.getInt("Select an integer (1-5): ", 1, 5);
//        Product p = Utilities.selectItem(
//                Inventory.listAllProductsInCategory(1)
//        //                Inventory.listAllProducts()
//        );
//        System.out.println(p);
        System.out.println(Inventory.highestProductInStock());
    }
}
