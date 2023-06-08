package backend.musicmanager.tma.service;

import backend.musicmanager.tma.dto.RoleDTO;
import backend.musicmanager.tma.exception.ObjectFailed;
import backend.musicmanager.tma.model.Permission;
import backend.musicmanager.tma.model.Role;
import backend.musicmanager.tma.repository.PermissionRepository;
import backend.musicmanager.tma.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PermissionRepository permissionRepository;

    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    public Object findById(int id) {
        Role role = roleRepository.findById(id).orElse(null);
        if(role == null) return ObjectFailed.builder().message("Not found role with id " + id).status(404).build();
        return role;
    }
    public Object findByName(String name) {
        Role role = roleRepository.findByName(name);
        if(role == null) return ObjectFailed.builder().message("Not found role with name " + name).status(404).build();
        return role;
    }
    public Object create(RoleDTO roleDto) {
        Role role = new Role();
        return save(roleDto, role);
    }

    public Object update(int id, RoleDTO roleDto) {
        Role role = roleRepository.findById(id).orElse(null);
        if(role == null) return ObjectFailed.builder().message("Not found role with id " + id).status(404).build();
        return save(roleDto, role);
    }

    public Object save(RoleDTO roleDto, Role role) {
        List<Role> list = findAll();
        for (Role r : list) {
            if (r.getName().equals(roleDto.getName()) && r.getId() != role.getId())
                return ObjectFailed.builder().message("This role is already exist").status(409).build();
        }
        role.setName(roleDto.getName());

        Set<Permission> permissionSet = new HashSet<>();
        for (int id : roleDto.getListPermissionId()) {
            Permission permission = permissionRepository.findById(id).orElse(null);
            if (permission != null) permissionSet.add(permission);
        }
        role.setPermissions(permissionSet);

        return roleRepository.save(role);
    }

    public Object delete(int id) {
        Role role = roleRepository.findById(id).orElse(null);
        if(role == null) return ObjectFailed.builder().message("Not found role with id " + id).status(404).build();
        roleRepository.deleteById(id);
        return role;
    }
}
