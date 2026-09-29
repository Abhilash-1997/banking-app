package com.novabank.identity.internal.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@org.springframework.test.context.ActiveProfiles("test")
class MeControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    JwtDecoder jwtDecoder;

    @Test
    void me_unauthenticated_is401() throws Exception {
        mockMvc.perform(get("/api/v1/me")).andExpect(status().isUnauthorized());
    }

    @Test
    void me_authenticated_returnsSubject() throws Exception {
        mockMvc.perform(get("/api/v1/me")
                        .with(jwt().jwt(token -> token
                                .subject("idp-sub-1")
                                .claim("preferred_username", "abhilash"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subject").value("idp-sub-1"))
                .andExpect(jsonPath("$.preferredUsername").value("abhilash"));
    }

    @Test
    void health_isPublic() throws Exception {
        mockMvc.perform(get("/actuator/health")).andExpect(status().isOk());
    }
}
