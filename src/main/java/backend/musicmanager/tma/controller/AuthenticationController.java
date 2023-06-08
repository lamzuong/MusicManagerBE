package backend.musicmanager.tma.controller;

import backend.musicmanager.tma.dto.LoginDTO;
import backend.musicmanager.tma.dto.RegisterDTO;
import backend.musicmanager.tma.exception.ObjectFailed;
import backend.musicmanager.tma.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDTO registerDTO) {
        Object response = authenticationService.register(registerDTO);
        if (response instanceof ObjectFailed)
            return ResponseEntity.status(((ObjectFailed) response).getStatus()).body(((ObjectFailed) response).getMessage());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        Object auth = authenticationService.login(loginDTO);
        if (auth instanceof ObjectFailed)
            return ResponseEntity.status(((ObjectFailed) auth).getStatus()).body(((ObjectFailed) auth).getMessage());
        return ResponseEntity.ok(auth);
    }

}
