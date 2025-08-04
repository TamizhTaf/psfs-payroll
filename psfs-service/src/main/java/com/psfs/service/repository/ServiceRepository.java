package com.psfs.service.repository;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.psfs.service.api.util.ServiceUtil;
import com.psfs.service.common.AppPropertyService;

public class ServiceRepository {
	private static final Log LOG = LogFactory.getLog(ServiceRepository.class);

	public static final String DB_URL = AppPropertyService.getProperty("spring.datasource.url");
	public static final String DB_USER = AppPropertyService.getProperty("spring.datasource.username");
	public static final String DB_PASS = AppPropertyService.getProperty("spring.datasource.password");
	public static final String TOKEN_KEY = "apitoken";

	public static List<Map<String, Object>> getUploadList(Map<String, Object> request) throws Exception {
		List<Map<String, Object>> uploads = new ArrayList<>();

		String fileName = String.valueOf(request.get("file_name"));
		String uploadMonth = String.valueOf(request.get("upload_month"));
		String action = String.valueOf(request.get("action"));
		String upload_purpose = String.valueOf(request.get("upload_purpose"));
		String user_id = String.valueOf(request.get("user_id"));
		String company_name = String.valueOf(request.get("company_name"));

		String sql = "SELECT * FROM file_upload WHERE 1 = 1";

		if ("download".equalsIgnoreCase(action))
			sql = "SELECT * FROM file_upload WHERE 1 = 1";

		if (ServiceUtil.isNotEmpty(fileName)) {
			sql += " AND file_name = ?";
		}

		if (ServiceUtil.isNotEmpty(uploadMonth)) {
			sql += " AND upload_month = ?";
		}

		if (ServiceUtil.isNotEmpty(upload_purpose)) {
			sql += " AND upload_purpose = ?";
		}

		if (ServiceUtil.isNotEmpty(user_id)) {
			sql += " AND user_id = ?";
		}

		if (ServiceUtil.isNotEmpty(company_name)) {
			sql += " AND company_name = ?";
		}

		try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			int parameterIndex = 1;
			if (ServiceUtil.isNotEmpty(fileName)) {
				stmt.setString(parameterIndex++, fileName);
			}

			if (ServiceUtil.isNotEmpty(uploadMonth)) {
				stmt.setString(parameterIndex++, uploadMonth);
			}

			if (ServiceUtil.isNotEmpty(upload_purpose)) {
				stmt.setString(parameterIndex++, upload_purpose);
			}

			if (ServiceUtil.isNotEmpty(user_id)) {
				stmt.setString(parameterIndex++, user_id);
			}

			if (ServiceUtil.isNotEmpty(company_name)) {
				stmt.setString(parameterIndex++, company_name);
			}

			try (ResultSet rs = stmt.executeQuery()) {
				ResultSetMetaData meta = rs.getMetaData();
				int columnCount = meta.getColumnCount();

				while (rs.next()) {
					Map<String, Object> row = new HashMap<>();
					for (int i = 1; i <= columnCount; i++) {
						String columnName = meta.getColumnName(i);

						if ("file_content".equalsIgnoreCase(columnName)) {
							InputStream stream = rs.getBinaryStream(columnName);
							char[] base64 = IOUtils.toCharArray(stream);
							String decodedData = new String(base64);
							row.put(columnName, decodedData);
						} else {
							Object value = rs.getObject(i);
							row.put(columnName, value);
						}
					}
					uploads.add(row);
				}

			}
		}

