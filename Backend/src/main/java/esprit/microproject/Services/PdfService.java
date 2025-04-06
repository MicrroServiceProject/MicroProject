package esprit.microproject.Services;

import com.lowagie.text.*; // OpenPDF imports
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import esprit.microproject.Entities.Order;
import esprit.microproject.Entities.OrderItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@Service
public class PdfService {

    private static final Logger logger = LoggerFactory.getLogger(PdfService.class);

    public byte[] generateInvoicePdf(Order order) {
        logger.info("Generating PDF invoice for Order ID: {}", order.getId());
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            // --- Basic Invoice Structure ---
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

            // Title
            Paragraph title = new Paragraph("Invoice", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE); // Add some space

            // Order Details
            document.add(new Paragraph("Order ID: " + order.getId(), normalFont));
            document.add(new Paragraph("Order Date: " + order.getOrderDate().format(DateTimeFormatter.ISO_DATE), normalFont));
            document.add(new Paragraph("Customer: " + order.getUser().getUsername(), normalFont));
            // Add billing/shipping address if available on User entity
            document.add(Chunk.NEWLINE);

            // Items Table
            PdfPTable table = new PdfPTable(4); // 4 columns: Product, Quantity, Price, Subtotal
            table.setWidthPercentage(100);
            table.setWidths(new float[]{4f, 1f, 2f, 2f}); // Relative widths

            // Table Header
            PdfPCell cell = new PdfPCell(new Phrase("Product", headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Quantity", headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Unit Price", headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Subtotal", headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);

            // Table Body
            for (OrderItem item : order.getOrderItems()) {
                table.addCell(new Phrase(item.getProduct().getName(), normalFont));
                PdfPCell qtyCell = new PdfPCell(new Phrase(String.valueOf(item.getQuantity()), normalFont));
                qtyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(qtyCell);
                PdfPCell priceCell = new PdfPCell(new Phrase("$" + item.getPrice().toPlainString(), normalFont)); // Assuming USD
                priceCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(priceCell);
                BigDecimal subtotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                PdfPCell subtotalCell = new PdfPCell(new Phrase("$" + subtotal.toPlainString(), normalFont));
                subtotalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(subtotalCell);
            }
            document.add(table);
            document.add(Chunk.NEWLINE);

            // Total Amount
            Paragraph total = new Paragraph("Total Amount: $" + order.getTotalAmount().toPlainString(), headerFont);
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);

            // --- End of Invoice Structure ---

            document.close();
            logger.info("PDF invoice generated successfully for Order ID: {}", order.getId());
            return baos.toByteArray();

        } catch (DocumentException e) {
            logger.error("Error generating PDF for Order ID {}: {}", order.getId(), e.getMessage());
            // Handle exception appropriately - maybe return null or throw a custom exception
            return null;
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
    }
}