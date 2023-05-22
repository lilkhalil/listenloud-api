package com.lilkhalil.listenloud;

import static org.hamcrest.Matchers.notNullValue;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@ExtendWith(RestDocumentationExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GettingStartedTests {

	@Autowired
	private ObjectMapper objectMapper;

	private MockMvc mockMvc;

	@BeforeEach
	void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
				.apply(documentationConfiguration(restDocumentation))
				.alwaysDo(document("{method-name}/{step}/",
					preprocessRequest(prettyPrint()),
					preprocessResponse(prettyPrint())))
				.build();
	}
	
	@Test
	@Order(1)
	void index() throws Exception {
		this.mockMvc.perform(get("/api/v1").accept(MediaTypes.HAL_JSON))
			.andExpect(status().isOk());
		this.mockMvc.perform(get("/api/v1/auth").accept(MediaTypes.HAL_JSON))
			.andExpect(status().isOk());
		this.mockMvc.perform(get("/api/v1/music").accept(MediaTypes.HAL_JSON))
			.andExpect(status().isOk());
	}

	@Test
	@Order(2)
	void registerUser() throws Exception {
		Map<String, String> user = new HashMap<String, String>();
		user.put("username", "aidar4021");
		user.put("password", "123456789");

		this.mockMvc
				.perform(
						post("/api/v1/auth/register")
						.contentType(MediaTypes.HAL_JSON)
						.content(objectMapper.writeValueAsString(user)))
				.andExpect(status().isOk());
	}

	@Test
	@Order(3)
	void applicationTest() throws Exception {
		final String token = authenticateUser();
		final List<String> locations = Arrays.asList(
			createMusic(token, "Author - Song", "This song is just an example!"),
			createMusic(token, "LILKHALIL - Centaur's Aghanim", "This song is beautiful!"),
			createMusic(token, "KIZARU - Break Up", """
				В седьмой композиции альбома “Тебя любят там где меня нет”
				Кизару повествует своим слушателям историю разрыва отношений
				со своей, ныне, бывшей девушкой Валерией Липиной
					""")
		);
		getOneMusic(token, locations.get(0));
		getAllMusic(token);
		editMusic(token, locations.get(0), "Example - This song was edited", "This song is edited");
		deleteMusic(token, locations.get(2));
	}

	private String authenticateUser() throws Exception {
		Map<String, String> user = new HashMap<String, String>();
		user.put("username", "aidar4021");
		user.put("password", "123456789");

		String response = this.mockMvc.perform(
			post("/api/v1/auth/authenticate")
			.contentType(MediaTypes.HAL_JSON)
			.content(objectMapper.writeValueAsString(user))
		)
		.andExpect(status().isOk())
		.andReturn().getResponse().getContentAsString();
		JSONObject jsonObject = new JSONObject(response);
		String token = jsonObject.getString("access_token");
		return token;
	}

	private String createMusic(String token, String name, String description) throws Exception {
		Map<String, String> music = new HashMap<String, String>();
		music.put("name", name); // "Author - Song"
		music.put("description", description); // "This song is just an example!"
		music.put("image", "http://example.com/image.jpg");
		music.put("audio", "http://example.com/audio.mp3");
		String musicLocation = this.mockMvc
				.perform(
						post("/api/v1/music/add")
						.header("Authorization", "Bearer " + token)
						.contentType(MediaTypes.HAL_JSON)
						.content(objectMapper.writeValueAsString(music)))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", notNullValue()))
				.andExpect(jsonPath("$").doesNotExist())
				.andReturn().getResponse().getHeader("Location");
		return musicLocation;
	}

	private void getAllMusic(String token) throws Exception {
		this.mockMvc
				.perform(
						get("/api/v1/music/all")
						.header("Authorization", "Bearer " + token)
						.contentType(MediaTypes.HAL_JSON))
				.andExpect(status().isOk());
	}

	private void getOneMusic(String token, String location) throws Exception {
		this.mockMvc
		.perform(
				get(location)
				.header("Authorization", "Bearer " + token)
				.contentType(MediaTypes.HAL_JSON))
		.andExpect(status().isOk());
	}

	private void editMusic(String token, String location, String name, String description) throws Exception {
		Map<String, String> music = new HashMap<String, String>();
		music.put("name", name); // "Author - Song"
		music.put("description", description); // "This song is just an example!"
		music.put("image", "http://example.com/imageEdit.jpg");
		music.put("audio", "http://example.com/audioEdit.mp3");
		this.mockMvc
		.perform(
				put(location)
				.header("Authorization", "Bearer " + token)
				.contentType(MediaTypes.HAL_JSON)
				.content(objectMapper.writeValueAsString(music)))
		.andExpect(status().isOk());
	}

	private void deleteMusic(String token, String location) throws Exception {
		this.mockMvc
		.perform(
				delete(location)
				.header("Authorization", "Bearer " + token)
				.contentType(MediaTypes.HAL_JSON))
		.andExpect(status().isNoContent());
	}

}
