package com.tfg.terranostra.controllers.config;

import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PayPalConfig {

    @Value("${paypal.clientId}")
    private String clientId;

    @Value("${paypal.clientSecret}")
    private String clientSecret;

    @Value("${paypal.mode}")
    private String mode;

    /**
     * Configures PayPalEnvironment based on application.properties
     */
    @Bean
    public PayPalEnvironment payPalEnvironment() {
        if ("live".equalsIgnoreCase(mode)) {
            return new PayPalEnvironment.Live(clientId, clientSecret);
        } else {
            return new PayPalEnvironment.Sandbox(clientId, clientSecret);
        }
    }

    /**
     * Exposes a PayPalHttpClient bean for injection in services
     */
    @Bean
    public PayPalHttpClient payPalHttpClient(PayPalEnvironment environment) {
        return new PayPalHttpClient(environment);
    }
}
