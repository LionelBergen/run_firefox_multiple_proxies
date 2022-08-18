package run_firefox_multiple_proxies;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
  private static final List<String> IGNORE_PROFILES = Arrays.asList("default", "default-release");
  private static final String FF_PREFERENCES_FILE = "prefs.js";

  public static void main(String[] args) throws Exception {
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
            .filter(file -> !IGNORE_PROFILES.contains(getProfileNameFromPath(file)))
            .collect(Collectors.toList());

    // Ensure every profile has a prefences file. Otherwise warn the user to run FF using the
    // profile atleast once.
    directories.forEach(
        directory -> {
          if (!new File(getFirefoxPreferencesFile(directory)).isFile()) {
            throw new RuntimeException(
                "Profile does not exist for profile: "
                    + getProfileNameFromPath(directory)
                    + " Please make sure you've run FF with this profile atleast oncce");
          }
        });

    directories.forEach(
        directory -> {
          List<String> fileContent;
          try {
            fileContent =
                new ArrayList<>(
                    Files.readAllLines(
                        new File(getFirefoxPreferencesFile(directory)).toPath(),
                        StandardCharsets.UTF_8));
          } catch (IOException e) {
            throw new RuntimeException(e);
          }

          fileContent.forEach(
              line -> {
                System.out.println(line);
              });

          // Files.write(FILE_PATH, fileContent, StandardCharsets.UTF_8);
        });
  }

  private static String getProfileNameFromPath(File file) {
    return file.getName().substring(file.getName().lastIndexOf('.') + 1);
  }

  private static String getFirefoxPreferencesFile(File profileFolderPath) {
    return profileFolderPath + "/" + FF_PREFERENCES_FILE;
  }
}
