import java.lang.reflect.Array;
import java.util.List;

public class JobScheduler {
    private String name;
    private double weightDuration;
    private int horizon;
    private Job[] allJobs;
    private Unavailability[] unavailabilities;

    public JobScheduler(String name, double weightDuration, int horizon, Job[] allJobs, Unavailability[] unavailabilities) {
        this.name = name;
        this.weightDuration = weightDuration;
        this.horizon = horizon;
        this.allJobs = allJobs;
        this.unavailabilities = unavailabilities;
    }

    private double evaluate(Job[] jobs){
        int[] startTime = new int[jobs.length];
        int[] finishTime = new int[jobs.length];
        int[] setupStart = new int[jobs.length];
        int[] setupFinish = new int[jobs.length];

        int t = 0;
        int lastjob = 0;
        for (int i = 0; i < jobs.length;i++){
            if(possibleFit(jobs[i], lastjob,t)){
                int startSetup = t+1;
                int finishSetup = startSetup + jobs[i].getSetupTimes()[lastjob];
                int start = finishSetup+1;
                int finish = start+jobs[i].getDuration();

                // look if this has overlap with unavailability
                // check the release

                lastjob = jobs[i].getJobID();0
            }
        }

        return 0.0;
    }

    private boolean possibleFit(Job job, int prevJob, int t) {
        return job.getDueDate() < t + job.getSetupTimes()[prevJob] + job.getDuration();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getWeightDuration() {
        return weightDuration;
    }

    public void setWeightDuration(double weightDuration) {
        this.weightDuration = weightDuration;
    }

    public int getHorizon() {
        return horizon;
    }

    public void setHorizon(int horizon) {
        this.horizon = horizon;
    }

    public Job[] getAllJobs() {
        return allJobs;
    }

    public Unavailability[] getUnavailabilities() {
        return unavailabilities;
    }
}
