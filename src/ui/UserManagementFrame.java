package ui;

import dao.UserDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;

public class UserManagementFrame extends JFrame {

    JTable table;
    DefaultTableModel model;

    public UserManagementFrame() {
        setTitle("Manage Users");
        setSize(600, 400);
        setLocationRelativeTo(null);

        model = new DefaultTableModel(
                new String[]{"ID", "Username", "Role"}, 0
        );

        table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);
        JButton resetPassBtn = new JButton("Reset Password");
       

        JButton refreshBtn = new JButton("Refresh");
        JButton deleteBtn = new JButton("Delete User");
        JButton roleBtn = new JButton("Make Admin");
        

        JPanel btnPanel = new JPanel();
        btnPanel.add(refreshBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(roleBtn);
         btnPanel.add(resetPassBtn);

        add(sp, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        // 🔷 Actions
        refreshBtn.addActionListener(e -> loadUsers());

        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) return;

            int id = (int) model.getValueAt(row, 0);
            new UserDAO().deleteUser(id);
            loadUsers();
        });


        resetPassBtn.addActionListener(e -> {

    int row = table.getSelectedRow();

    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Select a user!");
        return;
    }

    int id = (int) model.getValueAt(row, 0);

    String newPass = JOptionPane.showInputDialog(this, "Enter New Password:");

    if (newPass == null || newPass.trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Password cannot be empty!");
        return;
    }

    new UserDAO().resetPassword(id, newPass);

    JOptionPane.showMessageDialog(this, "Password Updated!");
});

        roleBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) return;

            int id = (int) model.getValueAt(row, 0);
            new UserDAO().updateRole(id, "admin");
            loadUsers();
        });

        loadUsers();
        setVisible(true);
    }

    private void loadUsers() {
        model.setRowCount(0);

        try {
            ResultSet rs = new UserDAO().getAllUsers();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("role")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}