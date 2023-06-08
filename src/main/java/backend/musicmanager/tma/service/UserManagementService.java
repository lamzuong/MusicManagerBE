package backend.musicmanager.tma.service;

import backend.musicmanager.tma.dto.UserDTO;
import backend.musicmanager.tma.exception.ObjectFailed;
import backend.musicmanager.tma.model.User;
import backend.musicmanager.tma.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserManagementService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthenticationService authenticationService;

    public Object getAllUser(int page, int limit, String sortBy, String direction, boolean getAll) {
        Sort sort = Sort.by(sortBy);
        PageRequest pageRequest;
        if (!getAll) {
            pageRequest = PageRequest.of(page - 1, limit, direction.equals("ASC") ? sort.ascending() : sort.descending());
        } else {
            pageRequest = PageRequest.of(0, Integer.MAX_VALUE, direction.equals("ASC") ? sort.ascending() : sort.descending());
        }
        int countUser = userRepository.countAllUser();
        List<User> listUser = userRepository.findAllUser(pageRequest);

        Map<String, Object> result = new HashMap<>();
        result.put("data", listUser);
        result.put("totalRows", countUser);
        return result;
    }

    public Object getUserById(int id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) return ObjectFailed.builder().message("Not found user with id " + id).status(404).build();
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setBlocked(user.isBlocked());
        userDTO.setGender(user.getGender());
        userDTO.setBirthday(user.getBirthday());
        userDTO.setAvatar(user.getAvatar());
        userDTO.setRole(user.getRole().getName());
        return userDTO;
    }

    public Object update(int id, User newUser) {
        User user = userRepository.findById(id).orElse(null);
        User currentUser = authenticationService.getCurrentUser();
        if (user != null && user.getId() != currentUser.getId() && !currentUser.getRole().getName().equals("ADMIN"))
            return ObjectFailed.builder().status(403).message("This id not of current user").build();
        if (user == null) return ObjectFailed.builder().message("Not found user with id " + id).status(404).build();
        user.setName(newUser.getName());
        user.setGender(newUser.getGender());
        user.setAvatar(newUser.getAvatar());
        user.setBirthday(newUser.getBirthday());
        return userRepository.save(user);
    }

    public Object block(int id) {
        User currentUser = authenticationService.getCurrentUser();
        if (id == currentUser.getId())
            return ObjectFailed.builder().message("Can't block/unblock yourself").status(400).build();
        User user = userRepository.findById(id).orElse(null);
        if (user == null) return ObjectFailed.builder().message("Not found user with id " + id).status(404).build();
        user.setBlocked(!user.isBlocked());
        return userRepository.save(user);
    }
}
