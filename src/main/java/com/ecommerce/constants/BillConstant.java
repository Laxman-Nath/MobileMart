package com.ecommerce.constants;



import jakarta.servlet.http.HttpServletRequest;

public class BillConstant {
	public static final String CONTENT = "Dear [[name]],\n\nPlease find your bill attached.\n\nThank you for your purchase!";
	public static final String SUBJECT = "Purchase bill";

	public static String getURL(HttpServletRequest request) {
		String url = request.getRequestURL().toString();
		url = url.replace(request.getRequestURI(), "");
		return url;
	}

	public static String getPdfPath(int orderId) {
		String pdfPath = "C:\\Bills\\bill_" + orderId + ".pdf";
		return pdfPath;
	}
}
