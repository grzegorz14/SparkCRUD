import java.util.Random;

public class Airbag {
    String description;
    String value;

    public Airbag(String description, String value) {
        this.description = description;
        this.value = value;
    }

    public static Airbag[] randomAirbags() {
        Random random = new Random();
        Airbag driver = new Airbag("driver", Boolean.toString(random.nextBoolean()));
        Airbag passenger = new Airbag("passenger", Boolean.toString(random.nextBoolean()));
        Airbag backSeats = new Airbag("backSeats", Boolean.toString(random.nextBoolean()));
        Airbag sides = new Airbag("sides", Boolean.toString(random.nextBoolean()));

        return new Airbag[] { driver, passenger, backSeats, sides };
    }

    @Override
    public String toString() {
        return "{" +
                "description='" + description + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}