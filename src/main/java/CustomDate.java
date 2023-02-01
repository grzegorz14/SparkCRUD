import java.util.Random;

public class CustomDate {
    private int year;
    private int month;
    private int day;

    public CustomDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public static CustomDate randomCustomDate() {
        Random random = new Random();

        int year = random.nextInt(23) + 2000;
        int month = random.nextInt(12) + 1;
        int day = month == 2 ? random.nextInt(28) + 1 : random.nextInt(30) + 1;

        return new CustomDate(year, month, day);
    }

    public String getDate() {
        return year + "-" + (month < 10 ? "0" + month : month) + (day < 10 ? "0" + day : day);
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }
}
