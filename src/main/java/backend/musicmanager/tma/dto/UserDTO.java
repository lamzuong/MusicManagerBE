package backend.musicmanager.tma.dto;

import backend.musicmanager.tma.model.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private int id;
    private String name;
    private String gender;
    private String birthday;
    private String avatar;
    private boolean blocked;
    private String role;
}
