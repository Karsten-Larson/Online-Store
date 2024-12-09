package onlinestore;

import tables.order.Order;
import java.sql.*;
import tables.order.OrderStatus;

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
//        Address addy;
////        Address addy = Address.createAddress("2640 Springfield", "Bismarck", "Noth Dakota", "58503", Arrays.asList(types));
//        addy = Address.fromID(5);
//        System.out.println(addy);
//        AddressType[] types = new AddressType[]{AddressType.BILLING, AddressType.SHIPPING};
////        Address o = Address.createAddress("2640 Springfield", "Bismarck", "Noth Dakota", "58503", Arrays.asList(types));
//        Address o = Address.fromID(5);
//        o.setTypes(Arrays.asList(types));
//
//        System.out.println(o);


        Order o = Order.createOrder(1, 2, 3, OrderStatus.DELIVERED, new Date(124, 10, 27));
        System.out.println(o);
    }
}
