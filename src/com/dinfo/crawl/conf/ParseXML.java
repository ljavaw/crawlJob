package com.dinfo.crawl.conf;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.tree.DefaultAttribute;

import com.dinfo.crawl.URLBean;
import com.dinfo.fetcher.bean.CrawlParameter;
import com.dinfo.fetcher.bean.CrawlType;
import com.dinfo.parse.bean.IntelligentType;
import com.dinfo.parse.bean.ParseParameter;
import com.dinfo.parse.bean.ParseType;

public class ParseXML {

	/*public static Map<String,JobConfig> getConfigMap() {
		Map<String,JobConfig> configMap = parseXML("crawl.xml");
		return configMap;
	}
	
	
	public static Map<String,JobConfig> getConfigMap(String fileName) {
		Map<String,JobConfig> configMap = parseXML(fileName);
		return configMap;
	}
	
	
	public static void main(String[] args) {
		parseXML("crawl.xml");
	}*/

	@SuppressWarnings("unchecked")
	public static Map<String,JobConfig> parseXML(Document doc) {
		Map<String,JobConfig>  configMap= null;
		try {
			/*SAXReader reader = new SAXReader();
			File file = new File(fileName);
			InputStream in = ParseXML.class.getClassLoader()
					.getResourceAsStream(fileName);
			FileInputStream fin = null;
			try {
				fin = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			Document doc = reader.read(fin);*/
			Element root = doc.getRootElement();
			System.out.println(root.getName());
			List<Element> jobs = root.elements();
			if(CollectionUtils.isNotEmpty(jobs)){
				configMap = new HashMap<String,JobConfig>();  
				for(Element job:jobs){
					JobConfig jobCon = new JobConfig();
					String name = job.elementTextTrim("name");
					String cron = job.elementTextTrim("cron");
					System.out.println(name);
					List<Element> pages = job.elements("page");
					if(CollectionUtils.isNotEmpty(pages)){
						List<PageConfig> pageList = new ArrayList<PageConfig>();
						for(Element page:pages){
							PageConfig pagecon = new PageConfig();
							Element urls = page.element("urls");
							int urlType = Integer.parseInt(urls.elementText("type"));  // url 类型 0：直接取得  1：从页面获得  2：拼接取得
							pagecon.setUrlType(urlType);
							String saveT = urls.elementText("savetype");    // 保存类型   0：保存   1：更改
							int saveType = 0;
							if(StringUtils.isNotBlank(saveT)){
								saveType = Integer.parseInt(saveT);
							}
							pagecon.setSaveType(saveType);
							
							String tableName = urls.elementText("table");
							if(StringUtils.isNotBlank(tableName)){
								pagecon.setTableName(tableName);
							}
							
							String columns = urls.elementText("columns");
							if(StringUtils.isNotBlank(columns)){
								List<String> columnList = getList(columns,";");
								pagecon.setColumns(columnList);
							}
							
							String trowkey = urls.elementText("trowkey");   // hbase 表的rowkey
							if(StringUtils.isNotBlank(trowkey)){
								pagecon.setTrowkey(trowkey);
							}
							String tfamily = urls.elementText("tfamily");   //hbase 表的列族
							if(StringUtils.isNotBlank(tfamily)){
								pagecon.setTfamily(tfamily);
							}
							String indexTableName = urls.elementText("indextablename");   //索引表的Name
							if(StringUtils.isNotBlank(indexTableName)){
								pagecon.setIndexTableName(indexTableName);
							}
							String irowkey = urls.elementText("irowkey");   //索引表的rowkey
							if(StringUtils.isNotBlank(irowkey)){
								pagecon.setIrowkey(irowkey);
							}
							String ifamily = urls.elementText("ifamily");     //索引表的列族
							if(StringUtils.isNotBlank(ifamily)){
								pagecon.setIfamily(ifamily);
							}
							
							String icolumns = urls.elementText("icolumns");   //索引表的列
							if(StringUtils.isNotBlank(icolumns)){
								List<String> columnList = getList(icolumns,";");
								pagecon.setIcolumns(columnList);
							}
							
							String repeattimes = urls.elementText("repeattimes");
							if(StringUtils.isNotBlank(repeattimes)){
								pagecon.setRepeattimes(repeattimes);
							}
							
							String sleepTime = urls.elementText("sleeptime");
							if(StringUtils.isNotBlank(sleepTime)){
								long stime = Long.parseLong(sleepTime);
								pagecon.setSleepTime(stime);
							}
							
							
							String testUrl = urls.elementText("testurl");   // 如果有代理则测试代理可用性的url
							pagecon.setTesturl(testUrl);
							
							List<Element> urlelist = urls.elements("url");
							if(CollectionUtils.isNotEmpty(urlelist)){
								List<URLBean> urllist = new ArrayList<URLBean>();
								for(Element urlele :urlelist){
									String urlf = urlele.getTextTrim();
									System.out.println(urlf);
									List<DefaultAttribute> atrrs = urlele.attributes();
									if(CollectionUtils.isNotEmpty(atrrs)){
										List<List<String>> listlist = new ArrayList<List<String>>();
										String pagen = urlele.attributeValue("pagenum");  // pageNum
										if(StringUtils.isNotBlank(pagen)){
											List<String> pageNumList = new ArrayList<String>();
											int pagenum = Integer.parseInt(pagen);
											for(int i=1; i<=pagenum; i++){
												pageNumList.add(i+"");
											}
											if(CollectionUtils.isNotEmpty(pageNumList)){
												listlist.add(pageNumList);
											}
										}
										
										String keyworda = urlele.attributeValue("keyword");  // 关键字
										
										if(StringUtils.isNotBlank(keyworda)){
											List<String> wordsList = getList(keyworda,";");
											List<String> wordList = new ArrayList<String>();
											for(String word:wordsList){
												String[] words = word.split("::");
												if(words.length==2){
													try {
														word = URLEncoder.encode(words[0], words[1]);
													} catch (UnsupportedEncodingException e) {
														e.printStackTrace();
													}
													wordList.add(word);
												}
											}
											if(CollectionUtils.isNotEmpty(wordList)){
												listlist.add(wordList);
											}
										}
										
										Map<String,String> dataMap = new HashMap<String,String>();   // 传入的携带参数
										String carrydata = urlele.attributeValue("carrydata");  // 关键字
										if(StringUtils.isNotBlank(carrydata)){
											String[] datas = carrydata.split(";;");
											if(datas!=null){
												for(String data:datas){
													String[] datam = data.split("::");
													if(null != datam && datam.length==2){
														String datakey = datam[0];
														String dataval = datam[1];
														dataMap.put(datakey, dataval);
													}
												}
											}
										}
										
										
										
										if(CollectionUtils.isNotEmpty(listlist)){
											List<String> urlist = new ArrayList<String>();
											urlist.add(urlf);
											urlist = getUrlList(listlist,urlist);
											if(CollectionUtils.isNotEmpty(urlist)){
												// 
												List<String> paraList = new ArrayList<String>();
												for(DefaultAttribute ele:atrrs){
													String atrr = ele.getName(); 
													if(atrr.contains("keyword") || atrr.contains("pagenum") || atrr.contains("count")){
														continue;
													}
													String value = urlele.attributeValue(atrr);  
													paraList.add(value);   // 除页数和关键字的属性之外其他的参数
												}
												
												for(String burl:urlist){
													URLBean urlbean = new URLBean();
													urlbean.setList(paraList);
													urlbean.setUrl(burl);
													urlbean.setBaseURL(burl);
													if(MapUtils.isNotEmpty(dataMap)){
														urlbean.setParaMap(dataMap);
													}
													urllist.add(urlbean);
												}
											}
										}else{
											List<String> paraList = new ArrayList<String>();
											for(DefaultAttribute ele:atrrs){
												String atrr = ele.getName(); 
												if(atrr.contains("keyword") || atrr.contains("pagenum") || atrr.contains("count")){
													continue;
												}
												String value = urlele.attributeValue(atrr);  
												paraList.add(value);   // 除页数和关键字的属性之外其他的参数
											}
											URLBean urlbean = new URLBean();
											urlbean.setList(paraList);
											urlbean.setUrl(urlf);
											urlbean.setBaseURL(urlf);
											if(MapUtils.isNotEmpty(dataMap)){
												urlbean.setParaMap(dataMap);
											}
											urllist.add(urlbean);
											
										}
									}else{
										URLBean urlbean = new URLBean();
										urlbean.setUrl(urlf);
										urlbean.setBaseURL(urlf);
										urllist.add(urlbean);
									}
									
								}
								pagecon.setUrls(urllist);    // 具体url
 							}
							
							Element fetele = page.element("fetcher");
							
							CrawlParameter crawlPara = new CrawlParameter();
							
							List<Element> felelist = fetele.elements();
							
							for(Element ele:felelist){
								String fname = ele.getName();
								String fvalue = ele.getTextTrim();
								addCrawlPara(fname, fvalue, crawlPara);  
							}
							
							pagecon.setCrawlPara(crawlPara);   // 采集参数
							
							Element parsele = page.element("parse");
							
							
							 List<Element> paraeles = parsele.elements("parameter");
							 
							 if(CollectionUtils.isNotEmpty(paraeles)){
								 List<ParseParameter> parseList = new ArrayList<ParseParameter>();
								 for(Element paraele:paraeles){
									 List<Element> pelelist = paraele.elements();
									 ParseParameter parsePara = new ParseParameter();
									 Map<String,String> regMap = new HashMap<String,String>();
									 for(Element ele:pelelist){
										String fname = ele.getName();
										if("subexpress".equals(fname)){
											String key = ele.attributeValue("filed");
											String value = ele.getTextTrim();
											regMap.put(key, value);
											continue;
										}
										
										String fvalue = ele.getTextTrim();
										if("intelligent".equals(fvalue)){
											addParsePara("isintelligent","true",parsePara);
											addParsePara("intelltype","content",parsePara);
										}else{
											addParsePara(fname, fvalue, parsePara);  
										}
									 }
									 parsePara.setExtractreg(regMap);
									 parseList.add(parsePara);
								 }
								 pagecon.setParsePara(parseList);  // 解析的参数
							 }
							 pageList.add(pagecon);
						}
						jobCon.setCronExp(cron);
						jobCon.setName(name);
						jobCon.setPageConList(pageList);
						configMap.put(name, jobCon);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return configMap;
	}
	
	
	public static CrawlParameter addCrawlPara(String name,String value,CrawlParameter crawlPara){
		if(name==null){
			name = "";
		}
		if(value==null){
			value="";
		}
		
		if(name.equals("type")){
			crawlPara.setType(CrawlType.valueOf(value));
		}else if(name.equals("encode")){
			crawlPara.setEncode(value);
		}else if(name.equals("isusejs")){
			crawlPara.setUseJs(getTrueOrFalse(value));
		}else if(name.equals("bv")){
			
		}else if(name.equals("readtime")){
			
		}else if(name.equals("conntime")){
			
		}else if(name.equals("proFilePath")){
			
		}else if(name.equals("fireFoxPath")){
			
		}else if(name.equals("reqmethod")){
			crawlPara.setReqmethod(value);
		}else if(name.equals("reqmap")){
			if(StringUtils.isNotBlank(value)){
				Map<String,String> map = new HashMap<String,String>();
				List<String> list = getList(value,";");
				for(String s:list){
					String[] arr = s.split(":");
					map.put(arr[0], arr[1]);
				}
				crawlPara.setReqmap(map);
			}
		}else if(name.equals("referrer")){
			if(StringUtils.isNotBlank(value)){
				crawlPara.setReferrer(value);
			}
			
		}else if(name.equals("cookie")){
			
		}else if(name.equals("header")){
			
			if(StringUtils.isNotBlank(value)){
				Map<String,String> map = new HashMap<String,String>();
				List<String> list = getList(value,";");
				for(String s:list){
					String[] arr = s.split(":::");
					map.put(arr[0], arr[1]);
				}
				crawlPara.setHeader(map);
			}
			
			
		}else if(name.equals("isUseWebClient")){
			
		}else if(name.equals("webClient")){
			
		}else if(name.equals("isuseproxy")){
			crawlPara.setUseProxy(getTrueOrFalse(value));
		}else if(name.equals("proxyurl")){
			/*ProxyBean proxy = QueryProxy.testProxy(value);
			crawlPara.setProxyBean(proxy);*/
		}else if(name.equals("isignorecontenttype")){
			crawlPara.setIsignorecontenttype(getTrueOrFalse(value));
		}
		
		return crawlPara;
	}
	
	
public static ParseParameter addParsePara(String name,String value,ParseParameter parsePara){
		
		if(name.equals("fileds")){
			List<String> filedlist = getList(value, ";");
			parsePara.setFileds(filedlist);
		}else if(name.equals("expression")){
			parsePara.setExpression(value);
		}else if(name.equals("type")){
			parsePara.setType(ParseType.valueOf(value));
		}else if(name.equals("intelltype")){
			parsePara.setIntellType(IntelligentType.valueOf(value));
		}else if(name.equals("isintelligent")){
			parsePara.setIntelligent(getTrueOrFalse(value));
		}else if(name.equals("issave")){
			parsePara.setSave(getTrueOrFalse(value));
		}else if(name.equals("isnextpara")){
			parsePara.setNextPara(getTrueOrFalse(value));
		}else if(name.equals("nextpara")){
			List<String> paraList = getList(value,";");
			parsePara.setNextPara(paraList);
		}else if(name.equals("savepara")){
			List<String> saveList = getList(value,";");
			parsePara.setSavePara(saveList);
		}
		return parsePara;
	}
	
	
	public static boolean getTrueOrFalse(String flag){
		if("true".equals(flag)){
			return true;
		}
		return false;
	}
	
	public static List<String> getList(String text,String reg){
		
		String patern = "\\s*|\t|\r|\n";
		text =text.replaceAll(patern, "");
		List<String> list = null;
		String[] arr = text.split(reg);
		if(ArrayUtils.isNotEmpty(arr)){
			list = new ArrayList<String>();
			for(String s:arr){
				list.add(s);
			}
		}
		return list;
	}
	
	/**
	 *   拼装urlList
	 * @param list
	 * @param urllist
	 * @return
	 */
	
	public static List<String> getUrlList(List<List<String>> list,List<String> urllist){
		
		for(int i=0; i<list.size(); i++){
			List<String> l = list.get(i);
			urllist = getUrls(urllist,l,i);
			
		}
		return urllist;
	}
	
	
	public static List<String>  getUrls(List<String> urllist,List<String> l,int i){
		List<String> resList = new ArrayList<String>();
		for(String s:l){
			for(String url:urllist){
				String ss = MessageFormat.format(url, s);
				int n = getMany(ss,'{');
				for(int k=1; k<=n; k++){
					ss = ss.replace("{"+k+"}", "{"+(k-1)+"}");
				}
				resList.add(ss);
			}
		}
		return resList;
	}
	
	public static int getMany(String ss, char c){
		int total = 0;
		for (int i = 0; i < ss.length(); i ++){
			if(ss.charAt(i)==c){
		    total ++;
		  }
		}
		return total;
		
	}

}

