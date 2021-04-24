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
  }

  @Test
  public void testGetEndpoint() {
    clientProps = loadProperties(clientPropsFilename);
    assertEquals(clientProps.getProperty("endpoint"), client.getEndpoint());
  }

  /*
  @Test
  public void testGetOrders() {
    clientProps = loadProperties(clientPropsFilename);
    assertEquals(clientProps.getProperty("orders"), client.getOrders());
  }
   */

  @Test
  public void testGetPostCode() {
    clientProps = loadProperties(clientPropsFilename);
    assertEquals(clientProps.getProperty("postcode"), client.getPostCode());
  }

  @Test
  public void testGetName() {
    clientProps = loadProperties(clientPropsFilename);
    assertEquals(clientProps.getProperty("name"), client.getName());
  }

  @Test
  public void testGetSurname() {
    clientProps = loadProperties(clientPropsFilename);
    assertEquals(clientProps.getProperty("surname"), client.getSurname());
  }

  @Test
  public void testGetPhoneNumber() {
    clientProps = loadProperties(clientPropsFilename);
    assertEquals(clientProps.getProperty("phoneNumber"), client.getPhoneNumber());
  }

  /*
  @Test
  public void testGetLatestOrderTime() {
    clientProps = loadProperties(clientPropsFilename);
    assertEquals(clientProps.getProperty("latestOrderTime"), client.getLatestOrderTime());
  }

   */

  /*
  @Test
  public void testGetLiveFoodBox() {
    clientProps = loadProperties(clientPropsFilename);
    assertEquals(clientProps.getProperty("liveFoodBox"), client.getLiveFoodBox());
  }

   */

  @Test
  public void testShieldingIndividualNewRegistration() {
    Random rand = new Random();
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
    Random rand = new Random();
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
}
