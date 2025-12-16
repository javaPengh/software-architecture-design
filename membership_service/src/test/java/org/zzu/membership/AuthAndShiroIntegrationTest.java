package org.zzu.membership;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AuthAndShiroIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void secureEndpointRequiresLogin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/membership/secure"))
                .andExpect(MockMvcResultMatchers.status().isFound());
    }

    @Test
    void loginThenAccessSecure() throws Exception {
        String body = "{\"username\":\"user\",\"password\":\"user123\"}";

        MvcResult login = mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        var cookies = login.getResponse().getCookies();
        mockMvc.perform(MockMvcRequestBuilders.get("/membership/secure")
                        .cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("member角色可访问"));
    }
}

