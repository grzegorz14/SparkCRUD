package controller;

import com.google.gson.JsonObject;

import java.io.OutputStream;

public interface PhotoService {

  JsonObject getPhotos();

  JsonObject getPhotoById(String id);

  JsonObject getPhotoByName(String name);

  OutputStream getPhotoStreamById(String id);

  boolean deletePhotoById(String id);

  boolean renamePhotoById(String id, String newName);

}
