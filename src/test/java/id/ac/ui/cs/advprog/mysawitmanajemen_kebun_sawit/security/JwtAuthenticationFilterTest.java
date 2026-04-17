package id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private JwtUtils jwtUtils;

    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtils);
    }

    @Test
    void testDoFilterInternal_NoAuthHeader() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_InvalidAuthHeader() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("InvalidToken");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_ValidToken() throws Exception {
        String validToken = "valid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtUtils.validateToken(validToken)).thenReturn(true);
        when(jwtUtils.getEmailFromToken(validToken)).thenReturn("test@example.com");
        when(jwtUtils.getRoleFromToken(validToken)).thenReturn("MANDOR");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_NoBearerPrefix() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic abc");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_AlreadyAuthenticated() throws Exception {
        String validToken = "valid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtUtils.validateToken(validToken)).thenReturn(true);
        when(jwtUtils.getEmailFromToken(validToken)).thenReturn("test@example.com");
        when(jwtUtils.getRoleFromToken(validToken)).thenReturn("MANDOR");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(2)).doFilter(request, response);
    }
}