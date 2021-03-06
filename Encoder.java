/*
 * Program done by Saleh Alghusson and Ovais Panjwani
 * For CS f361
 */


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Iterator;


public class Encoder {
	/*	how big is the alphabet?	*/
	static private int ALPHABET_SIZE = 0;

	/*	how many lines?	*/
	static private int LINES = 0;
	
	/*	ASCII table list, must have 128 elements to work, will pad the first 97 and last n elements	*/
	static ArrayList <Integer> num_frq = new ArrayList<Integer>();
	static ArrayList <Integer> num_dbl_frq = new ArrayList<Integer>();
	
	
	/*	increments the counter	*/
	static void incrementAlphabet(int n){
		ALPHABET_SIZE += n;
	}
	/*	returns the counter	*/
	static int getAlphabet(){
		return ALPHABET_SIZE;
	}
	
	/*	returns the lines	*/
	static int getLines(){
		return LINES;
	}
	/*	increments the lines	*/
	static void incrementLines(int n){
		LINES += n;
	}
	
	/*	converts Integer objects to int primitive types	*/
	private static int[] convertIntegers(ArrayList<Integer> integers){
	    int[] ret = new int[integers.size()];
	    Iterator<Integer> iterator = integers.iterator();
	    for (int i = 0; i < ret.length; i++)
	    {
	        ret[i] = iterator.next().intValue();
	    }
	    return ret;
	}
	/*	returns the entropy, flag = 1 symbol, !flag = false	*/
	static private double bitsPerSymbol(String encExt, boolean flag) throws Exception{
		if(flag)
			return ( 1.0 * Reader.readEncoding(encExt).length() ) / Reader.readFile().length();
		else
			return ( 0.5 * Reader.readEncoding(encExt).length() ) / Reader.readFile().length();
	}
	
	static private double diffrence(double x, double y){
		return Math.abs(x-y)/ ( (x+y) / 2 ) * 100 ; 
	}
	/*	returns the entropy	*/
	static private double entropy(){
		double h = 0;
		
		for(int x : num_frq){
			double y = x;
			
			if(y <= 0)
				continue;
			
			h += ( y /ALPHABET_SIZE) * ( Math.log(y/ALPHABET_SIZE) / Math.log(2.0));
		}
		return -h;
	}
	private static void setUpArray(String arg) throws Exception{
		/* pad beginning with 97 zeros	*/
		for (int i = 0; i < 97; i++)
			num_frq.add(0);
		/* add n chars	*/
		Reader.readInput(arg, num_frq);
		int fill = 26 - num_frq.size() + 97;
		/* pad end with 5 zeros + any fills	*/
		for (int i = 0; i < 5+fill; i++)
			num_frq.add(0);
	}
	private static void setUpDoubleArray(String arg) throws Exception{
		/* pad beginning with 97 zeros	*/
		for (int i = 0; i < 97; i++)
			num_frq.add(0);
		/* add n chars	*/
		Reader.readDoubleInput(arg, num_frq);
		
	}
	private static double round (double d, int i){
		double out = d;
		BigDecimal bd = new BigDecimal(out);
		bd = bd.round(new MathContext(i));
		return bd.doubleValue();
	}
	private static void clear(){
		ALPHABET_SIZE = 0;
		LINES = 0;
		num_frq.clear();
	}
	/*	Main method	*/
	public static void main(String[] args) throws Exception{
		
	/* PART 1	*/
		System.out.println("PART 1");
		setUpArray(args[0]);
		System.out.println("The Entropy of this text is: " + round(entropy(), 3) +"\n");
		
	/* PART 2	*/
		/* create huffman tree from frequencies	*/
		System.out.println("PART 2");
		HuffmanTree ht = new HuffmanTree(convertIntegers(num_frq));
		ht.displayChars();
	
	/* PART 3	*/
		System.out.println("PART 3");
		int k = 10000;
		String encExt = ".enc1";
		String decExt = ".dec1";
		String output = Frequency.generateText(num_frq, ALPHABET_SIZE, k, encExt, decExt);
		Writer.writeFile(ht, output, encExt, decExt, true);
		System.out.println("Generated and written " + k + " random 1 symbol characters in testText, encoded them into testText" + encExt +" and decoded them in testText"+ decExt + "\n");
	
	/* PART 4	*/
		System.out.println("PART 4");
		System.out.println("bits per symbol for " + k + " characters: " + round(bitsPerSymbol(encExt, true), 3));
		double d1 = round(diffrence(bitsPerSymbol(encExt, true), entropy() ), 3);
		System.out.println("a " + d1 + "% difference from entropy\n");
		clear();
		
	/* PART 5	*/
		System.out.println("PART 5&6");
		setUpDoubleArray(args[0]);
		
		if(LINES >= 145){
			System.out.println(LINES + " sorry input is too large for a 2 symbol implemantion.");
			return;
		}
		encExt = ".enc2";
		decExt = ".dec2";
		output = Frequency.generateDoubleText(num_frq, ALPHABET_SIZE, k, encExt, decExt);
		ht = new HuffmanTree(convertIntegers(num_frq));
		
		Writer.writeEncoding(ht, output, encExt, false);
		Writer.writeDecoding(ht, encExt, decExt, false);
		System.out.println("Generated " + k + " random 2 symbol charecters, encoded them into testText" + encExt +" and decoded them in testText"+ decExt);
		System.out.println("bits per symbol for " + k + " characters: " + round(bitsPerSymbol(encExt, false), 3));
		double d2 = round(diffrence(bitsPerSymbol(encExt, false), entropy() ), 3);
		System.out.println("a " + d2 + "% difference from entropy\n");
		
	/* PART 7	*/
		System.out.println("PART 7");
		double d3 = round(d1-d2, 3);
		if (d3 > 0)
			System.out.println("a " + d3 + "% efficiency increase from the 1 symbol implementation");
		else
			System.out.println("a " + d3 + "% efficiency decrease from the 1 symbol implementation");
	
		
	}
}


