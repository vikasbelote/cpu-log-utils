package com.cpu.log.utils.dto;

public class CpuLogDTO {
	
	private String cpuUtilizedTime;
	
	private String cpuAvailableTime;
	
	private Double cpuCount;
	
	private Double cpuUsage;
	
	private String ipAddress;	
	
	private String currentDateTime;

	public Double getCpuUsage() {
		return cpuUsage;
	}

	public void setCpuUsage(Double cpuUsage) {
		this.cpuUsage = cpuUsage;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public Double getCpuCount() {
		return cpuCount;
	}

	public void setCpuCount(Double cpuCount) {
		this.cpuCount = cpuCount;
	}

	public String getCpuUtilizedTime() {
		return cpuUtilizedTime;
	}

	public void setCpuUtilizedTime(String cpuUtilizedTime) {
		this.cpuUtilizedTime = cpuUtilizedTime;
	}

	public String getCpuAvailableTime() {
		return cpuAvailableTime;
	}

	public void setCpuAvailableTime(String cpuAvailableTime) {
		this.cpuAvailableTime = cpuAvailableTime;
	}

	public String getCurrentDateTime() {
		return currentDateTime;
	}

	public void setCurrentDateTime(String currentDateTime) {
		this.currentDateTime = currentDateTime;
	}



}
