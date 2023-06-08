package backend.musicmanager.tma.repository;

import backend.musicmanager.tma.model.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SongRepository extends JpaRepository<Song, Integer> {
    @Query(value = "SELECT * FROM song", nativeQuery = true)
    List<Song> findAllSong(Pageable pageable);
    @Query(value = "SELECT count(*) FROM song", nativeQuery = true)
    int countAllSong();

    @Query(value = "SELECT * FROM song WHERE name like %:keyword% AND created_by = :userId", nativeQuery = true)
    List<Song> searchSong(@Param("keyword") String keyword, @Param("userId") int userId, Pageable pageable);

    @Query(value = "SELECT count(*) FROM song WHERE name like %:keyword% AND created_by = :userId", nativeQuery = true)
    int countSearchSong(@Param("keyword") String keyword, @Param("userId") int userId);

    @Query(value = "SELECT s.* FROM song s JOIN playlist_song ps ON s.id = ps.song_id JOIN playlist p ON ps.playlist_id = p.id WHERE s.name LIKE %:keyword% AND p.id = :playlistId", nativeQuery = true)
    List<Song> searchSongInPlaylist(@Param("keyword") String keyword, @Param("playlistId") int playlistId, Pageable pageable);

    @Query(value = "SELECT count(*) FROM song s JOIN playlist_song ps ON s.id = ps.song_id JOIN playlist p ON ps.playlist_id = p.id WHERE s.name LIKE %:keyword% AND p.id = :playlistId", nativeQuery = true)
    int countSearchSongInPlaylist(@Param("keyword") String keyword, @Param("playlistId") int playlistId);

    @Query(value = "SELECT s.* FROM song s JOIN playlist_song ps ON s.id = ps.song_id JOIN playlist p ON ps.playlist_id = p.id WHERE p.id = :playlistId", nativeQuery = true)
    List<Song> getSongByPlaylistId(@Param("playlistId") int playlistId, Pageable pageable);

    @Query(value = "SELECT count(*) FROM song s JOIN playlist_song ps ON s.id = ps.song_id JOIN playlist p ON ps.playlist_id = p.id WHERE p.id = :playlistId", nativeQuery = true)
    int countSongByPlaylistId(@Param("playlistId") int playlistId);

    @Query(value = "SELECT * FROM song WHERE created_by = :userId", nativeQuery = true)
    List<Song> getSongByUserId(@Param("userId") int userId, Pageable pageable);

    @Query(value = "SELECT count(*) FROM song WHERE created_by = :userId", nativeQuery = true)
    int countSongByUserId(@Param("userId") int userId);

    @Query(value = "SELECT * FROM song WHERE created_by = :userId AND favorite = 1", nativeQuery = true)
    List<Song> getFavoriteSong(@Param("userId") int userId, Pageable pageable);

    @Query(value = "SELECT count(*) FROM song WHERE created_by = :userId AND favorite = 1", nativeQuery = true)
    int countFavoriteSong(@Param("userId") int userId);

    @Query(value = "SELECT * FROM song WHERE name like %:keyword% AND created_by = :userId AND favorite = 1", nativeQuery = true)
    List<Song> searchFavoriteSong(@Param("keyword") String keyword, @Param("userId") int userId, Pageable pageable);

    @Query(value = "SELECT count(*) FROM song WHERE name like %:keyword% AND created_by = :userId AND favorite = 1", nativeQuery = true)
    int countSearchFavoriteSong(@Param("keyword") String keyword, @Param("userId") int userId);

}
