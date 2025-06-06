package com.ecommerce.constants;

import java.util.UUID;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

public class OrderConstant {
	public static final String CONTENT = "Dear [[name]],please click on the link below to verify your order."
			+ "<h3><a href=\"[[path]]\" target=\"_self\">VERIFY</a></h3> Thank you!";

	public static final String generateOrderVerificationCode() {
		return UUID.randomUUID().toString();
	}

	public static final String SUBJECT = "Verify order";
}
