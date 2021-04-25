package shield;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class Generators {
  public static String generateCHI() {
    Random rand = new Random();
    int daysInPast = rand.nextInt(365 * 200);
    int extension = rand.nextInt(8999) + 1000;
    String date_of_birth =
            LocalDate.now().minusDays(daysInPast).format(DateTimeFormatter.ofPattern("ddMMyy"));
    return date_of_birth + extension;
  }
}
