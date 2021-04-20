/** Interface for catering company clients. */

package shield;

public interface CateringCompanyClient extends CateringCompanyClientEndpoints {
  boolean isRegistered();

  String getName();

  String getPostCode();
}
