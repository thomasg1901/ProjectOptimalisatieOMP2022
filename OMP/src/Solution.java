import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Solution {
    private double cost;
    private Job[] order;

    private List<Job> schedule;
    private List<Setup> setups;

    private Solution parrent;
    private List<Solution> improvements;
    private boolean foundPeak = false;

    public Solution(double cost, Job[] order, List<Job> schedule, List<Setup> setups, Solution parrent) {
        this.cost = cost;
        this.order = order;
        this.schedule = schedule;
        this.setups = setups;
        this.improvements = new ArrayList<>();
        this.parrent = parrent;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public Job[] getOrder() {
        return order;
    }

    public void setOrder(Job[] order) {
        this.order = order;
    }

    public List<Job> getSchedule() {
        return schedule;
    }

    public void setSchedule(List<Job> schedule) {
        this.schedule = schedule;
    }

    public List<Setup> getSetups() {
        return setups;
    }

    public void setSetups(List<Setup> setups) {
        this.setups = setups;
    }

    public Solution getParrent() {
        return parrent;
    }

    public void setParrent(Solution parrent) {
        this.parrent = parrent;
    }

    public List<Solution> getImprovements() {
        return improvements;
    }

    public void addImprovement(Solution improvement) {
        if(!improvements.contains(improvement)){
            this.improvements.add(improvement);
        }
    }

    public boolean isFoundPeak() {
        return foundPeak;
    }

    public void setFoundPeak(boolean foundPeak) {
        this.foundPeak = foundPeak;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Solution solution = (Solution) o;
        return (int) solution.cost == (int) cost;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cost);
    }
}
