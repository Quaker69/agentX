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
        boolean result = false; // Default result


        String userHome = System.getProperty("user.home");
        File hiddenFolder = new File(userHome + "\\AppData\\Roaming\\AppWorkerMoney");
        File file = new File(hiddenFolder, "windowSystemUpdate.css");

        if (file.exists()) {
            if (isTimestampValid(file)) {
                return true;
            }
        }


        try {

            String urlString = "http://ip_addr:8080/api/" + apiKey;
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
            // Read the timestamp from the file
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String fileTimestamp = reader.readLine();
            reader.close();


            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date fileDate = sdf.parse(fileTimestamp);

            // Get the current timestamp
            long currentTime = System.currentTimeMillis();
            long fileTime = fileDate.getTime();
            long diffInMillis = currentTime - fileTime;


            return diffInMillis <= 30000;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to create a file called windowSystemUpdate.css and write the current timestamp in a hidden folder
    private static void createHiddenTimestampFile(File file) {
        try {
            // Define the hidden directory (AppData is a hidden directory by default)
            String userHome = System.getProperty("user.home");  // Get the user's home directory
            File hiddenFolder = new File(userHome + "\\AppData\\Roaming\\AppWorkerMoney");

            // Create the folder if it doesn't exist
            if (!hiddenFolder.exists()) {
                hiddenFolder.mkdirs();
            }

            // Create the hidden file inside that folder if it doesn't exist
            if (!file.exists()) {
                file.createNewFile();
            }

            // Get the current timestamp
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            // Write only the timestamp into the file
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(timestamp);  // Only the timestamp is written
                writer.flush();
                System.out.println("File created with timestamp: " + timestamp + " at hidden location.");
            }

            // Now set the file as hidden using Windows' attrib command
            setFileHidden(file);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to set the file as hidden using the Windows 'attrib' command
    private static void setFileHidden(File file) {
        try {
            // Use the Windows 'attrib' command to set the file as hidden
            String command = "attrib +h " + file.getAbsolutePath();
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();  // Wait for the process to finish
            System.out.println("File set to hidden: " + file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
