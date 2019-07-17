package com.cpu.log.utils.dto;

import java.util.Date;

public class CpuLogDTO {
	
	private Date cpuUtilizedTime;
	
	private Date cpuAvailableTime;
	
	private Integer cpuCount;
	
	private Double cpuUsage;
	
	private String ipAddress;

	public Integer getCpuCount() {
		return cpuCount;
	}

	public void setCpuCount(Integer cpuCount) {
		this.cpuCount = cpuCount;
	}

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

	public Date getCpuUtilizedTime() {
		return cpuUtilizedTime;
	}

	public void setCpuUtilizedTime(Date cpuUtilizedTime) {
		this.cpuUtilizedTime = cpuUtilizedTime;
	}

	public Date getCpuAvailableTime() {
		return cpuAvailableTime;
	}

	public void setCpuAvailableTime(Date cpuAvailableTime) {
		this.cpuAvailableTime = cpuAvailableTime;
	}



}
