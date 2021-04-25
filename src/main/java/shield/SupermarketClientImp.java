/** */
package shield;

import shield.utils.Validators;

import java.io.IOException;

public class SupermarketClientImp implements SupermarketClient {

  private final String endpoint;
  private String name;
  private String postCode;
  private boolean isRegistered = false;

  public SupermarketClientImp(String endpoint) {
    this.endpoint = endpoint;
  }

  @Override
  public boolean registerSupermarket(String name, String postCode) {
    assert isValidPostcodeFormat(postCode) : "Postcode format is incorrect" + postCode;
    String request =
        String.format("/registerSupermarket?business_name=%s&postcode=%s", name, postCode);
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

  // **UPDATE2** ADDED METHOD
  @Override
  public boolean recordSupermarketOrder(String CHI, int orderNumber) {
    if (!isRegistered() || !Validators.isValidCHI(CHI)) {
      return false;
    }
    String request =
        String.format(
            "/recordSupermarketOrder?individual_id=%s&order_number=%d&"
                + "supermarket_business_name=%s&supermarket_postcode=%s",
            CHI, orderNumber, this.name, this.postCode);
    boolean isSuccessful = false;

    try {
      String response = ClientIO.doGETRequest(endpoint + request);
      isSuccessful = Boolean.parseBoolean(response);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return isSuccessful;
  }

  // **UPDATE**
  @Override
  public boolean updateOrderStatus(int orderNumber, String status) {
    if (!isRegistered()) {
      return false;
    }
    String request =
        String.format(
            "/updateSupermarketOrderStatus?order_id=%d&newStatus=%s", orderNumber, status);
    boolean isSuccessful = false;

    try {
      String response = ClientIO.doGETRequest(endpoint + request);
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

  @Override
  public boolean isValidPostcodeFormat(String postcode) {
    assert postcode.length() > 6;
    boolean startsWithEH = "EH".equals(postcode.substring(0, 2));
    boolean hasUnderScore = postcode.charAt(3) == '_' || postcode.charAt(4) == '_';
    boolean validLength = postcode.length() == 7 || postcode.length() == 8;

    return startsWithEH && hasUnderScore && validLength;
  }
}