/*
 * Writer is a class that is responsible of writing a file
 */
class Writer { 
	
	/*	/Users/almto3/Github/361assignment3/src/testText	*/
	static private final String filename = "testText";
	static private String filepath = "";
	
	private static void setPath()throws Exception{
		filepath = Reader.getPath().substring(0, Reader.getPath().lastIndexOf('/')+1) + filename;
	}
	static String getPath()throws Exception{
		return filepath;	
	}
	/*	write method will write to the file	*/
	static void writeFile (HuffmanTree ht, String content, String encExt, String decExt, boolean flag) throws Exception{
		setPath();	
		BufferedWriter writer = new BufferedWriter( new OutputStreamWriter( new FileOutputStream(filepath), "utf-8"));
		writer.write(content);
		writer.close();
		
		/*	encode the test and write it to testText.enc1	*/
		writeEncoding(ht, content, encExt, flag);
		
		/*	decode the encoded version and write it to testText.dec1	*/
		writeDecoding(ht, encExt, decExt, flag);
	}
	// flag = 1 symbol encoding,!flag = 2 symbol encoding 
	static void writeEncoding (HuffmanTree ht, String content, String encExt, boolean flag) throws Exception{
		
		if(flag)
			for (String line : ht.returnChars().split("\n"))
				content = content.replace(line.substring(0, 1), line.substring(3));
		else{
			for (String line : ht.returnChars().split("\n")){
				content = content.replace(line.substring(0, 1), line.substring(3));
			}
		}
		
		BufferedWriter writer = new BufferedWriter( new OutputStreamWriter( new FileOutputStream(filepath+encExt), "utf-8"));
		writer.write(content);
		writer.close();
	}
	static void writeDecoding (HuffmanTree ht, String encExt, String decExt, boolean flag) throws Exception{
		
		String encodedText = Reader.readEncoding(encExt);
		String decodedText = "";
		StringBuilder tmp = new StringBuilder();
		
		ArrayList<String> encodings = new ArrayList<String>();
		
		for(int i = 0; i<Encoder.getLines(); i++){
			encodings.add(ht.returnChars().split("\n")[i].substring(3));
		}
		if(flag)
			for (int i = 0; i < encodedText.length()-1; i++){
				tmp.append( encodedText.charAt(i) );
				for (int j = 0; j<encodings.size(); j++){
					if(encodings.get(j).equals(tmp.toString())){
						decodedText += ht.returnChars().split("\n")[j].substring(0,1);
						tmp.setLength(0);;
					}
				}
			}
		else{
			String[] tmpArray = ht.returnChars().split("\n");
			String[] charArray = new String[Encoder.getLines()];
			int c = 0;
			for (int i = 0; i< Math.sqrt(Encoder.getLines()); i++)
				for(int j = 0; j < Math.sqrt(Encoder.getLines()); j++){
					charArray[c] = tmpArray[i].substring(0,1) + tmpArray[j].substring(0,1);
					c++;
				}
			
			for (int i = 0; i < encodedText.length()-1; i++){
				tmp.append( encodedText.charAt(i) );
				for (int j = 0; j<encodings.size(); j++)
					if(encodings.get(j).equals(tmp.toString())){
						decodedText += charArray[j];
						tmp.setLength(0);;
					}
			}
			for (int i = 0; i< charArray.length; i++)
				System.out.println(charArray[i] + ": " + encodings.get(i));
		}
		BufferedWriter writer = new BufferedWriter( new OutputStreamWriter( new FileOutputStream(filepath+decExt), "utf-8"));
		writer.write(decodedText);
		writer.close();
	}
}

	
/*
 * Reader is a class that is responsible of reading a file
 */
