package com.dinfo.fetcher.bean;

public class ProxyBean {
    private String proxyIP;
    private int porxyPort;
    private String use_time;
    private String check_time;
    
    public String getUse_time() {
		return use_time;
	}
	public void setUse_time(String useTime) {
		use_time = useTime;
	}
	public String getCheck_time() {
		return check_time;
	}
	public void setCheck_time(String checkTime) {
		check_time = checkTime;
	}
	public String getProxyIP() {
        return proxyIP;
    }
    public void setProxyIP(String proxyIP) {
        this.proxyIP = proxyIP;
    }
    public int getPorxyPort() {
        return porxyPort;
    }
    public void setPorxyPort(int porxyPort) {
        this.porxyPort = porxyPort;
    }
    public ProxyBean(String proxyIP, int porxyPort) {
        super();
        this.proxyIP = proxyIP;
        this.porxyPort = porxyPort;
    }
    public ProxyBean() {
        super();
    }
    
}
