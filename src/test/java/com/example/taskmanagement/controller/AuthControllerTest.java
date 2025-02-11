package com.example.taskmanagement.controller;

import com.example.taskmanagement.dto.AuthRequest;
import com.example.taskmanagement.entity.User;
import com.example.taskmanagement.security.JwtUtil;
import com.example.taskmanagement.service.AuthService;
import com.example.taskmanagement.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private AuthService authService;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password");
    }

//    @Test
//    void testCreateAuthenticationToken() throws Exception {
//        AuthRequest authRequest = AuthRequest.builder()
//                .username("testuser")
//                .password("password")
//                .build();
//        UserDetails userDetails = org.springframework.security.core.userdetails.User
//                .withUsername("testuser")
//                .password("password")
//                .authorities("ROLE_USER")
//                .build();
//
//        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
//                .thenReturn(new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));
//        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
//        when(jwtUtil.generateToken(userDetails)).thenReturn("test_token");
//
//        mockMvc.perform(post("/auth/login")
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(authRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.token").value("test_token"));
//    }
//
//    @Test
//    void testRegisterUser() throws Exception {
//        when(userService.registerUser(any(User.class))).thenReturn(testUser);
//
//        mockMvc.perform(post("/auth/register")
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(testUser)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.username").value("testuser"));
//    }
//
//    @Test
//    void testRegisterUser_UsernameAlreadyExists() throws Exception {
//        when(userService.registerUser(any(User.class))).thenThrow(new RuntimeException("Username already exists"));
//
//        mockMvc.perform(post("/auth/register")
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(testUser)))
//                .andExpect(status().isBadRequest())
//                .andExpect(content().string("Username already exists"));
//    }
}