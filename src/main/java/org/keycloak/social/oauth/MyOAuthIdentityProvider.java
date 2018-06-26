/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.keycloak.social.oauth;

import org.keycloak.broker.oidc.AbstractOAuth2IdentityProvider;
import org.keycloak.broker.oidc.OAuth2IdentityProviderConfig;
import org.keycloak.broker.oidc.mappers.AbstractJsonUserAttributeMapper;
import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.broker.provider.IdentityBrokerException;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.broker.social.SocialIdentityProvider;
import org.keycloak.events.EventBuilder;
import org.keycloak.models.KeycloakSession;

import com.fasterxml.jackson.databind.JsonNode;

public class MyOAuthIdentityProvider extends AbstractOAuth2IdentityProvider implements SocialIdentityProvider {

	public static final String DEFAULT_SCOPE = "";

	public MyOAuthIdentityProvider(KeycloakSession session, OAuth2IdentityProviderConfig config) {
		super(session, config);
	}

	@Override
	protected boolean supportsExternalExchange() {
		return true;
	}
	
	@Override
	protected BrokeredIdentityContext extractIdentityFromProfile(EventBuilder event, JsonNode profile) {
		BrokeredIdentityContext user = new BrokeredIdentityContext(getJsonProperty(profile, "id"));

		String username = getJsonProperty(profile, "username");
		user.setUsername(username);

		logger.error(profile.get("attributes").asText());
		JsonNode emailNode = profile.get("attributes").get("email");
		JsonNode firstnameNode = profile.get("attributes").get("given-name");
		JsonNode lastnameNode = profile.get("attributes").get("family-name");

		String email = getJsonAttribute(emailNode);
		String firstName = getJsonAttribute(firstnameNode);
		String lastName = getJsonAttribute(lastnameNode);
		
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setEmail(email);

		user.setIdpConfig(getConfig());
		user.setIdp(this);

		AbstractJsonUserAttributeMapper.storeUserProfileForMapper(user, profile, getConfig().getAlias());

		return user;

	}

	private String getJsonAttribute(JsonNode node) {
		if (node.isArray()) {
			// return first value
			if (node.iterator().hasNext()) {
				return node.iterator().next().asText();
			}
		}
		return node.asText();
	}


	@Override
	protected BrokeredIdentityContext doGetFederatedIdentity(String accessToken) {
	
		try {
			JsonNode profile = SimpleHttp.doGet(getConfig().getUserInfoUrl(), session)
					.header("Authorization", "Bearer " + accessToken)
					.asJson();

			BrokeredIdentityContext user = extractIdentityFromProfile(null, profile);

			return user;
		} catch (Exception e) {
			throw new IdentityBrokerException("Could not obtain user profile from Oauth server.", e);
		}
	}

	@Override
	protected String getDefaultScopes() {
		return DEFAULT_SCOPE;
	}
}
