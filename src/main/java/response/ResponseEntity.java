package response;

import com.google.gson.JsonElement;

public class ResponseEntity {
  private ResponseStatus status;
  private String message;
  private JsonElement data;

  public ResponseEntity(String message) {
    this.message = message;
  }

  public ResponseEntity(ResponseStatus status, String message) {
    this.status = status;
    this.message = message;
  }

  public ResponseEntity(ResponseStatus status, String message, JsonElement data) {
    this.status = status;
    this.message = message;
    this.data = data;
  }

  public ResponseStatus getStatus() {
    return status;
  }

  public void setStatus(ResponseStatus status) {
    this.status = status;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public JsonElement getData() {
    return data;
  }

  public void setData(JsonElement data) {
    this.data = data;
  }
}
