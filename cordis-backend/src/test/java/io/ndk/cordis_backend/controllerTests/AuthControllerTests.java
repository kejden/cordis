package io.ndk.cordis_backend.controllerTests;

import io.ndk.cordis_backend.config.JwtFilter;
import io.ndk.cordis_backend.config.SecurityConfig;
import io.ndk.cordis_backend.controller.AuthController;
import io.ndk.cordis_backend.dto.request.AccountSignUp;
import io.ndk.cordis_backend.dto.request.SignInRequest;
import io.ndk.cordis_backend.service.CookieService;
import io.ndk.cordis_backend.service.JwtService;
import io.ndk.cordis_backend.service.UserService;
import io.ndk.cordis_backend.service.impl.customUserDetailService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = true)
@ExtendWith(MockitoExtension.class)
@Import(JwtFilter.class)
@ActiveProfiles("test")
public class AuthControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private CookieService cookieService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private customUserDetailService customUserDetailService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    void testSignUp() throws Exception {
        AccountSignUp signUpRequest = AccountSignUp.builder()
                .email("test@example.com")
                .password("12345")
                .build();

        when(userService.signUp(any(AccountSignUp.class))).thenReturn(signUpRequest);

        mockMvc.perform(post("/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"password\":\"12345\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void testSignIn() throws Exception {
        when(userService.signIn(any(SignInRequest.class))).thenReturn(null);
        when(jwtService.generateToken(anyString())).thenReturn("testToken");
        when(cookieService.getNewCookie("jwt", "testToken")).thenReturn(new Cookie("jwt", "testToken"));

        mockMvc.perform(post("/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"password\":\"12345\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void testLogout() throws Exception {
        when(cookieService.getJwtCookie(any())).thenReturn("jwtCookieValue");
        when(jwtService.extractUserName("jwtCookieValue")).thenReturn("test@example.com");
        when(cookieService.deleteCookie("jwt")).thenReturn(new Cookie("jwt", null));

        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isOk());
    }
}
