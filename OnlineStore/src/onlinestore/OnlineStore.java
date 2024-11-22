package onlinestore;

import java.util.Arrays;
import tables.address.Address;
import tables.address.AddressType;

/**
 *
 * @author karsten
 */
public class OnlineStore {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Address addy = Address.fromID(1);

        System.out.println(addy);
        addy.setStreet("University Dr N");
        System.out.println(addy);
        
        AddressType[] types = new AddressType[]{AddressType.BILLING, AddressType.SHIPPING};
        addy.setTypes(Arrays.asList(types));

        addy = Address.fromID(1);
        System.out.println(addy);
    }

}
