package run_firefox_multiple_proxies.modal;

public class ErrorMessage {
  public static final String APPDATA_MISSING =
      "FATAL ERROR, env variable APPDATA does not exist. Its used to get FireFox roaming folder location.";
  public static final String INVALID_FILE_ARG = "Invalid input file argument!";
  public static final String FF_PROPERTY_NOT_FOUND =
      "Cannot insert network properties, property %s not found inside %s";

  public static final String NO_PROFILE =
      "Profile does not exist for profile: %s Please make sure you've run FF with this profile atleast oncce";
  public static final String NOT_ENOUGH_PROXIES =
      "Not enough proxies for the # of profiles. Proxies: %s # of profiles (excluding defaults): %s Exiting.";
}
