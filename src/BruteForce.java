import java.io.BufferedWriter;
import java.io.FileWriter;
import java.lang.reflect.Array;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class BruteForce {
    private Partition inputMultisetPartition;


    //Contains the brute force bitstrings as bits
    private Set<List<String>> multisetCombinationsAsBitstrings = new HashSet<>();

    //contains the bruteforce partition list
    private List<Partition> multisetCombinationsAsPartitions = new ArrayList<>();

    //Contains best solutions in order
    private PriorityQueue<Partition> bruteForceSolutionRanking = new PriorityQueue<>(new SolutionComparator());

    //Contains all the brute force solutions
    private List<Partition> bruteforceSolutionsAsPartitions = new ArrayList<>();







    public BruteForce(ArrayList<Integer> multiset){
        inputMultisetPartition = new Partition(multiset, multiset.size());

        //Create all the brute force combinations
        generateAllBinaryStrings(multiset.size(), new int[multiset.size()], 0);
    }




    /*
    Function: Helper method for generate subsets

    Return: A list containing a nested list of integers
    */
    public void generateAllBinaryStrings(int n,int arr[], int i)
    {
        if (i == n)
        {
            addChromosomeToSolutions(arr, n);
            return;
        }

        //Begin by permuting with 0s
        arr[i] = 0;
        generateAllBinaryStrings(n, arr, i + 1);

        //Later permuting with 1s
        arr[i] = 1;
        generateAllBinaryStrings(n, arr, i + 1);
    }




    public void addChromosomeToSolutions(int[] arr, int n) {
        ArrayList<String> bitString = new ArrayList<>();
        ArrayList<Integer> nums = new ArrayList<>();
        for(int i = 0; i < n; i++){
            bitString.add(arr[i]+"");
            if(arr[i] == 1)
                nums.add(arr[i]);
        }
        Partition partition = new Partition(nums, n);
        partition.setChromosome(bitString);

        multisetCombinationsAsPartitions.add(partition);
        multisetCombinationsAsBitstrings.add(bitString);
    }





    //Evaluates all the brute force combinations and adds only the answers
    public void evaluateMultiset(){

        //Grab the input partition
        ArrayList<Integer> inputMultiset = inputMultisetPartition.getMultiset();

        //GO through each brute force combination
        for(int j = 0; j < multisetCombinationsAsPartitions.size(); j++) {
            Partition bruteForceCombination = multisetCombinationsAsPartitions.get(j);

            //Keep track of the sum of each partition of the brute force combination
            int sum1 = 0;
            int sum2 = 0;


            //If the incoming brute force combination has a 1 at pos i, add it to list 1
            // else to list 2
            for (int i = 0; i < bruteForceCombination.getChromosome().size(); i++) {
                if (bruteForceCombination.getChromosome().get(i).equals("1"))
                    sum1 += inputMultiset.get(i);
                else
                    sum2 += inputMultiset.get(i);
            }

            //If they have the same sum we add to our solutions
            if(sum1 == sum2 && sum1 != 0){
                bruteforceSolutionsAsPartitions.add(bruteForceCombination);
            }
        }

    }


    public void evaluateBruteForceFitness(){

        //Iterate through all the combinations
        for(int i = 0; i < multisetCombinationsAsPartitions.size(); i++){

            Partition partitionCombination = multisetCombinationsAsPartitions.get(i);



            //Iterate through all the solutions
            for(int j = 0; j < bruteforceSolutionsAsPartitions.size(); j++){

                int count = 0;

                Partition solutionCombination = bruteforceSolutionsAsPartitions.get(j);

                //Iterate through all the bits and count the number of 1's
                for(int k = 0; k < partitionCombination.getChromosome().size(); k++)
                    if(partitionCombination.getChromosome().get(k).equals(solutionCombination.getChromosome().get(k)))
                        count++;

                //If the count is 0, we give it the worst fitness since no numbers are in the partition
                    //Fitness Part 1: Ratio between # of 1's counted and length
                    double onesRatio = (double) count / partitionCombination.getChromosome().size();



                    //Fitness Part 2: Difference in sum between the combination and solution
                    int sumDifference = Math.abs(partitionCombination.getSum() - solutionCombination.getSum());


                    double fitnessLevel = onesRatio * sumDifference;

                    //Check to make sure the new fitness level is greater than the previous

                    if(count == partitionCombination.getChromosome().size()) {
                        partitionCombination.setFitness(Double.MAX_VALUE);
                        partitionCombination.setExactMatch(true);
                    }

                    if(partitionCombination.getFitness() < fitnessLevel && !partitionCombination.isExactMatch()){
                        partitionCombination.setFitness(fitnessLevel);
                    }
            }

            //After evaluating against all the solutions, add it to our ranking system
            //to evaluate against all the other partitions
            bruteForceSolutionRanking.add(partitionCombination);

        }
    }

    // Helps to keep our random solutions from best fitness to worst fitness
    // the higher the fitness, the closer of a solution we have
    // -1 returns the earlier, 1 returns the latter, 0 maintains relative order
    static class SolutionComparator implements Comparator<Partition> {

        @Override
        public int compare(Partition r1, Partition r2){
            if(r1.getFitness() > r2.getFitness())
                return -1;
            else if(r1.getFitness() < r2.getFitness())
                return 1;
            return 0;
        }
    }















    void writeToOutputFile(String outputFile, BruteForce bruteForce){
        try {

            FileWriter fw = new FileWriter(outputFile);
            BufferedWriter bw = new BufferedWriter(fw);

            bw.write("Inputted Multiset: ");
            bw.write(bruteForce.inputMultisetPartition.getMultiset().toString());
            bw.newLine();
            bw.newLine();

            bw.write("--------------- Brute Force Combinations ---------------");
            bw.newLine();

            int i = 1;
            for(List<String> bitstring : bruteForce.multisetCombinationsAsBitstrings){
                bw.write("Chromosome#" + i++ + ": " );
                for(String bit : bitstring)
                    bw.write(bit + " ");
                bw.write("");
                bw.newLine();
            }
            bw.newLine();



            bw.write("--------------- Brute Force Solutions ---------------");
            bw.newLine();
            for(Partition partition : bruteforceSolutionsAsPartitions) {
                bw.write(partition.getChromosome().toString());
                bw.newLine();
            }
            bw.newLine();

            bw.write("--------------- Brute Force Rankings ---------------");
            bw.newLine();


            Iterator<Partition> it = bruteForceSolutionRanking.iterator();
            while(it.hasNext()){
                bw.write(it.next().getChromosome().toString());
                bw.newLine();
            }

            bw.newLine();
            bw.newLine();
            bw.write(String.valueOf(bruteForceSolutionRanking.poll().getFitness()));




            bw.close();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

    }

    public static void main(String[] args){

        int i = 0;
        String arg;

        String inputFile = "";
        String outputFile = "";

        while (i < args.length && args[i].startsWith("-")) {
            arg = args[i++];

            // use this type of check for arguments that require arguments
            if (arg.equals("-o")) {
                    outputFile = args[i++];
            } else if (arg.equals("-i")) {
                    inputFile = args[i++];
            } else {
                System.out.println("Flag provided does not exist. Only -i and -o work for this program.");
            }

        }

        try {

            //Track the execution time of the program
            Instant start = Instant.now();

            //Gather the inputted numbers from the input.txt file
            ArrayList<Integer> inputMultiset = Utilities.convertInputDataToArray(inputFile);

            //Create our Brute Force setup
            BruteForce bruteForce = new BruteForce(inputMultiset);


            //Evaluate all of our combinations to create our brute force solutions
            bruteForce.evaluateMultiset();

            //Evaluate the fitness of each brute force combination
            bruteForce.evaluateBruteForceFitness();

            //Output the results
            bruteForce.writeToOutputFile(outputFile, bruteForce);


            //Finish the execution of the full program
            Instant finish = Instant.now();


        } catch(Exception e){
            System.out.println(e.getMessage());
        }


    }


    public PriorityQueue<Partition> getBruteForceSolutionRanking() {
        return bruteForceSolutionRanking;
    }

    public void setBruteForceSolutionRanking(PriorityQueue<Partition> bruteForceSolutionRanking) {
        this.bruteForceSolutionRanking = bruteForceSolutionRanking;
    }

    public Partition getInputMultisetPartition() {
        return inputMultisetPartition;
    }

    public void setInputMultisetPartition(Partition inputMultisetPartition) {
        this.inputMultisetPartition = inputMultisetPartition;
    }

    public Set<List<String>> getMultisetCombinationsAsBitstrings() {
        return multisetCombinationsAsBitstrings;
    }

    public void setMultisetCombinationsAsBitstrings(Set<List<String>> multisetCombinationsAsBitstrings) {
        this.multisetCombinationsAsBitstrings = multisetCombinationsAsBitstrings;
    }

    public List<Partition> getMultisetCombinationsAsPartitions() {
        return multisetCombinationsAsPartitions;
    }

    public void setMultisetCombinationsAsPartitions(List<Partition> multisetCombinationsAsPartitions) {
        this.multisetCombinationsAsPartitions = multisetCombinationsAsPartitions;
    }

    public List<Partition> getBruteforceSolutionsAsPartitions() {
        return bruteforceSolutionsAsPartitions;
    }

    public void setBruteforceSolutionsAsPartitions(List<Partition> bruteforceSolutionsAsPartitions) {
        this.bruteforceSolutionsAsPartitions = bruteforceSolutionsAsPartitions;
    }
}
