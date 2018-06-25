/**SerialMedianFilter
* Filters a series of numbers from a file into an arralist
* Output displayed to an outputfile
*
* @author Simphiwe Mchunu
*04 September 2017
*/


import java.util.*;
import java.io.*;
import static java.lang.Math.min;
import java.lang.*;

public class SerialMedianFilter{

   static long StartTime =0;
   protected static final ArrayList<Double> InputArray = new ArrayList<Double>();
   protected static  ArrayList<Double> OutputArray = new ArrayList<Double>();
   //public float time = 0;


   private static void tick(){
		StartTime = System.currentTimeMillis();
	}

   private static float toc(){
		return (System.currentTimeMillis() - StartTime) / 1000.0f;
	   }

      // Adds values from file to an arraylist
   public static ArrayList<Double> getInputArray(String file) throws FileNotFoundException{

         Scanner myInputFile = new Scanner(new FileInputStream(file));

         String line = "";
         int line_counter=0;
         myInputFile.nextLine();
         while(myInputFile.hasNextLine()){
            line = myInputFile.nextLine();
            String value = line.substring(line.indexOf(" "));
            Double Current_value = Double.parseDouble(value);
            InputArray.add(Current_value);
            }
       return InputArray;


         }
     //Returns Output ArrayList, filters values from a file into this array
   public static ArrayList<Double> getOutputArray (String file, int filterSize)throws FileNotFoundException{
          int pos = 0;
          int myFilter = filterSize;
          final int z = InputArray.size();
          getInputArray(file);
      for(int i=0; i<InputArray.size();i++){
            List<Double> SubList = InputArray.subList(pos,Math.min(InputArray.size(),myFilter));
            if((myFilter - pos)==filterSize && (SubList.size()==filterSize  )){
              Collections.sort(SubList);
              int median = SubList.size()/2;

              OutputArray.add(SubList.get(median));
              SubList.clear();

              pos++;
              myFilter++;
              InputArray.clear();
              getInputArray(file);




            }
          }
          OutputArray.add(OutputArray.size(), InputArray.get(InputArray.size()-1));
          OutputArray.add(0, InputArray.get(0));
           return OutputArray;
   }

   // prints results to an output file
   public static void getOutputFile(String Outputfile) throws IOException{
      BufferedWriter writer = null;
      File file = new File(Outputfile);

      writer = new BufferedWriter(new FileWriter(file, true));

      for(int k=0;k<OutputArray.size();k++){
         writer.write(k+" "+OutputArray.get(k));

      }
     writer.close();

   }



   // Takes user input:- Inputfile,filterSize and Outputfile
   public static void main(String[] args) throws FileNotFoundException{
      System.out.println("<data file name> <filter size (must be an odd integer >= 3)> <output file name>");
      try{
      Scanner input = new Scanner(System.in);
      String user_input = input.nextLine();
      String Inputfile = user_input.substring(0,user_input.indexOf(".")+4);

      String num = user_input.substring(user_input.indexOf(" ")+1, user_input.lastIndexOf(" "));
      int filterSize = Integer.parseInt(num);

      String Outputfile = user_input.substring(user_input.lastIndexOf(" ") +1);

      tick();
      getOutputArray(Inputfile,filterSize);
      float time = toc();
      System.out.println("Time taken to filter median is: "+time+" Seconds");

      getOutputFile(Outputfile);
      }

      catch(Exception e){

      e.printStackTrace();
}
   }


}
