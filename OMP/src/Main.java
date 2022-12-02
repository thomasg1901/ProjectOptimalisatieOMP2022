import org.json.*;
import com.google.gson.*;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {

//        String resourceName = "./OMP/src/resources/TOY-20-10.json";
//        resourceName = "./OMP/file.json";

//        JobScheduler scheduler = JsonReader.createJobSchedulerFromFile(resourceName);

//        System.out.println("Cost of " + resourceName + ": " + String.valueOf(scheduler.getCost()));
//        JsonWriter out = new JsonWriter(scheduler.getName(), scheduler.getSchedule(), scheduler.getSetups(), scheduler.getCost());
//        out.writeSolutionToJson("./OMP/output");
        //findSolutionsA("./OMP/src/resources/");
        //findSolutionsB("./OMP/src/resources/");
        findSolution("OMP/src/resources/A-100-30.json","./OMP/output");

//        Voor Jef:
//        findSolutionsA("./src/resources/");
//        findSolutionsB("./src/resources/");

    }

    private static void findSolutionsA(String basePath) throws IOException {
        String[] resourcesA = new String[]{"A-100-30.json","A-200-30.json","A-400-90.json"};
        for(String resourceName : resourcesA){
            resourceName = basePath+resourceName;
            findSolution(resourceName, "./OMP/output");
        }
    }

    private static void findSolutionsB(String basePath) throws IOException {
        String[] resourcesB = new String[]{"B-100-30.json","B-200-30.json","B-400-90.json"};
        for(String resourceName : resourcesB){
            resourceName = basePath+resourceName;
            findSolution(resourceName, "./OMP/output");
        }
    }

    private static void findSolution(String inputPath, String outputPath) throws IOException {
        JobScheduler scheduler = JsonReader.createJobSchedulerFromFile(inputPath);
        visualize(scheduler);
        System.out.println("Cost of " + scheduler.getName() + ": " + String.valueOf(scheduler.getCost()));
        JsonWriter out = new JsonWriter(scheduler.getName(), scheduler.getSchedule(), scheduler.getSetups(), scheduler.getCost());
        out.writeSolutionToJson(outputPath);
    }

    private static void visualize(JobScheduler scheduler){
        List<Job> schedule = scheduler.getSchedule();
        String u = "";
        for (Unavailability unavailability: scheduler.getUnavailabilities()) {
            if(u.length() < unavailability.getStart()){
                for (int i = u.length(); i < unavailability.getStart(); i++) {
                    u += " ";
                }
            }
            for (int i = u.length(); i < unavailability.getEnd(); i++) {
                u += "X";
            }
        }
        for (int i = 0; i < schedule.size(); i++) {
            String s = "";
            String v = "";
            Job job = schedule.get(i);
            for (int j = 0; j < job.getDueDate(); j++) {
                if(j >= job.getReleaseDate()){
                    s += "-";
                }else{
                    s += " ";
                }
            }
            if(i > 0){
                Setup setup = scheduler.getSetups().get(i-1);
                int duration = job.getSetupTimes()[setup.getFrom()];
                for (int j = 0; j < job.getStart(); j++) {
                    if(j >= setup.getStart() && j <= setup.getStart() + duration){
                        v += "$";
                    }else {
                        v += " ";
                    }

                }
            }else{
                for (int j = 0; j < job.getStart(); j++) {
                    v += " ";
                }
            }

            for(int j = schedule.get(i).getStart(); j < schedule.get(i).getStart()+schedule.get(i).getDuration(); j++){
                v += "A";
            }
            System.out.println(u);
            System.out.println(s);
            System.out.println(v);
        }
    }
}



