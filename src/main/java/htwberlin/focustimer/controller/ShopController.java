package htwberlin.focustimer.controller;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import htwberlin.focustimer.dto.UserProductDTO;
import htwberlin.focustimer.entity.Product;
import htwberlin.focustimer.entity.UserAccount;
import htwberlin.focustimer.repository.UserAccountRepository;
import htwberlin.focustimer.service.ProductService;
import org.springframework.security.core.Authentication;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/shop")
public class ShopController {

    @Autowired
    private ProductService service;

    @Autowired
    private UserAccountRepository userRepository;

    Logger logger = LoggerFactory.getLogger(ShopController.class);

    // Produkte mit Info über gekauft und aktiv
    @GetMapping("/products")
    public ResponseEntity<List<UserProductDTO>> getUserProducts() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = ((UserDetails) authentication.getPrincipal()).getUsername();

        Optional<UserAccount> optionalUserAccount = userRepository.findByEmail(userEmail);

        if (optionalUserAccount.isPresent()) {
            UserAccount userAccount = optionalUserAccount.get();
            List<Product> allProducts = service.getAll();

            List<UserProductDTO> userProductsDTOList = new ArrayList<>();

            for (Product product : allProducts) {
                boolean isPurchased = userAccount.getPurchasedProducts().contains(product);
                
                boolean isActiveBackground = product.equals(userAccount.getActiveBackground());
                boolean isActiveForeground = product.equals(userAccount.getActiveForeground());
                boolean isActive = isActiveBackground || isActiveForeground;

                // Füge spezifische Informationen zum Produkt hinzu (gekauft, aktiv).
                UserProductDTO userProductDTO = new UserProductDTO();
                userProductDTO.setProduct(product);
                userProductDTO.setPurchased(isPurchased);
                userProductDTO.setActive(isActive);

                userProductsDTOList.add(userProductDTO);
            }

            return ResponseEntity.ok(userProductsDTOList);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Kaufen eines Produkts durch einen angemeldeten Benutzer
    @PostMapping("/buy/{id}")
    public ResponseEntity<String> buyProduct(@PathVariable String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = ((UserDetails) authentication.getPrincipal()).getUsername();

        Optional<UserAccount> optionalUserAccount = userRepository.findByEmail(userEmail);

        Long productId = Long.parseLong(id);
        Product product = service.get(productId);

        if (optionalUserAccount.isPresent()) {
            UserAccount userAccount = optionalUserAccount.get();

            // Füge das Produkt zur Liste der gekauften Produkte hinzu, wenn der Benutzer genug Münzen hat
            int userCoins = userAccount.getCoins();
            int productPrice = product.getPrice();
            if (userCoins >= productPrice) {
                // Überprüfe, ob der Benutzer das Produkt bereits gekauft hat
                List<Product> purchasedProducts = userAccount.getPurchasedProducts();
                if (!purchasedProducts.contains(product)) {
                    // Füge das Produkt zur Liste der gekauften Produkte hinzu
                    userAccount.setCoins(userCoins - productPrice);
                    purchasedProducts.add(product);
                    userAccount.setPurchasedProducts(purchasedProducts);
                    userRepository.save(userAccount);
                    return ResponseEntity.ok("Product purchased successfully!");
                } else {
                    return ResponseEntity.badRequest().body("Product already purchased.");
                }
            } else {
                return ResponseEntity.badRequest().body("Insufficient coins to buy this product.");
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Aktivieren eines Hintergrund- oder Vordergrundprodukts für einen Benutzer
    @PostMapping("/activate/{id}")
    public ResponseEntity<String> activateProduct(@PathVariable String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = ((UserDetails) authentication.getPrincipal()).getUsername();

        Optional<UserAccount> optionalUserAccount = userRepository.findByEmail(userEmail);

        Long productId = Long.parseLong(id);
        Product product = service.get(productId);

        if (optionalUserAccount.isPresent()) {
            UserAccount userAccount = optionalUserAccount.get();

            // Überprüfe, ob das Produkt bereits gekauft wurde, bevor es aktiviert wird
            if (userAccount.getPurchasedProducts().contains(product)) {
                if (product.getType().equals("Background")) {
                    userAccount.setActiveBackground(product);
                } else if (product.getType().equals("Foreground")) {
                    userAccount.setActiveForeground(product);
                }
                userRepository.save(userAccount);
                return ResponseEntity.ok("Product activated successfully!");
            } else {
                return ResponseEntity.badRequest().body("Product needs to be purchased before activation.");
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/products")
    public Product createProduct(@RequestBody Product product) {
        return service.save(product);
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable String id) {
        Long productId = Long.parseLong(id);
        
        // Überprüfen, ob das Produkt existiert, bevor du versuchst, es zu löschen
        if (service.get(productId) != null) {
            service.delete(productId);
            return ResponseEntity.ok("Product deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}