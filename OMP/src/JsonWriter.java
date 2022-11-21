import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonWriter {
    private List<Job> schedule;
    private  List<Setup> setups;
    private double cost;

    private String name;

    public JsonWriter(String name,List<Job> schedule, List<Setup> setups, double cost) {
        this.schedule = schedule;
        this.setups = setups;
        this.cost = cost;
        this.name = name;
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

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path+"/"+name+"_sol.json"));
            writer.write(jsonSolution.toString());

            writer.close();
            System.out.println(jsonSolution.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
