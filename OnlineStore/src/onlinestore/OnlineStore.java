package onlinestore;

import java.util.List;
import tables.customer.Customer;
import tables.distributor.Distributor;
import tables.order.Order;
import tables.order.OrderItem;
import tables.order.OrderStatus;
import tables.product.Inventory;
import tables.product.Product;
import tables.product.ProductType;
import tables.wishlist.Wishlist;
import tables.wishlist.WishlistItem;
import view.Menu;
import static view.Mappings.*;
import tables.product.Inventory;
import tables.product.Product;

import view.Utilities;

/**
 *
 * @author karsten
 */
public class OnlineStore {

    private static Menu<Product, Void> getDetailedViewProduct() {
        return new Menu<Product, Void>("Product Detail View").stateDisplay(DETAILED_MAP_PRODUCT);
    }

    private static Menu<List<Product>, Void> getDetailedViewProducts() {
        return new Menu<List<Product>, Void>("Products Detail View").stateDisplay(DETAILED_MAP_PRODUCTS);
    }

    private static Menu<Order, Void> getAddressEditOrder() {
        return new Menu<Order, Void>("Edit Address") {
            @Override
            protected void performAction() {
                parent.getState().getPayment().getBillAddress().setStreet(Utilities.getString("Street: ", 100));
                parent.getState().getPayment().getBillAddress().setCity(Utilities.getString("City: ", 50));
                parent.getState().getPayment().getBillAddress().setState(Utilities.getString("State: ", 50));
                parent.getState().getPayment().getBillAddress().setCountry(Utilities.getString("Country: ", 50));
                parent.getState().getPayment().getBillAddress().setZipCode(Utilities.getString("Zip Code: ", 10));
                parent.getState().getPayment().getBillAddress().setApartmentNumber(Utilities.getString("Apartment Number: ", 20));
            }
        }.confirmation();
    }

