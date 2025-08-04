package com.psfs.service.api;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;

public class PayslipGenerator {

	public static String generatePayslip(Map<String, Object> request) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		Map<String, String> data = new HashMap<>();
		for (Map.Entry<String, Object> entry : request.entrySet()) {
			data.put(entry.getKey(), String.valueOf(entry.getValue()));
		}

		PdfWriter writer = new PdfWriter(outStream);
		PdfDocument pdf = new PdfDocument(writer);
		Document doc = new Document(pdf, PageSize.A4);

		// Create a 2-column table: left = text (80%), right = logo (20%)
		Table headerTable = new Table(UnitValue.createPercentArray(new float[] { 80, 20 })).useAllAvailableWidth();

		String companyName = "POWER STAR FACILITY SERVICES\n";
		String address1 = "No.c-10, 1st Floor, Razack Garden Main Road\n";
		String address2 = "Arumbakkam, Chennai - 600 106\\n";

		// Combine all center-aligned text into one paragraph
		Paragraph textBlock = new Paragraph().add(new Text(companyName).setBold().setFontSize(16))
				.add(new Text(address1)).add(new Text(address2))
				.add(new Text("PAY SLIP FOR THE MONTH OF " + data.get("upload_month")).setBold())
				.setTextAlignment(TextAlignment.CENTER);

		// Left cell with centered multiline text
		Cell textCell = new Cell().add(textBlock).setTextAlignment(TextAlignment.CENTER)
				.setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER);

		headerTable.addCell(textCell);

		// Right cell with logo
		ClassPathResource imageFile = new ClassPathResource("logo.png");
		Image logo = new Image(ImageDataFactory.create(imageFile.getInputStream().readAllBytes()));
		logo.setHeight(60).setAutoScale(false);

		Cell logoCell = new Cell().add(logo).setTextAlignment(TextAlignment.RIGHT)
				.setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER);

		headerTable.addCell(logoCell);

		// Add to document
		doc.add(headerTable);
		doc.add(new Paragraph("\n"));

		// Employee Info Table
		Table empInfo = new Table(UnitValue.createPercentArray(new float[] { 25, 25, 25, 25 })).useAllAvailableWidth();
		empInfo.addCell(labelCell("I.D.No."));
		empInfo.addCell(valueCell(data.get("id_no")));
		empInfo.addCell(labelCell("AADHAR CARD NO."));
		empInfo.addCell(valueCell(data.get("aadhar_card_no")));

		empInfo.addCell(labelCell("NAME OF THE EMPLOYEE"));
		empInfo.addCell(valueCell(data.get("name_of_the_employee")));
		empInfo.addCell(labelCell("BANK A/C NO."));
		empInfo.addCell(valueCell(data.get("bank_ac_no")));

		empInfo.addCell(labelCell("DESINGNATION"));
		empInfo.addCell(valueCell(data.get("desingnation")));
		empInfo.addCell(labelCell("UAN .No."));
		empInfo.addCell(valueCell(data.get("uan_no")));

		empInfo.addCell(labelCell("SITE NAME"));
		empInfo.addCell(valueCell(data.get("site_name")));
		empInfo.addCell(labelCell("ESI. No."));
		empInfo.addCell(valueCell(data.get("esi_no")));

		empInfo.addCell(labelCell("GENDER"));
		empInfo.addCell(valueCell(data.get("gender")));
		empInfo.addCell(labelCell("TOTAL DAYS"));
		empInfo.addCell(valueCell(data.get("total_days")));
		doc.add(empInfo);

		doc.add(new Paragraph("\n"));

		// Salary & Deduction Table
		Table payTable = new Table(UnitValue.createPercentArray(new float[] { 25, 25, 25, 25 })).useAllAvailableWidth();
		payTable.addCell(headerCell("COMPONENTS"));
		payTable.addCell(headerCell(""));
		payTable.addCell(headerCell("DEDUCTIONS"));
		payTable.addCell(headerCell(""));

		addPayRow(payTable, "BASIC", data.get("basic"), "ESI", data.get("esi"));
		addPayRow(payTable, "D.A", data.get("d_a"), "EPF", data.get("epf"));
		addPayRow(payTable, "HRA", data.get("hra"), "PROFESSIONAL TAX", data.get("professional_tax"));
		addPayRow(payTable, "CONVEYANCE", data.get("conveyance"), "LABOUR WELFARE FUND",
				data.get("labour_welfare_fund"));
		addPayRow(payTable, "WASHING ALLW", data.get("washing_allw"), "UNIFORM DED", data.get("uniform_ded"));
		addPayRow(payTable, "OTHER ALLW", data.get("other_allw"), "SALARY ADVANCE", data.get("salary_advance"));

		payTable.addCell(labelCell(""));
		payTable.addCell(labelCell(""));
		payTable.addCell(labelCell("TOTAL DEDUCT"));
		payTable.addCell(valueCell(data.get("total_deduct")));

		payTable.addCell(labelCell("GROSS - SALARY"));
		payTable.addCell(valueCell(data.get("gross_salary")));
		payTable.addCell(labelCell("NET PAY"));
		payTable.addCell(valueCell(data.get("net_pay")));

		doc.add(payTable);

		doc.add(new Paragraph("\n\nFor Power Star Facility Services").setTextAlignment(TextAlignment.LEFT)
				.setFontSize(10));

		doc.add(new Paragraph("Authorised Signatory").setTextAlignment(TextAlignment.LEFT).setFontSize(10));

		doc.add(new Paragraph("Employee's Signature").setTextAlignment(TextAlignment.RIGHT).setFontSize(10));

		doc.close();

		byte[] pdfBytes = outStream.toByteArray();
		return Base64.getEncoder().encodeToString(pdfBytes);
	}

	private static void addPayRow(Table table, String leftLabel, String leftVal, String rightLabel, String rightVal) {
		table.addCell(labelCell(leftLabel));
		table.addCell(valueCell(leftVal));
		table.addCell(labelCell(rightLabel));
		table.addCell(valueCell(rightVal));
	}

	private static Cell labelCell(String text) {
		return new Cell().add(new Paragraph(text)).setBold().setFontSize(9);
	}

	private static Cell valueCell(String text) {
		return new Cell().add(new Paragraph(text != null ? text : "")).setFontSize(9);
	}

	private static Cell headerCell(String text) {
		return new Cell().add(new Paragraph(text)).setFontSize(9).setBold()
				.setBackgroundColor(ColorConstants.LIGHT_GRAY);
	}
}
