package ui;

import dao.UserDAO;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    JTextField userField;
    JPasswordField passField;
    boolean isLoggingIn = false;

    public LoginFrame() {
        setTitle("Inventory System - Login");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setSize(500, 300);
        setLocationRelativeTo(null); // center screen
        setDefaultCloseOperation(EXIT_ON_CLOSE);

       setLayout(new BorderLayout());

// 🔷 LEFT PANEL (Professional Look)
JPanel leftPanel = new JPanel();
leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
leftPanel.setBackground(new Color(30, 41, 59)); // dark navy

// spacing
leftPanel.add(Box.createVerticalStrut(80));

// Welcome Text
JLabel title = new JLabel("Welcome Back");
title.setAlignmentX(Component.CENTER_ALIGNMENT);
title.setForeground(Color.WHITE);
title.setFont(new Font("Segoe UI", Font.BOLD, 28));

JLabel subtitle = new JLabel("Manage your inventory smartly");
subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
subtitle.setForeground(Color.LIGHT_GRAY);
subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));

leftPanel.add(title);
leftPanel.add(Box.createVerticalStrut(10));
leftPanel.add(subtitle);

// Image
leftPanel.add(Box.createVerticalStrut(30));
ImageIcon icon = new ImageIcon("src/assets/login.png"); // simpler path
Image img = icon.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
JLabel imgLabel = new JLabel(new ImageIcon(img));
imgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

leftPanel.add(imgLabel);

// 🔷 RIGHT PANEL (Clean Form)
JPanel rightPanel = new JPanel(new GridBagLayout());
rightPanel.setBackground(Color.WHITE);

GridBagConstraints gbc = new GridBagConstraints();
gbc.insets = new Insets(15, 15, 15, 15);
gbc.fill = GridBagConstraints.HORIZONTAL;

//Login LOGO


gbc.gridx = 0;
gbc.gridy = 0;
gbc.gridwidth = 2;
//MODIFIED
// 🔷 IMAGE
ImageIcon loginImage = new ImageIcon("assets/login.png");
//Image img = loginImage.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
JLabel imageLabel =  createImageLabel("assets/Welcome.jpg", 300, 200);
imageLabel.setHorizontalAlignment(JLabel.CENTER);

gbc.gridx = 0;
gbc.gridy = 0;
gbc.gridwidth = 2;
rightPanel.add(imageLabel, gbc);

// 🔷 LOGIN TITLE
JLabel loginTitle = new JLabel("Login");
loginTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
loginTitle.setHorizontalAlignment(JLabel.CENTER);

gbc.gridy = 1;
rightPanel.add(loginTitle, gbc);

// Username
gbc.gridy++;
gbc.gridwidth = 1;
rightPanel.add(new JLabel("Username"), gbc);

gbc.gridx = 1;
userField = new JTextField(15);
userField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
rightPanel.add(userField, gbc);

// Password
gbc.gridx = 0;
gbc.gridy++;
rightPanel.add(new JLabel("Password"), gbc);

gbc.gridx = 1;
passField = new JPasswordField(15);
passField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
rightPanel.add(passField, gbc);

// Buttons
JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
btnPanel.setBackground(Color.WHITE);

JButton loginBtn = new JButton("Login");
JButton registerBtn = new JButton("Register");

// styling buttons
loginBtn.setBackground(new Color(37, 99, 235));
loginBtn.setForeground(Color.WHITE);

registerBtn.setBackground(new Color(16, 185, 129));
registerBtn.setForeground(Color.WHITE);

btnPanel.add(loginBtn);
btnPanel.add(registerBtn);

JButton forgotBtn = new JButton("Forgot Password?");
forgotBtn.setFocusPainted(false);
forgotBtn.setBorderPainted(false);
forgotBtn.setContentAreaFilled(false);
forgotBtn.setForeground(Color.BLUE);

btnPanel.add(forgotBtn);

gbc.gridx = 0;
gbc.gridy++;
gbc.gridwidth = 2;
rightPanel.add(btnPanel, gbc);

// 🔷 SPLIT
JSplitPane splitPane = new JSplitPane(
        JSplitPane.HORIZONTAL_SPLIT,
        leftPanel,
        rightPanel
);

splitPane.setDividerLocation(400);
splitPane.setEnabled(false);

add(splitPane, BorderLayout.CENTER);

        // 🔷 Actions
        loginBtn.addActionListener(e -> login());

        registerBtn.addActionListener(e -> {
            new RegisterFrame();
        });

        forgotBtn.addActionListener(e -> openForgotPassword());

        setVisible(true);
    }

    // 🔥 LOGIN LOGIC
   private void login() {

    if (isLoggingIn) return;   // 🔥 prevents multiple clicks
    isLoggingIn = true;

    String user = userField.getText().trim();
    String pass = new String(passField.getPassword()).trim();

    String role = new UserDAO().login(user, pass);

    if (role == null) {
        JOptionPane.showMessageDialog(this, "Invalid Login");
        isLoggingIn = false;
    } 
    else if (role.equals("admin")) {
        JOptionPane.showMessageDialog(this, "Welcome Admin");
        new DashboardFrame();
        dispose();   // 🔥 important
    } 
    else {
        JOptionPane.showMessageDialog(this, "Welcome User");
        new UserDashboard(user);
        dispose();   // 🔥 important
    }
}
private JLabel createImageLabel(String path, int w, int h) {
    ImageIcon icon = new ImageIcon(path);
    Image img = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
    return new JLabel(new ImageIcon(img));
}
private void openForgotPassword() {

    JTextField userField = new JTextField();
    JPasswordField newPassField = new JPasswordField();

    Object[] message = {
            "Username:", userField,
            "New Password:", newPassField
    };

    int option = JOptionPane.showConfirmDialog(
            this,
            message,
            "Reset Password",
            JOptionPane.OK_CANCEL_OPTION
    );

    if (option == JOptionPane.OK_OPTION) {

        String username = userField.getText().trim();
        String newPass = new String(newPassField.getPassword()).trim();

        if (username.isEmpty() || newPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fields cannot be empty!");
            return;
        }

        try {
            java.sql.Connection con = db.DBConnection.getConnection();

            String query = "UPDATE users SET password=? WHERE username=?";
            java.sql.PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1, newPass);
            ps.setString(2, username);

            int updated = ps.executeUpdate();

            if (updated > 0) {
                JOptionPane.showMessageDialog(this, "Password Updated Successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "User not found!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
}