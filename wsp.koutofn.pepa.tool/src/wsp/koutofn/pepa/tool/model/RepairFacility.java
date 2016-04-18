package wsp.koutofn.pepa.tool.model;

import java.util.ArrayList;

import wsp.koutofn.pepa.tool.pepa.Action;
import wsp.koutofn.pepa.tool.pepa.State;
import wsp.koutofn.pepa.tool.utality.Const;

public class RepairFacility implements Component, PepaComponent{
	ArrayList<State> pepaSts = new ArrayList<State>();
	private int count;
	
	public RepairFacility(int count, ArrayList<BasicComponent> bcList) {
		this.count = count;
		State st = new State(getIdleSt());
		pepaSts.add(st);
		for(BasicComponent bc: bcList) {
			String nextStName = getOccupyState(bc.getPriority());
			String actName = getOccupyActName(bc.getPriority());
			Action act = new Action(actName, Const.fast, nextStName);
			st.addAction(act);
			
			State occSt = new State( getOccupyState(bc.getPriority()) );
			Action occAct = new Action(getRepairActionName(bc.getPriority(), Const.copSystem), bc.getRepairRate()+"", null);
			occSt.addAction(occAct);
			
			Action occAct2 = new Action(getRepairActionName(bc.getPriority(), Const.copBasicComponent), Const.fast, getIdleSt());
			occSt.addAction(occAct2);
			
			pepaSts.add(occSt);
		}
	}
	
	public static String getRepairActionName(int pri, int copObject) {
		if(copObject == Const.copBasicComponent) {
			return "repair_" + pri + "_bc";
		}else {
			return "repair_" + pri + "_sys";
		}
		
	}
	
	
	public static String getIdleSt() {
		return "V_idle";
	}
	
	public static String getOccupyState(int pri) {
		return "V_" + pri;
	}
	
	public static String getOccupyActName(int pri) {
		return "occ_" + pri;
	}

	@Override
	public ArrayList<String> toPEPAStates() {
		ArrayList<String> ret = new ArrayList<String>();
		for(State st: pepaSts) {
			String str = st.toString();
			ret.add(str);
		}
		return ret;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public String toPEPAComponents() {
		String str = "(";
//		for(int i=0; i<count; i++) {
//			if(i==0) {
//				str += getIdleSt();
//			}else {
//				str += "||"+getIdleSt();
//			}
//		}
		if(count == 1) {
			str += getIdleSt();
		}else {
			str += getIdleSt() + "[" + count + "]";
		}
		
		str += ")";
		return str;
	}
}
