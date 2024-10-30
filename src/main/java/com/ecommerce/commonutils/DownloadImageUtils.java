package com.ecommerce.commonutils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class DownloadImageUtils {

    public String downloadImage(String url) throws IOException {
        URI uri;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URL: " + url, e);
        }

        URL imageUrl = uri.toURL(); 
        String imageName = UUID.randomUUID() + ".jpg"; 
        File directory = new File("src/main/resources/static/img/profile_img/");
        if (!directory.exists()) {
            directory.mkdirs(); 
        }

        try (InputStream inputStream = imageUrl.openStream();
             OutputStream outputStream = new FileOutputStream(new File(directory, imageName))) {
            byte[] buffer = new byte[4096]; 
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            throw new IOException("Failed to download image from: " + url, e);
        }

        return imageName;
    }
}
