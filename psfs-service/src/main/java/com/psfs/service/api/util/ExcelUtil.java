package com.psfs.service.api.util;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.psfs.service.repository.ServiceRepository;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.logging.*;

public class ExcelUtil {
	private static final Logger logger = Logger.getLogger(ExcelUtil.class.getName());

	public static List<Map<String, Object>> readExcel(byte[] inputFile) throws Exception {
		List<Map<String, Object>> records = new ArrayList<>();

		if (inputFile == null || inputFile.length == 0) {
			return records;
		}

		try (InputStream inp = new ByteArrayInputStream(inputFile)) {
			Workbook wb;
			InputStream stream = (!inp.markSupported()) ? new PushbackInputStream(inp, 8) : inp;

			if (POIFSFileSystem.hasPOIFSHeader(stream)) {
				wb = new HSSFWorkbook(stream);
			} else {
				wb = new XSSFWorkbook(OPCPackage.open(stream));
			}

			Sheet sheet = wb.getSheetAt(0);
			DataFormatter formatter = new DataFormatter();
			FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();

			// Read header row
			Row headerRow = sheet.getRow(0);
			if (headerRow == null)
				return records;

			int lastCellNum = headerRow.getLastCellNum();
			List<String> headers = new ArrayList<>();
			for (int i = 0; i < lastCellNum; i++) {
				String header = formatter.formatCellValue(headerRow.getCell(i)).trim();
				headers.add(ServiceUtil.removeEOL(header));
			}

			// Read data rows
			for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
				Row row = sheet.getRow(rowIndex);
				if (row == null)
					continue;

				Map<String, Object> rowData = new LinkedHashMap<>();
				boolean isNonEmptyRow = false;

				for (int i = 0; i < lastCellNum; i++) {
					Cell cell = row.getCell(i, MissingCellPolicy.CREATE_NULL_AS_BLANK);
					String value = formatter.formatCellValue(cell, evaluator).trim();
					value = ServiceUtil.removeEOL(value);

					if (ServiceUtil.isNotEmpty(value)) {
						isNonEmptyRow = true;
					}

					String key = (i < headers.size()) ? headers.get(i) : "Column" + (i + 1);

					Optional<Entry<String, String>> foundKey = ServiceRepository.FIELD_LABELS.entrySet().stream()
							.filter(item -> item.getValue().equalsIgnoreCase(key)).findFirst();

					if (!rowData.containsKey("emp_signature") && foundKey.isPresent()) {
						rowData.put(foundKey.get().getKey(), value);
						continue;
					}
				}

				if (isNonEmptyRow) {
					records.add(rowData);
				}
			}

			records = records.stream().filter(item -> ServiceUtil.isNotEmpty(String.valueOf(item.get("id_no"))))
					.toList();

		} catch (Exception e) {
			logger.warning("Error while converting Excel to List<Map>: " + e.getMessage());
			throw e;
		}

		return records;
	}

}
