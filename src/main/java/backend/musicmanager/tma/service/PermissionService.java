package backend.musicmanager.tma.service;

import backend.musicmanager.tma.exception.ObjectFailed;
import backend.musicmanager.tma.model.Permission;
import backend.musicmanager.tma.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionService {
    @Autowired
    private PermissionRepository permissionRepository;

    public List<Permission> findAll() {
        return permissionRepository.findAll();
    }

    public Object findById(int id) {
        Permission permission = permissionRepository.findById(id).orElse(null);
        if(permission == null) return ObjectFailed.builder().message("Not found permission with id " + id).status(404).build();
        return permission;
    }

    public Object create(Permission newPermission) {
        return save(new Permission(), newPermission);
    }

    public Object update(int id, Permission newPermission) {
        Permission permission = permissionRepository.findById(id).orElse(null);
        if(permission == null)
            return ObjectFailed.builder().message("Not found permission with id " + id).status(404).build();
        return save(permission, newPermission);
    }

    public Object save(Permission permission, Permission newPermission) {
        List<Permission> list = findAll();
        for (Permission p : list) {
            if (p.getName().equals(newPermission.getName()) && p.getId() != permission.getId())
                return ObjectFailed.builder().message("This permission is already exist").status(409).build();
        }
        permission.setName(newPermission.getName());
        permission.setRoles(newPermission.getRoles());
        return permissionRepository.save(permission);
    }

    public Object delete(int id) {
        Permission permission = permissionRepository.findById(id).orElse(null);
        if (permission == null)
            return ObjectFailed.builder().message("Failed to delete permission, not found permission with id " + id).status(404).build();
        permissionRepository.deleteById(id);
        return permission;
    }
    public List<String> getPermissionForCurrentUser(int id) {
        return permissionRepository.getPermissionForCurrentUser(id);
    }
}
