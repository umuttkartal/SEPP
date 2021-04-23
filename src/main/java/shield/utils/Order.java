package shield.utils;

import java.time.LocalDateTime;

// Listener for order surely
public class Order {

  private final String CHI;
  private int id;
  private MessagingFoodBox foodBox;
  private int status;
  private LocalDateTime datePlaced;

  public Order(String CHI, int id, MessagingFoodBox foodBox, int status, LocalDateTime datePlaced) {
    this.CHI = CHI;
    this.id = id;
    this.foodBox = foodBox;
    this.status = status;
    this.datePlaced = datePlaced;
  }

  public String getCHI() {
    return CHI;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public MessagingFoodBox getFoodBox() {
    return foodBox;
  }

  public void setFoodBox(MessagingFoodBox foodBox) {
    this.foodBox = foodBox;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public LocalDateTime getDatePlaced() {
    return datePlaced;
  }

  public void setDatePlaced(LocalDateTime datePlaced) {
    this.datePlaced = datePlaced;
  }

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
        + datePlaced
        + '\''
        + '}';
  }
}
