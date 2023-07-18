package com.udemy.demo.jwt;

import com.udemy.demo.user.UserInfo;
import com.udemy.demo.user.UserRepository;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JwtController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtUtils jwtUtils;


    // @Autowired
    // MyUserDetailService service;

    @Autowired
    AuthenticationManagerBuilder authenticationManagerBuilder;

    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest jwtRequest) {
        // service.loadUserByUsername(login);
        Authentication authentication = logUser(jwtRequest.getEmail(), jwtRequest.getPassword());


        Object principal = authentication.getPrincipal();
        UserInfo existingUser = userRepository.findOneByEmail(((UserDetails) principal).getUsername());
        if(existingUser == null) {
            return new ResponseEntity<String>("User not found", HttpStatus.BAD_REQUEST);
        }
        String token = jwtUtils.generateToken(authentication);
        existingUser.setToken("Bearer " + token);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, token);
        Map<String, String> userMap = mappingUser(existingUser);

        return new ResponseEntity<Map<String, String>>(userMap, httpHeaders, HttpStatus.OK);
    }

    public Authentication logUser(String mail, String password) {
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(new UsernamePasswordAuthenticationToken(mail, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    public Map<String, String> mappingUser(UserInfo userInfo){
        Map<String, String> userMap = new HashMap<>();
        userMap.put("email", userInfo.getEmail());
        userMap.put("firsName", userInfo.getFirstName());
        userMap.put("lastName", userInfo.getLastName());
        userMap.put("token", userInfo.getToken());
        return userMap;
    }

}