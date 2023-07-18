package com.udemy.demo.user;

import com.udemy.demo.jwt.JwtController;
import com.udemy.demo.jwt.JwtFilter;
import com.udemy.demo.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    JwtController jwtController;

    @PostMapping("/users")
    public ResponseEntity<?> add(@Valid @RequestBody UserInfo userInfo) {
        UserInfo existingUser = userRepository.findOneByEmail(userInfo.getEmail());
        if (existingUser != null){
            return new ResponseEntity<String>("User already existing", HttpStatus.BAD_REQUEST);
        }
        //persist to BD with saveUser method
        UserInfo user = saveUser(userInfo);
        Authentication authentication = jwtController.logUser(userInfo.getEmail(), userInfo.getPassword());
        String jwt = jwtUtils.generateToken(authentication);
        //System.out.println(jwt);
        user.setToken("Bearer " + jwt);
        //persist to BD afeter setting token
        // userRepository.save(user);
        Map<String, String> userMap = mappingUser(user);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
        return new ResponseEntity<Map<String, String>>(userMap, httpHeaders, HttpStatus.CREATED);

    }

    @GetMapping("/isConnected")
    public ResponseEntity<String> getUSerConnected() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserInfo user = userRepository.findOneByEmail(((UserDetails) principal).getUsername());
            
            return new ResponseEntity<>(user.getEmail(), HttpStatus.OK);

        }
        catch(Exception e) {
            return new ResponseEntity<String>("User is not connected", HttpStatus.FORBIDDEN);
        }
    }

    private UserInfo saveUser(UserInfo userInfo) {
        UserInfo user = new UserInfo();
        user.setEmail(userInfo.getEmail());
        user.setPassword(new BCryptPasswordEncoder().encode(userInfo.getPassword()));
        user.setLastName(StringUtils.capitalize(userInfo.getLastName()));
        user.setFirstName(StringUtils.capitalize(userInfo.getFirstName()));

        userRepository.save(user);
        return user;
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