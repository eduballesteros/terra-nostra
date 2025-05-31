package com.tfg.terranostra.services;

import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.http.exceptions.HttpException;
import com.paypal.orders.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class PayPalService {

    private static final Logger logger = LoggerFactory.getLogger(PayPalService.class);
    private final PayPalHttpClient client;

    public PayPalService(PayPalHttpClient client) {
        this.client = client;
    }

    /**
     * Crea una orden de prueba de 1.00 EUR con intención de captura inmediata.
     * @return el objeto Order devuelto por PayPal
     * @throws IOException si hay un error de E/S al comunicarse con PayPal
     * @throws HttpException si PayPal devuelve un error HTTP
     */
    public Order createTestOrder() throws IOException, HttpException {
        OrdersCreateRequest request = new OrdersCreateRequest();
        request.prefer("return=representation");
        request.requestBody(
                new OrderRequest()
                        .checkoutPaymentIntent("CAPTURE")
                        .purchaseUnits(List.of(
                                new PurchaseUnitRequest()
                                        .amountWithBreakdown(
                                                new AmountWithBreakdown()
                                                        .currencyCode("EUR")
                                                        .value("1.00")
                                        )
                        ))
        );

        HttpResponse<Order> response = client.execute(request);

        // Log de la respuesta
        logger.info("Código de Estado: {}", response.statusCode());
        Order order = response.result();
        logger.info("ID de la Orden: {}", order.id());
        logger.info("Intención: {}", order.checkoutPaymentIntent());
        order.links().forEach(link ->
                logger.info("Link [rel: {}, href: {}, method: {}]", link.rel(), link.href(), link.method())
        );

        return order;
    }

    /**
     * Captura una orden existente
     * @param orderId ID de la orden a capturar
     * @return el objeto Order con el estado capturado
     * @throws IOException si hay un error de E/S
     * @throws HttpException si PayPal devuelve un error HTTP
     */
    public Order captureOrder(String orderId) throws IOException, HttpException {
        OrdersCaptureRequest request = new OrdersCaptureRequest(orderId);
        request.requestBody(new OrderRequest());
        HttpResponse<Order> response = client.execute(request);
        return response.result();
    }
}
