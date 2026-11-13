import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.sql.*;

public class NSP extends JFrame implements ActionListener {

    // --- System State Variables ---
    private static String loggedInUserRole = "Admin";

    // --- GUI Components ---
    JTextField txtId, txtName, txtUnits, txtSearch;
    JComboBox<String> cmbType;
    JTextArea txtReceipt;
    JButton btnCalculate, btnClear, btnView, btnDelete, btnSearch, btnSaveFile, btnExportCSV, btnAnalytics;

    // --- Color Scheme ---
    Color BG_TEAL = new Color(17, 56, 71);
    Color CARD_TEAL = new Color(33, 89, 104);
    Color HEADER_TEAL = new Color(26, 82, 118);
    Color TEXT_LIGHT = new Color(220, 235, 245);
    Color BTN_GOLD = new Color(197, 160, 89);

    public NSP() {
        setTitle("PowerGrid - Enterprise Management System (" + loggedInUserRole + ")");

        // 1. विंडो की फिक्स साइज सेट करें (ना ज्यादा बड़ी, ना ज्यादा छोटी)
        setSize(540, 750);
        setResizable(false); // साइज फिक्स रहेगी ताकि लेआउट बिगड़े नहीं
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 2. पूरे प्रोजेक्ट के चारों तरफ 4px का गोल्ड बॉर्डर लगाने का लॉजिक
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(BG_TEAL);
        mainPanel.setBorder(BorderFactory.createLineBorder(BTN_GOLD, 4)); // यह बॉर्डर केवल प्रोजेक्ट के चारों तरफ रहेगा
        setContentPane(mainPanel);

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBounds(4, 4, 532, 50);
        headerPanel.setBackground(HEADER_TEAL);
        headerPanel.setLayout(null);
        mainPanel.add(headerPanel);

        JLabel headLabel = new JLabel("POWERGRID ENTERPRISE DASHBOARD");
        headLabel.setFont(new Font("Consolas", Font.BOLD, 15));
        headLabel.setForeground(Color.WHITE);
        headLabel.setBounds(110, 12, 350, 25);
        headerPanel.add(headLabel);

        // Form Components
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 12);

        addLabel(mainPanel, "Customer ID:", 30, 70, labelFont);
        txtId = createTextField(170, 70); mainPanel.add(txtId);

        addLabel(mainPanel, "Customer Name:", 30, 105, labelFont);
        txtName = createTextField(170, 105); mainPanel.add(txtName);

        addLabel(mainPanel, "Units Consumed:", 30, 140, labelFont);
        txtUnits = createTextField(170, 140); mainPanel.add(txtUnits);

        addLabel(mainPanel, "Connection Type:", 30, 175, labelFont);
        String[] types = {"Domestic", "Commercial"};
        cmbType = new JComboBox<>(types);
        cmbType.setBounds(170, 175, 220, 25);
        cmbType.setBackground(CARD_TEAL);
        cmbType.setForeground(TEXT_LIGHT);
        mainPanel.add(cmbType);

        // Buttons Row 1
        btnCalculate = createButton("Calculate & Save", 30, 215, 130, 30, new Color(27, 108, 168), Color.WHITE);
        mainPanel.add(btnCalculate);
        btnClear = createButton("Clear", 170, 215, 70, 30, new Color(42, 82, 98), Color.WHITE);
        mainPanel.add(btnClear);
        btnView = createButton("View All", 250, 215, 90, 30, new Color(83, 120, 166), Color.WHITE);
        mainPanel.add(btnView);
        btnAnalytics = createButton("Analytics", 350, 215, 120, 30, BTN_GOLD, Color.BLACK);
        mainPanel.add(btnAnalytics);

        // Search/Delete Row 2
        addLabel(mainPanel, "Search/Delete ID:", 30, 260, labelFont);
        txtSearch = createTextField(170, 260);
        txtSearch.setSize(100, 25);
        mainPanel.add(txtSearch);

