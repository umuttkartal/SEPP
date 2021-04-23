package shield.utils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MessagingFoodBox {
  List<Item> contents;

  private String deliveredBy;
  private String diet;
  private int id;
  private String name;

  public List<Item> getContents() {
    return contents;
  }

  public Map<Integer, Item> getContentsAsMap() {
    return contents.stream().collect(Collectors.toMap(Item::getId, item -> item));
  }

  public void setContents(List<Item> contents) {
    this.contents = contents;
  }

  public String getDeliveredBy() {
    return deliveredBy;
  }

  public void setDeliveredBy(String deliveredBy) {
    this.deliveredBy = deliveredBy;
  }

  public String getDiet() {
    return diet;
  }

  public void setDiet(String diet) {
    this.diet = diet;
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
