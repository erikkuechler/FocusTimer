package htwberlin.focustimer.dto;

import htwberlin.focustimer.entity.Product;

/**
 * Data Transfer Object
 */
public class UserProductDTO {

    private Product product;

    private boolean purchased;
    private boolean active;
    
    public UserProductDTO() {}

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public boolean isPurchased() {
        return purchased;
    }

    public void setPurchased(boolean purchased) {
        this.purchased = purchased;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    
}
