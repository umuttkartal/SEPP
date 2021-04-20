/**
 * Interface for the communication endpoints of an application for a shielding individual.
 *
 * <p>These endpoints represent the communication endpoints to the server
 *
 * @author
 */

package shield;

import java.util.Collection;

public interface ShieldingIndividualClientEndpoints {
  // **UPDATE2** COMMENT ONLY - CHANGES ON THE SERVER RETURN

  /**
   * Returns true if the operation occurred correctly.
   *
   * <p>This method returns true if the operation occurred correctly (this includes
   * re-registrations) and false if input incorrect (null or CHI number not respecting this format:
   * https://datadictionary.nhs.uk/attributes/community_health_index_number.html) or any of the data
   * retrieved from the server for the shielding individual is null.
   *
   * @param CHI CHI number of the shiedling individual
   * @return true if the operation occurred correctly
   */
  boolean registerShieldingIndividual(String CHI);

  // **UPDATE** javadoc comment fix

  /**
   * Returns collection of food box ids if the operation occurred correctly.
   *
   * @param dietaryPreference one of the
   * @return collection of food box ids
   */
  Collection<String> showFoodBoxes(String dietaryPreference);

  // **UPDATE2** REMOVED PARAMETER

  /**
   * Returns true if the operation occurred correctly.
   *
   * @return true if the operation occurred correctly
   */
  boolean placeOrder();

  /**
   * Returns true if the operation occurred correctly.
   *
   * @param orderNumber the order number
   * @return true if the operation occurred correctly
   */
  boolean editOrder(int orderNumber);

  /**
   * Returns true if the operation occurred correctly.
   *
   * @param orderNumber the order number
   * @return true if the operation occurred correctly
   */
  boolean cancelOrder(int orderNumber);

  /**
   * Returns true if the operation occurred correctly.
   *
   * @param orderNumber the order number
   * @return true if the operation occurred correctly
   */
  int requestOrderStatus(int orderNumber);

  // **UPDATE**

  /**
   * Returns collection of catering companies and their locations.
   *
   * @return collection of catering companies and their locations
   */
  Collection<String> getCateringCompanies();

  // **UPDATE**

  /**
   * Returns the distance between two locations based on their post codes.
   *
   * @param postCode1 post code of one location
   * @param postCode2 post code of another location
   * @return the distance as a float between the two locations
   */
  float getDistance(String postCode1, String postCode2);
}
