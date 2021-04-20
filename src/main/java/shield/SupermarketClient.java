/** */

package shield;

public interface SupermarketClient extends SupermarketClientEndpoints {
  boolean isRegistered();

  String getName();

  String getPostCode();
}
