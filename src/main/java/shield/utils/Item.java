package shield.utils;

// Surely should convert ids to ints not strings?
public class Item {
  private int id;
  private String name;
  private int quantity;

  public boolean changeQuantity(int quantity) {
    if (quantity < this.quantity) {
      setQuantity(quantity);
      return true;
    }
    return false;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  @Override
  public String toString() {
    return "Item{" + "id=" + id + ", name='" + name + '\'' + ", quantity='" + quantity + '\'' + '}';
  }
}
