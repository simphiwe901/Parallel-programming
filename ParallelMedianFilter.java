/** ParallelMedianFilter
* Filters a series of numbers from a file into an arralist
* Output displayed to an outputfile
*
* @author Simphiwe Mchunu
*04 September 2017
*/




import java.util.concurrent.ForkJoinPool;
import  java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.*;
import java.io.*;
import java.util.Scanner;



public class ParallelMedianFilter extends RecursiveTask<ArrayList<Double>>{

      static final int SEQUENTIAL_THRESHOLD= 5;
      public static ArrayList<Double> InputArray = new ArrayList<Double>();
      public static ArrayList<Double> OutputArray = new ArrayList<Double>();
      int low;
      int high;
      int result = 0;
      String file;
      int filterSize;
    static long startTime = 0;

	   private static void tick(){
		   startTime = System.currentTimeMillis();
	   }
	   private static float toc(){
		   return (System.currentTimeMillis() - startTime) / 1000.0f;
	   }


      //Constructor
      public ParallelMedianFilter(ArrayList<Double> InputArray, int low, int high){
         this.InputArray = InputArray;
         this.low = low;
         this.high = high;

      }

     static final ForkJoinPool fjPool = new ForkJoinPool();
     private static ArrayList<Double> ForkPool(ArrayList<Double> arr){
	         return fjPool.commonPool().invoke(new ParallelMedianFilter(arr,0,arr.size()));
	}


      // Adds values from file to an arraylist
     public static ArrayList<Double> getInputArray(String file) throws FileNotFoundException{

         Scanner myInputFile = new Scanner(new FileInputStream(file));

         String line = "";
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

      protected ArrayList<Double> compute(){
         try{
         //runs the program in serial when
         // difference between boundaries is less then
         // specified SEQUENTIAL_THRESHOLD
         if((high - low)<=SEQUENTIAL_THRESHOLD){
            return getOutputArray(file, filterSize);
         }
         // Divide and Conquer
         // Use of Fork/Join Framework
         else{
            int median = this.low +(this.high - this.low)/2;
            ParallelMedianFilter left = new ParallelMedianFilter(InputArray, this.low, median);
            ParallelMedianFilter right = new ParallelMedianFilter(InputArray, median, this.high);

            left.fork();
            ArrayList<Double> rightA = right.compute();
            ArrayList<Double> leftA = left.join();
            OutputArray = new ArrayList<Double>(rightA);
            OutputArray.addAll(leftA);
            return OutputArray;
            }

             }
           catch(Exception e){e.printStackTrace();}
          return OutputArray;
        }

      public static void main(String[] args) throws InterruptedException{
      try{
      System.out.println("<data file name> <filter size (must be an odd integer >= 3)> <output file name>");

      Scanner input = new Scanner(System.in);
      String user_input = input.nextLine();
      String Inputfile = user_input.substring(0,user_input.indexOf(".")+4);

      String num = user_input.substring(user_input.indexOf(" ")+1, user_input.lastIndexOf(" "));
      int filterSize = Integer.parseInt(num);

      String Outputfile = user_input.substring(user_input.lastIndexOf(" ") +1);
      getInputArray(Inputfile);

      tick();
      ParallelMedianFilter a = new ParallelMedianFilter(InputArray,0,InputArray.size());
      float time = toc();
      System.out.println("Time taken to filter median is: "+time);

      getOutputFile(Outputfile);
       }

      catch(Exception e){

      e.printStackTrace();}
   }


      }
