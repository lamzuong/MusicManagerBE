package backend.musicmanager.tma.model;

import backend.musicmanager.tma.audit.AuditModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "artist")
@Getter
@Setter
public class Artist extends AuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "name", unique = true)
    private String name;
    @Column(name = "image")
    private String image;
    @Column(name = "deleted")
    private boolean deleted;

    @JsonIgnore
    @ManyToMany(mappedBy = "artists")
    private Set<Song> songs;

}
