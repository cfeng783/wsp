package wsp.koutofn.pepa.tool.framework;

import java.util.ArrayList;

import wsp.koutofn.pepa.tool.model.BasicComponent;
import wsp.koutofn.pepa.tool.model.Model;
import wsp.koutofn.pepa.tool.model.RepairFacility;
import wsp.koutofn.pepa.tool.model.RepairQueue;
import wsp.koutofn.pepa.tool.parser.Parser;
import wsp.koutofn.pepa.tool.utality.Const;

public class MainProgram {

	public static void main(String[] args) {
		
		
		if (args.length == 0){
			System.out.println("illegal argument! Specify a file name!");
			return;
		}else if(args.length > 1) {
			System.out.println("illegal argument! Only specify a file name!");
			return;
		}
		
		//BasicComponent bc1 = new BasicComponent(0.0007,0.0007, 0.05, 2);
		//BasicComponent bc2 = new BasicComponent(0.001,0.0005, 0.03, 2);
//		BasicComponent bc3 = new BasicComponent(0.2,0.1,0.1,1);
	//	BasicComponent bc4 = new BasicComponent(0.2,0.1,0.1,1);
		
		
//		Model model = new Model();
//		model.addBasicComponent(bc1);
//		model.addBasicComponent(bc2);
//		model.addBasicComponent(bc3);
	//	model.addBasicComponent(bc4);
		//model.init(2, 3);
		Parser parser = new Parser();
		Model model = null;
		try{
			model = parser.parse(args[0]);
		}catch(Exception e) {
			System.err.println("error in " + args[0]);
		}
		
		if(model != null) {
			ArrayList<String> modelDesp = model.toPEPAStates();
			modelDesp.add(" ");
			modelDesp.add(model.toPEPAComponents());
			try{
				parser.output(modelDesp, args[0]);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		
	}

}
