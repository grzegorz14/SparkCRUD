import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;
import org.imgscalr.Scalr;
import spark.Request;
import spark.Response;

import javax.imageio.ImageIO;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.Part;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static spark.Spark.*;

public class App {
  static ArrayList<Car> cars = new ArrayList<>();
  static String uploadUuid;
  static String galleryUuid;
  static Gson gson = new Gson();


  public static void main(String[] args) {

    // School
    //externalStaticFileLocation("E:\\School\\Stefanczyk\\Java\\SparkCrud\\SparkCrud\\src\\main\\resources\\public");

    // Home
    externalStaticFileLocation("F:\\School\\Stefanczyk\\Java\\SparkCrud\\SparkCrud\\src\\main\\resources\\public");

    get("/json", App::json);
    get("/download/:title", App::download);

    get("/cars/upload", App::uploadRedirect);
    get("/cars/gallery", App::galleryRedirect);
    get("/upload/getImage", App::getImage);
    get("/gallery/getImage", App::getImage);
    get("/editImage/getImage", App::getImage);

    post("/add", App::add);
    post("/delete", App::delete);
    post("/update", App::update);
    post("/upload", App::uploadImages);
    post("/saveUpload", App::saveImages);
    post("/getImages", App::getImages);
    post("/editImage", App::editImage);
    post("/cropImage", App::cropImage);

    post("/generate", App::generate);
    post("/invoice", App::invoice);
    post("/invoiceForAll", App::invoiceForAll);
    post("/invoiceForYear", App::invoiceForYear);
    post("/invoiceForPriceRange", App::invoiceForPriceRange);

    clearDirectories();
  }

  static String add(Request req, Response res) {
    res.type("application/json");
    Car newCar = Car.carFromJson(req.body());
    cars.add(newCar);
    return gson.toJson(newCar);
  }

  static JsonObject json(Request req, Response res) {
    res.type("application/json");
    JsonObject carsJson = new JsonObject();
    carsJson.addProperty("cars", gson.toJson(cars));
    return carsJson;
  }

  static String delete(Request req, Response res) {
    cars.removeIf(car -> car.uuid.toString().equals(req.body()));
    return "Car deleted";
  }

  static String update(Request req, Response res) {
    JsonObject newCarData = gson.fromJson(req.body(), JsonObject.class);

    for(int i = 0; i < cars.size(); i++) {
      if(cars.get(i).uuid.toString().equals(newCarData.get("uuid").getAsString())) {
        cars.set(i, Car.carFromJsonWithUUID(newCarData.get("uuid").getAsString(), req.body()));
        return "Car updated: " + newCarData.get("uuid").getAsString();
      }
    }
    return "None cars updated: " + newCarData.get("uuid").getAsString();
  }

  static String generate(Request req, Response res) {
    for(int i = 0; i < 20; i++) {
      cars.add(Car.randomCar());
    }
    return "Cars generated";
  }


  static String invoice(Request req, Response res) {
    for(int i = 0; i < cars.size(); i++) {
      if(cars.get(i).uuid.toString().equals(req.body())) {
        Car updateInvoiceCar = cars.get(i);
        updateInvoiceCar.setInvoice(true);
        cars.set(i, updateInvoiceCar);

        Invoices.generateInvoiceForSingleCar(updateInvoiceCar);
      }
    }
    return "Invoice for car " + req.body() + " generated";
  }

  static String invoiceForAll(Request req, Response res) {
    return Invoices.generateInvoiceForAll();
  }

  static String invoiceForYear(Request req, Response res) {
    return Invoices.generateInvoiceForYear(req.body());
  }

  static String invoiceForPriceRange(Request req, Response res) {
    JsonObject prices = gson.fromJson(req.body(), JsonObject.class);

    float min = prices.get("min").getAsFloat();
    float max = prices.get("max").getAsFloat();

    return Invoices.generateInvoiceForPriceRange(min, max);
  }

  static String download(Request req, Response res) {
    res.type("application/octet-stream");
    res.header("Content-Disposition", "attachment; filename=invoice" + req.params("title") + ".pdf");

    try {
      OutputStream outputStream = res.raw().getOutputStream();
      outputStream.write(Files.readAllBytes(Path.of("invoices/" + req.params("title") + ".pdf")));
    }
    catch(Exception ex) {
      System.out.println("EXCEPTION: " + ex);
    }
    return "Invoice downloaded";
  }

  static String uploadRedirect(Request req, Response res) {
    uploadUuid = req.queryParams("uuid");
    res.redirect("/upload/upload.html");
    return "Upload";
  }

  static String galleryRedirect(Request req, Response res) {
    galleryUuid = req.queryParams("uuid");
    res.redirect("/gallery/gallery.html");
    return "Gallery";
  }

