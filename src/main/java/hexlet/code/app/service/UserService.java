package hexlet.code.app.service;

import hexlet.code.app.dto.UserDto;
import hexlet.code.app.model.User;

public interface UserService {
    User createUser(UserDto registrationData);
    User updateUser(Long id, UserDto newData);
}
