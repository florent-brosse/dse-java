package com.datastax.example.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.datastax.driver.mapping.Mapper;
import com.datastax.example.dao.UserSearchDao;
import com.datastax.example.dao.accessor.UserAccessor;
import com.datastax.example.model.User;

@RestController
public class HomeController {
	
	@Autowired
	private UserAccessor userAccessor;
	
	@Autowired
	private Mapper<User> userMapper;
	
	@Autowired
	private UserSearchDao userdao;
	
	@RequestMapping("/")
    public ResponseEntity<?> home() {
		return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).header(HttpHeaders.LOCATION, "/users").build();
    }

    @RequestMapping("/users")
    public List<User> users() {
        return userAccessor.getAll().all();
    }
    
    @RequestMapping("/users2")
    public List<User> users2() {
        return userdao.search();
    }
        
    @PostMapping("/users")
    public void createUser(@RequestBody User user) {
        userMapper.save(user);
    }
    
    @PostMapping("/users2")
    public void createUser2(@RequestBody User user) {
    	userAccessor.insert(user.getId(),user.getFirstname(),user.getLastname());
    }
    
    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable String id) {
    	userMapper.delete(new User(id));
    }
}