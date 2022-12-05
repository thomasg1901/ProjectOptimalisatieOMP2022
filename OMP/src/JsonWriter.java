import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonWriter {
    private List<Job> schedule;
    private  List<Setup> setups;
    private double cost;
    private JSONArray gridSearch;

    private String name;

    public JsonWriter(String name,List<Job> schedule, List<Setup> setups, double cost) {
        this.schedule = schedule;
        this.setups = setups;
        this.cost = cost;
        this.name = name;
        gridSearch = new JSONArray();
    }

    public JsonWriter(){
        gridSearch = new JSONArray();
    }

    public void createGridSearchJson(List<Long> times, List<Double> costs, double alpha, double T){
        JSONObject jsonSolution = new JSONObject();

        JSONArray innerArrayCosts = new JSONArray();

        for (double cost : costs) {
            innerArrayCosts.put(cost);
        }

        JSONArray innerArrayTimes = new JSONArray();
        for (long time : times ){
            innerArrayTimes.put(time);
        }

        jsonSolution.put("costs", innerArrayCosts);
        jsonSolution.put("times", innerArrayTimes);
        jsonSolution.put("alpha", alpha);
        jsonSolution.put("T", T);

        this.gridSearch.put(jsonSolution);
    }

    public void writeGridSearchToJson(String path){
        writeToFile(path, this.gridSearch.toString());
    }

    public void writeSolutionToJson(String path){
        JSONObject jsonSolution = new JSONObject();
        jsonSolution.put("name",name);
        jsonSolution.put("value", Math.round(cost * 100.0) / 100.0);

        List<JSONObject> scheduleMap = new ArrayList<>();

        for(Job job: schedule){
            JSONObject scheduleEntry = new JSONObject();
            scheduleEntry.put("id", job.getJobID());
            scheduleEntry.put("start", job.getStart());
            scheduleMap.add(scheduleEntry);
        }
        jsonSolution.put("jobs",scheduleMap);
        jsonSolution.put("setups",setups);

        System.out.println(jsonSolution);
        writeToFile(path, jsonSolution.toString());
    }

    private void writeToFile(String path, String jsonSolution){
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path));
            writer.write(jsonSolution);

            writer.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
