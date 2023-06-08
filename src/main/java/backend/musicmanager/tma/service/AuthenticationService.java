package backend.musicmanager.tma.service;

import backend.musicmanager.tma.security.CustomUserDetails;
import backend.musicmanager.tma.security.JwtService;
import backend.musicmanager.tma.dto.LoginDTO;
import backend.musicmanager.tma.dto.RegisterDTO;
import backend.musicmanager.tma.exception.ObjectFailed;
import backend.musicmanager.tma.model.Role;
import backend.musicmanager.tma.model.User;
import backend.musicmanager.tma.repository.RoleRepository;
import backend.musicmanager.tma.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthenticationService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PermissionService permissionService;

    public Object checkValidRegister(RegisterDTO registerDTO) {
        if (!userRepository.findByUsername(registerDTO.getUsername()).isEmpty())
            return ObjectFailed.builder().message("Username is already exist").status(409).build();
        if (!registerDTO.getUsername().matches("^\\w{5,}$"))
            return ObjectFailed.builder().message("Username have at least 5 characters just only includes word, number").status(400).build();
        if (!registerDTO.getPassword().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"))
            return ObjectFailed.builder().message("Password have at least 8 characters includes at least 1 uppercase word, 1 lowercase word, 1 number, 1 special character").status(400).build();
        return true;
    }

    public Object register(RegisterDTO registerDTO) {
        if (registerDTO.getRole().equals("ADMIN")) {
            User user;
            try {
                user = getCurrentUser();
            } catch (Exception e) {
                return ObjectFailed.builder().message("You don't have permission to create ADMIN account").status(403).build();
            }
            if (user == null || user.getRole().getName().equals("USER"))
                return ObjectFailed.builder().message("You don't have permission to create ADMIN account").status(403).build();
        }
        Object check = checkValidRegister(registerDTO);
        if (check instanceof ObjectFailed) return check;
        Role userRole = roleRepository.findByName(registerDTO.getRole());
        var user = User.builder()
                .username(registerDTO.getUsername())
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .blocked(false)
                .role(userRole)
                .build();
        CustomUserDetails userDetails = new CustomUserDetails(user);
        var jwtToken = jwtService.generateToken(userDetails);
        userRepository.save(user);
        return jwtToken;
    }

    public Object login(LoginDTO loginDTO) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDTO.getUsername(),
                            loginDTO.getPassword()
                    )
            );
            var user = userRepository.findByUsername(loginDTO.getUsername()).orElseThrow();
            if (user.isBlocked()) return ObjectFailed.builder().message("User is blocked").status(403).build();
            CustomUserDetails userDetails = new CustomUserDetails(user);
            var jwtToken = jwtService.generateToken(userDetails);
            userRepository.save(user);
            return jwtToken;
        } catch (AuthenticationException e) {
            return ObjectFailed.builder().message("Invalid Username or Password").status(401).build();
        }
    }

    public Object changePassword(String password) {
        User user = getCurrentUser();
        if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"))
            return ObjectFailed.builder().message("Password must have 8 characters includes at least 1 uppercase word, 1 lowercase word, 1 number, 1 special character").status(400).build();

        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        return user;
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int id = ((CustomUserDetails) authentication.getPrincipal()).getUser().getId();
        User user = userRepository.findById(id).orElse(null);
        return user;
    }

    public boolean checkPermission(String permission) {
        List<String> permissions = permissionService.getPermissionForCurrentUser(getCurrentUser().getId());
        return permissions.contains(permission);
    }

}
