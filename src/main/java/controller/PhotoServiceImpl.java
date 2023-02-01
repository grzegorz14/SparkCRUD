package controller;

import com.google.gson.JsonObject;
import model.Photo;

import java.io.OutputStream;
import java.util.HashMap;

public class PhotoServiceImpl implements PhotoService {

  HashMap<String, Photo> photos = new HashMap<>(){{
    put("0", new Photo("11", "car", "asdfasdf.jsp"));
    put("1", new Photo("54", "tree", "fdsf.jsp"));
    put("2", new Photo("31", "form", "fghdshdh.jsp"));
    put("3", new Photo("22", "wand", "hjgjhj.jsp"));
  }};

  @Override
  public JsonObject getPhotos() {
    if (photos.values().size() == 0) {
      return null;
    }

    JsonObject photosJson = new JsonObject();
    for (Photo photo : photos.values()) {
      photosJson.add(photo.id(), createJsonPhoto(photo));
    }
    return photosJson;
  }

  @Override
  public JsonObject getPhotoById(String id) {
    Photo photo = photos.get(id);
    if (photo == null) {
      return null;
    }

    return createJsonPhoto(photo);
  }

  @Override
  public JsonObject getPhotoByName(String name) {
    Photo photo = null;
    for (Photo p : photos.values()) {
      if (p.name().equals(name)) {
        photo = p;
      }
    }
    if (photo == null) {
      return null;
    }

    return createJsonPhoto(photo);
  }

  @Override
  public OutputStream getPhotoStreamById(String id) {
    return null;
  }

  @Override
  public boolean deletePhotoById(String id) {
    return photos.remove(id) != null;
  }

  @Override
  public boolean renamePhotoById(String id, String newName) {
    Photo photo = photos.remove(id);
    if (photo == null) {
      return false;
    }
    photos.put(photo.id(), new Photo(photo.id(),
                                    newName,
                                    photo.path()));
    return true;
  }

  private JsonObject createJsonPhoto(Photo photo) {
    JsonObject jsonPhoto = new JsonObject();
    jsonPhoto.addProperty("id", photo.id());
    jsonPhoto.addProperty("name", photo.name());
    jsonPhoto.addProperty("path", photo.path());
    return jsonPhoto;
  }
}
