package com.terra_nostra.config;

import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de cliente PayPal para el entorno de Terra Nostra.
 * Permite alternar entre modo sandbox y producción (live) usando propiedades externas.
 */
@Configuration
public class PayPalConfig {

    @Value("${paypal.clientId}")
    private String clientId;

    @Value("${paypal.clientSecret}")
    private String clientSecret;

    @Value("${paypal.mode}")
    private String mode;

    /**
     * Bean del cliente HTTP de PayPal para realizar operaciones con la API.
     * El entorno se define mediante la propiedad 'paypal.mode' que puede ser 'sandbox' o 'live'.
     *
     * @return una instancia de PayPalHttpClient configurada.
     */
    @Bean
    public PayPalHttpClient payPalHttpClient() {
        PayPalEnvironment environment = "live".equalsIgnoreCase(mode)
                ? new PayPalEnvironment.Live(clientId, clientSecret)
                : new PayPalEnvironment.Sandbox(clientId, clientSecret);

        return new PayPalHttpClient(environment);
    }
}
