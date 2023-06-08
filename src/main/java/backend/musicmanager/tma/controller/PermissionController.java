package backend.musicmanager.tma.controller;

import backend.musicmanager.tma.exception.ObjectFailed;
import backend.musicmanager.tma.model.Permission;
import backend.musicmanager.tma.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth/permission")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
public class PermissionController {
    @Autowired
    private PermissionService permissionService;

    @GetMapping
    public List<Permission> getAll() {
        return ResponseEntity.ok(permissionService.findAll()).getBody();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id) {
        Object obj = permissionService.findById(id);
        if (obj instanceof ObjectFailed)
            return ResponseEntity.status(((ObjectFailed) obj).getStatus()).body(((ObjectFailed) obj).getMessage());
        return ResponseEntity.ok(obj);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Permission permission) {
        Object obj = permissionService.create(permission);
        if (obj instanceof ObjectFailed)
            return ResponseEntity.status(((ObjectFailed) obj).getStatus()).body(((ObjectFailed) obj).getMessage());
        return ResponseEntity.ok(obj);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody Permission permission) {
        Object obj = permissionService.update(id, permission);
        if (obj instanceof ObjectFailed)
            return ResponseEntity.status(((ObjectFailed) obj).getStatus()).body(((ObjectFailed) obj).getMessage());
        return ResponseEntity.ok(obj);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        Object obj = permissionService.delete(id);
        if (obj instanceof ObjectFailed)
            return ResponseEntity.status(((ObjectFailed) obj).getStatus()).body(((ObjectFailed) obj).getMessage());
        return ResponseEntity.ok().body("Delete permission " + ((Permission) obj).getName() + " successfully ");
    }

}
