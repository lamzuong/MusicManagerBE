package backend.musicmanager.tma.controller;

import backend.musicmanager.tma.exception.ObjectFailed;
import backend.musicmanager.tma.model.Artist;
import backend.musicmanager.tma.service.ArtistService;
import backend.musicmanager.tma.service.AuthenticationService;
import backend.musicmanager.tma.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artist")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class ArtistController {
    @Autowired
    private ArtistService artistService;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private AuthenticationService authenticationService;

    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
                                    @RequestParam(name = "limit", required = false, defaultValue = "5") Integer limit,
                                    @RequestParam(name = "sortBy", required = false, defaultValue = "modified_at") String sortBy,
                                    @RequestParam(name = "direction", required = false, defaultValue = "ASC") String direction,
                                    @RequestParam(name = "all", required = false, defaultValue = "false") boolean getAll) {
        if (authenticationService.checkPermission("READ_ARTIST"))
            return ResponseEntity.ok(artistService.findAll(page, limit, sortBy, direction, getAll));
        return ResponseEntity.status(403).body("You don't have permission to get artist");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id) {
        if (authenticationService.checkPermission("READ_ARTIST")) {
            Object obj = artistService.findById(id);
            if (obj instanceof ObjectFailed)
                return ResponseEntity.status(((ObjectFailed) obj).getStatus()).body(((ObjectFailed) obj).getMessage());
            return ResponseEntity.ok(obj);
        }
        return ResponseEntity.status(403).body("You don't have permission to find artist");
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam(name = "keyword") String keyword,
                                    @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
                                    @RequestParam(name = "limit", required = false, defaultValue = "5") Integer limit,
                                    @RequestParam(name = "sortBy", required = false, defaultValue = "modified_at") String sortBy,
                                    @RequestParam(name = "direction", required = false, defaultValue = "ASC") String direction,
                                    @RequestParam(name = "all", required = false, defaultValue = "false") boolean getAll) {
        if (authenticationService.checkPermission("READ_ARTIST")) {
            return ResponseEntity.ok(artistService.search(keyword, page, limit, sortBy, direction, getAll));
        }
        return ResponseEntity.status(403).body("You don't have permission to find artist");
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Artist artist) {
        if (authenticationService.checkPermission("CREATE_ARTIST")) {
            Object obj = artistService.save(artist);
            if (obj instanceof ObjectFailed)
                return ResponseEntity.status(((ObjectFailed) obj).getStatus()).body(((ObjectFailed) obj).getMessage());
            return ResponseEntity.ok(obj);
        }
        return ResponseEntity.status(403).body("You don't have permission to create artist");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody Artist artist) {
        if (authenticationService.checkPermission("UPDATE_ARTIST")) {
            Object obj = artistService.update(id, artist);
            if (obj instanceof ObjectFailed)
                return ResponseEntity.status(((ObjectFailed) obj).getStatus()).body(((ObjectFailed) obj).getMessage());
            return ResponseEntity.ok(obj);
        }
        return ResponseEntity.status(403).body("You don't have permission to update artist");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        if (authenticationService.checkPermission("DELETE_ARTIST")) {
            Artist artist = artistService.deleteById(id);
            if (artist == null)
                return ResponseEntity.status(404).body("Failed to delete artist, not found artist with id " + id);
            return ResponseEntity.ok().body("Delete " + artist.getName() + " successfully");
        }
        return ResponseEntity.status(403).body("You don't have permission to delete artist");
    }

    @DeleteMapping("/by_name/{name}")
    public ResponseEntity<?> delete(@PathVariable String name) {
        if (authenticationService.checkPermission("DELETE_ARTIST")) {
            Artist artist = artistService.deleteByName(name);
            if (artist == null)
                return ResponseEntity.status(404).body("Failed to delete artist, not found artist with name " + name);
            return ResponseEntity.ok().body("Delete " + artist.getName() + " successfully");
        }
        return ResponseEntity.status(403).body("You don't have permission to delete artist");
    }

}
