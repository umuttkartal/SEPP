/**
 * Dummy example of a potential application for a shielding individual.
 *
 * <p>This is mostly to show an example of the JSON marshalling and unmarshalling machineray.
 *
 * @author
 */

package shield;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import shield.utils.MessagingFoodBox;
import shield.utils.Order;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class DummyShieldingIndividualClientImp implements ShieldingIndividualClient {
  /** The string representation of the base server endpoint (a HTTP address). */
  private final String endpoint;

  public DummyShieldingIndividualClientImp(String endpoint) {
    this.endpoint = endpoint;
  }

  @Override
  public boolean registerShieldingIndividual(String CHI) {
    return false;
  }

  @Override
  public Collection<String> showFoodBoxes(String dietaryPreference) {
    // construct the endpoint request
    String request = "/showFoodBox?orderOption=catering&dietaryPreference=none";

    // setup the response recepient
    List<MessagingFoodBox> responseBoxes = new ArrayList<MessagingFoodBox>();

    List<String> boxIds = new ArrayList<String>();

    try {
      // perform request
      String response = ClientIO.doGETRequest(endpoint + request);

      // unmarshal response
      Type listType = new TypeToken<List<MessagingFoodBox>>() {}.getType();
      responseBoxes = new Gson().fromJson(response, listType);

      // gather required fields
      for (MessagingFoodBox b : responseBoxes) {
        boxIds.add(b.id);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return boxIds;
  }

  @Override
  public boolean placeOrder() {
    return false;
  }

  @Override
  public boolean editOrder(int orderNumber) {
    return false;
  }

  @Override
  public boolean cancelOrder(int orderNumber) {
    return false;
  }

  @Override
  public int requestOrderStatus(int orderNumber) {
    return 0;
  }

  // **UPDATE**
  @Override
  public Collection<String> getCateringCompanies() {
    return null;
  }

  // **UPDATE**
  @Override
  public float getDistance(String postCode1, String postCode2) {
    return 0;
  }

  @Override
  public boolean isRegistered() {
    return false;
  }

  @Override
  public String getCHI() {
    return null;
  }

  @Override
  public int getFoodBoxNumber() {
    return 0;
  }

  @Override
  public String getDietaryPreferenceForFoodBox(int foodBoxId) {
    return null;
  }

  @Override
  public int getItemsNumberForFoodBox(int foodBoxId) {
    return 0;
  }

  @Override
  public Collection<Integer> getItemIdsForFoodBox(int foodboxId) {
    return null;
  }

  @Override
  public String getItemNameForFoodBox(int itemId, int foodBoxId) {
    return null;
  }

  @Override
  public int getItemQuantityForFoodBox(int itemId, int foodBoxId) {
    return 0;
  }

  @Override
  public boolean pickFoodBox(int foodBoxId) {
    return false;
  }

  @Override
  public boolean changeItemQuantityForPickedFoodBox(int itemId, int quantity) {
    return false;
  }

  @Override
  public Collection<Integer> getOrderNumbers() {
    return null;
  }

  @Override
  public String getStatusForOrder(int orderNumber) {
    return null;
  }

  @Override
  public Collection<Integer> getItemIdsForOrder(int orderNumber) {
    return null;
  }

  @Override
  public String getItemNameForOrder(int itemId, int orderNumber) {
    return null;
  }

  @Override
  public int getItemQuantityForOrder(int itemId, int orderNumber) {
    return 0;
  }

  @Override
  public boolean setItemQuantityForOrder(int itemId, int orderNumber, int quantity) {
    return false;
  }

  // **UPDATE**
  @Override
  public String getClosestCateringCompany() {
    return null;
  }

  @Override
  public String getEndpoint() {
    return null;
  }

  @Override
  public Map<Integer, Order> getOrders() {
    return null;
  }

  @Override
  public String getPostCode() {
    return null;
  }

  @Override
  public String getName() {
    return null;
  }

  @Override
  public String getSurname() {
    return null;
  }

  @Override
  public String getPhoneNumber() {
    return null;
  }

  @Override
  public LocalDateTime getLatestOrderTime() {
    return null;
  }

  @Override
  public shield.utils.MessagingFoodBox getLiveFoodBox() {
    return null;
  }

  // internal field only used for transmission purposes
  final class MessagingFoodBox {
    // a field marked as transient is skipped in marshalling/unmarshalling
    transient List<String> contents;

    String deliveredBy;
    String diet;
    String id;
    String name;
  }
}
