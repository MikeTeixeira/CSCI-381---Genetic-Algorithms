import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.Random;

public class GeneticAlgorithm {


    //Contains all the brute force partitions
    private List<Partition> multisetCombinationsAsPartitions = new ArrayList<>();

    Partition inputMultiset;

    //Keeps track of the generations
    Stack<Population> generations;
    private int generationCount;

    //Average solution of current generation
    private double averageFitnessLevel;
    private double crossoverRate = .80;
    private double mutationRate = 0.030;
    private static final boolean elitism = true;

    //Best Solution
    private Partition solutionPartition = new Partition();

    //Stores the tournament size. Changes as we go through each tournament
    private int tournamentSize;
    private boolean hasFoundSolution;


    public GeneticAlgorithm(Partition inputMultiset){
        this.inputMultiset = inputMultiset;
        this.tournamentSize = 100;
        generations = new Stack<>();
        generateAllBinaryStrings(inputMultiset.getSize(), new int[inputMultiset.getSize()], 0); //generate all solutions
        evaluateBestSolutionInMultiset(); //grab best solution from input
    }



    /*
        Function: Randomly generates a population to begin our GA
     */

    private Population initializePopulation(int size, int chromosomeSize){

        Population population = new Population(size, false);
        List<Partition> individuals = new ArrayList<>();

        //Create a random set of chromosomes to begin our initial population
        //Even valued numbers get 1, odd numbers get 0
        for(int i = 0; i < size; i++){
            ArrayList<String> chromosome = new ArrayList<>();
            for(int j = 0; j < chromosomeSize; j++){
                int num = new Random().nextInt((1000 - 10) + 1) + 10;
                chromosome.add(num % 2 == 0 ? "0": "1");
            }

            //Add our chromosome to the random generated population
            Partition randomGeneratedPopulation = new Partition(chromosome);
            individuals.add(randomGeneratedPopulation);

        }

        population.setIndividuals(individuals);

        return population;

    }

    /*
        Function: Uses the best solution of our input set as the benchmark. Goes through our population and assesses the fitness levels
        of each solution. The average fitness level is then stored


     */
    private void evaluateFitness(Population population){

        double averageFitness = 0.0;
//        Iterate through our population
        for (Partition partition : population.getIndividuals()) {

//            Grab our random candidate
            double count = 0.0;


            //Compare the solution to the actual solution bit by bit
            //count the # of 0's and 1's that match
            for (int j = 0; j < solutionPartition.getChromosome().size(); j++)
                if (partition.getChromosome().get(j).equals(solutionPartition.getChromosome().get(j)))
                    count++;



            //set the fitness level of the initial population member
            partition.setFitness(count);

            //keep track of the avg fitness per member
            averageFitness += count;
        }

        double avg = averageFitness / population.getIndividuals().size();

        //Set the average fitness for the population
        population.setAverageFitness(avg);

    }




