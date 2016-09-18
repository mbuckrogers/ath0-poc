package ch.emedicus.poc.proxy.config;

import com.auth0.jwt.Algorithm;
import com.auth0.spring.security.api.Auth0AuthenticationProvider;
import com.auth0.spring.security.api.Auth0AuthorityStrategy;

public class GatewayAuthenticationProvider extends Auth0AuthenticationProvider {
	
	public GatewayAuthenticationProvider(
			String domain, String issuer, String clientId,
			String clientSecret, String securedRoute,
			Auth0AuthorityStrategy authorityStrategy, boolean base64EncodedSecret,
			Algorithm signingAlgorithm, String publicKeyPath
			)

	{
		setDomain(domain);
		setIssuer(issuer);
		setClientId(clientId);
		setClientSecret(clientSecret);
		setSecuredRoute(securedRoute);
		setAuthorityStrategy(authorityStrategy.getStrategy());
		setBase64EncodedSecret(base64EncodedSecret);
		setSigningAlgorithm(signingAlgorithm);
		setPublicKeyPath(publicKeyPath);
	}
}
