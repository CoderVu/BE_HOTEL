package com.example.booking_hotel.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

public class ImageGeneral {
    public static String fileToBase64(InputStream inputStream) {
        if (inputStream == null)
            return "";

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead = -1;

        try {
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            byte[] imageBytes = outputStream.toByteArray();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            return base64Image;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}