package com.digitalbanking.security.services;

import com.digitalbanking.security.entities.AppRole;
import com.digitalbanking.security.entities.AppUser;

public interface AccountService {
    AppUser addNewUser(String username, String password, String email);
    AppRole addNewRole(String roleName);
    void addRoleToUser(String username, String roleName);
    void removeRoleFromUser(String username, String roleName);
    AppUser loadUserByUsername(String username);
    void changePassword(String username, String oldPassword, String newPassword);
}
