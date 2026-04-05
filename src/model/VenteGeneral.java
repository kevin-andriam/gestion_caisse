package model;

import java.time.LocalDateTime;
import java.util.List;

public class VenteGeneral {
    private int idVente;
    private LocalDateTime dateVente;
    private double total;
    private List<VenteProduit> details;

    public VenteGeneral(int idVente, LocalDateTime dateVente, double total, List<VenteProduit> details) {
        this.idVente = idVente;
        this.dateVente = dateVente;
        this.total = total;
        this.details = details;
    }

    // Getters and Setters
    public int getIdVente() {
    	return idVente; 
    }
    
    
    public void setIdVente(int idVente) { 
    	this.idVente = idVente; 
    }
    
    
    public LocalDateTime getDateVente() { 
    	return dateVente; 
    }
    
    
    public void setDateVente(LocalDateTime dateVente) { 
    	this.dateVente = dateVente; 
    }
    
    
    public double getTotal() { 
    	return total; 
    }
    
    
    public void setTotal(double total) { 
    	this.total = total; 
    }
    
    
    public List<VenteProduit> getDetails() { 
    	return details; 
    }
    
    
    public void setDetails(List<VenteProduit> details) { 
    	this.details = details; 
    }
}