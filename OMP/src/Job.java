import java.util.ArrayList;
import java.util.Objects;

public class Job implements Comparable {
    private int jobID;
    private int duration;
    private int releaseDate;
    private int dueDate;
    private double earlinessPenalty;
    private double rejectionPenalty;
    private int[] setupTimes;

    private int start;

    public Job(int jobID, int duration, int releaseDate, int dueDate, double earlinessPenalty, double rejectionPenalty, int[] setupTimes) {
        this.jobID = jobID;
        this.duration = duration;
        this.releaseDate = releaseDate;
        this.dueDate = dueDate;
        this.earlinessPenalty = earlinessPenalty;
        this.rejectionPenalty = rejectionPenalty;
        this.setupTimes = setupTimes;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Job job = (Job) o;
        return jobID == job.jobID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(jobID);
    }

    @Override
    public int compareTo(Object o) {
        Job oJob = (Job)o;
        if(this.getReleaseDate() != oJob.getReleaseDate()){
            return this.getReleaseDate() - oJob.getReleaseDate();
        } else {
            return this.getDueDate() - oJob.getDueDate();
        }
    }
}
