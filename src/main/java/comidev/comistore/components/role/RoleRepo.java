package comidev.comistore.components.role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import comidev.comistore.components.role.util.RoleName;

@Repository
public interface RoleRepo extends JpaRepository<Role, Long> {
    Role findByName(RoleName name);
}
