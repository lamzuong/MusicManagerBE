package backend.musicmanager.tma.repository;

import backend.musicmanager.tma.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Integer> {
    @Query(value = "SELECT DISTINCT p.name FROM user u JOIN role r ON r.id = u.role_id JOIN permission_role pr ON r.id = pr.role_id JOIN permission p ON p.id = pr.permission_id \n" +
            "WHERE u.id = :id", nativeQuery = true)
    List<String> getPermissionForCurrentUser(@Param("id") int id);
}
