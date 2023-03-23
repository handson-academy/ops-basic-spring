package com.handson.basic.jwt;


import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DBUserService {


    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(DBUserService.class);

    @Autowired
    private DBUserRepository repository;

    public Optional<DBUser> findUserName(String userName) {
            return repository.findByName(userName);
    }

    public void save(DBUser user) {
        repository.save(user);
    }
}
