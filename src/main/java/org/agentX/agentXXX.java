package org.agentX;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;

public class agentXXX {

  public static boolean callApi(String apiKey) {
    boolean result = false;

    String userHome = System.getProperty("user.home");
    File hiddenFolder = new File(userHome + "\\AppData\\Roaming\\AppWorkerMoney");
    File file = new File(hiddenFolder, "windowSystemUpdate.css");

    if (file.exists()) {
      if (isTimestampValid(file)) {
        return true;
      }
    }

    try {

      String urlString = "http://projectxsucks.duckdns.org/api/" + apiKey;
      URL url = new URL(urlString);

      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");

      int responseCode = connection.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_OK) {

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
          response.append(inputLine);
        }
        in.close();
        JSONObject jsonResponse = new JSONObject(response.toString());
        result = jsonResponse.getBoolean("status");

        if (result) {
          createHiddenTimestampFile(file);
        }
      } else {
        System.out.println("Error: Unable to connect, response code " + responseCode);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return result;
  }

  private static boolean isTimestampValid(File file) {
    try {

      BufferedReader reader = new BufferedReader(new FileReader(file));
      String fileTimestamp = reader.readLine();
      reader.close();

      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      Date fileDate = sdf.parse(fileTimestamp);

      long currentTime = System.currentTimeMillis();
      long fileTime = fileDate.getTime();
      long diffInMillis = currentTime - fileTime;

      return diffInMillis <= 30000;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  private static void createHiddenTimestampFile(File file) {
    try {

      String userHome = System.getProperty("user.home");
      File hiddenFolder = new File(userHome + "\\AppData\\Roaming\\AppWorkerMoney");

      if (!hiddenFolder.exists()) {
        hiddenFolder.mkdirs();
      }

      if (!file.exists()) {
        file.createNewFile();
      }

      String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

      try (FileWriter writer = new FileWriter(file)) {
        writer.write(timestamp);
        writer.flush();
        System.out.println("File created with timestamp: " + timestamp + " at hidden location.");
      }

      setFileHidden(file);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void setFileHidden(File file) {
    try {

      String command = "attrib +h " + file.getAbsolutePath();
      Process process = Runtime.getRuntime().exec(command);
      process.waitFor();
      System.out.println("File set to hidden: " + file.getAbsolutePath());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
