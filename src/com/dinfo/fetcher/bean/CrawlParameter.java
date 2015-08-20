package com.dinfo.fetcher.bean;

import java.io.Serializable;
import java.util.Map;

import com.dinfo.fetcher.util.ParseProperty;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;

public class CrawlParameter  implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private CrawlType type = CrawlType.jsoup;
	private String encode = "utf-8"; // 编码
	private boolean isUseJs = true; // js
	private String template ; // 模板
	//private BrowserVersion bv = BrowserVersion.FIREFOX_3_6;
	private long readtime = 60*1000;
	private long conntime = 60*1000;
	private String proFilePath = ParseProperty.getVaule("profilepath");
	private String fireFoxPath = ParseProperty.getVaule("firefoxpath");
	private String reqmethod; //访问get 或 post 方式 
	private Map<String,String> reqmap; // 请求参数
	private String referrer; //设置 来源网站。
	private Map<String,String> cookie; 
	private Map<String,String> header;
	private boolean isUseWebClient;
	private WebClient webClient;
	private boolean isignorecontenttype;
	private boolean isUseProxy; //是否要使用代理
	private ProxyBean proxyBean;
	
	public boolean isIsignorecontenttype() {
		return isignorecontenttype;
	}
	public void setIsignorecontenttype(boolean isignorecontenttype) {
		this.isignorecontenttype = isignorecontenttype;
	}
	public boolean isUseWebClient() {
		return isUseWebClient;
	}
	public void setUseWebClient(boolean isUseWebClient) {
		this.isUseWebClient = isUseWebClient;
	}
	public WebClient getWebClient() {
		return webClient;
	}
	public void setWebClient(WebClient webClient) {
		this.webClient = webClient;
		 if(webClient != null){
	            this.isUseWebClient = true;
	        }
	}
	
	public Map<String, String> getCookie() {
		return cookie;
	}
	public void setCookie(Map<String, String> cookie) {
		this.cookie = cookie;
	}
	public Map<String, String> getHeader() {
		return header;
	}
	public void setHeader(Map<String, String> header) {
		this.header = header;
	}
	public String getReqmethod() {
		return reqmethod;
	}
	public void setReqmethod(String reqmethod) {
		this.reqmethod = reqmethod;
	}
	public Map<String, String> getReqmap() {
		return reqmap;
	}
	public void setReqmap(Map<String, String> reqmap) {
		this.reqmap = reqmap;
	}
	public String getProFilePath() {
		return proFilePath;
	}
	public void setProFilePath(String proFilePath) {
		this.proFilePath = proFilePath;
	}
	public String getFireFoxPath() {
		return fireFoxPath;
	}
	public void setFireFoxPath(String fireFoxPath) {
		this.fireFoxPath = fireFoxPath;
	}
	/*public BrowserVersion getBv() {
		return bv;
	}
	public void setBv(BrowserVersion bv) {
		this.bv = bv;
	}*/
	public long getConntime() {
		return conntime;
	}
	public void setConntime(long conntime) {
		this.conntime = conntime;
	}
	public long getReadtime() {
		return readtime;
	}
	public void setReadtime(long readtime) {
		this.readtime = readtime;
	}
	
	public CrawlType getType() {
		return type;
	}
	public void setType(CrawlType type) {
		this.type = type;
	}
	public String getEncode() {
		return encode;
	}
	public void setEncode(String encode) {
		this.encode = encode;
	}
	public boolean isUseJs() {
		return isUseJs;
	}
	public void setUseJs(boolean isUseJs) {
		this.isUseJs = isUseJs;
	}
	public String getTemplate() {
		return template;
	}
	public void setTemplate(String template) {
		this.template = template;
	}
    public void setUseProxy(boolean isUseProxy) {
        this.isUseProxy = isUseProxy;
    }
    public boolean isUseProxy() {
        return isUseProxy;
    }
    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }
    public String getReferrer() {
        return referrer;
    }
    public void setProxyBean(ProxyBean proxyBean) {
        this.proxyBean = proxyBean;
        if(proxyBean != null && proxyBean.getProxyIP()!=null){
            this.isUseProxy = true;
        }
    }
    public ProxyBean getProxyBean() {
        return proxyBean;
    }
	

}
