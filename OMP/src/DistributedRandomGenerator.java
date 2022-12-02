import java.util.HashMap;
import java.util.Map;

public class DistributedRandomGenerator {
    private Map<Integer, Double> distribution;
    private double distSum;

    public DistributedRandomGenerator(Map<Integer, Double> dist) {
        this.distribution = dist;
        this.distSum = 0;
        for (Integer i : distribution.keySet()) {
            distSum += distribution.get(i);
        }
    }

    public int getDistributedRandomNumber() {
        double rand = Math.random();
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