package ch.emedicus.poc.proxy;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;

import com.auth0.jwt.pem.X509CertUtils;

public class PublicKeyUtil {

	@Autowired
    private ResourceLoader resourceLoader;

	public String getPemFileContent(String keyPath) throws IOException {
		Validate.notEmpty(keyPath);
		return IOUtils.toString(
				resourceLoader.getResource(keyPath).getInputStream(), StandardCharsets.UTF_8);
	}
	
	public PublicKey createPublicKey(String keyPath) throws IOException {
		final X509Certificate cert = X509CertUtils.parse(getPemFileContent(keyPath));
        if (cert != null) {
            java.security.PublicKey publicKey = cert.getPublicKey();
            return publicKey;
        }
        return null;
	}
	
}
