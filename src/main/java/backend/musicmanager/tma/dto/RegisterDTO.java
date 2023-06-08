package backend.musicmanager.tma.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegisterDTO {
    private String username;
    private String password;
    private String role;
}
