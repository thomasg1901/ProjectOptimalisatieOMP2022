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
        String resourceName = "./file.json";
        String is = new String(Files.readAllBytes(Paths.get(resourceName)));
        JSONTokener tokener = new JSONTokener(is);
        JSONObject object = new JSONObject(tokener);
        JSONArray jsonjobs = object.getJSONArray("jobs");
        JSONArray jsonsetup = object.getJSONArray("setups");

        for (int i=0; i<jsonjobs.length();i++){
            JSONObject data = jsonjobs.getJSONObject(i);

            jobs.add(new Job((int)data.get("id"),
                    (int)data.get("duration"),
                    (int)data.get("release_date"),
                    ((BigDecimal) data.get("earliness_penalty")).doubleValue(),
                    ((BigDecimal) data.get("rejection_penalty")).doubleValue(),
                    convertJsonArray(jsonsetup.getJSONArray((int)data.get("id")))));
        }
    }

    private static int[] convertJsonArray(JSONArray array){
        int[] result = new int[array.length()];

        for (int i = 0; i < array.length();i++){
            result[i] = Integer.parseInt(""+array.get(i));
        }

        return result;
    }


}



