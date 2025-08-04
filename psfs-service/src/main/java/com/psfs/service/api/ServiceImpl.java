package com.psfs.service.api;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;

import com.psfs.service.api.util.ExcelUtil;
import com.psfs.service.api.util.ServiceUtil;
import com.psfs.service.model.SecurityUser;
import com.psfs.service.repository.ServiceRepository;

public class ServiceImpl {

	public static Map<String, Object> upload(Map<String, Object> request) throws Exception {
		ServiceRepository.insertFileUpload(request);
		String id = String.valueOf(request.get("id"));
		String uploadPurpose = String.valueOf(request.get("upload_purpose"));

		if (!ServiceUtil.isNotEmpty(id)) {
			request.put("status", "failed");
			request.put("message", "Failed to create record");
			return request;
		}

		if ("SALARY".equalsIgnoreCase(uploadPurpose)) {
			String base64 = String.valueOf(request.get("file_content"));
			String upload_month = String.valueOf(request.get("upload_month"));
			String company_name = String.valueOf(request.get("company_name"));

			// Remove base64 prefix if present
			if (base64.contains(",")) {
				base64 = base64.split(",")[1];
			}

			// Decode it
			byte[] decoded = Base64.getDecoder().decode(base64);

			List<Map<String, Object>> records = ExcelUtil.readExcel(decoded);
			for (Map<String, Object> uploadData : records) {
				uploadData.put("upload_month", upload_month);
				uploadData.put("company_name", company_name);
			}

			ServiceRepository.insertEmployeeSalaries(records);
		}

		request.remove("file_content");
		return request;
	}

	public static List<Map<String, Object>> uploadList(Map<String, Object> request) throws Exception {
		return ServiceRepository.getUploadList(request);
	}

	public static Map<String, Object> downloadUpload(Map<String, Object> request) throws Exception {
		Map<String, Object> response = new HashMap<>();
		String id = String.valueOf(request.get("id"));
		response.put("status", "failed");

		if (!ServiceUtil.isNotEmpty(id)) {
			response.put("message", "ID is mandatory!");
		}

		request.put("action", "download");
		List<Map<String, Object>> records = ServiceRepository.getUploadList(request);

		if (records.isEmpty()) {
			response.put("message", "Record not found!");
		}

		for (Map<String, Object> uploadData : records) {
			String fileName = String.valueOf(uploadData.get("file_name"));
			String base64Excel = String.valueOf(uploadData.get("file_content"));
			response.put("file_name", fileName);
			response.put("file_content", base64Excel);
			response.put("status", "success");

		}

		return response;
	}

	public static List<Map<String, Object>> salaryList(Map<String, Object> request) throws Exception {

		SecurityUser securityUser = ServiceUtil.getLoginSession();
		Optional<GrantedAuthority> foundAdminRole = securityUser.getAuthorities().stream()
				.filter(item -> item.getAuthority().equalsIgnoreCase("admin")).findFirst();

		if (!foundAdminRole.isPresent())
			request.put("id_no", securityUser.getUsername());

		return ServiceRepository.salaryList(request);
	}

	public static Map<String, Object> downloadSalary(Map<String, Object> request) throws Exception {
		Map<String, Object> response = new HashMap<>();
		String id_no = String.valueOf(request.get("id_no"));
		response.put("status", "failed");

		if (!ServiceUtil.isNotEmpty(id_no)) {
			response.put("message", "ID is mandatory!");
		}

		SecurityUser securityUser = ServiceUtil.getLoginSession();
		Optional<GrantedAuthority> foundAdminRole = securityUser.getAuthorities().stream()
				.filter(item -> item.getAuthority().equalsIgnoreCase("admin")).findFirst();

		if (!foundAdminRole.isPresent()) {
			request.put("id_no", securityUser.getUsername());
			if (!securityUser.getUsername().equalsIgnoreCase(id_no)) {
				response.put("message", "You are not authorised to download!");
			}
		}

		List<Map<String, Object>> records = ServiceRepository.salaryList(request);

		if (records.isEmpty()) {
			response.put("message", "Record not found!");
		}

		for (Map<String, Object> payslipData : records) {
			String uploadMonth = String.valueOf(payslipData.get("upload_month"));
			String fileName = id_no + "_" + uploadMonth + ".pdf";
			String base64Pdf = PayslipGenerator.generatePayslip(payslipData);
			response.put("file_name", fileName);
			response.put("file_content", base64Pdf);
			response.put("status", "success");

		}

		return response;
	}
}
