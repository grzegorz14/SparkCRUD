import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import controller.PhotoServiceImpl;
import response.ResponseEntity;
import response.ResponseStatus;
import spark.Request;
import spark.Response;

import java.io.OutputStream;

import static spark.Spark.*;

public class AppREST {
  static PhotoServiceImpl service = new PhotoServiceImpl();

  public static void main(String[] args) {
    port(7777);

    get("/api/photos", AppREST::getPhotos);
    get("/api/photos/:id", AppREST::getPhotoById);
    get("/api/photos/name/:name", AppREST::getPhotoByName);
    get("/api/photos/stream/:id", AppREST::getPhotoStreamById);
    delete("/api/photos/:id", AppREST::deletePhoto);
    put("/api/photos", AppREST::renamePhoto);
  }

  static String getPhotos(Request req, Response res) {
    res.header("Access-Control-Allow-Origin", "*");
    res.type("application/json");
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    JsonObject photos = service.getPhotos();

    if(photos == null) {
      return gson.toJson(new ResponseEntity(
          ResponseStatus.NOT_FOUND,
          "No photos found"
      ));
    }

    return gson.toJson(new ResponseEntity(
        ResponseStatus.SUCCESS,
        "Photos list",
        gson.toJsonTree(photos)
    ));
  }

  static String getPhotoById(Request req, Response res) {
    res.header("Access-Control-Allow-Origin", "*");
    res.type("application/json");
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    JsonObject jsonPhoto = service.getPhotoById(req.params("id"));

    if(jsonPhoto == null) {
      return gson.toJson(new ResponseEntity(
          ResponseStatus.NOT_FOUND,
          "No photo found with id = " + req.params("id")
      ));
    }

    return gson.toJson(new ResponseEntity(
        ResponseStatus.SUCCESS,
        "Photo with id = " + req.params("id"),
        gson.toJsonTree(jsonPhoto)
    ));
  }

  static String getPhotoByName(Request req, Response res) {
    res.header("Access-Control-Allow-Origin", "*");
    res.type("application/json");
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    JsonObject jsonPhoto = service.getPhotoByName(req.params("name"));

    if(jsonPhoto == null) {
      return gson.toJson(new ResponseEntity(
          ResponseStatus.NOT_FOUND,
          "No photo found with name = " + req.params("name")
      ));
    }

    return gson.toJson(new ResponseEntity(
        ResponseStatus.SUCCESS,
        "Photo with name = " + req.params("name"),
        gson.toJsonTree(jsonPhoto)
    ));
  }

  static OutputStream getPhotoStreamById(Request req, Response res) {
    return service.getPhotoStreamById(req.params("id"));
  }

  static boolean deletePhoto(Request req, Response res) {
    return service.deletePhotoById(req.params("id"));
  }

  static boolean renamePhoto(Request req, Response res) {
    Gson gson = new Gson();
    JsonObject jsonPhoto = gson.fromJson(req.body(), JsonObject.class);

    return service.renamePhotoById(jsonPhoto.get("id").getAsString(), jsonPhoto.get("name").getAsString());
  }

}
