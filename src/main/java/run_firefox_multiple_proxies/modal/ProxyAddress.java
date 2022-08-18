package run_firefox_multiple_proxies.modal;

public class ProxyAddress {
  private String ipAddress;
  private int port;

  public ProxyAddress(String ipAddress, int port) {
    this.ipAddress = ipAddress;
    this.port = port;
  }

  public static ProxyAddress parse(String ipPort) {
    String ipAddress = ipPort.split(":")[0];
    int port = Integer.valueOf(ipPort.split(":")[1]);

    return new ProxyAddress(ipAddress, port);
  }

  public String getIpAddress() {
    return ipAddress;
  }

  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  @Override
  public String toString() {
    return this.ipAddress + ":" + this.port;
  }
}
