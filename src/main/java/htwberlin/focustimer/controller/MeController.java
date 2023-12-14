package htwberlin.focustimer.controller;

import java.util.Optional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
    public ResponseEntity<UserAccount> index() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = ((UserDetails) authentication.getPrincipal()).getUsername();
        
        Optional<UserAccount> optionalUserAccount = userRepository.findByEmail(userEmail);
        
        if (optionalUserAccount.isPresent()) {
            UserAccount userAccount = optionalUserAccount.get();
            return ResponseEntity.ok(userAccount);
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
            LocalDateTime lastEarnTime = userAccount.getLastEarnTime(); // Zeitpunkt des letzten Coin-Erwerbs
            LocalDateTime currentTime = LocalDateTime.now();

            // Überprüfe, ob weniger als eine Minute seit dem mal vergangen ist
            if (lastEarnTime != null && ChronoUnit.MINUTES.between(lastEarnTime, currentTime) < 1) {
                return ResponseEntity.badRequest().body("You can earn only one coin per minute.");
            } else {
                userAccount.setCoins(userAccount.getCoins() + 1); // Erhöhe die Coins um 1
                userAccount.setLastEarnTime(currentTime); // Aktualisiere den Zeitpunkt des letzten Coin-Erwerbs
                userRepository.save(userAccount); // Speichere die aktualisierten Daten in der Datenbank
                return ResponseEntity.ok("Coin earned successfully!");
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/gettestcoins")
    public ResponseEntity<String> gettestcoins() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = ((UserDetails) authentication.getPrincipal()).getUsername();

        Optional<UserAccount> optionalUserAccount = userRepository.findByEmail(userEmail);

        if (optionalUserAccount.isPresent()) {
            UserAccount userAccount = optionalUserAccount.get();
            int currentCoins = userAccount.getCoins();
            userAccount.setCoins(currentCoins + 100); // Erhöhe die Coins um 100
            userRepository.save(userAccount); // Speichere die aktualisierten Coins in der Datenbank
            return ResponseEntity.ok("Coins earned successfully!");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
