package hexlet.code.app.controller;

import hexlet.code.app.dto.UserDto;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.service.AuthenticationService;
import hexlet.code.app.service.UserServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;

import static hexlet.code.app.controller.UserController.USERS_URL;

@AllArgsConstructor
@RestController
@RequestMapping("{$base-url}" + USERS_URL)
public class UserController {

    public static final String USERS_URL = "/users";

    private static final String ID_CHECK = "@userRepository.findById(#id).get().getEmail() == authentication.getName()";

    private final UserServiceImpl userService;

    private final UserRepository userRepository;

    private final AuthenticationService authenticationService;

    @GetMapping
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable final Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No users with such id"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String createUser(@RequestBody @Valid UserDto dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new DuplicateKeyException("User with such email already exist");
        }
        userService.createUser(dto);
        return authenticationService.login(dto.getEmail(), dto.getPassword());
    }

    @PutMapping("/{id}")
    @PreAuthorize(ID_CHECK)
    public User update(@PathVariable final Long id, @RequestBody @Valid final UserDto dto) {
        final User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No users with such id"));

        return userService.updateUser(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(ID_CHECK)
    public void delete(@PathVariable final Long id) {
        userRepository.deleteById(id);
    }
}
