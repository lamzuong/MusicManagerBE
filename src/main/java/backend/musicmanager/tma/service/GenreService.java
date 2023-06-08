package backend.musicmanager.tma.service;

import backend.musicmanager.tma.exception.ObjectFailed;
import backend.musicmanager.tma.model.Genre;
import backend.musicmanager.tma.repository.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GenreService {
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private AuthenticationService authenticationService;

    public Object findAll(int page, int limit, String sortBy, String direction, boolean getAll) {
        PageRequest pageRequest = null;
        Sort sort = Sort.by(sortBy);
        if (!getAll) {
            pageRequest = PageRequest.of(page - 1, limit, direction.equals("ASC") ? sort.ascending() : sort.descending());
        } else {
            pageRequest = PageRequest.of(0, Integer.MAX_VALUE, direction.equals("ASC") ? sort.ascending() : sort.descending());
        }
        int countGenre = genreRepository.countAllGenre();
        List<Genre> listGenre = genreRepository.findAllGenre(pageRequest);

        Map<String, Object> result = new HashMap<>();
        result.put("data", listGenre);
        result.put("totalRows", countGenre);
        return result;
    }

    public Object findById(int id) {
        Genre genre = genreRepository.findById(id).orElse(null);
        if (genre == null) return ObjectFailed.builder().message("Not found genre with id " + id).status(404).build();
        return genre;
    }

    public Object create(Genre genre) {
        return save(new Genre(), genre);
    }

    public Object update(int id, Genre newGenre) {
        Genre genre = genreRepository.findById(id).orElse(null);
        if (genre == null) return ObjectFailed.builder().message("Not found genre with id " + id).status(404).build();
        if (genre.isDeleted()) return ObjectFailed.builder().message("This genre is deleted").status(400).build();
        return save(genre, newGenre);
    }

    public Object save(Genre genre, Genre newGenre) {
        Sort sort = Sort.by("modified_at");
        PageRequest pageRequest = PageRequest.of(0, Integer.MAX_VALUE, sort.descending());
        List<Genre> list = genreRepository.findAllGenre(pageRequest);

        for (Genre g : list) {
            if (g.getName().equals(newGenre.getName()) && g.getId() != genre.getId())
                return ObjectFailed.builder().message("This genre is already exist").status(409).build();
        }
        genre.setName(newGenre.getName());
        genre.setCreatedBy(authenticationService.getCurrentUser());
        genre.setLastModifiedBy(authenticationService.getCurrentUser());
        return genreRepository.save(genre);
    }

    public Genre delete(int id) {
        Genre genre = genreRepository.findById(id).orElse(null);
        if (genre != null) {
            genre.setDeleted(true);
            genre.setLastModifiedBy(authenticationService.getCurrentUser());
            genreRepository.save(genre);
        }
        return genre;
    }

    public Object search(String keyword, int page, int limit, String sortBy, String direction, boolean getAll) {
        PageRequest pageRequest = null;
        Sort sort = Sort.by(sortBy);
        if (!getAll) {
            pageRequest = PageRequest.of(page - 1, limit, direction.equals("ASC") ? sort.ascending() : sort.descending());
        } else {
            pageRequest = PageRequest.of(0, Integer.MAX_VALUE, direction.equals("ASC") ? sort.ascending() : sort.descending());
        }
        int countGenre = genreRepository.countSearchGenre(keyword);
        List<Genre> listGenre = genreRepository.searchGenre(keyword, pageRequest);

        Map<String, Object> result = new HashMap<>();
        result.put("data", listGenre);
        result.put("totalRows", countGenre);
        return result;
    }
}
