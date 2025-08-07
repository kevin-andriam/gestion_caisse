package gestion_caisse;

public class Produit {
    private int id;
    private String nom;
    private double prixUnitaire;
    private int quantiteStock;

    public Produit(int id, String nom, double prixUnitaire, int quantiteStock) {
        this.id = id;
        this.nom = nom;
        this.prixUnitaire = prixUnitaire;
        this.quantiteStock = quantiteStock;
    }

    // Getters and Setters
    public int getId() { 
    	return id; 
    }
    
    
    public void setId(int id) {
    	this.id = id; 
    }
    
    
    public String getNom() {
    	return nom; 
    }
    
    
    public void setNom(String nom) {
    	this.nom = nom; 
    }
    
    
    public double getPrixUnitaire() { 
    	return prixUnitaire; 
    }
    
    
    public void setPrixUnitaire(double prixUnitaire) {
    	this.prixUnitaire = prixUnitaire; 
    }
    
    
    public int getQuantiteStock() { 
    	return quantiteStock; 
    }
    
    public void setQuantiteStock(int quantiteStock) { 
    	this.quantiteStock = quantiteStock; 
    }
    
    
    @Override
    public String toString() {
        return nom + " (" + quantiteStock + ")";
    }
}