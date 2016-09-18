package ch.emedicus.poc.proxy.domain;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TokenDataTest {
	
	@Test
	public void shouldSerialize() throws Exception {
		
		TokenData td = new TokenData();
		td.setId_token("id.token");
		td.setRefreshToken("refresh.token");
		
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(td);
		System.out.println(json);
		
		
	}

}
