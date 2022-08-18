package run_firefox_multiple_proxies;

public class Main {
  public static void main(String[] args) {
    String appDataFolder = System.getenv("APPDATA");

    if (appDataFolder == null) {
      throw new RuntimeException(
          "FATAL ERROR, env variable APPDATA does not exist. Its used to get FireFox roaming folder location.");
    }
  }
}
