package ui;

import model.Product;
import service.ProductService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class DashboardFrame extends JFrame {

    int selectedId = -1;
    JLabel totalAmountLabel;
    JTextField nameField, qtyField, priceField;
    JTable table;
    JTextField idField;
    DefaultTableModel model;
    JTable requestTable;
    DefaultTableModel requestModel;
    JLabel totalLabel, pendingLabel, approvedLabel;
    JPanel requestPanel = new JPanel(new BorderLayout());

    ProductService service = new ProductService();

   public DashboardFrame() {

    setTitle("Inventory System - Admin Dashboard");
    setExtendedState(JFrame.MAXIMIZED_BOTH);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLayout(new BorderLayout());

    // 🔷 ===== HEADER PANEL =====
    JPanel headerPanel = new JPanel(new BorderLayout());
    headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    headerPanel.setBackground(Color.WHITE);

    JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
    leftPanel.setOpaque(false);

    JLabel userLabel = new JLabel("Welcome, Admin");
    userLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));

    // 👤 Profile Icon
    ImageIcon icon = new ImageIcon("assets/profile.png");
    Image img = icon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
    userLabel.setIcon(new ImageIcon(img));
    userLabel.setIconTextGap(10);

    JLabel roleLabel = new JLabel("(Admin)");
    roleLabel.setForeground(Color.GRAY);

    leftPanel.add(userLabel);
    leftPanel.add(roleLabel);
JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

JButton logoutBtn = new JButton("Logout");
logoutBtn.setPreferredSize(new Dimension(100, 30));
logoutBtn.setBackground(new Color(231, 76, 60));
logoutBtn.setForeground(Color.WHITE);

btnPanel.add(logoutBtn);

add(btnPanel, BorderLayout.SOUTH); // ✅ ONLY ONCE

    //add(headerPanel, BorderLayout.NORTH);

    // 🔷 ===== STATS PANEL =====
    JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 10));
    statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    totalLabel = new JLabel("Total Products: 0", JLabel.CENTER);
    totalLabel.setOpaque(true);
    totalLabel.setBackground(new Color(52, 152, 219));
    totalLabel.setForeground(Color.WHITE);

    pendingLabel = new JLabel("Pending: 0", JLabel.CENTER);
    pendingLabel.setOpaque(true);
    pendingLabel.setBackground(new Color(243, 156, 18));
    pendingLabel.setForeground(Color.WHITE);

    approvedLabel = new JLabel("Approved: 0", JLabel.CENTER);
    approvedLabel.setOpaque(true);
    approvedLabel.setBackground(new Color(46, 204, 113));
    approvedLabel.setForeground(Color.WHITE);

    statsPanel.add(totalLabel);
    statsPanel.add(pendingLabel);
    statsPanel.add(approvedLabel);

   // 🔷 FORM PANEL (same as yours)
JPanel formPanel = new JPanel(new GridBagLayout());
formPanel.setBorder(BorderFactory.createTitledBorder("Product Details"));
formPanel.setBackground(Color.WHITE);

GridBagConstraints gbc = new GridBagConstraints();
gbc.insets = new Insets(10, 10, 10, 10);
gbc.fill = GridBagConstraints.HORIZONTAL;

// Labels
gbc.gridx = 0; gbc.gridy = 0;
formPanel.add(new JLabel("ID"), gbc);

gbc.gridx = 1;
formPanel.add(new JLabel("Name"), gbc);

gbc.gridx = 2;
formPanel.add(new JLabel("Quantity"), gbc);

gbc.gridx = 3;
formPanel.add(new JLabel("Price"), gbc);

// Fields
idField = new JTextField(10);
nameField = new JTextField(10);
qtyField = new JTextField(10);
priceField = new JTextField(10);

Dimension fieldSize = new Dimension(120, 30);
idField.setPreferredSize(fieldSize);
nameField.setPreferredSize(fieldSize);
qtyField.setPreferredSize(fieldSize);
priceField.setPreferredSize(fieldSize);

