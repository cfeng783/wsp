package wsp.koutofn.pepa.tool.pepa;

import java.text.DecimalFormat;

import wsp.koutofn.pepa.tool.utality.Const;

public class Action {
	private String name;
	private String rate;
	private String nextSt;
	
	public Action(String name, String rate, String nextSt) {
		this.name = name;
		this.rate = rate;
		this.nextSt = nextSt;
		if(this.nextSt == null) {
			this.nextSt = "";
		}
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRate() {
		return rate;
	}
	public void setRate(String rate) {
		this.rate = rate;
	}
	public String getNextSt() {
		return nextSt;
	}
	public void setNextSt(String nextSt) {
		this.nextSt = nextSt;
	}
	
	public String toString() {
		DecimalFormat df = new DecimalFormat("#.#");
        df.setMaximumFractionDigits(8);
        String pr = "";
        
        if(rate.equals(Const.infty) || rate.equals(Const.fast) ) {
        	pr = rate;
        }else {
        	pr = df.format(Double.parseDouble(rate));
        }
        	
		String str = "(" + name + ", " + pr + ")." + nextSt;
		return str;
	}
	
}

