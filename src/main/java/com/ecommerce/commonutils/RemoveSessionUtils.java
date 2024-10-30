package com.ecommerce.commonutils;

import java.util.Enumeration;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
@Component
public class RemoveSessionUtils {
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
	}
}
