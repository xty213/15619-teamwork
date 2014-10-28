package phase1;

import org.json.JSONObject;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

public class Process {
    public static void main(String[] args) throws IOException, ParseException {
        String line;

        HashMap<String, Integer> afinn = new HashMap<String, Integer>();
        File afinnFile = new File("./afinn.txt");
        BufferedReader afinnBufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(afinnFile)));

        while ((line = afinnBufferedReader.readLine()) != null) {
            String[] split = line.split(",");
            String word = split[0];
            int score = Integer.parseInt(split[1]);
            afinn.put(word, score);
        }
        afinnBufferedReader.close();

        HashSet<String> banned = new HashSet<String>();
        File bannedFile = new File("./banned.txt");
        BufferedReader bannedBufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(bannedFile)));

        while ((line = bannedBufferedReader.readLine()) != null) {
            banned.add(line);
        }

        File file = new File(args[0]);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));

        SimpleDateFormat originalSDF = new SimpleDateFormat("EEE MMM dd HH:mm:ss +0000 yyyy");
        SimpleDateFormat targetSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        while ((line = bufferedReader.readLine()) != null) {
            if (line.length() == 0)
                continue;
            JSONObject jsonObject = new JSONObject(line);
            String time = jsonObject.getString("created_at");
            String userId = jsonObject.getJSONObject("user").getString("id_str");
            String text = jsonObject.getString("text");
            String tweetId = jsonObject.getString("id_str");

            Date date = originalSDF.parse(time);
            String targetTime = targetSDF.format(date);
            long timestamp = date.getTime() / 1000;

            int score = 0;
            StringBuilder stringBuilder = new StringBuilder();
            String[] words = text.split("(?=[^a-zA-Z0-9])|(?<=[^a-zA-Z0-9])");
            for (String word : words) {
                if (word.length() == 0) {
                    stringBuilder.append(word);
                    continue;
                }
                char first = word.charAt(0);
                if (!((first >= 'a' && first <= 'z') || (first >= 'A' && first <= 'Z') || (first >= '0' && first <= '9'))) {
                    stringBuilder.append(word);
                    continue;
                }
                if (afinn.containsKey(word.toLowerCase())) {
                    score += afinn.get(word.toLowerCase());
                }
                if (banned.contains(word.toLowerCase())) {
                    stringBuilder.append(word.charAt(0));
                    for (int i = 0; i < word.length() - 2; i++)
                        stringBuilder.append("*");
                    stringBuilder.append(word.charAt(word.length() - 1));
                }
                else {
                    stringBuilder.append(word);
                }
            }
            String censoredText = stringBuilder.toString();
            censoredText = censoredText.replace("\t", "\\t").replace("\n", "\\n").replace("\r", "\\r");
            System.out.println(tweetId + "\t" + userId + "\t" + targetTime + "\t" + timestamp + "\t" + score + "\t" + censoredText);
        }
        bufferedReader.close();
    }
}
