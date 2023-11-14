package peersim.transport;

public class TestProtocolRouterInfo implements RouterInfo {
  private int router;

  public TestProtocolRouterInfo() {
    // Initialize the router to a default value or set it as needed
    this.router = -1; // u ninitialized state
  }

  @Override
  public void setRouter(int router) {
    // Set the router associated with this node
    this.router = router;
  }

  @Override
  public int getRouter() {
    // Get the router associated with this node
    return router;
  }
}
