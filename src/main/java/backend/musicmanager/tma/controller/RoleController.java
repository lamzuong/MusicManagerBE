package backend.musicmanager.tma.controller;

import backend.musicmanager.tma.dto.RoleDTO;
import backend.musicmanager.tma.exception.ObjectFailed;
import backend.musicmanager.tma.model.Role;
import backend.musicmanager.tma.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth/role")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
public class RoleController {
    @Autowired
    private RoleService roleService;

    @GetMapping
    public List<Role> getAll() {
        return ResponseEntity.ok(roleService.findAll()).getBody();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id) {
        Object obj = roleService.findById(id);
        if (obj instanceof ObjectFailed)
            return ResponseEntity.status(((ObjectFailed) obj).getStatus()).body(((ObjectFailed) obj).getMessage());
        return ResponseEntity.ok(obj);
    }

    @GetMapping("/by_name/{name}")
    public ResponseEntity<?> getByName(@PathVariable String name) {
        Object obj = roleService.findByName(name);
        if (obj instanceof ObjectFailed)
            return ResponseEntity.status(((ObjectFailed) obj).getStatus()).body(((ObjectFailed) obj).getMessage());
        return ResponseEntity.ok(obj);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody RoleDTO role) {
        Object obj = roleService.create(role);
        if (obj instanceof ObjectFailed)
            return ResponseEntity.status(((ObjectFailed) obj).getStatus()).body(((ObjectFailed) obj).getMessage());
        return ResponseEntity.ok(obj);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody RoleDTO role) {
        Object obj = roleService.update(id, role);
        if (obj instanceof ObjectFailed)
            return ResponseEntity.status(((ObjectFailed) obj).getStatus()).body(((ObjectFailed) obj).getMessage());
        return ResponseEntity.ok(obj);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        Object obj = roleService.delete(id);
        if (obj instanceof ObjectFailed)
            return ResponseEntity.status(((ObjectFailed) obj).getStatus()).body(((ObjectFailed) obj).getMessage());
        return ResponseEntity.ok().body("Delete role " + ((Role) obj).getName() + " successfully");
    }
}
