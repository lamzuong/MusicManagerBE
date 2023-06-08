package backend.musicmanager.tma.controller;

import backend.musicmanager.tma.dto.SongDTO;
import backend.musicmanager.tma.exception.ObjectFailed;
import backend.musicmanager.tma.model.Song;
import backend.musicmanager.tma.service.AuthenticationService;
import backend.musicmanager.tma.service.PermissionService;
import backend.musicmanager.tma.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/song")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class SongController {
    @Autowired
    private SongService songService;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private AuthenticationService authenticationService;

    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
                                    @RequestParam(name = "limit", required = false, defaultValue = "5") Integer limit,
                                    @RequestParam(name = "sortBy", required = false, defaultValue = "modified_at") String sortBy,
                                    @RequestParam(name = "direction", required = false, defaultValue = "DESC") String direction,
                                    @RequestParam(name = "all", required = false, defaultValue = "false") boolean getAll) {
        if (authenticationService.checkPermission("READ_SONG"))
            return ResponseEntity.ok(songService.findAll(page, limit, sortBy, direction, getAll));
        return ResponseEntity.status(403).body("You don't have permission to get song");
    }

    @GetMapping("/user")
    public ResponseEntity<?> getSongByUserId(
            @RequestParam(name = "favorite", required = false, defaultValue = "false") boolean favorite,
            @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(name = "limit", required = false, defaultValue = "5") Integer limit,
            @RequestParam(name = "sortBy", required = false, defaultValue = "modified_at") String sortBy,
            @RequestParam(name = "direction", required = false, defaultValue = "DESC") String direction,
            @RequestParam(name = "all", required = false, defaultValue = "false") boolean getAll) {
        if (authenticationService.checkPermission("READ_SONG")) {
            Object obj = songService.findByUserId(page, limit, sortBy, direction, favorite, getAll);
            if (obj instanceof ObjectFailed)
                return ResponseEntity.status(((ObjectFailed) obj).getStatus()).body(((ObjectFailed) obj).getMessage());
            return ResponseEntity.ok(obj);
        }
        return ResponseEntity.status(403).body("You don't have permission to get song");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id) {
        if (authenticationService.checkPermission("READ_SONG")) {
            Object obj = songService.findById(id);
            if (obj instanceof ObjectFailed)
                return ResponseEntity.status(((ObjectFailed) obj).getStatus()).body(((ObjectFailed) obj).getMessage());
            return ResponseEntity.ok(obj);
        }
        return ResponseEntity.status(403).body("You don't have permission to get song");
    }

    @GetMapping("/playlist/{id}")
    public ResponseEntity<?> getSongByPlaylistId(@PathVariable int id,
                                                 @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
                                                 @RequestParam(name = "limit", required = false, defaultValue = "5") Integer limit,
                                                 @RequestParam(name = "sortBy", required = false, defaultValue = "modified_at") String sortBy,
                                                 @RequestParam(name = "direction", required = false, defaultValue = "DESC") String direction,
                                                 @RequestParam(name = "all", required = false, defaultValue = "false") boolean getAll) {
        if (authenticationService.checkPermission("READ_PLAYLIST")) {
            Object obj = songService.findByPlaylistId(id, page, limit, sortBy, direction, getAll);
            if (obj instanceof ObjectFailed)
                return ResponseEntity.status(((ObjectFailed) obj).getStatus()).body(((ObjectFailed) obj).getMessage());
            return ResponseEntity.ok(obj);
        }
        return ResponseEntity.status(403).body("You don't have permission to get playlist");
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam(name = "keyword") String keyword,
                                    @RequestParam(name = "favorite", required = false, defaultValue = "false") boolean favorite,
                                    @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
                                    @RequestParam(name = "limit", required = false, defaultValue = "5") Integer limit,
                                    @RequestParam(name = "sortBy", required = false, defaultValue = "modified_at") String sortBy,
                                    @RequestParam(name = "direction", required = false, defaultValue = "DESC") String direction,
                                    @RequestParam(name = "all", required = false, defaultValue = "false") boolean getAll) {
        if (authenticationService.checkPermission("READ_SONG")) {
            return ResponseEntity.ok(songService.search(keyword, page, limit, sortBy, direction, favorite, getAll));
        }
        return ResponseEntity.status(403).body("You don't have permission to get song");
    }

    @GetMapping("/search_in_playlist")
    public ResponseEntity<?> searchInPlaylist(@RequestParam(name = "keyword") String keyword, @RequestParam(name = "playlistId") int id,
                                              @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
                                              @RequestParam(name = "limit", required = false, defaultValue = "5") Integer limit,
                                              @RequestParam(name = "sortBy", required = false, defaultValue = "modified_at") String sortBy,
                                              @RequestParam(name = "direction", required = false, defaultValue = "DESC") String direction,
                                              @RequestParam(name = "all", required = false, defaultValue = "false") boolean getAll) {
        if (authenticationService.checkPermission("READ_SONG")) {
            Object obj = songService.searchInPlaylist(keyword, id, page, limit, sortBy, direction, getAll);
            if (obj instanceof ObjectFailed)
                return ResponseEntity.status(((ObjectFailed) obj).getStatus()).body(((ObjectFailed) obj).getMessage());
            return ResponseEntity.ok(obj);
        }
        return ResponseEntity.status(403).body("You don't have permission to get song");
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody SongDTO songDTO) {
        if (authenticationService.checkPermission("CREATE_SONG")) {
            Song song = songService.create(songDTO);
            return ResponseEntity.ok(song);
        }
        return ResponseEntity.status(403).body("You don't have permission to create song");
    }

    @PutMapping("/add_favorite")
    public ResponseEntity<?> addSongToFavoriteList(@RequestBody Set<Integer> songs) {
        if (authenticationService.checkPermission("UPDATE_SONG")) {
            List<Song> song = songService.addSongToFavoriteList(songs);
            return ResponseEntity.ok(song);
        }
        return ResponseEntity.status(403).body("You don't have permission to add song in favorite list");
    }

    @PutMapping("/remove_favorite")
    public ResponseEntity<?> removeSongFromFavoriteList(@RequestBody Set<Integer> songs) {
        if (authenticationService.checkPermission("UPDATE_SONG")) {
            List<Song> song = songService.removeSongFromFavoriteList(songs);
            return ResponseEntity.ok(song);
        }
        return ResponseEntity.status(403).body("You don't have permission to remove song in favorite list");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody SongDTO songDTO) {
        if (authenticationService.checkPermission("UPDATE_SONG")) {
            Object obj = songService.update(id, songDTO);
            if (obj instanceof ObjectFailed)
                return ResponseEntity.status(((ObjectFailed) obj).getStatus()).body(((ObjectFailed) obj).getMessage());
            return ResponseEntity.ok(obj);
        }
        return ResponseEntity.status(403).body("You don't have permission to update song");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        if (authenticationService.checkPermission("DELETE_SONG")) {
            Song song = songService.delete(id);
            if (song == null)
                return ResponseEntity.status(404).body("Failed to delete song, not found song with id " + id);
            return ResponseEntity.ok().body("Delete " + song.getName() + " song successfully");
        }
        return ResponseEntity.status(403).body("You don't have permission to delete song");
    }
}
