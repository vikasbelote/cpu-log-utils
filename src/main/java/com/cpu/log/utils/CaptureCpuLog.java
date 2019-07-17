package com.cpu.log.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import com.cpu.log.utils.dto.CpuLogDTO;

public class CaptureCpuLog {
	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	
	private boolean fileWriteMode;

	public CaptureCpuLog(boolean fileWriteMode) {
		this.fileWriteMode = fileWriteMode;
	}

	public CpuLogDTO createLog() {
		
		CpuLogDTO cpuLogDTO = new CpuLogDTO();

		Long start = System.nanoTime();
		Integer cpuCount = ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();
		cpuLogDTO.setCpuCount(cpuCount);
		
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
		
		this.calcCPU(startCPUTime, start, cpuLogDTO);
		
		log("=========================END=============================================");
		return cpuLogDTO;
	}

	private void log(String message) {
		System.out.println(message);

		if (this.fileWriteMode) {

			try {
				FileWriter writer = new FileWriter("app.log", true);
				BufferedWriter bw = new BufferedWriter(writer);
				
				bw.append(message);
				bw.newLine();
				bw.close();
				

			} catch (IOException e) {
				System.err.format("IOException: %s%n", e);
			}
		}

	}

	private void calcCPU(long cpuStartTime, long elapsedStartTime, CpuLogDTO cpuLogDTO) {
		
		Long end = System.nanoTime();
		Long totalAvailCPUTime = cpuLogDTO.getCpuCount() * (end - elapsedStartTime);
		Long totalUsedCPUTime = ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime() - cpuStartTime;
		
		Date cpuUtilizedTime = new Date(totalUsedCPUTime);
		cpuLogDTO.setCpuUtilizedTime(cpuUtilizedTime);
		log("Total CPU Time:" + dateFormat.format(cpuUtilizedTime));
		
		Date cpuAvailableTime = new Date(totalAvailCPUTime);
		cpuLogDTO.setCpuAvailableTime(cpuAvailableTime);
		log("Total Avail CPU Time:" + dateFormat.format(cpuAvailableTime));
		
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

}
