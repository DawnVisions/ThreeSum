import java.io.FileWriter;
import java.io.PrintWriter;

public class ThreeSumExperiment {
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
        runFullExperiment("ThreeSum-Exp1-ThrowAway.txt");
        runFullExperiment("ThreeSum-Exp2.txt");
        runFullExperiment("ThreeSum-Exp3.txt");
    }

    static void runFullExperiment(String resultsFileName) {

        try {
            resultsFile = new FileWriter(ResultsFolderPath + resultsFileName);
            resultsWriter = new PrintWriter(resultsFile);
        } catch (Exception e) {
            System.out.println("*****!!!!!  Had a problem opening the results file " + ResultsFolderPath + resultsFileName);
            return;
        }

        ThreadCpuStopWatch TrialStopwatch = new ThreadCpuStopWatch();
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
                int found = threeSum(testList);
                batchElapsedTime = batchElapsedTime + TrialStopwatch.elapsedTime();
                System.out.println("Found " + found + " triplets on test " + trial);
            }
            double averageTimePerTrialInBatch = (double) batchElapsedTime / (double) numberOfTrials; // calculate the average time per trial in this batch
            if (lastAverageTime != -1) {
                doublingRatio = averageTimePerTrialInBatch / lastAverageTime;
            }
            lastAverageTime = averageTimePerTrialInBatch;
            System.out.println("Last average is " + lastAverageTime);

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

    //Brute force threeSum algorithm
    //Iterates through three loops to test each combination of three numbers
    public static int threeSum(long[] arr) {  // Count triples that sum to 0.
        int N = arr.length;
        int cnt = 0;
        for (int i = 0; i < N; i++) {
            for (int j = i + 1; j < N; j++) {
                for (int k = j + 1; k < N; k++) {
                    if (arr[i] + arr[j] + arr[k] == 0)
                        cnt++;
                }
            }
        }
        return cnt;
    }

    static void verifyThreeSum() {
        System.out.println("Test ThreeSum");
        long zero[] = {-2, 13, 5, -4, -16, 19, 21, -28};
        System.out.println("Found " + threeSum(zero) + " triplets on test array with 0 triplets");
        long one[] = {-2, 3, 1, 17, 23, 1, 19, 54};
        System.out.println("Found " + threeSum(one) + " triplets on test array with 1 triplets");
        long two[] = {-5, -4, -2, 3, 2, 17, 23, 9, 54};
        System.out.println("Found " + threeSum(two) + " triplets on test array with 2 triplets");
        long three[] = {-3, -4, 7, 12, 2, 2, 4, 21, 9, 43, -30};
        System.out.println("Found " + threeSum(three) + " triplets on test array with 3 triplets");
    }
}
