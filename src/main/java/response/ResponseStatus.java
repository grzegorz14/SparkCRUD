package response;

public enum ResponseStatus {
  SUCCESS("SUCCESS"),
  ERROR("ERROR"),
  NOT_FOUND("NOT_FOUND"),
  OTHER("OTHER");
  ResponseStatus(String message) {
  }
}
