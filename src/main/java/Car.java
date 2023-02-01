import com.fasterxml.uuid.Generators;
import com.google.gson.JsonObject;

import java.util.*;

public class Car {
    static int mainId = 0;
    public static String[] models = {"Renault", "BMW", "Tesla", "Mazda", "Fiat", "Toyota", "Seat"};

    int id;
    UUID uuid;
    String model;
    CustomDate date;
    Airbag[] airbags;
    String color;
    float price;
    Vat vat;
    List<String> photosNames;

    boolean invoice = false;

    public Car(int id,
               String uuid,
               String model,
               CustomDate date,
               Airbag[] airbags,
               String color) {
        this.id = id < 0 ? mainId++ : id;
        this.uuid = uuid.isBlank() ? Generators.randomBasedGenerator().generate() : UUID.fromString(uuid);
        this.model = model;
        this.date = date == null ? CustomDate.randomCustomDate() : date;
        this.airbags = airbags;
        this.color = color;
        this.price = Helpers.randomPrice();
        this.vat = Helpers.randomVat();
        this.photosNames = new ArrayList<>();
    }

    public static Car carFromJson(String jsonString) {
        return carFromJsonWithUUID("", jsonString);
    }

    public static Car carFromJsonWithUUID(String uuid, String jsonString) {
        JsonObject jsonCar = App.gson.fromJson(jsonString, JsonObject.class);

        int id = jsonCar.get("id") == null ? mainId++ : jsonCar.get("id").getAsInt();

        String model = jsonCar.get("model").getAsString();
        Airbag[] airbags = App.gson.fromJson(jsonCar.get("airbags"), Airbag[].class);

        CustomDate date = CustomDate.randomCustomDate();
        if (jsonCar.get("year") != null && !jsonCar.get("year").getAsString().isBlank() ) {
            date.setYear(jsonCar.get("year").getAsInt());
        }

        String color = jsonCar.get("color").getAsString();

        return new Car(id, uuid, model, date, airbags, color);
    }

    public static Car randomCar() {
        String model = models[new Random().nextInt(models.length)];
        Airbag[] airbags = Airbag.randomAirbags();
        String color = String.format("#%06x", new Random().nextInt(0xffffff + 1));

        return new Car(-1, "", model, null, airbags, color);
    }

    public void setInvoice(boolean value) {
        this.invoice = value;
    }

    public double getPriceWithVat() {
        return price + price * vat.getValue() * 0.01;
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", uuid=" + uuid +
                ", model='" + model + '\'' +
                ", date=" + date.getDate() +
                ", airbags=" + Arrays.toString(airbags) +
                ", color=" + color +
                ", price=" + price +
                ", vat=" + vat +
                ", numberOfPhotos=" + photosNames.size() +
                '}';
    }
}

