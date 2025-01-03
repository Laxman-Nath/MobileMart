package com.ecommerce.servicesimpl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.tomcat.util.http.fileupload.FileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.commonutils.DiscountUtils;
import com.ecommerce.commonutils.FileUploadUtils;
import com.ecommerce.dao.ProductDao;
import com.ecommerce.models.Customer;
import com.ecommerce.models.Product;
import com.ecommerce.services.ProductService;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import lombok.Setter;

@Service
public class ProductServiceImpl implements ProductService {
	@Autowired
	private ProductDao pd;
	@Autowired
	private DiscountUtils discountUtils;
	@Autowired
	private FileUploadUtils fileUploadUtils;

	@Override
	public Product addProduct(Product product, MultipartFile image) throws IOException {

		double discountedPrice = discountUtils.calculateDiscountedPrice(product);
		product.setDiscountedPrice(discountedPrice);
		product.setFile(image.getOriginalFilename());
		if (!fileUploadUtils.uploadFile(image, "src\\main\\resources\\static\\img\\product_img\\")) {

			return null;

		}
		return this.pd.save(product);

	}

	@Override
	public Boolean existByName(String name) {

		return this.pd.existsByName(name);
	}

	@Override
	public Boolean deleteProductById(int id) {
		if (this.pd.existsById(id)) {
			this.pd.deleteById(id);
			return true;
		}
		return false;
	}

	@Override
	public Page<Product> getAllProducts(String category, int page, int size) {
		Page<Product> products;
		Pageable pageable = PageRequest.of(page, size);
		if (category == null || category.equals("")) {

			products = this.pd.findAll(pageable);
		} else {
			products = this.pd.findByCategory(category, pageable);
		}
		return products;
	}

	@Override
	public Product findByProductId(int id) {

		return this.pd.findById(id).orElse(null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Page<Product> searchProduct(String keyword, int page, int size) {
//	   System.out.println(keyword);
		Set<Product> productsSet = new HashSet<>();
		if (keyword != "") {
			String searchedKeywordString = keyword.trim();

			String[] keywords = searchedKeywordString.split("\\s+");
			for (String str : keywords) {
				productsSet.addAll(this.pd.searchProducts(str));
			}
		}
		List<Product> productsList = new ArrayList<>(productsSet);
		int totalProducts = productsList.size();

		int startIndex = Math.min(page * size, totalProducts);
		int endIndex = Math.min(startIndex + size, totalProducts);

		List<Product> paginatedList = productsList.subList(startIndex, endIndex);

		return new PageImpl(paginatedList, PageRequest.of(page, size), totalProducts);
	}

	@Override
	public List<Product> getLatestProduct() {
		List<Product> products = this.pd.getLatestProducts(PageRequest.of(0, 10));

		return products;
	}

	@Override
	public Long getNumberOfProducts() {

		return this.pd.count();
	}

	@Override
	public Product editProduct(Product oldProduct, Product product, MultipartFile newfile) throws IOException {

		if (product.getName() != null)
			oldProduct.setName(product.getName());
		if (product.getDimension() != null)
			oldProduct.setDimension(product.getDimension());
		if (product.getWeight() != null)
			oldProduct.setWeight(product.getWeight());
		if (product.getBuild() != null)
			oldProduct.setBuild(product.getBuild());
		if (product.getSim() != null)
			oldProduct.setSim(product.getSim());
		if (product.getColor() != null)
			oldProduct.setColor(product.getColor());
		if (product.getType() != null)
			oldProduct.setType(product.getType());
		if (product.getSize() != null)
			oldProduct.setSize(product.getSize());
		if (product.getResolution() != null)
			oldProduct.setResolution(product.getResolution());
		if (product.getOs() != null)
			oldProduct.setOs(product.getOs());
		if (product.getChipSet() != null)
			oldProduct.setChipSet(product.getChipSet());
		if (product.getCpu() != null)
			oldProduct.setCpu(product.getCpu());
		if (product.getGpu() != null)
			oldProduct.setGpu(product.getGpu());
		if (product.getCardSlot() != null)
			oldProduct.setCardSlot(product.getCardSlot());
		if (product.getInternal() != null)
			oldProduct.setInternal(product.getInternal());
		if (product.getBFeatures() != null)
			oldProduct.setBFeatures(product.getBFeatures());
		if (product.getBVideo() != null)
			oldProduct.setBVideo(product.getBVideo());
		if (product.getFFeatures() != null)
			oldProduct.setFFeatures(product.getFFeatures());
		if (product.getFVideo() != null)
			oldProduct.setFVideo(product.getFVideo());
		if (product.getLoudSpeaker() != null)
			oldProduct.setLoudSpeaker(product.getLoudSpeaker());
		if (product.getSFeatures() != null)
			oldProduct.setSFeatures(product.getSFeatures());
		if (product.getBType() != null)
			oldProduct.setBType(product.getBType());
		if (product.getCharging() != null)
			oldProduct.setCharging(product.getCharging());
		if (product.getCFeatures() != null)
			oldProduct.setCFeatures(product.getCFeatures());
		if (product.getSecurity() != null)
			oldProduct.setSecurity(product.getSecurity());

		if (product.getStatus() != null)
			oldProduct.setStatus(product.getStatus());
		if (product.getCategory() != null)
			oldProduct.setCategory(product.getCategory());
		if (product.getPolicy() != null)
			oldProduct.setPolicy(product.getPolicy());
		if (product.getPrice() != 0)
			oldProduct.setPrice(product.getPrice());
		if (product.getQuantity() != 0)
			oldProduct.setQuantity(product.getQuantity());

		if (oldProduct.getDiscountPercent() != product.getDiscountPercent()) {
			oldProduct.setDiscountPercent(product.getDiscountPercent());
			double discountedPrice = discountUtils.calculateDiscountedPrice(product);
			oldProduct.setDiscountedPrice(discountedPrice);
		}

		if (!ObjectUtils.isEmpty(newfile) && !newfile.isEmpty()) {
			oldProduct.setFile(newfile.getOriginalFilename());
			if (!fileUploadUtils.uploadFile(newfile, "src\\main\\resources\\static\\img\\product_img\\")) {

				return null;

			}
		}

		return this.pd.save(oldProduct);

	}
}
