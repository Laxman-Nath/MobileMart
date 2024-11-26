package com.ecommerce.models;

import lombok.Data;

@Data
public class PaymentRequest {
	  private String merchantId;
	    private String amount;
	    private String orderId;
	    private String productName;
	    private String returnUrl;
	    private String cancelUrl;
}
