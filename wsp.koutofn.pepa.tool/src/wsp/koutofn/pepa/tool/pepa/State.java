package wsp.koutofn.pepa.tool.pepa;

import java.util.ArrayList;

public class State {
	private String name;
	private ArrayList<Action> actionList;
	
	public State(String name) {
		this.name = name;
		this.actionList = new ArrayList<Action>();
	}
	
	public State(String name, ArrayList<Action> actionList) {
		this.name = name;
		this.actionList = actionList;
	}
	
	public void addAction(Action e) {
		this.actionList.add(e);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<Action> getActionList() {
		return actionList;
	}

	public void setActionList(ArrayList<Action> actionList) {
		this.actionList = actionList;
	}
	
	public String toString() {
		String str = name + " = ";
		boolean isFirst = true;
		boolean isTempt = false;
		for(Action act: this.actionList){
			if(isFirst){
				str += act.toString();
				if(act.getNextSt().isEmpty()) {
					isTempt = true;
				}
				isFirst = false;
			}else {
				if(isTempt) {
					str += act.toString();
				}else{
					str += " + " + act.toString();
				}
				
				if(act.getNextSt().isEmpty()) {
					isTempt = true;
				}else {
					isTempt = false;
				}
				
			}
		}
		str += ";";
		return str;
	}
	
}
