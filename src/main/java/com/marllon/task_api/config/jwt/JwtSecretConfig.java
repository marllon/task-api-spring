package com.marllon.task_api.config.jwt;

import com.marllon.task_api.config.GcpSecretManager;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtSecretConfig {

    private final GcpSecretManager secretManager;

    @Value("${gcp.jwt.secret-id:jwt-secret}")
    private String secretId;

    @Getter
    private String jwtSecret;

    public JwtSecretConfig(GcpSecretManager secretManager) {
        this.secretManager = secretManager;
    }

    @PostConstruct
    public void init() {
        this.jwtSecret = secretManager.getSecret(secretId);
    }
}
