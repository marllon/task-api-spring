package com.marllon.task_api.config;

import com.google.cloud.secretmanager.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GcpSecretManager {

    @Value("${gcp.project-id}")
    private String projectId;

    public String getSecret(String secretId) {
        return getSecret(secretId, "latest");
    }

    public String getSecret(String secretId, String version) {
        try (SecretManagerServiceClient client = SecretManagerServiceClient.create()) {
            SecretVersionName secretVersionName = SecretVersionName.of(
                    projectId, secretId, version
            );
            AccessSecretVersionResponse response = client.accessSecretVersion(secretVersionName);
            ByteString payload = response.getPayload().getData();
            return payload.toStringUtf8();
        } catch (Exception e) {
            throw new RuntimeException("Failed to access secret: " + secretId, e);
        }
    }
}

