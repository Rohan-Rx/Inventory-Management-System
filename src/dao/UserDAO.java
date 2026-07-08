package dao;

import db.DBConnection;
import java.sql.*;

public class UserDAO {
//Register
    public void register(String username, String password) {
    try {
        Connection con = DBConnection.getConnection();

        String query = "INSERT INTO users(username,password,role) VALUES(?,?,?)";
        PreparedStatement ps = con.prepareStatement(query);

        ps.setString(1, username);
        ps.setString(2, password);
        ps.setString(3, "user");

        ps.executeUpdate();

    } catch (Exception e) {
        e.printStackTrace();
    }
}
//Login
    public String login(String username, String password) {
    try {
        Connection con = DBConnection.getConnection();

        String query = "SELECT role FROM users WHERE username=? AND password=?";
        PreparedStatement ps = con.prepareStatement(query);

        ps.setString(1, username);
        ps.setString(2, password);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getString("role"); // "admin" or "user"
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return null; // ❗ NOT true/false
}
// MANAGE USERS //
// GET ALL USERS
public ResultSet getAllUsers() {
    try {
        Connection con = DBConnection.getConnection();
        String query = "SELECT id, username, role FROM users";
        PreparedStatement ps = con.prepareStatement(query);
        return ps.executeQuery();
    } catch (Exception e) {
        e.printStackTrace();
    }
    return null;
}

// DELETE USER
public void deleteUser(int id) {
    try {
        Connection con = DBConnection.getConnection();
        String query = "DELETE FROM users WHERE id=?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, id);
        ps.executeUpdate();
    } catch (Exception e) {
        e.printStackTrace();
    }
}

// UPDATE ROLE
public void updateRole(int id, String role) {
    try {
        Connection con = DBConnection.getConnection();
        String query = "UPDATE users SET role=? WHERE id=?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, role);
        ps.setInt(2, id);
        ps.executeUpdate();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
public void resetPassword(int id, String newPassword) {
    try {
        Connection con = DBConnection.getConnection();
        String query = "UPDATE users SET password=? WHERE id=?";
        PreparedStatement ps = con.prepareStatement(query);

        ps.setString(1, newPassword);
        ps.setInt(2, id);

        ps.executeUpdate();

    } catch (Exception e) {
        e.printStackTrace();
    }
}
}