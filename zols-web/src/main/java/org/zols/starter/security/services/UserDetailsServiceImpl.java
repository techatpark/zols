package org.zols.starter.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zols.starter.models.User;
import org.zols.starter.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    /**
     * userRepository.
     */
    @Autowired
    private UserRepository userRepository;


    /**
     * @param username
     * @return user
     * @throws UsernameNotFoundException
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String username)
            throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User Not Found with username: " + username));

        return UserDetailsImpl.build(user);
    }

    /**
     * @param entity the entity
     * @param <S>
     * @return user
     */
    public <S extends User> S save(final S entity) {
        return userRepository.save(entity);
    }
}
