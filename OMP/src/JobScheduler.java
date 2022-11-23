import java.util.*;

public class JobScheduler {
    private String name;
    private double weightDuration;
    private int horizon;
    private Job[] allJobs;
    private Unavailability[] unavailabilities;
    private double cost;

    private int t;
    private int lastjobId;

    private List<Job> schedule;

    private List<Setup> setups;


    // Local search
    private Job[] bestOrder;
    private List<Job> bestSchedule;
    private List<Setup> bestSetups;

    private double bestCost;

    public JobScheduler(String name, double weightDuration, int horizon, Job[] allJobs, Unavailability[] unavailabilities) {
        this.name = name;
        this.weightDuration = weightDuration;
        this.horizon = horizon;
        this.allJobs = allJobs;
        this.unavailabilities = unavailabilities;

        Arrays.sort(this.allJobs);
        long seconds = 300;
        long time = (long) (seconds * Math.pow(10,3));
        localSearch(allJobs, time, 5);
    }



    private void localSearch(Job[] initialSolution, long stopTime, int seed){
        long startTime = System.currentTimeMillis();

        // get initial solution

        bestOrder = initialSolution;
        bestCost = evaluate(initialSolution);

        bestSchedule = this.schedule;
        bestSetups = this.setups;

        Random generator = new Random(seed);
        do{
            // Get new solution
            Job[] newOrder = getNewOrder(bestOrder, generator);

            // Evaluate solution compaired to initial solution
            cost = evaluate(newOrder);
            if (cost < bestCost){
                //bestOrder = newOrder;
                bestCost = cost;
                bestSchedule = schedule;
                bestSetups = setups;
                System.out.println("[" + (System.currentTimeMillis() - startTime) + "ms] improvement: " + cost);
            }

        } while (System.currentTimeMillis() - startTime < stopTime);

    }

    private Job[] getNewOrder(Job[] jobs, Random generator){
        int index1 = generator.nextInt(jobs.length-1);
        int index2 = generator.nextInt(jobs.length-1);

        if (index1 == index2){
            index2 = index1+1;
        }

        Job[] reorder = new Job[jobs.length];
        for (int i = 0; i < jobs.length;i++){
            Job j = jobs[i];
            if (i == index1)
                j = jobs[index2];
            else if (i == index2)
                j = jobs[index1];

            reorder[i] = new Job(j.getJobID(), j.getDuration(), j.getReleaseDate(), j.getDueDate(), j.getEarlinessPenalty(), j.getRejectionPenalty(), j.getSetupTimes());
        }
        return reorder;
    }

    private double evaluate(Job[] jobs){
        schedule = new ArrayList<>();
        setups = new ArrayList<>();

        t = 0;

        for (int i = 0; i < jobs.length;i++){
            calcJob(t, jobs[i], lastjobId);
        }

        backwardsPassJobs(schedule, setups);

        return costFunction(schedule, jobs);
    }

    private int[][] calculateForwardPass(int[] es, int[] ef, int index){
        return new int[][]{es, ef};
    }

    private void backwardsPassJobs(List<Job> schedule, List<Setup> setups){
        int[] lastStart = new int[schedule.size()];

        lastStart[lastStart.length -1] = backwardsCalc(horizon, schedule.get(schedule.size()-1),setups.get(setups.size()-1), schedule.get(schedule.size()-2).getJobID(), false);

        for(int i = schedule.size()-2; i >= 0; i--){
            if(i == 0){
                lastStart[i] = backwardsCalc(lastStart[i+1], schedule.get(i), null, 0, true);
            }else {
                lastStart[i] = backwardsCalc(lastStart[i+1], schedule.get(i), setups.get(i-1), schedule.get(i-1).getJobID(), false);
            }

        }
    }

    private int backwardsCalc(int earlyeastFinish, Job job, Setup setup, int prevJobId, boolean firstJob){
        int finishJob = earlyeastFinish;
        //Wanneer laatste mogelijke finish na due date ligt, aanpassen naar due date
        if(finishJob > job.getDueDate()){
            finishJob = job.getDueDate();
        }

        int startJob = finishJob - job.getDuration();


        if(!firstJob){
            int finishSetup = startJob;
            int startSetup = finishSetup - job.getSetupTimes()[prevJobId];
            Unavailability unavailabilityOverlap = overlapUnavailable(startSetup, finishJob, unavailabilities);
            if(unavailabilityOverlap != null){
                return backwardsCalc(unavailabilityOverlap.getStart()-1, job, setup, prevJobId, firstJob);
            }
            setup.setStart(startSetup);
            job.setStart(startJob);
            return startSetup;
        }else {
            job.setStart(startJob);
            return startJob;
        }
    }


    private boolean calcJob(int t, Job job, int lastjobId){
        int startSetup, finishSetup, start, finish;
        if(!schedule.isEmpty()){
            startSetup = t; // setup vroeger starten dan release
            if(t < job.getReleaseDate() && job.getReleaseDate() > t + job.getSetupTimes()[lastjobId]){
                startSetup = job.getReleaseDate() - job.getSetupTimes()[lastjobId];
            }
            finishSetup = startSetup + job.getSetupTimes()[lastjobId];
            start = finishSetup;
            finish = start+job.getDuration();
        } else {
            start = t > job.getReleaseDate()? t : job.getReleaseDate();
            finish = start+job.getDuration();
            startSetup = start;
        }

        Unavailability unavailabilityOverlap = overlapUnavailable(startSetup, finish, unavailabilities);
        if(unavailabilityOverlap != null){
            return calcJob(unavailabilityOverlap.getEnd()+1, job, lastjobId);
        }
        else if(possibleFit(job, lastjobId,t)){
            job.setStart(start);
            if(!schedule.isEmpty())
                setups.add(new Setup(lastjobId, job.getJobID(), startSetup));
            schedule.add(job);
            this.lastjobId = job.getJobID();
            this.t = finish;
            return true;
        }
        return false;
    }

    private Unavailability overlapUnavailable(int startSetup, int finish, Unavailability[] unavailabilities) {
        //Mogelijke verbetering: separation van setup & job processing (setup voor unavailability & processing erna bv.)
        for(Unavailability unavailability: unavailabilities){
            if(isInPeriod(unavailability.getStart(), unavailability.getEnd(), startSetup))
                return unavailability;
            if(isInPeriod(unavailability.getStart(), unavailability.getEnd(), finish))
                return unavailability;
            if(startSetup <= unavailability.getStart() && finish >= unavailability.getEnd())
                return unavailability;
        }

        return null;
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

//        System.out.println("Rejected count: "+ rejectedCount);
//        System.out.println("Duration: " + weightDuration * makeSpan);
//        System.out.println("Earliness penalty: " + earlinessPenaltySum);
//        System.out.println("Rejection penalty: " + rejectionPenaltySum);
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

    public double getCost() {
        return this.bestCost;
    }

    public List<Job> getSchedule() {
        return this.bestSchedule;
    }

    public List<Setup> getSetups() {
        return this.bestSetups;
    }

    public Unavailability[] getUnavailabilities() {
        return unavailabilities;
    }
}
