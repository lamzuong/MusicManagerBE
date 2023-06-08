package backend.musicmanager.tma.repository;

import backend.musicmanager.tma.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    @Query(value = "SELECT * FROM user", nativeQuery = true)
    List<User> findAllUser(Pageable pageable);
    @Query(value = "SELECT count(*) FROM user", nativeQuery = true)
    int countAllUser();
}
