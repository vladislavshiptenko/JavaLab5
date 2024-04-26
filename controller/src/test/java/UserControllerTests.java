import com.example.Main;
import com.example.dto.user.SignUpDto;
import com.example.services.auth.AuthenticationService;
import com.example.services.auth.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
@Transactional
public class UserControllerTests {

    private final MockMvc mockMvc;
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    @Autowired
    UserControllerTests(MockMvc mockMvc, AuthenticationService authenticationService, JwtService jwtService) {
        this.mockMvc = mockMvc;
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
    }

    @Test
    public void addUserTest() throws Exception {
        SignUpDto user1 = new SignUpDto();
        user1.setUsername("User1");
        user1.setEmail("user1@gmail.com");
        user1.setPassword("1234");
        user1.setRole("ROLE_ADMIN");
        String jwtUser1 = authenticationService.signUp(user1);

        String newUser = "{\"username\": \"Vlad\", \"email\": \"example@gmail.com\", \"password\": \"password\", \"role\": \"ROLE_ADMIN\"}";

        String token = mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUser)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(jwtService.extractUsername(token), "Vlad");
    }

    @Test
    public void addUserWithNoAdminTest() throws Exception {
        SignUpDto user1 = new SignUpDto();
        user1.setUsername("User1");
        user1.setEmail("user1@gmail.com");
        user1.setPassword("1234");
        user1.setRole("ROLE_USER");
        String jwtUser1 = authenticationService.signUp(user1);

        String newUser = "{\"username\": \"Vlad\", \"email\": \"example@gmail.com\", \"password\": \"password\", \"role\": \"ROLE_ADMIN\"}";

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUser)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isForbidden());
    }

    @Test
    public void addUserWithoutAuthorizationTest() throws Exception {
        String newUser = "{\"username\": \"Vlad\", \"email\": \"example@gmail.com\", \"password\": \"password\", \"role\": \"ROLE_ADMIN\"}";

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUser))
                .andExpect(status().is(403));
    }

    @Test
    public void addUserWithoutUsernameTest() throws Exception {
        SignUpDto user1 = new SignUpDto();
        user1.setUsername("User1");
        user1.setEmail("user1@gmail.com");
        user1.setPassword("1234");
        user1.setRole("ROLE_ADMIN");
        String jwtUser1 = authenticationService.signUp(user1);

        String newUser = "{\"email\": \"example@gmail.com\", \"password\": \"password\", \"role\": \"ROLE_ADMIN\"}";

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUser)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addUserWithoutPasswordTest() throws Exception {
        SignUpDto user1 = new SignUpDto();
        user1.setUsername("User1");
        user1.setEmail("user1@gmail.com");
        user1.setPassword("1234");
        user1.setRole("ROLE_ADMIN");
        String jwtUser1 = authenticationService.signUp(user1);

        String newUser = "{\"username\": \"Vlad\", \"email\": \"example@gmail.com\", \"role\": \"ROLE_ADMIN\"}";

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUser)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addUserWithIncorrectEmailTest() throws Exception {
        SignUpDto user1 = new SignUpDto();
        user1.setUsername("User1");
        user1.setEmail("user1@gmail.com");
        user1.setPassword("1234");
        user1.setRole("ROLE_ADMIN");
        String jwtUser1 = authenticationService.signUp(user1);

        String newUser = "{\"username\": \"Vlad\", \"email\": \"example\", \"password\": \"password\", \"role\": \"ROLE_ADMIN\"}";

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUser)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addUserWithIncorrectRoleTest() throws Exception {
        SignUpDto user1 = new SignUpDto();
        user1.setUsername("User1");
        user1.setEmail("user1@gmail.com");
        user1.setPassword("1234");
        user1.setRole("ROLE_ADMIN");
        String jwtUser1 = authenticationService.signUp(user1);

        String newUser = "{\"username\": \"Vlad\", \"email\": \"example@gmail.com\", \"password\": \"password\", \"role\": \"ROLE_WRONG\"}";

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUser)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addUsersWithSameUsernameTest() throws Exception {
        SignUpDto user1 = new SignUpDto();
        user1.setUsername("User1");
        user1.setEmail("user1@gmail.com");
        user1.setPassword("1234");
        user1.setRole("ROLE_ADMIN");
        String jwtUser1 = authenticationService.signUp(user1);

        String newUser1 = "{\"username\": \"Vlad\", \"email\": \"example1@gmail.com\", \"password\": \"password\", \"role\": \"ROLE_ADMIN\"}";
        String newUser2 = "{\"username\": \"Vlad\", \"email\": \"example2@gmail.com\", \"password\": \"password\", \"role\": \"ROLE_ADMIN\"}";

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUser1)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newUser2)
                .header("Authorization", "Bearer "+jwtUser1))
        .andExpect(status().isBadRequest());
    }

    @Test
    public void addUsersWithSameEmailTest() throws Exception {
        SignUpDto user1 = new SignUpDto();
        user1.setUsername("User1");
        user1.setEmail("user1@gmail.com");
        user1.setPassword("1234");
        user1.setRole("ROLE_ADMIN");
        String jwtUser1 = authenticationService.signUp(user1);

        String newUser1 = "{\"username\": \"Vlad\", \"email\": \"example@gmail.com\", \"password\": \"password\", \"role\": \"ROLE_ADMIN\"}";
        String newUser2 = "{\"username\": \"NeVlad\", \"email\": \"example@gmail.com\", \"password\": \"password\", \"role\": \"ROLE_ADMIN\"}";

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUser1)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUser2)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getUserTest() throws Exception {
        SignUpDto user1 = new SignUpDto();
        user1.setUsername("User1");
        user1.setEmail("user1@gmail.com");
        user1.setPassword("1234");
        user1.setRole("ROLE_ADMIN");
        String jwtUser1 = authenticationService.signUp(user1);

        String newUser = "{\"username\": \"Vlad\", \"email\": \"example@gmail.com\", \"password\": \"password\", \"role\": \"ROLE_ADMIN\"}";

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUser)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk());

        String authUser = "{\"username\": \"Vlad\", \"password\": \"password\"}";

        String token = mockMvc.perform(post("/api/users/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authUser))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(jwtService.extractUsername(token), "Vlad");
    }

    @Test
    public void getUserWithoutUsernameTest() throws Exception {
        SignUpDto user1 = new SignUpDto();
        user1.setUsername("User1");
        user1.setEmail("user1@gmail.com");
        user1.setPassword("1234");
        user1.setRole("ROLE_ADMIN");
        String jwtUser1 = authenticationService.signUp(user1);

        String newUser = "{\"username\": \"Vlad\", \"email\": \"example@gmail.com\", \"password\": \"password\", \"role\": \"ROLE_ADMIN\"}";

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUser)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk());

        String authUser = "{\"password\": \"password\"}";

        mockMvc.perform(post("/api/users/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authUser))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getUserWithoutPasswordTest() throws Exception {
        SignUpDto user1 = new SignUpDto();
        user1.setUsername("User1");
        user1.setEmail("user1@gmail.com");
        user1.setPassword("1234");
        user1.setRole("ROLE_ADMIN");
        String jwtUser1 = authenticationService.signUp(user1);

        String newUser = "{\"username\": \"Vlad\", \"email\": \"example@gmail.com\", \"password\": \"password\", \"role\": \"ROLE_ADMIN\"}";

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUser)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk());

        String authUser = "{\"username\": \"Vlad\"}";

        mockMvc.perform(post("/api/users/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authUser))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getUserWithWrongCredentialsTest() throws Exception {
        SignUpDto user1 = new SignUpDto();
        user1.setUsername("User1");
        user1.setEmail("user1@gmail.com");
        user1.setPassword("1234");
        user1.setRole("ROLE_ADMIN");
        String jwtUser1 = authenticationService.signUp(user1);

        String newUser = "{\"username\": \"Vlad\", \"email\": \"example@gmail.com\", \"password\": \"password\", \"role\": \"ROLE_ADMIN\"}";

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUser)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk());

        String authUser = "{\"username\": \"Oleg\", \"password\": \"password\"}";

        mockMvc.perform(post("/api/users/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authUser))
                .andExpect(status().is(403));
    }
}
