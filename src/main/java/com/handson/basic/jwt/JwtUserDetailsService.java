package com.handson.basic.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private DBUserService userService;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {

        Optional<DBUser> dbUser = userService.findUserName(userName);
        if (dbUser.isPresent()) {
            return new User(dbUser.get().getName(), dbUser.get().getPassword(), new ArrayList<>());
        } else {
            throw new UsernameNotFoundException("User not found : " + userName);
        }
    }
}
