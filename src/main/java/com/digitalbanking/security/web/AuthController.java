package com.digitalbanking.security.web;

import com.digitalbanking.security.entities.AppRole;
import com.digitalbanking.security.entities.AppUser;
import com.digitalbanking.security.services.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
@Tag(name = "Authentication", description = "Inscription, profil et gestion du mot de passe")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AccountService accountService;

    @PostMapping("/register")
    @Operation(summary = "Inscription d'un nouvel utilisateur")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        try {
            AppUser user = accountService.addNewUser(
                    body.get("username"), body.get("password"), body.get("email"));
            accountService.addRoleToUser(user.getUsername(), "ROLE_USER");
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Utilisateur créé avec succès",
                                 "username", user.getUsername()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/profile")
    @Operation(summary = "Récupère le profil de l'utilisateur connecté")
    public ResponseEntity<?> getProfile(Principal principal) {
        AppUser user = accountService.loadUserByUsername(principal.getName());
        return ResponseEntity.ok(Map.of(
                "username", user.getUsername(),
                "email",    user.getEmail() != null ? user.getEmail() : "",
                "roles",    user.getRoles().stream().map(AppRole::getRoleName).toList(),
                "active",   user.isActive()));
    }

    @PutMapping("/change-password")
    @Operation(summary = "Change le mot de passe de l'utilisateur connecté")
    public ResponseEntity<?> changePassword(Principal principal,
                                            @RequestBody Map<String, String> body) {
        try {
            accountService.changePassword(
                    principal.getName(), body.get("oldPassword"), body.get("newPassword"));
            return ResponseEntity.ok(Map.of("message", "Mot de passe modifié avec succès"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/admin/add-role")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> addRole(@RequestBody Map<String, String> body) {
        AppRole role = accountService.addNewRole(body.get("roleName"));
        return ResponseEntity.status(HttpStatus.CREATED).body(role);
    }

    @PostMapping("/admin/assign-role")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> assignRole(@RequestBody Map<String, String> body) {
        accountService.addRoleToUser(body.get("username"), body.get("roleName"));
        return ResponseEntity.ok(Map.of("message", "Rôle assigné avec succès"));
    }
}
