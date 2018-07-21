package com.datastax.example.dao.accessor;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;
import com.datastax.example.model.User;

@Accessor
public interface UserAccessor {
    @Query("SELECT id,firstname,lastname FROM ks.user")
    Result<User> getAll();
    
    @Query("insert into user (id, firstname, lastname) values (?, ?, ?)")
    ResultSet insert(String id,String firstname, String lastname);
}