import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class Random {

    public static List<Partition> possibleSolutionsMathRandom = new ArrayList<>();
    public static List<Partition> possibleSolutionsBBS = new ArrayList<>();

    public static PriorityQueue<Partition> randomSolutionsRankingMathRandom;
    public static PriorityQueue<Partition> randomSolutionsRankingBBS;

    private double totalFitnessMathRandom;
    private double totalFitnessBBSRandom;




    /*
    Function: generate random partitions with Math.random()

    N: number of solutions to make
    multisetSize: the length of each chromosome
    max: maximum number to generate
    min: minimum number to generate

     */
    public static void generateRandomSolutionsWithMathRandom(int n, int multisetSize, int max, int min, Partition inputMultiSet){

        //Implements a custom Comparator to rank the fitness level of each individual
        randomSolutionsRankingMathRandom = new PriorityQueue<>(n, new RandomSolutionComparator());



        //Iterate through the number of solutions we wish to make and randomly create them
        for(int i = 0; i < n; i++){

            ArrayList<Integer> possibleSolution = new ArrayList<>();

            //populate a random solution (chromosome)
            for(int j = 0; j < multisetSize + 1; j++){
                possibleSolution.add((int) (Math.random() * ((max - min) + 1)) + min);
            }

            //Create our random partition object to store it's information
            Partition randomSolution = new Partition(possibleSolution, multisetSize);

            //Add the random solution to our list of random solutions
            possibleSolutionsMathRandom.add(randomSolution);

        }

    }







    /*
        Function: generate random partitions with the BBS

        N: number of solutions to make
        multisetSize: the length of each chromosome
     */

    public static void generateRandomSolutionsWithBBS(int n, int multisetSize, Partition inputMultiSet){

        //Grab our list of prime numbers to be p and q
        ArrayList<Integer> listOfPrimeNumbers = Utilities.generateListOfRandomPrimes(n+ (n/2));


        //Implements a custom Comparator to keep track of the fitness level of each individual
        randomSolutionsRankingBBS = new PriorityQueue<>(n, new RandomSolutionComparator());


        int ptr = 2;

        for(int i = 0; i < n; i++ ){
            int p = listOfPrimeNumbers.get(ptr-2);
            int q = listOfPrimeNumbers.get(ptr-1);
            int M = p * q;
            int x = listOfPrimeNumbers.get(ptr++);
            int kRemainBits = Utilities.log2(M);
            int remainder;
            int randomNumberForSolution;

            ArrayList<Integer> possibleSolution = new ArrayList<>();


            for(int j = 0; j < multisetSize; j++){
                remainder = (x * x) % M; // simulates our X^2 % M equation
                randomNumberForSolution = remainder & ((1 << kRemainBits) - 1); // Grab the k remaining bits from M
                possibleSolution.add(randomNumberForSolution); // Add our constructed number to array
                x = remainder; // our remainder becomes our new x value
            }



            //Create our random partition object to store it's information
            Partition randomSolution = new Partition(possibleSolution, multisetSize);


            //add it to our collection of possible solutions
            possibleSolutionsBBS.add(randomSolution);


        }


    }


    // Helps to keep our random solutions from best fitness to worst fitness
    // the higher the fitness, the closer of a solution we have
    // -1 returns the earlier, 1 returns the latter, 0 maintains relative order
    static class RandomSolutionComparator implements Comparator<Partition> {

        @Override
        public int compare(Partition r1, Partition r2){
            if(r1.getFitness() > r2.getFitness())
                return -1;
            if(r1.getFitness() < r2.getFitness())
                return 1;
            return 0;
        }
    }



    public static void main(String[] args){
        int i = 0;
        String arg;
        int numOfRandomSolutionsToGenerate = 10000;

        String inputFile = "";
        String outputFile = "";

        while (i < args.length && args[i].startsWith("-")) {
            arg = args[i++];

            // use this type of check for arguments that require arguments
            if (arg.equals("-o")) {
                outputFile = args[i++];
            } else if (arg.equals("-i")) {
                inputFile = args[i++];
            }
        }

            try {
                //Gather the inputted numbers from the input.txt file
                ArrayList<Integer> multiset = Utilities.convertInputDataToArray(inputFile);
                int[] tempArr = new int[multiset.size()];

                //Setup our brute force solution
                BruteForce bruteForce = new BruteForce(multiset);

                //Track the execution time of the program
                Instant start = Instant.now();

                bruteForce.generateAllBinaryStrings(multiset.size(),tempArr, 0);

                //Instantiate our partition class to keep track of our partition data
                Partition inputPartition = new Partition(multiset, multiset.size());

                //Create our partitions and collect the valid 2-partitions as a bitstring
                bruteForce.evaluateMultiset();

                bruteForce.evaluateBruteForceFitness();

                //Create our random partitions with Math.Random
                generateRandomSolutionsWithMathRandom(numOfRandomSolutionsToGenerate, multiset.size(),100, 0, inputPartition);

                //Assess the fitness of all the math random solutions
                fitnessFunction(bruteForce.getBruteforceSolutionsAsPartitions(), possibleSolutionsMathRandom, "Math Random");


                //Create our random partitions with BBS
                generateRandomSolutionsWithBBS(numOfRandomSolutionsToGenerate, multiset.size(),inputPartition);

                //Assess the fitness of all the BBS random solutions
                fitnessFunction(bruteForce.getBruteforceSolutionsAsPartitions(), possibleSolutionsBBS, "BBS");


                PriorityQueue<Partition> bruteForceRanking = bruteForce.getBruteForceSolutionRanking();


                //Finish the execution of the full program
                Instant finish = Instant.now();

                //Write out the results to our file
                writeOutToRandomFile(start, finish, outputFile, bruteForce, bruteForceRanking);


                //We can only operate on a multiset that is even
                if(inputPartition.getSum() % 2 != 0){
                    System.out.println("Unable to create subsets since the sum is not even");
                    System.exit(0);
                }

            }catch(Exception e){
                System.out.println(e.getMessage());
            }

    }

    /*
    Function: Helper function to establish our necessary variables to evaluate our random solutions

    Solutions: The actual solutions for the program
    RandomSolutions: The Random Solutions generated by the MathRandom Class or BBSRandom Class
    RandomType: Decipher whether the incoming randomSolutions are from MathRandom or MathBBS

     */
    private static void fitnessFunction(List<Partition> solutions, List<Partition> randomSolutions, String randomType) {

        //Keep track of the sum we're looking for
        int solutionSum = solutions.get(0).getSum();

        //Iterate through all of the random solutions generated
        for(Partition randomPartition: randomSolutions){

            //Iterate through the bruteforce solutions against the current randomPartition
            for(Partition solutionSequence: solutions){
                ArrayList<String> chromosome = new ArrayList<>();

                int count = 0;

                //Check to see if the random solution contains any number from the brute force  solution
                for(int i = 0; i < solutionSequence.getMultiset().size(); i++){
                    int potentialNumber = solutionSequence.getMultiset().get(i);

                    if(randomPartition.getMultiset().get(i).equals(potentialNumber)) {
                        count++;
                        chromosome.add("1");
                    } else
                        chromosome.add("0");
                }

                if(randomPartition.getMultiset().size() != chromosome.size()){
                    for(int i = 0; i < randomPartition.getMultiset().size() - chromosome.size() + 1; i++)
                        chromosome.add("0");
                }


                //Partitions that don't contain a single number matched have a terrible fitness
                //The lower the fitness, the worse that solution is in the current generation
                    //Check to see how far away the sum is compared to the solution sum to factor in for
                    //the fitness function
                    int sumDiffOfAnsAndRandomSolution = Math.abs(solutionSum - randomPartition.getSum());

                    //Check how many #'s (in %) in the random partition were present from the overall set
                    double percentOfNumbersMatched = (double) count / randomPartition.getMultiset().size();

                    //We determine the fitness by the numbers the partition matched times
                    // the sum difference of the actual solution from the randomSolution
                    //If the sum difference is 0, that means we have an exact match for a solution
                    //We raise the diff to a power of 2 because some solutions may be very close to each other
                    //and we want to ensure our genetic algorithm grows exponentially instead of linearly
                    double fitnessScore = Math.pow(percentOfNumbersMatched * sumDiffOfAnsAndRandomSolution, 2);

                    //Evaluate the BBS random based on the number of 1s counted
                    //The reason is because the BBS works on large primes and the fitness value will be large
                    //Evaluate the MathRandom based on number of 1's and percentage away from the sum
                    if(!randomType.equals("Math Random")){
                        randomPartition.setFitness(Math.pow(percentOfNumbersMatched, 2));
                    } else if(randomPartition.getFitness() < fitnessScore) {
                        randomPartition.setFitness(fitnessScore);
                        randomPartition.setSolutionClosestToo(solutionSequence);
                        randomPartition.setChromosome(chromosome);
                    }

            }

            if(randomPartition.getChromosome().size() == 0) {
                ArrayList<String> chromosome = new ArrayList<>();
                for (int i = 0; i < randomPartition.getMultiset().size(); i++)
                    chromosome.add("0");
                randomPartition.setChromosome(chromosome);
            }

            //After evaluating a random solution against the bruteforce solutions
            //Place it's best fitness into the rankingArray

            //Keep track of the best fitness scores based off of the random type used
            if(randomType.equals("Math Random")) {
                randomSolutionsRankingMathRandom.add(randomPartition);
            }else {
                randomSolutionsRankingBBS.add(randomPartition);
            }


        }





    }

    private static void writeOutToRandomFile(Instant start, Instant finish, String outputFile,BruteForce bruteForce, PriorityQueue<Partition> bruteForceRanking) {

        try {

            FileWriter fileWriter = new FileWriter(outputFile);
            BufferedWriter bw = new BufferedWriter(fileWriter);

            bw.write("------------- RANDOM SOLUTION(S)----------------");
            bw.newLine();
            bw.newLine();


            bw.write("Brute Force Solution Fitness: ");
            bw.write(""+ bruteForceRanking.peek().getFitness());
            bw.newLine();
            bw.newLine();

            bw.write("Brute Force Solution Chromosome: ");
            Partition bruteForcePartition = bruteForceRanking.poll();
            bw.write("[ ");
            for(String bit : bruteForcePartition.getChromosome())
                bw.write(bit + " ");
            bw.write("]");
            bw.newLine();
            bw.newLine();
            bw.newLine();


            bw.write("-------------- Evaluating the Math Random Fitness ----------------------");
            bw.newLine();
            bw.newLine();

            bw.write("Creating random solutions is never the ideal way to go about implementing a Genetic Algorithm. Poor fitness is common \n" +
                    "amongst most random solutions generated since many of them may be far away from the mean. Therefore, altering the sequence \n" +
                    "through crossover and mutation techniques will take further time to generate a valid solution. Shown below is the result of using\n" +
                    "Math Random to generate 10,000 random solutions. The bitstring with the highest fitness is still far away from the ideal  \nfitness level" +
                    "we would like to have.");


            bw.newLine();
            bw.newLine();
            bw.write("Top Fitness For Math Random: ");
            bw.write(""+randomSolutionsRankingMathRandom.peek().getFitness());

            bw.newLine();
            bw.newLine();
            bw.write("Top Random Solution for Math Random: ");

            Partition mathRandomSolution = randomSolutionsRankingMathRandom.poll();

            bw.write("[ ");
            for(String bit : mathRandomSolution.getChromosome())
                bw.write(bit + " ");
            bw.write("]");
            bw.newLine();
            bw.newLine();

            bw.write("-------------- Evaluating the BBS Random Fitness ----------------------");
            bw.newLine();
            bw.newLine();

            bw.write("The BBS Random Fitness does an even worse job at creating random solutions compared to the Math.Random implementation. \n" +
                    "Random algorithms in a computer system are never really random, they're psuedo-random since an algorithm is \n" +
                    "implemented to generate a random sequence. The BBS approach may do better depending on the input that we're given but \n" +
                    "succinctly, it's not the ideal approach.");


            bw.newLine();
            bw.newLine();

            bw.write("Top Fitness For BBS Random: ");
            bw.write(""+randomSolutionsRankingBBS.peek().getFitness());
            bw.newLine();
            bw.newLine();
            bw.write("Top Random Solution for BBS Random: ");

            Partition randomBBS = randomSolutionsRankingBBS.poll();
            bw.write("[ ");
            for(String bit : randomBBS.getChromosome())
                bw.write(bit + " ");
            bw.write("]");
            bw.newLine();
            bw.newLine();

            long timeElapsed = Duration.between(start, finish).toMillis();

            bw.write("-------------------- Total Time Of Execution -------------------------");
            bw.newLine();
            bw.write(""+timeElapsed+"ms");
            bw.newLine();
            bw.newLine();

            bw.write("-------------- Explaining The Concept Behind My Maximum Fitness ----------------------");
            bw.newLine();
            bw.newLine();

            bw.write("The Brute Force solution receives the Double.MAX_VALUE since this is the highest fitness you could possibly attain. \n " +
                    "Being that it's the correct solution and bitstring, there is no other possible value more unique then the highest possible \n double value.");

            bw.newLine();
            bw.newLine();

            bw.write("------------------------------------- Brute Force Combinations -------------------------------------");
            bw.newLine();

            int i = 1;
            for(List<String> bitstring : bruteForce.getMultisetCombinationsAsBitstrings()){
                bw.write("Chromosome#" + i++ + ": " );
                for(String bit : bitstring)
                    bw.write(bit + " ");
                bw.write("");
                bw.newLine();
            }
            bw.newLine();



            bw.write("------------------------------------- Brute Force Solutions -------------------------------------");
            bw.newLine();
            for(Partition partition : bruteForce.getBruteforceSolutionsAsPartitions()) {
                bw.write(partition.getChromosome().toString());
                bw.newLine();
            }
            bw.newLine();

            bw.write("------------------------------------- Brute Force Rankings -------------------------------------------");
            bw.newLine();


            Iterator<Partition> it = bruteForce.getBruteForceSolutionRanking().iterator();
            while(it.hasNext()){
                bw.write(it.next().getChromosome().toString());
                bw.newLine();
            }

            bw.newLine();
            bw.newLine();




            bw.close();
        }catch(Exception e) {
            System.out.println("Error");
        }
    }


}
