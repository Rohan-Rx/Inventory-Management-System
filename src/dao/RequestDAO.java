package dao;

import db.DBConnection;
import java.sql.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RequestDAO {

    public void addRequest(String username, int productId, int quantity) {
        try {
            Connection con = DBConnection.getConnection();
            String query = "INSERT INTO requests(username, product_id, quantity, status) VALUES(?,?,?,?)";

            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, username);
            ps.setInt(2, productId);
            ps.setInt(3, quantity);   // 🔥 NEW
            ps.setString(4, "PENDING");

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}