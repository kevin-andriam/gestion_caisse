package model;

public class VenteProduit {
    private int idVente;
    private int idProduit;
    private int quantite;
    private double prixUnitaire;

    public VenteProduit(int idVente, int idProduit, int quantite, double prixUnitaire) {
        this.idVente = idVente;
        this.idProduit = idProduit;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
    }

    public int getIdVente() { 
        return idVente; 
    }

    public void setIdVente(int idVente) { 
        this.idVente = idVente; 
    }

    public int getIdProduit() { 
        return idProduit; 
    }

    public void setIdProduit(int idProduit) { 
        this.idProduit = idProduit; 
    }

    public int getQuantite() { 
        return quantite; 
    }

    public void setQuantite(int quantite) { 
        this.quantite = quantite; 
    }

    public double getPrixUnitaire() { 
        return prixUnitaire; 
    }

    public void setPrixUnitaire(double prixUnitaire) { 
        this.prixUnitaire = prixUnitaire; 
    }
   
}
