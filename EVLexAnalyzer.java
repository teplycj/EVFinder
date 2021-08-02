import java.io.File;
import java.util.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileWriter;
import java.io.PrintWriter;

public class EVLexAnalyzer{

   private Scanner input; //.txt file that is being read
   private Scanner currentLine; //current line of .txt file
   private String currentToken; //current word
   private String line; //current line
   private int linenum = 0; //line number for reference
   private int NROW = 0; //the placeholder of the valence band electron
   private int NCOL = 0; //the placeholder of the conduction band electron
   private double[][] dataMatt; //where all of our data will be stored
   
   void scanInput(Scanner inputFile) throws NoSuchElementException{
   
      input = inputFile; //make local copy of this instance in this class
      currentToken = null;
      line = input.nextLine(); //start scanning the first line, basically tells the program to get to work
      linenum = 1; //create the line indices to keep track of where you are in the file. you will have to tell it to manually update, as shown below in the FOR loop
      for(int i=0; i<4; i++){
        line=input.nextLine(); //go to the next line
        linenum++; //update the line index (any number with a ++ after it tells it to increase by one, so this is line = line+1)
      }
      
      currentToken= input.next(); //start looking at the 6th line of the EIGENVALUE text file
      currentToken = input.next(); //number of KPOINTS
      NCOL = Integer.parseInt(currentToken);
      currentToken = input.next();
      NROW = Integer.parseInt(currentToken); // number of electron bands being examined per KPOINT
      
      System.out.println(NROW+" "+NCOL);
      dataMatt = new double[NROW][NCOL]; //where all of the values will be stored when going through your text file
      
      line=input.nextLine();
      linenum++;
      line=input.nextLine();
      linenum++;
      line=input.nextLine();
      linenum++;
      
      scanNext();//starts scanning, which is taken care of in the function below
   
   }

   private void scanNext(){
   
    int currRow = 0;
    int currCol = -1;
   
    while(input.hasNextLine()){

        if(currCol>NCOL){ //this is used to ensure that the file finishes reading
            break;//this gets us out of the while loop so we don't stay here for an eternity (your computer hates you when you keep it in a while loop, so be nice)
            
        }else if(line.contains("E") && currCol<NCOL){ //this initializes the looking for band gap energies, where it checks the boolean function below to make sure we are at a new k-point
        
            currCol++; //move over a column, aka the next KPOINT
            currRow=0; //start in the first row
            line=input.nextLine(); //go to the next line
            linenum++;
            
        }else{ //this section is for us to start looking at band gap energies after we know we are in that realm from setting the startSearch flag to TRUE
            currentLine = new Scanner(line);
            if(currentLine.hasNext()){
                currentToken=currentLine.next(); //start looking at the individual "tokens" in the current line
                currentToken=currentLine.next(); //go to the second token, which is our BG energy
                double bgNRG = Double.parseDouble(currentToken); //convert it to a number (because java initially reads it as a String)
                dataMatt[currRow][currCol]=bgNRG; //store it in our data matrix that we will export once the file is done reading
                currRow++; //index the next row
            }
            line=input.nextLine(); // go to the next line
            linenum++; //update the line index
        }  
     }
     
     currentLine = new Scanner(line);
    if(currentLine.hasNext()){
        currentToken=currentLine.next(); //start looking at the individual "tokens" in the current line
        currentToken=currentLine.next(); //go to the second token, which is our BG energy
        double bgNRG = Double.parseDouble(currentToken); //convert it to a number (because java initially reads it as a String)
        dataMatt[currRow][currCol]=bgNRG; //store it in our data matrix that we will export once the file is done reading
        currRow++; //index the next row
    }
     
     printEnergies();
     
   }

	//prints all of the energies that we stored while parsing through the text file
   private void printEnergies(){
   
        FileWriter fileWriter;
        PrintWriter printWriter;
        printWriter=null;
        
        try{
          File outputFile = new File("EV_OUTPUT.txt");
          fileWriter = new FileWriter(outputFile);
          printWriter = new PrintWriter(fileWriter);
        }catch (Exception e){
         System.out.println(e);
        }
   
        for(int r=0; r<NROW; r++){
            for(int c=0; c<NCOL; c++){
                printWriter.print(dataMatt[r][c]+" \t");    
            }
            printWriter.println();
        }
        
        printWriter.close();
   }

}
