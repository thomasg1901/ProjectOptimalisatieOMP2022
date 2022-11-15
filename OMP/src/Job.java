import java.util.ArrayList;

public class Job implements Comparable {
    private int jobID;
    private int duration;
    private int releaseDate;

    private int dueDate;
    private double earlinessPenalty;
    private double rejectionPenalty;
    private int[] setupTimes;

    public Job(int jobID, int duration, int releaseDate, int dueDate, double earlinessPenalty, double rejectionPenalty, int[] setupTimes) {
        this.jobID = jobID;
        this.duration = duration;
        this.releaseDate = releaseDate;
        this.dueDate = dueDate;
        this.earlinessPenalty = earlinessPenalty;
        this.rejectionPenalty = rejectionPenalty;
        this.setupTimes = setupTimes;
    }

    public int getJobID() {
        return jobID;
    }

    public void setJobID(int jobID) {
        this.jobID = jobID;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(int releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getDueDate() {
        return dueDate;
    }

    public void setDueDate(int dueDate) {
        this.dueDate = dueDate;
    }

    public double getEarlinessPenalty() {
        return earlinessPenalty;
    }

    public void setEarlinessPenalty(double earlinessPenalty) {
        this.earlinessPenalty = earlinessPenalty;
    }

    public double getRejectionPenalty() {
        return rejectionPenalty;
    }

    public void setRejectionPenalty(double rejectionPenalty) {
        this.rejectionPenalty = rejectionPenalty;
    }

    public int[] getSetupTimes() {
        return setupTimes;
    }

    public void setSetupTimes(int[] setupTimes) {
        this.setupTimes = setupTimes;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
