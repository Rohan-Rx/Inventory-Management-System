package ui;

import dao.UserDAO;

import javax.swing.*;
import java.awt.*;

public class RegisterFrame extends JFrame {

    JTextField userField;
    JPasswordField passField;

    public RegisterFrame() {
        setTitle("Register");
        setSize(300,200);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3,2,10,10));

        panel.add(new JLabel("Username"));
        userField = new JTextField();
        panel.add(userField);

        panel.add(new JLabel("Password"));
        passField = new JPasswordField();
        panel.add(passField);

        JButton registerBtn = new JButton("Register");
        panel.add(registerBtn);

        add(panel);

        registerBtn.addActionListener(e -> register());

        setVisible(true);
    }

    private void register() {
        String user = userField.getText();
        String pass = new String(passField.getPassword());

        new UserDAO().register(user, pass);

        JOptionPane.showMessageDialog(this, "User Registered!");
    }
}