		return uploads;
	}

	public static void insertFileUpload(Map<String, Object> request) throws Exception {
		String uploadBy = String.valueOf(request.getOrDefault("upload_by", "admin"));
		String uploadMonth = String.valueOf(request.get("upload_month"));
		String fileName = String.valueOf(request.get("file_name"));
		String fileContent = String.valueOf(request.get("file_content")); // Base64 or plain
		String upload_purpose = String.valueOf(request.get("upload_purpose"));
		String user_id = String.valueOf(request.get("user_id"));
		String company_name = String.valueOf(request.get("company_name"));

		String sql = "INSERT INTO file_upload (upload_by, upload_month, file_name, file_content, upload_date, upload_purpose, user_id, company_name) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

		try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, uploadBy);
			stmt.setString(2, uploadMonth);
			stmt.setString(3, fileName);
			stmt.setBytes(4, fileContent.getBytes());
			stmt.setTimestamp(5, new java.sql.Timestamp(System.currentTimeMillis()));
			stmt.setString(6, upload_purpose);
			stmt.setString(7, user_id);
			stmt.setString(8, company_name);

			int result = stmt.executeUpdate();
			request.put("id", result);
			LOG.info("File uploaded successfully.");
		}
	}

	public static List<Map<String, Object>> salaryList(Map<String, Object> request) throws Exception {

		List<Map<String, Object>> records = new ArrayList<>();
		String uploadMonth = String.valueOf(request.get("upload_month"));
		String upload_purpose = String.valueOf(request.get("upload_purpose"));
		String empId = String.valueOf(request.get("id_no"));
		String uan = String.valueOf(request.get("uan_no"));
		String company_name = String.valueOf(request.get("company_name"));

		StringBuilder sql = new StringBuilder("SELECT * FROM employee_salary WHERE 1=1");

		if (ServiceUtil.isNotEmpty(empId)) {
			sql.append(" AND id_no = ?");
		}

		if (ServiceUtil.isNotEmpty(uploadMonth)) {
			sql.append(" AND upload_month = ?");
		}

		if (ServiceUtil.isNotEmpty(uan)) {
			sql.append(" AND uan_no = ?");
		}

		if (ServiceUtil.isNotEmpty(upload_purpose)) {
			sql.append(" AND upload_purpose = ?");
		}

		if (ServiceUtil.isNotEmpty(company_name)) {
			sql.append(" AND company_name = ?");
		}

		try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
				PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

			int paramIndex = 1;
			if (ServiceUtil.isNotEmpty(empId)) {
				stmt.setString(paramIndex++, empId);
			}

			if (ServiceUtil.isNotEmpty(uploadMonth)) {
				stmt.setString(paramIndex++, uploadMonth);
			}

			if (ServiceUtil.isNotEmpty(uan)) {
				stmt.setString(paramIndex++, uan);
			}

			if (ServiceUtil.isNotEmpty(upload_purpose)) {
				stmt.setString(paramIndex++, upload_purpose);
			}

			if (ServiceUtil.isNotEmpty(company_name)) {
				stmt.setString(paramIndex++, company_name);
			}

			ResultSet rs = stmt.executeQuery();
			ResultSetMetaData meta = rs.getMetaData();
			int columnCount = meta.getColumnCount();

			while (rs.next()) {
				Map<String, Object> row = new LinkedHashMap<>();
				for (int i = 1; i <= columnCount; i++) {
					row.put(meta.getColumnLabel(i), rs.getString(i));
				}
				records.add(row);
			}
		}

		return records;
	}

	public static void insertEmployeeSalaries(List<Map<String, Object>> records) throws Exception {

		String sql = "INSERT INTO employee_salary ("
				+ "serial_no, id_no, uan_no, esi_no, aadhar_card_no, bank_ac_no, gender, "
				+ "name_of_the_employee, desingnation, site_name, total_days, basic, d_a, hra, "
				+ "conveyance, washing_allw, other_allw, gross_salary, epf, esi, professional_tax, "
				+ "labour_welfare_fund, uniform_ded, salary_advance, total_deduct, net_pay, emp_signature, upload_month, company_name"
				+ ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			for (Map<String, Object> row : records) {
				int i = 1;

				stmt.setString(i++, ServiceUtil.safeString(row.get("serial_no")));
				stmt.setString(i++, ServiceUtil.safeString(row.get("id_no")));
				stmt.setString(i++, ServiceUtil.safeString(row.get("uan_no")));
				stmt.setString(i++, ServiceUtil.safeString(row.get("esi_no")));
				stmt.setString(i++, ServiceUtil.safeString(row.get("aadhar_card_no")));
				stmt.setString(i++, ServiceUtil.safeString(row.get("bank_ac_no")));
				stmt.setString(i++, ServiceUtil.safeString(row.get("gender")));
				stmt.setString(i++, ServiceUtil.safeString(row.get("name_of_the_employee")));
				stmt.setString(i++, ServiceUtil.safeString(row.get("desingnation")));
				stmt.setString(i++, ServiceUtil.safeString(row.get("site_name")));
				stmt.setString(i++, ServiceUtil.safeString(row.get("total_days")));
				stmt.setString(i++, ServiceUtil.safeString(row.get("basic")));
				stmt.setString(i++, ServiceUtil.safeString(row.get("d_a")));
				stmt.setString(i++, ServiceUtil.safeString(row.get("hra")));
				stmt.setString(i++, ServiceUtil.safeString(row.get("conveyance")));
				stmt.setString(i++, ServiceUtil.safeString(row.get("washing_allw")));
				stmt.setString(i++, ServiceUtil.safeString(row.get("other_allw")));
				stmt.setString(i++, ServiceUtil.safeString(row.get("gross_salary")));
				stmt.setString(i++, ServiceUtil.safeString(row.get("epf")));
				stmt.setString(i++, ServiceUtil.safeString(row.get("esi")));
				stmt.setString(i++, ServiceUtil.safeString(row.get("professional_tax")));
				stmt.setString(i++, ServiceUtil.safeString(row.get("labour_welfare_fund")));
				stmt.setString(i++, ServiceUtil.safeString(row.get("uniform_ded")));
				stmt.setString(i++, ServiceUtil.safeString(row.get("salary_advance")));
				stmt.setString(i++, ServiceUtil.safeString(row.get("total_deduct")));
				stmt.setString(i++, ServiceUtil.safeString(row.get("net_pay")));
				stmt.setString(i++, ServiceUtil.safeString(row.get("emp_signature")));
				stmt.setString(i++, ServiceUtil.safeString(row.get("upload_month")));
				stmt.setString(i++, ServiceUtil.safeString(row.get("company_name")));
				stmt.addBatch();
			}

			stmt.executeBatch();
		}
	}

	public static final Map<String, String> FIELD_LABELS = Map.ofEntries(Map.entry("serial_no", "S.   No"),
			Map.entry("id_no", "I.D.No."), Map.entry("uan_no", "UAN .No."), Map.entry("esi_no", "ESI. No."),
			Map.entry("aadhar_card_no", "AADHAR CARD NO."), Map.entry("bank_ac_no", "BANK A/C NO."),
			Map.entry("gender", "GENDER"), Map.entry("name_of_the_employee", "NAME OF THE EMPLOYEE"),
			Map.entry("desingnation", "DESINGNATION"), Map.entry("site_name", "SITE NAME"),
			Map.entry("total_days", "TOTAL DAYS"), Map.entry("basic", "BASIC"), Map.entry("d_a", "D.A"),
			Map.entry("hra", "HRA"), Map.entry("conveyance", "CONVEYANCE"), Map.entry("washing_allw", "WASHING ALLW"),
			Map.entry("other_allw", "OTHER ALLW"), Map.entry("gross_salary", "GROSS - SALARY"), Map.entry("epf", "EPF"),
			Map.entry("esi", "ESI"), Map.entry("professional_tax", "PROFESSIONAL TAX"),
			Map.entry("labour_welfare_fund", "LABOUR WELFARE FUND"), Map.entry("uniform_ded", "UNIFORM DED"),
			Map.entry("salary_advance", "SALARY ADVANCE"), Map.entry("total_deduct", "TOTAL DEDUCT"),
			Map.entry("net_pay", "NET PAY"), Map.entry("emp_signature", "Emp. Signature"));

}
