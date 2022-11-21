import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
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

        Arrays.sort(this.allJobs);
        evaluate(allJobs);
    }

    private double evaluate(Job[] jobs){
        int[] startTime = new int[jobs.length];
        int[] finishTime = new int[jobs.length];
        int[] setupStart = new int[jobs.length];
        int[] setupFinish = new int[jobs.length];
        ArrayList<Job> schedule = new ArrayList<Job>();

        int t = 0;
        int lastjob = 0;
        for (int i = 0; i < jobs.length;i++){
            if(possibleFit(jobs[i], lastjob,t)){
                int startSetup = t > jobs[i].getReleaseDate()? t+1 : jobs[i].getReleaseDate(); // Kan nog verbeterd worden setup vroeger starten dan release
                int finishSetup = startSetup + jobs[i].getSetupTimes()[lastjob];
                int start = finishSetup+1;
                int finish = start+jobs[i].getDuration();

                if(!overlapUnavailable(startSetup, finish, unavailabilities)){
                    lastjob = jobs[i].getJobID();
                    schedule.add(jobs[i]);
                    t = finish;
                }
            }
        }

        return 0.0;
    }

    private boolean overlapUnavailable(int startSetup, int finish, Unavailability[] unavailabilities) {
        //Mogelijke verbetering: separation van setup & job processing (setup voor unavailability & processing erna bv.)
        for(Unavailability unavailability: unavailabilities){
            if(isInPeriod(unavailability.getStart(), unavailability.getEnd(), startSetup))
                return true;
            if(isInPeriod(unavailability.getStart(), unavailability.getEnd(), finish))
                return true;
            if(startSetup <= unavailability.getStart() && finish >= unavailability.getEnd())
                return true;
        }

        return false;
    }

    private double costFunction(){
        // w.dur + sum van early penalty + sum van rejection penalty
    }

    private boolean isInPeriod(int periodBegin, int periodEnd, int value){
        return value >= periodBegin && value <= periodEnd;
    }

    private boolean possibleFit(Job job, int prevJob, int t) {
        return job.getDueDate() > t + job.getSetupTimes()[prevJob] + job.getDuration();
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
