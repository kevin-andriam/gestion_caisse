package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import gestion_caisse.DatabaseConnection;
import model.Produit;
import model.VenteProduit;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VenteUI {
    private JPanel panel;
    private JComboBox<Produit> productComboBox;
    private List<Produit> allProducts;
    private JTextField quantityField;
    private DefaultTableModel saleTableModel;
    private JTable saleTable;
    private List<VenteProduit> saleDetails = new ArrayList<>();
    private JLabel totalLabel;
    private CardLayout cardLayout;
    private JPanel cardPanel;

    public VenteUI(CardLayout cardLayout, JPanel cardPanel) {
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
        this.allProducts = new ArrayList<>();

        panel = new JPanel();
        panel.setLayout(new BorderLayout(15, 15));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(3, 2, 15, 15));
        formPanel.setBackground(new Color(245, 247, 250));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)), "Réalisation d'une Vente",
                        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION,
                        new Font("SansSerif", Font.BOLD, 18)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        formPanel.add(createStyledLabel("Produit :"));
        productComboBox = new JComboBox<>();
        productComboBox.setEditable(true);
        productComboBox.setFont(new Font("SansSerif", Font.PLAIN, 16));
        JTextField editor = (JTextField) productComboBox.getEditor().getEditorComponent();
        editor.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        formPanel.add(productComboBox);

        formPanel.add(createStyledLabel("Quantité :"));
        quantityField = createStyledTextField();
        formPanel.add(quantityField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        buttonPanel.setBackground(new Color(245, 247, 250));

        JButton addToSaleButton = createStyledButton("Ajouter au panier", new Color(255, 167, 38), new Color(255, 183, 77));
        JButton validateSaleButton = createStyledButton("Valider la vente", new Color(46, 125, 50), new Color(67, 160, 71));
        JButton removeItemButton = createStyledButton("Supprimer l'article", new Color(211, 47, 47), new Color(239, 83, 80));
        JButton clearCartButton = createStyledButton("Vider le panier", new Color(211, 47, 47), new Color(239, 83, 80));
        buttonPanel.add(addToSaleButton);
        buttonPanel.add(validateSaleButton);
        buttonPanel.add(removeItemButton);
        buttonPanel.add(clearCartButton);

        formPanel.add(new JLabel());
        formPanel.add(buttonPanel);

        panel.add(formPanel, BorderLayout.NORTH);

        // Center Panel for Table and Total Label
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(245, 247, 250));

        // Sale Table
        saleTableModel = new DefaultTableModel(new String[]{"Produit", "Quantité", "Prix Unitaire", "Total"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        saleTable = new JTable(saleTableModel);
        saleTable.setFont(new Font("SansSerif", Font.PLAIN, 16));
        saleTable.setRowHeight(30);
        saleTable.getTableHeader().setFont(new Font("SansSerif", Font.PLAIN, 16));
        saleTable.getTableHeader().setPreferredSize(new Dimension(0, 35));
        saleTable.setSelectionBackground(new Color(200, 240, 255));
        saleTable.setGridColor(new Color(200, 200, 200));
        saleTable.setShowGrid(true);
        JScrollPane scrollPane = new JScrollPane(saleTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        saleTableModel.addTableModelListener(e -> adjustTableSize());
        centerPanel.add(scrollPane);

        // Total Label
        totalLabel = new JLabel("Total : 0.0");
        totalLabel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        totalLabel.setForeground(new Color(45, 52, 54));
        totalLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(totalLabel);

        panel.add(centerPanel, BorderLayout.CENTER);

        // Setup product search functionality
        setupProductSearch();

        // Actions
        addToSaleButton.addActionListener(e -> addToSale());
        validateSaleButton.addActionListener(e -> validateSale());
        removeItemButton.addActionListener(e -> removeSelectedItem());
        clearCartButton.addActionListener(e -> clearCart());

        // Initial load of products
        refreshProducts();
    }

    public JPanel getPanel() {
        return panel;
    }

    public void refreshProducts() {
        loadProducts();
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
        int rowCount = saleTableModel.getRowCount();
        int rowHeight = saleTable.getRowHeight();
        int headerHeight = saleTable.getTableHeader().getPreferredSize().height;
        int maxRows = 10;
        int tableHeight = Math.min(rowCount + 1, maxRows) * rowHeight + headerHeight;
        saleTable.getParent().setPreferredSize(new Dimension(saleTable.getPreferredSize().width, tableHeight));
        saleTable.getParent().revalidate();
        saleTable.getParent().repaint();
    }

    private void setupProductSearch() {
        JTextField editor = (JTextField) productComboBox.getEditor().getEditorComponent();
        editor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                // Ignore navigation keys to prevent filtering on arrow keys or Enter
                if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN || 
                    e.getKeyCode() == KeyEvent.VK_ENTER) {
                    return;
                }
                String searchText = editor.getText().trim().toLowerCase();
                SwingUtilities.invokeLater(() -> {
                    filterProducts(searchText);
                    if (!searchText.isEmpty() && productComboBox.getItemCount() > 0) {
                        productComboBox.showPopup();
                    } else {
                        productComboBox.hidePopup();
                    }
                });
            }
        });

        // Handle Enter key to select the first item if available
        editor.addActionListener(e -> {
            if (productComboBox.getItemCount() > 0) {
                productComboBox.setSelectedIndex(0);
                productComboBox.hidePopup();
            }
        });

        // Hide popup when focus is lost
        editor.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                productComboBox.hidePopup();
            }
        });

        // Handle mouse selection
        productComboBox.addActionListener(e -> {
            if (productComboBox.getSelectedItem() != null) {
                productComboBox.hidePopup();
            }
        });
    }

    private void loadProducts() {
        allProducts.clear();
        DefaultComboBoxModel<Produit> model = new DefaultComboBoxModel<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM produits")) {
            while (rs.next()) {
                Produit product = new Produit(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getDouble("prix_unitaire"),
                        rs.getInt("quantite_stock")
                );
                allProducts.add(product);
                model.addElement(product);
            }
            productComboBox.setModel(model);
            productComboBox.revalidate();
            productComboBox.repaint();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(panel, "Erreur lors du chargement des produits : " + e.getMessage());
        }
    }

    private void filterProducts(String searchText) {
        DefaultComboBoxModel<Produit> model = new DefaultComboBoxModel<>();
        List<Produit> filteredProducts = allProducts.stream()
                .filter(p -> p.getNom().toLowerCase().contains(searchText))
                .collect(Collectors.toList());
        for (Produit product : filteredProducts) {
            model.addElement(product);
        }
        productComboBox.setModel(model);
        productComboBox.revalidate();
        productComboBox.repaint();
    }

    private void addToSale() {
        Produit selectedProduct = (Produit) productComboBox.getSelectedItem();
        String quantityStr = quantityField.getText().trim();

        if (selectedProduct == null || quantityStr.isEmpty()) {
            JOptionPane.showMessageDialog(panel, "Veuillez sélectionner un produit et entrer une quantité.");
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0 || quantity > selectedProduct.getQuantiteStock()) {
                JOptionPane.showMessageDialog(panel, "Quantité invalide ou stock insuffisant.");
                return;
            }

            VenteProduit detail = new VenteProduit(0, selectedProduct.getId(), quantity, selectedProduct.getPrixUnitaire());
            saleDetails.add(detail);
            saleTableModel.addRow(new Object[]{
                    selectedProduct.getNom(),
                    quantity,
                    selectedProduct.getPrixUnitaire(),
                    quantity * selectedProduct.getPrixUnitaire()
            });

            updateTotal();
            quantityField.setText("");
            productComboBox.setSelectedIndex(-1); // Clear selection
            JTextField editor = (JTextField) productComboBox.getEditor().getEditorComponent();
            editor.setText(""); // Clear text field
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(panel, "Veuillez entrer une quantité valide.");
        }
    }

    private void validateSale() {
        if (saleDetails.isEmpty()) {
            JOptionPane.showMessageDialog(panel, "Aucun produit dans le panier.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                PreparedStatement saleStmt = conn.prepareStatement(
                        "INSERT INTO ventes (date_vente, total) VALUES (?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                LocalDateTime now = LocalDateTime.now();
                double total = calculateTotal();
                saleStmt.setString(1, now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                saleStmt.setDouble(2, total);
                saleStmt.executeUpdate();

                ResultSet rs = saleStmt.getGeneratedKeys();
                rs.next();
                int saleId = rs.getInt(1);

                PreparedStatement detailStmt = conn.prepareStatement(
                        "INSERT INTO details_vente (id_vente, id_produit, quantite, prix_unitaire) VALUES (?, ?, ?, ?)");
                PreparedStatement stockStmt = conn.prepareStatement(
                        "UPDATE produits SET quantite_stock = quantite_stock - ? WHERE id = ?");

                for (VenteProduit detail : saleDetails) {
                    detailStmt.setInt(1, saleId);
                    detailStmt.setInt(2, detail.getIdProduit());
                    detailStmt.setInt(3, detail.getQuantite());
                    detailStmt.setDouble(4, detail.getPrixUnitaire());
                    detailStmt.executeUpdate();

                    stockStmt.setInt(1, detail.getQuantite());
                    stockStmt.setInt(2, detail.getIdProduit());
                    stockStmt.executeUpdate();
                }

                conn.commit();
                saleDetails.clear();
                saleTableModel.setRowCount(0);
                updateTotal();
                refreshProducts();

                JOptionPane.showMessageDialog(panel, "Vente validée avec succès.");
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                JOptionPane.showMessageDialog(panel, "Erreur lors de la validation de la vente : " + e.getMessage());
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(panel, "Erreur de connexion à la base de données : " + e.getMessage());
        }
    }

    private void removeSelectedItem() {
        int selectedRow = saleTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(panel, "Veuillez sélectionner un article à supprimer.");
            return;
        }

        saleDetails.remove(selectedRow);
        saleTableModel.removeRow(selectedRow);
        updateTotal();
    }

    private void clearCart() {
        int confirm = JOptionPane.showConfirmDialog(panel,
                "Voulez-vous vraiment vider le panier ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            saleDetails.clear();
            saleTableModel.setRowCount(0);
            updateTotal();
            JOptionPane.showMessageDialog(panel, "Panier vidé avec succès.");
        }
    }

    private double calculateTotal() {
        double total = 0;
        for (VenteProduit detail : saleDetails) {
            total += detail.getQuantite() * detail.getPrixUnitaire();
        }
        return total;
    }

    private void updateTotal() {
        totalLabel.setText("Total: " + calculateTotal());
    }
}