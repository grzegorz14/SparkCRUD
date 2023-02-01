public enum Vat {
  NONE(0),
  SEVEN(7),
  TWENTY_TWO(22);

  private final int value;
  Vat(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }
}
