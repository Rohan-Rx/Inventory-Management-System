package ui;

import db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class RequestFrame extends JFrame {

    JTable table;
    DefaultTableModel model;

    public RequestFrame() {
        setTitle("Manage Requests");
        setSize(600, 400);
        setLocationRelativeTo(null);

        model = new DefaultTableModel(
                new String[]{"Req ID", "Username", "Product ID", "Status"}, 0
        );

        table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);

        JButton approveBtn = new JButton("Approve");
        JButton rejectBtn = new JButton("Reject");

        JPanel btnPanel = new JPanel();
        btnPanel.add(approveBtn);
        btnPanel.add(rejectBtn);

        add(sp, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        approveBtn.addActionListener(e -> updateRequest("APPROVED"));
        rejectBtn.addActionListener(e -> updateRequest("REJECTED"));

        loadRequests();

        setVisible(true);
    }

    private void loadRequests() {
        model.setRowCount(0);

        try {
            Connection con = DBConnection.getConnection();
            String query = "SELECT * FROM requests";

            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getInt("product_id"),
                        rs.getString("status")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateRequest(String status) {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a request!");
            return;
        }

        int id = (int) model.getValueAt(row, 0);

        try {
            Connection con = DBConnection.getConnection();
            String query = "UPDATE requests SET status=? WHERE id=?";

            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, status);
            ps.setInt(2, id);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Updated!");
            loadRequests();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}