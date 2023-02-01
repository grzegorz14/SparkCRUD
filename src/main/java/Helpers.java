import java.awt.*;
import java.util.Random;

public class Helpers {

  public static Color hexToColor(String hex) {
    return new Color(
        Integer.valueOf(hex.substring(1, 3), 16),
        Integer.valueOf(hex.substring(3, 5), 16),
        Integer.valueOf(hex.substring(5, 7), 16));
  }

  public static float randomPrice() {
    Random random = new Random();
    return random.nextFloat(100_000) + 5_000;
  }

  public static Vat randomVat() {
    Vat[] vats = Vat.values();
    Random random = new Random();
    return vats[random.nextInt(vats.length)];
  }
}
