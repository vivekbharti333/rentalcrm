package com.datfusrental.invoice;

import java.io.ByteArrayOutputStream;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.itextpdf.barcodes.BarcodeQRCode;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.datfusrental.entities.LeadDetails;
import com.itextpdf.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class ItextInvoice {

    public ByteArrayOutputStream invoice(LeadDetails leadDetails) throws Exception {
    	
    	 LocalDateTime currentDateTime = LocalDateTime.now();
         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
         String invoiceDate = currentDateTime.format(formatter);
        
    	// Create a ByteArrayOutputStream to hold the PDF data
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // Creating a PdfWriter to write to the ByteArrayOutputStream
        PdfWriter writer = new PdfWriter(byteArrayOutputStream);

        // Creating a PdfDocument object
        PdfDocument pdfDoc = new PdfDocument(writer);

        // Creating a Document object
        Document doc = new Document(pdfDoc, PageSize.A4);

        // Creating a table with two columns
        float[] pointColumnWidths = {150f, 600f};
        Table table = new Table(pointColumnWidths);

        // Adding an image to the table
        Cell cellLogo = new Cell();
        String imageFile = "D:\\logosamples.jpg";
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
        comName.add("Myrran Rentals And Adventures Goa Pvt. Ltd.").setFontSize(15).setTextAlignment(TextAlignment.RIGHT);

        Paragraph addLine1 = new Paragraph("12/1, Marinha Dourada Rd, Tamudki Vado,").setFontSize(10).setTextAlignment(TextAlignment.RIGHT);
        comName.add(addLine1);
        
        Paragraph addLine2 = new Paragraph("Arpora,Baga,Goa -403518").setFontSize(10).setTextAlignment(TextAlignment.RIGHT);
        comName.add(addLine2);
        
        Paragraph phoneNo = new Paragraph("Phone: +91 9513166378").setFontSize(10).setTextAlignment(TextAlignment.RIGHT);
        comName.add(phoneNo);
        
//        Paragraph gstin = new Paragraph("GSTIN: 29GGGGG1314R9Z6").setFontSize(10).setTextAlignment(TextAlignment.RIGHT);
//        comName.add(gstin);
        
        table.addCell(comName);
        doc.add(table);

        // Retrieve the current Y-position after adding the table
        float yPositionAfterTable = doc.getRenderer().getCurrentArea().getBBox().getBottom();

        // Get the current page to draw the line below the table
        PdfPage pdfPage = pdfDoc.getLastPage();
        PdfCanvas canvas = new PdfCanvas(pdfPage);

        // Draw a horizontal line just below the table
        canvas.moveTo(35, yPositionAfterTable + 650); // Starting point of the line (x, y)
        canvas.lineTo(555, yPositionAfterTable + 650); // Ending point of the line
        
//        canvas.moveTo(35, yPositionAfterTable + 645); // Starting point of the line (x, y)
//        canvas.lineTo(555, yPositionAfterTable + 645); // Ending point of the line
        canvas.stroke(); // Draw the line
        
        doc.add(new Paragraph("").setFontSize(20).setTextAlignment(TextAlignment.CENTER));

        doc.add(new Paragraph("PAYMENT RECEIPT").setFontSize(20)
//        		.setUnderline()
        		.setTextAlignment(TextAlignment.CENTER));
        
        doc.add(new Paragraph("").setFontSize(20).setTextAlignment(TextAlignment.CENTER));
        doc.add(new Paragraph("").setFontSize(20).setTextAlignment(TextAlignment.CENTER));
        doc.add(new Paragraph("").setFontSize(20).setTextAlignment(TextAlignment.CENTER));
        
        
        float [] pointColumnWidths0 = {400F,900F, 200F};       
        Table highLightTable = new Table(pointColumnWidths0);
        
        // Populating row 1 and adding it to the table    
        Cell empHighLight1 = new Cell();                        
        empHighLight1.add("Bill To,").setFontSize(12);                                      
        empHighLight1.setBorder(Border.NO_BORDER);        
        highLightTable.addCell(empHighLight1);
        
        Cell invoiceHighLight = new Cell();                        
        invoiceHighLight.add("Invoice No: ");                                 
        invoiceHighLight.setBackgroundColor(Color.WHITE);      
        invoiceHighLight.setBorder(Border.NO_BORDER);
        invoiceHighLight.setTextAlignment(TextAlignment.RIGHT).setFontSize(10);         
        highLightTable.addCell(invoiceHighLight);                            
        
        Cell invoiceValue = new Cell();                        
        invoiceValue.add("MYRAAG2024/09/987YU7");                                 
        invoiceValue.setBackgroundColor(Color.WHITE);      
        invoiceValue.setBorder(Border.NO_BORDER);
        invoiceValue.setTextAlignment(TextAlignment.RIGHT).setFontSize(10);         
        highLightTable.addCell(invoiceValue);
        
        Cell empHighLight2 = new Cell();                        
        empHighLight2.add(leadDetails.getCustomeName()).setFontSize(10);                                      
        empHighLight2.setBorder(Border.NO_BORDER);        
        highLightTable.addCell(empHighLight2);
        
        Cell dateHead = new Cell();                        
        dateHead.add("Date: ");                                 
        dateHead.setBackgroundColor(Color.WHITE);      
        dateHead.setBorder(Border.NO_BORDER);
        dateHead.setTextAlignment(TextAlignment.RIGHT).setFontSize(10);         
        highLightTable.addCell(dateHead);                            
        
        Cell dateValue = new Cell();                        
//        dateValue.add("30/09/2024");                                 
        dateValue.add(invoiceDate);                                 
        dateValue.setBackgroundColor(Color.WHITE);      
        dateValue.setBorder(Border.NO_BORDER);
        dateValue.setTextAlignment(TextAlignment.RIGHT).setFontSize(10);         
        highLightTable.addCell(dateValue);
        
        Cell empHighLight3 = new Cell();                        
        empHighLight3.add("Phone : "+leadDetails.getCountryDialCode()+" "+leadDetails.getCustomerMobile()).setFontSize(10);                                      
        empHighLight3.setBorder(Border.NO_BORDER);        
        highLightTable.addCell(empHighLight3);
        
        Cell paymentTermHead = new Cell();                        
        paymentTermHead.add("Payment Terms:");                                 
        paymentTermHead.setBackgroundColor(Color.WHITE);      
        paymentTermHead.setBorder(Border.NO_BORDER);
        paymentTermHead.setTextAlignment(TextAlignment.RIGHT).setFontSize(10);         
        highLightTable.addCell(paymentTermHead);                            
        
        Cell paymentTermValue = new Cell();                        
        paymentTermValue.add("PIA");                                 
        paymentTermValue.setBackgroundColor(Color.WHITE);      
        paymentTermValue.setBorder(Border.NO_BORDER);
        paymentTermValue.setTextAlignment(TextAlignment.RIGHT).setFontSize(10);         
        highLightTable.addCell(paymentTermValue);
        
        Cell empHighLight4 = new Cell();                        
        empHighLight4.add("");                                      
        empHighLight4.setBorder(Border.NO_BORDER);        
        highLightTable.addCell(empHighLight4);
        
        Cell balanceHead = new Cell();                        
        balanceHead.add("Balance Due:");                                 
        balanceHead.setBackgroundColor(Color.WHITE);      
        balanceHead.setBorder(Border.NO_BORDER);
        balanceHead.setTextAlignment(TextAlignment.RIGHT).setFontSize(10);         
        highLightTable.addCell(balanceHead);                            
        
        Cell balanceValue = new Cell();                        
        balanceValue.add("5900");                                 
        balanceValue.setBackgroundColor(Color.WHITE);      
        balanceValue.setBorder(Border.NO_BORDER);
        balanceValue.setTextAlignment(TextAlignment.RIGHT).setFontSize(10);         
        highLightTable.addCell(balanceValue);
        
        Cell empHighLight5 = new Cell();                        
        empHighLight5.add("");                                      
        empHighLight5.setBorder(Border.NO_BORDER);        
        highLightTable.addCell(empHighLight5);
        
        Cell balanceWithSecurityHead = new Cell();                        
        balanceWithSecurityHead.add("Balance Due With Security Deposit:");                                 
        balanceWithSecurityHead.setBackgroundColor(Color.WHITE);      
        balanceWithSecurityHead.setBorder(Border.NO_BORDER);
        balanceWithSecurityHead.setTextAlignment(TextAlignment.RIGHT).setFontSize(10);         
        highLightTable.addCell(balanceWithSecurityHead);                            
        
        Cell balanceWithSecurityValue = new Cell();                        
        balanceWithSecurityValue.add("5900");                                 
        balanceWithSecurityValue.setBackgroundColor(Color.WHITE);      
        balanceWithSecurityValue.setBorder(Border.NO_BORDER);
        balanceWithSecurityValue.setTextAlignment(TextAlignment.RIGHT).setFontSize(10);         
        highLightTable.addCell(balanceWithSecurityValue);
        
        doc.add(highLightTable);
        
        doc.add(new Paragraph(" "));
        doc.add(new Paragraph(" ")); 
        
        // Creating a table       
        float [] pointColumnWidths1 = {600F, 50F, 50F, 50F};       
        Table table1 = new Table(pointColumnWidths1);
        
        // Populating row 1 and adding it to the table               
        Cell c1 = new Cell();                        
        c1.add("Description");                                 
        c1.setBackgroundColor(new DeviceRgb(245, 153, 11));  // Mustard color
        c1.setBorder(new SolidBorder(Color.BLACK, 1));
        c1.setTextAlignment(TextAlignment.CENTER);         
        table1.addCell(c1);                          
        
        Cell c2 = new Cell();                        
        c2.add("NOD");                                 
        c2.setBackgroundColor(new DeviceRgb(245, 153, 11));      
        c2.setBorder(new SolidBorder(Color.BLACK, 1));
        c2.setTextAlignment(TextAlignment.CENTER);         
        table1.addCell(c2);                           
        
        Cell c3 = new Cell();                         
        c3.add("PDT");                                 
        c3.setBackgroundColor(new DeviceRgb(245, 153, 11));      
        c3.setBorder(new SolidBorder(Color.BLACK, 1));
        c3.setTextAlignment(TextAlignment.CENTER);        
        table1.addCell(c3);                            
        
        Cell c4 = new Cell();                        
        c4.add("Total");                                 
        c4.setBackgroundColor(new DeviceRgb(245, 153, 11));      
        c4.setBorder(new SolidBorder(Color.BLACK, 1));
        c4.setTextAlignment(TextAlignment.CENTER);         
        table1.addCell(c4);    
        
        Cell c5 = new Cell();                        
        c5.add("ERTIGA | Manual-SUV | 1 | 26/10/2024 & 9:00 Hours |\n 29/10/2024 & 8:00 Hour | Calangute & Madgaon railway station");    
//        c5.add("Vehicle Details : ERTIGA | Manual-SUV\n Quantity : 1 \n From Date & Time : 26/10/2024 | 9:00 Hours\n To Date & Time 29/10/2024 | 8:00 Hour \n Location :  Calangute & Madgaon railway station"); 
        c5.setBackgroundColor(new DeviceRgb(247, 245, 242));      
        c5.setBorder(new SolidBorder(Color.BLACK, 0));
        c5.setTextAlignment(TextAlignment.LEFT);         
        table1.addCell(c5);   
        
        Cell c6 = new Cell();                        
        c6.add("52");                                 
        c6.setBackgroundColor(new DeviceRgb(247, 245, 242));      
        c6.setBorder(new SolidBorder(Color.BLACK, 0));
        c6.setTextAlignment(TextAlignment.CENTER);         
        table1.addCell(c6);  
        
        Cell c7 = new Cell();                        
        c7.add("2");                                 
        c7.setBackgroundColor(new DeviceRgb(247, 245, 242));      
        c7.setBorder(new SolidBorder(Color.BLACK, 0));
        c7.setTextAlignment(TextAlignment.CENTER);         
        table1.addCell(c7); 
        
        Cell c8 = new Cell();                        
        c8.add("500");                                 
        c8.setBackgroundColor(new DeviceRgb(247, 245, 242));      
        c8.setBorder(new SolidBorder(Color.BLACK, 0));
        c8.setTextAlignment(TextAlignment.CENTER);         
        table1.addCell(c8);
        
        doc.add(table1);
        
        doc.add(new Paragraph(" "));
        doc.add(new Paragraph(" "));

        // Creating a table       
        float [] pointColumnWidths2 = {605F, 195F,};       
        Table table2 = new Table(pointColumnWidths2);
        
        // Populating row 1 and adding it to the table               
        Cell subtotalHeader = new Cell();                        
        subtotalHeader.add("Sub-total:");                                 
        subtotalHeader.setBackgroundColor(Color.WHITE);      
        subtotalHeader.setBorder(Border.NO_BORDER);
        subtotalHeader.setTextAlignment(TextAlignment.RIGHT).setFontSize(10)    ;     
        table2.addCell(subtotalHeader);    
        
        Cell subtotalValue = new Cell();                        
        subtotalValue.add("500");                                 
        subtotalValue.setBackgroundColor(Color.WHITE);      
        subtotalValue.setBorder(Border.NO_BORDER);
        subtotalValue.setTextAlignment(TextAlignment.CENTER).setFontSize(10);         
        table2.addCell(subtotalValue); 
        
        Cell discountHeader = new Cell();                        
        discountHeader.add("Discount:");                                 
        discountHeader.setBackgroundColor(Color.WHITE);      
        discountHeader.setBorder(Border.NO_BORDER);
        discountHeader.setTextAlignment(TextAlignment.RIGHT).setFontSize(10);     
        table2.addCell(discountHeader);    
        
        Cell discountValue = new Cell();                        
        discountValue.add("0.00");                                 
        discountValue.setBackgroundColor(Color.WHITE);      
        discountValue.setBorder(Border.NO_BORDER);
        discountValue.setTextAlignment(TextAlignment.CENTER).setFontSize(10);         
        table2.addCell(discountValue); 
        
        Cell taxHeader = new Cell();                        
        taxHeader.add("Tax:");                                 
        taxHeader.setBackgroundColor(Color.WHITE);      
        taxHeader.setBorder(Border.NO_BORDER);
        taxHeader.setTextAlignment(TextAlignment.RIGHT).setFontSize(10);  
        table2.addCell(taxHeader);    
        
        Cell taxtValue = new Cell();                        
        taxtValue.add("0.00");                                 
        taxtValue.setBackgroundColor(Color.WHITE);      
        taxtValue.setBorder(Border.NO_BORDER);
        taxtValue.setTextAlignment(TextAlignment.CENTER).setFontSize(10);         
        table2.addCell(taxtValue); 
        
        Cell deliveryHeader = new Cell();                        
        deliveryHeader.add("Delivery:");                                 
        deliveryHeader.setBackgroundColor(Color.WHITE);      
        deliveryHeader.setBorder(Border.NO_BORDER);
        deliveryHeader.setTextAlignment(TextAlignment.RIGHT).setFontSize(10);         
        table2.addCell(deliveryHeader);    
        
        Cell deliveryValue = new Cell();                        
        deliveryValue.add("0.00");                                 
        deliveryValue.setBackgroundColor(Color.WHITE);      
        deliveryValue.setBorder(Border.NO_BORDER);
        deliveryValue.setTextAlignment(TextAlignment.CENTER).setFontSize(10);         
        table2.addCell(deliveryValue); 
        
        Cell totalHeader = new Cell();                        
        totalHeader.add("Total:");                                 
        totalHeader.setBackgroundColor(Color.WHITE);      
        totalHeader.setBorder(Border.NO_BORDER);
        totalHeader.setTextAlignment(TextAlignment.RIGHT).setFontSize(10);         
        table2.addCell(totalHeader);    
        
        Cell totalValue = new Cell();                        
        totalValue.add("0.00");                                 
        totalValue.setBackgroundColor(Color.WHITE);      
        totalValue.setBorder(Border.NO_BORDER);
        totalValue.setTextAlignment(TextAlignment.CENTER).setFontSize(10);         
        table2.addCell(totalValue);
        
        Cell amountPaidHeader = new Cell();                        
        amountPaidHeader.add("Amount Paid:");                                 
        amountPaidHeader.setBackgroundColor(Color.WHITE);      
        amountPaidHeader.setBorder(Border.NO_BORDER);
        amountPaidHeader.setTextAlignment(TextAlignment.RIGHT).setFontSize(10);         
        table2.addCell(amountPaidHeader);    
        
        Cell amountPaidValue = new Cell();                        
        amountPaidValue.add("0.00");                                 
        amountPaidValue.setBackgroundColor(Color.WHITE);      
        amountPaidValue.setBorder(Border.NO_BORDER);
        amountPaidValue.setTextAlignment(TextAlignment.CENTER).setFontSize(10);         
        table2.addCell(amountPaidValue);
        
        Cell securityDepositHeader = new Cell();                        
        securityDepositHeader.add("Security Deposit:");                                 
        securityDepositHeader.setBackgroundColor(Color.WHITE);      
        securityDepositHeader.setBorder(Border.NO_BORDER);
        securityDepositHeader.setTextAlignment(TextAlignment.RIGHT).setFontSize(10);         
        table2.addCell(securityDepositHeader);    
        
        Cell securityDepositValue = new Cell();                        
        securityDepositValue.add("0.00");                                 
        securityDepositValue.setBackgroundColor(Color.WHITE);      
        securityDepositValue.setBorder(Border.NO_BORDER);
        securityDepositValue.setTextAlignment(TextAlignment.CENTER).setFontSize(10);         
        table2.addCell(securityDepositValue);
        
        doc.add(table2);
        
        doc.add(new Paragraph("\n\n"));
//        doc.add(new Paragraph(" ")); 

        float [] pointColumnWidthsNotes = {900F, };       
        Table notesTable = new Table(pointColumnWidthsNotes);
        
        // Populating row 1 and adding it to the table               
        Cell notes = new Cell();                        
        notes.add("Notes:").setFontSize(12); 
        notes.add("NOD : Number of days").setFontSize(10);  
        notes.add("PDT : Per day rent").setFontSize(10); 
        notes.setBackgroundColor(Color.WHITE);      
        notes.setBorder(Border.NO_BORDER);
        notes.setTextAlignment(TextAlignment.LEFT);     
        notesTable.addCell(notes); 
        
        doc.add(notesTable);
        
        doc.add(new Paragraph("\n\n"));
//        doc.add(new Paragraph(" "));
        
        float [] pointColumnWidthsTnC = {900F, };       
        Table tncTable = new Table(pointColumnWidthsTnC);
        
        Cell tnc = new Cell();                        
        tnc.add("TERM & CONDITIONS:").setFontSize(12);  
        tnc.add("https://myraanrentals.com/refund-and-cancellation").setFontSize(10);  
        tnc.setBackgroundColor(Color.WHITE);      
        tnc.setBorder(Border.NO_BORDER);
        tnc.setTextAlignment(TextAlignment.LEFT);     
        tncTable.addCell(tnc);
        
        doc.add(tncTable);
           
        doc.add(new Paragraph("\n\n"));
//        doc.add(new Paragraph(" "));   
        
        // Closing the document
        doc.close();

        System.out.println("Image added to table successfully with line drawn below.");
        return byteArrayOutputStream;
    }
}
