package com.pvncodes.MD.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Verifies the URL rules in SecurityConfig using stub endpoints (no database needed). */
@WebMvcTest(controllers = SecurityRulesTest.Stub.class)
@Import({SecurityConfig.class, JwtAuthFilter.class, JwtService.class, SecurityRulesTest.Stub.class})
@TestPropertySource(properties = "jwt.secret=0123456789-0123456789-0123456789-ab")
class SecurityRulesTest {

    @RestController
    static class Stub {
        @GetMapping({"/api/users/profile", "/api/ngos/profile", "/api/users", "/api/medicines"})
        String ok() { return "ok"; }
    }

    @Autowired MockMvc mvc;
    @Autowired JwtService jwt;

    private String bearer(long id, String role) {
        return "Bearer " + jwt.generateToken(id, role);
    }

    @Test
    void profileWithoutTokenIs401() throws Exception {
        mvc.perform(get("/api/users/profile")).andExpect(status().isUnauthorized());
    }

    @Test
    void profileWithLegacyFakeTokenIs401() throws Exception {
        mvc.perform(get("/api/users/profile").header("Authorization", "Bearer fake-jwt-token-1"))
           .andExpect(status().isUnauthorized());
    }

    @Test
    void userTokenOpensUserProfile() throws Exception {
        mvc.perform(get("/api/users/profile").header("Authorization", bearer(1, "USER")))
           .andExpect(status().isOk());
    }

    @Test
    void userTokenCannotOpenNgoProfile() throws Exception {
        mvc.perform(get("/api/ngos/profile").header("Authorization", bearer(1, "USER")))
           .andExpect(status().isForbidden());
    }

    @Test
    void ngoTokenCannotOpenUserProfile() throws Exception {
        mvc.perform(get("/api/users/profile").header("Authorization", bearer(1, "NGO")))
           .andExpect(status().isForbidden());
    }

    @Test
    void userListIsNotPublicNorForOrdinaryUsers() throws Exception {
        mvc.perform(get("/api/users")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/users").header("Authorization", bearer(1, "USER")))
           .andExpect(status().isForbidden());
    }

    @Test
    void medicinesStayPublicForGuests() throws Exception {
        mvc.perform(get("/api/medicines")).andExpect(status().isOk());
    }
}
