package wsp.koutofn.pepa.tool.model;

import java.util.ArrayList;

import wsp.koutofn.pepa.tool.pepa.Action;
import wsp.koutofn.pepa.tool.pepa.State;
import wsp.koutofn.pepa.tool.utality.Const;

public class RepairQueue implements Component, PepaComponent{
	ArrayList<State> pepaSts = new ArrayList<State>();
	private int[] bcCounts;
	
	public RepairQueue(ArrayList<BasicComponent> bcList, int k, int n) {
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
			State st = new State(getStateName(key));
			for(int i=0; i<bcList.size(); i++) {
				int[] nextKey = copyKey(key,i,1);
				if(isValid(nextKey, bcList, k, n)) {
					Action act1 = new Action(BasicComponent.getActiveFailActionName(bcList.get(i).getPriority(), Const.copRepairQueue), 
							Const.infty, getStateName(nextKey));
					st.addAction(act1);
					
					Action act2 = new Action(BasicComponent.getWarmFailActionName(bcList.get(i).getPriority(), Const.copRepairQueue), 
							Const.infty, getStateName(nextKey));
					st.addAction(act2);
				}
				
			}
			
			int occIndex = chooseOccIndex(key);
			if(occIndex != -1) {
				int[] nextKey = copyKey(key,occIndex, -1);
				if(isValid(nextKey, bcList,k ,n)) {
					Action act = new Action(RepairFacility.getOccupyActName(bcList.get(occIndex).getPriority()), 
							Const.infty, getStateName(nextKey));
					st.addAction(act);
				}
				
			}
			if(st.getActionList() != null && st.getActionList().size()>0)
				pepaSts.add(st);
		}
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
	
	
	public static String getStateName(int[] key) {
		String stName = "L";
		for(int i=0;i<key.length;i++) {
			stName += "_" + key[i];
		}
		return stName;
	}
	
	private int chooseOccIndex(int[] key) {
		for(int i=0; i<key.length; i++) {
			if(key[i] > 0) {
				return i;
			}
		}
		return -1;
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
		return getStateName(key);
	}
	
}
