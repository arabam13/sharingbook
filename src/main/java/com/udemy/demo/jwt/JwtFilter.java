package com.udemy.demo.jwt;

 //import com.udemy.demo.configuration.MyUserDetailService;
 import com.udemy.demo.user.UserInfo;
 import com.udemy.demo.user.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

//    @Autowired
//    MyUserDetailService service;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserRepository userRepository;

    public static final String AUTHORIZATION_HEADER = "Authorization";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String jwt = resolveToken(request);

        if (jwt != null) {
            try {
                UserInfo existingUser = userRepository.findOneByEmail(jwtUtils.decodedToken(jwt).getBody().get("sub").toString());
                if (existingUser != null){
                    if (StringUtils.hasText(jwt) && !isUrlPermitted(request) ) {
                        Authentication authentication = jwtUtils.getAuthentication(jwt);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (Exception e) {
                new RuntimeException("token not valid : "+ e.getMessage());
            }
        }

        chain.doFilter(request, response);
    }
    private boolean isUrlPermitted(HttpServletRequest request) {
        String url = request.getRequestURI();
        if(url.equals("/authenticate") || url.equals("/users")) {
            return true;
        }
        return false;
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}