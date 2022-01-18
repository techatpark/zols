package org.zols.starter.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zols.starter.payload.request.LoginRequest;
import org.zols.starter.payload.response.JwtResponse;
import org.zols.starter.repository.RoleRepository;
import org.zols.starter.repository.UserRepository;
import org.zols.starter.security.jwt.TokenProvider;
import org.zols.starter.security.services.UserDetailsImpl;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /**
     * declare Authentication Manager authenticationManager.
     */
    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * declare a UserRepository userRepository.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * declare a RoleRepository roleRepository.
     */
    @Autowired
    private RoleRepository roleRepository;

    /**
     * declare a PasswordEncoder encoder.
     */
    @Autowired
    private PasswordEncoder encoder;

    /**
     * declare a TokenProvider jwtUtils.
     */
    @Autowired
    private TokenProvider jwtUtils;

    /**
     * performs the signin function.
     *
     * @param loginRequest login Request
     * @return void response entity
     */
    @PostMapping("/signin")
    public final ResponseEntity<?> authenticateUser(@Valid @RequestBody final
                                                    LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails =
                (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }


}
