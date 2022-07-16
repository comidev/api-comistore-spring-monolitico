package comidev.comistore.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.fasterxml.jackson.databind.ObjectMapper;

import comidev.comistore.exceptions.ErrorMessage;
import comidev.comistore.exceptions.HandleException;
import comidev.comistore.exceptions.HttpException;
import comidev.comistore.services.jwt.JwtService;
import comidev.comistore.services.jwt.Payload;

public class JwtFilter extends OncePerRequestFilter {
    private JwtService jwtService;
    private final Logger LOGGER = LoggerFactory.getLogger(JwtFilter.class);

    public JwtFilter() {
        this.jwtService = new JwtService();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        LOGGER.info("Pasó por aqui");
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!jwtService.isBearer(bearerToken)) {
            chain.doFilter(request, response);
            LOGGER.info("No hay token uwu");
            return;
        }

        try {
            LOGGER.info("Iniciando la payloadizacion :v");
            Payload payload = jwtService.verify(bearerToken);

            List<SimpleGrantedAuthority> authorities = payload.getRoles().stream()
                    .map(item -> "ROLE_" + item)
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            System.out.println("\n\n\t");
            authorities.forEach(item -> System.out.println(item.getAuthority()));
            System.out.println("\n\n");

            Authentication auth = new UsernamePasswordAuthenticationToken(
                    payload.getUsername(),
                    bearerToken,
                    authorities);

            SecurityContextHolder.getContext().setAuthentication(auth);

            chain.doFilter(request, response);

        } catch (HttpException e) {
            HttpStatus status = e.getStatus();
            ErrorMessage body = new ErrorMessage(status, e.getMessage(), request);

            response.setStatus(status.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            new ObjectMapper().writeValue(response.getOutputStream(), body);

        } catch (Exception e) {
            HttpStatus status = HandleException.statusByException(e);
            ErrorMessage body = new ErrorMessage(status, e.getMessage(), request);

            response.setStatus(status.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            new ObjectMapper().writeValue(response.getOutputStream(), body);
        }
    }
}
