package wsp.koutofn.pepa.tool.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import wsp.koutofn.pepa.tool.model.BasicComponent;
import wsp.koutofn.pepa.tool.model.Model;

public class Parser {
	int k;
	int repairman;
	
	public Model parse(String filename) throws Exception {
		Model model = new Model();
	    int index = 0;
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename))); 
		String data;
		while((data = br.readLine())!=null)  
		{  	
			index++;
			if(index == 1) {
				k = Integer.parseInt(data.trim());
			}else if(index == 2) {
				repairman = Integer.parseInt(data.trim());
			}else {
				String[] str = data.split(",");
				BasicComponent bc = new BasicComponent(Double.parseDouble(str[0].trim()), Double.parseDouble(str[1].trim()), Double.parseDouble(str[2].trim()), Integer.parseInt(str[3].trim()));
				model.addBasicComponent(bc);
			}
		}
		br.close();
		model.init(repairman, k);
		return model;
	}
	
	public void output(ArrayList<String> modelDesp, String filename) throws Exception {
		File file = new File(filename);
		String parent = file.getParent();
		if(parent == null) {
			parent = ".";
		}
		System.out.println(parent + "/output.pepa created!");
		PrintWriter temp = new PrintWriter(new OutputStreamWriter(new FileOutputStream(parent + "/output.pepa")),true);
		for(String str: modelDesp) {
			temp.println(str);
		}
		temp.close();
	}
}
