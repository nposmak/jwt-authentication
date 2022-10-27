package com.nposmak.app.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@ConfigurationProperties(prefix ="rsa")
public record RsaProperties(RSAPrivateKey privateKey, RSAPublicKey publicKey) {

}
