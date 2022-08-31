package comidev.comistore.components.role;

import java.util.Set;
import java.util.HashSet;

import org.springframework.stereotype.Service;

import comidev.comistore.components.role.util.RoleName;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RoleService {
    private RoleRepo roleRepo;

    public Set<Role> initRole(RoleName roleName) {
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepo.findByName(roleName));
        return roles;
    }
}
