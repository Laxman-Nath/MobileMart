package com.ecommerce.servicesimpl;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.dao.OrderDao;
import com.ecommerce.models.Order;
import com.ecommerce.models.OrderItem;
import com.ecommerce.services.ReportService;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import com.itextpdf.text.pdf.qrcode.ByteArray;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class ReportServiceImpl implements ReportService {
	@Autowired
	private OrderDao orderDao;

	@Override
	public void generateOrdersToCsv(LocalDateTime startDateTime, LocalDateTime endDateTime,
			HttpServletResponse response) throws IOException {
		List<Order> orders = this.orderDao.findByPlacedDateBetween(startDateTime, endDateTime);
		double totalCost = 0.0;
		response.setContentType("text/csv");
		response.setHeader("Content-Disposit",
				"attachment;filename=orders_" + startDateTime + "_to_" + endDateTime + ".csv");
		PrintWriter writer = response.getWriter();
		writer.println("OrderId,CustomerName,Order date,Total Amount");
		for (Order order : orders) {
			writer.println(order.getId() + "," + order.getCustomer().getName() + "," + order.getPlacedDate() + ","
					+ order.getTotalPrice());
			totalCost += order.getTotalPrice();
		}
		writer.println("TotalSales,,,\"" + totalCost + "\"");
		writer.flush();

	}

	@Override
	public void generateBill(int orderId, String filePath) throws DocumentException, IOException {

		String folder = "C:/Bills/";
		File directory = new File(folder);
		if (!directory.exists()) {
			directory.mkdirs();
		}
		Order order = this.orderDao.findById(orderId).get();
		List<OrderItem> orderItems = order.getOrderItems();
		String invoiceNumber = order.getInvoiceNumber();
		Document document = new Document();

		PdfWriter.getInstance(document, new FileOutputStream(filePath));
		Image logo = Image.getInstance("D://Ecommerce/MobileMart/src/main/resources/static/img/logo.jpg");
		logo.scaleToFit(150, 75);
		logo.setAlignment(Element.ALIGN_CENTER);

		document.open();
		document.add(logo);
		document.add(new Paragraph("\n"));
		Font boldFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
		Paragraph titleParagraph = new Paragraph("MOBILEMART", boldFont);
		titleParagraph.setAlignment(Element.ALIGN_CENTER);
		document.add(titleParagraph);

		Paragraph invoiceParagraph = new Paragraph("Invoice Number: " + invoiceNumber);
		invoiceParagraph.setAlignment(Element.ALIGN_CENTER);
		document.add(invoiceParagraph);

		Paragraph customerParagraph = new Paragraph("Customer Name: " + order.getCustomer().getName());
		customerParagraph.setAlignment(Element.ALIGN_CENTER);
		document.add(customerParagraph);
		document.add(new Paragraph("\n"));

		PdfPTable table = new PdfPTable(4);

		table.addCell("Quantity");
		table.addCell("Unit price");
		table.addCell("Description");
		table.addCell("Total price");

		for (OrderItem item : orderItems) {
			table.addCell(Integer.toString(item.getQuantity()));
			table.addCell(Double.toString(item.getProduct().getDiscountedPrice()));
			table.addCell(item.getProduct().getDimension() + "," + item.getProduct().getColor() + ","
					+ item.getProduct().getInternal() + "," + item.getProduct().getSecurity() + ","
					+ item.getProduct().getBType());
			table.addCell(Double.toString(item.getPrice()));

		}

		document.add(table);

		Paragraph total = new Paragraph("Total amount due: Rs." + order.getTotalPrice(), boldFont);
		total.setAlignment(Element.ALIGN_CENTER);
		Paragraph message = new Paragraph("\n Thank you for purchase!");
		message.setAlignment(Element.ALIGN_CENTER);
		document.add(total);
		document.add(message);
		document.close();

	}

}
