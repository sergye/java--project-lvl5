package hexlet.code.app.service;

import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;


@Service
@AllArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;

    private final TokenService tokenService;

    private final PasswordEncoder passwordEncoder;

    @Override
    public String login(String username, String password) {
        return userRepository.findByEmail(username)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .map(user -> tokenService.getToken(Map.of("username", username)))
                .orElseThrow(() -> new UsernameNotFoundException("invalid login and/or password"));
    }

    @Override
    public Optional<User> findByToken(String token) {
        return userRepository.findByEmail(tokenService.parse(token).get("username").toString());
    }
}
