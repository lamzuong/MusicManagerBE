package backend.musicmanager.tma.controller;

import backend.musicmanager.tma.dto.PlaylistDTO;
import backend.musicmanager.tma.exception.ObjectFailed;
import backend.musicmanager.tma.service.AuthenticationService;
import backend.musicmanager.tma.service.PlaylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/playlist")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
public class PlaylistController {
    @Autowired
    private PlaylistService playlistService;
    @Autowired
    private AuthenticationService authenticationService;

    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
                                    @RequestParam(name = "limit", required = false, defaultValue = "5") Integer limit,
                                    @RequestParam(name = "sortBy", required = false, defaultValue = "modified_at") String sortBy,
                                    @RequestParam(name = "direction", required = false, defaultValue = "DESC") String direction,
                                    @RequestParam(name = "all", required = false, defaultValue = "false") boolean getAll) {
        if (authenticationService.checkPermission("READ_PLAYLIST"))
            return ResponseEntity.ok(playlistService.findAll(page, limit, sortBy, direction, getAll));
        return ResponseEntity.status(403).body("You don't have permission to get playlist");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id) {
        if (authenticationService.checkPermission("READ_PLAYLIST")) {
            Object obj = playlistService.findById(id);
            if (obj instanceof ObjectFailed)
                return ResponseEntity.status(((ObjectFailed) obj).getStatus()).body(((ObjectFailed) obj).getMessage());
            return ResponseEntity.ok(obj);
        }
        return ResponseEntity.status(403).body("You don't have permission to get playlist");
    }

    @GetMapping("/user")
    public ResponseEntity<?> getAllPlaylistByUserId(@RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
                                                    @RequestParam(name = "limit", required = false, defaultValue = "5") Integer limit,
                                                    @RequestParam(name = "sortBy", required = false, defaultValue = "modified_at") String sortBy,
                                                    @RequestParam(name = "direction", required = false, defaultValue = "DESC") String direction,
                                                    @RequestParam(name = "all", required = false, defaultValue = "false") boolean getAll) {
        if (authenticationService.checkPermission("READ_PLAYLIST")) {
            Object obj = playlistService.findByUserId(page, limit, sortBy, direction, getAll);
            if (obj instanceof ObjectFailed)
                return ResponseEntity.status(((ObjectFailed) obj).getStatus()).body(((ObjectFailed) obj).getMessage());
            return ResponseEntity.ok(obj);
        }
        return ResponseEntity.status(403).body("You don't have permission to get playlist");
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody PlaylistDTO playlistDTO) {
        if (authenticationService.checkPermission("CREATE_PLAYLIST")) {
            return ResponseEntity.ok(playlistService.create(playlistDTO));
        }
        return ResponseEntity.status(403).body("You don't have permission to create playlist");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody PlaylistDTO playlistDTO) {
        if (authenticationService.checkPermission("UPDATE_PLAYLIST")) {
            Object obj = playlistService.update(id, playlistDTO);
            if (obj instanceof ObjectFailed)
                return ResponseEntity.status(((ObjectFailed) obj).getStatus()).body(((ObjectFailed) obj).getMessage());
            return ResponseEntity.ok(obj);
        }
        return ResponseEntity.status(403).body("You don't have permission to update playlist");
    }

    @PutMapping("/add_song/{id}")
    public ResponseEntity<?> addSong(@PathVariable int id, @RequestBody Set<Integer> songs) {
        if (authenticationService.checkPermission("UPDATE_PLAYLIST")) {
            Object obj = playlistService.addSong(id, songs);
            if (obj instanceof ObjectFailed)
                return ResponseEntity.status(((ObjectFailed) obj).getStatus()).body(((ObjectFailed) obj).getMessage());
            return ResponseEntity.ok(obj);
        }
        return ResponseEntity.status(403).body("You don't have permission to update playlist");
    }

    @PutMapping("/remove_song/{id}")
    public ResponseEntity<?> removeSong(@PathVariable int id, @RequestBody Set<Integer> songs) {
        if (authenticationService.checkPermission("UPDATE_PLAYLIST")) {
            Object obj = playlistService.removeSong(id, songs);
            if (obj instanceof ObjectFailed)
                return ResponseEntity.status(((ObjectFailed) obj).getStatus()).body(((ObjectFailed) obj).getMessage());
            return ResponseEntity.ok(obj);
        }
        return ResponseEntity.status(403).body("You don't have permission to update playlist");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        if (authenticationService.checkPermission("DELETE_PLAYLIST")) {
            Object obj = playlistService.delete(id);
            if (obj instanceof ObjectFailed)
                return ResponseEntity.status(((ObjectFailed) obj).getStatus()).body(((ObjectFailed) obj).getMessage());
            return ResponseEntity.ok().body("Delete success playlist with id " + id);
        }
        return ResponseEntity.status(403).body("You don't have permission to delete playlist");
    }
}
