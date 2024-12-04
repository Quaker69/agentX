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

        try {

            if (hiddenFolder.exists()) {
                //System.out.println("Hidden folder exists: " + hiddenFolder.getAbsolutePath());


                if (file.exists()) {
                    //System.out.println("File exists: " + file.getAbsolutePath());
   
                    if (isTimestampValid(file)) {
                        return true;  
                    }
                }
            }


            result = makeApiCall(apiKey);


            if (result) {
                if (!hiddenFolder.exists()) {
                    boolean folderCreated = hiddenFolder.mkdirs();  
                    System.out.println("Hidden folder created: " + folderCreated);
                }

                if (!file.exists()) {
                    boolean fileCreated = file.createNewFile();  
                   // System.out.println("File created: " + fileCreated);
                }


                storeTimestampInFile(file);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


    private static boolean makeApiCall(String apiKey) {
        boolean result = false;

        try {
            String urlString = "http://127.0.0.1:5000/api/" + apiKey;
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


                if (jsonResponse.getBoolean("status")) {
                    result = true;
                }
            } else {
                //System.out.println("Error: Unable to connect, response code " + responseCode);
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

            return diffInMillis <= 60000; 
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    private static void storeTimestampInFile(File file) {
        try {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            try (FileWriter writer = new FileWriter(file)) {
                writer.write(timestamp);
                writer.flush();
                //System.out.println("Timestamp updated in the file: " + timestamp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
