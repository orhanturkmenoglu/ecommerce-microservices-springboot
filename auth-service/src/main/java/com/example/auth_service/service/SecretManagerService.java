package com.example.auth_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.*;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SecretManagerService {


    private final SecretsManagerClient secretsManagerClient;

    @Cacheable(value = "secrets", key = "#secretName")
    public String getSecret (String secretName){
        GetSecretValueRequest request = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        try {
            GetSecretValueResponse response = secretsManagerClient.getSecretValue(request);
            return response.secretString();
        }  catch (SecretsManagerException e) {
            throw new RuntimeException("AWS Secrets Manager hatası: " + e.awsErrorDetails().errorMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Beklenmeyen bir hata oluştu: " + e.getMessage(), e);
        }
    }

    public List<String> getAllSecrets() {
        try {
            ListSecretsRequest listSecretsRequest = ListSecretsRequest.builder().build();
            ListSecretsResponse listSecretsResponse = secretsManagerClient.listSecrets(listSecretsRequest);

            return listSecretsResponse.secretList()
                    .stream()
                    .map(SecretListEntry::name)
                    .toList();
        }catch (InvalidParameterException e) {
            throw new RuntimeException("Invalid parameter provided to AWS Secrets Manager: " + e.getMessage(), e);
        } catch (InvalidRequestException e) {
            throw new RuntimeException("The request to AWS Secrets Manager is invalid: " + e.getMessage(), e);
        } catch (InvalidNextTokenException e) {
            throw new RuntimeException("Invalid pagination token provided: " + e.getMessage(), e);
        } catch (InternalServiceErrorException e) {
            throw new RuntimeException("Internal error occurred within AWS Secrets Manager: " + e.getMessage(), e);
        } catch (AwsServiceException e) {
            throw new RuntimeException("AWS service exception occurred: " + e.awsErrorDetails().errorMessage(), e);
        } catch (SdkClientException e) {
            throw new RuntimeException("Client-side error occurred while communicating with AWS: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while listing secrets: " + e.getMessage(), e);
        }
    }
}
