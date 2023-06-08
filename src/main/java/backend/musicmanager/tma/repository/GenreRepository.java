package backend.musicmanager.tma.repository;

import backend.musicmanager.tma.model.Genre;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Integer> {
    @Query(value = "SELECT * FROM genres WHERE deleted = 0", nativeQuery = true)
    List<Genre> findAllGenre(Pageable pageable);
    @Query(value = "SELECT count(*) FROM genres WHERE deleted = 0", nativeQuery = true)
    int countAllGenre();

    @Query(value = "SELECT * FROM genres WHERE deleted = 0 AND name LIKE %:keyword%", nativeQuery = true)
    List<Genre> searchGenre(String keyword, Pageable pageable);
    @Query(value = "SELECT count(*) FROM genres WHERE deleted = 0 AND name LIKE %:keyword%", nativeQuery = true)
    int countSearchGenre(String keyword);
}
