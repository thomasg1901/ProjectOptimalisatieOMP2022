import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class JobScheduler {
    private String name;
    private double weightDuration;
    private int horizon;
    private Job[] allJobs;
    private Unavailability[] unavailabilities;
    private double cost;

    private List<Double> costs;
    private List<Long> time;
    private int t;
    private int lastjobId;

    private List<Job> schedule;

    private List<Setup> setups;

    private double biggestLeap = 0;


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

        costs = new ArrayList<>();
        time = new ArrayList<>();

        Arrays.sort(this.allJobs);
        long seconds = 300;
        long time = (long) (seconds * Math.pow(10,3));
        simulatedAnneling(getInitialSolution(allJobs), System.currentTimeMillis(), time, 5);
        //localSearch(allJobs, time, 5);
        System.out.println(costs.size());

        writeToFile(costs, this.name);
        writeToFile(this.time, name+"_times");
    }

    private void writeToFile(List list,String name){
        final String FILENAME = name+".txt";
        try ( BufferedWriter bw = new BufferedWriter (new FileWriter(FILENAME)) )
        {
            for (var line : list) {
                bw.write (line + ";");
            }
        } catch (IOException e) {
            e.printStackTrace ();
        }
    }

    private Solution getInitialSolution(Job[] initialOrder){
        bestOrder = initialOrder;
        bestCost = evaluate(initialOrder);

        bestSchedule = this.schedule;
        bestSetups = this.setups;

        return new Solution(bestCost, bestOrder, bestSchedule, bestSetups, null);
    }



    private void localSearch(Job[] initialOrder, long stopTime, int seed){
        long startTime = System.currentTimeMillis();

        bestOrder = initialOrder;
        bestCost = evaluate(initialOrder);

        bestSchedule = this.schedule;
        bestSetups = this.setups;

        Solution initialSolution = new Solution(bestCost, bestOrder, bestSchedule, bestSetups, null);
        try {
            Solution s = searchSolution(initialSolution,startTime, seed);
            for (int i = s.getImprovements().size() -1; i > 0 ; i--) {
                hillClimb(s.getImprovements().get(i), startTime, stopTime, seed);
            }

        }catch (RuntimeException e){
            System.out.println(e.getMessage());
            System.out.println(System.currentTimeMillis() - startTime);
        }
    }

    private void simulatedAnneling(Solution solution, long start, long stopTime, int seed){
        double T = 7800;
        double alpha = 0.51;
        Random generator = new Random(seed);
        do{
            Job[] newOrder = getNewOrder(solution.getOrder(), generator);
            double cost = evaluate(newOrder);
            if(cost < this.bestCost){
                bestSchedule = schedule;
                bestSetups = setups;
                bestCost = cost;
                solution.setOrder(newOrder);
                solution.setSchedule(schedule);
                solution.setSetups(setups);
                solution.setCost(cost);
                System.out.println("[" + (System.currentTimeMillis() - start) + "ms] Global improvement found: " + cost);
                costs.add(cost);
                time.add(System.currentTimeMillis() - start);
            }else if(Math.exp((this.bestCost - cost)/T)*100 > 30){
                solution.setOrder(newOrder);
                solution.setSchedule(schedule);
                solution.setSetups(setups);
                solution.setCost(cost);
                costs.add(cost);
                time.add(System.currentTimeMillis() - start);
            }
            T = alpha * T;
        }while (System.currentTimeMillis() - start < stopTime);
    }

    private void hillClimb(Solution solution, long start, long stopTime, int seed){
        long improvementFound = System.currentTimeMillis();
        Random generator = new Random(seed);
        do{
            Job[] newOrder = getNewOrder(solution.getOrder(), generator);

            cost = evaluate(newOrder);
            if (cost < bestCost){
                bestSchedule = schedule;
                bestSetups = setups;
                bestCost = cost;
                solution.setOrder(newOrder);
                solution.setSchedule(schedule);
                solution.setSetups(setups);
                solution.setCost(cost);
                improvementFound = System.currentTimeMillis();
                System.out.println("[" + (System.currentTimeMillis() - start) + "ms] Global improvement found: " + cost);
            } else if (cost < solution.getCost()) {
                solution.setOrder(newOrder);
                solution.setSchedule(schedule);
                solution.setSetups(setups);
                solution.setCost(cost);
                improvementFound = System.currentTimeMillis();
                System.out.println("[" + (System.currentTimeMillis() - start) + "ms] Local improvement found: " + cost);
            }

        }while (System.currentTimeMillis() - start < stopTime && System.currentTimeMillis() - improvementFound < 10000);
        // geen verbetering in x tijd volgende
    }

    private Solution searchSolution(Solution solution, long totalStart, int seed){
        long improvementFound = System.currentTimeMillis();
        Random generator = new Random(seed);
        do{
            // Get new solution
            Job[] newOrder = getNewOrder(solution.getOrder(), generator);

            // Evaluate solution compaired to initial solution
            cost = evaluate(newOrder);
            if (cost < bestCost){
                if(solution.getCost() - cost > biggestLeap)
                    biggestLeap = solution.getCost() - cost;
                bestCost = cost;
                solution.addImprovement(new Solution(cost, newOrder, schedule, setups, solution));
                bestSchedule = schedule;
                bestSetups = setups;
                System.out.println("[" + (System.currentTimeMillis() - totalStart) + "ms] improvement: " + cost);
                improvementFound = System.currentTimeMillis();
            }
        } while (System.currentTimeMillis() - improvementFound < 5000);
        solution.setFoundPeak(true);
        return solution;
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
        double sum = weightDuration * makeSpan + earlinessPenaltySum + rejectionPenaltySum;

        return Math.round(sum * 100.0) / 100.0;
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
