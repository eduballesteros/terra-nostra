package com.terra_nostra.service;

import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class PayPalService {

    @Autowired
    private PayPalHttpClient payPalHttpClient;

    @Value("${paypal.return.url}")
    private String returnUrl;

    @Value("${paypal.cancel.url}")
    private String cancelUrl;

    /**
     * Crea una orden de PayPal y devuelve la URL de aprobación
     * @param total Monto total del pedido (formato "12.50")
     * @return URL para redirigir al usuario a PayPal
     * @throws IOException si falla la comunicación con PayPal
     */
    public String crearOrden(double total) throws IOException {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("CAPTURE");

        ApplicationContext applicationContext = new ApplicationContext()
                .brandName("Terra Nostra")
                .landingPage("BILLING")
                .cancelUrl(cancelUrl)
                .returnUrl(returnUrl)
                .userAction("PAY_NOW");

        orderRequest.applicationContext(applicationContext);

        AmountWithBreakdown amount = new AmountWithBreakdown()
                .currencyCode("EUR")
                .value(String.format(java.util.Locale.US, "%.2f", total)); // ✅ punto decimal

        PurchaseUnitRequest purchaseUnit = new PurchaseUnitRequest()
                .amountWithBreakdown(amount);

        orderRequest.purchaseUnits(List.of(purchaseUnit));

        OrdersCreateRequest request = new OrdersCreateRequest();
        request.requestBody(orderRequest);

        HttpResponse<Order> response = payPalHttpClient.execute(request);

        return response.result().links().stream()
                .filter(link -> "approve".equals(link.rel()))
                .findFirst()
                .map(LinkDescription::href)
                .orElseThrow(() -> new IllegalStateException("No se pudo obtener la URL de aprobación de PayPal"));
    }


    /**
     * Captura una orden ya aprobada por el usuario
     * @param orderId ID de la orden PayPal (token)
     * @return Objeto Order con los detalles de la captura
     * @throws IOException si falla la comunicación con PayPal
     */
    public Order capturarPago(String orderId) throws IOException {
        OrdersCaptureRequest request = new OrdersCaptureRequest(orderId);
        request.requestBody(null); // cuerpo vacío es válido
        HttpResponse<Order> response = payPalHttpClient.execute(request);
        return response.result();
    }
}
