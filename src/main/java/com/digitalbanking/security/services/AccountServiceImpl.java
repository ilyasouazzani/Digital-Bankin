package com.digitalbanking.security.services;

import com.digitalbanking.security.entities.AppRole;
import com.digitalbanking.security.entities.AppUser;
import com.digitalbanking.security.repositories.AppRoleRepository;
import com.digitalbanking.security.repositories.AppUserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AppUserRepository appUserRepository;
    private final AppRoleRepository appRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AppUser addNewUser(String username, String password, String email) {
        if (appUserRepository.existsByUsername(username))
            throw new RuntimeException("Username '" + username + "' already exists");
        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setActive(true);
        log.info("Création utilisateur : {}", username);
        return appUserRepository.save(user);
    }

    @Override
    public AppRole addNewRole(String roleName) {
        AppRole role = new AppRole();
        role.setRoleName(roleName);
        return appRoleRepository.save(role);
    }

    @Override
    public void addRoleToUser(String username, String roleName) {
        AppUser user = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        AppRole role = appRoleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
        user.getRoles().add(role);
        log.info("Role {} ajouté à {}", roleName, username);
    }

    @Override
    public void removeRoleFromUser(String username, String roleName) {
        AppUser user = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        AppRole role = appRoleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
        user.getRoles().remove(role);
    }

    @Override
    public AppUser loadUserByUsername(String username) {
        return appUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        AppUser user = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        if (!passwordEncoder.matches(oldPassword, user.getPassword()))
            throw new RuntimeException("Ancien mot de passe incorrect");
        user.setPassword(passwordEncoder.encode(newPassword));
        appUserRepository.save(user);
        log.info("Mot de passe changé pour : {}", username);
    }
}
