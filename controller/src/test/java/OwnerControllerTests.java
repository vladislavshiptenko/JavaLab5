import com.example.Main;
import com.example.dao.user.UserDao;
import com.example.dto.user.SignUpDto;
import com.example.entities.user.UserRole;
import com.example.services.auth.AuthenticationService;
import com.example.services.auth.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
@Transactional
public class OwnerControllerTests {
    private final MockMvc mockMvc;
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    @Autowired
    OwnerControllerTests(MockMvc mockMvc, AuthenticationService authenticationService, JwtService jwtService) {
        this.mockMvc = mockMvc;
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
    }

    @Test
    public void addOwnerTest() throws Exception {
        SignUpDto user1 = new SignUpDto();
        user1.setUsername("User1");
        user1.setEmail("user1@gmail.com");
        user1.setPassword("1234");
        user1.setRole("ROLE_USER");
        String jwtUser1 = authenticationService.signUp(user1);

        String newOwner = "{\"name\": \"Vlad\", \"birthday\": \"2004-10-13T00:00:00Z\", \"cats\": []}";

        String response = mockMvc.perform(post("/api/owners/addOwner")
                        .header("Authorization", "Bearer "+jwtUser1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(newOwner))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        long userIdFromOwner = objectMapper.readTree(response).get("userId").asLong();
        long userIdFromToken = jwtService.extractId(jwtUser1);

        assertEquals(userIdFromOwner, userIdFromToken);
    }

    @Test
    public void addManyOwnersByOneUserTest() throws Exception {
        SignUpDto user1 = new SignUpDto();
        user1.setUsername("User1");
        user1.setEmail("user1@gmail.com");
        user1.setPassword("1234");
        user1.setRole("ROLE_USER");
        String jwtUser1 = authenticationService.signUp(user1);

        String newOwner1 = "{\"name\": \"Vlad\", \"birthday\": \"2004-10-13T00:00:00Z\", \"cats\": []}";
        String newOwner2 = "{\"name\": \"Vlad\", \"birthday\": \"2004-10-13T00:00:00Z\", \"cats\": []}";

        mockMvc.perform(post("/api/owners/addOwner")
                        .header("Authorization", "Bearer "+jwtUser1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newOwner1))
                .andExpect(status().isOk());

        assertThrows(Exception.class, () ->
                mockMvc.perform(post("/api/owners/addOwner")
                        .header("Authorization", "Bearer "+jwtUser1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newOwner2))
        );
    }

    @Test
    public void getOwnersByNameTest() throws Exception {
        SignUpDto user1 = new SignUpDto();
        user1.setUsername("User1");
        user1.setEmail("user1@gmail.com");
        user1.setPassword("1234");
        user1.setRole("ROLE_USER");
        String jwtUser1 = authenticationService.signUp(user1);

        SignUpDto user2 = new SignUpDto();
        user2.setUsername("User2");
        user2.setEmail("user2@gmail.com");
        user2.setPassword("12345");
        user2.setRole("ROLE_USER");
        String jwtUser2 = authenticationService.signUp(user2);

        String newOwner1 = "{\"name\": \"Vlad\", \"birthday\": \"2004-10-13T00:00:00Z\", \"cats\": []}";
        String newOwner2 = "{\"name\": \"NeVlad\", \"birthday\": \"2007-10-13T00:00:00Z\", \"cats\": []}";

        mockMvc.perform(post("/api/owners/addOwner")
                        .header("Authorization", "Bearer "+jwtUser1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newOwner1))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/owners/addOwner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer "+jwtUser2)
                        .content(newOwner2))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/owners/getOwnersByName/Vlad")
                        .header("Authorization", "Bearer "+jwtUser2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));

