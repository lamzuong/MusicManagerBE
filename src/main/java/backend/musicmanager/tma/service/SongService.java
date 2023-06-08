package backend.musicmanager.tma.service;

import backend.musicmanager.tma.dto.SongDTO;
import backend.musicmanager.tma.exception.ObjectFailed;
import backend.musicmanager.tma.model.Artist;
import backend.musicmanager.tma.model.Genre;
import backend.musicmanager.tma.model.Playlist;
import backend.musicmanager.tma.model.Song;
import backend.musicmanager.tma.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SongService {
    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private SongRepository songRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private PlaylistRepository playlistRepository;
    @Autowired
    private PlaylistService playlistService;
    @Autowired
    private UserRepository userRepository;

    public Object findAll(int page, int limit, String sortBy, String direction, boolean getAll) {
        PageRequest pageRequest = null;
        Sort sort = Sort.by(sortBy);

        if (!getAll) {
            pageRequest = PageRequest.of(page - 1, limit, direction.equals("ASC") ? sort.ascending() : sort.descending());
        } else {
            pageRequest = PageRequest.of(0, Integer.MAX_VALUE, direction.equals("ASC") ? sort.ascending() : sort.descending());
        }
        int countSong = songRepository.countAllSong();
        List<Song> listSong = songRepository.findAllSong(pageRequest);

        Map<String, Object> result = new HashMap<>();
        result.put("data", listSong);
        result.put("totalRows", countSong);
        return result;
    }

    public Object findById(int id) {
        Song song = songRepository.findById(id).orElse(null);
        if (song != null && song.getCreatedBy().getId() != authenticationService.getCurrentUser().getId()) {
            return ObjectFailed.builder().status(403).message("This song is not for current user").build();
        }
        if (song == null) ObjectFailed.builder().status(404).message("Not found song with id " + id).build();
        return song;
    }

    public Song create(SongDTO songDTO) {
        return save(new Song(), songDTO);
    }

    public Object update(int id, SongDTO songDTO) {
        Song song = songRepository.findById(id).orElse(null);
        if (song != null && song.getCreatedBy().getId() != authenticationService.getCurrentUser().getId()) {
            return ObjectFailed.builder().status(403).message("This song is not of current user").build();
        }
        if (song == null) return ObjectFailed.builder().status(404).message("Not found song with id " + id).build();
        return save(song, songDTO);
    }

    public Song save(Song song, SongDTO songDTO) {
        song.setName(songDTO.getName());
        song.setImage(songDTO.getImage());
        song.setUrl(songDTO.getUrl());

        Genre genre = genreRepository.findById(songDTO.getGenreId()).orElse(null);
        if (genre != null) song.setGenre(genre);

        Set<Artist> artists = new HashSet<>();
        for (int id : songDTO.getArtists()) {
            Artist artist = artistRepository.findById(id).orElse(null);
            if (artist != null) artists.add(artist);
        }
        song.setArtists(artists);
        song.setCreatedBy(authenticationService.getCurrentUser());
        song.setLastModifiedBy(authenticationService.getCurrentUser());
        return songRepository.save(song);
    }

    public Song delete(int id) {
        Song song = songRepository.findById(id).orElse(null);
        if (song != null) {
            song.getPlaylists().forEach(e ->
                    playlistService.removeSong(e.getId(), new HashSet<>(Arrays.asList(id))));
            songRepository.deleteById(id);
        }
        return song;
    }

    public Object search(String keyword, int page, int limit, String sortBy, String direction, boolean favorite, boolean getAll) {
        int userId = authenticationService.getCurrentUser().getId();
        Sort sort = Sort.by(sortBy);
        PageRequest pageRequest;
        if (!getAll) {
            pageRequest = PageRequest.of(page - 1, limit, direction.equals("ASC") ? sort.ascending() : sort.descending());
        } else {
            pageRequest = PageRequest.of(0, Integer.MAX_VALUE, direction.equals("ASC") ? sort.ascending() : sort.descending());
        }
        List<Song> listSong;
        int countSong;
        if (favorite) {
            listSong = songRepository.searchFavoriteSong(keyword, userId, pageRequest);
            countSong = songRepository.countSearchFavoriteSong(keyword, userId);
        } else {
            listSong = songRepository.searchSong(keyword, userId, pageRequest);
            countSong = songRepository.countSearchSong(keyword, userId);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("data", listSong);
        result.put("totalRows", countSong);
        return result;
    }

    public Object searchInPlaylist(String keyword, int playlistId, int page, int limit, String sortBy, String direction, boolean getAll) {
        Playlist playlist = playlistRepository.findById(playlistId).orElse(null);
        if (playlist == null)
            return ObjectFailed.builder().message("Not found playlist with id " + playlistId).status(404).build();
        if (playlist.getCreatedBy().getId() != authenticationService.getCurrentUser().getId())
            return ObjectFailed.builder().message("This playlist not for current user").status(403).build();
        Sort sort = Sort.by(sortBy);
        PageRequest pageRequest;
        if (!getAll) {
            pageRequest = PageRequest.of(page - 1, limit, direction.equals("ASC") ? sort.ascending() : sort.descending());
        } else {
            pageRequest = PageRequest.of(0, Integer.MAX_VALUE, direction.equals("ASC") ? sort.ascending() : sort.descending());
        }
        List<Song> listSong = songRepository.searchSongInPlaylist(keyword, playlistId, pageRequest);
        int countSong = songRepository.countSearchSongInPlaylist(keyword, playlistId);

        Map<String, Object> result = new HashMap<>();
        result.put("data", listSong);
        result.put("totalRows", countSong);
        return result;
    }

    public Object findByUserId(int page, int limit, String sortBy, String direction, boolean favorite, boolean getAll) {
        int id = authenticationService.getCurrentUser().getId();
        PageRequest pageRequest = null;
        Sort sort = Sort.by(sortBy);
        if (!getAll) {
            pageRequest = PageRequest.of(page - 1, limit, direction.equals("ASC") ? sort.ascending() : sort.descending());
        } else {
            pageRequest = PageRequest.of(0, Integer.MAX_VALUE, direction.equals("ASC") ? sort.ascending() : sort.descending());
        }

        int countSong;
        List<Song> listSong;

        if (favorite) {
            countSong = songRepository.countFavoriteSong(id);
            listSong = songRepository.getFavoriteSong(id, pageRequest);
        } else {
            countSong = songRepository.countSongByUserId(id);
            listSong = songRepository.getSongByUserId(id, pageRequest);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("data", listSong);
        result.put("totalRows", countSong);
        return result;
    }

    public Object findByPlaylistId(int id, Integer page, Integer limit, String sortBy, String direction, boolean getAll) {
        PageRequest pageRequest = null;
        Sort sort = Sort.by(sortBy);
        if (!getAll) {
            pageRequest = PageRequest.of(page - 1, limit, direction.equals("ASC") ? sort.ascending() : sort.descending());
        } else {
            pageRequest = PageRequest.of(0, Integer.MAX_VALUE, direction.equals("ASC") ? sort.ascending() : sort.descending());
        }
        List<Song> listSong = songRepository.getSongByPlaylistId(id, pageRequest);
        int count = songRepository.countSongByPlaylistId(id);

        Map<String, Object> result = new HashMap<>();
        result.put("data", listSong);
        result.put("totalRows", count);
        return result;
    }

    public List<Song> addSongToFavoriteList(Set<Integer> songs) {
        int userId = authenticationService.getCurrentUser().getId();

        for (int id : songs) {
            Song song = songRepository.findById(id).orElse(null);
            if (song != null && song.getCreatedBy().getId() == userId) {
                if (!song.isFavorite()) {
                    song.setFavorite(true);
                    songRepository.save(song);
                }
            }
        }

        Sort sort = Sort.by("modified_at");
        PageRequest pageRequest = PageRequest.of(0, 5, sort.descending());
        return songRepository.getFavoriteSong(userId, pageRequest);
    }

    public List<Song> removeSongFromFavoriteList(Set<Integer> songs) {
        int userId = authenticationService.getCurrentUser().getId();

        for (int id : songs) {
            Song song = songRepository.findById(id).orElse(null);
            if (song != null && song.getCreatedBy().getId() == userId) {
                if (song.isFavorite()) {
                    song.setFavorite(false);
                    songRepository.save(song);
                }
            }
        }

        Sort sort = Sort.by("modified_at");
        PageRequest pageRequest = PageRequest.of(0, 5, sort.descending());
        return songRepository.getFavoriteSong(userId, pageRequest);
    }

}
