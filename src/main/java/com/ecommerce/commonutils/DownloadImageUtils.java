package com.ecommerce.commonutils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.UUID;

import org.springframework.stereotype.Component;
@Component
public class DownloadImageUtils {
	public String downloadImage(String url) throws IOException {
		@SuppressWarnings("deprecation")
		URL imageUrl = new URL(url);
		String imageName = UUID.randomUUID() + ".jpg";
		try (InputStream inputStream = imageUrl.openStream();
				OutputStream outputStream = new FileOutputStream(
						"src/main/resources/static/img/profile_img/" + imageName)) {
			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}

		}
		return imageName;
	}
}
