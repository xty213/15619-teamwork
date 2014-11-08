import java.io.*;
import java.util.HashMap;
import java.util.TreeSet;

public class Q3 {
    public static void main(String[] args) throws IOException {
        File file = new File(args[0]);
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

        HashMap<Long, TreeSet<Long>> retweet_map = new HashMap<Long, TreeSet<Long>>();

        String line;
        while ((line = br.readLine()) != null) {
            String[] split = line.split("\t");
            long origin_user_id = Long.parseLong(split[0]);
            long retweet_user_id = Long.parseLong(split[1]);

            TreeSet<Long> values = retweet_map.get(origin_user_id);
            if (values != null) {
                values.add(retweet_user_id);
            }
            else {
                values = new TreeSet<Long>();
                values.add(retweet_user_id);
                retweet_map.put(origin_user_id, values);
            }
        }

        for (long key : retweet_map.keySet()) {
            System.out.print(key + "\t");
            TreeSet<Long> values = retweet_map.get(key);
            for (long value : values) {
                if (retweet_map.get(value) != null && retweet_map.get(value).contains(key)) {
                    System.out.print("(" + value + ")\\n");
                }
                else {
                    System.out.print(value + "\\n");
                }
            }
            System.out.print("\n");
        }
    }
}
