package com.terra_nostra.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.terra_nostra.dto.PedidoDto;
import com.terra_nostra.utils.PdfGenerator;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class FacturaService {

    private static final String API_URL = "http://terraapi:8080/api/pedidos/";


    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private JavaMailSender mailSender;

    public FacturaService() {
        mapper.registerModule(new JavaTimeModule());
    }

    public void enviarFacturaPorCorreo(Long pedidoId, String emailDestino) {
        try {
            // 1. Obtener el pedido desde la API
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + pedidoId))
                    .GET()
                    .build();

            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                throw new RuntimeException("❌ Error al obtener el pedido: " + response.body());
            }

            PedidoDto pedido = mapper.readValue(response.body(), PedidoDto.class);

            // 2. Generar el PDF
            ByteArrayOutputStream pdf = PdfGenerator.generarFacturaPdf(pedido);

            // 3. Enviar el correo
            String asunto = "🧾 Tu factura Terra Nostra está lista";
            String mensaje = """
            Hola,
            
            🎉 ¡Gracias por confiar en Terra Nostra!
            
            Adjuntamos en este correo la factura de tu compra, donde encontrarás el detalle completo de tu pedido: productos adquiridos, cantidades, precios unitarios y el desglose de impuestos aplicados.
            
            📎 Puedes encontrar tu factura en formato PDF adjunta a este mensaje.
            
            Si tienes alguna duda o necesitas más información, estaremos encantados de ayudarte. Responde a este correo o visítanos en nuestra web.
            
            Un cordial saludo,  
            🌿 Equipo Terra Nostra
            """;


            enviarCorreo(emailDestino, asunto, mensaje, pdf.toByteArray());


        } catch (Exception e) {
            throw new RuntimeException("❌ Error al enviar la factura", e);
        }
    }

    private void enviarCorreo(String destinatario, String asunto, String mensaje, byte[] archivoPdf) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(mensaje, false);
            helper.addAttachment("factura.pdf", new ByteArrayResource(archivoPdf));

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("❌ Error al enviar el correo con PDF", e);
        }
    }
}
