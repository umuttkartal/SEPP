/** */
package shield;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/** */
public class CateringCompanyClientImpTest {
  private static final String clientPropsFilename = "client.cfg";

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
  public void testCateringCompanyNewRegistration() {
    Random rand = new Random();
    String name = String.valueOf(rand.nextInt(10000));
    String postCode = "EH9_1LT";

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
    assertTrue(client.registerCateringCompany(name, postCode));
  }

  @Test
  public void testCateringCompanyOrderFlow() {
    Random rand = new Random();
    String CHI = rand.nextInt(310000000) + "0";
    String statusPacked = "packed";
    String statusDispatched = "dispatched";
    String statusDelivered = "delivered";
    String name = String.valueOf(rand.nextInt(10000));
    String postCode = "EH9_1LT";
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
  public void testSupermarketOrderWrongStatus() {
    Random rand = new Random();
    String CHI = String.valueOf(rand.nextInt(10000));
    String statusDel = "delivered";
    String statusWrong = "wRoNg";
    String name = String.valueOf(rand.nextInt(10000));
    String postCode = "EH9_1LT";
    shieldedInd.registerShieldingIndividual(CHI);
    client.registerCateringCompany(name, postCode);
    shieldedInd.pickFoodBox(2);
    shieldedInd.placeOrder();
    int orderNumber = shieldedInd.getOrderNumbers().iterator().next();
    assertTrue(client.updateOrderStatus(orderNumber, statusDel));
    assertFalse(client.updateOrderStatus(orderNumber, statusWrong));
  }

  @Test
  public void testPostcodeValidation() {
    assertTrue(client.isValidPostcodeFormat("EH9_1KT"));
    assertTrue(client.isValidPostcodeFormat("EH15_1KT"));
    assertFalse(client.isValidPostcodeFormat("ST9_1LT"));
    assertFalse(client.isValidPostcodeFormat("Not valid"));
    assertFalse(client.isValidPostcodeFormat("EH9 1LT"));
    assertFalse(client.isValidPostcodeFormat("eh9_1lt"));
    assertFalse(client.isValidPostcodeFormat("EH9_1lT   A"));
  }
}
