package com.ecommerce.commonutils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
@Component
public class UserRoleUtils {

	public String getAuthenticateUserRole() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated()) {
			GrantedAuthority authority = authentication.getAuthorities().stream().findFirst().orElse(null);
			if (authority != null) {
				return authority.getAuthority();
			}
		}
		return null;
	}
}
