package com.ecommerce.commonutils;

import java.io.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import com.ecommerce.models.Customer;
import jakarta.mail.internet.MimeMessage;
@Component
public class EmailUtils {
	@Autowired
	private JavaMailSender mailSender;

	public void sendEmail(String subject, String url, Customer customer, String content, boolean isOrder,
			boolean isBill, String filePath) {

		String to = customer.getEmail();

		MimeMessage message = mailSender.createMimeMessage();

		try {

			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setFrom("MobileMart");
			content = content.replace("[[name]]", customer.getName());
			if (isOrder) {
				url = url + "/user/verifyOrder?code=" + customer.getCode();
			} else if (isBill) {
				url = url + "/sendBill";
				FileSystemResource file = new FileSystemResource(new File(filePath));
				helper.addAttachment("bill.pdf", file);

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
}
