import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class JsonWriter {
    private ArrayList<Job> schedule;
    private  ArrayList<Setup> setups;
    private double cost;

    private String name;

    public JsonWriter(String name,ArrayList<Job> schedule, ArrayList<Setup> setups, double cost) {
        this.schedule = schedule;
        this.setups = setups;
        this.cost = cost;
        this.name = name;
    }

    public void writeSolutionToJson(String path){
        JSONObject jsonSolution = new JSONObject();
        jsonSolution.put("name",name);
        jsonSolution.put("value", cost);
        jsonSolution.put("jobs",schedule);
        jsonSolution.put("setups",setups);


        try {
            FileWriter file = new FileWriter(path+"/"+name+"_sol.json");
            file.write(jsonSolution.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
