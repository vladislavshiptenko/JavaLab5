import com.example.Main;
import com.example.dto.user.SignUpDto;
import com.example.services.auth.AuthenticationService;
import com.example.services.auth.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
@Transactional
public class CatControllerTests {

    private final MockMvc mockMvc;
    private final AuthenticationService authenticationService;

    @Autowired
    CatControllerTests(MockMvc mockMvc, AuthenticationService authenticationService) {
        this.mockMvc = mockMvc;
        this.authenticationService = authenticationService;
    }

    @Test
    public void addCatTest() throws Exception {
        SignUpDto user1 = new SignUpDto();
        user1.setUsername("User1");
        user1.setEmail("user1@gmail.com");
        user1.setPassword("1234");
        user1.setRole("ROLE_USER");
        String jwtUser1 = authenticationService.signUp(user1);

        String newOwner = "{\"name\": \"Vlad\", \"birthday\": \"2004-10-13T00:00:00Z\", \"cats\": []}";

        String responseOwner = mockMvc.perform(post("/api/owners/addOwner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newOwner)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        long ownerId = objectMapper.readTree(responseOwner).get("id").asLong();

        String newCat = "{\"name\": \"Vasya\", \"breed\": \"breed1\", \"color\": \"black\", \"owner\": " +
                ownerId + ", \"friends\": []}";

        mockMvc.perform(post("/api/cats/addCat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newCat)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk());
    }

    @Test
    public void addCatWithoutAuthorizationTest() throws Exception {
        SignUpDto user1 = new SignUpDto();
        user1.setUsername("User1");
        user1.setEmail("user1@gmail.com");
        user1.setPassword("1234");
        user1.setRole("ROLE_USER");
        String jwtUser1 = authenticationService.signUp(user1);

        String newOwner = "{\"name\": \"Vlad\", \"birthday\": \"2004-10-13T00:00:00Z\", \"cats\": []}";

        String responseOwner = mockMvc.perform(post("/api/owners/addOwner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newOwner)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        long ownerId = objectMapper.readTree(responseOwner).get("id").asLong();

        String newCat = "{\"name\": \"Vasya\", \"breed\": \"breed1\", \"color\": \"black\", \"owner\": " +
                ownerId + ", \"friends\": []}";

        mockMvc.perform(post("/api/cats/addCat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newCat))
                .andExpect(status().is(403));
    }

    @Test
    public void addCatWithWrongUserTest() throws Exception {
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

        String responseOwner = mockMvc.perform(post("/api/owners/addOwner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newOwner)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        long ownerId = objectMapper.readTree(responseOwner).get("id").asLong();

        String newCat = "{\"name\": \"Vasya\", \"breed\": \"breed1\", \"color\": \"black\", \"owner\": " +
                ownerId + ", \"friends\": []}";

        mockMvc.perform(post("/api/cats/addCat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newCat)
                        .header("Authorization", "Bearer "+jwtUser2))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deleteCatTest() throws Exception {
        SignUpDto user1 = new SignUpDto();
        user1.setUsername("User1");
        user1.setEmail("user1@gmail.com");
        user1.setPassword("1234");
        user1.setRole("ROLE_USER");
        String jwtUser1 = authenticationService.signUp(user1);

        String newOwner = "{\"name\": \"Vlad\", \"birthday\": \"2004-10-13T00:00:00Z\", \"cats\": []}";

        String responseOwner = mockMvc.perform(post("/api/owners/addOwner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newOwner)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        long ownerId = objectMapper.readTree(responseOwner).get("id").asLong();

        String newCat = "{\"name\": \"Vasya\", \"breed\": \"breed1\", \"color\": \"black\", \"owner\": " +
                ownerId + ", \"friends\": []}";

        String responseCat = mockMvc.perform(post("/api/cats/addCat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newCat)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long catId = objectMapper.readTree(responseCat).get("id").asLong();
        mockMvc.perform(delete("/api/cats/deleteCat/" + catId)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteCatWithoutAuthorizationTest() throws Exception {
        SignUpDto user1 = new SignUpDto();
        user1.setUsername("User1");
        user1.setEmail("user1@gmail.com");
        user1.setPassword("1234");
        user1.setRole("ROLE_USER");
        String jwtUser1 = authenticationService.signUp(user1);

        String newOwner = "{\"name\": \"Vlad\", \"birthday\": \"2004-10-13T00:00:00Z\", \"cats\": []}";

        String responseOwner = mockMvc.perform(post("/api/owners/addOwner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newOwner)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        long ownerId = objectMapper.readTree(responseOwner).get("id").asLong();

        String newCat = "{\"name\": \"Vasya\", \"breed\": \"breed1\", \"color\": \"black\", \"owner\": " +
                ownerId + ", \"friends\": []}";

        String responseCat = mockMvc.perform(post("/api/cats/addCat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newCat)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long catId = objectMapper.readTree(responseCat).get("id").asLong();
        mockMvc.perform(delete("/api/cats/deleteCat/" + catId))
                .andExpect(status().is(403));
    }

    @Test
    public void deleteCatWithWrongUserTest() throws Exception {
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

        String responseOwner = mockMvc.perform(post("/api/owners/addOwner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newOwner)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        long ownerId = objectMapper.readTree(responseOwner).get("id").asLong();

        String newCat = "{\"name\": \"Vasya\", \"breed\": \"breed1\", \"color\": \"black\", \"owner\": " +
                ownerId + ", \"friends\": []}";

        String responseCat = mockMvc.perform(post("/api/cats/addCat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newCat)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long catId = objectMapper.readTree(responseCat).get("id").asLong();
        mockMvc.perform(delete("/api/cats/deleteCat/" + catId)
                        .header("Authorization", "Bearer "+jwtUser2))
                .andExpect(status().isForbidden());
    }

    @Test
    public void updateCatTest() throws Exception {
        SignUpDto user1 = new SignUpDto();
        user1.setUsername("User1");
        user1.setEmail("user1@gmail.com");
        user1.setPassword("1234");
        user1.setRole("ROLE_USER");
        String jwtUser1 = authenticationService.signUp(user1);

        String newOwner = "{\"name\": \"Vlad\", \"birthday\": \"2004-10-13T00:00:00Z\", \"cats\": []}";

        String responseOwner = mockMvc.perform(post("/api/owners/addOwner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newOwner)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        long ownerId = objectMapper.readTree(responseOwner).get("id").asLong();

        String newCat = "{\"name\": \"Vasya\", \"breed\": \"breed1\", \"color\": \"black\", \"owner\": " +
                ownerId + ", \"friends\": []}";

        String responseCat = mockMvc.perform(post("/api/cats/addCat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newCat)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long catId = objectMapper.readTree(responseCat).get("id").asLong();

        String updatedCat = "{\"id\": " + catId + ", \"name\": \"AnotherCatName\", \"breed\": \"breed1\", \"color\": \"black\", \"owner\": " +
                ownerId + ", \"friends\": []}";

        mockMvc.perform(put("/api/cats/updateCat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedCat)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk());
    }

    @Test
    public void updateCatWithoutAuthorizationTest() throws Exception {
        SignUpDto user1 = new SignUpDto();
        user1.setUsername("User1");
        user1.setEmail("user1@gmail.com");
        user1.setPassword("1234");
        user1.setRole("ROLE_USER");
        String jwtUser1 = authenticationService.signUp(user1);

        String newOwner = "{\"name\": \"Vlad\", \"birthday\": \"2004-10-13T00:00:00Z\", \"cats\": []}";

        String responseOwner = mockMvc.perform(post("/api/owners/addOwner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newOwner)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        long ownerId = objectMapper.readTree(responseOwner).get("id").asLong();

        String newCat = "{\"name\": \"Vasya\", \"breed\": \"breed1\", \"color\": \"black\", \"owner\": " +
                ownerId + ", \"friends\": []}";

        String responseCat = mockMvc.perform(post("/api/cats/addCat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newCat)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long catId = objectMapper.readTree(responseCat).get("id").asLong();

        String updatedCat = "{\"id\": " + catId + ", \"name\": \"AnotherCatName\", \"breed\": \"breed1\", \"color\": \"black\", \"owner\": " +
                ownerId + ", \"friends\": []}";

        mockMvc.perform(put("/api/cats/updateCat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedCat))
                .andExpect(status().is(403));
    }

    @Test
    public void updateCatWithWrongUserTest() throws Exception {
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

        String responseOwner = mockMvc.perform(post("/api/owners/addOwner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newOwner)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        long ownerId = objectMapper.readTree(responseOwner).get("id").asLong();

        String newCat = "{\"name\": \"Vasya\", \"breed\": \"breed1\", \"color\": \"black\", \"owner\": " +
                ownerId + ", \"friends\": []}";

        String responseCat = mockMvc.perform(post("/api/cats/addCat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newCat)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long catId = objectMapper.readTree(responseCat).get("id").asLong();

        String updatedCat = "{\"id\": " + catId + ", \"name\": \"AnotherCatName\", \"breed\": \"breed1\", \"color\": \"black\", \"owner\": " +
                ownerId + ", \"friends\": []}";

        mockMvc.perform(put("/api/cats/updateCat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedCat)
                        .header("Authorization", "Bearer "+jwtUser2))
                .andExpect(status().isForbidden());
    }

    @Test
    public void getCatsByBreedTest() throws Exception {
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
        String newOwner2 = "{\"name\": \"Dmitry\", \"birthday\": \"2004-10-13T00:00:00Z\", \"cats\": []}";

        String responseOwner1 = mockMvc.perform(post("/api/owners/addOwner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newOwner1)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        mockMvc.perform(post("/api/owners/addOwner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newOwner2)
                        .header("Authorization", "Bearer "+jwtUser2))
                .andExpect(status().isOk());

        ObjectMapper objectMapper = new ObjectMapper();
        long ownerId1 = objectMapper.readTree(responseOwner1).get("id").asLong();

        String newCat1 = "{\"name\": \"Vasya\", \"breed\": \"breed1\", \"color\": \"black\", \"owner\": " +
                ownerId1 + ", \"friends\": []}";

        mockMvc.perform(post("/api/cats/addCat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newCat1)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk());

        String newCat2 = "{\"name\": \"AnotherCatName\", \"breed\": \"breed1\", \"color\": \"white\", \"owner\": " +
                ownerId1 + ", \"friends\": []}";

        mockMvc.perform(post("/api/cats/addCat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newCat2)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/cats/getCatsByBreed/breed1")
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));

        mockMvc.perform(get("/api/cats/getCatsByBreed/breed1")
                        .header("Authorization", "Bearer "+jwtUser2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    @Test
    public void getCatsByNameTest() throws Exception {
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
        String newOwner2 = "{\"name\": \"Dmitry\", \"birthday\": \"2004-10-13T00:00:00Z\", \"cats\": []}";

        String responseOwner1 = mockMvc.perform(post("/api/owners/addOwner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newOwner1)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        mockMvc.perform(post("/api/owners/addOwner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newOwner2)
                        .header("Authorization", "Bearer "+jwtUser2))
                .andExpect(status().isOk());

        ObjectMapper objectMapper = new ObjectMapper();
        long ownerId1 = objectMapper.readTree(responseOwner1).get("id").asLong();

        String newCat1 = "{\"name\": \"Vasya\", \"breed\": \"breed1\", \"color\": \"black\", \"owner\": " +
                ownerId1 + ", \"friends\": []}";

        mockMvc.perform(post("/api/cats/addCat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newCat1)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk());

        String newCat2 = "{\"name\": \"AnotherCatName\", \"breed\": \"breed1\", \"color\": \"white\", \"owner\": " +
                ownerId1 + ", \"friends\": []}";

        mockMvc.perform(post("/api/cats/addCat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newCat2)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/cats/getCatsByName/Vasya")
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));

        mockMvc.perform(get("/api/cats/getCatsByName/Vasya")
                        .header("Authorization", "Bearer "+jwtUser2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    @Test
    public void getCatsByColorTest() throws Exception {
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
        String newOwner2 = "{\"name\": \"Dmitry\", \"birthday\": \"2004-10-13T00:00:00Z\", \"cats\": []}";

        String responseOwner1 = mockMvc.perform(post("/api/owners/addOwner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newOwner1)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        mockMvc.perform(post("/api/owners/addOwner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newOwner2)
                        .header("Authorization", "Bearer "+jwtUser2))
                .andExpect(status().isOk());

        ObjectMapper objectMapper = new ObjectMapper();
        long ownerId1 = objectMapper.readTree(responseOwner1).get("id").asLong();

        String newCat1 = "{\"name\": \"Vasya\", \"breed\": \"breed1\", \"color\": \"black\", \"owner\": " +
                ownerId1 + ", \"friends\": []}";

        mockMvc.perform(post("/api/cats/addCat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newCat1)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk());

        String newCat2 = "{\"name\": \"AnotherCatName\", \"breed\": \"breed1\", \"color\": \"white\", \"owner\": " +
                ownerId1 + ", \"friends\": []}";

        mockMvc.perform(post("/api/cats/addCat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newCat2)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/cats/getCatsByColor/white")
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));

        mockMvc.perform(get("/api/cats/getCatsByColor/white")
                        .header("Authorization", "Bearer "+jwtUser2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    @Test
    public void getCatsByBreedWithoutAuthorizationTest() throws Exception {
        SignUpDto user1 = new SignUpDto();
        user1.setUsername("User1");
        user1.setEmail("user1@gmail.com");
        user1.setPassword("1234");
        user1.setRole("ROLE_USER");
        String jwtUser1 = authenticationService.signUp(user1);

        String newOwner1 = "{\"name\": \"Vlad\", \"birthday\": \"2004-10-13T00:00:00Z\", \"cats\": []}";

        String responseOwner1 = mockMvc.perform(post("/api/owners/addOwner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newOwner1)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        long ownerId1 = objectMapper.readTree(responseOwner1).get("id").asLong();

        String newCat1 = "{\"name\": \"Vasya\", \"breed\": \"breed1\", \"color\": \"black\", \"owner\": " +
                ownerId1 + ", \"friends\": []}";

        mockMvc.perform(post("/api/cats/addCat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newCat1)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk());

        String newCat2 = "{\"name\": \"AnotherCatName\", \"breed\": \"breed1\", \"color\": \"white\", \"owner\": " +
                ownerId1 + ", \"friends\": []}";

        mockMvc.perform(post("/api/cats/addCat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newCat2)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/cats/getCatsByBreed/breed1"))
                .andExpect(status().is(403));
    }

    @Test
    public void getCatsByNameWithoutAuthorizationTest() throws Exception {
        SignUpDto user1 = new SignUpDto();
        user1.setUsername("User1");
        user1.setEmail("user1@gmail.com");
        user1.setPassword("1234");
        user1.setRole("ROLE_USER");
        String jwtUser1 = authenticationService.signUp(user1);

        String newOwner1 = "{\"name\": \"Vlad\", \"birthday\": \"2004-10-13T00:00:00Z\", \"cats\": []}";

        String responseOwner1 = mockMvc.perform(post("/api/owners/addOwner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newOwner1)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        long ownerId1 = objectMapper.readTree(responseOwner1).get("id").asLong();

        String newCat1 = "{\"name\": \"Vasya\", \"breed\": \"breed1\", \"color\": \"black\", \"owner\": " +
                ownerId1 + ", \"friends\": []}";

        mockMvc.perform(post("/api/cats/addCat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newCat1)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk());

        String newCat2 = "{\"name\": \"AnotherCatName\", \"breed\": \"breed1\", \"color\": \"white\", \"owner\": " +
                ownerId1 + ", \"friends\": []}";

        mockMvc.perform(post("/api/cats/addCat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newCat2)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/cats/getCatsByName/Vasya"))
                .andExpect(status().is(403));
    }

    @Test
    public void getCatsByColorWithoutAuthorizationTest() throws Exception {
        SignUpDto user1 = new SignUpDto();
        user1.setUsername("User1");
        user1.setEmail("user1@gmail.com");
        user1.setPassword("1234");
        user1.setRole("ROLE_USER");
        String jwtUser1 = authenticationService.signUp(user1);

        String newOwner1 = "{\"name\": \"Vlad\", \"birthday\": \"2004-10-13T00:00:00Z\", \"cats\": []}";

        String responseOwner1 = mockMvc.perform(post("/api/owners/addOwner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newOwner1)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        long ownerId1 = objectMapper.readTree(responseOwner1).get("id").asLong();

        String newCat1 = "{\"name\": \"Vasya\", \"breed\": \"breed1\", \"color\": \"black\", \"owner\": " +
                ownerId1 + ", \"friends\": []}";

        mockMvc.perform(post("/api/cats/addCat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newCat1)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk());

        String newCat2 = "{\"name\": \"AnotherCatName\", \"breed\": \"breed1\", \"color\": \"white\", \"owner\": " +
                ownerId1 + ", \"friends\": []}";

        mockMvc.perform(post("/api/cats/addCat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newCat2)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/cats/getCatsByColor/white"))
                .andExpect(status().is(403));
    }

    @Test
    public void makeFriendsTest() throws Exception {
        SignUpDto user1 = new SignUpDto();
        user1.setUsername("User1");
        user1.setEmail("user1@gmail.com");
        user1.setPassword("1234");
        user1.setRole("ROLE_ADMIN");
        String jwtUser1 = authenticationService.signUp(user1);

        String newOwner = "{\"name\": \"Vlad\", \"birthday\": \"2004-10-13T00:00:00Z\", \"cats\": []}";

        String responseOwner = mockMvc.perform(post("/api/owners/addOwner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newOwner)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        long ownerId = objectMapper.readTree(responseOwner).get("id").asLong();

        String newCat1 = "{\"name\": \"Vasya\", \"breed\": \"breed1\", \"color\": \"black\", \"owner\": " +
                ownerId + ", \"friends\": []}";

        String responseCat1 = mockMvc.perform(post("/api/cats/addCat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newCat1)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String newCat2 = "{\"name\": \"Vasya\", \"breed\": \"breed1\", \"color\": \"white\", \"owner\": " +
                ownerId + ", \"friends\": []}";

        String responseCat2 = mockMvc.perform(post("/api/cats/addCat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newCat2)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long catId1 = objectMapper.readTree(responseCat1).get("id").asLong();
        long catId2 = objectMapper.readTree(responseCat2).get("id").asLong();

        mockMvc.perform(put("/api/cats/makeFriends/" + catId1 + ", " + catId2)
                        .header("Authorization", "Bearer "+jwtUser1))
                        .andExpect(status().isOk());
    }

    @Test
    public void makeFriendsWithNoAdminTest() throws Exception {
        SignUpDto user1 = new SignUpDto();
        user1.setUsername("User1");
        user1.setEmail("user1@gmail.com");
        user1.setPassword("1234");
        user1.setRole("ROLE_USER");
        String jwtUser1 = authenticationService.signUp(user1);

        String newOwner = "{\"name\": \"Vlad\", \"birthday\": \"2004-10-13T00:00:00Z\", \"cats\": []}";

        String responseOwner = mockMvc.perform(post("/api/owners/addOwner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newOwner)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        long ownerId = objectMapper.readTree(responseOwner).get("id").asLong();

        String newCat1 = "{\"name\": \"Vasya\", \"breed\": \"breed1\", \"color\": \"black\", \"owner\": " +
                ownerId + ", \"friends\": []}";

        String responseCat1 = mockMvc.perform(post("/api/cats/addCat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newCat1)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String newCat2 = "{\"name\": \"Vasya\", \"breed\": \"breed1\", \"color\": \"white\", \"owner\": " +
                ownerId + ", \"friends\": []}";

        String responseCat2 = mockMvc.perform(post("/api/cats/addCat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newCat2)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long catId1 = objectMapper.readTree(responseCat1).get("id").asLong();
        long catId2 = objectMapper.readTree(responseCat2).get("id").asLong();

        mockMvc.perform(put("/api/cats/makeFriends/" + catId1 + ", " + catId2)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isForbidden());
    }

    @Test
    public void makeFriendsWithoutAuthorizationTest() throws Exception {
        SignUpDto user1 = new SignUpDto();
        user1.setUsername("User1");
        user1.setEmail("user1@gmail.com");
        user1.setPassword("1234");
        user1.setRole("ROLE_ADMIN");
        String jwtUser1 = authenticationService.signUp(user1);

        String newOwner = "{\"name\": \"Vlad\", \"birthday\": \"2004-10-13T00:00:00Z\", \"cats\": []}";

        String responseOwner = mockMvc.perform(post("/api/owners/addOwner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newOwner)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        long ownerId = objectMapper.readTree(responseOwner).get("id").asLong();

        String newCat1 = "{\"name\": \"Vasya\", \"breed\": \"breed1\", \"color\": \"black\", \"owner\": " +
                ownerId + ", \"friends\": []}";

        String responseCat1 = mockMvc.perform(post("/api/cats/addCat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newCat1)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String newCat2 = "{\"name\": \"Vasya\", \"breed\": \"breed1\", \"color\": \"white\", \"owner\": " +
                ownerId + ", \"friends\": []}";

        String responseCat2 = mockMvc.perform(post("/api/cats/addCat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newCat2)
                        .header("Authorization", "Bearer "+jwtUser1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long catId1 = objectMapper.readTree(responseCat1).get("id").asLong();
        long catId2 = objectMapper.readTree(responseCat2).get("id").asLong();

        mockMvc.perform(put("/api/cats/makeFriends/" + catId1 + ", " + catId2))
                .andExpect(status().is(403));
    }
}
