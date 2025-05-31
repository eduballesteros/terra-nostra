package com.terra_nostra.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.terra_nostra.dto.PedidoDto;
import com.terra_nostra.dto.ProductoPedidoDto;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PdfGenerator {

    // === PALETA DE COLORES ===
    private static final BaseColor COLOR_PRIMARY = new BaseColor(0x00, 0x33, 0x00);   // Verde oscuro
    private static final BaseColor COLOR_SECONDARY = new BaseColor(0x59, 0x79, 0x31); // Verde oliva
    private static final BaseColor COLOR_WARNING = new BaseColor(0xFF, 0x99, 0x00);   // Naranja
    private static final BaseColor COLOR_LIGHT = new BaseColor(0xF5, 0xF5, 0xF5);     // Gris claro

    public static ByteArrayOutputStream generarFacturaPdf(PedidoDto pedido) {
        Document document = new Document(PageSize.A4, 50, 50, 60, 50);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // === LOGO ===
            try {
                URL logoUrl = PdfGenerator.class.getClassLoader().getResource("logo.webp");
                if (logoUrl != null) {
                    Image logo = Image.getInstance(logoUrl);
                    logo.scaleToFit(120, 60);
                    logo.setAlignment(Image.ALIGN_LEFT);
                    document.add(logo);
                }
            } catch (Exception e) {
                System.err.println("⚠️ No se pudo cargar el logo.");
            }

            // === INFORMACIÓN DE EMPRESA Y CLIENTE ===
            Font infoFont = new Font(Font.FontFamily.HELVETICA, 11);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setSpacingAfter(20);
            infoTable.setWidths(new float[]{1, 1});

            PdfPCell empresa = new PdfPCell();
            empresa.setBorder(Rectangle.NO_BORDER);
            empresa.addElement(new Phrase("Terra Nostra", new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD, COLOR_PRIMARY)));
            empresa.addElement(new Phrase("info@terranostra.com", infoFont));
            empresa.addElement(new Phrase("www.terranostra.eduballesterosperez.com", infoFont));

            PdfPCell cliente = new PdfPCell();
            cliente.setBorder(Rectangle.NO_BORDER);
            cliente.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cliente.addElement(new Phrase("Factura para:", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, COLOR_WARNING)));
            cliente.addElement(new Phrase(pedido.getEmailUsuario(), infoFont));
            cliente.addElement(new Phrase(pedido.getDireccionEnvio(), infoFont));
            cliente.addElement(new Phrase("Fecha: " + pedido.getFecha().format(formatter), infoFont));

            infoTable.addCell(empresa);
            infoTable.addCell(cliente);
            document.add(infoTable);

            // === TABLA DE PRODUCTOS ===
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{4, 2, 2, 2});
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            Font headFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);

            addHeaderCell(table, "Producto", headFont, COLOR_SECONDARY);
            addHeaderCell(table, "Cantidad", headFont, COLOR_SECONDARY);
            addHeaderCell(table, "Precio unitario", headFont, COLOR_SECONDARY);
            addHeaderCell(table, "Subtotal", headFont, COLOR_SECONDARY);

            List<ProductoPedidoDto> productos = pedido.getProductos();
            for (ProductoPedidoDto prod : productos) {
                table.addCell(prod.getNombre());
                table.addCell(String.valueOf(prod.getCantidad()));
                table.addCell(String.format("%.2f €", prod.getPrecioUnitario()));
                double sub = prod.getCantidad() * prod.getPrecioUnitario();
                table.addCell(String.format("%.2f €", sub));
            }

            document.add(table);

            // === TOTALES CON IVA DESGLOSADO ===
            Font boldFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, COLOR_PRIMARY);
            BigDecimal total = pedido.getTotal();
            BigDecimal divisor = new BigDecimal("1.21");
            BigDecimal baseImponible = total.divide(divisor, 2, RoundingMode.HALF_UP);
            BigDecimal iva = total.subtract(baseImponible);


            PdfPTable totalTable = new PdfPTable(2);
            totalTable.setWidthPercentage(40);
            totalTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalTable.setSpacingBefore(10);
            totalTable.setWidths(new float[]{2, 1});

            addTotalRow(totalTable, "Base imponible:", baseImponible.doubleValue(), boldFont);
            addTotalRow(totalTable, "IVA (21%):", iva.doubleValue(), boldFont);
            addTotalRow(totalTable, "Total:", total.doubleValue(), boldFont);


            document.add(totalTable);

        } catch (Exception e) {
            throw new RuntimeException("Error al generar la factura PDF", e);
        } finally {
            document.close();
        }

        return outputStream;
    }

    private static void addHeaderCell(PdfPTable table, String text, Font font, BaseColor bgColor) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bgColor);
        cell.setPadding(8);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private static void addTotalRow(PdfPTable table, String label, double value, Font font) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, font));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        labelCell.setPadding(5);

        PdfPCell valueCell = new PdfPCell(new Phrase(String.format("%.2f €", value), font));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        valueCell.setPadding(5);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }
}