    private static Menu<Distributor, Void> getAddressDistributorEdit() {
        return new Menu<Distributor, Void>("Edit Address") {
            @Override
            protected void performAction() {
                parent.getState().getAddress().setStreet(Utilities.getString("Street: ", 100));
                parent.getState().getAddress().setCity(Utilities.getString("City: ", 50));
                parent.getState().getAddress().setState(Utilities.getString("State: ", 50));
                parent.getState().getAddress().setCountry(Utilities.getString("Country: ", 50));
                parent.getState().getAddress().setZipCode(Utilities.getString("Zip Code: ", 10));
                parent.getState().getAddress().setApartmentNumber(Utilities.getString("Apartment Number: ", 20));
            }
        }.confirmation();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Menu showProducts = new Menu<Void, Product>("Show Products") {
            @Override
            protected void performAction() {
                selectItem(Inventory.listAllProducts(), "Select a product: ");
            }
        }.selectionDisplay(MAP_PRODUCT);

        Menu showProduct = new Menu<Product, Void>("Detail Show Product").stateDisplay(DETAILED_MAP_PRODUCT);

        Menu homeMenu = new Menu<Void, Void>("Home Menu");

        // ----- Customer View -----
        Menu customerView = new Menu<Void, Customer>("Customer View") {
            @Override
            protected void performAction() {
                selectItem(Customer.getAllCustomers(), "Select a customer: ");
            }
        }.selectionDisplay(MAP_CUSTOMER);

        // Detailed Customer View
        Menu customerDetailView = new Menu<Customer, Void>("Customer Detail View").stateDisplay(DETAILED_MAP_CUSTOMER);

        // Product View
        Menu customerProductView = new Menu<Customer, Product>("View Products") {
            @Override
            protected void performAction() {
                selectItem(Inventory.listAllProducts(), "Select a product: ");
            }
        }.selectionDisplay(MAP_PRODUCT);

        Menu customerProductToOrder = new Menu<Product, Order>("Add to Order") {
            @Override
            protected void performAction() {
                selectItem(((Menu<?, Customer>) parent.getParent()).getState().getOrders(), "Select an order: ");

                int quantity = parent.getState().getQuantity();

                if (quantity != 0) {
                    try {
                        state.addItem(parent.getState(), Utilities.getInt("Input a quantity [0," + quantity + "]: ", 0, quantity));
                    } catch (RuntimeException ex) {
                        System.out.println("Product already in the order.");
                    }
                } else {
                    System.out.println("Cannot add a product with nothing in stock.");
                }
            }
        }.selectionDisplay(MAP_ORDER);

        Menu customerProductToWishlist = new Menu<Product, Wishlist>("Add to Wishlist") {
            @Override
            protected void performAction() {
                selectItem(((Menu<?, Customer>) parent.getParent()).getState().getWishlists(), "Select a wishlist: ");

                int quantity = parent.getState().getQuantity();

                if (quantity != 0) {
                    try {
                        state.addItem(parent.getState(), Utilities.getInt("Input a quantity [0," + quantity + "]: ", 0, quantity));
                    } catch (RuntimeException ex) {
                        System.out.println("Product already in the wishlist.");
                    }
                } else {
                    System.out.println("Cannot add a product with nothing in stock.");
                }
            }
        }.selectionDisplay(MAP_WISHLIST);

        // View Wishlist
        Menu customerViewWishlists = new Menu<Customer, Wishlist>("View Wishlists") {
            @Override
            protected void performAction() {
                selectItem(parent.getState().getWishlists(), "Select a wishlist: ");
            }
        }.selectionDisplay(MAP_WISHLIST);

        Menu customerViewDetailedWishlist = new Menu<Wishlist, Void>("Wishlist Detail View").stateDisplay(DETAILED_MAP_WISHLIST_FUNCTION);

        Menu customerDeleteWishlistItem = new Menu<Wishlist, WishlistItem>("Delete Item") {
            @Override
            protected void performAction() {
                selectItem(parent.getState().getItems(), "Select an item to delete: ");

                parent.getState().removeItem(state);
                state = null;
            }
        }.selectionDisplay(MAP_WISHLIST_ITEM).confirmation();

        // Delete Order
        Menu customerDeleteOrder = new Menu<Customer, Order>("Delete Order") {
            @Override
            protected void performAction() {
                selectItem(parent.getState().getOrders(), "Select an order to delete: ");

                parent.getState().removeOrder(state);
                state = null;
            }
        }.selectionDisplay(MAP_ORDER).confirmation();

        // View Orders
        Menu customerViewOrders = new Menu<Customer, Order>("View Orders") {
            @Override
            protected void performAction() {
                selectItem(parent.getState().getOrders(), "Select an order: ");
            }
        }.selectionDisplay(MAP_ORDER);

        Menu customerViewDetailedOrder = new Menu<Order, Void>("Order Detail View").stateDisplay(DETAILED_MAP_ORDER);

        Menu customerDeleteOrderItem = new Menu<Order, OrderItem>("Delete Item") {
            @Override
            protected void performAction() {
                selectItem(parent.getState().getItems(), "Select an item to delete: ");

                parent.getState().removeItem(state);
                state = null;
            }
        }.selectionDisplay(MAP_ORDER_ITEM).confirmation();

        // Edit Wishlist Name
        Menu customerEditWishlistName = new Menu<Customer, Wishlist>("Edit Wishlist Name") {
            @Override
            protected void performAction() {
                selectItem(parent.getState().getWishlists(), "Select a wishlist to edit the name of: ");

                state.setName(Utilities.getString("New name: ", 50));
            }
        }.selectionDisplay(MAP_WISHLIST).confirmation();

        // Delete Wishlist
        Menu customerDeleteWishlist = new Menu<Customer, Wishlist>("Delete Wishlist") {
            @Override
            protected void performAction() {
                selectItem(parent.getState().getWishlists(), "Select a wishlist to delete: ");

                parent.getState().removeWishlist(state);
                state = null;
            }
        }.selectionDisplay(MAP_WISHLIST).confirmation();

        // Create Order
        Menu customerCreateOrder = new Menu<Customer, Order>("Create Order") {
            @Override
            protected void performAction() {
                parent.getState().addOrder(Utilities.createPaymentInfo().getPaymentId(), Utilities.createShippingAddress().getId(), OrderStatus.SHIPPED);
            }
        }.confirmation();

        // Create Order
        Menu customerCreateWishlist = new Menu<Customer, Wishlist>("Create Wishlist") {
            @Override
            protected void performAction() {
                parent.getState().addWishlist(Utilities.getString("Wishlist name: ", 50));
            }
        }.confirmation();

        // Finishing Customer View
        customerView
                .add(customerDetailView)
                .add(customerProductView
                        .add(customerProductToOrder)
                        .add(customerProductToWishlist)
                        .add(getDetailedViewProduct()))
                .add(customerCreateOrder)
                .add(customerViewOrders
                        .add(customerViewDetailedOrder)
                        .add(getAddressEditOrder())
                        .add(customerDeleteOrderItem))
                .add(customerDeleteOrder)
                .add(customerCreateWishlist)
                .add(customerViewWishlists
                        .add(customerViewDetailedWishlist)
                        .add(customerDeleteWishlistItem))
                .add(customerEditWishlistName)
                .add(customerDeleteWishlist);

        // ----- Distributor View -----
        Menu distributorView = new Menu<Void, Distributor>("Distributor View") {
            @Override
            protected void performAction() {
                selectItem(Distributor.getAllDistributors(), "Select a distributor: ");
            }
        }.selectionDisplay(MAP_DISTRIBUTOR);

        Menu detialedView = new Menu<Distributor, Void>("Detailed Distributor View").stateDisplay(DETAILED_MAP_DISTRIBUTOR);

        Menu distributorViewProducts = new Menu<Distributor, Product>("Distributor Products") {
            @Override
            protected void performAction() {
                selectItem(parent.getState().getProducts(), "Select a product: ");
            }
        }.selectionDisplay(MAP_PRODUCT);

        Menu distributorCreateProduct = new Menu<Distributor, Product>("Create Product") {
            @Override
            protected void performAction() {
                try {
                    parent.getState().addProduct(
                            Utilities.getString("Name: ", 50),
                            Utilities.getString("Description: ", 255),
                            Utilities.getInt("Quantity: ", 0),
                            Utilities.getDouble("Unit Price: ", 0)
                    );
                } catch (RuntimeException ex) {
                    System.out.println("Product of the same name already exists.");
                }
            }
        }.selectionDisplay(MAP_PRODUCT).confirmation();

        Menu distributorDeleteProduct = new Menu<Distributor, Product>("Delete Product") {
            @Override
            protected void performAction() {
                if (parent.getState().getProducts().isEmpty()) {
                    System.out.println("There are no products to delete");
                    return;
                }

                selectItem(parent.getState().getProducts(), "Select a product to delete: ");

                parent.getState().removeProduct(state);
                state = null;
            }
        }.selectionDisplay(MAP_PRODUCT).confirmation();

        Menu productEditName = new Menu<Product, Void>("Edit Name") {
            @Override
            protected void performAction() {
                parent.getState().setName(Utilities.getString("Name: ", 50));
            }
        }.confirmation();
        Menu productEditDescription = new Menu<Product, Void>("Edit Description") {
            @Override
            protected void performAction() {
                parent.getState().setDescription(Utilities.getString("Description: ", 255));
            }
        }.confirmation();
        Menu productEditPrice = new Menu<Product, Void>("Edit Price") {
            @Override
            protected void performAction() {
                parent.getState().setUnitPrice(Utilities.getDouble("Price: ", 0));
            }
        }.confirmation();
        Menu productEditQuantity = new Menu<Product, Void>("Edit Quantity") {
            @Override
            protected void performAction() {
                parent.getState().setQuantity(Utilities.getInt("Quantity: ", 0));
            }
        }.confirmation();
        Menu productAddCategory = new Menu<Product, Void>("Add Category") {
            @Override
            protected void performAction() {
                try {
                    parent.getState().addCategory(Utilities.getString("Category: ", 50));
                } catch (RuntimeException ex) {
                    System.out.println("Category already exists on the product.");
                }
            }
        }.confirmation();
        Menu productRemoveCategory = new Menu<Product, ProductType>("Remove Category") {
            @Override
            protected void performAction() {
                if (parent.getState().getCategories().isEmpty()) {
                    System.out.println("Product has not categores to remove.");
                    return;
                }

                selectItem(parent.getState().getCategories(), "Select a category to remove: ");

                parent.getState().removeCategory(state);
                state = null;
            }
        }.selectionDisplay(MAP_PRODUCT_TYPE).confirmation();

        // Create Distributor View
        distributorView
                .add(detialedView)
                .add(getAddressDistributorEdit())
                .add(distributorViewProducts
                        .add(getDetailedViewProduct())
                        .add(productEditName)
                        .add(productEditDescription)
                        .add(productAddCategory)
                        .add(productRemoveCategory)
                        .add(productEditQuantity)
                        .add(productEditPrice))
                .add(distributorCreateProduct)
                .add(distributorDeleteProduct);

        // ------ Add Customer -------
        Menu addCustomer = new Menu<Void, Customer>("Create Customer") {
            @Override
            protected void performAction() {
                state = Utilities.createCustomer();

            }
        }.confirmation();

        // ------ Add Distributor -------
        Menu addDistributor = new Menu<Void, Distributor>("Create Distributor") {
            @Override
            protected void performAction() {
                state = Utilities.createDistributor();
            }
        }.confirmation();

        // ------ Delete Customer -------
        Menu deleteCustomer = new Menu<Void, Customer>("Delete Customer") {
            @Override
            protected void performAction() {
                selectItem(Customer.getAllCustomers(), "Select a customer to delete: ");

                state.deleteCustomer();
            }
        }.confirmation().selectionDisplay(MAP_CUSTOMER);

        // ------ Add Distributor -------
        Menu deleteDistributor = new Menu<Void, Distributor>("Delete Distributor") {
            @Override
            protected void performAction() {
                selectItem(Distributor.getAllDistributors(), "Select a distributor to delete: ");

                state.deleteDistributor();
            }
        }.confirmation().selectionDisplay(MAP_DISTRIBUTOR);

        // ------ View Products --------
        Menu productRankings = new Menu<Void, Void>("Product View");

        Menu productMostPurchased = new Menu<Void, Product>("Most Purchased") {
            @Override
            protected void performAction() {
                state = Inventory.mostPurchasedProduct();
            }
        }
                .add(getDetailedViewProduct());

        Menu productExpensivePurchased = new Menu<Void, Product>("Most Expensive") {
            @Override
            protected void performAction() {
                state = Inventory.mostExpensiveProduct();
            }
        }
                .add(getDetailedViewProduct());

        Menu productHighStock = new Menu<Void, Product>("Highest in Stock") {
            @Override
            protected void performAction() {
                state = Inventory.highestProductInStock();
            }
        }
                .add(getDetailedViewProduct());

        Menu productInRange = new Menu<Void, List<Product>>("Product in Price Range") {
            @Override
            protected void performAction() {
                double min = Utilities.getDouble("Min Price: ", 0);
                double max = Utilities.getDouble("Max Price: ", min);
                state = Inventory.listAllProductsInPriceRange(min, max);
            }
        }.add(getDetailedViewProducts());
        Menu productByCategory = new Menu<Void, ProductType>("Product in Category") {
            @Override
            protected void performAction() {
                selectItem(ProductType.getAllProductTypes(), "Select a category: ");
            }
        }.selectionDisplay(MAP_PRODUCT_TYPE).add(new Menu<ProductType, Product>("Show Products") {
            @Override
            protected void performAction() {
                selectItem(Inventory.listAllProductsInCategory(parent.getState().getCategoryId()), "Select a product: ");
            }
        }.selectionDisplay(MAP_PRODUCT).add(getDetailedViewProduct())
        );

        productRankings
                .add(productMostPurchased)
                .add(productExpensivePurchased)
                .add(productHighStock)
                .add(productInRange)
                .add(productByCategory);

        // ------ Home Menu ---------
        homeMenu
                .add(customerView)
                .add(distributorView)
                .add(showProducts
                        .add(showProduct))
                .add(addCustomer)
                .add(addDistributor)
                .add(deleteCustomer)
                //                .add(deleteDistributor)
                .add(productRankings)
                .run();

    }
}
