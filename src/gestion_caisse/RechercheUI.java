package gestion_caisse;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.text.SimpleDateFormat;

public class RechercheUI {
    private JPanel panel;
    private JDateChooser startDateChooser, endDateChooser;
    private DefaultTableModel tableModel;
    private JTable table;
    private JLabel totalLabel;
    private CardLayout cardLayout;
    private JPanel cardPanel;

    public RechercheUI(CardLayout cardLayout, JPanel cardPanel) {
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;

        panel = new JPanel();
        panel.setLayout(new BorderLayout(15, 15));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(3, 2, 15, 15));
        formPanel.setBackground(new Color(245, 247, 250));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)), "Recherche des Ventes",
                        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION,
                        new Font("SansSerif", Font.BOLD, 18)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        formPanel.add(createStyledLabel("Date de début :"));
        startDateChooser = new JDateChooser();
        startDateChooser.setDateFormatString("yyyy-MM-dd");
        startDateChooser.setFont(new Font("SansSerif", Font.PLAIN, 16));
        formPanel.add(startDateChooser);

        formPanel.add(createStyledLabel("Date de fin :"));
        endDateChooser = new JDateChooser();
        endDateChooser.setDateFormatString("yyyy-MM-dd");
        endDateChooser.setFont(new Font("SansSerif", Font.PLAIN, 16));
        formPanel.add(endDateChooser);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        buttonPanel.setBackground(new Color(245, 247, 250));

        JButton searchButton = createStyledButton("Rechercher", new Color(46, 125, 50), new Color(67, 160, 71));
        buttonPanel.add(searchButton);

        formPanel.add(new JLabel());
        formPanel.add(buttonPanel);

        panel.add(formPanel, BorderLayout.NORTH);

        // Center Panel for Table and Total Label
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(245, 247, 250));

        // Table
        tableModel = new DefaultTableModel(new String[]{"ID Vente", "Date", "Total"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setFont(new Font("SansSerif", Font.PLAIN, 16));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("SansSerif", Font.PLAIN, 16)); // Increased header font
        table.getTableHeader().setPreferredSize(new Dimension(0, 35)); // Increased header height
        table.setSelectionBackground(new Color(200, 240, 255));
        table.setGridColor(new Color(200, 200, 200));
        table.setShowGrid(true);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        tableModel.addTableModelListener(e -> adjustTableSize());
        centerPanel.add(scrollPane);

        // Total Label
        totalLabel = new JLabel("Total des ventes : 0.0");
        totalLabel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        totalLabel.setForeground(new Color(45, 52, 54));
        totalLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(totalLabel);

        panel.add(centerPanel, BorderLayout.CENTER);

        searchButton.addActionListener(e -> searchSales());
    }

    public JPanel getPanel() {
        return panel;
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 16));
        label.setForeground(new Color(45, 52, 54));
        return label;
    }

    private JButton createStyledButton(String text, Color backgroundColor, Color hoverColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(backgroundColor);
            }
        });
        return button;
    }

    private void adjustTableSize() {
        int rowCount = tableModel.getRowCount();
        int rowHeight = table.getRowHeight();
        int headerHeight = table.getTableHeader().getPreferredSize().height; // Uses updated header height
        int maxRows = 10;
        int tableHeight = Math.min(rowCount + 1, maxRows) * rowHeight + headerHeight;
        table.getParent().setPreferredSize(new Dimension(table.getPreferredSize().width, tableHeight));
        table.getParent().revalidate();
        table.getParent().repaint();
    }

    private void searchSales() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String startDate = startDateChooser.getDate() != null ? sdf.format(startDateChooser.getDate()) : "";
        String endDate = endDateChooser.getDate() != null ? sdf.format(endDateChooser.getDate()) : "";

        if (startDate.isEmpty() || endDate.isEmpty()) {
            JOptionPane.showMessageDialog(panel, "Veuillez sélectionner des dates valides.");
            return;
        }

        tableModel.setRowCount(0);
        double total = 0;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT * FROM ventes WHERE date_vente BETWEEN ? AND ?")) {
            pstmt.setString(1, startDate + "T00:00:00");
            pstmt.setString(2, endDate + "T23:59:59");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("id_vente"),
                        rs.getString("date_vente"),
                        rs.getDouble("total")
                });
                total += rs.getDouble("total");
            }
            totalLabel.setText("Total des ventes : " + total);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(panel, "Erreur lors de la recherche des ventes : " + e.getMessage());
        }
    }
}