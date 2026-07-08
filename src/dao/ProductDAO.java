package dao;

import db.DBConnection;
import model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public void addProduct(Product product) {
    try {
        Connection con = DBConnection.getConnection();
        String query = "INSERT INTO products(id, name, quantity, price) VALUES(?,?,?,?)";

        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, product.getId());
        ps.setString(2, product.getName());
        ps.setInt(3, product.getQuantity());
        ps.setDouble(4, product.getPrice());

        ps.executeUpdate();

    } catch (Exception e) {
        e.printStackTrace();
    }
}
    public void updateProduct(Product product) {
    try {
        Connection con = DBConnection.getConnection();
        String query = "UPDATE products SET name=?, quantity=?, price=? WHERE id=?";
        
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, product.getName());
        ps.setInt(2, product.getQuantity());
        ps.setDouble(3, product.getPrice());
        ps.setInt(4, product.getId());

        ps.executeUpdate();
        System.out.println("Product Updated!");

    } catch (Exception e) {
        e.printStackTrace();
    }
}

    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        try {
            Connection con = DBConnection.getConnection();
            String query = "SELECT * FROM products";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(query);

            while (rs.next()) {
                list.add(new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("quantity"),
                        rs.getDouble("price")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void deleteProduct(int id) {
        try {
            Connection con = DBConnection.getConnection();
            String query = "DELETE FROM products WHERE id=?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Product Deleted!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}