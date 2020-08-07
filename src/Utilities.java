import java.io.*;
import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class Utilities {

    public Utilities(){}

    /*
    Function: Returns the sum of an integer arr

    Return: int
     */
    public static int calculateSum(List<Integer> arr){
        int sum = 0;
        for(int num : arr)
            sum += num;
        return sum;
    }


    //Convert our number to log2
    public static int log2(int num){
        return (int) (Math.log(num) / Math.log(2) + 1e-10);
    }



    /*
    Function: Reads from the input file and converts the Characters to numbers

    Return: multiset array
    */
    public static ArrayList<Integer> convertInputDataToArray(String inputFile) throws IOException {


        try {

            //Grab the location of the file from the src director
            File file = new File(inputFile);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));


            //Read in the data from the input file
            String st;
            StringBuilder stringBuilder = new StringBuilder();

            //Read in all the data
            while ((st = bufferedReader.readLine()) != null) {
                stringBuilder.append(st.trim());
            }

            //Convert the String integers to type Integer and return as an ArrayList<Integer>
            return characterDataToNumber(stringBuilder);


        } catch(Exception e){
            System.out.println("Error: An error has occurred while reading the file.");
            System.out.println(e.getMessage());
        }

        //Error occurred while converting, return an empty list
        return new ArrayList<>();
    }



    /*
    Function: Converts the inputted data to a series of numbers

    Return: multiset array
    */
    private static ArrayList<Integer> characterDataToNumber(StringBuilder sb){

        //Holds all the numbers inputted through the input file
        ArrayList<Integer> list = new ArrayList<>();

        //iterate through all the characters and store only the numbers
        for (int i = 0; i < sb.length(); ) {
            StringBuilder temp = new StringBuilder();

            //Make sure the character value is a number
            while (Character.isDigit(sb.charAt(i)))
                temp.append(sb.charAt(i++));

            //Error check against an empty string
            if (!temp.toString().isEmpty()) {
                try {
                    Integer num = Integer.parseInt(temp.toString());
                    list.add(num);
                } catch (Exception e){
                    System.out.println("Error: an error occurred while converting the Integer");
                }
            }
            i++;
        }

        return list;

    }


    public static ArrayList<Integer> generateListOfRandomPrimes(int maxNumbers){
        boolean[] notPrime = new boolean[maxNumbers];

        for(int i = 2; i < maxNumbers; i++){
            if(!notPrime[i]){
                for(int j = 2; j * j < maxNumbers; j++){
                    notPrime[j] = true;
                }
            }
        }

        ArrayList<Integer> primeNumbers = new ArrayList<>();

        //Generate only the large prime numbers
        for(int i = 50; i < notPrime.length; i++){
            if(!notPrime[i])
                primeNumbers.add((i));
        }

        return primeNumbers;
    }

}
