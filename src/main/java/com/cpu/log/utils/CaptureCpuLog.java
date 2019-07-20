package com.cpu.log.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.cpu.log.utils.dto.CpuLogDTO;

public class CaptureCpuLog {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	
	private static final SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	private static final String tempFolder = System.getProperty("java.io.tmpdir");

	public CpuLogDTO createLog() {

		CpuLogDTO cpuLogDTO = new CpuLogDTO();
		cpuLogDTO.setCurrentDateTime(dateFormat1.format(new Date()));
		cpuLogDTO.setIpAddress(getIpAddress());

		Long start = System.nanoTime();
		
		long cpuCount = ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();
		cpuLogDTO.setCpuCount((double)cpuCount);

		Random random = new Random(start);
		int seed = Math.abs(random.nextInt());
		log("Starting Test with " + cpuCount + " CPUs and random number:" + seed);

		int primes = 10000;
		long startCPUTime = ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime();
		
		start = System.nanoTime();
		while (primes != 0) {
			if (isPrime(seed)) {
				primes--;
			}
			seed++;

		}

		this.calcCPU(startCPUTime, start, cpuCount, cpuLogDTO);

		this.writeToExcel(cpuLogDTO);
		log("=========================END=============================================");
		return cpuLogDTO;
	}

	public List<CpuLogDTO> getCpuLogDTOList() {
		
		List<CpuLogDTO> cpuLogDTOList = new ArrayList<CpuLogDTO>();
		
		try {
			FileInputStream inputStream = new FileInputStream(new File(tempFolder + "/app-log-data.xlsx"));
			XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
			XSSFSheet sheet = workbook.getSheet("app log");

			boolean isFirstRow = true;
			Iterator<Row> rowIterator = sheet.iterator();
			while (rowIterator.hasNext()) {
				
				Row row = rowIterator.next();
				if (isFirstRow) {
					isFirstRow = false;
					continue;
				}
				
				CpuLogDTO cpuLogDTO = new CpuLogDTO();
				cpuLogDTO.setCpuCount(row.getCell(0).getNumericCellValue());
				cpuLogDTO.setCpuUsage(row.getCell(1).getNumericCellValue());
				cpuLogDTO.setIpAddress(row.getCell(2).getStringCellValue());
				cpuLogDTO.setCpuAvailableTime(row.getCell(3).getStringCellValue());
				cpuLogDTO.setCpuUtilizedTime(row.getCell(4).getStringCellValue());
				cpuLogDTO.setCurrentDateTime(row.getCell(5).getStringCellValue());
				cpuLogDTOList.add(cpuLogDTO);
			}
			inputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return cpuLogDTOList;
	}

	private void log(String message) {
		System.out.println(message);
		try {
			FileWriter writer = new FileWriter(tempFolder + "/app.log", true);
			BufferedWriter bw = new BufferedWriter(writer);

			bw.append(message);
			bw.newLine();
			bw.close();
		} catch (IOException e) {
			System.err.format("IOException: %s%n", e);
		}
	}

	private void calcCPU(long cpuStartTime, long elapsedStartTime,long cpuCount, CpuLogDTO cpuLogDTO) {

		Long end = System.nanoTime();
		Long totalAvailCPUTime = cpuCount * (end - elapsedStartTime);
		Long totalUsedCPUTime = ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime() - cpuStartTime;

		Date cpuUtilizedTime = new Date(totalUsedCPUTime);
		cpuLogDTO.setCpuUtilizedTime(dateFormat.format(cpuUtilizedTime));
		log("Total CPU Time:" + cpuLogDTO.getCpuUtilizedTime());

		Date cpuAvailableTime = new Date(totalAvailCPUTime);
		cpuLogDTO.setCpuAvailableTime( dateFormat.format(cpuAvailableTime));
		log("Total Avail CPU Time:" + cpuLogDTO.getCpuAvailableTime());

		double per = ((double) totalUsedCPUTime * 100) / (double) totalAvailCPUTime;
		cpuLogDTO.setCpuUsage(per);
		log("CPU Util:" + per);
	}

	private boolean isPrime(int n) {
		// 2 is the smallest prime
		if (n <= 2) {
			return n == 2;
		}
		// even numbers other than 2 are not prime
		if (n % 2 == 0) {
			return false;
		}
		// check odd divisors from 3
		// to the square root of n
		for (int i = 3, end = (int) Math.sqrt(n); i <= end; i += 2) {
			if (n % i == 0) {
				return false;
			}
		}
		return true;
	}

	private void writeToExcel(CpuLogDTO cpuLogDTO) {

		if (!isFileExist()) {

			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("app log");
			// write header to excel
			String[] colsNameArr = new String[] { "CPU Count", "CPU Usages", "IP Address", "CPU Utilized Time",
					"CPU Available Time", "Current Time" };
			Row row = sheet.createRow(0);
			int cellnum = 0;
			for (String str : colsNameArr) {
				Cell cell = row.createCell(cellnum++);
				cell.setCellValue(str);
			}
			row = sheet.createRow(1);
			row.createCell(0).setCellValue(cpuLogDTO.getCpuCount());
			row.createCell(1).setCellValue(cpuLogDTO.getCpuUsage());
			row.createCell(2).setCellValue(cpuLogDTO.getIpAddress());
			row.createCell(3).setCellValue(cpuLogDTO.getCpuUtilizedTime());
			row.createCell(4).setCellValue(cpuLogDTO.getCpuAvailableTime());
			row.createCell(5).setCellValue(cpuLogDTO.getCurrentDateTime());

			this.writeNewWorkBookToFile(workbook);
		} else {
			this.appendNewRowToExcel(cpuLogDTO);
		}
	}

	private void appendNewRowToExcel(CpuLogDTO cpuLogDTO) {

		try {
			FileInputStream inputStream = new FileInputStream(new File(tempFolder + "/app-log-data.xlsx"));
			XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
			XSSFSheet sheet = workbook.getSheet("app log");
			int lastRow = sheet.getLastRowNum();
			Row row = sheet.createRow(++lastRow);
			row.createCell(0).setCellValue(cpuLogDTO.getCpuCount());
			row.createCell(1).setCellValue(cpuLogDTO.getCpuUsage());
			row.createCell(2).setCellValue(cpuLogDTO.getIpAddress());
			row.createCell(3).setCellValue(cpuLogDTO.getCpuUtilizedTime());
			row.createCell(4).setCellValue(cpuLogDTO.getCpuAvailableTime());
			row.createCell(5).setCellValue(cpuLogDTO.getCurrentDateTime());
			inputStream.close();

			this.writeNewWorkBookToFile(workbook);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void writeNewWorkBookToFile(XSSFWorkbook workbook) {

		try {
			// this Writes the workbook
			FileOutputStream out = new FileOutputStream(new File(tempFolder + "/app-log-data.xlsx"));
			workbook.write(out);
			out.close();
			System.out.println("app-log-data.xlsx written successfully on disk.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean isFileExist() {
		File appLogDataFile = new File(tempFolder + "/app-log-data.xlsx");
		return appLogDataFile.exists();
	}
	
	private String getIpAddress() {
		
		try {
			return InetAddress.getLocalHost().getHostAddress().trim();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return null;
		}
	}
}