gbc.gridy = 1;

gbc.gridx = 0;
formPanel.add(idField, gbc);

gbc.gridx = 1;
formPanel.add(nameField, gbc);

gbc.gridx = 2;
formPanel.add(qtyField, gbc);

gbc.gridx = 3;
formPanel.add(priceField, gbc);

// Add Button
JButton addBtn = new JButton("Add", resizeIcon("assets/add.png"));
addBtn.setPreferredSize(new Dimension(120, 35));
addBtn.setBackground(new Color(46, 204, 113));
addBtn.setForeground(Color.WHITE);

gbc.gridx = 4;
formPanel.add(addBtn, gbc);

// 🔷 WRAPPER (IMPORTANT)
JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
wrapper.add(formPanel);

// 🔷 TOP PANEL
JPanel topPanel = new JPanel(new BorderLayout());
topPanel.add(statsPanel, BorderLayout.NORTH);
topPanel.add(wrapper, BorderLayout.CENTER);

// 🔥 CORRECT ADD
add(topPanel, BorderLayout.NORTH);

    // 🔷 ===== PRODUCT TABLE =====
    model = new DefaultTableModel(new String[]{"ID","Name","Qty","Price"}, 0);
    table = new JTable(model);
    table.setRowHeight(25);

    JScrollPane scrollPane = new JScrollPane(table);

    // 🔷 ===== REQUEST TABLE =====
    requestModel = new DefaultTableModel(
        new String[]{"Req ID", "Username", "Product ID", "Qty", "Status"}, 0
    );

    requestTable = new JTable(requestModel);
    JScrollPane reqScroll = new JScrollPane(requestTable);

    requestTable.getSelectionModel().addListSelectionListener(e -> calculateTotal());

    JPanel requestPanel = new JPanel(new BorderLayout());
    requestPanel.setBorder(BorderFactory.createTitledBorder("Requests"));

    // 🔥 Total Amount
    totalAmountLabel = new JLabel("Total Amount: 0");
    totalAmountLabel.setFont(new Font("Arial", Font.BOLD, 16));

    requestPanel.add(totalAmountLabel, BorderLayout.NORTH);
    requestPanel.add(reqScroll, BorderLayout.CENTER);

    JButton approveBtn = new JButton("Approve");
    JButton rejectBtn = new JButton("Reject");
    JButton deleteReqBtn = new JButton("Delete");

    JPanel actionPanel = new JPanel();
    actionPanel.add(approveBtn);
    actionPanel.add(rejectBtn);
    actionPanel.add(deleteReqBtn);

    requestPanel.add(actionPanel, BorderLayout.SOUTH);

    // 🔷 ===== SPLIT =====
    JSplitPane splitPane = new JSplitPane(
        JSplitPane.VERTICAL_SPLIT,
        scrollPane,
        requestPanel
    );
    splitPane.setDividerLocation(300);

    add(splitPane, BorderLayout.CENTER);

    // 🔷 ===== BUTTON PANEL =====
    //JPanel btnPanel = new JPanel();

    JButton viewBtn = new JButton("Refresh", resizeIcon("assets/view.png"));
    JButton updateBtn = new JButton("Update", resizeIcon("assets/update.png"));
    JButton deleteBtn = new JButton("Delete", resizeIcon("assets/delete.png"));

    //MANAGR USER//
    JButton manageUsersBtn = new JButton("Manage Users");
    btnPanel.add(manageUsersBtn);
    

    btnPanel.add(viewBtn);
    btnPanel.add(updateBtn);
    btnPanel.add(deleteBtn);

   // add(btnPanel, BorderLayout.SOUTH);

    // 🔷 ===== ACTIONS =====
    addBtn.addActionListener(e -> addProduct());

    manageUsersBtn.addActionListener(e -> {
    new UserManagementFrame();
});

    viewBtn.addActionListener(e -> {
        loadTable();
        loadRequests();
        calculateTotal();
        loadStats();
    });

    deleteBtn.addActionListener(e -> deleteProduct());
    updateBtn.addActionListener(e -> updateProduct());
    approveBtn.addActionListener(e -> updateRequest("APPROVED"));
    rejectBtn.addActionListener(e -> updateRequest("REJECTED"));
    deleteReqBtn.addActionListener(e -> deleteRequest());

    logoutBtn.addActionListener(e -> {
        dispose();
        new LoginFrame();
    });

    // 🔷 LOAD DATA
    loadTable();
    loadRequests();
    loadStats();

    setVisible(true);
}
    // 🔥 ICON RESIZE METHOD
     private ImageIcon resizeIcon(String path) {
    ImageIcon icon = new ImageIcon(path);  // ✅ FIXED (no getResource)
    Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
    return new ImageIcon(img);
}

    // 🔷 Add
   private void addProduct() {
    try {
        int id = Integer.parseInt(idField.getText());  // ✅ only once
        String name = nameField.getText();
        int qty = Integer.parseInt(qtyField.getText());
        double price = Double.parseDouble(priceField.getText());

        service.addProductWithId(id, name, qty, price);

        JOptionPane.showMessageDialog(this, "Product Added!");
        loadTable();
        clearFields();

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Invalid Input!");
    }
}
//request method
    private void loadRequests() {
    requestModel.setRowCount(0);

    try {
        java.sql.Connection con = db.DBConnection.getConnection();
        String query = "SELECT * FROM requests";
        java.sql.PreparedStatement ps = con.prepareStatement(query);
        java.sql.ResultSet rs = ps.executeQuery();

        while (rs.next()) {
           requestModel.addRow(new Object[]{
    rs.getInt("id"),
    rs.getString("username"),
    rs.getInt("product_id"),
    rs.getInt("quantity"),   // 🔥 NEW
    rs.getString("status")
});
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
}

// Calculate total
    private void calculateTotal() {

    int row = requestTable.getSelectedRow();

    // 🔥 If nothing selected → select first row
    if (row == -1 && requestTable.getRowCount() > 0) {
        requestTable.setRowSelectionInterval(0, 0);
        row = 0;
    }

    if (row == -1) {
        totalAmountLabel.setText("Total Amount: ₹ 0");
        return;
    }

    int productId = (int) requestModel.getValueAt(row, 2);
    int qty = (int) requestModel.getValueAt(row, 3);

    try {
        java.sql.Connection con = db.DBConnection.getConnection();

        String query = "SELECT price FROM products WHERE id=?";
        java.sql.PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, productId);

        java.sql.ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            double price = rs.getDouble("price");
            double total = price * qty;

            totalAmountLabel.setText("Total Amount: ₹ " + String.format("%,.2f", total));
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
}   

    // 🔷 Load Table
    private void loadTable() {
        model.setRowCount(0);
        List<Product> list = service.getAllProducts();

        for (Product p : list) {
            model.addRow(new Object[]{
                    p.getId(),
                    p.getName(),
                    p.getQuantity(),
                    p.getPrice()
            });
        }
    }

    // 🔷 Delete
    private void deleteProduct() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a product first!");
            return;
        }

        int id = (int) model.getValueAt(row, 0);
        service.deleteProduct(id);

        JOptionPane.showMessageDialog(this, "Deleted!");
        loadTable();
        clearFields();
    }

    // 🔷 Update
    private void updateProduct() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Select a product first!");
            return;
        }

        try {
            String name = nameField.getText();
            int qty = Integer.parseInt(qtyField.getText());
            double price = Double.parseDouble(priceField.getText());

            service.updateProduct(selectedId, name, qty, price);

            JOptionPane.showMessageDialog(this, "Product Updated!");
            loadTable();
            clearFields();
            selectedId = -1;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid Input!");
        }
    }

    //Load stats
    private void loadStats() {
    try {
        java.sql.Connection con = db.DBConnection.getConnection();

        // Total Products
        String q1 = "SELECT COUNT(*) FROM products";
        java.sql.PreparedStatement ps1 = con.prepareStatement(q1);
        java.sql.ResultSet rs1 = ps1.executeQuery();
        if (rs1.next()) {
            totalLabel.setText("Total Products: " + rs1.getInt(1));
        }

        // Pending Requests
        String q2 = "SELECT COUNT(*) FROM requests WHERE status='PENDING'";
        java.sql.PreparedStatement ps2 = con.prepareStatement(q2);
        java.sql.ResultSet rs2 = ps2.executeQuery();
        if (rs2.next()) {
            pendingLabel.setText("Pending: " + rs2.getInt(1));
        }

        // Approved Requests
        String q3 = "SELECT COUNT(*) FROM requests WHERE status='APPROVED'";
        java.sql.PreparedStatement ps3 = con.prepareStatement(q3);
        java.sql.ResultSet rs3 = ps3.executeQuery();
        if (rs3.next()) {
            approvedLabel.setText("Approved: " + rs3.getInt(1));
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
}


    //update method
  private void updateRequest(String status) {
    int row = requestTable.getSelectedRow();

    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Select a request!");
        return;
    }

    int reqId = (int) requestModel.getValueAt(row, 0);
    int productId = (int) requestModel.getValueAt(row, 2);
    int reqQty = (int) requestModel.getValueAt(row, 3);

    try {
        java.sql.Connection con = db.DBConnection.getConnection();

        // 🔥 If approved → reduce stock
        if (status.equals("APPROVED")) {

            // check stock
            String check = "SELECT quantity FROM products WHERE id=?";
            PreparedStatement psCheck = con.prepareStatement(check);
            psCheck.setInt(1, productId);
            ResultSet rs = psCheck.executeQuery();

            if (rs.next()) {
                int currentStock = rs.getInt("quantity");

                if (currentStock < reqQty) {
                    JOptionPane.showMessageDialog(this, "Not enough stock!");
                    return;
                }

                // 🔥 update stock
                String updateStock = "UPDATE products SET quantity=quantity-? WHERE id=?";
                PreparedStatement psStock = con.prepareStatement(updateStock);
                psStock.setInt(1, reqQty);
                psStock.setInt(2, productId);
                psStock.executeUpdate();
            }
        }

        // update request status
        String query = "UPDATE requests SET status=? WHERE id=?";
        PreparedStatement ps = con.prepareStatement(query);

        ps.setString(1, status);
        ps.setInt(2, reqId);
        ps.executeUpdate();

        JOptionPane.showMessageDialog(this, "Request " + status);

        loadRequests();
        loadTable();   // 🔥 refresh product stock
        loadStats();

    } catch (Exception e) {
        e.printStackTrace();
    }
}

//Delete request Method for admin 
private void deleteRequest() {
    int row = requestTable.getSelectedRow();

    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Select a request!");
        return;
    }

    // 🔥 ✅ SAFETY LOGIC HERE
    String status = requestModel.getValueAt(row, 3).toString();

    if (status.equalsIgnoreCase("APPROVED")) {
        JOptionPane.showMessageDialog(this, "Cannot delete approved request!");
        return;
    }

    // 🔥 Confirmation
    int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete this request?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION
    );

    if (confirm != JOptionPane.YES_OPTION) return;

    int reqId = (int) requestModel.getValueAt(row, 0);

    try {
        java.sql.Connection con = db.DBConnection.getConnection();
        String query = "DELETE FROM requests WHERE id=?";
        java.sql.PreparedStatement ps = con.prepareStatement(query);

        ps.setInt(1, reqId);
        ps.executeUpdate();

        JOptionPane.showMessageDialog(this, "Request Deleted!");
        loadRequests();
        loadStats();

    } catch (Exception e) {
        e.printStackTrace();
    }
}
    private void clearFields() {
        nameField.setText("");
        qtyField.setText("");
        priceField.setText("");
    }
    
}