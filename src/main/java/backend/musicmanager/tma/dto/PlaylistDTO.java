package backend.musicmanager.tma.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class PlaylistDTO {
    private String name;
    private String avatar;
    private Set<Integer> songs;
}
