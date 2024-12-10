package onlinestore;

import tables.order.Order;

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
        Order o = Order.fromID(1);
        System.out.println(o);
    }
}
