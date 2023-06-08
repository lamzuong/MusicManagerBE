package backend.musicmanager.tma.controller;

import backend.musicmanager.tma.dto.UserDTO;
import backend.musicmanager.tma.exception.ObjectFailed;
import backend.musicmanager.tma.model.User;
import backend.musicmanager.tma.service.AuthenticationService;
import backend.musicmanager.tma.service.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class UserController {
    @Autowired
    private UserManagementService userManagementService;
    @Autowired
    private AuthenticationService authenticationService;

    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
                                    @RequestParam(name = "limit", required = false, defaultValue = "5") Integer limit,
                                    @RequestParam(name = "sortBy", required = false, defaultValue = "modified_at") String sortBy,
                                    @RequestParam(name = "direction", required = false, defaultValue = "DESC") String direction,
                                    @RequestParam(name = "all", required = false, defaultValue = "false") boolean getAll) {
        if (authenticationService.checkPermission("READ_USER") && authenticationService.getCurrentUser().getRole().getName().equals("ADMIN"))
            return ResponseEntity.ok(userManagementService.getAllUser(page, limit, sortBy, direction, getAll));
        return ResponseEntity.status(403).body("You don't have permission to get all user");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id) {
        if (authenticationService.checkPermission("READ_USER")) {
            Object obj = userManagementService.getUserById(id);
            if (obj instanceof ObjectFailed)
                return ResponseEntity.status(((ObjectFailed) obj).getStatus()).body(((ObjectFailed) obj).getMessage());
            return ResponseEntity.ok(obj);
        }
        return ResponseEntity.status(403).body("You don't have permission to get user");
    }

    @GetMapping("/current_user")
    public ResponseEntity<?> getCurrentUser() {
        User user = authenticationService.getCurrentUser();
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setBlocked(user.isBlocked());
        userDTO.setGender(user.getGender());
        userDTO.setBirthday(user.getBirthday());
        userDTO.setAvatar(user.getAvatar());
        userDTO.setRole(user.getRole().getName());
        return ResponseEntity.ok(userDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody User user) {
        if (authenticationService.checkPermission("UPDATE_USER")) {
            Object obj = userManagementService.update(id, user);
            if (obj instanceof ObjectFailed)
                return ResponseEntity.status(((ObjectFailed) obj).getStatus()).body(((ObjectFailed) obj).getMessage());
            return ResponseEntity.ok(obj);
        }
        return ResponseEntity.status(403).body("You don't have permission to update user");
    }

    @PutMapping("/change_pass")
    public ResponseEntity<?> changePass(@RequestBody String newPassword) {
        if (authenticationService.checkPermission("UPDATE_USER")) {
            Object obj = authenticationService.changePassword(newPassword);
            if (obj instanceof ObjectFailed)
                return ResponseEntity.status(((ObjectFailed) obj).getStatus()).body(((ObjectFailed) obj).getMessage());
            return ResponseEntity.ok(obj);
        }
        return ResponseEntity.status(403).body("You don't have permission to update user");
    }

    @PutMapping("/block_unblock/{id}")
    public ResponseEntity<?> blockUser(@PathVariable int id) {
        if (authenticationService.checkPermission("DELETE_USER")) {
            Object obj = userManagementService.block(id);
            if (obj instanceof ObjectFailed)
                return ResponseEntity.status(((ObjectFailed) obj).getStatus()).body(((ObjectFailed) obj).getMessage());
            if (((User) obj).isBlocked())
                return ResponseEntity.ok().body("Blocked user with id " + id);
            return ResponseEntity.ok().body("Unblocked user with id " + id);
        }
        return ResponseEntity.status(403).body("You don't have permission to block user");
    }


}
