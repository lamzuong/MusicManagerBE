package backend.musicmanager.tma.service;

import backend.musicmanager.tma.exception.ObjectFailed;
import backend.musicmanager.tma.model.Artist;
import backend.musicmanager.tma.model.Genre;
import backend.musicmanager.tma.repository.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ArtistService {
    @Autowired
    private ArtistRepository artistRepository;
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
        int countArtist = artistRepository.countAllArtist();
        List<Artist> listArtist = artistRepository.findAllArtist(pageRequest);

        Map<String, Object> result = new HashMap<>();
        result.put("data", listArtist);
        result.put("totalRows", countArtist);
        return result;
    }

    public Object findById(int id) {
        Artist artist = artistRepository.findById(id).orElse(null);
        if (artist == null)
            return ObjectFailed.builder().status(404).message("Failed to update artist, not found artist with id " + id).build();
        return artist;
    }

    public Object save(Artist artist) {
        Artist artistFind = artistRepository.getArtistByName(artist.getName()).orElse(null);
        if (artistFind != null && artistFind.getId() != artist.getId())
            return ObjectFailed.builder().status(409).message("Artist is already exist").build();
        artist.setCreatedBy(authenticationService.getCurrentUser());
        artist.setLastModifiedBy(authenticationService.getCurrentUser());
        return artistRepository.save(artist);
    }

    public Object search(String keyword, int page, int limit, String sortBy, String direction, boolean getAll) {
        PageRequest pageRequest = null;
        Sort sort = Sort.by(sortBy);
        if (!getAll) {
            pageRequest = PageRequest.of(page - 1, limit, direction.equals("ASC") ? sort.ascending() : sort.descending());
        } else {
            pageRequest = PageRequest.of(0, Integer.MAX_VALUE, direction.equals("ASC") ? sort.ascending() : sort.descending());
        }

        int countArtist = artistRepository.countSearchArtist(keyword);
        List<Artist> listArtist = artistRepository.searchArtist(keyword, pageRequest);

        Map<String, Object> result = new HashMap<>();
        result.put("data", listArtist);
        result.put("totalRows", countArtist);
        return result;
    }

    public Object update(int id, Artist newArtist) {
        Artist artist = artistRepository.findById(id).orElse(null);
        if (artist == null)
            return ObjectFailed.builder().status(404).message("Failed to update artist, not found artist with id " + id).build();
        if (artist.isDeleted()) return ObjectFailed.builder().status(400).message("This genre is deleted").build();
        artist.setName(newArtist.getName());
        artist.setImage(newArtist.getImage());
        artist.setLastModifiedBy(authenticationService.getCurrentUser());
        return artistRepository.save(artist);
    }

    public Artist deleteById(int id) {
        Artist artist = artistRepository.findById(id).orElse(null);
        if (artist != null) {
            artist.setDeleted(true);
            artist.setLastModifiedBy(authenticationService.getCurrentUser());
            artistRepository.save(artist);
        }
        return artist;
    }
    public Artist deleteByName(String name) {
        Artist artist = artistRepository.getArtistByName(name).orElse(null);
        if (artist != null) {
            artist.setDeleted(true);
            artist.setLastModifiedBy(authenticationService.getCurrentUser());
            artistRepository.save(artist);
        }
        return artist;
    }
}
