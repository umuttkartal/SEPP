package shield.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Validators {
  public static final String CHI_DATE_OF_BIRTH_FORMAT = "ddMMyy";
  public static final int ASSUMED_OLDEST_AGE = 200;
  public static final String UK_POSTCODE_REGEX =
      "(GIR 0AA)|((([A-Z-[QVX]][0-9][0-9]?)|(([A-Z-[QVX]][A-Z-[IJZ]][0-9][0-9]?)|(([A-Z-[QVX]][0-9][A-HJKSTUW])|([A-Z-[QVX]][A-Z-[IJZ]][0-9][ABEHMNPRVWXY]))))\\s?[0-9][A-Z-[CIKMOV]]{2})";
  public static final String SYSTEM_POSTCODE_REGEX = "EH.*_.*";

  public static boolean isValidCHI(String CHI) {
    return isValidDate(CHI.substring(0,6)) && CHI.matches("[0-9] {10}");
  }

  private static boolean isValidDate(String date) {
    LocalDateTime parsedDate = LocalDateTime.parse(date, DateTimeFormatter.ofPattern(CHI_DATE_OF_BIRTH_FORMAT));
    return isValidDate(parsedDate);
  }

  private static boolean isValidDate(LocalDateTime date) {
    return date.isBefore(LocalDateTime.now())
        && date.isAfter(LocalDateTime.now().minusYears(ASSUMED_OLDEST_AGE));
  }

  public static boolean isValidPostCode(String postCode) {
    return postCode.matches(SYSTEM_POSTCODE_REGEX)
        && postCode.replaceAll("_", "").matches(UK_POSTCODE_REGEX);
  }
}
