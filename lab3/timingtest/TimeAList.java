package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeAList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeAListConstruction();
    }

    public static void timeAListConstruction() {
        // TODO: YOUR CODE HERE
        int n = 1000;
        AList<Integer> Ns = new AList<Integer>();
        AList<Double> times = new AList<Double>();
        for (int i = 0; i < 10; i += 1) {
            Ns.addLast(n);
            n *= 2;
        }
        for (int i = 0; i < Ns.size(); i += 1) {
            AList<Integer> test = new AList<Integer>();
            Stopwatch t = new Stopwatch();
            for (int j = 0; j < Ns.get(i); j += 1) {
                test.addLast(1);
            }
            times.addLast(t.elapsedTime());
        }
        printTimingTable(Ns, times, Ns);
    }
}