        btnSearch = createButton("Search", 280, 260, 80, 25, new Color(189, 195, 199), Color.BLACK);
        mainPanel.add(btnSearch);
        btnDelete = createButton("Delete", 370, 260, 80, 25, new Color(146, 78, 78), Color.WHITE);
        mainPanel.add(btnDelete);

        if (loggedInUserRole.equalsIgnoreCase("Operator")) {
            btnDelete.setEnabled(false);
        }

        // Receipt Display Box
        txtReceipt = new JTextArea();
        txtReceipt.setBounds(30, 300, 460, 320);
        txtReceipt.setBackground(CARD_TEAL);
        txtReceipt.setForeground(TEXT_LIGHT);
        txtReceipt.setFont(new Font("Consolas", Font.BOLD, 13));
        txtReceipt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BTN_GOLD, 2),
                new EmptyBorder(10, 10, 10, 10)
        ));
        txtReceipt.setEditable(false);
        mainPanel.add(txtReceipt);

        // Bottom Actions
        btnSaveFile = createButton("Download Receipt (.txt)", 30, 640, 220, 35, BTN_GOLD, Color.BLACK);
        mainPanel.add(btnSaveFile);
        btnExportCSV = createButton("Export to Excel (.csv)", 270, 640, 220, 35, new Color(46, 139, 87), Color.WHITE);
        mainPanel.add(btnExportCSV);

        // Listeners
        btnCalculate.addActionListener(this);
        btnClear.addActionListener(this);
        btnView.addActionListener(this);
        btnSearch.addActionListener(this);
        btnDelete.addActionListener(this);
        btnAnalytics.addActionListener(this);
        btnSaveFile.addActionListener(this);
        btnExportCSV.addActionListener(this);

        // 3. प्रोजेक्ट को PC स्क्रीन के बिल्कुल बीचों-बीच (Dead Center) लाने का कमांड
        setLocationRelativeTo(null);
    }

    // Helpers
    private void addLabel(JPanel panel, String text, int x, int y, Font font) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, 130, 25);
        label.setFont(font);
        label.setForeground(TEXT_LIGHT);
        panel.add(label);
    }

    private JTextField createTextField(int x, int y) {
        JTextField tf = new JTextField();
        tf.setBounds(x, y, 220, 25);
        tf.setBackground(CARD_TEAL);
        tf.setForeground(TEXT_LIGHT);
        tf.setCaretColor(Color.WHITE);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 120, 140), 1),
                BorderFactory.createEmptyBorder(0, 5, 0, 5)
        ));
        return tf;
    }

    private JButton createButton(String text, int x, int y, int w, int h, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, w, h);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private double calculateSlabBill(double units, String type) {
        double amount = 0;
        if (type.equalsIgnoreCase("Domestic")) {
            if (units <= 100) amount = units * 4.0;
            else if (units <= 300) amount = (100 * 4.0) + ((units - 100) * 7.0);
            else amount = (100 * 4.0) + (200 * 7.0) + ((units - 300) * 10.0);
        } else {
            amount = units * 9.0;
        }
        return amount + 50.0;
    }

    private Connection getDBConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(schema.URL + "electricitydb", schema.USER, schema.PASS);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnCalculate) saveBillData();
        else if (e.getSource() == btnClear) clearInputs();
        else if (e.getSource() == btnView) renderDataTable("");
        else if (e.getSource() == btnSearch) renderDataTable(txtSearch.getText().trim());
        else if (e.getSource() == btnDelete) deleteRecordFromDB();
        else if (e.getSource() == btnAnalytics) showAnalyticsSummary();
        else if (e.getSource() == btnExportCSV) exportToCSVFile();
        else if (e.getSource() == btnSaveFile) downloadReceiptTxt();
    }

    private void saveBillData() {
        if (txtId.getText().trim().isEmpty() || txtName.getText().trim().isEmpty() || txtUnits.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "कृपया सभी फील्ड्स भरें!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String id = txtId.getText().trim();
            String name = txtName.getText().trim();
            double units = Double.parseDouble(txtUnits.getText().trim());
            String type = (String) cmbType.getSelectedItem();
            double total = calculateSlabBill(units, type);

            Connection conn = getDBConnection();
            String query = "INSERT INTO bills (customer_id, customer_name, units, connection_type, total_amount) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, id);
            pst.setString(2, name);
            pst.setDouble(3, units);
            pst.setString(4, type);
            pst.setDouble(5, total);
            pst.executeUpdate();
            conn.close();

            txtReceipt.setText("⚡ ================================== ⚡\n");
            txtReceipt.append("          POWER GRID RECEIPT          \n");
            txtReceipt.append("⚡ ================================== ⚡\n\n");
            txtReceipt.append(" Customer ID     : " + id + "\n");
            txtReceipt.append(" Customer Name   : " + name + "\n");
            txtReceipt.append(" Units Consumed  : " + units + "\n");
            txtReceipt.append(" Connection Type : " + type + "\n");
            txtReceipt.append(" -----------------------------------\n");
            txtReceipt.append(" TOTAL AMOUNT    : ₹" + total + "\n");
            txtReceipt.append(" -----------------------------------\n");
            txtReceipt.append(" STATUS          : SAVED IN DATABASE\n");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }

    private void renderDataTable(String searchId) {
        String[] columns = {"DB ID", "Customer ID", "Name", "Units", "Type", "Total Amount"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        try (Connection conn = getDBConnection()) {
            String query = searchId.isEmpty() ? "SELECT * FROM bills" : "SELECT * FROM bills WHERE customer_id = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            if (!searchId.isEmpty()) pst.setString(1, searchId);

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Object[] row = {
                        rs.getInt("id"), rs.getString("customer_id"), rs.getString("customer_name"),
                        rs.getDouble("units"), rs.getString("connection_type"), rs.getDouble("total_amount")
                };
                model.addRow(row);
            }

            JTable table = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(table);
            JFrame frame = new JFrame(searchId.isEmpty() ? "All Bills History" : "Search Result: " + searchId);
            frame.add(scrollPane);
            frame.setSize(650, 300);
            frame.setLocationRelativeTo(this);
            frame.setVisible(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error fetching data: " + ex.getMessage());
        }
    }

    private void deleteRecordFromDB() {
        String deleteId = txtSearch.getText().trim();
        if (deleteId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Delete करने के लिए Customer ID दर्ज करें!");
            return;
        }

        try (Connection conn = getDBConnection()) {
            PreparedStatement pst = conn.prepareStatement("DELETE FROM bills WHERE customer_id = ?");
            pst.setString(1, deleteId);
            int count = pst.executeUpdate();
            if (count > 0) {
                JOptionPane.showMessageDialog(this, "ID: " + deleteId + " सफलतापूर्वक डिलीट हो गया!");
                txtSearch.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "कोई रिकॉर्ड नहीं मिला!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Delete Error: " + ex.getMessage());
        }
    }

    private void showAnalyticsSummary() {
        try (Connection conn = getDBConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT COUNT(*), SUM(total_amount), AVG(units) FROM bills");
            if (rs.next()) {
                int totalBills = rs.getInt(1);
                double totalRevenue = rs.getDouble(2);
                double avgUnits = rs.getDouble(3);

                txtReceipt.setText("📊 ================================== 📊\n");
                txtReceipt.append("     POWERGRID ANALYTICS SUMMARY     \n");
                txtReceipt.append("📊 ================================== 📊\n\n");
                txtReceipt.append(" Total Bills Generated  : " + totalBills + "\n");
                txtReceipt.append(" Total Revenue Collected: ₹" + String.format("%.2f", totalRevenue) + "\n");
                txtReceipt.append(" Average Unit Consumed  : " + String.format("%.2f", avgUnits) + " Units\n");
                txtReceipt.append(" -----------------------------------\n");
                txtReceipt.append(" SYSTEM HEALTH          : OPTIMAL\n");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Analytics Error: " + ex.getMessage());
        }
    }

    private void exportToCSVFile() {
        try (Connection conn = getDBConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM bills");
             FileWriter writer = new FileWriter("Bills_Report.csv")) {

            writer.write("DB_ID,Customer_ID,Customer_Name,Units,Connection_Type,Total_Amount\n");

            while (rs.next()) {
                writer.write(rs.getInt("id") + "," +
                        rs.getString("customer_id") + "," +
                        rs.getString("customer_name") + "," +
                        rs.getDouble("units") + "," +
                        rs.getString("connection_type") + "," +
                        rs.getDouble("total_amount") + "\n");
            }
            JOptionPane.showMessageDialog(this, "Excel Report 'Bills_Report.csv' सेव हो गई!");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Export Error: " + ex.getMessage());
        }
    }

    private void downloadReceiptTxt() {
        if (txtReceipt.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "सेव करने के लिए कोई रसीद नहीं है!");
            return;
        }
        try (FileWriter writer = new FileWriter("Receipt_" + System.currentTimeMillis() + ".txt")) {
            writer.write(txtReceipt.getText());
            JOptionPane.showMessageDialog(this, "रसीद डाउनलोड हो गई!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "File Error: " + ex.getMessage());
        }
    }

    private void clearInputs() {
        txtId.setText(""); txtName.setText(""); txtUnits.setText(""); txtSearch.setText(""); txtReceipt.setText("");
    }

    // Main Launcher & Login Window
    public static void main(String[] args) {
        JDialog loginDialog = new JDialog((Frame) null, "Secure Login System", true);
        loginDialog.setSize(360, 240);
        loginDialog.setLayout(null);
        loginDialog.setResizable(false);
        loginDialog.setLocationRelativeTo(null); // लॉगिन विंडो भी स्क्रीन के बीचों-बीच खुलेगी
        loginDialog.getContentPane().setBackground(new Color(17, 56, 71));

        JLabel lblHeader = new JLabel("POWERGRID SECURE LOGIN");
        lblHeader.setFont(new Font("Consolas", Font.BOLD, 14));
        lblHeader.setForeground(Color.WHITE);
        lblHeader.setBounds(70, 15, 230, 25);
        loginDialog.add(lblHeader);

        JLabel l1 = new JLabel("Username:");
        l1.setForeground(Color.WHITE);
        l1.setBounds(40, 60, 80, 25);
        loginDialog.add(l1);

        JTextField txtUser = new JTextField("admin");
        txtUser.setBounds(130, 60, 160, 25);
        loginDialog.add(txtUser);

        JLabel l2 = new JLabel("Password:");
        l2.setForeground(Color.WHITE);
        l2.setBounds(40, 100, 80, 25);
        loginDialog.add(l2);

        JPasswordField txtPass = new JPasswordField("1234");
        txtPass.setBounds(130, 100, 160, 25);
        loginDialog.add(txtPass);

        JLabel l3 = new JLabel("Role:");
        l3.setForeground(Color.WHITE);
        l3.setBounds(40, 140, 80, 25);
        loginDialog.add(l3);

        String[] roles = {"Admin", "Operator","customer"};
        JComboBox<String> cmbRole = new JComboBox<>(roles);
        cmbRole.setBounds(130, 140, 160, 25);
        loginDialog.add(cmbRole);

        JButton btnLogin = new JButton("Login");
        btnLogin.setBounds(130, 175, 100, 25);
        btnLogin.setBackground(new Color(197, 160, 89));
        btnLogin.setForeground(Color.BLACK);
        loginDialog.add(btnLogin);

        final boolean[] isAuthenticated = {false};

        btnLogin.addActionListener(e -> {
            String u = txtUser.getText().trim();
            String p = new String(txtPass.getPassword()).trim();

            if ((u.equalsIgnoreCase("admin") || u.equalsIgnoreCase("operator")) && p.equals("1234")) {
                loggedInUserRole = (String) cmbRole.getSelectedItem();
                isAuthenticated[0] = true;
                loginDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(loginDialog, "गलत पासवर्ड! (User: admin/operator, Pass: 1234)", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        loginDialog.setVisible(true);

        if (isAuthenticated[0]) {
            try {
                schema.create();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Schema Error: " + ex.getMessage());
            }
            SwingUtilities.invokeLater(() -> new NSP().setVisible(true));
        } else {
            System.exit(
                    0);
        }
    }
}