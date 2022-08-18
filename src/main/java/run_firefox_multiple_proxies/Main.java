package run_firefox_multiple_proxies;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import run_firefox_multiple_proxies.modal.ErrorMessage;
import run_firefox_multiple_proxies.modal.ProxyAddress;

public class Main {
  private static final List<String> IGNORE_PROFILES = Arrays.asList("default", "default-release");
  private static final String FF_PREFERENCES_FILE = "prefs.js";

  private static final String USER_PROXY_START_STRING = "user_pref(\"network.proxy.ssl\", ";
  private static final String USER_PROXY_PORT_START_STRING =
      "user_pref(\"network.proxy.ssl_port\", ";
  private static final String USER_PROXY_TYPE_START_STRING = "user_pref(\"network.proxy.type\", ";

  private static final String BEFORE_PROXY_SETTING_START_STRING =
      "user_pref(\"media.hardware-video-decoding.failed\", ";

  public static void main(String[] args) throws Exception {
    String appDataFolder = System.getenv("APPDATA");
    // TODO: this is for testing. Change ot use args, and validate args
    String inputFilePath = "C:\\Users\\Lionel\\Desktop\\listOfProxies.txt";

    if (appDataFolder == null) {
      throw new RuntimeException(ErrorMessage.APPDATA_MISSING);
    }

    if (inputFilePath == null || !new File(inputFilePath).isFile()) {
      throw new RuntimeException(ErrorMessage.INVALID_FILE_ARG);
    }

    // this will throw an error if any are not formatted properly
    List<ProxyAddress> listOfProxies = parseProxiesFromFile(inputFilePath);

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
                String.format(ErrorMessage.NO_PROFILE, getProfileNameFromPath(directory)));
          }
        });

    if (directories.size() > listOfProxies.size()) {
      throw new RuntimeException(
          String.format(ErrorMessage.NOT_ENOUGH_PROXIES, listOfProxies.size(), directories.size()));
    }

    Map<File, ProxyAddress> directoryWithProxyMap = new HashMap<File, ProxyAddress>();
    Iterator<ProxyAddress> proxyIterator = listOfProxies.iterator();
    Iterator<File> direcotryIterator = directories.iterator();
    while (direcotryIterator.hasNext()) {
      ProxyAddress proxyAddress = proxyIterator.next();
      File directory = direcotryIterator.next();
      directoryWithProxyMap.put(directory, proxyAddress);
    }

    directoryWithProxyMap.forEach(
        (directory, proxy) -> {
          String fileToEdit = getFirefoxPreferencesFile(directory);
          List<String> fileContent = readFileContents(fileToEdit);

          String userProxyString = findStringStartingWith(fileContent, USER_PROXY_START_STRING);

          String lineToAdd1 = USER_PROXY_START_STRING + "\"" + proxy.getIpAddress() + "\");";
          String lineToAdd2 = USER_PROXY_PORT_START_STRING + "\"" + proxy.getPort() + "\");";
          String lineToAdd3 = USER_PROXY_TYPE_START_STRING + "\"1\");";

          if (userProxyString == null) {
            String elementBefore =
                findStringStartingWith(fileContent, BEFORE_PROXY_SETTING_START_STRING);

            if (elementBefore == null) {
              throw new RuntimeException(
                  String.format(
                      ErrorMessage.FF_PROPERTY_NOT_FOUND,
                      BEFORE_PROXY_SETTING_START_STRING,
                      FF_PREFERENCES_FILE));
            }

            fileContent.add(fileContent.indexOf(elementBefore) + 1, lineToAdd1);
            fileContent.add(fileContent.indexOf(elementBefore) + 2, lineToAdd2);
            fileContent.add(fileContent.indexOf(elementBefore) + 3, lineToAdd3);
          } else {
            int indexOfIp =
                fileContent.indexOf(findStringStartingWith(fileContent, USER_PROXY_START_STRING));
            int indexOfPort =
                fileContent.indexOf(
                    findStringStartingWith(fileContent, USER_PROXY_PORT_START_STRING));
            fileContent.set(indexOfIp, lineToAdd1);
            fileContent.set(indexOfPort, lineToAdd2);
          }

          try {
            Files.write(Paths.get(fileToEdit), fileContent, StandardCharsets.UTF_8);
          } catch (IOException e) {
            throw new RuntimeException("Could not write to file: " + fileToEdit);
          }
        });
  }

  private static String findStringStartingWith(List<String> haystack, String needleStartsWith) {
    return haystack.stream().filter(e -> e.startsWith(needleStartsWith)).findFirst().orElse(null);
  }

  private static List<ProxyAddress> parseProxiesFromFile(String filePath) {
    List<String> fileContents = readFileContents(filePath);

    return fileContents.stream()
        .filter(fc -> !fc.trim().isEmpty())
        .map(fc -> ProxyAddress.parse(fc.trim()))
        .collect(Collectors.toList());
  }

  private static List<String> readFileContents(String filePath) {
    try {
      return new ArrayList<>(
          Files.readAllLines(new File(filePath).toPath(), StandardCharsets.UTF_8));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static String getProfileNameFromPath(File file) {
    return file.getName().substring(file.getName().lastIndexOf('.') + 1);
  }

  private static String getFirefoxPreferencesFile(File profileFolderPath) {
    return profileFolderPath + "/" + FF_PREFERENCES_FILE;
  }
}
