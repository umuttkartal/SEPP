package shield;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ShieldingIndividualClientImp implements ShieldingIndividualClient {

  private String endpoint;
  private int individual_id;
  private int order_id;
  private static final Gson gson = new Gson();

  // Nested, might not work
  final class Item {
    String id;
    String name;
    String quantity;
  }

  final class MessagingFoodBox {
    transient List<Item> contents;

    String delivered_by;
    String diet;
    String id;
    String name;
  }

  private MessagingFoodBox foodBox = new MessagingFoodBox();
  private List<MessagingFoodBox> foodBoxes = new ArrayList<MessagingFoodBox>();

  public ShieldingIndividualClientImp(String endpoint) {
    this.endpoint = endpoint;
    String request = "/showFoodBox?orderOption=catering&dietaryPreference=none";
    try {
      String response = ClientIO.doGETRequest(endpoint + request);
      Type listType = new TypeToken<List<MessagingFoodBox>>() {} .getType();
      this.foodBoxes = new Gson().fromJson(response, listType);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public boolean registerShieldingIndividual(String CHI) {
    String request = String.format("cancelOrder?order_id=%s", CHI);
    boolean isSuccessful = false;

    try {
      String response = ClientIO.doGETRequest(endpoint + request);
      System.out.println(response);
      if (!response.equals("already registered")) {
        isSuccessful = true;
        this.individual_id = Integer.parseInt(CHI);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return isSuccessful;
  }

  @Override
  public Collection<String> showFoodBoxes(String dietaryPreference) {
    String request = String.format("/showFoodBox?orderOption=%s&dietaryPreference=%s", "none", dietaryPreference);
    List<MessagingFoodBox> responseBoxes = new ArrayList<MessagingFoodBox>();
    List<String> boxIds = new ArrayList<String>();

    try {
      String response = ClientIO.doGETRequest(endpoint + request);
      Type listType = new TypeToken<List<MessagingFoodBox>>() {} .getType();
      responseBoxes = new Gson().fromJson(response, listType);

      for (MessagingFoodBox b : responseBoxes) {
        boxIds.add(b.id);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return boxIds;
  }

  // **UPDATE2** REMOVED PARAMETER
  @Override
  public boolean placeOrder() {
    String request = String.format("placeOrder?individual_id=%d", this.individual_id);
    String data = gson.toJson(this.foodBox);
    boolean isSuccessful = false;

    try {
      String response = ClientIO.doPOSTRequest(endpoint + request, data);
      // NECESSARY FOR NEW SERVER this.order_id = Integer.parseInt(response);
      isSuccessful = Boolean.parseBoolean(response);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return isSuccessful;
  }

  @Override
  public boolean editOrder(int orderNumber) {
    String request = String.format("editOrder?order_id=%d", orderNumber);
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
    String request = String.format("cancelOrder?order_id=%d", orderNumber);
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
  public boolean requestOrderStatus(int orderNumber) {
    String request = String.format("requestStatus?order_id=%d", orderNumber);
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
  public Collection<String> getCateringCompanies() {
    String request = "getCaterers";
    List<String> caterers = new ArrayList<>();
    List<String> boxIds = new ArrayList<>();

    try {
      String response = ClientIO.doGETRequest(endpoint + request);
      Type listType = new TypeToken<List<String>>() {} .getType();
      caterers = new Gson().fromJson(response, listType);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return caterers;
  }

  // **UPDATE** //OVERLOAD HERE POSSIBLY TO HANDLE NAMES AS WELL AS POSTCODES?
  @Override
  public float getDistance(String postCode1, String postCode2) {
    String request = String.format("distance?postcode1=%s,postcode2=%s", postCode1, postCode2);
    float distance = Float.POSITIVE_INFINITY;

    try {
      String response = ClientIO.doGETRequest(endpoint + request);
      distance = Float.parseFloat(response);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return distance;
  }

// TODO: WRITE FILE OR LIST TO KEEP REGISTERED IDs
  @Override
  public boolean isRegistered() {
    return false;
  }

  @Override
  public String getCHI() {
    return Integer.toString(this.individual_id);
  }

  @Override
  public int getFoodBoxNumber() {
    return Integer.parseInt(this.foodBox.id);
  }

  @Override
  public String getDietaryPreferenceForFoodBox(int foodBoxId) {
    for (MessagingFoodBox foodBox : this.foodBoxes) {
      if (Integer.parseInt(foodBox.id) == foodBoxId) {
        return foodBox.diet;
      }
    }
    return "No food box with this ID found";
  }

  @Override
  public int getItemsNumberForFoodBox(int foodBoxId) {
    for (MessagingFoodBox foodBox : this.foodBoxes) {
      if (Integer.parseInt(foodBox.id) == foodBoxId) {
        return foodBox.contents.size();
      }
    }
    return -1;
  }

  @Override
  public Collection<Integer> getItemIdsForFoodBox(int foodBoxId) {
    List<Integer> item_ids = new ArrayList<Integer>();
    for (MessagingFoodBox foodBox : this.foodBoxes) {
      if (Integer.parseInt(foodBox.id) == foodBoxId) {
        for (Item item : foodBox.contents) {
          item_ids.add(Integer.parseInt(item.id));
        }
      }
    }
    return item_ids;
  }

  @Override
  public String getItemNameForFoodBox(int itemId, int foodBoxId) {
    for (MessagingFoodBox foodBox : this.foodBoxes) {
      if (Integer.parseInt(foodBox.id) == foodBoxId) {
        for (Item item : foodBox.contents) {
          if (Integer.parseInt(item.id) == itemId) {
            return item.name;
          }
        }
      }
    }
    return "No food box with this ID found";
  }

  @Override
  public int getItemQuantityForFoodBox(int itemId, int foodBoxId) {
    for (MessagingFoodBox foodBox : this.foodBoxes) {
      if (Integer.parseInt(foodBox.id) == foodBoxId) {
        for (Item item : foodBox.contents) {
          if (Integer.parseInt(item.id) == itemId) {
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
      if (Integer.parseInt(foodBox.id) == foodBoxId) {
        this.foodBox = foodBox;
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean changeItemQuantityForPickedFoodBox(int itemId, int quantity) {
    for (Item item : this.foodBox.contents) {
      if (Integer.parseInt(item.id) == itemId) {
        if (quantity > Integer.parseInt(item.quantity)) {
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
    return null;
  }

  @Override
  public String getStatusForOrder(int orderNumber) {
    String request = String.format("requestStatus?order_id=%s", orderNumber);
    String status = "No status could be found for order number";

    try {
      status = ClientIO.doGETRequest(endpoint + request);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return status;
  }
  //IRDK HERE FOR NOW
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

  @Override
  public String getClosestCateringCompany() {
    return null;
  }
}
