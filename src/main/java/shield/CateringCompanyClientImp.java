/**
 *
 */

package shield;

import java.io.IOException;

public class CateringCompanyClientImp implements CateringCompanyClient {

  private String endpoint;
  private String name;
  private String postCode;
  private boolean isRegistered;

  public CateringCompanyClientImp(String endpoint) {
    this.endpoint = endpoint;
  }

  @Override
  public boolean registerCateringCompany(String name, String postCode) {
      String request = String.format("registerCateringCompany?business_name=%s&postcode=%s", name, postCode);
      boolean isSuccessful = false;

      try {
          String response = ClientIO.doGETRequest(endpoint + request);
          System.out.println(response);

          isSuccessful = true;
          this.name = name;
          this.postCode = postCode;
          this.isRegistered = true;

      } catch (IOException e) {
          e.printStackTrace();
      }

      return isSuccessful;
  }

  @Override
  public boolean updateOrderStatus(int orderNumber, String status) {
      String request = String.format("updateOrderStatus?order_id=%s&newStatus=%s", orderNumber, status);
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
}
