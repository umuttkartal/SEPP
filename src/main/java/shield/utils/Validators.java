package shield.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class Validators {
    public static final String CHI_DATE_OF_BIRTH_FORMAT = "ddMMyy";
    public static final int ASSUMED_OLDEST_AGE = 200;
    public static final String UK_POSTCODE_REGEX =
            "(GIR 0AA)|((([A-Z-[QVX]][0-9][0-9]?)|(([A-Z-[QVX]][A-Z-[IJZ]][0-9][0-9]?)|(([A-Z-[QVX]][0-9][A-HJKSTUW])|([A-Z-[QVX]][A-Z-[IJZ]][0-9][ABEHMNPRVWXY]))))\\s?[0-9][A-Z-[CIKMOV]]{2})";
    public static final String SYSTEM_POSTCODE_REGEX = "EH.*_.*";

    public static boolean isValidCHI(String CHI) {
        return isValidFormat(CHI_DATE_OF_BIRTH_FORMAT, CHI.substring(0,6), Locale.ENGLISH) && CHI.length() == 10;
    }

    private static boolean isValidDate(String date, Locale locale) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CHI_DATE_OF_BIRTH_FORMAT, locale);
        LocalDateTime parsedDate = LocalDateTime.parse(date, formatter);
        return isValidDate1(parsedDate);
    }

    private static boolean isValidDate1(LocalDateTime date) {
        return date.isBefore(LocalDateTime.now())
                && date.isAfter(LocalDateTime.now().minusYears(ASSUMED_OLDEST_AGE));
    }

    public static boolean isValidFormat(String format, String value, Locale locale) {
        LocalDateTime ldt = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format, locale);

        try {
            ldt = LocalDateTime.parse(value, formatter);
            String result = ldt.format(formatter);
            boolean out = ldt.isBefore(LocalDateTime.now());
            return result.equals(value) && out;
        } catch (DateTimeParseException e) {
            try {
                LocalDate ld = LocalDate.parse(value, formatter);
                String result = ld.format(formatter);
                return result.equals(value);
            } catch (DateTimeParseException exp) {
                try {
                    LocalTime lt = LocalTime.parse(value, formatter);
                    String result = lt.format(formatter);
                    return result.equals(value);
                } catch (DateTimeParseException e2) {
                    e2.printStackTrace();
                }
            }
        }
        return false;
    }

    public static boolean isValidPostCode(String postCode) {
        return postCode.matches(SYSTEM_POSTCODE_REGEX)
                && postCode.replaceAll("_", "").matches(UK_POSTCODE_REGEX);
    }
}
