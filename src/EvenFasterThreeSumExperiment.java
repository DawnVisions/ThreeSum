import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;

public class EvenFasterThreeSumExperiment {
    /* define constants */
    static long MAXVALUE = 2000000000;
    static long MINVALUE = -2000000000;
    static int numberOfTrials = 25;
    static int MAXINPUTSIZE = (int) Math.pow(2, 18);
    static int MININPUTSIZE = 1;

    static String ResultsFolderPath = "/home/elizabeth/IdeaProjects/Results/"; // pathname to results folder
    static FileWriter resultsFile;
    static PrintWriter resultsWriter;

    public static void main(String[] args) {

        // run the whole experiment at least twice, and expect to throw away the data from the earlier runs, before java has fully optimized
        runFullExperiment("EvenFasterThreeSum-Exp1-ThrowAway.txt");
        runFullExperiment("EvenFasterThreeSum-Exp2.txt");
        runFullExperiment("EvenFasterThreeSum-Exp3.txt");
    }

    static void runFullExperiment(String resultsFileName) {

        try {
            resultsFile = new FileWriter(ResultsFolderPath + resultsFileName);
            resultsWriter = new PrintWriter(resultsFile);
        } catch (Exception e) {
            System.out.println("*****!!!!!  Had a problem opening the results file " + ResultsFolderPath + resultsFileName);
            return;
        }

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
                TrialStopwatch.start();
                int found = evenFasterThreeSum(testList);
                batchElapsedTime = batchElapsedTime + TrialStopwatch.elapsedTime();
                //Code to test the return value of faster algorithm against the brute force version
                /*if(found != ThreeSumExperiment.threeSum(testList))
                {
                    System.out.println("Error********************");
                }*/
                System.out.println("Found " + found + " triplets on test " + trial);
            }
            double averageTimePerTrialInBatch = (double) batchElapsedTime / (double) numberOfTrials; // calculate the average time per trial in this batch
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

    //ThreeSum Fastest algorithm
    public static int evenFasterThreeSum(long[] arr) {  // Count triples that sum to 0
        //Call to sort the list
        MergeSortPerformance.sort(arr, 0, arr.length-1);
        //Initializing variables
        int N = arr.length;
        int count = 0;
        long sumTwoNum = 0;

        //Same loop through the list where i index is the first value
        for (int i = 0; i<N; i++)
        {
            //Set the value of the third number and it's index to the last value in the sorted list
            long thirdNum = arr[N-1];
            int thirdNumIndex = N-1;
            //Loop between i+1 and third number index for the second value at index j
            for(int j = i+1; j<thirdNumIndex; j++)
            {
                //sumTwoNum is the third number that we are looking for
                //If the current third number value at the end of the list is greater
                //  than sumTwoNum, then third number index move down towards the beginning of the list and we try the
                //  next lowest number to be thirdNum
                sumTwoNum = (arr[i] + arr[j]) * (-1);
                while(thirdNum >= sumTwoNum && j<thirdNumIndex)
                {
                    if(thirdNum == sumTwoNum)
                    {
                        count++;
                    }
                    thirdNumIndex--;
                    thirdNum = arr[thirdNumIndex];
                }
                //When the third number is less than sumTwoNum, then we check the next larger value for the second
                //value at j
                //When j and thirdNumIndex meet, then we try the next i index
            }
        }
        return count;
    }


    static void verifyThreeSum() {
        System.out.println("Test ThreeSum");
        long zero[] = {-2, 13, 5, -4, -16, 19, 21, -28};
        System.out.println("Found " + evenFasterThreeSum(zero) + " triplets on test array with 0 triplets");
        long one[] = {-2, 3, 1, 17, 23, 1, 19, 54};
        System.out.println("Found " + evenFasterThreeSum(one) + " triplets on test array with 1 triplets");
        long two[] = {-5, -4, -2, 3, 2, 17, 23, 9, 54};
        System.out.println("Found " + evenFasterThreeSum(two) + " triplets on test array with 2 triplets");
        long three[] = {-3, -4, 7, 12, 2, 2, 4, 21, 9, 43, -30};
        System.out.println("Found " + evenFasterThreeSum(three) + " triplets on test array with 3 triplets");
    }
}
