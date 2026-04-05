package gestion_caisse;

import javax.swing.*;

import view.GestionProduitUI;
import view.RechercheUI;
import view.VenteUI;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class Main {
    public static void main(String[] args) {
        // Initialisation de la base de données
        try (Connection conn = DatabaseConnection.getConnection()) {
            Statement stmt = conn.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Driver JDBC introuvable : " + e.getMessage());
            return;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Erreur lors de l'initialisation de la base de données : " + e.getMessage());
            return;
        }

        // Interface principale
        JFrame frame = new JFrame("Système de Caisse");
        frame.setSize(1000, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        // Création du CardLayout pour le contenu
        CardLayout cardLayout = new CardLayout();
        JPanel contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(new Color(245, 247, 250));

        // Ajouter les panneaux des interfaces
        GestionProduitUI productManagementUI = new GestionProduitUI(cardLayout, contentPanel);
        VenteUI salesUI = new VenteUI(cardLayout, contentPanel);
        RechercheUI salesSearchUI = new RechercheUI(cardLayout, contentPanel);
        contentPanel.add(productManagementUI.getPanel(), "ProductManagement");
        contentPanel.add(salesUI.getPanel(), "Sales");
        contentPanel.add(salesSearchUI.getPanel(), "SalesSearch");

        // Création du menu latéral
        JPanel sidebar = createSidebar(cardLayout, contentPanel, salesUI);

        // Ajouter les composants à la fenêtre
        frame.add(sidebar, BorderLayout.WEST);
        frame.add(contentPanel, BorderLayout.CENTER);

        // Afficher la fenêtre
        frame.setVisible(true);
    }

    private static JPanel createSidebar(CardLayout cardLayout, JPanel contentPanel, VenteUI salesUI) {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(44, 62, 80));
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        // Titre
        JLabel title = new JLabel("Système de Caisse");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(title);
        sidebar.add(Box.createRigidArea(new Dimension(0, 40)));

        // Boutons du menu
        String[] options = { "Gestion des Produits", "Réalisation de Vente", "Recherche des Ventes" };
        String[] panels = { "ProductManagement", "Sales", "SalesSearch" };
        JButton[] buttons = new JButton[options.length];

        for (int i = 0; i < options.length; i++) {
            JButton button = createSidebarButton(options[i]);
            final String panelName = panels[i];
            buttons[i] = button;

            button.addActionListener(e -> {
                cardLayout.show(contentPanel, panelName);
                if (panelName.equals("Sales")) {
                    salesUI.refreshProducts(); // Refresh products when switching to SalesUI
                }
                for (JButton btn : buttons) {
                    btn.setBackground(new Color(44, 62, 80));
                    btn.setOpaque(true);
                }
                button.setBackground(new Color(30, 144, 255));
                button.setOpaque(true);
            });

            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (button.getBackground().getRGB() != new Color(30, 144, 255).getRGB()) {
                        button.setBackground(new Color(52, 73, 94));
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (button.getBackground().getRGB() != new Color(30, 144, 255).getRGB()) {
                        button.setBackground(new Color(44, 62, 80));
                    }
                }
            });

            sidebar.add(button);
            sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        }

        return sidebar;
    }

    private static JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(220, 50));
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBackground(new Color(44, 62, 80));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(44, 62, 80), 2),
                BorderFactory.createEmptyBorder(12, 20, 12, 20)));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        return button;
    }
}