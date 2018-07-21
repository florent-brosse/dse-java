package com.datastax.example.dao;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.datastax.example.model.User;

@Repository
public class UserSearchDao {

	@Autowired
	@Qualifier("solrSession")
	private Session solrSession;

	private PreparedStatement solrPreparedStatement;

	@PostConstruct
	public void init() {
		solrPreparedStatement = solrSession.prepare("SELECT id,firstname,lastname from user where solr_query=?");
	}

	public List<User> search() {
		BoundStatement searchStatement = solrPreparedStatement.bind("*:*");
		return solrSession.execute(searchStatement).all().stream().map(r -> new User(r.getString("id"),r.getString("firstname"),r.getString("lastname"))).collect(Collectors.toList());
	}

}
