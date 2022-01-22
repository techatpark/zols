package org.zols.starter.controllers;

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

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
class AuthController {

    /**
     * declare Authentication Manager authenticationManager.
     */
    private final AuthenticationManager authenticationManager;

    /**
     * declare a UserRepository userRepository.
     */
    private final UserRepository userRepository;

    /**
     * declare a RoleRepository roleRepository.
     */
    private final RoleRepository roleRepository;

    /**
     * declare a PasswordEncoder encoder.
     */
    private final PasswordEncoder encoder;

    /**
     * declare a TokenProvider jwtUtils.
     */
    private final TokenProvider jwtUtils;

    /**
     * Build Controller.
     *
     * @param aAuthenticationManager
     * @param aUserRepository
     * @param aRoleRepository
     * @param anEncoder
     * @param aJwtUtils
     */
    AuthController(final AuthenticationManager aAuthenticationManager,
                          final UserRepository aUserRepository,
                          final RoleRepository aRoleRepository,
                          final PasswordEncoder anEncoder,
                          final TokenProvider aJwtUtils) {
        this.authenticationManager = aAuthenticationManager;
        this.userRepository = aUserRepository;
        this.roleRepository = aRoleRepository;
        this.encoder = anEncoder;
        this.jwtUtils = aJwtUtils;
    }

    /**
     * performs the signin function.
     *
     * @param loginRequest login Request
     * @return void response entity
     */
    @PostMapping("/signin")
    public final ResponseEntity<?> authenticateUser(final @RequestBody
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
