package backend.musicmanager.tma.model;

import backend.musicmanager.tma.audit.AuditModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "genres")
@Getter
@Setter
public class Genre extends AuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "deleted")
    private boolean deleted;

    @JsonIgnore
    @OneToMany(mappedBy = "genre")
    private Set<Song> songs;
}
