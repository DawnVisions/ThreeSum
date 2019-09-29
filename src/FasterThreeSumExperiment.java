import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;

public class FasterThreeSumExperiment {
    /* define constants */
    static long MAXVALUE = 2000000000;
    static long MINVALUE = -2000000000;
    static int numberOfTrials = 25;
    static int MAXINPUTSIZE = (int) Math.pow(2, 13);
    static int MININPUTSIZE = 1;

    static String ResultsFolderPath = "/home/elizabeth/IdeaProjects/Results/"; // pathname to results folder
    static FileWriter resultsFile;
    static PrintWriter resultsWriter;

    public static void main(String[] args) {
        // run the whole experiment at least twice, and expect to throw away the data from the earlier runs, before java has fully optimized
        runFullExperiment("FasterThreeSum-Exp1-ThrowAway.txt");
        runFullExperiment("FasterThreeSum-Exp2.txt");
        runFullExperiment("FasterThreeSum-Exp3.txt");
    }

    static void runFullExperiment(String resultsFileName) {

        try {
            resultsFile = new FileWriter(ResultsFolderPath + resultsFileName);
            resultsWriter = new PrintWriter(resultsFile);
        } catch (Exception e) {
            System.out.println("*****!!!!!  Had a problem opening the results file " + ResultsFolderPath + resultsFileName);
            return;
        }

        ThreadCpuStopWatch BatchStopwatch = new ThreadCpuStopWatch(); // for timing an entire set of trials
        ThreadCpuStopWatch TrialStopwatch = new ThreadCpuStopWatch(); // for timing an individual trial
        double lastAverageTime = -1;
        double doublingRatio = 0;

        resultsWriter.println("#InputSize    AverageTime     Doubling Ratio"); // # marks a comment in gnuplot data
        resultsWriter.flush();

        for (int inputSize = MININPUTSIZE; inputSize <= MAXINPUTSIZE; inputSize *= 2) {
            System.out.println("Running test for input size " + inputSize + " ... ");
            System.out.print("    Running trial batch...\n");
            System.gc();
            long batchElapsedTime = 0;
            for (long trial = 0; trial < numberOfTrials; trial++) {
                long[] testList = createRandomList(inputSize);
                //Start stopwatch
                TrialStopwatch.start();
                //Runs ThreeSumFast
                //This algorithm includes the call and time for list sorting
                int found = fasterThreeSum(testList);
                //Get trial run time
                batchElapsedTime = batchElapsedTime + TrialStopwatch.elapsedTime();
                System.out.println("Found " + found + " triplets on test " + trial);
            }
            double averageTimePerTrialInBatch = (double) batchElapsedTime / (double) numberOfTrials; // calculate the average time per trial in this batch
            //Calculate doubling ratio based on the last average time for the last input size
            if (lastAverageTime != -1) {
                doublingRatio = averageTimePerTrialInBatch / lastAverageTime;
            }
            lastAverageTime = averageTimePerTrialInBatch;

            /* print data for this size of input */
            resultsWriter.printf("%12d  %15.2f %10.2f\n", inputSize, averageTimePerTrialInBatch, doublingRatio);
            resultsWriter.flush();
            System.out.println(" ....done.");
        }
    }

    public static long[] createRandomList(int size) {

        long[] list = new long[size];
        for (int i = 0; i < size; i++) {
            list[i] = (long) (MINVALUE + Math.random() * (MAXVALUE - MINVALUE));
        }
        return list;
    }

    //ThreeSum Fast
    public static int fasterThreeSum(long[] arr) {  // Count triples that sum to 0
        //Uses merge sort to sort the list
        MergeSortPerformance.sort(arr, 0, arr.length-1);
        //Initializing variables
        int N = arr.length;
        int count = 0;
        long sumTwoNum = 0;
        //Loop through sorted list, i is the index of the first number
        for (int i = 0; i<N; i++)
        {
            //j is the index of the second number
            for(int j = i+1; j<N; j++)
            {
                //Based on numbers at i and j, sumTwoNum is the third number we need to add up to zero
                sumTwoNum = (arr[i] + arr[j]) * (-1);
                //Uses binarySearch from the Java Arrays library to search for the third number
                //Returns a number greater than zero if the third number is found
                if (Arrays.binarySearch(arr, j+1, N, sumTwoNum) >= 0) {
                    count++;
                }
            }
        }
        //Returns number of three sums found that add up to zero
        return count;
    }


    static void verifyThreeSum() {
        System.out.println("Test ThreeSum");
        long zero[] = {-2, 13, 5, -4, -16, 19, 21, -28};
        System.out.println("Found " + fasterThreeSum(zero) + " triplets on test array with 0 triplets");
        long one[] = {-2, 3, 1, 17, 23, 1, 19, 54};
        System.out.println("Found " + fasterThreeSum(one) + " triplets on test array with 1 triplets");
        long two[] = {-5, -4, -2, 3, 2, 17, 23, 9, 54};
        System.out.println("Found " + fasterThreeSum(two) + " triplets on test array with 2 triplets");
        long three[] = {-3, -4, 7, 12, 2, 2, 4, 21, 9, 43, -30};
        System.out.println("Found " + fasterThreeSum(three) + " triplets on test array with 3 triplets");
    }
}
