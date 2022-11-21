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

    private int t;
    private int lastjobId;
    private double evaluate(Job[] jobs){
        schedule = new ArrayList<>();
        setups = new ArrayList<>();


        int start = jobs[0].getReleaseDate();
        int finish = start+jobs[0].getDuration();

        jobs[0].setStart(start);
        schedule.add(jobs[0]);

        lastjobId = jobs[0].getJobID();
        t = finish;

        for (int i = 1; i < jobs.length;i++){
            calcJob(t, jobs[i], lastjobId);
        }
        return costFunction(schedule, allJobs);
    }

    private boolean calcJob(int t, Job job, int lastjobId){
        int startSetup, finishSetup;
        startSetup = t > job.getReleaseDate()? t+1 : job.getReleaseDate(); // Kan nog verbeterd worden setup vroeger starten dan release
        finishSetup = startSetup + job.getSetupTimes()[lastjobId];
        int start = finishSetup+1;
        int finish = start+job.getDuration();

        int endUnavailabilityOverlap = overlapUnavailable(startSetup, finish, unavailabilities);
        if(endUnavailabilityOverlap > -1){
            return calcJob(endUnavailabilityOverlap+1, job, lastjobId);
        }
        else if(possibleFit(job, lastjobId,t)){
            job.setStart(start);
            schedule.add(job);
            setups.add(new Setup(lastjobId, job.getJobID(), startSetup));

            this.lastjobId = job.getJobID();
            this.t = finish;

            return true;
        }

        return false;
    }

    private int overlapUnavailable(int startSetup, int finish, Unavailability[] unavailabilities) {
        //Mogelijke verbetering: separation van setup & job processing (setup voor unavailability & processing erna bv.)
        for(Unavailability unavailability: unavailabilities){
            if(isInPeriod(unavailability.getStart(), unavailability.getEnd(), startSetup))
                return unavailability.getEnd();
            if(isInPeriod(unavailability.getStart(), unavailability.getEnd(), finish))
                return unavailability.getEnd();
            if(startSetup <= unavailability.getStart() && finish >= unavailability.getEnd())
                return unavailability.getEnd();
        }

        return -1;
    }

    private double costFunction(List<Job> scheduledJobs, Job[] allJobs){
        Job firstJob = scheduledJobs.get(0);
        Job lastJob = scheduledJobs.get(scheduledJobs.size() - 1);
        int lastJobFinish = lastJob.getStart() + lastJob.getDuration();
        int makeSpan = lastJobFinish - firstJob.getStart();
        int rejectedCount = 0;
        double rejectionPenaltySum = 0;
        double earlinessPenaltySum = 0;
        for(Job job : allJobs){
            if(scheduledJobs.contains(job)){
                //Job is scheduled
                int finish = (job.getStart() + job.getDuration()-1);
                earlinessPenaltySum += (job.getDueDate() - finish) * job.getEarlinessPenalty();
            } else {
                //Job is rejected
                rejectedCount++;
                rejectionPenaltySum += job.getRejectionPenalty();
            }
        }

        System.out.println("Recjected count: "+ rejectedCount);
        System.out.println("Duration: " + weightDuration * makeSpan);
        System.out.println("Earliness penalty: " + earlinessPenaltySum);
        System.out.println("Rejection penalty: " + rejectionPenaltySum);
        return weightDuration * makeSpan + earlinessPenaltySum + rejectionPenaltySum;
    }

    private boolean isInPeriod(int periodBegin, int periodEnd, int value){
        return value >= periodBegin && value <= periodEnd;
    }

    private boolean possibleFit(Job job, int prevJob, int t) {
        if(prevJob < 0)
            return true;
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
