package gestion_caisse;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestConnection {
    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/caisse_db?useSSL=false&serverTimezone=UTC",
                "root",
                "root"
            );
            System.out.println("Connexion réussie !");
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }
}