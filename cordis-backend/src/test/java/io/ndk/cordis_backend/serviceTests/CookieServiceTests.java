package io.ndk.cordis_backend.serviceTests;

import io.ndk.cordis_backend.handler.BusinessErrorCodes;
import io.ndk.cordis_backend.handler.CustomException;
import io.ndk.cordis_backend.service.impl.CookieServiceImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class CookieServiceTests {
    @InjectMocks
    private CookieServiceImpl cookieService;

    @Mock
    private HttpServletRequest request;

    @Test
    void testGetNewCookieCreatesCookieWithCorrectSettings() {
        String name = "testName";
        String value = "testValue";

        Cookie cookie = cookieService.getNewCookie(name, value);

        assertNotNull(cookie);
        assertEquals(name, cookie.getName());
        assertEquals(value, cookie.getValue());
        assertTrue(cookie.isHttpOnly());
        assertFalse(cookie.getSecure());
        assertEquals("/", cookie.getPath());
        assertEquals(24*60*60, cookie.getMaxAge());
    }


    @Test
    void testDeleteCookieCreatesCookieWithNullValueAndMaxAgeZero() {
        String cookieName = "testCookie";

        Cookie cookie = cookieService.deleteCookie(cookieName);

        assertNotNull(cookie);
        assertEquals(cookieName, cookie.getName());
        assertNull(cookie.getValue());
        assertTrue(cookie.isHttpOnly());
        assertFalse(cookie.getSecure());
        assertEquals("/", cookie.getPath());
        assertEquals(0, cookie.getMaxAge());
    }



    @Test
    void testGetJwtCookieReturnsValueWhenCookieExists() {
        Cookie[] cookies = {
                new Cookie("someCookie", "someValue"),
                new Cookie("jwt", "expectedJwtValue"),
                new Cookie("anotherCookie", "anotherValue")
        };
        when(request.getCookies()).thenReturn(cookies);

        String jwtValue = cookieService.getJwtCookie(request);

        assertEquals("expectedJwtValue", jwtValue);
    }

    @Test
    void testGetJwtCookieThrowsExceptionWhenCookieDoesNotExist() {
        Cookie[] cookies = {
                new Cookie("someCookie", "someValue"),
                new Cookie("anotherCookie", "anotherValue")
        };
        when(request.getCookies()).thenReturn(cookies);

        CustomException exception = assertThrows(
                CustomException.class,
                () -> cookieService.getJwtCookie(request)
        );
        assertEquals(BusinessErrorCodes.BAD_COOKIE, exception.getErrorCode());
    }

    @Test
    void testGetJwtCookieThrowsExceptionWhenCookiesArrayIsNull() {
        when(request.getCookies()).thenReturn(null);

        CustomException exception = assertThrows(
                CustomException.class,
                () -> cookieService.getJwtCookie(request)
        );
        assertEquals(BusinessErrorCodes.BAD_COOKIE, exception.getErrorCode());
    }
}
