package eugenestellar.authservice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.juli.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class GatewaySecretFilter extends OncePerRequestFilter {

  private final String gatewaySecret;
  private static final Logger log = LoggerFactory.getLogger(GatewaySecretFilter.class);

  public GatewaySecretFilter(@Value("${GATEWAY_SECRET}") String gatewaySecret) {
    this.gatewaySecret = gatewaySecret;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {

    String path = request.getRequestURI();

    if (path.startsWith("/.well-known/") || path.startsWith("/uptime")) {
      filterChain.doFilter(request, response);
      return;
    }

    String secretFromHeader = request.getHeader("x-gateway-secret");
    if (secretFromHeader == null || !secretFromHeader.equals(gatewaySecret)) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Direct access is forbidden. Please use API Gateway.");
      return;
    }
    filterChain.doFilter(request, response);
  }
}
