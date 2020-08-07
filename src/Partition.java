import java.util.*;

public class Partition {
    private static ArrayList<Integer> inputMultiset = new ArrayList<>();
    private ArrayList<Integer> multiset;
    private ArrayList<String> chromosome = new ArrayList<>();
    private Partition solutionClosestToo;


    private int size;
    private int sum;
    private double fitness;
    private int numOfOnes;
    private static double fitnessAvg;
    boolean exactMatch = false;

    public Partition(){}

    public Partition(ArrayList<String> chromosome){
        this.chromosome = chromosome;
        this.size = chromosome.size();
        this.fitness = 0;
    }

    // Initialize our Partition values
    public Partition(ArrayList<Integer> multiset, int size){
        this.multiset = multiset;
        this.fitness = Double.MIN_VALUE;
        this.size = size;
        this.sum = Utilities.calculateSum(multiset);
        this.numOfOnes = 0;
        chromosome = new ArrayList<>();
    }





    public Partition getSolutionClosestToo() {
        return solutionClosestToo;
    }

    public void setSolutionClosestToo(Partition solutionClosestToo) {
        this.solutionClosestToo = solutionClosestToo;
    }

    public int getNumOfOnes() {
        return numOfOnes;
    }

    public void setNumOfOnes(int numOfOnes) {
        this.numOfOnes = numOfOnes;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getSum() {
        return sum;
    }

    public ArrayList<Integer> getMultiset() {
        return multiset;
    }

    public void setMultiset(ArrayList<Integer> multiset) {
        this.multiset = multiset;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public static double getFitnessAvg() {
        return fitnessAvg;
    }

    public static void setFitnessAvg(double fitnessAvg) {
        Partition.fitnessAvg = fitnessAvg;
    }

    public static ArrayList<Integer> getInputMultiset() {
        return inputMultiset;
    }

    public static void setInputMultiset(ArrayList<Integer> inputMultiset) {
        Partition.inputMultiset = inputMultiset;
    }

    public ArrayList<String> getChromosome() {
        return chromosome;
    }

    public void setChromosome(ArrayList<String> chromosome) {
        this.chromosome = chromosome;
    }

    public boolean isExactMatch() {
        return exactMatch;
    }

    public void setExactMatch(boolean exactMatch) {
        this.exactMatch = exactMatch;
    }

    public int length(){
        return inputMultiset.size();
    }

    @Override
    public String toString(){
        return chromosome.toString();
    }


}
