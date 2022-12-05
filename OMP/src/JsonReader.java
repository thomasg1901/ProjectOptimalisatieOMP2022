import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonReader {

    private static Job[] getJoblistFromFile(String resourceName) throws IOException {
        JSONObject object = getJsonObject(resourceName);
        JSONArray jsonjobs = object.getJSONArray("jobs");
        JSONArray jsonsetup = object.getJSONArray("setups");
        Job[] jobs = new Job[jsonjobs.length()];
        for (int i=0; i<jsonjobs.length();i++){
            JSONObject data = jsonjobs.getJSONObject(i);
            double earliness_penalty, rejection_penalty;
            if(data.get("earliness_penalty").getClass() == Integer.class){
                earliness_penalty = ((Integer) data.get("earliness_penalty")).doubleValue();
            } else {
                earliness_penalty = ((BigDecimal) data.get("earliness_penalty")).doubleValue();
            }
            if(data.get("rejection_penalty").getClass() == Integer.class){
                rejection_penalty = ((Integer)data.get("rejection_penalty")).doubleValue();
            } else {
                rejection_penalty = ((BigDecimal) data.get("rejection_penalty")).doubleValue();
            }
            jobs[i] = new Job((int)data.get("id"),
                    (int)data.get("duration"),
                    (int)data.get("release_date"),
                    (int)data.get("due_date"),
                    earliness_penalty,
                    rejection_penalty,
                    convertJsonArray(jsonsetup.getJSONArray((int)data.get("id"))));
        }
        return jobs;
    }

    private static double getWeightDurationFromFile(String resourceName) throws IOException {
        JSONObject object = getJsonObject(resourceName);
        return ((BigDecimal)(object.get("weight_duration"))).doubleValue();
    }

    private static int getHorizonFromFile(String resourceName) throws IOException {
        JSONObject object = getJsonObject(resourceName);
        return (int)(object.get("horizon"));
    }

    private static String getNameFromFile(String resourceName) throws IOException {
        JSONObject object = getJsonObject(resourceName);
        return (String)(object.get("name"));
    }

    public static JobScheduler createJobSchedulerFromFile(String resourceName, int seed, int timeLimit, int maxThreads) throws IOException {
        String name = getNameFromFile(resourceName);
        double weightDuration = JsonReader.getWeightDurationFromFile(resourceName);
        int horizon = JsonReader.getHorizonFromFile(resourceName);
        Job[] allJobs = JsonReader.getJoblistFromFile(resourceName);
        Unavailability[] unavailabilities = JsonReader.getUnavailabilityPeriodsFromFile(resourceName);

        return new JobScheduler(name, weightDuration, horizon, allJobs, unavailabilities, seed, timeLimit, maxThreads);
    }

    public static Unavailability[] getUnavailabilityPeriodsFromFile(String resourceName) throws IOException {
        JSONObject object = getJsonObject(resourceName);
        JSONArray jsonUnavailabilities = object.getJSONArray("unavailability");
        Unavailability[] unavailabilities = new Unavailability[jsonUnavailabilities.length()];
        for (int i=0; i<jsonUnavailabilities.length();i++){
            JSONObject data = jsonUnavailabilities.getJSONObject(i);
            unavailabilities[i] = new Unavailability((int)data.get("start"), (int)data.get("end"));
        }
        return unavailabilities;
    }



    private static JSONObject getJsonObject(String resourceName) throws IOException {

        String is = new String(Files.readAllBytes(Paths.get(resourceName)));
        JSONTokener tokener = new JSONTokener(is);
        JSONObject object = new JSONObject(tokener);
        return object;
    }

    private static int[] convertJsonArray(JSONArray array){
        int[] result = new int[array.length()];

        for (int i = 0; i < array.length();i++){
            result[i] = Integer.parseInt(""+array.get(i));
        }

        return result;
    }

}
