package eugenestellar.authservice.config;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import eugenestellar.authservice.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class JwtFilter extends OncePerRequestFilter {

  private static final Logger log = LoggerFactory.getLogger(JwtFilter.class);

  private final JwtUtil jwtUtil;
  private final ObjectMapper objectMapper;

  public JwtFilter(JwtUtil jwtUtil, ObjectMapper objectMapper) {
    this.jwtUtil = jwtUtil;
    this.objectMapper = objectMapper;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {

    String authHeader = request.getHeader("Authorization");

    if (authHeader != null && authHeader.startsWith("Bearer ") && SecurityContextHolder.getContext().getAuthentication() == null) {
      try {
        String token = authHeader.substring(7);
        DecodedJWT jwt = jwtUtil.validateAccessToken(token);
        String username = jwt.getSubject();

        List<String> roles = jwt.getClaim("roles").isNull() ? List.of() :
            jwt.getClaim("roles").asList(String.class);

        List<SimpleGrantedAuthority> authorities = roles.stream()
            .map(SimpleGrantedAuthority::new)
            .toList();

        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(username, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authToken);
      } catch (JWTVerificationException ex) {

        SecurityContextHolder.clearContext();
        log.warn("Invalid JWT token. {}", ex.getMessage());
        sendCustomError(response, "Invalid JWT Token");
        return;

      } catch (UsernameNotFoundException ex) {

        log.warn("Unknown user. {}", ex.getMessage());
        SecurityContextHolder.clearContext();
        sendCustomError(response, "Unknown user");
        return;
      }
    }
    filterChain.doFilter(request, response);
  }

  private void sendCustomError(HttpServletResponse response, String message) throws IOException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    response.addHeader("WWW-Authenticate", "Bearer error=\"invalid_token\", error_description=\"" + message + "\"");

    response.getWriter().write(objectMapper.writeValueAsString(Map.of("error", message)));
  }

}