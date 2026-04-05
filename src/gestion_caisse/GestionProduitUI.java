package gestion_caisse;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class GestionProduitUI {
    private JPanel panel;
    private JTextField nomField, prixField, stockField;
    private DefaultTableModel tableModel;
    private JTable table;
    private int selectedProductId = -1;
    private CardLayout cardLayout;
    private JPanel cardPanel;

    public GestionProduitUI(CardLayout cardLayout, JPanel cardPanel) {
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;

        panel = new JPanel();
        panel.setLayout(new BorderLayout(15, 15));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(4, 2, 15, 15));
        formPanel.setBackground(new Color(245, 247, 250));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)), "Gestion des Produits",
                        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION,
                        new Font("SansSerif", Font.BOLD, 18)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        formPanel.add(createStyledLabel("Nom :"));
        nomField = createStyledTextField();
        formPanel.add(nomField);

        formPanel.add(createStyledLabel("Prix Unitaire :"));
        prixField = createStyledTextField();
        formPanel.add(prixField);

        formPanel.add(createStyledLabel("Quantité Stock :"));
        stockField = createStyledTextField();
        formPanel.add(stockField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        buttonPanel.setBackground(new Color(245, 247, 250));

        JButton addButton = createStyledButton("Ajouter", new Color(46, 125, 50), new Color(67, 160, 71));
        JButton updateButton = createStyledButton("Modifier", new Color(255, 167, 38), new Color(255, 183, 77));
        JButton deleteButton = createStyledButton("Supprimer", new Color(211, 47, 47), new Color(239, 83, 80));

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        formPanel.add(new JLabel());
        formPanel.add(buttonPanel);

        panel.add(formPanel, BorderLayout.NORTH);

        // Center Panel for Table
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(245, 247, 250));

        // Table
        tableModel = new DefaultTableModel(new String[]{"ID", "Nom", "Prix Unitaire", "Stock"}, 0) {
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

        panel.add(centerPanel, BorderLayout.CENTER);

        // Actions
        addButton.addActionListener(e -> addProduct());
        updateButton.addActionListener(e -> updateProduct());
        deleteButton.addActionListener(e -> deleteProduct());

        table.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                selectedProductId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
                nomField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                prixField.setText(tableModel.getValueAt(selectedRow, 2).toString());
                stockField.setText(tableModel.getValueAt(selectedRow, 3).toString());
            }
        });

        loadProducts();
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

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        textField.setPreferredSize(new Dimension(0, 35));
        return textField;
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

    private void loadProducts() {
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM produits")) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getDouble("prix_unitaire"),
                        rs.getInt("quantite_stock")
                });
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(panel, "Erreur lors du chargement des produits : " + e.getMessage());
        }
    }

    private void addProduct() {
        String nom = nomField.getText().trim();
        String prix = prixField.getText().trim();
        String stock = stockField.getText().trim();

        if (nom.isEmpty() || prix.isEmpty() || stock.isEmpty()) {
            JOptionPane.showMessageDialog(panel, "Veuillez remplir tous les champs.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM produits WHERE nom = ?")) {
            pstmt.setString(1, nom);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(panel, "Un produit avec ce nom existe déjà.");
                return;
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(panel, "Erreur lors de la vérification du produit : " + e.getMessage());
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO produits (nom, prix_unitaire, quantite_stock) VALUES (?, ?, ?)")) {
            pstmt.setString(1, nom);
            pstmt.setDouble(2, Double.parseDouble(prix));
            pstmt.setInt(3, Integer.parseInt(stock));
            pstmt.executeUpdate();
            loadProducts();
            clearFields();
            JOptionPane.showMessageDialog(panel, "Produit ajouté avec succès.");
        } catch (SQLException | ClassNotFoundException | NumberFormatException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(panel, "Erreur lors de l'ajout du produit : " + e.getMessage());
        }
    }

    private void updateProduct() {
        if (selectedProductId == -1) {
            JOptionPane.showMessageDialog(panel, "Veuillez sélectionner un produit.");
            return;
        }

        String nom = nomField.getText().trim();
        String prix = prixField.getText().trim();
        String stock = stockField.getText().trim();

        if (nom.isEmpty() || prix.isEmpty() || stock.isEmpty()) {
            JOptionPane.showMessageDialog(panel, "Veuillez remplir tous les champs.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM produits WHERE nom = ? AND id != ?")) {
            pstmt.setString(1, nom);
            pstmt.setInt(2, selectedProductId);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(panel, "Un produit avec ce nom existe déjà.");
                return;
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(panel, "Erreur lors de la vérification du produit : " + e.getMessage());
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE produits SET nom = ?, prix_unitaire = ?, quantite_stock = ? WHERE id = ?")) {
            pstmt.setString(1, nom);
            pstmt.setDouble(2, Double.parseDouble(prix));
            pstmt.setInt(3, Integer.parseInt(stock));
            pstmt.setInt(4, selectedProductId);
            pstmt.executeUpdate();
            loadProducts();
            clearFields();
            selectedProductId = -1;
            JOptionPane.showMessageDialog(panel, "Produit modifié avec succès.");
        } catch (SQLException | ClassNotFoundException | NumberFormatException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(panel, "Erreur lors de la modification du produit : " + e.getMessage());
        }
    }

    private void deleteProduct() {
        if (selectedProductId == -1) {
            JOptionPane.showMessageDialog(panel, "Veuillez sélectionner un produit.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(panel, "Voulez-vous supprimer ce produit ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("DELETE FROM produits WHERE id = ?")) {
                pstmt.setInt(1, selectedProductId);
                pstmt.executeUpdate();
                loadProducts();
                clearFields();
                selectedProductId = -1;
                JOptionPane.showMessageDialog(panel, "Produit supprimé avec succès.");
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(panel, "Erreur lors de la suppression du produit : " + e.getMessage());
            }
        }
    }

    private void clearFields() {
        nomField.setText("");
        prixField.setText("");
        stockField.setText("");
    }
}