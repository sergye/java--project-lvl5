package hexlet.code.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.api.DBRider;
import hexlet.code.app.dto.UserDto;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DBRider
@DataSet("users.yml")
public class UserControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestUtils utils;

    private final UserDto newUser = new UserDto("vladimir@gmail.com", "Vladimir", "Kulikov", "qazwsx");

    private static final String BASE_URL = "/api/users";

    @Test
    void getUsers() throws Exception {
        MockHttpServletResponse response = utils
                .perform(get(BASE_URL))
                .andReturn()
                .getResponse();

        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals(MediaType.APPLICATION_JSON.toString(), response.getContentType());
        Assertions.assertTrue(response.getContentAsString().contains("igor@gmail.com"));
        Assertions.assertTrue(response.getContentAsString().contains("alex@gmail.com"));
        Assertions.assertTrue(response.getContentAsString().contains("gena@gmail.com"));
        Assertions.assertFalse(response.getContentAsString().contains("password"));
    }

    @Test
    void createUser() throws Exception {
        Assertions.assertEquals(3, userRepository.count());
        utils.regUser(newUser).andExpect(status().isCreated());
        Assertions.assertEquals(4, userRepository.count());

        MockHttpServletResponse response = utils.perform(get(BASE_URL))
                .andReturn()
                .getResponse();

        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals(MediaType.APPLICATION_JSON.toString(), response.getContentType());
        Assertions.assertTrue(response.getContentAsString().contains("vladimir@gmail.com"));

    }

    @Test
    void updateUser() throws Exception {
        final MockHttpServletRequestBuilder request = put(BASE_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(newUser));

        utils.perform(request, userRepository.findById(1L).get().getEmail()).andExpect(status().isOk());

        MockHttpServletResponse response = utils
                .perform(get(BASE_URL))
                .andReturn()
                .getResponse();


        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals(MediaType.APPLICATION_JSON.toString(), response.getContentType());
        Assertions.assertTrue(response.getContentAsString().contains("Vladimir"));
        Assertions.assertFalse(response.getContentAsString().contains("Igor"));
    }

    @Test
    void deleteUser() throws Exception {
        utils.regUser(newUser);
        Long userId = userRepository
                .findByEmail(newUser.getEmail())
                .get()
                .getId();

        utils.perform(delete(BASE_URL + "/" + userId), newUser.getEmail()).andExpect(status().isOk());

        MockHttpServletResponse response = utils
                .perform(get(BASE_URL))
                .andReturn()
                .getResponse();

        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals(MediaType.APPLICATION_JSON.toString(), response.getContentType());
        Assertions.assertEquals(3, userRepository.count());
    }

    @Test
    void validateUserData() throws Exception {
        Assertions.assertEquals(3, userRepository.count());
        utils.regUser(new UserDto("g#%23", "", "?j", "&")).andExpect(status().isUnprocessableEntity());
        Assertions.assertEquals(3, userRepository.count());
    }

    @Test
    void createUserWithSameData() throws Exception {
        Assertions.assertEquals(3, userRepository.count());
        utils.regUser(newUser).andExpect(status().isCreated());
        utils.regUser(newUser).andExpect(status().isBadRequest());
        Assertions.assertEquals(4, userRepository.count());

    }
}
