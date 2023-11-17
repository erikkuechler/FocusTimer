package htwberlin.focustimer.controller;

import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import htwberlin.focustimer.entity.UserAccount;
import htwberlin.focustimer.repository.UserAccountRepository;

import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/me")
public class MeController {

    private UserAccountRepository userRepository;

    public MeController (UserAccountRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String index() {
        return "Hello World";
    }

    @GetMapping("/email")
    public ResponseEntity<String> getUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = ((UserDetails) authentication.getPrincipal()).getUsername();
        
        return ResponseEntity.ok(userEmail);
    }

    @GetMapping("/coins")
    public ResponseEntity<Integer> getUserCoins() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = ((UserDetails) authentication.getPrincipal()).getUsername();
        
        Optional<UserAccount> optionalUserAccount = userRepository.findByEmail(userEmail);
        
        if (optionalUserAccount.isPresent()) {
            UserAccount userAccount = optionalUserAccount.get();
            int userCoins = userAccount.getCoins();
            return ResponseEntity.ok(userCoins);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/earn")
    public ResponseEntity<String> earnCoin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = ((UserDetails) authentication.getPrincipal()).getUsername();

        Optional<UserAccount> optionalUserAccount = userRepository.findByEmail(userEmail);

        if (optionalUserAccount.isPresent()) {
            UserAccount userAccount = optionalUserAccount.get();
            int currentCoins = userAccount.getCoins();
            userAccount.setCoins(currentCoins + 1); // Erhöhe die Coins um 1
            userRepository.save(userAccount); // Speichere die aktualisierten Coins in der Datenbank
            return ResponseEntity.ok("Coin earned successfully!");
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}
