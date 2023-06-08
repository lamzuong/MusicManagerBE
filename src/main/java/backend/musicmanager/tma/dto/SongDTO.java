package backend.musicmanager.tma.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;
@Getter
@Setter
public class SongDTO {
    private String name;
    private String url;
    private String image;
    private boolean favorite;
    private Set<Integer> artists;
    private int genreId;
}
