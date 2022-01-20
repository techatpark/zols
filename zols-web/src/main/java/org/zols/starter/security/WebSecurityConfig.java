package org.zols.starter.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.zols.starter.models.ERole;
import org.zols.starter.models.Role;
import org.zols.starter.models.User;
import org.zols.starter.repository.RoleRepository;
import org.zols.starter.security.jwt.AuthEntryPointJwt;
import org.zols.starter.security.jwt.AuthTokenFilter;
import org.zols.starter.security.services.UserDetailsServiceImpl;

import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        // securedEnabled = true,
        // jsr250Enabled = true,
        prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    /**
     * userDetailsService.
     */
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    /**
     * roleRepository.
     */
    @Autowired
    private RoleRepository roleRepository;
    /**
     * unauthorizedHandler.
     */
    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;
    /**
     * Default Password.
     */
    @Value("${security.user.password:password}")
    private String password;

    /**
     * authenticationJwtTokenFilter.
     * @return new AuthTokenFilter
     */
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    /**
     * @param authenticationManagerBuilder the authenticationManagerBuilder
     * @throws Exception
     */
    @Override
    public void configure(
            final AuthenticationManagerBuilder authenticationManagerBuilder)
            throws Exception {
        authenticationManagerBuilder.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
        intialize();
    }

    /**
     * intialize.
     */
    void intialize() {
        Role role = new Role();
        role.setName(ERole.valueOf("ROLE_USER"));
        roleRepository.save(role);
        role.setName(ERole.valueOf("ROLE_MODERATOR"));
        roleRepository.save(role);
        role.setName(ERole.valueOf("ROLE_ADMIN"));
        roleRepository.save(role);

        User user = new User();
        user.setUsername("admin");
        user.setPassword(passwordEncoder().encode(password));
        user.setEmail("admin@email.com");
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(role);
        user.setRoles(roleSet);
        userDetailsService.save(user);

    }

    /**
     * @return authenticationManagerBean
     * @throws Exception
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * passwordEncoder.
     * @return BCrypt Password Encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * @param http the http
     * @throws Exception
     */
    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(unauthorizedHandler).and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests().antMatchers("/api/auth/**").permitAll()
                .antMatchers("/api/**").authenticated()
                .anyRequest().permitAll();

        http.addFilterBefore(authenticationJwtTokenFilter(),
                UsernamePasswordAuthenticationFilter.class);
    }
}
