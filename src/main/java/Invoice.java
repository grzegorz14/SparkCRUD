import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;

public class Invoice {
  Timestamp time;
  String seller;
  String buyer;
  Car[] cars;

  public Invoice(Car[] cars) {
    this.time = Timestamp.valueOf(LocalDateTime.now());
    this.seller = "DS_CarDealer";
    this.buyer = "buyer123";
    this.cars = cars;
  }

  public double getPrice() {
    return Arrays.stream(cars)
                 .mapToDouble(c -> c.price)
                 .sum();
  }

  public double getPriceWithVAT() {
    return Arrays.stream(cars)
                 .mapToDouble(Car::getPriceWithVat)
                 .sum();
  }
}
