package wsp.koutofn.pepa.tool.model;

import java.util.ArrayList;

import wsp.koutofn.pepa.tool.pepa.Action;
import wsp.koutofn.pepa.tool.pepa.State;
import wsp.koutofn.pepa.tool.utality.Const;

public class GSystem implements Component, PepaComponent{
	ArrayList<State> pepaSts = new ArrayList<State>();
	private int[] bcCounts;
	private int K;
	private int N;
	
	public GSystem(ArrayList<BasicComponent> bcList, int K, int N) {
		this.K = K;
		this.N = N;
		bcCounts = new int[bcList.size()];
		int total = 1;
		for(int i=0; i<bcList.size(); i++) {
			total = total * (bcList.get(i).getCount()+1);
			bcCounts[i] = bcList.get(i).getCount()+1;
		}
		
		int key[] = new int[bcList.size()];
		for(int index=0; index<total; index++) {
			for(int j=0; j<bcList.size(); j++) {
				key[j] = getNext(index,j);
			}
			int prevSysSt = indicateSysSt(key);
			if(prevSysSt == -1) {
				continue;
			}
			
			State st_ex = new State(getStateName(key, true));
			Action act_ex = new Action(getSignalName(prevSysSt), Const.fast, getStateName(key, false));
			st_ex.addAction(act_ex);
			pepaSts.add(st_ex);
			
			State st_in = new State(getStateName(key, false));
			for(int i=0; i<bcList.size(); i++) {
				// fail active action
				int[] nextKey = copyKey(key, i, 1);
				if(isValid(nextKey, bcList, K, N)) {
					Action act_fail_a = new Action(BasicComponent.getActiveFailActionName(i, Const.copSystem), Const.infty, null);
					int nextSysSt = indicateSysSt(nextKey);
					if(nextSysSt == 0) {
						String nextStName = getStateName(nextKey, true);
						int q = get_q(nextKey, bcList);
						Action act_hot = new Action(BasicComponent.getHotActionName(q), Const.fast, nextStName);
						st_in.addAction(act_fail_a);
						st_in.addAction(act_hot);
					}else if (nextSysSt == 1) {
						st_in.addAction(act_fail_a);
						String nextStName = getStateName(nextKey, true);
						for(int k=0; k<bcList.size(); k++) {
							if(k == bcList.size()-1) {
								Action act_freeze = new Action(BasicComponent.getFreezeActionName(k), Const.fast, nextStName);
								st_in.addAction(act_freeze);
							}else {
								Action act_freeze = new Action(BasicComponent.getFreezeActionName(k), Const.fast, null);
								st_in.addAction(act_freeze);
							}
							
						}
					}
					
					//fail warm action
					Action act_fail_w = new Action(BasicComponent.getWarmFailActionName(i, Const.copSystem), Const.infty, getStateName(nextKey, true));
					st_in.addAction(act_fail_w);
				}
				
				
				
				//repair action
				nextKey = copyKey(key, i, -1);
				if(isValid(nextKey, bcList, K, N)) {
					int nextSysSt = indicateSysSt(nextKey);
					int q = get_q(key, bcList);
					String nextStName = getStateName(nextKey, true);
					if(prevSysSt == 0 && nextSysSt == 0 && i < q) {
						Action act_repair = new Action(RepairFacility.getRepairActionName(i, Const.copSystem), Const.infty, null);
						st_in.addAction(act_repair);
						
						Action act_hot = new Action(BasicComponent.getHotActionName(i), Const.fast, null);
						st_in.addAction(act_hot);
						
						Action act_warm = new Action(BasicComponent.getWarmActionName(q), Const.fast, nextStName);
						st_in.addAction(act_warm);
					}else if(prevSysSt == 0 && nextSysSt == 0 && i >= q) {
						Action act_repair = new Action(RepairFacility.getRepairActionName(i, Const.copSystem), Const.infty, nextStName);
						st_in.addAction(act_repair);
						
					}else if(prevSysSt == 1 && nextSysSt == 1) {
						Action act_repair = new Action(RepairFacility.getRepairActionName(i, Const.copSystem), Const.infty, null);
						st_in.addAction(act_repair);
						
						Action act_freeze = new Action(BasicComponent.getFreezeActionName(i), Const.fast, nextStName);
						st_in.addAction(act_freeze);
					}else if(prevSysSt == 1 && nextSysSt == 0) {
						Action act_repair = new Action(RepairFacility.getRepairActionName(i, Const.copSystem), Const.infty, null);
						st_in.addAction(act_repair);
						
						
						int importIndex = get_q(nextKey, bcList);
						
						for(int k=0; k<bcList.size(); k++) {
							
							String act_name = "";
							if(k<importIndex) {
								act_name = BasicComponent.getDefreezeActionName(k, bcList.get(k).getCount()-nextKey[k]);
							}else if (k == importIndex) {
								act_name = BasicComponent.getDefreezeActionName(k, K-getTotalUpBcNum(nextKey, bcList, k-1));
							}else {
								act_name = BasicComponent.getDefreezeActionName(k, 0);
							}
							
							if(k == bcList.size()-1) {	
								Action act_defreeze = new Action(act_name, Const.fast, nextStName);
								st_in.addAction(act_defreeze);
							}else {
								Action act_freeze = new Action(act_name, Const.fast, null);
								st_in.addAction(act_freeze);
							}
							
						}
					} 
				}
				
			}
			pepaSts.add(st_in);
		}
		initGSysSts();	
	}
	
	
	