  static JsonObject getImages(Request req, Response res) {
    JsonObject imagesJson = new JsonObject();

    List<String> imagesUuids = cars.stream()
                                   .filter(c -> c.uuid.toString().equals(galleryUuid))
                                   .findFirst()
                                   .orElse(Car.randomCar()).photosNames;

    imagesJson.addProperty("imagesUuids", gson.toJson(imagesUuids));
    return imagesJson;
  }

  static JsonObject uploadImages(Request req, Response res) {
    req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/upload"));
    res.type("application/json");
    JsonObject imagesUuids = new JsonObject();

    ArrayList<String> uuids = new ArrayList<>();

    try {
      for(Part p : req.raw().getParts()) {
        InputStream inputStream = p.getInputStream();
        byte[] bytes = inputStream.readAllBytes();
        String uuid = UUID.randomUUID().toString();
        uuids.add(uuid);
        String fileName = uuid + ".jpg";
        FileOutputStream fos = new FileOutputStream("upload/" + fileName);
        fos.write(bytes);
        fos.close();
      }

      imagesUuids.addProperty("uuids", gson.toJson(uuids));
    }
    catch(IOException | ServletException ex) {
      System.out.println("Exception while saving uploaded photos: " + ex);
    }

    return imagesUuids;
  }

  static JsonObject getImage(Request req, Response res) {
    String params = req.queryParams("uuid");
    File file = new File("upload/" + params.replace("uuid=", ""));
    res.type("image/jpeg");

    OutputStream outputStream = null;
    try {
      outputStream = res.raw().getOutputStream();
      outputStream.write(Files.readAllBytes(Path.of(file.getPath())));
      outputStream.flush();
    }
    catch(IOException e) {
      throw new RuntimeException(e);
    }
    res.type("application/json");
    JsonObject images = new JsonObject();
    images.addProperty("images", gson.toJson(cars));

    return images;
  }

  static String saveImages(Request req, Response res) {
    Objects.requireNonNull(cars.stream()
                               .filter(c -> c.uuid.toString().equals(uploadUuid))
                               .findFirst()
                               .orElse(null)).photosNames.addAll(Arrays.asList(req.body().split(",")));
    return "Photos saved";
  }

  static String editImage(Request req, Response res) {
    JsonObject editData = gson.fromJson(req.body(), JsonObject.class);

    String uuid = editData.get("uuid").getAsString().replace("uuid=", "");
    String fullPath = "upload/" + uuid + ".jpg";

    File sourceFile = new File(fullPath);

    try {
      BufferedImage originalImage = ImageIO.read(sourceFile);
      BufferedImage targetImage;

      switch(editData.get("type").getAsString()) {
        case "rotate" -> targetImage = Scalr.rotate(originalImage, Scalr.Rotation.CW_90);
        case "flipHorizontal" -> targetImage = Scalr.rotate(originalImage, Scalr.Rotation.FLIP_HORZ);
        case "flipVertical" -> targetImage = Scalr.rotate(originalImage, Scalr.Rotation.FLIP_VERT);
        default -> {
          System.out.println(editData.get("type").getAsString() + " as DEFAULT case");
          targetImage = originalImage;
        }
      }

      File targetFile = new File(fullPath);
      ImageIO.write(targetImage, "jpg", targetFile);

      originalImage.flush();
      targetImage.flush();
    }
    catch(IOException e) {
      throw new RuntimeException(e);
    }

    return "Image operation";
  }

  static String cropImage(Request req, Response res) {
    JsonObject editData = gson.fromJson(req.body(), JsonObject.class);

    String uuid = editData.get("uuid").getAsString().replace("uuid=", "");
    String fullPath = "upload/" + uuid + ".jpg";

    File sourceFile = new File(fullPath);

    try {
      BufferedImage originalImage = ImageIO.read(sourceFile);

      BufferedImage targetImage = Scalr.crop(originalImage,
                                             editData.get("x").getAsInt(),
                                             editData.get("y").getAsInt(),
                                             editData.get("w").getAsInt(),
                                             editData.get("h").getAsInt());;

      File targetFile = new File(fullPath);
      ImageIO.write(targetImage, "jpg", targetFile);

      originalImage.flush();
      targetImage.flush();
    }
    catch(IOException e) {
      throw new RuntimeException(e);
    }

    return "Image cropped";
  }

  private static void clearDirectories() {
    File uploadDirectory = new File("upload");
    File invoicesDirectory = new File("invoices");

    try {
      FileUtils.deleteDirectory(uploadDirectory);
      Files.createDirectory(Path.of("upload"));

      FileUtils.deleteDirectory(invoicesDirectory);
      Files.createDirectory(Path.of("invoices"));
    }
    catch (IOException ex) {
      System.out.println(ex.getMessage());
    }
  }
}
