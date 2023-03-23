package com.handson.basic.jwt;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface DBUserRepository extends CrudRepository<DBUser,Long> {
    Optional<DBUser> findByName(String name);
}
