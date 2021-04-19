/**
 *
 */

package shield;

import org.junit.jupiter.api.*;

import java.util.*;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 */

public class ShieldingIndividualClientImpTest {
  private final static String clientPropsFilename = "client.cfg";

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
  public void testShieldingIndividualNewRegistration() {
    Random rand = new Random();
    String chi = String.valueOf(rand.nextInt(10000));

    assertTrue(client.registerShieldingIndividual(chi));
    assertTrue(client.isRegistered());
    assertEquals(client.getCHI(), chi);
  }


  @Test
  public void testGetItemQuantityForFoodBox(){
    assertEquals(client.getItemQuantityForFoodBox(1, 1), 1);
    assertEquals(client.getItemQuantityForFoodBox(3, 2), 1);
    assertEquals(client.getItemQuantityForFoodBox(4, 3), 2);
    assertThrows(AssertionError.class, () -> {client.getItemQuantityForFoodBox(2,-3);});
    assertThrows(AssertionError.class, () -> {client.getItemQuantityForFoodBox(-3,1);});
  }

  @Test
  public void testGetDietaryPreferenceForFoodBox(){
    assertEquals("none", client.getDietaryPreferenceForFoodBox(1));
    assertEquals("vegan", client.getDietaryPreferenceForFoodBox(5));
  }

  @Test
  public void testGetItemsNumberForFoodBox(){
    assertEquals(3, client.getItemsNumberForFoodBox(1));
    assertEquals(3, client.getItemsNumberForFoodBox(3));
    assertEquals(4, client.getItemsNumberForFoodBox(4));
  }

  @Test
  public void testGetItemIdsForFoodBox(){
    List<Integer> expectedResult = new ArrayList<Integer>();
    expectedResult.add(1);
    expectedResult.add(3);
    expectedResult.add(7);
    System.out.println(client.getItemIdsForFoodBox(2));
    assertEquals(expectedResult, client.getItemIdsForFoodBox(2));
  }

  @Test
  public void testShowFoodBoxes(){
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
  public void testPlaceOrder(){
    Random rand = new Random();
    String chi = String.valueOf(rand.nextInt(10000));
    assertTrue(client.registerShieldingIndividual(chi));
    assertTrue(client.isRegistered());
    assertEquals(chi, client.getCHI());
    client.pickFoodBox(1);
    assertTrue(client.placeOrder());
    client.changeItemQuantityForPickedFoodBox(2,1);
    assertTrue(client.editOrder(2));
    assertTrue(client.cancelOrder(2));
  }

}