	public void initGSysSts() {
		State st_0 = new State(getGSysStName(0));
		State st_1 = new State(getGSysStName(1));
		
		Action act1 = new Action(getSignalName(1), Const.infty, getGSysStName(1));
		Action act0 = new Action(getSignalName(0), Const.infty, getGSysStName(0));
		
		st_0.addAction(act0);
		st_0.addAction(act1);
		
		st_1.addAction(act0);
		st_1.addAction(act1);
		
		pepaSts.add(st_0);
		pepaSts.add(st_1);
	}
	
	public static String getGSysStName(int st) {
		return "GSys_" + st;
	}
	
	public boolean isValid(int[] key, ArrayList<BasicComponent> bcList, int k, int n) {
		int totalFail = 0;
		for(int i=0; i<bcList.size(); i++) {
			if(key[i] < 0 || key[i] > bcList.get(i).getCount()) {
				return false;
			}
			totalFail += key[i];
		}
		if(totalFail > n-k+1) {
			return false;
		}
		return true;
	}
	
	
	private int get_q(int[] key, ArrayList<BasicComponent> bcList) {
		for(int q=0; q<bcList.size(); q++) {
			if(K > getTotalUpBcNum(key, bcList, q-1) && K <= getTotalUpBcNum(key, bcList, q)) {
				return q;
			}
		}
		return -1;
	}
	
	private int getTotalUpBcNum(int[] key, ArrayList<BasicComponent> bcList, int index) {
		int totalUpBcNum = 0;
		if(index < 0) {
			return 0;
		}
		
		for(int i=0; i<=index; i++) {
			totalUpBcNum += (bcList.get(i).getCount()-key[i]);
		}
		return totalUpBcNum;
	}
	
	public static String getSignalName(int indicator) {
		return "g_" + indicator;
	}
	
	/**
	 * @param mode 
	 * */
	public static String getStateName(int[] key, boolean external) {
		String stName = "G";
		if(external) {
			stName += "_ex";
		}else {
			stName += "_in";
		}
		for(int i=0;i<key.length;i++) {
			stName += "_" + key[i];
		}
		return stName;
	}
	
	private int[] copyKey(int[] key, int index, int op) {
		int[] ret = new int[key.length];
		for(int i=0; i<key.length; i++) {
			if(index == i) {
				ret[i] = key[i] + op;
			}else {
				ret[i] = key[i];
			}
		}
		return ret;
	}
	
	/**
	 * @return -1 invalid state
	 * @return 1 down state
	 * @return 0 up state
	 * */
	private int indicateSysSt(int[] key) {
		int totalFail = 0;
		for(int i=0; i<key.length; i++) {
			totalFail += key[i];
		}
		if(totalFail > N - K + 1) {
			return -1;
		}
		if(totalFail > N - K) {
			return 1;
		}else {
			return 0;
		}
	}
		
	private int getNext(int index, int j) {	
		if(j==bcCounts.length-1) {
			return index%bcCounts[j];
		}else {
			int total = 1;
			for(int i=j+1; i<bcCounts.length; i++) {
				total = total * bcCounts[i];
			}
			return (index/total)%bcCounts[j];
		}
		
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

	@Override
	public String toPEPAComponents() {
		int[] key = new int[bcCounts.length];
		return getStateName(key, true);
	}

}
