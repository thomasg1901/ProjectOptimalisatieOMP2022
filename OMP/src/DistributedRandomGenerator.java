import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DistributedRandomGenerator {

    private Map<Map<Integer, Double>, Double> candidates;
    private Map<Integer, Double> distribution;
    private double distSum;
    private double candidateSum;

    private Random generator;

    public DistributedRandomGenerator(Map<Integer, Double> dist, Random generator) {
        this.distribution = dist;
        this.distSum = 0;
        this.candidateSum = 0;
        this.generator = generator;
        for (Integer i : distribution.keySet()) {
            distSum += distribution.get(i);
        }
    }

    public int getDistributedRandomNumber() {
        double rand = generator.nextDouble();
        if(distSum < 0){
            distSum = 0;
            for (Integer i : distribution.keySet()) {
                distribution.put(i, 1/Math.abs(distribution.get(i)));
                distSum += 1/Math.abs(distribution.get(i));
            }
        }
        double ratio = 1.0f / distSum;
        double tempDist = 0;
        for (Integer i : distribution.keySet()) {
            tempDist += distribution.get(i);
            if (rand / ratio <= tempDist) {
                return i;
            }
        }
        return 0;
    }
}