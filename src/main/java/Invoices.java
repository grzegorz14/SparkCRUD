import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.awt.*;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class Invoices {

  static final Font font = FontFactory.getFont(FontFactory.HELVETICA, 16, BaseColor.BLACK);
  static final Font modelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, BaseColor.BLACK);
  static final Font redFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.RED);

  public static void generateInvoiceForSingleCar(Car car) {
    Document document = new Document();
    String path = "invoices/" + car.uuid + ".pdf";
    try {
      PdfWriter.getInstance(document, new FileOutputStream(path));
    }
    catch(Exception ex) {
      System.out.println(ex);
    }

    document.open();

    Invoice invoice = new Invoice(new Car[]{car});

    Color carColor = Helpers.hexToColor(car.color);
    Font colorFont = FontFactory.getFont(FontFactory.HELVETICA, 18, new BaseColor(carColor.getRed(),
                                                                                  carColor.getGreen(),
                                                                                  carColor.getBlue()));

    Paragraph numberP = new Paragraph("INVOICE for: " + car.uuid, modelFont);
    Paragraph modelP = new Paragraph("model: " + car.model, font);
    Paragraph colorP = new Paragraph("color: " + car.color, colorFont);
    Paragraph yearP = new Paragraph("year: " + car.date.getYear(), font);
    Paragraph driverP = new Paragraph("airbag: driver - " + car.airbags[0].value, font);
    Paragraph passengerP = new Paragraph("airbag: passenger - " + car.airbags[1].value, font);
    Paragraph backSeatsP = new Paragraph("airbag: backSeats - " + car.airbags[2].value, font);
    Paragraph sidesP = new Paragraph("airbag: sides - " + car.airbags[3].value, font);

    Paragraph price = new Paragraph("price: " + String.format("%.02f", invoice.getPrice()) + "$", font);
    Paragraph vat = new Paragraph("VAT: " + car.vat.getValue() + "%", font);
    Paragraph priceWithVAT = new Paragraph("price with VAT: " + String.format("%.02f", invoice.getPriceWithVAT()) + "$", modelFont);

    try {
      Image carImage;
      if(Arrays.stream(Car.models).anyMatch(model -> model.equals(car.model))) {
        carImage = Image.getInstance("src/main/resources/public/images/" + car.model + ".png");
      }
      else {
        carImage = Image.getInstance("src/main/resources/public/images/car.png");
      }
      carImage.scaleAbsolute(400, 250);

      document.add(numberP);
      document.add(modelP);
      document.add(colorP);
      document.add(yearP);
      document.add(driverP);
      document.add(passengerP);
      document.add(backSeatsP);
      document.add(sidesP);

      document.add(carImage);

      document.add(price);
      document.add(vat);
      document.add(priceWithVAT);
    }
    catch(Exception ex) {
      System.out.println("EXCEPTION: " + ex);
    }
    document.close();
  }

  public static String generateInvoiceForAll() {
    Document document = new Document();

    String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_hh_mm_ss"));
    String path = "invoices/invoice_for_all_" + dateTime + ".pdf";

    try {
      PdfWriter.getInstance(document, new FileOutputStream(path));
    }
    catch(Exception ex) {
      System.out.println(ex);
    }

    document.open();

    Invoice invoice = new Invoice(App.cars.toArray(new Car[0]));
    
    
    Paragraph title = new Paragraph("INVOICE: VAT_" + dateTime, modelFont);
    Paragraph buyer = new Paragraph("Buyer: " + invoice.buyer, font);
    Paragraph seller = new Paragraph("Seller: " + invoice.seller, font);
    Paragraph description = new Paragraph("Invoice for all cars", redFont);
    Paragraph empty = new Paragraph(" ");

    PdfPTable table = new PdfPTable(4);

    table.addCell(new PdfPCell(new Phrase("id", font)));
    table.addCell(new PdfPCell(new Phrase("price", font)));
    table.addCell(new PdfPCell(new Phrase("VAT", font)));
    table.addCell(new PdfPCell(new Phrase("value", font)));

    float sum = 0f;

    for(Car car : App.cars) {
      table.addCell(new PdfPCell(new Phrase(String.valueOf(car.id), font)));
      table.addCell(new PdfPCell(new Phrase((int) car.price + "$", font)));
      table.addCell(new PdfPCell(new Phrase(String.valueOf(car.vat.getValue()), font)));
      table.addCell(new PdfPCell(new Phrase((int) car.getPriceWithVat() + "$", font)));

      sum += car.getPriceWithVat();
    }

    Paragraph price = new Paragraph("PRICE for all with VAT:" + (int) sum + "$", modelFont);

    try {
      document.add(title);
      document.add(buyer);
      document.add(seller);
      document.add(description);
      document.add(empty);

      document.add(table);

      document.add(price);
    }
    catch(Exception ex) {
      System.out.println("EXCEPTION: " + ex);
    }
    document.close();

    return dateTime;
  }

  public static String generateInvoiceForYear(String year) {
    Document document = new Document();

    String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_hh_mm_ss"));
    String path = "invoices/invoice_for_year_" + dateTime + ".pdf";

    try {
      PdfWriter.getInstance(document, new FileOutputStream(path));
    }
    catch(Exception ex) {
      System.out.println(ex);
    }

    document.open();

    Invoice invoice = new Invoice(App.cars.toArray(new Car[0]));

    Paragraph title = new Paragraph("INVOICE: VAT_" + dateTime, modelFont);
    Paragraph buyer = new Paragraph("Buyer: " + invoice.buyer, font);
    Paragraph seller = new Paragraph("Seller: " + invoice.seller, font);
    Paragraph description = new Paragraph("Invoice for year: " + year, redFont);
    Paragraph empty = new Paragraph(" ");

    PdfPTable table = new PdfPTable(5);

    table.addCell(new PdfPCell(new Phrase("id", font)));
    table.addCell(new PdfPCell(new Phrase("year", font)));
    table.addCell(new PdfPCell(new Phrase("price", font)));
    table.addCell(new PdfPCell(new Phrase("VAT", font)));
    table.addCell(new PdfPCell(new Phrase("value", font)));

    float sum = 0f;

    List<Car> carsFromYear = App.cars.stream().filter(c -> c.date.getYear() == Integer.parseInt(year)).toList();

    for(Car car : carsFromYear) {
      table.addCell(new PdfPCell(new Phrase(String.valueOf(car.id), font)));
      table.addCell(new PdfPCell(new Phrase(String.valueOf(car.date.getYear()), font)));
      table.addCell(new PdfPCell(new Phrase((int) car.price + "$", font)));
      table.addCell(new PdfPCell(new Phrase(String.valueOf(car.vat.getValue()), font)));
      table.addCell(new PdfPCell(new Phrase((int) car.getPriceWithVat() + "$", font)));

      sum += car.getPriceWithVat();
    }

    Paragraph price = new Paragraph("PRICE for year with VAT:" + (int) sum + "$", modelFont);

    try {
      document.add(title);
      document.add(buyer);
      document.add(seller);
      document.add(description);
      document.add(empty);

      document.add(table);

      document.add(price);
    }
    catch(Exception ex) {
      System.out.println("EXCEPTION: " + ex);
    }
    document.close();

    return dateTime;
  }

  public static String generateInvoiceForPriceRange(float min, float max) {
    Document document = new Document();

    String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_hh_mm_ss"));
    String path = "invoices/invoice_for_price_range_" + dateTime + ".pdf";

    try {
      PdfWriter.getInstance(document, new FileOutputStream(path));
    }
    catch(Exception ex) {
      System.out.println(ex);
    }

    document.open();

    Invoice invoice = new Invoice(App.cars.toArray(new Car[0]));

    Paragraph title = new Paragraph("INVOICE: VAT_" + dateTime, modelFont);
    Paragraph buyer = new Paragraph("Buyer: " + invoice.buyer, font);
    Paragraph seller = new Paragraph("Seller: " + invoice.seller, font);
    Paragraph description = new Paragraph("Invoice for price range: " + (int) min + "-" + (int) max + " $", redFont);
    Paragraph empty = new Paragraph(" ");

    PdfPTable table = new PdfPTable(5);

    table.addCell(new PdfPCell(new Phrase("id", font)));
    table.addCell(new PdfPCell(new Phrase("year", font)));
    table.addCell(new PdfPCell(new Phrase("price", font)));
    table.addCell(new PdfPCell(new Phrase("VAT", font)));
    table.addCell(new PdfPCell(new Phrase("value", font)));

    float sum = 0f;

    List<Car> carsFromPriceRange = App.cars.stream().filter(c -> c.price >= min && c.price <= max).toList();

    for(Car car : carsFromPriceRange) {
      table.addCell(new PdfPCell(new Phrase(String.valueOf(car.id), font)));
      table.addCell(new PdfPCell(new Phrase(String.valueOf(car.date.getYear()), font)));
      table.addCell(new PdfPCell(new Phrase((int) car.price + "$", font)));
      table.addCell(new PdfPCell(new Phrase(String.valueOf(car.vat.getValue()), font)));
      table.addCell(new PdfPCell(new Phrase((int) car.getPriceWithVat() + "$", font)));

      sum += car.getPriceWithVat();
    }

    Paragraph price = new Paragraph("PRICE for price range with VAT:" + (int) sum + "$", modelFont);

    try {
      document.add(title);
      document.add(buyer);
      document.add(seller);
      document.add(description);
      document.add(empty);

      document.add(table);

      document.add(price);
    }
    catch(Exception ex) {
      System.out.println("EXCEPTION: " + ex);
    }
    document.close();

    return dateTime;
  }
}
