package com.psfs.service.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController 
public class ServiceApi {
	private static final Log LOG = LogFactory.getLog(ServiceApi.class);

	@PostMapping(value = "/api/upload", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> upload(@RequestBody(required = false) Map<String, Object> request) {
		Map<String, Object> response = new HashMap<>();

		try {

			response = ServiceImpl.upload(request);

		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}

		return new ResponseEntity<>(response, HttpStatus.OK);

	}

	@PostMapping(value = "/api/uploadList", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public ResponseEntity<List<Map<String, Object>>> uploadList(
			@RequestBody(required = false) Map<String, Object> request) {
		List<Map<String, Object>> response = new ArrayList<>();

		try {

			response = ServiceImpl.uploadList(request);

		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}

		return new ResponseEntity<>(response, HttpStatus.OK);

	}

	@PostMapping(value = "/api/downloadUpload", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> downloadUpload(
			@RequestBody(required = false) Map<String, Object> request) {
		Map<String, Object> response = new HashMap<>();

		try {

			response = ServiceImpl.downloadUpload(request);

		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}

		return new ResponseEntity<>(response, HttpStatus.OK);

	}

	@PostMapping(value = "/api/salaryList", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public ResponseEntity<List<Map<String, Object>>> salaryList(
			@RequestBody(required = false) Map<String, Object> request) {
		List<Map<String, Object>> response = new ArrayList<>();

		try {

			response = ServiceImpl.salaryList(request);

		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}

		return new ResponseEntity<>(response, HttpStatus.OK);

	}

	@PostMapping(value = "/api/downloadSalary", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> downloadSalary(
			@RequestBody(required = false) Map<String, Object> request) {
		Map<String, Object> response = new HashMap<>();

		try {

			response = ServiceImpl.downloadSalary(request);

		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}

		return new ResponseEntity<>(response, HttpStatus.OK);

	}

}
