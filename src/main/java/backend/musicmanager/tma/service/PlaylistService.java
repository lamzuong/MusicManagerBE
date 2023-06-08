package backend.musicmanager.tma.service;

import backend.musicmanager.tma.dto.PlaylistDTO;
import backend.musicmanager.tma.exception.ObjectFailed;
import backend.musicmanager.tma.model.Playlist;
import backend.musicmanager.tma.model.Song;
import backend.musicmanager.tma.model.User;
import backend.musicmanager.tma.repository.PlaylistRepository;
import backend.musicmanager.tma.repository.SongRepository;
import backend.musicmanager.tma.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PlaylistService {
    @Autowired
    private PlaylistRepository playlistRepository;
    @Autowired
    private SongRepository songRepository;
    @Autowired
    private AuthenticationService authenticationService;
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
        List<Playlist> playlist = playlistRepository.getAllPlaylist(pageRequest);
        int count = playlistRepository.countAllPlaylist();

        Map<String, Object> result = new HashMap<>();
        result.put("data", playlist);
        result.put("totalRows", count);
        return result;
    }

    public Object findById(int id) {
        Playlist playlist = playlistRepository.findById(id).orElse(null);
        if (playlist == null)
            return ObjectFailed.builder().status(404).message("Not found playlist with id " + id).build();
        if (playlist != null && playlist.getCreatedBy().getId() != authenticationService.getCurrentUser().getId())
            return ObjectFailed.builder().status(403).message("This playlist not of current user").build();
        return playlist;
    }

    public Object update(int id, PlaylistDTO playlistDTO) {
        Playlist playlist = playlistRepository.findById(id).orElse(null);
        if (playlist == null)
            return ObjectFailed.builder().status(404).message("Not found playlist with id " + id).build();
        if (playlist != null && playlist.getCreatedBy().getId() != authenticationService.getCurrentUser().getId())
            return ObjectFailed.builder().status(403).message("This playlist not of current user").build();
        return save(playlist, playlistDTO);
    }

    public Playlist create(PlaylistDTO playlistDTO) {
        return save(new Playlist(), playlistDTO);
    }

    public Playlist save(Playlist playlist, PlaylistDTO playlistDTO) {
        playlist.setName(playlistDTO.getName());
        playlist.setAvatar(playlistDTO.getAvatar());
        Set<Song> songs = new HashSet<>();
        Set<Integer> setIdSong = playlistDTO.getSongs();
        if (setIdSong != null) {
            for (int id : playlistDTO.getSongs()) {
                Song song = songRepository.findById(id).orElse(null);
                if (song != null) songs.add(song);
            }
            playlist.setSongs(songs);
        }
        playlist.setCreatedBy(authenticationService.getCurrentUser());
        playlist.setLastModifiedBy(authenticationService.getCurrentUser());
        return playlistRepository.save(playlist);
    }

    public Object addSong(int id, Set<Integer> songs) {
        Playlist playlist = playlistRepository.findById(id).orElse(null);
        if (playlist == null)
            return ObjectFailed.builder().status(404).message("Not found playlist with id " + id).build();
        if (playlist != null && playlist.getCreatedBy().getId() != authenticationService.getCurrentUser().getId())
            return ObjectFailed.builder().status(403).message("This playlist not of current user").build();
        Set<Song> songsInPlaylist = playlist.getSongs();
        for (int idSong : songs) {
            Song song = songRepository.findById(idSong).orElse(null);
            if (song != null && song.getCreatedBy().getId() == authenticationService.getCurrentUser().getId())
                songsInPlaylist.add(song);
        }
        playlist.setSongs(songsInPlaylist);
        return playlistRepository.save(playlist);
    }

    public Object removeSong(int id, Set<Integer> songs) {
        Playlist playlist = playlistRepository.findById(id).orElse(null);
        if (playlist == null)
            return ObjectFailed.builder().status(404).message("Not found playlist with id " + id).build();
        if (playlist != null && playlist.getCreatedBy().getId() != authenticationService.getCurrentUser().getId())
            return ObjectFailed.builder().status(403).message("This playlist not of current user").build();
        Set<Song> songsInPlaylist = playlist.getSongs();
        for (int idSong : songs) {
            Song song = songRepository.findById(idSong).orElse(null);
            if (song != null && song.getCreatedBy().getId() == authenticationService.getCurrentUser().getId())
                songsInPlaylist.remove(song);
        }
        playlist.setSongs(songsInPlaylist);
        return playlistRepository.save(playlist);
    }

    public Object delete(int id) {
        Playlist playlist = playlistRepository.findById(id).orElse(null);
        if (playlist == null)
            return ObjectFailed.builder().status(404).message("Not found playlist with id " + id).build();
        if (playlist != null && playlist.getCreatedBy().getId() != authenticationService.getCurrentUser().getId())
            return ObjectFailed.builder().status(403).message("This playlist not of current user").build();
        playlistRepository.deleteById(id);
        return playlist;
    }

    public Object findByUserId(int page, int limit, String sortBy, String direction, boolean getAll) {
        User user = authenticationService.getCurrentUser();
        PageRequest pageRequest = null;
        Sort sort = Sort.by(sortBy);
        if (!getAll) {
            pageRequest = PageRequest.of(page - 1, limit, direction.equals("ASC") ? sort.ascending() : sort.descending());
        } else {
            pageRequest = PageRequest.of(0, Integer.MAX_VALUE, direction.equals("ASC") ? sort.ascending() : sort.descending());
        }
        List<Playlist> playlist = playlistRepository.findByUserId(user.getId(), pageRequest);
        int count = playlistRepository.countByUserId(user.getId());

        Map<String, Object> result = new HashMap<>();
        result.put("data", playlist);
        result.put("totalRows", count);
        return result;
    }
}
