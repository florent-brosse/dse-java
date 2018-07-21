package com.datastax.example.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.datastax.example.accessor.UserAccessor;
import com.datastax.example.model.User;

@RestController
public class HomeController {
	
	@Autowired
	private UserAccessor userAccessor;

    @RequestMapping("/")
    List<User> home() {
        return userAccessor.getAll().all();
    }
}