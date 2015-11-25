/*
Copyright 2015 pinniq

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package nl.pinniq.web.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import nl.pinniq.web.config.WebMvcConfiguration;

@WebAppConfiguration
@ContextConfiguration(classes = { WebMvcConfiguration.class })

@RunWith(SpringJUnit4ClassRunner.class)
public class ViewControllerTest {
	
	@Autowired
	private WebApplicationContext context;
	private MockMvc mvc;
	
	@Before
	public void setup() {
		mvc = MockMvcBuilders
				.webAppContextSetup(context)
				.build();
		
	}
	
	@Test
    public void testGetRoot() throws Exception {
       mvc.perform(get("/"))
				.andDo(print())
				.andExpect(forwardedUrl("/WEB-INF/index.jsp"))
				.andExpect(status().is(200));
    }
	
	@Test
    public void testGetIndex() throws Exception {
       mvc.perform(get("/index"))
				.andDo(print())
				.andExpect(forwardedUrl("/WEB-INF/index.jsp"))
				.andExpect(status().is(200));
    }
	
	@Test
    public void testGetBlaat() throws Exception {
       mvc.perform(get("/blaat"))
				.andDo(print())
				.andExpect(status().is(404));
    }

}
