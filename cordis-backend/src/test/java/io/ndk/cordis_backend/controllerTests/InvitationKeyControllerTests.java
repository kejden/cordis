package io.ndk.cordis_backend.controllerTests;

import io.ndk.cordis_backend.config.JwtFilter;
import io.ndk.cordis_backend.controller.InvitationKeyController;
import io.ndk.cordis_backend.entity.InvitationKeyEntity;
import io.ndk.cordis_backend.service.CookieService;
import io.ndk.cordis_backend.service.InvitationKeyService;
import io.ndk.cordis_backend.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = InvitationKeyController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@Import(JwtFilter.class)
@ActiveProfiles("test")
public class InvitationKeyControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InvitationKeyService invitationKeyService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private io.ndk.cordis_backend.service.impl.customUserDetailService customUserDetailService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private CookieService cookieService;

    private Principal principal;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        principal = () -> "testUser@example.com";
        objectMapper = new ObjectMapper();
    }

    @Test
    void testGenerateInvitationKey() throws Exception {
        String invitationKeyValue = UUID.randomUUID().toString();
        InvitationKeyEntity invitationKey = InvitationKeyEntity.builder()
                .id(1L)
                .invitationKey(invitationKeyValue)
                .activationTime(LocalDateTime.now())
                .expirationTime(LocalDateTime.now().plusWeeks(1))
                .build();

        when(invitationKeyService.generateInvitationKey(anyLong())).thenReturn(invitationKey);

        mockMvc.perform(post("/api/invitation-keys/generate/1")
                        .principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.invitationKey").value(invitationKeyValue));
    }

    @Test
    void testGetActiveInvitationKeys() throws Exception {
        InvitationKeyEntity invitationKey = InvitationKeyEntity.builder()
                .id(1L)
                .invitationKey("test-invite")
                .activationTime(LocalDateTime.now().minusDays(1))
                .expirationTime(LocalDateTime.now().plusDays(1))
                .build();

        List<InvitationKeyEntity> invitationKeys = Collections.singletonList(invitationKey);
        when(invitationKeyService.getActiveInvitationKeys(anyLong())).thenReturn(invitationKeys);

        mockMvc.perform(get("/api/invitation-keys/active/1")
                        .principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].invitationKey").value("test-invite"));
    }
}