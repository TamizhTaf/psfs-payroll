package com.psfs.service.api.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.psfs.service.common.AppPropertyService;

public class ConverterUtil {
	private static final Log LOG = LogFactory.getLog(ConverterUtil.class);

	static String timeZone = "IST";

	public static String getJSONFromObject(Object anSourceObject) {
		String jsonString = "";

		try {

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			jsonString = mapper.writeValueAsString(anSourceObject);

		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}

		return jsonString;
	}

	public static Object getObjectFromJson(String json, Class<?> classObj) {
		Object jsonObj = null;

		try {

			jsonObj = classObj.newInstance();
			ObjectMapper mapper = new ObjectMapper();
			jsonObj = mapper.readValue(json, classObj);

		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}

		return jsonObj;
	}

	@SuppressWarnings("unchecked")
	public static List<HashMap<String, Object>> convertObjectToList(Object data) {

		List<HashMap<String, Object>> anList = new ArrayList<>();
		try {

			if (data instanceof List) {
				anList = (List<HashMap<String, Object>>) data;

			} else {

				ObjectMapper mapper = new ObjectMapper();
				TypeReference<List<HashMap<String, Object>>> typeReference = null;
				typeReference = new TypeReference<List<HashMap<String, Object>>>() {
				};
				anList = mapper.readValue(String.valueOf(data), typeReference);
			}

		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}

		return anList;
	}

	public static String getLocalDateTime() {
		Date currentDate = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat(AppPropertyService.getProperty("datetime.format"));
		dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
		return dateFormat.format(currentDate);
	}

	public static Date getLocalDate() {
		Date currentDate = new Date();

		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(AppPropertyService.getProperty("date.format"));
			dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
			currentDate = dateFormat.parse(dateFormat.format(currentDate));
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}

		return currentDate;
	}

	public static Object convertDateToString(Object date) {
		try {

			if (date != null && !"null".equals(date) && !StringUtils.isEmpty(date)) {
				SimpleDateFormat dateFormat = new SimpleDateFormat(AppPropertyService.getProperty("date.format"));
				dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
				date = dateFormat.format(date);
			}

		} catch (Exception e) {
			LOG.error("convertDateToString =>" + e.getMessage());
		}

		if (!isNotEmpty(String.valueOf(date)))
			date = null;

		return date;
	}

	public static Object convertStringToDate(Object date) {
		try {

			if (isNotEmpty(String.valueOf(date))) {
				SimpleDateFormat dateFormat = new SimpleDateFormat(AppPropertyService.getProperty("date.format"));
				dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
				date = dateFormat.parse(String.valueOf(date));
			}

		} catch (Exception e) {
			LOG.error("convertStringToDateTime =>" + e.getMessage());
		}

		if (!isNotEmpty(String.valueOf(date)))
			date = null;

		return date;
	}

	public static Date convertStringToDateByFormat(String value, String format) {
		Date date = null;
		try {

			if (isNotEmpty(String.valueOf(value))) {
				SimpleDateFormat dateFormat = new SimpleDateFormat(format);
				dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
				date = dateFormat.parse(value);
			}

		} catch (Exception e) {
			LOG.error("convertStringToDateByFormat =>" + e.getMessage());
		}

		if (!isNotEmpty(String.valueOf(date)))
			date = null;

		return date;
	}

	public static String convertDateToStringByFormat(Date date, String format) {
		String value = null;
		try {

			if (isNotEmpty(String.valueOf(date))) {
				SimpleDateFormat dateFormat = new SimpleDateFormat(format);
				dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
				value = dateFormat.format(date);
			}

		} catch (Exception e) {
			LOG.error("convertDateToStringByFormat =>" + e.getMessage());
		}

		if (!isNotEmpty(String.valueOf(value)))
			value = null;

		return value;
	}

	public static Object convertDateTimeToString(Object dateTime) {

		try {

			if (dateTime != null && !"null".equals(dateTime) && !StringUtils.isEmpty(dateTime)) {
				SimpleDateFormat dateFormat = new SimpleDateFormat(AppPropertyService.getProperty("datetime.format"));
				dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
				dateTime = dateFormat.format(dateTime);
			}
		} catch (Exception e) {
			LOG.error("convertDateTimeToString =>" + e.getMessage());
		}

		if (!isNotEmpty(String.valueOf(dateTime)))
			dateTime = null;

		return dateTime;
	}

	public static String convertUTCStringtoISTTime(String dateTime) {

		// create a DateTimeFormatter for the UTC format
		DateTimeFormatter utcFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'00Z'");

		// create a LocalDateTime object from the UTC string using the formatter
		LocalDateTime utcTime = LocalDateTime.parse(dateTime, utcFormatter);

		// create a ZonedDateTime object for the UTC time
		ZonedDateTime utcDateTime = ZonedDateTime.of(utcTime, ZoneId.of("UTC"));

		// convert the UTC time to IST
		ZonedDateTime istDateTime = utcDateTime.withZoneSameInstant(ZoneId.of("Asia/Kolkata"));

		// create a DateTimeFormatter for the IST format
		DateTimeFormatter istFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

		// format the IST time as a string
		String istTime = istDateTime.format(istFormatter);

		return istTime;
	}

	public static Object convertUTCDateToIST(String dateTime) {
		SimpleDateFormat utcFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		utcFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));

		// parse the UTC string and convert it to a Date object
		Date utcDate = null;
		try {
			utcDate = utcFormatter.parse(dateTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return utcDate;
	}

	public static Object convertStringToDateTime(Object dateTime) {
		try {

			if (isNotEmpty(String.valueOf(dateTime))) {
				SimpleDateFormat dateFormat = new SimpleDateFormat(AppPropertyService.getProperty("datetime.format"));
				dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
				dateTime = dateFormat.parse(String.valueOf(dateTime));
			}

		} catch (Exception e) {
			LOG.error("convertStringToDateTime =>" + e.getMessage());
		}

		if (!isNotEmpty(String.valueOf(dateTime)))
			dateTime = null;

		return dateTime;
	}

	public static Boolean isNotEmpty(String value) {

		boolean isValid = false;

		if (value != null && !"null".equalsIgnoreCase(value) && !StringUtils.isEmpty(value)) {
			isValid = true;
		}

		return isValid;
	}

}
