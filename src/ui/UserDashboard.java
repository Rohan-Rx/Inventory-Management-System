package ui;

import model.Product;
import service.ProductService;
import dao.RequestDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import javax.swing.table.DefaultTableCellRenderer;

public class UserDashboard extends JFrame {

    JTable table;
    DefaultTableModel model;
    String username;
    JTable requestTable;
    DefaultTableModel requestModel;
    JLabel totalBillLabel;

    

    ProductService service = new ProductService();

    //Colored Stats
    class StatusRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {

        Component c = super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);

        String status = value.toString();

        if (status.equalsIgnoreCase("APPROVED")) {
            c.setForeground(new Color(0, 128, 0)); // Green
        } 
        else if (status.equalsIgnoreCase("REJECTED")) {
            c.setForeground(Color.RED);
        } 
        else {
            c.setForeground(new Color(255, 140, 0)); // Orange
        }

        return c;
    }
}

   public UserDashboard(String username) {
    this.username = username;

    setTitle("User Dashboard");
    setExtendedState(JFrame.MAXIMIZED_BOTH);
    setLocationRelativeTo(null);
    setLayout(new BorderLayout());
    //JPanel headerPanel = new JPanel(new BorderLayout());
    //headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
    // 🔷 HEADER PANEL
    JPanel headerPanel = new JPanel(new BorderLayout());
    headerPanel.setPreferredSize(new Dimension(getWidth(), 60));
    headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    headerPanel.setBackground(Color.WHITE);

    // 🔷 LEFT PANEL (USER INFO)
    JPanel leftPanel = new JPanel();
    leftPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 5));
    leftPanel.setOpaque(false);

    // 👤 USERNAME LABEL
    JLabel userLabel = new JLabel("Welcome, " + username);
    userLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
    userLabel.setForeground(Color.BLACK);

    // 👤 PROFILE ICON (DIRECT PATH)
    ImageIcon icon = new ImageIcon("assets/profile.png");
    Image img = icon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
    userLabel.setIcon(new ImageIcon(img));
    userLabel.setIconTextGap(10);

    // 👤 ROLE
    JLabel roleLabel = new JLabel("(User)");
    roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    roleLabel.setForeground(Color.GRAY);

    // 🔥 ADD COMPONENTS (IMPORTANT ORDER)
    leftPanel.add(userLabel);
    leftPanel.add(roleLabel);

    // 🔴 LOGOUT BUTTON
    JButton logoutBtn = new JButton("Logout");
    logoutBtn.setPreferredSize(new Dimension(90, 30));
    logoutBtn.setBackground(new Color(231, 76, 60));
    logoutBtn.setForeground(Color.WHITE);

    // 🔥 ADD TO HEADER
    headerPanel.add(leftPanel, BorderLayout.WEST);
    headerPanel.add(logoutBtn, BorderLayout.EAST);

    // 🔥 ADD HEADER TO FRAME (ONLY ONCE)
    add(headerPanel, BorderLayout.NORTH);
        

    
   
    // 🔷 Product Table
    model = new DefaultTableModel(new String[]{"ID", "Name", "Qty", "Price"}, 0);
    table = new JTable(model);
    JScrollPane sp = new JScrollPane(table);

    // 🔷 Request Table (ONLY ONCE)
   requestModel = new DefaultTableModel(
    new String[]{"Req ID","Product ID","Qty","Price","Status"}, 0
);

    requestTable = new JTable(requestModel);
    requestTable.getColumnModel()
    .getColumn(2)
    .setCellRenderer(new StatusRenderer());
    JScrollPane reqScroll = new JScrollPane(requestTable);

    JPanel reqPanel = new JPanel(new BorderLayout());
    reqPanel.setBorder(BorderFactory.createTitledBorder("My Requests"));
    reqPanel.add(reqScroll, BorderLayout.CENTER);
    totalBillLabel = new JLabel("Total Bill: ₹ 0");
    totalBillLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
    totalBillLabel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

reqPanel.add(totalBillLabel, BorderLayout.SOUTH);

    // 🔷 Buttons
   //JButton logoutBtn = new JButton("Logout");

// 🔥 Make it small
    logoutBtn.setPreferredSize(new Dimension(90, 30));
    logoutBtn.setFocusPainted(false);
    logoutBtn.setBackground(new Color(231, 76, 60));
    logoutBtn.setForeground(Color.WHITE);
    logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
    JButton requestBtn = new JButton("Request Product");
    headerPanel.add(logoutBtn, BorderLayout.EAST);
    JPanel bottomPanel = new JPanel();

//JButton requestBtn = new JButton("Request Product");
JButton invoiceBtn = new JButton("View Invoice");

bottomPanel.add(requestBtn);
bottomPanel.add(invoiceBtn);

