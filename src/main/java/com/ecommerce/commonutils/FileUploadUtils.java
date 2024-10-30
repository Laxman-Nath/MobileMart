package com.ecommerce.commonutils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
@Component
public class FileUploadUtils {
	public boolean uploadFile(MultipartFile file, String uploadPath) throws IOException {
		File f = new File(uploadPath);
		if (!f.exists()) {
			f.mkdir();
		}
		String storePath = uploadPath.concat(file.getOriginalFilename());

		long result = Files.copy(file.getInputStream(), Path.of(storePath), StandardCopyOption.REPLACE_EXISTING);
		if (result > 0) {
			return true;
		}
		return false;
	}
}
