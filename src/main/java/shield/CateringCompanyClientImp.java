/**
 * Class for a catering company This allows for a catering company to register itself and update
 * orders that are sent to it
 */

package shield;

import java.io.IOException;

public class CateringCompanyClientImp implements CateringCompanyClient {
  private final String endpoint;
  private String name;
  private String postCode;
  private boolean isRegistered = false;

  public CateringCompanyClientImp(String endpoint) {
    this.endpoint = endpoint;
  }

  @Override
  public boolean registerCateringCompany(String name, String postCode) {
    assert isValidPostcodeFormat(postCode) : "Postcode format is incorrect" + postCode;
    String request =
        String.format("/registerCateringCompany?business_name=%s&postcode=%s", name, postCode);
    boolean isSuccessful = false;

    try {
      String response = ClientIO.doGETRequest(endpoint + request);
      if (response.equals("registered new") || response.equals("already registered")) {
        System.out.println(response);
        isSuccessful = true;
        this.name = name;
        this.postCode = postCode;
        this.isRegistered = true;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return isSuccessful;
  }

  @Override
  public boolean updateOrderStatus(int orderNumber, String status) {
    if (!isRegistered()) {
      return false;
    }
    String request =
        String.format("/updateOrderStatus?order_id=%d&newStatus=%s", orderNumber, status);
    boolean isSuccessful = false;

    try {
      String response = ClientIO.doGETRequest(endpoint + request);
      System.out.println(response);
      System.out.println(orderNumber);
      isSuccessful = Boolean.parseBoolean(response);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return isSuccessful;
  }

  @Override
  public boolean isRegistered() {
    return this.isRegistered;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public String getPostCode() {
    return this.postCode;
  }

  public boolean isValidPostcodeFormat(String postcode) {
    assert postcode.length() > 6;
    boolean startsWithEH = "EH".equals(postcode.substring(0, 2));
    boolean hasUnderScore = postcode.charAt(3) == '_' || postcode.charAt(4) == '_';
    boolean validLength = postcode.length() == 7 || postcode.length() == 8;

    return startsWithEH && hasUnderScore && validLength;
  }
}
