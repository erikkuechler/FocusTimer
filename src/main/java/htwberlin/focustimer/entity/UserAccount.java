package htwberlin.focustimer.entity;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;

@Entity
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    @JsonIgnore
    private String password;

    private String userName;

    private int coins;

    @JsonIgnore
    private LocalDateTime lastEarnTime;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
        name = "user_purchased_products",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> purchasedProducts;

    @OneToOne
    @JoinColumn(name = "background_id")
    private Product activeBackground;

    @OneToOne
    @JoinColumn(name = "foreground_id")
    private Product activeForeground;

    public UserAccount() { }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LocalDateTime getLastEarnTime() {
        return lastEarnTime;
    }

    public void setLastEarnTime(LocalDateTime lastEarnTime) {
        this.lastEarnTime = lastEarnTime;
    }

    public List<Product> getPurchasedProducts() {
        return purchasedProducts;
    }

    public void setPurchasedProducts(List<Product> purchasedProducts) {
        this.purchasedProducts = purchasedProducts;
    }

    public Product getActiveBackground() {
        return activeBackground;
    }

    public void setActiveBackground(Product activeBackground) {
        this.activeBackground = activeBackground;
    }

    public Product getActiveForeground() {
        return activeForeground;
    }

    public void setActiveForeground(Product activeForeground) {
        this.activeForeground = activeForeground;
    }
    
}
