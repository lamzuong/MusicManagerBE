package backend.musicmanager.tma.controller;

import backend.musicmanager.tma.exception.ObjectFailed;
import backend.musicmanager.tma.model.Genre;
import backend.musicmanager.tma.service.AuthenticationService;
import backend.musicmanager.tma.service.GenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/genre")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
public class GenreController {
    @Autowired
    private GenreService genreService;
    @Autowired
    private AuthenticationService authenticationService;

    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
                                    @RequestParam(name = "limit", required = false, defaultValue = "5") Integer limit,
                                    @RequestParam(name = "sortBy", required = false, defaultValue = "modified_at") String sortBy,
                                    @RequestParam(name = "direction", required = false, defaultValue = "ASC") String direction,
                                    @RequestParam(name = "all", required = false, defaultValue = "false") boolean getAll) {
        if (authenticationService.checkPermission("READ_GENRE"))
            return ResponseEntity.ok(genreService.findAll(page, limit, sortBy, direction, getAll));
        return ResponseEntity.status(403).body("You don't have permission to get genre");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id) {
        if (authenticationService.checkPermission("READ_GENRE")) {
            Object obj = genreService.findById(id);
            if (obj instanceof ObjectFailed)
                return ResponseEntity.status(((ObjectFailed) obj).getStatus()).body(((ObjectFailed) obj).getMessage());
            return ResponseEntity.ok(obj);
        }
        return ResponseEntity.status(403).body("You don't have permission to get genre");
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam(name = "keyword") String keyword,
                                    @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
                                    @RequestParam(name = "limit", required = false, defaultValue = "5") Integer limit,
                                    @RequestParam(name = "sortBy", required = false, defaultValue = "modified_at") String sortBy,
                                    @RequestParam(name = "direction", required = false, defaultValue = "ASC") String direction,
                                    @RequestParam(name = "all", required = false, defaultValue = "false") boolean getAll) {
        if (authenticationService.checkPermission("READ_GENRE")) {
            return ResponseEntity.ok(genreService.search(keyword, page, limit, sortBy, direction, getAll));
        }
        return ResponseEntity.status(403).body("You don't have permission to find genre");
    }

    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody Genre genre
    ) {
        if (authenticationService.checkPermission("CREATE_GENRE")) {
            Object obj = genreService.create(genre);
            if (obj instanceof ObjectFailed)
                return ResponseEntity.status(((ObjectFailed) obj).getStatus()).body(((ObjectFailed) obj).getMessage());
            return ResponseEntity.ok(obj);
        }
        return ResponseEntity.status(403).body("You don't have permission to create genre");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody Genre genre) {
        if (authenticationService.checkPermission("UPDATE_GENRE")) {
            Object obj = genreService.update(id, genre);
            if (obj instanceof ObjectFailed)
                return ResponseEntity.status(((ObjectFailed) obj).getStatus()).body(((ObjectFailed) obj).getMessage());
            return ResponseEntity.ok(obj);
        }
        return ResponseEntity.status(403).body("You don't have permission to update genre");
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        if (authenticationService.checkPermission("DELETE_GENRE")) {
            Genre genre = genreService.delete(id);
            if (genre == null)
                return ResponseEntity.status(404).body("Failed to delete genre, not found genre with id " + id);
            return ResponseEntity.ok().body("Delete " + genre.getName() + " genre successfully");
        }
        return ResponseEntity.status(403).body("You don't have permission to delete genre");
    }

}
