/**
 *
 */

package shield;

import org.junit.jupiter.api.*;

import java.util.Collection;
import java.util.Properties;
import java.time.LocalDateTime;
import java.io.InputStream;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 */

public class CateringCompanyClientImpTest {
  private final static String clientPropsFilename = "client.cfg";

  private Properties clientProps;
  private CateringCompanyClient client;
  private ShieldingIndividualClientImp shieldedInd;

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

    client = new CateringCompanyClientImp(clientProps.getProperty("endpoint"));
    shieldedInd = new ShieldingIndividualClientImp(clientProps.getProperty("endpoint"));
  }


  @Test
  public void testCateringCompanyNewRegistration() {
    Random rand = new Random();
    String name = String.valueOf(rand.nextInt(10000));
    String postCode = String.valueOf(rand.nextInt(10000));

    assertTrue(client.registerCateringCompany(name, postCode));
    assertTrue(client.isRegistered());
    assertEquals(client.getName(), name);
    assertEquals(client.getPostCode(), postCode);
  }

  @Test
  public void testAlreadyRegistered() {
    Random rand = new Random();
    String name = String.valueOf(rand.nextInt(10000));
    String postCode = "EH9_1LT";

    assertTrue(client.registerCateringCompany(name, postCode));
    assertTrue(client.isRegistered());
    assertFalse(client.registerCateringCompany(name, postCode));
  }

  @Test
  public void testCateringCompanyOrderFlow() {
    Random rand = new Random();
    String CHI = rand.nextInt(310000000) + "0";
    // int orderNumber = 0; // Can't just pick any random number
    String statusPacked = "packed";
    String statusDispatched = "dispatched";
    String statusDelivered = "delivered";
    String name = String.valueOf(rand.nextInt(10000));
    String postCode = "EH16_5AY";
    shieldedInd.registerShieldingIndividual(CHI);
    client.registerCateringCompany(name, postCode);
    shieldedInd.pickFoodBox(2);
    shieldedInd.placeOrder();

    int orderNumber = shieldedInd.getOrderNumbers().iterator().next();
    assertTrue(client.updateOrderStatus(orderNumber, statusPacked));
    assertTrue(client.updateOrderStatus(orderNumber, statusDispatched));
    assertTrue(client.updateOrderStatus(orderNumber, statusDelivered));
  }

  @Test
  public void testSupermarketOrderWrongStatusOrder() {
    Random rand = new Random();
    String CHI = rand.nextInt(310000000) + "0";
    // String CHI = "1105871232";
    // int orderNumber = rand.randInt(10000); // Can't just pick any random number
    String statusPacked = "packed";
    String statusFake = "fake";
    String name = String.valueOf(rand.nextInt(10000));
    String postCode = "EH16_5AY";
    shieldedInd.registerShieldingIndividual(CHI);
    client.registerCateringCompany(name, postCode);
    shieldedInd.pickFoodBox(2);
    shieldedInd.placeOrder();
    int orderNumber = shieldedInd.getOrderNumbers().iterator().next();
    assertTrue(client.updateOrderStatus(orderNumber, statusPacked));
    assertFalse(client.updateOrderStatus(orderNumber, statusFake));
  }
}
