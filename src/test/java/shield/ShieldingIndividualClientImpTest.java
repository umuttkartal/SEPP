/** */
package shield;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import shield.utils.MessagingFoodBox;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static shield.Generators.generateCHI;

/** */
public class ShieldingIndividualClientImpTest {
  private static final String clientPropsFilename = "client.cfg";

  private Properties clientProps;
  private ShieldingIndividualClient client;
  private SupermarketClient supermarket;
  private CateringCompanyClient catering;

  private Properties loadProperties(String propsFilename) {
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    Properties props = new Properties();

    try {
      InputStream propsStream = loader.getResourceAsStream(propsFilename);
      props.load(propsStream);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return props;
  }

  @BeforeEach
  public void setup() {
    clientProps = loadProperties(clientPropsFilename);

    client = new ShieldingIndividualClientImp(clientProps.getProperty("endpoint"));
    supermarket = new SupermarketClientImp(clientProps.getProperty("endpoint"));
    catering = new CateringCompanyClientImp(clientProps.getProperty("endpoint"));
  }

  @Test
  public void testShieldingIndividualNewRegistration() {
    String CHI = generateCHI();

    assertTrue(client.registerShieldingIndividual(CHI));
    assertTrue(client.isRegistered());
    assertEquals(client.getCHI(), CHI);
  }

  @Test
  public void testGetItemQuantityForFoodBox() {
    assertEquals(client.getItemQuantityForFoodBox(1, 1), 1);
    assertEquals(client.getItemQuantityForFoodBox(3, 3), 1);
    assertThrows(
        AssertionError.class,
        () -> {
          client.getItemQuantityForFoodBox(-22, 71);
        });
    assertThrows(
        AssertionError.class,
        () -> {
          client.getItemQuantityForFoodBox(3, -16);
        });
  }

  @Test
  public void testGetDietaryPreferenceForFoodBox() {
    assertEquals("none", client.getDietaryPreferenceForFoodBox(3));
    assertEquals("vegan", client.getDietaryPreferenceForFoodBox(5));
  }

  @Test
  public void testGetItemsNumberForFoodBox() {
    assertEquals(3, client.getItemsNumberForFoodBox(1));
    assertEquals(3, client.getItemsNumberForFoodBox(2));
    assertEquals(3, client.getItemsNumberForFoodBox(3));
    assertEquals(4, client.getItemsNumberForFoodBox(4));
    assertEquals(3, client.getItemsNumberForFoodBox(5));
  }

  @Test
  public void testGetItemIdsForFoodBox() {
    List<Integer> expectedResult = new ArrayList<Integer>();
    expectedResult.add(1);
    expectedResult.add(3);
    expectedResult.add(7);
    System.out.println(client.getItemIdsForFoodBox(2));
    assertEquals(expectedResult, client.getItemIdsForFoodBox(2));
  }

  @Test
  public void testShowFoodBoxes() {
    List<String> expectedResult = new ArrayList<String>();
    assertEquals(expectedResult, client.showFoodBoxes("wrongDiet"));
    expectedResult.add("5");
    assertEquals(expectedResult, client.showFoodBoxes("vegan"));
    expectedResult.set(0, "2");
    assertEquals(expectedResult, client.showFoodBoxes("pollotarian"));
    expectedResult.set(0, "1");
    expectedResult.add("3");
    expectedResult.add("4");
    assertEquals(expectedResult, client.showFoodBoxes("none"));
  }

  @Test
  public void testPlaceOrder() {
    String CHI = generateCHI();
    assertTrue(client.registerShieldingIndividual(CHI));
    assertTrue(client.isRegistered());
    assertEquals(CHI, client.getCHI());
    client.pickFoodBox(3);
    assertTrue(client.placeOrder());
    client.changeItemQuantityForPickedFoodBox(3, 2);
    int orderNumber = client.getOrderNumbers().iterator().next();
    assertTrue(client.editOrder(orderNumber));
    assertTrue(client.cancelOrder(orderNumber));
  }


  @Test
  public void testRequestOrderStatus() {
    Random rand = new Random();
    String CHI = generateCHI();
    String statusPacked = "packed";
    String statusDispatched = "dispatched";
    String statusDelivered = "delivered";
    String name = String.valueOf(rand.nextInt(10000));
    String postCode = "EH9_1LT";
    client.registerShieldingIndividual(CHI);
    catering.registerCateringCompany(name, postCode);
    client.pickFoodBox(2);
    client.placeOrder();

    int orderNumber = client.getOrderNumbers().iterator().next();

    assertTrue(catering.updateOrderStatus(orderNumber, statusPacked));
    assertEquals(client.requestOrderStatus(orderNumber), 1);
    assertTrue(catering.updateOrderStatus(orderNumber, statusDispatched));
    assertEquals(client.requestOrderStatus(orderNumber), 2);
    assertTrue(catering.updateOrderStatus(orderNumber, statusDelivered));
    assertEquals(client.requestOrderStatus(orderNumber), 3);
  }
}
