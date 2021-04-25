/**
 * Interface for the communication endpoints of an application for a catering company.
 *
 * <p>These endpoints represent the communication endpoints to the server
 *
 * @author
 */

package shield;

public interface CateringCompanyClientEndpoints {
  /**
   * Returns true if the operation occurred correctly.
   *
   * @param name name of the business
   * @param postCode post code of the business
   * @return true if the operation occurred correctly
   */
  boolean registerCateringCompany(String name, String postCode);

  /**
   * Returns true if the operation occurred correctly.
   *
   * @param orderNumber the order number
   * @param status status of the order for the requested number
   * @return true if the operation occurred correctly
   */
  boolean updateOrderStatus(int orderNumber, String status);

  boolean isValidPostcodeFormat(String postcode);
}