add(bottomPanel, BorderLayout.SOUTH);
    // 🔷 Split Layout
    JSplitPane splitPane = new JSplitPane(
        JSplitPane.VERTICAL_SPLIT,
        sp,
        reqPanel
    );

    splitPane.setDividerLocation(200);
    add(splitPane, BorderLayout.CENTER);

    // 🔷 Actions
   requestBtn.addActionListener(e -> {
    requestProduct();
    loadRequests();
    calculateTotalBill();
});
    invoiceBtn.addActionListener(e -> showInvoice());
    logoutBtn.addActionListener(e -> {
    SwingUtilities.invokeLater(() -> {
        dispose();              // close current dashboard
        new LoginFrame();       // open login
    });
});

    // 🔷 Load Data
    loadProducts();
    loadRequests();
    calculateTotalBill();   // ✅ ADD THIS

    setVisible(true);
}

    private void loadProducts() {
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

   private void loadRequests() {
    requestModel.setRowCount(0);

    try {
        java.sql.Connection con = db.DBConnection.getConnection();

        String query =
        "SELECT r.id, r.product_id, r.quantity, r.status, p.price " +
        "FROM requests r JOIN products p ON r.product_id = p.id " +
        "WHERE LOWER(r.username)=LOWER(?) AND r.status='APPROVED'";
        java.sql.PreparedStatement ps = con.prepareStatement(query);

        System.out.println("Current user: " + username);

        ps.setString(1, username);

        java.sql.ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            requestModel.addRow(new Object[]{
                rs.getInt("id"),
                rs.getInt("product_id"),
                rs.getInt("quantity"),
                rs.getDouble("price"),
                rs.getString("status")
            });
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
}
private void showInvoice() {

    JDialog dialog = new JDialog(this, "Invoice", true);
    dialog.setSize(500, 450);
    dialog.setLocationRelativeTo(this);

    JTextArea area = new JTextArea();
    area.setFont(new Font("Monospaced", Font.PLAIN, 14));
    area.setEditable(false);

    StringBuilder bill = new StringBuilder();

    bill.append("=========== INVOICE ===========\n");
    bill.append("User: ").append(username).append("\n");
    bill.append("--------------------------------------\n");
    bill.append(String.format("%-10s %-10s %-10s\n", "ProdID", "Qty", "Price"));
    bill.append("--------------------------------------\n");

    double subtotal = 0;

    for (int i = 0; i < requestModel.getRowCount(); i++) {

        int productId = (int) requestModel.getValueAt(i, 1);
        int qty = (int) requestModel.getValueAt(i, 2);
        double price = (double) requestModel.getValueAt(i, 3);

        double itemTotal = qty * price;
        subtotal += itemTotal;

        bill.append(String.format("%-10d %-10d %-10.2f\n", productId, qty, itemTotal));
    }

    double gst = subtotal * 0.18;   // 18% GST
    double finalTotal = subtotal + gst;

    bill.append("--------------------------------------\n");
    bill.append(String.format("Subtotal: ₹ %.2f\n", subtotal));
    bill.append(String.format("GST (18%%): ₹ %.2f\n", gst));
    bill.append("--------------------------------------\n");
    bill.append(String.format("TOTAL: ₹ %.2f\n", finalTotal));
    bill.append("======================================");

    area.setText(bill.toString());

    dialog.setLayout(new BorderLayout());
    dialog.add(new JScrollPane(area), BorderLayout.CENTER);

    // 🔥 Print Button
    JButton printBtn = new JButton("Print");
    printBtn.addActionListener(e -> {
        try {
            area.print();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    });

    dialog.add(printBtn, BorderLayout.SOUTH);

    dialog.setVisible(true);
}
   private void requestProduct() {
    int row = table.getSelectedRow();

    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Select a product!");
        return;
    }

    int productId = (int) model.getValueAt(row, 0);

    // 🔥 Ask quantity
    String qtyStr = JOptionPane.showInputDialog(this, "Enter Quantity:");

    if (qtyStr == null || qtyStr.isEmpty()) return;

    int qty = Integer.parseInt(qtyStr);

    new RequestDAO().addRequest(username, productId, qty);

    JOptionPane.showMessageDialog(this, "Request Sent!");
    loadRequests();
}
private void calculateTotalBill() {
    double total = 0;

    for (int i = 0; i < requestModel.getRowCount(); i++) {
        int qty = (int) requestModel.getValueAt(i, 2);
        double price = (double) requestModel.getValueAt(i, 3);

        total += qty * price;
    }

    totalBillLabel.setText("Total Bill: ₹ " + total);
}
private ImageIcon resizeIcon(String path, int w, int h) {
    ImageIcon icon = new ImageIcon(path);
    Image img = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
    return new ImageIcon(img);
}
}
