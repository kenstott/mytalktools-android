/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile.Server;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

// TODO: Auto-generated Javadoc

/**
 * The Class SendFile.
 */
class SendFile {

    /**
     * The Constant KILOBYTE.
     */
    private static final int KILOBYTE = 1024;

    /**
     * Execute.
     *
     * @param pathToOurFile the path to our file
     * @param username      the username
     * @param progress      the progress
     * @return the object[]
     */
    public static Object[] execute(final String pathToOurFile, final String username,
                                   final AsyncGetNewDatabase progress) {
        HttpURLConnection connection;
        DataOutputStream outputStream;

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = KILOBYTE * KILOBYTE;

        try {
            File inputFile = new File(pathToOurFile);
            FileInputStream fileInputStream = new FileInputStream(inputFile);

            /* The url server. */
            String urlServer = "https://www.mytalktools.com/dnn/UploadToLibrary.ashx";
            URL url = new URL(urlServer);
            connection = (HttpURLConnection) url.openConnection();

            // Allow Inputs & Outputs
            connection.setConnectTimeout(1000);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            // Enable POST method

            connection.setRequestMethod("POST");

            connection.setRequestProperty("Connection", "Keep-Alive");
            /* The boundary. */
            String boundary = "*****";
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            connection.addRequestProperty("user", username);

            outputStream = new DataOutputStream(connection.getOutputStream());

            /* The line end. */
            String lineEnd = "\r\n";
            /* The two hyphens. */
            String twoHyphens = "--";
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"user\"" + lineEnd);
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(username);
            outputStream.writeBytes(lineEnd);

            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\""
                    + new File(pathToOurFile).getName().replaceFirst("-", "") + "\"" + lineEnd);
            outputStream.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // Read file
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            progress.updateSecondaryMax((int) inputFile.length());

            while (bytesRead > 0) {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                progress.incrementSecondaryCount(bytesRead);
            }

            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Responses from the server (code and message)
            int serverResponseCode = connection.getResponseCode();
            String serverResponseMessage = connection.getResponseMessage();

            fileInputStream.close();
            outputStream.flush();
            outputStream.close();

            return new Object[]{serverResponseCode, serverResponseMessage};

        } catch (Exception ex) {
            return new Object[]{ex};
        }
    }
}
