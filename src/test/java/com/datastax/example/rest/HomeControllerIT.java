package com.datastax.example.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.datastax.driver.dse.DseSession;
import com.datastax.example.Application;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@ActiveProfiles("IT")
public class HomeControllerIT {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	@Qualifier("defaultSession")
	private DseSession session;
	
	@Before
	public void init() {
		session.execute("TRUNCATE ks.user");
		session.execute("INSERT INTO ks.user (id , firstname , lastname ) VALUES ( '1','John','Doe')");
		session.execute("COMMIT SEARCH INDEX ON ks.user");
	}

	@Test
	public void getUser() throws Exception {
		assertThat(this.mockMvc.perform(get("/users")).andDo(print()).andExpect(status().isOk())
				.andExpect(content().json("[{\"id\":\"1\",\"firstname\":\"John\",\"lastname\":\"Doe\",\"login\":null}]")))	;
	}
	
	@Test
	public void getUser2() throws Exception {
		assertThat(this.mockMvc.perform(get("/users2")).andDo(print()).andExpect(status().isOk())
				.andExpect(content().json("[{\"id\":\"1\",\"firstname\":\"John\",\"lastname\":\"Doe\",\"login\":null}]")))	;
	}

}