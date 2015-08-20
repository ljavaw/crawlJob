package com.dinfo.parse.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


public class ParseParameter implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<String> fileds;
	
	private String expression ;  // 表达式
	
	private ParseType type = ParseType.regParse; //解析的不同类型
	
	private IntelligentType intellType ;  // 智能抽取哪一部分
	
	private boolean isIntelligent;  //是否智能抽取
	
	private boolean isSave;  //是否入库
	
	private List<String> savePara; //保存参数
	
	private boolean isNextPara ; //是否是下一层的请求参数
	
	private List<String> nextPara;  // 作为一下层参数的字段名称
	
	
	private Map<String,String> extractreg; //每个字段的提取正则规则  
	
	
	
	
	
	


	public Map<String, String> getExtractreg() {
		return extractreg;
	}

	public void setExtractreg(Map<String, String> extractreg) {
		this.extractreg = extractreg;
	}

	public List<String> getSavePara() {
		return savePara;
	}

	public void setSavePara(List<String> savePara) {
		this.savePara = savePara;
	}

	public List<String> getNextPara() {
		return nextPara;
	}

	public void setNextPara(List<String> nextPara) {
		this.nextPara = nextPara;
	}

	public boolean isNextPara() {
		return isNextPara;
	}

	public void setNextPara(boolean isNextPara) {
		this.isNextPara = isNextPara;
	}

	public boolean isSave() {
		return isSave;
	}

	public void setSave(boolean isSave) {
		this.isSave = isSave;
	}


	public List<String> getFileds() {
		return fileds;
	}

	public void setFileds(List<String> fileds) {
		this.fileds = fileds;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public IntelligentType getIntellType() {
		return intellType;
	}

	public void setIntellType(IntelligentType intellType) {
		this.intellType = intellType;
		this.setIntelligent(true);
	}

	public boolean isIntelligent() {
		return isIntelligent;
	}

	public void setIntelligent(boolean isIntelligent) {
		this.isIntelligent = isIntelligent;
	}

	public ParseType getType() {
		return type;
	}

	public void setType(ParseType type) {
		this.type = type;
	}
}
