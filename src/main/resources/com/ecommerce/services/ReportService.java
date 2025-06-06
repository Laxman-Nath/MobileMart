package com.ecommerce.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface ReportService {
	public void generateOrdersToCsv(LocalDateTime startDateTime, LocalDateTime endDateTime
			,HttpServletResponse response) throws IOException;
	
	public void generateBill(int orderId,String filePath) throws Exception;
}
