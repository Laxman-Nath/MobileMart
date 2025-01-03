package com.ecommerce.servicesimpl;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.commonutils.FileUploadUtils;
import com.ecommerce.dao.CategoryDao;
import com.ecommerce.models.Category;
import com.ecommerce.services.CategoryService;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import ch.qos.logback.core.status.Status;

@Service
public class CategoryServiceImpl implements CategoryService {
	@Autowired
	private CategoryDao cd;
	@Autowired
	private FileUploadUtils fileUploadUtils;

	@Override
	public Category addCategory(Category category, MultipartFile image, Boolean status) throws IOException {
		Category category2 = null;
		category.setFile(image.getOriginalFilename());
		category.setActive(status);
		if (fileUploadUtils.uploadFile(image, "src\\main\\resources\\static\\img\\category_img\\")) {
			category2 = cd.save(category);
		}

		return category2;

	}

	@Override
	public List<Category> listAllCategory() {

		return this.cd.findAll();
	}

	@Override
	public boolean existsByName(String name) {
		return this.cd.existsByName(name);
	}

	@Override
	public boolean deleteCategoryById(int id) {
		if (this.cd.existsById(id)) {
			this.cd.deleteById(id);
			return true;
		}
		return false;
	}

	@Override
	public Category findByCategoryId(int id) {
		Optional<Category> category = this.cd.findById(id);

		return category.orElse(null);
	}

	@Override
	public List<Category> findByIsActiveTrue() {
		return this.cd.findByIsActiveTrue();

	}

	@Override
	public Category updateCategory(Category category, MultipartFile newfile, Boolean status) throws IOException {
		Category oldcategory = cd.findById(category.getId()).orElse(null);
		if (oldcategory == null) {
			return oldcategory;
		}
		oldcategory.setName(category.getName());
		oldcategory.setActive(status);
		if (!ObjectUtils.isEmpty(newfile) && !newfile.isEmpty()) {
			oldcategory.setFile(newfile.getOriginalFilename());
			if (!fileUploadUtils.uploadFile(newfile, "src\\main\\resources\\static\\img\\category_img\\")) {
				return null;

			}
		}
		return this.cd.save(oldcategory);
	}

}
