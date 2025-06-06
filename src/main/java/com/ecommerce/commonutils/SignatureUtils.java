package com.ecommerce.commonutils;
import java.security.Key;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
public class SignatureUtils {
	 public static String generateSignature(String data, String secretKey) {
	        try {
	            Key key = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
	            Mac mac = Mac.getInstance("HmacSHA256");
	            mac.init(key);
	            byte[] rawHmac = mac.doFinal(data.getBytes());

	            // Encode the signature in Base64
	            return Base64.getEncoder().encodeToString(rawHmac);
	        } catch (Exception e) {
	            throw new RuntimeException("Error generating signature", e);
	        }
	    }
}
