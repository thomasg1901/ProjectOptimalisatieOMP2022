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

    private double cost;

    private List<Job> schedule;

    private List<Setup> setups;

    public double getCost() {
        return cost;
    }

    public List<Job> getSchedule() {
        return schedule;
    }

    public List<Setup> getSetups() {
        return setups;
    }

    public JobScheduler(String name, double weightDuration, int horizon, Job[] allJobs, Unavailability[] unavailabilities) {
        this.name = name;
        this.weightDuration = weightDuration;
        this.horizon = horizon;
        this.allJobs = allJobs;
        this.unavailabilities = unavailabilities;

        Arrays.sort(this.allJobs);
        this.cost = evaluate(allJobs);
    }

    private double evaluate(Job[] jobs){
        schedule = new ArrayList<>();
        setups = new ArrayList<>();

        int t = 0;
        int lastjobId = 0;
        for (int i = 0; i < jobs.length;i++){
            if(possibleFit(jobs[i], lastjobId,t)){
                int startSetup = t > jobs[i].getReleaseDate()? t+1 : jobs[i].getReleaseDate(); // Kan nog verbeterd worden setup vroeger starten dan release
                int finishSetup = startSetup + jobs[i].getSetupTimes()[lastjobId];
                int start = finishSetup+1;
                int finish = start+jobs[i].getDuration();

                if(!overlapUnavailable(startSetup, finish, unavailabilities)){
                    jobs[i].setStart(start);
                    schedule.add(jobs[i]);
                    if(t > 0)
                        setups.add(new Setup(lastjobId, jobs[i].getJobID(), startSetup));

                    lastjobId = jobs[i].getJobID();
                    t = finish;
                }
            }
        }
        return costFunction(schedule, allJobs);
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

    private double costFunction(List<Job> scheduledJobs, Job[] allJobs){
        Job firstJob = scheduledJobs.get(0);
        Job lastJob = scheduledJobs.get(scheduledJobs.size() - 1);
        int lastJobFinish = lastJob.getStart() + lastJob.getDuration();
        int makeSpan = lastJobFinish - firstJob.getStart();
        double rejectionPenaltySum = 0;
        double earlinessPenaltySum = 0;
        for(Job job : allJobs){
            if(scheduledJobs.contains(job)){
                //Job is scheduled
                int finish = (job.getStart() + job.getDuration())-1;
                earlinessPenaltySum += (job.getDueDate() - finish) * job.getEarlinessPenalty();
            } else {
                //Job is rejected
                rejectionPenaltySum += job.getRejectionPenalty();
            }
        }
        return weightDuration * makeSpan + earlinessPenaltySum + rejectionPenaltySum;
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
