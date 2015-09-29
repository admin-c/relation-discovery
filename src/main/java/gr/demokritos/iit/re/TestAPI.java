package gr.demokritos.iit.re;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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
		// String filename = "reveal.json";
		// String filename = "example.json";
		String filename = "default+ner.json";
		/* 
		// Test case where json is a single line of array
		String filename = "defaultarray+ner.json";
		try{
			String json_array = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
			String json = demokritos.iit.api.API.RE(json_array, false);
			System.out.println(json);
		}
		catch(IOException e){}
		*/
		// Test case where an ArrayList of json strings is passed
		try (Scanner in = new Scanner(new FileReader(filename))) {
			List <String> jsoni = new ArrayList();
			while(in.hasNextLine()){
				String json = in.nextLine();
				jsoni.add(json);
			}
			String json = gr.demokritos.iit.api.API.RE(jsoni, false);
			System.out.println(json);
			//RelationList rel = RelationList.fromJSON(json);
			//rel.prettyPrint();
		}
		catch(FileNotFoundException ex){
			Logger.getLogger(TestAPI.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
