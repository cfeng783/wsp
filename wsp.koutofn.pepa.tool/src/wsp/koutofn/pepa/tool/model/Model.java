package wsp.koutofn.pepa.tool.model;

import java.util.ArrayList;

import wsp.koutofn.pepa.tool.pepa.State;
import wsp.koutofn.pepa.tool.utality.Const;

public class Model implements Component, PepaComponent{
	private ArrayList<BasicComponent> bcList;
	private RepairFacility rf;
	private RepairQueue rq;
	private GSystem sys;
	private int K;
	private int N = 0;
	
	
	public Model() {
		bcList = new ArrayList<BasicComponent>();
	}
	
	/**
	 * @param number of repair facilities
	 * @param minimal number of active components required
	 * */
	public void init(int repairFacilityCount, int k) {
		this.K = k;
		for(int i=0; i<bcList.size(); i++) {
			N += bcList.get(i).getCount();
		}
		
		this.rf = new RepairFacility(repairFacilityCount, bcList);
		this.rq = new RepairQueue(bcList, K, N);
		
		this.sys = new GSystem(bcList, K, N);
	}
	
	public void addBasicComponent(BasicComponent bc) {
		if(rf == null && rq == null && sys == null) {
			bc.setPriority(bcList.size());
			bcList.add(bc);
		}else {
			System.err.println("You cannot add basic component after model has been initiated!");
		}
	}
	
	public ArrayList<BasicComponent> getBcList() {
		return bcList;
	}

	public void setBcList(ArrayList<BasicComponent> bcList) {
		this.bcList = bcList;
	}

	public RepairFacility getRf() {
		return rf;
	}

	public void setRf(RepairFacility rf) {
		this.rf = rf;
	}

	public RepairQueue getRq() {
		return rq;
	}

	public void setRq(RepairQueue rq) {
		this.rq = rq;
	}

	public GSystem getSys() {
		return sys;
	}

	public void setSys(GSystem sys) {
		this.sys = sys;
	}
	
	private String getPepaCompForBcList() {
		int q = get_q();
		int h = getTotalBcNum(q)-K;
		String ret = "";
		for(int i=0; i<bcList.size(); i++) {
			BasicComponent bc = bcList.get(i);
			if(i<q) {
				ret += BasicComponent.getStName(i, bc.getCount(), 0, 0, 0, false) + " || ";
			}else if(i==q) {
				ret += BasicComponent.getStName(i, bc.getCount()-h, h, 0, 0, false) + " || ";
			}else {
				ret += BasicComponent.getStName(i, 0, bc.getCount(), 0, 0, false) + " || ";
			}
		}
		return ret.substring(0, ret.length()-4);
	}
	
	private int get_q() {
		for(int q=0; q<bcList.size(); q++) {
			if(K > getTotalBcNum(q-1) && K <= getTotalBcNum(q)) {
				return q;
			}
		}
		return -1;
	}
	
	private int getTotalBcNum(int index) {
		int total = 0;
		if(index < 0) {
			return 0;
		}
		
		for(int i=0; i<=index; i++) {
			total += (bcList.get(i).getCount());
		}
		return total;
	}

	@Override
	public ArrayList<String> toPEPAStates() {
		ArrayList<String> ret = new ArrayList<String>();
		ret.add("eps=100;");
		ret.add(" ");
		for(BasicComponent bc: bcList) {
			ret.addAll(bc.toPEPAStates());
			ret.add(" ");
		}
		ret.add("//repair facility");
		ret.addAll(rf.toPEPAStates());
		ret.add(" ");
		ret.add("//repair queue");
		ret.addAll(rq.toPEPAStates());
		ret.add(" ");
		ret.add("//system");
		ret.addAll(sys.toPEPAStates());
		return ret;
	}

	@Override
	public String toPEPAComponents() {
		String str = "((((" + getPepaCompForBcList() + ")";
		str += " <";
		for(int i=0; i<bcList.size(); i++) {
			str += BasicComponent.getActiveFailActionName(i, Const.copSystem) + ",";
			str += BasicComponent.getWarmFailActionName(i, Const.copSystem) + ",";
			str += BasicComponent.getWarmActionName(i) + ",";
			str += BasicComponent.getHotActionName(i) + ",";
			str += BasicComponent.getFreezeActionName(i) + ",";
			for(int j=0; j<=bcList.get(i).getCount(); j++) {
				str += BasicComponent.getDefreezeActionName(i,j) + ",";
			}
			
		}
		str = str.substring(0, str.length()-1);
		str += "> ";
		str += sys.toPEPAComponents() + ")";
		str += " <";
		for(int i=0; i<bcList.size(); i++) {
			str += BasicComponent.getActiveFailActionName(i, Const.copRepairQueue) + ",";
			str += BasicComponent.getWarmFailActionName(i, Const.copRepairQueue) + ",";
		}
		str = str.substring(0, str.length()-1);
		str += "> ";
		str += rq.toPEPAComponents() + ")";
		str += " <";
		for(int i=0; i<bcList.size(); i++) {
			str += RepairFacility.getRepairActionName(i, Const.copBasicComponent) + ",";
			str += RepairFacility.getRepairActionName(i, Const.copSystem) + ",";
			str += RepairFacility.getOccupyActName(i) + ",";
		}
		str = str.substring(0, str.length()-1);
		str += "> ";
		str += rf.toPEPAComponents() + ")";
		str += " <" + GSystem.getSignalName(0) + "," + GSystem.getSignalName(1) + "> " + GSystem.getGSysStName(0);
		return str;
	}
}
