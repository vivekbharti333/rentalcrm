package com.datfusrental.invoice;

import org.springframework.stereotype.Component;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.element.LineSeparator;

@Component
public class ItextInvoice {

    public void invoice() throws Exception {
        // Creating a PdfWriter
        String dest = "D:\\sample.pdf";
        PdfWriter writer = new PdfWriter(dest);

        // Creating a PdfDocument object
        PdfDocument pdfDoc = new PdfDocument(writer);

        // Creating a Document object
        Document doc = new Document(pdfDoc);

        // Creating a table with two columns
        float[] pointColumnWidths = {150f, 300f};
        Table table = new Table(pointColumnWidths);

        // Adding an image to the table
        Cell cellLogo = new Cell();
        String imageFile = "D:\\download.jpg";
        ImageData data = ImageDataFactory.create(imageFile);
        Image img = new Image(data);
        img.setWidth(100);  // Set the desired width of the image
        img.setHeight(50);  // Set the desired height of the image

        cellLogo.setBorder(Border.NO_BORDER);
        cellLogo.add(img.setAutoScale(true));
        table.addCell(cellLogo);

        // Adding a company name and subtitle to the table
        Cell comName = new Cell();
        comName.setBorder(Border.NO_BORDER);
        comName.add("Icon").setFontSize(40).setTextAlignment(TextAlignment.RIGHT);

        Paragraph subTitle = new Paragraph("Your Subtitle Here")
                .setFontSize(10)
                .setTextAlignment(TextAlignment.RIGHT);
        comName.add(subTitle);

        table.addCell(comName);

        // Adding the table to the document
        doc.add(table);

        // Retrieve the current Y-position after adding the table
        float yPositionAfterTable = doc.getRenderer().getCurrentArea().getBBox().getBottom();

        // Get the current page to draw the line below the table
        PdfPage pdfPage = pdfDoc.getLastPage();
        PdfCanvas canvas = new PdfCanvas(pdfPage);

        // Draw a horizontal line just below the table
        canvas.moveTo(100, yPositionAfterTable + 200); // Starting point of the line (x, y)
        canvas.lineTo(550, yPositionAfterTable - 10); // Ending point of the line
        canvas.stroke(); // Draw the line

        // Closing the document
        doc.close();

        System.out.println("Image added to table successfully with line drawn below.");
    }
}
