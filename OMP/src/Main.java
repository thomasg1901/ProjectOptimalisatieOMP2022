import org.json.*;
import com.google.gson.*;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {
        ArrayList<Job> jobs = new ArrayList<>();
        String resourceName = "./OMP/file.json";
        String is = new String(Files.readAllBytes(Paths.get(resourceName)));
        JSONTokener tokener = new JSONTokener(is);
        JSONObject object = new JSONObject(tokener);
        JSONArray jsonjobs = object.getJSONArray("jobs");
        JSONArray jsonsetup = object.getJSONArray("setups");

        for (int i=0; i<jsonjobs.length();i++){
            JSONObject data = jsonjobs.getJSONObject(i);

            jobs.add(new Job((int)data.get("id"),(int)data.get("duration"),(int)data.get("release_date"),((BigDecimal) data.get("earliness_penalty")).doubleValue(),((BigDecimal) data.get("rejection_penalty")).doubleValue(),jsonsetup.getJSONArray(i)));
        }
//
//        try {
//            // create Gson instance
//            Gson gson = new Gson();
//
//            // create a reader
//
//            Reader reader = Files.newBufferedReader(Paths.get("/Users/arijnborzo/ProjectOptimalisatieOMP2022/OMP/file.json"));
//
//            // convert JSON file to map
//            Map<?, ?> map = gson.fromJson(reader, Map.class);
//
//            // print map entries
//            for (Map.Entry<?, ?> entry : map.entrySet()) {
//                System.out.println(entry.getKey() + "=" + entry.getValue());
//            }
//
//            // close reader
//            reader.close();
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
    }


}



