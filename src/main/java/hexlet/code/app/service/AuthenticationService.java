package hexlet.code.app.service;

import hexlet.code.app.model.User;

import java.util.Optional;

public interface AuthenticationService {

    String login(String username, String password);

    Optional<User> findByToken(String token);
}
