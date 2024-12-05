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

        /* 
        This file will be changing until the final objects are finalized.
        It's okay to delete this stuff 
        */
        
        Address addy = Address.fromID(1);

        System.out.println(addy);
        addy.setCity("Cheyenne");
        addy.setState("Wyoming");
        addy.setZipCode("82002");
        addy.setApartmentNumber("515 APT");
        System.out.println(addy);
        
        AddressType[] types = new AddressType[]{AddressType.SHIPPING};
        addy.setTypes(Arrays.asList(types));

        addy = Address.fromID(1);
        System.out.println(addy);
    }

}
