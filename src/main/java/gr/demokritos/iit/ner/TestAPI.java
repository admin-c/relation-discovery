package gr.demokritos.iit.ner;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author grv
 */
public class TestAPI {
	public static void main(String[] args) {

		// String filename = "json_tests.txt";
		String filename = "example.json";
		try (Scanner in = new Scanner(new FileReader(filename))) {
			List <String> jsoni = new ArrayList();
			while(in.hasNextLine()){
				String json = in.nextLine();
				jsoni.add(json);
			}
			jsoni = API.NER(jsoni, false);
			for(String json: jsoni){
				System.out.println(json);
				// NamedEntityList nel = NamedEntityList.fromJSON(json);
				// nel.prettyPrint();
			}
		} catch (FileNotFoundException ex) {
		Logger.getLogger(TestAPI.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
}
