package com.project.apilistadetarefas;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "app.jwt.secret=test-secret-for-jwt-signing-at-least-32-characters")
@AutoConfigureMockMvc
class ApilistadetarefasApplicationTests {

    @Autowired
    private MockMvc mockMvc;

	@Test
    void contextLoads() {
    }

    @Test
    void openApiYamlIncludesApiPathsAndJwtScheme() throws Exception {
        mockMvc.perform(get("/v3/api-docs.yaml"))
                .andExpect(status().isOk())
                .andExpect(content().string(allOf(
                        containsString("openapi:"),
                        containsString("/api/tasks"),
                        containsString("bearerAuth")
                )));
    }

    @Test
    void passwordResetRequestIsPublic() throws Exception {
        mockMvc.perform(post("/api/users/password-reset/request")
                        .contentType(APPLICATION_JSON)
                        .content("{\"email\":\"unknown@example.com\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void profileEndpointRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());
    }

}
