package run_firefox_multiple_proxies;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
  private static final List<String> IGNORE_PROFILES = Arrays.asList("default", "default-release");

  public static void main(String[] args) {
    String appDataFolder = System.getenv("APPDATA");

    if (appDataFolder == null) {
      throw new RuntimeException(
          "FATAL ERROR, env variable APPDATA does not exist. Its used to get FireFox roaming folder location.");
    }

    String fireFoxProfilesDirectory = appDataFolder + "\\Mozilla\\Firefox\\Profiles";

    System.out.println("Path to Firefox profiles: " + fireFoxProfilesDirectory);

    List<File> directories =
        Arrays.asList(new File(fireFoxProfilesDirectory).listFiles(File::isDirectory));
    directories =
        directories.stream()
            .filter(
                file ->
                    !IGNORE_PROFILES.contains(
                        file.getName().substring(file.getName().lastIndexOf('.') + 1)))
            .collect(Collectors.toList());

    for (File file : directories) {
      System.out.println(file);
    }
  }
}
