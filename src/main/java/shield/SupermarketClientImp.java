/**
 *
 */

package shield;

import java.io.IOException;

public class SupermarketClientImp implements SupermarketClient {

  private String endpoint;
  private String name;
  private String postCode;
  private String status;
  private boolean isRegistered = false;

  public SupermarketClientImp(String endpoint) {
    this.endpoint = endpoint;
  }

  @Override
  public boolean registerSupermarket(String name, String postCode) {
    String request = String.format("/registerSupermarket?business_name=%s&postcode=%s", name, postCode);
    boolean isSuccessful = false;

    try {
      String response = ClientIO.doGETRequest(endpoint + request);
      if (response.equals("registered new")){
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
    String request = String.format("/recordSupermarketOrder?individual_id=%s&order_number=%d&supermarket_business_name=%s&supermarket_postcode=%s", CHI, orderNumber, this.name, this.postCode);
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
    String request = String.format("/updateSupermarketOrderStatus?order_id=%d&newStatus=%s", orderNumber, status);
    boolean isSuccessful = false;

    try {
      String response = ClientIO.doGETRequest(endpoint + request);
      isSuccessful = Boolean.parseBoolean(response);
      if(isSuccessful) {
        this.status = status;
      }
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
