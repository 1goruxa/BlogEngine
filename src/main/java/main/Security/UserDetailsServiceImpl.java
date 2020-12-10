package main.Security;

import main.Repo.UserRepository;
import main.model.User;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service("userDetailsServiceImpl")
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println(userRepository);
        User user = userRepository.findOneByEmail(email).orElseThrow(() -> new UsernameNotFoundException("USER NOT FOUND"));

        return SecurityUser.fromUser(user);
    }
}