class Reader { 
	
	/*	/Users/almto3/Github/361assignment3/src/testcoin	*/
	static private String filePath = "";
	static private File input = null;
	static private BufferedReader br = null;
	
	static String getPath(){
		return filePath;
	}
	private static void setPath(String path)throws Exception{
		filePath = path;
		input = new File(path);
		
		if(!input.exists() || input.isDirectory()) { 
			throw new FileNotFoundException("File not found at path: " + path);
		}
		
	}
	/*	will read and return the encoded file may return null	*/
	static String readEncoding(String encExt) throws Exception{
		
		String filePath = Writer.getPath() + encExt;
		File input = new File(filePath);
		
		StringBuilder encoded = new StringBuilder();

		try {
	    	String line;
		    br = new BufferedReader(new FileReader(input));
		    while ((line = br.readLine()) != null) {
		    	encoded.append(line);
		    }
	    } 
		catch (IOException e) {
			e.printStackTrace();
		} 
		finally {
			try {
				if (br != null)
					br.close();
		    } 
			catch (IOException ex) {
				ex.printStackTrace();
		    }
		}
		return encoded.toString();
	}
	/*	read method will read the input file	*/
	static void readInput (String path, ArrayList <Integer> num_frq) throws Exception{
		setPath(path);	
	  
	    try {
	    	String line;
		    br = new BufferedReader(new FileReader(input));
		    while ((line = br.readLine()) != null) {
		    	int n = Integer.parseInt(line);
		    	num_frq.add(n);
		    	if (n>0){
		    		Encoder.incrementAlphabet(n);
		    		Encoder.incrementLines(1);
		    	}
		    }
	    } 
		catch (IOException e) {
			e.printStackTrace();
		} 
		finally {
			try {
				if (br != null)
					br.close();
		    } 
			catch (IOException ex) {
				ex.printStackTrace();
		    }
		}
	}
	/*	read method will read the input file	*/
	static void readDoubleInput (String path, ArrayList <Integer> num_frq) throws Exception{
		setPath(path);	
	  
	    try {
	    	String tmp;
	    	StringBuilder text = new StringBuilder();
	    	
		    br = new BufferedReader(new FileReader(input));
		    br = new BufferedReader(new FileReader(input));
		    int counter = 0;
		    
		    while ((tmp = br.readLine()) != null) {
		    	text.append(tmp + "\n");
		    }
		    for (String line : text.toString().split("\n")){
			    int n = Integer.parseInt(line);
		    	if (n>0){
		    		counter++;
		    		for (String line2 : text.toString().split("\n")){
		    			int n2 = Integer.parseInt(line2);
		    			Encoder.incrementAlphabet(n*n2);
		    			num_frq.add(n*n2);
		    		}
		    	}
		    }
		    Encoder.incrementLines(counter*counter);
	    } 
		catch (IOException e) {
			e.printStackTrace();
		} 
		finally {
			try {
				if (br != null)
					br.close();
		    } 
			catch (IOException ex) {
				ex.printStackTrace();
		    }
		}
	}
	/*	will read the testText file written by Writer, cannot be called before readInput(String, ArrayList <Integer>) 
	 * returns a string	
	 */
	static String readFile() throws Exception{
		String filePath = Writer.getPath();
		File input = new File(filePath);
		
		StringBuilder text = new StringBuilder();
		try {
	    	String line;
		    br = new BufferedReader(new FileReader(input));
		    while ((line = br.readLine()) != null) {
		    	text.append(line);
		    }
	    } 
		catch (IOException e) {
			e.printStackTrace();
		} 
		finally {
			try {
				if (br != null)
					br.close();
		    } 
			catch (IOException ex) {
				ex.printStackTrace();
		    }
		}
		return text.toString();
	}
}