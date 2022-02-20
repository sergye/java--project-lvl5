package hexlet.code.app.controller;

import hexlet.code.app.dto.LoginDto;
import hexlet.code.app.service.AuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static hexlet.code.app.controller.AuthController.LOGIN_URL;

@AllArgsConstructor
@RestController
@RequestMapping("${base-url}" + LOGIN_URL)
public class AuthController {

    public static final String LOGIN_URL = "/login";

    private final AuthenticationService authenticationService;

    @PostMapping
    public String login(@RequestBody final LoginDto loginDto) {
        return authenticationService.login(loginDto.getUsername(), loginDto.getPassword());
    }
}
