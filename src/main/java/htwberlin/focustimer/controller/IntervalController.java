package htwberlin.focustimer.controller;

import java.util.List;
import java.util.Optional;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import htwberlin.focustimer.entity.Interval;
import htwberlin.focustimer.entity.UserAccount;
import htwberlin.focustimer.repository.UserAccountRepository;
import htwberlin.focustimer.service.IntervalService;

@RestController
@RequestMapping("/intervals")
public class IntervalController {

    @Autowired
    IntervalService service;

    @Autowired
    private UserAccountRepository userRepository;

    @PostMapping("/add")
    public ResponseEntity<String> addInterval(@RequestBody Interval interval) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = ((UserDetails) authentication.getPrincipal()).getUsername();

        Optional<UserAccount> optionalUserAccount = userRepository.findByEmail(userEmail);

        if (optionalUserAccount.isPresent()) {
            UserAccount userAccount = optionalUserAccount.get();

            interval.setUserAccount(userAccount);
            service.save(interval);

            return ResponseEntity.ok("Interval created for user: " + userEmail);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Interval>> getAllIntervals() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = ((UserDetails) authentication.getPrincipal()).getUsername();

        Optional<UserAccount> optionalUserAccount = userRepository.findByEmail(userEmail);

        if (optionalUserAccount.isPresent()) {
            UserAccount userAccount = optionalUserAccount.get();

            List<Interval> intervals = userAccount.getIntervals();
            return ResponseEntity.ok(intervals);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
    }
    
}