        public static void main(String[] args){

        //Read in our multiset from the input file
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
            }
        }

        try {
            //Gather the inputted numbers from the input.txt file
            ArrayList<Integer> multiset = Utilities.convertInputDataToArray(inputFile);

            //Instantiate our partition class to keep track of our partition data
            Partition inputPartition = new Partition(multiset, multiset.size());

            Instant start = Instant.now();


           // ------------------------------- GENETIC ALGORITHM BEGINS ----------------------------------------------


            GeneticAlgorithm ga = new GeneticAlgorithm(inputPartition);
            Population population = ga.initializePopulation(100, inputPartition.getSize());
            ga.evaluateFitness(population);
            Population origPop = population;
            int generationCount = 1;


            while(population.getFittest().getFitness() < ga.solutionPartition.getFitness()){
                System.out.println("Generation Number: " + generationCount);
                population = evolvePopulation(population, ga.tournamentSize, ga.crossoverRate, ga.mutationRate);
                ga.evaluateFitness(population);
                generationCount++;
            }


            Instant finish = Instant.now();

            GeneticAlgorithm.writeOutToFile(start, finish, outputFile, population, ga, generationCount, origPop);

        }catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }



    public static Population evolvePopulation(Population population, int tournamentSize, double crossoverRate, double mutationRate){
        Population newPopulation = new Population(population.getIndividuals().size(), false);

        ArrayList<Partition> newIndividuals = new ArrayList<>();

        for(int i = 0; i < population.getIndividuals().size(); i++){
            Partition p1 = tournamentSelection(population, tournamentSize);
            Partition p2 = tournamentSelection(population, tournamentSize);
            Partition offSpring = crossOver(p1, p2, crossoverRate);
            newIndividuals.add(offSpring);
        }

        for(int i = 0; i < population.getIndividuals().size(); i++)
            mutate(newIndividuals.get(i), mutationRate);

        newPopulation.setIndividuals(newIndividuals);

        return newPopulation;
    }

    public static Partition tournamentSelection(Population population, int tournamentSize){
        Population tournament = new Population(tournamentSize, false);
        for (int i = 0; i < tournamentSize; i++) {
            if(population.getIndividuals().get(i).getFitness() >= population.averageFitness){
                int randomId = (int) (Math.random() * population.getIndividuals().size());
                tournament.getIndividuals().add(population.getIndividual(randomId));
            }
        }
        return tournament.getFittest();
    }

    private static Partition crossOver(Partition indiv1, Partition indiv2, double crossoverRate) {
        Partition newSol = indiv1;
        for (int i = 0; i < indiv1.getChromosome().size(); i++) {
            if (Math.random() <= crossoverRate) {
                newSol.getChromosome().set(i, indiv1.getChromosome().get(i));
            } else {
                newSol.getChromosome().set(i, indiv2.getChromosome().get(i));
            }

        }
        return newSol;
    }

    private static void mutate(Partition indiv, double mutationRate) {
        for (int i = 0; i < indiv.getChromosome().size(); i++) {
            if (Math.random() <= mutationRate) {
                String gene = indiv.getChromosome().get(i);
                if(gene.equals("1"))
                    indiv.getChromosome().set(i, "0");
                else
                    indiv.getChromosome().set(i, "1");
            }
        }
    }







        // POPULATION CLASS

    public static class Population {

        private List<Partition> individuals;
        private double averageFitness = 0.0;

        public Population(int size, boolean createNew) {
            individuals = new ArrayList<>();
            if (createNew) {
                createNewPopulation(size);
            }
        }

        protected Partition getIndividual(int index) {
            return individuals.get(index);
        }

        protected Partition getFittest() {
            Partition fittest = individuals.get(0);
            for (int i = 0; i < individuals.size(); i++) {
                if (fittest.getFitness() <= getIndividual(i).getFitness()) {
                    fittest = getIndividual(i);
                }
            }
            return fittest;
        }

        private void createNewPopulation(int size) {
            for (int i = 0; i < size; i++) {
                Partition newIndividual = new Partition();
                individuals.add(i, newIndividual);
            }
        }

        public List<Partition> getIndividuals() {
            return individuals;
        }

        public void setIndividuals(List<Partition> list){
            this.individuals = list;
        }


        public double getAverageFitness(){
            return this.averageFitness;
        }
        public void setAverageFitness(double fit){
            this.averageFitness = fit;
        }
    }












































        private static void writeOutToFile(Instant start, Instant finish, String outputFile, Population population, GeneticAlgorithm ga, int genNumber, Population origPop) {

            Partition solutionMultiset = ga.solutionPartition;
            List<Partition> individuals = population.getIndividuals();

            try {
                FileWriter fileWriter = new FileWriter(outputFile);
                BufferedWriter bw = new BufferedWriter(fileWriter);

                bw.write("------------- GENETIC ALGORITHM SOLUTION ----------------");
                bw.newLine();
                bw.newLine();

                bw.write("Solution to target: ");
                bw.write(solutionMultiset.toString());
                bw.newLine();
                bw.newLine();

                bw.write("Average Fitness Level of Generated Population: ");
                bw.write(String.valueOf(origPop.averageFitness));
                bw.newLine();
                bw.newLine();
                bw.write("Number Of Generations: " + genNumber);
                bw.newLine();
                bw.newLine();

                bw.write("Member with Solution: " + population.getFittest());
                bw.newLine();
                bw.newLine();

                bw.write("Execution Time: " + (finish.toEpochMilli() - start.toEpochMilli()) + "ms");

                bw.newLine();
                bw.newLine();
                bw.write("Members of Population: ");
                bw.newLine();
                int i = 1;
                for(Partition partition : origPop.getIndividuals()){
                    bw.write("Member " + i++ + ": " + partition + " Fitness Level: " + partition.getFitness());
                    bw.newLine();
                }



                bw.close();
            } catch(Exception e){
                System.out.println(e.getMessage());
            }
        }




        /*
        Function: Helper method for generate subsets

        Return: A list containing a nested list of integers
        */
        public void generateAllBinaryStrings(int n, int[] arr, int i)
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

        }


        //Evaluates all the input combinations and adds the best solution
        public void evaluateBestSolutionInMultiset(){

            //Grab the input partition
            ArrayList<Integer> multisetArr = inputMultiset.getMultiset();

            //Go through each brute force combination and find the best one
            for(int j = 0; j < multisetCombinationsAsPartitions.size(); j++) {
                Partition partition = multisetCombinationsAsPartitions.get(j);

                //Keep track of the sum of each partition of the brute force combination
                int sum1 = 0;
                int sum2 = 0;


                //If the incoming brute force combination has a 1 at pos i, add it to list 1
                // else to list 2
                for (int i = 0; i < partition.getChromosome().size(); i++) {
                    if (partition.getChromosome().get(i).equals("1"))
                        sum1 += multisetArr.get(i);
                    else
                        sum2 += multisetArr.get(i);
                }

                //If they have the same sum we have found our solution
                if(sum1 == sum2 && sum1 != 0){
                    solutionPartition = partition;
                    solutionPartition.setFitness(solutionPartition.getSize());
                    break;
                }
            }

        }

    }


