package backend.musicmanager.tma.model;

import backend.musicmanager.tma.audit.AuditModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "song")
@Getter
@Setter
public class Song extends AuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "url")
    private String url;
    @Column(name = "image")
    private String image;
    @Column(name = "favorite")
    private boolean favorite;

    @ManyToMany
    @JoinTable(name = "songs_artists",
            joinColumns = {@JoinColumn(name = "song_id")},
            inverseJoinColumns = {@JoinColumn(name = "artist_id")})
    private Set<Artist> artists;

    @ManyToOne
    @JoinColumn(name = "genre_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer"})
    private Genre genre;

    @JsonIgnore
    @ManyToMany(mappedBy = "songs")
    private Set<Playlist> playlists;
}
