package org.zols.securitymanager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.zols.securitymanager.domain.Role;
import org.zols.securitymanager.domain.User;

@Service
public class UserService implements UserDetailsService {

    public UserDetails loadUserByUsername(String userName, String password) throws UsernameNotFoundException {
        User user = null;
        try {
            GitHub github = GitHub.connectUsingPassword(userName, password);
            GHMyself myself = github.getMyself();
            GHRepository repository = myself.getRepository("zols");
            user = new User();
            user.setUsername(userName);
            user.setPassword(password);
            
            Role role = new Role();
            role.setName("ADMIN");
            List<Role> roles = new ArrayList<Role>();
            roles.add(role);
            
            user.setAuthorities(roles);
            
            
        } catch (IOException ex) {
            Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
        }

        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String string) throws UsernameNotFoundException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
