package ch.emedicus.poc.util;

import java.io.IOException;
import java.security.PublicKey;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.emedicus.poc.proxy.PublicKeyUtil;
import ch.emedicus.poc.util.PermReaderTest.TestConfig;
import junit.framework.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class PermReaderTest {
	
	@Autowired
	PublicKeyUtil auth0Resource;
	
	@Test
	public void shouldCreatePublicKey() throws IOException {
		PublicKey  key = auth0Resource.createPublicKey("classpath:certificate/cert.pem");
		Assert.assertNotNull(key);
	}

	
	@Configuration
	public static class TestConfig {
		@Bean
		public PublicKeyUtil auth0Resource() {
			PublicKeyUtil resource = new PublicKeyUtil();
			return resource;
		}
		
	}
}