        mockMvc.perform(get("/api/owners/getOwnersByName/Vlad")
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    public void updateOwnerTest() throws Exception {
        SignUpDto user1 = new SignUpDto();
        user1.setUsername("User1");
        user1.setEmail("user1@gmail.com");
        user1.setPassword("1234");
        user1.setRole("ROLE_USER");
        String jwtUser1 = authenticationService.signUp(user1);

        String newOwner = "{\"name\": \"Vlad\", \"birthday\": \"2004-10-13T00:00:00Z\", \"cats\": []}";

        String response = mockMvc.perform(post("/api/owners/addOwner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newOwner)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        long ownerId = objectMapper.readTree(response).get("id").asLong();
        String updatedOwner = "{\"id\": " + ownerId + ", \"name\": \"Dmitry\", \"birthday\": \"2004-10-13T00:00:00Z\", \"cats\": []}";

        mockMvc.perform(put("/api/owners/updateOwner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedOwner)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteOwnerTest() throws Exception {
        SignUpDto user1 = new SignUpDto();
        user1.setUsername("User1");
        user1.setEmail("user1@gmail.com");
        user1.setPassword("1234");
        user1.setRole("ROLE_USER");
        String jwtUser1 = authenticationService.signUp(user1);

        String newOwner = "{\"name\": \"Vlad\", \"birthday\": \"2004-10-13T00:00:00Z\", \"cats\": []}";

        String response = mockMvc.perform(post("/api/owners/addOwner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newOwner)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        long ownerId = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/api/owners/deleteOwner/" + ownerId)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk());
    }

    @Test
    public void getOwnersByNameWithoutAuthorizationTest() throws Exception {
        mockMvc.perform(get("/api/owners/getOwnersByName/Vlad"))
                .andExpect(status().is(403));
    }

    @Test
    public void addOwnerWithoutAuthorizationTest() throws Exception {
        String newOwner = "{\"name\": \"Vlad\", \"birthday\": \"2004-10-13T00:00:00Z\", \"cats\": []}";

        mockMvc.perform(post("/api/owners/addOwner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newOwner))
                .andExpect(status().is(403));
    }

    @Test
    public void updateOwnerWithoutAuthorizationTest() throws Exception {
        SignUpDto user1 = new SignUpDto();
        user1.setUsername("User1");
        user1.setEmail("user1@gmail.com");
        user1.setPassword("1234");
        user1.setRole("ROLE_USER");
        String jwtUser1 = authenticationService.signUp(user1);

        String newOwner = "{\"name\": \"Vlad\", \"birthday\": \"2004-10-13T00:00:00Z\", \"cats\": []}";

        String response = mockMvc.perform(post("/api/owners/addOwner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newOwner)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        long ownerId = objectMapper.readTree(response).get("id").asLong();
        String updatedOwner = "{\"id\": " + ownerId + ", \"name\": \"Dmitry\", \"birthday\": \"2004-10-13T00:00:00Z\", \"cats\": []}";

        mockMvc.perform(put("/api/owners/updateOwner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedOwner))
                .andExpect(status().is(403));
    }

    @Test
    public void deleteOwnerWithoutAuthorizationTest() throws Exception {
        SignUpDto user1 = new SignUpDto();
        user1.setUsername("User1");
        user1.setEmail("user1@gmail.com");
        user1.setPassword("1234");
        user1.setRole("ROLE_USER");
        String jwtUser1 = authenticationService.signUp(user1);

        String newOwner = "{\"name\": \"Vlad\", \"birthday\": \"2004-10-13T00:00:00Z\", \"cats\": []}";

        String response = mockMvc.perform(post("/api/owners/addOwner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newOwner)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        long ownerId = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/api/owners/deleteOwner/" + ownerId))
                .andExpect(status().is(403));
    }

    @Test
    public void updateOwnerWithWrongUserTest() throws Exception {
        SignUpDto user1 = new SignUpDto();
        user1.setUsername("User1");
        user1.setEmail("user1@gmail.com");
        user1.setPassword("1234");
        user1.setRole("ROLE_USER");
        String jwtUser1 = authenticationService.signUp(user1);

        SignUpDto user2 = new SignUpDto();
        user2.setUsername("User2");
        user2.setEmail("user2@gmail.com");
        user2.setPassword("12345");
        user2.setRole("ROLE_USER");
        String jwtUser2 = authenticationService.signUp(user2);

        String newOwner = "{\"name\": \"Vlad\", \"birthday\": \"2004-10-13T00:00:00Z\", \"cats\": []}";

        String response = mockMvc.perform(post("/api/owners/addOwner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newOwner)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        long ownerId = objectMapper.readTree(response).get("id").asLong();
        String updatedOwner = "{\"id\": " + ownerId + ", \"name\": \"Dmitry\", \"birthday\": \"2004-10-13T00:00:00Z\", \"cats\": []}";

        mockMvc.perform(put("/api/owners/updateOwner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedOwner)
                        .header("Authorization", "Bearer "+jwtUser2))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deleteOwnerWithWrongUserTest() throws Exception {
        SignUpDto user1 = new SignUpDto();
        user1.setUsername("User1");
        user1.setEmail("user1@gmail.com");
        user1.setPassword("1234");
        user1.setRole("ROLE_USER");
        String jwtUser1 = authenticationService.signUp(user1);

        SignUpDto user2 = new SignUpDto();
        user2.setUsername("User2");
        user2.setEmail("user2@gmail.com");
        user2.setPassword("12345");
        user2.setRole("ROLE_USER");
        String jwtUser2 = authenticationService.signUp(user2);

        String newOwner = "{\"name\": \"Vlad\", \"birthday\": \"2004-10-13T00:00:00Z\", \"cats\": []}";

        String response = mockMvc.perform(post("/api/owners/addOwner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newOwner)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        long ownerId = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/api/owners/deleteOwner/" + ownerId)
                        .header("Authorization", "Bearer "+jwtUser2))
                .andExpect(status().isForbidden());
    }
}
