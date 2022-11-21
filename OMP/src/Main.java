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

        String resourceName = "./OMP/src/resources/TOY-20-10.json";
        resourceName = "./OMP/file.json";

        JobScheduler scheduler = JsonReader.createJobSchedulerFromFile(resourceName);

        System.out.println("Cost of " + resourceName + ": " + String.valueOf(scheduler.getCost()));
        JsonWriter out = new JsonWriter("test", scheduler.getSchedule(), scheduler.getSetups(), scheduler.getCost());
        out.writeSolutionToJson("./OMP");
    }
}



