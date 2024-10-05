package com.ecommerce.servicesimpl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Enumeration;

import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.ecommerce.dao.CustomerDao;
import com.ecommerce.models.Customer;
import com.ecommerce.services.CommonService;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

@Service
public class CommonServiceImpl implements CommonService {
	@Autowired
	private CustomerDao customerDao;
	@Autowired
	private JavaMailSender mailSender;

	@Override
	public void removeSessionAttribute() {
		HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.getRequestAttributes()))
				.getRequest();
		HttpSession session = request.getSession();
		Enumeration<String> attributeNames = session.getAttributeNames();

		while (attributeNames.hasMoreElements()) {
			String attributeName = attributeNames.nextElement();
//	        System.out.println("Removing attribute: " + attributeName);  // Debug line

			if (attributeName.equals("error")) {
				session.removeAttribute("error");
			} else if (attributeName.equals("success")) {
				session.removeAttribute("success");
			} else if (attributeName.equals("Deleteerror")) {
				session.removeAttribute("Deleteerror");
			} else if (attributeName.equals("Deletesuccess")) {
				session.removeAttribute("Deletesuccess");
			} else if (attributeName.equals("EditSuccess")) {
				session.removeAttribute("EditSuccess");
			} else if (attributeName.equals("EditError")) {
				session.removeAttribute("EditError");
			} else if (attributeName.equals("addProductSuccess")) {
				session.removeAttribute("addProductSuccess");
			} else if (attributeName.equals("addProductError")) {
				session.removeAttribute("addProductError");
			} else if (attributeName.equals("ProductEditError")) {
				session.removeAttribute("ProductEditError");
			} else if (attributeName.equals("deleteProductError")) {
				session.removeAttribute("deleteProductError");
			} else if (attributeName.equals("ProductEditSuccess")) {
				session.removeAttribute("ProductEditSuccess");
			} else if (attributeName.equals("deleteProductSuccess")) {
				session.removeAttribute("deleteProductSuccess");
			} else if (attributeName.equals("registerSuccess")) {
				session.removeAttribute("registerSuccess");
			} else if (attributeName.equals("registerError")) {
				session.removeAttribute("registerError");
			} else if (attributeName.equals("saleSuccess")) {
				session.removeAttribute("saleSuccess");
			} else if (attributeName.equals("saleError")) {
				session.removeAttribute("saleError");
			}

		}

//		session.removeAttribute("error");
//		session.removeAttribute("success");
//		session.removeAttribute("Deleteerror");
//		session.removeAttribute("Deletesuccess");
	}

	@Override
	public void sendEmail(String subject, String url, Customer customer, String content, boolean isOrder,
			boolean isBill,String filePath) {

		String to = customer.getEmail();

		MimeMessage message = mailSender.createMimeMessage();

		try {

			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setFrom("Laxman");
			content = content.replace("[[name]]", customer.getName());
			if (isOrder) {
				url = url + "/verifyOrder?code=" + customer.getCode();
			} else if (isBill) {
				url = url + "/sendBill";
                FileSystemResource file=new FileSystemResource(new File(filePath));
                helper.addAttachment("bill.pdf",file);
				
			} else {
				url = url + "/resetPassword?code=" + customer.getCode();
			}
			content = content.replace("[[path]]", url);
			helper.setText(content, true);
			System.out.println("Email is send successfully!");
			mailSender.send(message);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public boolean verifyCode(String code) {

		if (this.customerDao.existsByCode(code)) {
			return true;
		}
		return false;
	}

	@Override
	public Customer findByCode(String code) {

		return this.customerDao.findByCode(code);
	}

}
