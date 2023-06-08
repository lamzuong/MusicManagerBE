package backend.musicmanager.tma.repository;

import backend.musicmanager.tma.model.Playlist;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Integer> {
    @Query(value = "SELECT * FROM music_manager.playlist", nativeQuery = true)
    List<Playlist> getAllPlaylist(Pageable pageable);

    @Query(value = "SELECT count(*) FROM music_manager.playlist", nativeQuery = true)
    int countAllPlaylist();

    @Query(value = "SELECT * FROM music_manager.playlist where created_by=:id", nativeQuery = true)
    List<Playlist> findByUserId(@Param("id") int id, Pageable pageable);

    @Query(value = "SELECT count(*) FROM music_manager.playlist where created_by=:id", nativeQuery = true)
    int countByUserId(@Param("id") int id);
}
