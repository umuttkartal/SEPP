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
  public void testGetCateringCompanies(){
    List<String> expectedResult = new LinkedList<String>();
    expectedResult.add("TastyFood");
    expectedResult.add("Yummy");
    assertEquals(client.getCateringCompanies(),expectedResult);
  }

  @Test
  public void testGetItemQuantityForFoodBox(){
    assertEquals(client.getItemQuantityForFoodBox(1, 1), 1);
    assertEquals(client.getItemQuantityForFoodBox(3, 2), 1);
    assertEquals(client.getItemQuantityForFoodBox(4, 3), 2);
    assertThrows(AssertionError.class, () -> {client.getItemQuantityForFoodBox(2,-3);});
  }
}
