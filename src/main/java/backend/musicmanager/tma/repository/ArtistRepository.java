package backend.musicmanager.tma.repository;

import backend.musicmanager.tma.model.Artist;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Integer> {
    @Query(value = "SELECT * FROM artist WHERE deleted = 0", nativeQuery = true)
    List<Artist> findAllArtist(Pageable pageable);
    @Query(value = "SELECT count(*) FROM artist WHERE deleted = 0", nativeQuery = true)
    int countAllArtist();

    @Query(value = "SELECT * FROM artist WHERE deleted = 0 AND name LIKE %:keyword%", nativeQuery = true)
    List<Artist> searchArtist(@Param("keyword") String keyword, Pageable pageable);
    @Query(value = "SELECT count(*) FROM artist WHERE deleted = 0 AND name LIKE %:keyword%", nativeQuery = true)
    int countSearchArtist(@Param("keyword") String keyword);

    @Query(value = "SELECT * FROM artist WHERE deleted = 0 AND name LIKE :name", nativeQuery = true)
    Optional<Artist> getArtistByName(@Param("name") String name);


}
