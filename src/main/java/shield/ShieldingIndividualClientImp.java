package shield;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import shield.utils.Item;
import shield.utils.MessagingFoodBox;
import shield.utils.Order;

import static shield.utils.Validators.isValidCHI;
import static shield.utils.Validators.isValidPostCode;

public class ShieldingIndividualClientImp implements ShieldingIndividualClient {

  private static final Gson gson = new Gson();
  private final String endpoint;
  private final Map<Integer, Order> orders = new HashMap<>();
  private String CHI;
  private String postCode;
  private String name;
  private String surname;
  private String phoneNumber;

  private boolean isRegistered = false;
  private LocalDateTime latestOrderTime = LocalDateTime.MIN;
  private MessagingFoodBox liveFoodBox = new MessagingFoodBox();
  private Map<Integer, MessagingFoodBox> foodBoxes = new HashMap<>();

  public ShieldingIndividualClientImp(String endpoint) {
    this.endpoint = endpoint;
    loadFoodBoxes();
  }

  private void loadFoodBoxes() {
    String request = "/showFoodBox";
    try {
      String response = ClientIO.doGETRequest(endpoint + request);
      Type listType = new TypeToken<List<MessagingFoodBox>>() {}.getType();
      List<MessagingFoodBox> foodBoxList = gson.fromJson(response, listType);
      this.foodBoxes =
          foodBoxList.stream()
              .collect(Collectors.toMap(MessagingFoodBox::getId, foodBox -> foodBox));
      System.out.println(this.foodBoxes);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public boolean registerShieldingIndividual(String CHI) {
    if (!isValidCHI(CHI)) {
      return false;
    }
    String request = String.format("/registerShieldingIndividual?CHI=%s", CHI);

    try {
      String response = ClientIO.doGETRequest(endpoint + request);
      System.out.println(response);
      if (!response.equals("already registered")) {
        Type listType = new TypeToken<List<String>>() {}.getType();
        List<String> details = gson.fromJson(response, listType);
        assert details.size() == 4;

        this.CHI = CHI;
        this.postCode = details.get(0);
        this.name = details.get(1);
        this.surname = details.get(2);
        this.phoneNumber = details.get(3);
        this.postCode = this.postCode.replace(' ', '_').toUpperCase();
        assert isValidPostCode(this.postCode);
      }
      this.isRegistered = true;
    } catch (IOException e) {
      e.printStackTrace();
    }

    return this.isRegistered;
  }

  @Override
  public Collection<String> showFoodBoxes(String dietaryPreference) {
    String request =
        String.format("/showFoodBox?orderOption=catering&dietaryPreference=%s", dietaryPreference);
    List<MessagingFoodBox> responseBoxes;
    List<String> boxIds = new ArrayList<>();

    try {
      String response = ClientIO.doGETRequest(endpoint + request);
      Type listType = new TypeToken<List<MessagingFoodBox>>() {}.getType();
      responseBoxes = gson.fromJson(response, listType);

      for (MessagingFoodBox b : responseBoxes) {
        boxIds.add(String.valueOf(b.getId()));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return boxIds;
  }

  // add date and time to order when placing it surely?
  @Override
  public boolean placeOrder() {
    if (!isRegistered()) {
      return false;
    }
    if (this.liveFoodBox == null || this.liveFoodBox.getDeliveredBy().isEmpty()) {
      System.out.println("No food box found");
      return false;
    }

    String[] caterer = this.liveFoodBox.getDeliveredBy().split(",");
    String request =
        String.format(
            "/placeOrder?individual_id=%s&catering_business_name=%s&catering_postcode=%s",
            this.CHI, caterer[1], caterer[2]);
    String data = gson.toJson(this.liveFoodBox);
    boolean isSuccessful = false;

    try {
      if (isEligible()) {
        String response = ClientIO.doPOSTRequest(endpoint + request, data);
        int id = Integer.parseInt(response);
        this.latestOrderTime = LocalDateTime.now();
        Order order = new Order(this.CHI, id, this.liveFoodBox, this.PLACED, this.latestOrderTime);
        this.orders.put(id, order);
        isSuccessful = true;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return isSuccessful;
  }

  public boolean isEligible() {
    if (!isRegistered()) {
      return false;
    }
    return latestOrderTime.isBefore(LocalDateTime.now().minusWeeks(1));
  }

  @Override
  public boolean editOrder(int orderNumber) {
    if (!isRegistered()) {
      return false;
    }
    String request = String.format("/editOrder?order_id=%d", orderNumber);
    String data = gson.toJson(this.liveFoodBox);
    boolean isSuccessful = false;

    try {
      String response = ClientIO.doPOSTRequest(endpoint + request, data);
      isSuccessful = Boolean.parseBoolean(response);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return isSuccessful;
  }

  @Override
  public boolean cancelOrder(int orderNumber) {
    if (!isRegistered()) {
      return false;
    }
    String request = String.format("/cancelOrder?order_id=%d", orderNumber);
    boolean isSuccessful = false;

    try {
      String response = ClientIO.doGETRequest(endpoint + request);
      isSuccessful = Boolean.parseBoolean(response);
      if (isSuccessful) {
        this.latestOrderTime = LocalDateTime.MIN;
        for (Order order : this.orders.values()) {
          if (order.getDatePlaced().isAfter(this.latestOrderTime)) {
            this.latestOrderTime = order.getDatePlaced();
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return isSuccessful;
  }

  // **UPDATE2** REMOVED PARAMETER

  @Override
  // Method needs to be adapted for new server

  public int requestOrderStatus(int orderNumber) {
    if (!isRegistered()) {
      return ERROR_CODE;
    }
    String request = String.format("/requestStatus?order_id=%d", orderNumber);
    int status = ERROR_CODE;

    try {
      String response = ClientIO.doGETRequest(endpoint + request);
      status = Integer.parseInt(response);
      this.orders.get(orderNumber).setStatus(status);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return status;
  }

  // **UPDATE**
  @Override
  public Collection<String> getCateringCompanies() {
    String request = "/getCaterers";
    List<String> caterers = new ArrayList<>();

    try {
      String response = ClientIO.doGETRequest(endpoint + request);
      Type listType = new TypeToken<List<String>>() {}.getType();
      caterers = gson.fromJson(response, listType);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return caterers;
  }

  // **UPDATE** //OVERLOAD HERE POSSIBLY TO HANDLE NAMES AS WELL AS POSTCODES?
  @Override
  public float getDistance(String postCode1, String postCode2) {
    postCode1 = postCode1.replace(' ', '_').toUpperCase();
    postCode2 = postCode2.replace(' ', '_').toUpperCase();
    String request = String.format("/distance?postcode1=%s&postcode2=%s", postCode1, postCode2);
    float distance = Float.POSITIVE_INFINITY;

    try {
      String response = ClientIO.doGETRequest(endpoint + request);
      System.out.println("RESPONSE " + response);
      distance = Float.parseFloat(response);
    } catch (IOException e) {
      System.out.println("Killer");
      e.printStackTrace();
    } catch (RuntimeException e) {
      if (!e.getMessage().contains("400")) {
        System.out.println(postCode2 + " " + e.getMessage());
      } else {
        System.out.println("ERROR-BREAKER: 400 " + postCode2);
      }
    }

    return distance;
  }

  @Override
  public boolean isRegistered() {
    String request = "/registerShieldingIndividual?CHI=" + this.CHI;
    try {
      // perform request
      String response = ClientIO.doGETRequest(endpoint + request);
      return response.equals("already registered");

    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public String getCHI() {
    return this.CHI;
  }

  public void setCHI(String CHI) {
    this.CHI = CHI;
  }

  @Override
  public int getFoodBoxNumber() {
    return this.liveFoodBox.getId();
  }

  @Override
  public String getDietaryPreferenceForFoodBox(int foodBoxId) {
    return this.foodBoxes.get(foodBoxId).getDiet();
  }

  @Override
  public int getItemsNumberForFoodBox(int foodBoxId) {
    return this.foodBoxes.get(foodBoxId).getContents().size();
  }

  @Override
  public Collection<Integer> getItemIdsForFoodBox(int foodBoxId) {
    List<Integer> itemIds = new ArrayList<>();
    for (Item item : this.foodBoxes.get(foodBoxId).getContents()) {
      itemIds.add(item.getId());
    }
    return itemIds;
  }

  public String getEndpoint() {
    return endpoint;
  }

  public Map<Integer, Order> getOrders() {
    return orders;
  }

  public String getPostCode() {
    return postCode;
  }

  public void setPostCode(String postCode) {
    this.postCode = postCode;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSurname() {
    return surname;
  }

  public void setSurname(String surname) {
    this.surname = surname;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public LocalDateTime getLatestOrderTime() {
    return latestOrderTime;
  }

  public void setLatestOrderTime(LocalDateTime latestOrderTime) {
    this.latestOrderTime = latestOrderTime;
  }

  public MessagingFoodBox getLiveFoodBox() {
    return liveFoodBox;
  }

  public void setLiveFoodBox(MessagingFoodBox liveFoodBox) {
    this.liveFoodBox = liveFoodBox;
  }

  @Override
  public String getItemNameForFoodBox(int itemId, int foodBoxId) {
    assert itemId >= 0 : "Item ID must be non-negative";
    assert foodBoxId >= 0 : "Food box ID must be non-negative";

    return this.foodBoxes.get(foodBoxId).getContentsAsMap().get(itemId).getName();
  }

  @Override
  public int getItemQuantityForFoodBox(int itemId, int foodBoxId) {
    assert itemId >= 0 : "Item ID must be non-negative";
    assert foodBoxId >= 0 : "Food box ID must be non-negative";

    return this.foodBoxes.get(foodBoxId).getContentsAsMap().get(itemId).getQuantity();
  }

  @Override
  public boolean pickFoodBox(int foodBoxId) {
    if (!isRegistered()) {
      return false;
    }
    this.liveFoodBox = this.foodBoxes.get(foodBoxId);
    this.liveFoodBox.setDeliveredBy(getClosestCateringCompany());

    return this.liveFoodBox != null;
  }

  @Override
  public boolean changeItemQuantityForPickedFoodBox(int itemId, int quantity) {
    if (!isRegistered()) {
      return false;
    }
    if (quantity < this.liveFoodBox.getContentsAsMap().get(itemId).getQuantity()) {
      this.liveFoodBox.setItemQuantity(itemId, quantity);
      return true;
    }
    return false;
  }

  @Override
  public Collection<Integer> getOrderNumbers() {
    return this.orders.keySet();
  }

  // add constants for status
  @Override
  public String getStatusForOrder(int orderNumber) {
    if (!isRegistered()) {
      return Integer.toString(ERROR_CODE);
    }
    String request = String.format("/requestStatus?order_id=%s", orderNumber);
    String status = Integer.toString(ERROR_CODE);

    try {
      status = ClientIO.doGETRequest(endpoint + request);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return status;
  }

  // Surely if ID is in list of ids then permit *or else reject*
  @Override
  public Collection<Integer> getItemIdsForOrder(int orderNumber) {
    List<Integer> itemIds = new ArrayList<>();
    if (!isRegistered()) {
      return itemIds;
    }

    for (Item item : orders.get(orderNumber).getFoodBox().getContents()) {
      itemIds.add(item.getId());
    }

    return itemIds;
  }

  // Should enforce that all ids are strings
  @Override
  public String getItemNameForOrder(int itemId, int orderNumber) {
    if (!isRegistered()) {
      return Integer.toString(ERROR_CODE);
    }
    return this.orders.get(orderNumber).getFoodBox().getContentsAsMap().get(itemId).getName();
  }

  @Override
  public int getItemQuantityForOrder(int itemId, int orderNumber) {
    if (!isRegistered()) {
      return ERROR_CODE;
    }
    return this.orders.get(orderNumber).getFoodBox().getContentsAsMap().get(itemId).getQuantity();
  }

  // Do we want to revert the active food box to the food box it was before this command
  @Override
  public boolean setItemQuantityForOrder(int itemId, int orderNumber, int quantity) {
    if (!isRegistered()) {
      return false;
    }
    boolean isSuccessful = false;
    this.liveFoodBox = this.orders.get(orderNumber).getFoodBox();
    if (changeItemQuantityForPickedFoodBox(itemId, quantity)) {
      isSuccessful = editOrder(orderNumber);
    }

    return isSuccessful;
  }

  @Override
  public String getClosestCateringCompany() {
    if (!isRegistered()) {
      return Integer.toString(ERROR_CODE);
    }
    String closestCaterer = "No caterer found";
    float closestDistance = Float.POSITIVE_INFINITY;

    for (String caterer : getCateringCompanies()) {
      String[] catererDetails = caterer.split(",");
      if (catererDetails.length == 3) {
        float distance = getDistance(this.postCode, catererDetails[2]);
        if (distance < closestDistance) {
          System.out.println("New closest");
          closestDistance = distance;
          closestCaterer = caterer;
        }
      }
    }
    System.out.println("CLOSEST " + closestCaterer);
    return closestCaterer;
  }
}
