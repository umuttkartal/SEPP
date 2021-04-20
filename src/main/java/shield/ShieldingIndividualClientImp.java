package shield;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ShieldingIndividualClientImp implements ShieldingIndividualClient {

  // Get that weird class that stores constants to do this

  // Should we store unique unedited food boxes locally?
  // Also allows reuse of functions for getting ids and the like from the box
  // - alternatively can just store them locally
  // and use alternative find order function

  public static final int ERROR_CODE = -1;
  private static final int PLACED = 0;
  private static final int PACKED = 1;
  private static final int DISPATCHED = 2;
  private static final int DELIVERED = 3;
  private static final int CANCELLED = 4;
  private static final Gson gson = new Gson();
  private final String endpoint;
  private final List<Order> orders = new ArrayList<>();
  private String CHI;
  private String postCode;
  private String name;
  private String surname;
  private String phoneNumber;
  // Add multi threading possibly (concurrent programming)

  // Nested, might not work
  private boolean isRegistered = false;
  // private last order date
  private MessagingFoodBox foodBox = new MessagingFoodBox();

  // What happens if they want to order again but fails because order is late, cancelled etc due to
  // lateness?
  private List<MessagingFoodBox> foodBoxes = new ArrayList<>();

  public ShieldingIndividualClientImp(String endpoint) {
    this.endpoint = endpoint;
    String request = "/showFoodBox";
    try {
      String response = ClientIO.doGETRequest(endpoint + request);
      Type listType = new TypeToken<List<MessagingFoodBox>>() {}.getType();
      this.foodBoxes = gson.fromJson(response, listType);
      System.out.println(this.foodBoxes);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public boolean registerShieldingIndividual(String CHI) {
    String request = String.format("/registerShieldingIndividual?CHI=%s", CHI);
    boolean isSuccessful = false;

    try {
      String response = ClientIO.doGETRequest(endpoint + request);
      System.out.println(response);
      if (!response.equals("already registered")) {
        isSuccessful = true;
        this.CHI = CHI;
        this.isRegistered = true;

        Type listType = new TypeToken<List<String>>() {}.getType();
        List<String> details = gson.fromJson(response, listType);
        this.postCode = details.get(0);
        this.name = details.get(1);
        this.surname = details.get(2);
        this.phoneNumber = details.get(3);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return isSuccessful;
  }

  // Can do a better job of showing food boxes - show more detail? format it?
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
        boxIds.add(String.valueOf(b.id));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return boxIds;
  }

  // add date and time to order when placing it surely?
  @Override
  public boolean placeOrder() {
    if (this.foodBox.deliveredBy.isEmpty()) {
      System.out.println("No food box found");
      return false;
    }
    String[] caterer = this.foodBox.deliveredBy.split(",");
    String request =
        String.format(
            "/placeOrder?individual_id=%s&catering_business_name=%s&catering_postcode=%s",
            this.CHI, caterer[1], caterer[2]);
    String data = gson.toJson(this.foodBox);
    Order order = new Order();

    boolean isSuccessful = false;

    try {
      String response = ClientIO.doPOSTRequest(endpoint + request, data);
      // NECESSARY FOR NEW SERVER this.order_id = Integer.parseInt(response);
      isSuccessful = true;
      order.id = Integer.parseInt(response);
      order.foodBox = this.foodBox;
      order.status = PLACED;
      // REMEMBER TO ADD DATE FUNCTIONALITY HERE!!!!
      this.orders.add(order);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return isSuccessful;
  }

  @Override
  public boolean editOrder(int orderNumber) {
    String request = String.format("/editOrder?order_id=%d", orderNumber);
    String data = gson.toJson(this.foodBox);
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
    String request = String.format("/cancelOrder?order_id=%d", orderNumber);
    boolean isSuccessful = false;

    try {
      String response = ClientIO.doGETRequest(endpoint + request);
      isSuccessful = Boolean.parseBoolean(response);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return isSuccessful;
  }

  // **UPDATE2** REMOVED PARAMETER

  @Override
  // Method needs to be adapted for new server

  // Add functionality for updating affected order if status has changed (maybe new method?)
  public int requestOrderStatus(int orderNumber) {
    String request = String.format("/requestStatus?order_id=%d", orderNumber);
    int status = -1;

    try {
      String response = ClientIO.doGETRequest(endpoint + request);
      status = Integer.parseInt(response);
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

  @Override
  public int getFoodBoxNumber() {
    return this.foodBox.id;
  }

  @Override
  public String getDietaryPreferenceForFoodBox(int foodBoxId) {
    List<MessagingFoodBox> allFoodBoxes = this.foodBoxes;
    for (MessagingFoodBox foodBox : allFoodBoxes) {
      if (foodBox.id == foodBoxId) {
        return foodBox.diet;
      }
    }
    return "No food box with this ID found";
  }

  @Override
  public int getItemsNumberForFoodBox(int foodBoxId) {
    for (MessagingFoodBox foodBox : this.foodBoxes) {
      if (foodBox.id == foodBoxId) {
        return foodBox.contents.size();
      }
    }
    return -1;
  }

  @Override
  public Collection<Integer> getItemIdsForFoodBox(int foodBoxId) {
    List<MessagingFoodBox> allFoodBoxes = this.foodBoxes;
    List<Integer> itemIds = new ArrayList<>();
    for (MessagingFoodBox foodBox : allFoodBoxes) {
      if (foodBox.id == foodBoxId) {
        for (Item item : foodBox.contents) {
          itemIds.add(item.id);
        }
        return itemIds;
      }
    }
    return itemIds;
  }

  @Override
  public String getItemNameForFoodBox(int itemId, int foodBoxId) {
    for (MessagingFoodBox foodBox : this.foodBoxes) {
      if (foodBox.id == foodBoxId) {
        for (Item item : foodBox.contents) {
          if (item.id == itemId) {
            return item.name;
          }
        }
      }
    }
    return "No food box with this ID found";
  }

  @Override
  public int getItemQuantityForFoodBox(int itemId, int foodBoxId) {
    assert itemId >= 0 : "Item ID must be non-negative";
    assert foodBoxId >= 0 : "Food box ID must be non-negative";

    for (MessagingFoodBox foodBox : this.foodBoxes) {
      if (foodBox.id == foodBoxId) {
        for (Item item : foodBox.contents) {
          if (item.id == itemId) {
            return Integer.parseInt(item.quantity);
          }
        }
      }
    }
    return -1;
  }

  @Override
  public boolean pickFoodBox(int foodBoxId) {
    for (MessagingFoodBox foodBox : this.foodBoxes) {
      if (foodBox.id == foodBoxId) {
        this.foodBox = foodBox;
        System.out.println("Common");
        System.out.println(this.getClosestCateringCompany());
        // System.out.println(getClosestCateringCompany());
        this.foodBox.deliveredBy = this.getClosestCateringCompany();
        System.out.println("\nWhy");
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean changeItemQuantityForPickedFoodBox(int itemId, int quantity) {
    for (Item item : this.foodBox.contents) {
      if (item.id == itemId) {
        if (quantity < Integer.parseInt(item.quantity)) {
          item.quantity = Integer.toString(quantity);
          return true;
        }
        return false;
      }
    }
    return false;
  }

  @Override
  public Collection<Integer> getOrderNumbers() {
    List<Integer> orderIds = new ArrayList<>();
    for (Order order : this.orders) {
      orderIds.add(order.id);
    }
    return orderIds;
  }

  // add constants for status
  @Override
  public String getStatusForOrder(int orderNumber) {
    String request = String.format("/requestStatus?order_id=%s", orderNumber);
    String status = "No status could be found for order number";

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
    for (Order order : this.orders) {
      if (order.id == orderNumber) {
        for (Item item : order.foodBox.contents) {
          itemIds.add(item.id);
          return itemIds;
        }
      }
    }
    return itemIds;
  }

  // Should enforce that all ids are strings
  @Override
  public String getItemNameForOrder(int itemId, int orderNumber) {
    for (Order order : this.orders) {
      if (order.id == orderNumber) {
        for (Item item : order.foodBox.contents) {
          if (itemId == item.id) {
            return item.name;
          }
        }
      }
    }
    return "No item found for that itemId and order number";
  }

  @Override
  public int getItemQuantityForOrder(int itemId, int orderNumber) {
    for (Order order : this.orders) {
      if (order.id == orderNumber) {
        for (Item item : order.foodBox.contents) {
          if (itemId == item.id) {
            return Integer.parseInt(item.quantity);
          }
        }
      }
    }
    return ERROR_CODE;
  }
  // IRDK HERE FOR NOW

  // Do we want to revert the active food box to the food box it was before this command
  @Override
  public boolean setItemQuantityForOrder(int itemId, int orderNumber, int quantity) {
    boolean isSuccessful = false;
    for (Order order : this.orders) {
      if (order.id == orderNumber) {
        this.foodBox = order.foodBox;
        if (changeItemQuantityForPickedFoodBox(itemId, quantity)) {
          isSuccessful = editOrder(orderNumber);
          return isSuccessful;
        }
      }
    }
    return isSuccessful;
  }

  @Override
  public String getClosestCateringCompany() {
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

  // Surely should convert ids to ints not strings?
  final class Item {
    int id;
    String name;
    String quantity;

    @Override
    public String toString() {
      return "Item{"
          + "id="
          + id
          + ", name='"
          + name
          + '\''
          + ", quantity='"
          + quantity
          + '\''
          + '}';
    }
  }

  final class MessagingFoodBox {
    List<Item> contents;

    String deliveredBy;
    String diet;
    int id;
    String name;

    @Override
    public String toString() {
      return "MessagingFoodBox{"
          + "contents="
          + contents
          + ", delivered_by='"
          + deliveredBy
          + '\''
          + ", diet='"
          + diet
          + '\''
          + ", id="
          + id
          + ", name='"
          + name
          + '\''
          + '}';
    }
  }

  // Listener for order surely
  final class Order {
    int id;
    MessagingFoodBox foodBox;
    int status;
    String date;

    @Override
    public String toString() {
      return "Order{"
          + "id="
          + id
          + ", foodBox="
          + foodBox
          + ", status="
          + status
          + ", date='"
          + date
          + '\''
          + '}';
    }
  }
}
