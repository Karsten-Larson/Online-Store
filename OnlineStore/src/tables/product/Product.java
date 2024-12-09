package tables.product;

import java.sql.*;
import java.util.Objects;
import tables.Table;
import tables.DatabaseManager;
import java.util.Scanner;

/**
 * Class that manages all data related to the Product table.
 * 
 * @author Owen Sailer
 */
public class Product extends Table {

    private int id;
    private String name;
    private String description;
    private int quantity;
    private boolean inStock;
    private double unitPrice;
    private Integer distributorId;
    
    public Product(String string, int aInt) { }
    
    public Product(int id, String name, String description, int quantity, double unitPrice, Integer distributorId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.inStock = quantity > 0;
        this.unitPrice = unitPrice;
        this.distributorId = distributorId;
    }

    public int addProduct() throws SQLException {
        String insertProduct = "INSERT INTO product (product_name, product_description, product_quantity, product_in_stock, current_unit_price, distributor_id) "
                             + "VALUES (?, ?, ?, ?, ?, ?)";

        PreparedStatement stmt = DatabaseManager.getConnection().prepareStatement(insertProduct);

        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter product name: ");
        String newproductName = scanner.nextLine();

        System.out.print("Enter product description: ");
        String newproductDescription = scanner.nextLine();

        System.out.print("Enter product quantity: ");
        int newproductQuantity = scanner.nextInt();

        boolean newproductInStock = (newproductQuantity != 0);

        System.out.print("Enter current unit price: ");
        double newunitPrice = scanner.nextDouble();

        System.out.print("Enter distributor ID: ");
        int newdistributorId = scanner.nextInt();

        stmt.setString(1, newproductName);
        stmt.setString(2, newproductDescription);
        stmt.setInt(3, newproductQuantity);
        stmt.setBoolean(4, newproductInStock);
        stmt.setDouble(5, newunitPrice);
        stmt.setInt(6, newdistributorId);

        int count = stmt.executeUpdate();

        return count;
    }


    public int removeProduct(int productId) throws SQLException {
        String deleteProduct = "DELETE FROM product WHERE product_id = ?";

        PreparedStatement stmt = DatabaseManager.getConnection().prepareStatement(deleteProduct);
        stmt.setInt(1, productId);

        int count = stmt.executeUpdate();
        return count;
    }

    public int updateProduct(int productId) throws SQLException {
        String updateProduct = "UPDATE product SET "
                + "product_name = ?, "
                + "product_description = ?, "
                + "product_quantity = ?, "
                + "current_unit_price = ?, "
                + "distributor_id = ? "
                + "WHERE product_id = ?";
        
        PreparedStatement stmt = DatabaseManager.getConnection().prepareStatement(updateProduct);

        Scanner scan = new Scanner(System.in);
        System.out.print("Enter new product name: ");
        String productname = scan.nextLine();
        System.out.print("Enter new product description: ");
        String productdescription = scan.nextLine();
        System.out.print("Enter new product quantity: ");
        int productquantity = scan.nextInt();
        System.out.print("Enter new unit price: ");
        double productunitPrice = scan.nextDouble();
        System.out.print("Enter new distributor ID: ");
        int productdistributorId = scan.nextInt();

        stmt.setString(1, productname);
        stmt.setString(2, productdescription);
        stmt.setInt(3, productquantity);
        stmt.setDouble(4, productunitPrice);
        stmt.setInt(5, productdistributorId);
        stmt.setInt(6, productId); 

        return stmt.executeUpdate();
    }


    public int getProductId() {
        return id;
    }

    public String getProductName() {
        return name;
    }

    public String getProductDescription() {
        return description;
    }

    public int getProductQuantity() {
        return quantity;
    }

    public boolean isProductInStock() {
        return inStock;
    }

    public double getCurrentUnitPrice() {
        return unitPrice;
    }

    public Integer getDistributorId() {
        return distributorId;
    }
    
    public void setUnitPrice(double unitPrice) throws SQLException {
        String query = "UPDATE product "
                     + "SET current_unit_price=? "
                     + "WHERE product_id=?";

        update(query, unitPrice, id);

        this.unitPrice = unitPrice;
    }

    public void setQuantity(int quantity) throws SQLException {
        String query = "UPDATE product "
                     + "SET product_quantity=? "
                     + "WHERE product_id=?";

        update(query, quantity, id);

        this.quantity = quantity;
        this.inStock = quantity > 0;
    }

    public void setName(String name) throws SQLException {
        String query = "UPDATE product "
                     + "SET product_name=? "
                     + "WHERE product_id=?";

        update(query, name, id);

        this.name = name;
    }

    public void setDescription(String description) throws SQLException {
        String query = "UPDATE product "
                     + "SET product_description=? "
                     + "WHERE product_id=?";

        update(query, description, id);

        this.description = description;
    }

    public void setDistributorId(Integer distributorId) throws SQLException {
        String query = "UPDATE product "
                     + "SET distributor_id=? "
                     + "WHERE product_id=?";

        update(query, distributorId, id);

        this.distributorId = distributorId;
    }

    public void setInStock(boolean inStock) throws SQLException {
        String query = "UPDATE product "
                     + "SET product_in_stock=? "
                     + "WHERE product_id=?";

        update(query, inStock, id);

        this.inStock = inStock;
    }


    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", quantity=" + quantity +
                ", inStock=" + inStock +
                ", unitPrice=" + unitPrice +
                ", distributorId=" + distributorId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Product)) return false;
        Product other = (Product) o;
        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
