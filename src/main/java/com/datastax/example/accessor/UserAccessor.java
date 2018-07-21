package com.datastax.example.accessor;

import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;
import com.datastax.example.model.User;

@Accessor
public interface UserAccessor {
    @Query("SELECT id,firstname,lastname FROM ks.user")
    Result<User> getAll();
}