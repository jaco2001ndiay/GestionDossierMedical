package com.medical.GestionDossierMedical.controller;

import com.medical.GestionDossierMedical.dto.LoginRequest;
import com.medical.GestionDossierMedical.dto.LoginResponse;
import com.medical.GestionDossierMedical.dto.LogoutRequest;
import com.medical.GestionDossierMedical.dto.RegisterRequest;
import com.medical.GestionDossierMedical.entity.User;
import com.medical.GestionDossierMedical.repository.UserRepository;
import com.medical.GestionDossierMedical.security.JwtUtil;
import com.medical.GestionDossierMedical.service.LogoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private LogoutService logoutService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(auth);

            User user = userRepository.findByUsername(req.getUsername()).orElseThrow();

            Set<String> roles = user.getRoles();
            String accessToken = jwtUtil.generateToken(
                    user.getUsername(),
                    user.getId(),
                    roles
            );
            String refreshToken = jwtUtil.generateRefreshToken(
                    user.getUsername(),
                    user.getId(),
                    roles
            );

            return ResponseEntity.ok(new LoginResponse(accessToken, refreshToken));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nom d'utilisateur ou mot de passe incorrect");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (userRepository.findByUsername(req.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Nom d'utilisateur déjà utilisé.");
        }
        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRoles(req.getRoles());
        userRepository.save(user);

        return ResponseEntity.ok("Utilisateur enregistré avec succès !");
    }
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String,String> request) {
        String refreshToken = request.get("refreshToken");
        if (refreshToken != null && jwtUtil.validateToken(refreshToken)) {
            String username = jwtUtil.getUsernameFromToken(refreshToken);
            Long userId = jwtUtil.getUserIdFromToken(refreshToken);
            Set<String> roles = jwtUtil.getRolesFromToken(refreshToken);

            String newAccessToken = jwtUtil.generateToken(username, userId, roles);
            return ResponseEntity.ok(new LoginResponse(newAccessToken, refreshToken));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token de rafraîchissement invalide");
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé");
        }
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LogoutRequest request) {
        logoutService.logout(request.getRefreshTokenId());
        return ResponseEntity.ok().build();
    }



}
