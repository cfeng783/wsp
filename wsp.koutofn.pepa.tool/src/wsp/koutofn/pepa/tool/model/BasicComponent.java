package wsp.koutofn.pepa.tool.model;

import java.util.ArrayList;

import wsp.koutofn.pepa.tool.pepa.Action;
import wsp.koutofn.pepa.tool.pepa.State;
import wsp.koutofn.pepa.tool.utality.Const;

public class BasicComponent implements Component {
	private double activeFailRate;
	private double warmFailRate;
	private double repairRate;
	private int priority; //priority also indicate the identity of the group of components
	private int count;
	
	/**
	 * @param priority
	 * @param active fail rate
	 * @param warm fail rate
	 * @param repair rate
	 * @param total count
	 */
	public BasicComponent(int priority, double activeFailRate, double warmFailRate, double repairRate, int count) {
		this.priority = priority;
		this.activeFailRate = activeFailRate;
		this.warmFailRate = warmFailRate;
		this.count = count;
		this.repairRate = repairRate;
	}
	
	public BasicComponent(double activeFailRate, double warmFailRate, double repairRate, int count) {
		this.activeFailRate = activeFailRate;
		this.warmFailRate = warmFailRate;
		this.count = count;
		this.repairRate = repairRate;
	}
	
	
	public ArrayList<State> getPEPASts() {
		ArrayList<State> ret = new ArrayList<State>();
		for(int a=0; a<=count; a++) {
			for(int w=0; w<=count; w++) {
				if(a+w <= count) {
					int f = count-a-w;
					String stName = getStName(priority, a, w, f, 0, false);
					State st = new State(stName);
					
					
					if(a>0) {
						String nextStName = getStName(priority, a-1, w, f+1, 0, false);
						String actName = getActiveFailActionName(priority, Const.copSystem);
						double rate = a*activeFailRate;
						Action act1 = new Action(actName, rate+"", null);
						st.addAction(act1);
						Action act2= new Action(getActiveFailActionName(priority, Const.copRepairQueue), Const.fast, nextStName);
						st.addAction(act2);
					}
					
					if(w>0) {
						String nextStName = getStName(priority, a, w-1, f+1, 0, false);
						String actName = getWarmFailActionName(priority, Const.copSystem);
						double rate = w*warmFailRate;
						Action act1 = new Action(actName, rate+"", null);
						st.addAction(act1);
						Action act2 = new Action(getWarmFailActionName(priority, Const.copRepairQueue), Const.fast, nextStName);
						st.addAction(act2);
					}
					
					if(f>0) {
						String nextStName = getStName(priority, a, w+1, f-1, 0, false);
						String actName = RepairFacility.getRepairActionName(priority, Const.copBasicComponent);
						Action act = new Action(actName, Const.infty, nextStName);
						st.addAction(act);
					}
					
					if(a>0) {
						String nextStName = getStName(priority, a-1, w+1, f, 0, false);
						String actName = getWarmActionName(priority);
						Action act = new Action(actName, Const.infty, nextStName);
						st.addAction(act);
					}
					
					if(w>0) {
						String nextStName = getStName(priority, a+1, w-1, f, 0, false);
						String actName = getHotActionName(priority);
						Action act = new Action(actName, Const.infty, nextStName);
						st.addAction(act);
					}
					
					if(true) {
						String nextStName = getStName(priority, 0, 0, f, a+w, true);
						String actName = getFreezeActionName(priority);
						Action act = new Action(actName, Const.infty, nextStName);
						st.addAction(act);
					}
					
					//deal with deadlock
//					String nextStName = getStName(priority, a, w, f, 0, false);
//					String actName = getDefreezeActionName(priority);
//					Action act = new Action(actName, Const.infty, nextStName);
//					st.addAction(act);
//					if(a+w == 0) {
//						
//					}
					
					if(st.getActionList().size() > 0) {
						ret.add(st);
					}
				}
			}
		}
		
		for(int f=0; f<=count; f++) {
			int z = count - f;
			String stName = getStName(priority, 0, 0, f, z, true);
			State st = new State(stName);
			
//			if(f>0) {
//				String nextStName = getStName(priority, 0, 1, f-1, 0);
//				String actName = getRepairActionName(priority);
//				Action act = new Action(actName, Const.infty, nextStName);
//				st.addAction(act);
//			}
			
			if(true) {
				for(int a = 0; a<=z; a++) {
					String nextStName = getStName(priority, a, z-a, f, 0, false);
					String actName = getDefreezeActionName(priority, a);
					Action act = new Action(actName, Const.infty, nextStName);
					st.addAction(act);
				}
				
				if(f>0) {
					String nextStName2 = getStName(priority, 0, 0, f-1, z+1, true);
					String actName2 = RepairFacility.getRepairActionName(priority,Const.copBasicComponent);
					Action act2 = new Action(actName2, Const.infty, nextStName2);
					st.addAction(act2);
				}
			}
			
			if(st.getActionList().size() > 0) {
				ret.add(st);
			}
		}
		
		return ret;
	}
	
	
	public static String getStName(int pri, int a, int w, int f, int z, boolean frozen){
		if(frozen) {
			return "B_frozen" + "_" + pri + "_a" + a + "_w" + w + "_f" + f + "_z" + z;
		}else {
			return "B_unfrozen" + "_" + pri + "_a" + a + "_w" + w + "_f" + f + "_z" + z;
		}
		
	}
	
	//
	public static String getActiveFailActionName(int pri, int copObject){
		if(copObject == Const.copSystem) {
			return "fail_" + pri + "_a_sys";
		}else {
			return "fail_" + pri + "_a_rq";
		}
	}
	
	public static String getWarmFailActionName(int pri, int copObject) {
		if(copObject == Const.copSystem) {
			return "fail_" + pri + "_w_sys";
		}else {
			return "fail_" + pri + "_w_rq";
		}
	}
	
//	public static String getRepairActionName(int pri) {
//		return "repair_" + pri;
//	}
//	
	public static String getWarmActionName(int pri) {
		return "warm_" + pri;
	}
	
	public static String getHotActionName(int pri) {
		return "hot_" + pri;
	}
	
	public static String getFreezeActionName(int pri) {
		return "freeze_" + pri;
	}
	
	public static String getDefreezeActionName(int pri, int a) {
		return "defreeze_" + pri + "_a" + a;
	}
	
	public static String getDefreezeActionName(int pri) {
		return "defreeze_" + pri;
	}
	
	public double getActiveFailRate() {
		return activeFailRate;
	}
	public void setActiveFailRate(double activeFailRate) {
		this.activeFailRate = activeFailRate;
	}
	public double getWarmFailRate() {
		return warmFailRate;
	}
	public void setWarmFailRate(double warmFailRate) {
		this.warmFailRate = warmFailRate;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}


	
	@Override
	public ArrayList<String> toPEPAStates() {
		ArrayList<String> ret = new ArrayList<String>();
		ArrayList<State> pepaSts = this.getPEPASts();
		for(State st: pepaSts) {
			String str = st.toString();
			ret.add(str);
		}
		return ret;
	}


	public double getRepairRate() {
		return repairRate;
	}


	public void setRepairRate(double repairRate) {
		this.repairRate = repairRate;
	}


}
