public class Job {
    private int jobID;
    private int duration;
    private int releaseDate;
    private float earlinessPenalty;
    private float rejectionPenalty;

    public Job(int jobID, int duration, int releaseDate, float earlinessPenalty, float rejectionPenalty) {
        this.jobID = jobID;
        this.duration = duration;
        this.releaseDate = releaseDate;
        this.earlinessPenalty = earlinessPenalty;
        this.rejectionPenalty = rejectionPenalty;
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

    public float getEarlinessPenalty() {
        return earlinessPenalty;
    }

    public void setEarlinessPenalty(float earlinessPenalty) {
        this.earlinessPenalty = earlinessPenalty;
    }

    public float getRejectionPenalty() {
        return rejectionPenalty;
    }

    public void setRejectionPenalty(float rejectionPenalty) {
        this.rejectionPenalty = rejectionPenalty;
    }
}
