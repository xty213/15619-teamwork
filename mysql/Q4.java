import java.io.*;
import java.util.*;

public class Q4 {
    public static void main(String[] args) throws IOException {
        File file = new File(args[0]);
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

        HashMap<Integer, HashMap<Integer, HashSet<Tweet>>> data = new HashMap<Integer, HashMap<Integer, HashSet<Tweet>>>();

        String line;
        while ((line = br.readLine()) != null) {
            String[] split = line.split("\t");
            int hashed_location = Math.abs(split[2].hashCode());
            System.out.println(hashed_location);
            int date = Integer.parseInt(split[3]);
            Tweet tweet = new Tweet(Long.parseLong(split[0]), hashed_location, date,
                    split[4], Integer.parseInt(split[5]));

            HashMap<Integer, HashSet<Tweet>> tmp = data.get(date);
            if (tmp == null) {
                HashSet<Tweet> hashSet = new HashSet<Tweet>();
                hashSet.add(tweet);
                HashMap<Integer, HashSet<Tweet>> hashMap = new HashMap<Integer, HashSet<Tweet>>();
                hashMap.put(hashed_location, hashSet);
                data.put(date, hashMap);
            }
            else {
                HashSet<Tweet> hashSet = tmp.get(hashed_location);
                if (hashSet == null) {
                    hashSet = new HashSet<Tweet>();
                    hashSet.add(tweet);
                    tmp.put(hashed_location, hashSet);
                }
                else {
                    hashSet.add(tweet);
                }
            }
        }

        for (int date : data.keySet()) {
            HashMap<Integer, HashSet<Tweet>> hashMap = data.get(date);
            for (int hashed_location : hashMap.keySet()) {
                int rank = 1;
                HashSet<Tweet> hashSet = hashMap.get(hashed_location);

                HashMap<String, ArrayList<Tweet>> tmp = new HashMap<String, ArrayList<Tweet>>();
                for (Tweet tweet : hashSet) {
                    ArrayList<Tweet> arrayList = tmp.get(tweet.hashtag);
                    if (arrayList == null) {
                        arrayList = new ArrayList<Tweet>();
                        arrayList.add(tweet);
                        tmp.put(tweet.hashtag, arrayList);
                    }
                    else {
                        arrayList.add(tweet);
                    }
                }

                HashMap<Integer, ArrayList<String>> reverseHashMap = new HashMap<Integer, ArrayList<String>>();

                for (String hashtag : tmp.keySet()) {
                    Collections.sort(tmp.get(hashtag));
                    ArrayList<String> hashtags = reverseHashMap.get(tmp.get(hashtag).size());
                    if (hashtags == null) {
                        hashtags = new ArrayList<String>();
                        hashtags.add(hashtag);
                        reverseHashMap.put(tmp.get(hashtag).size(), hashtags);
                    }
                    else {
                        hashtags.add(hashtag);
                    }
                }

                ArrayList<Integer> keys = new ArrayList<Integer>();
                keys.addAll(reverseHashMap.keySet());
                Collections.sort(keys);
                Collections.reverse(keys);
                for (int size : keys) {
                    if (reverseHashMap.get(size).size() > 1) {
                        String[] hashtags = new String[reverseHashMap.get(size).size()];
                        reverseHashMap.get(size).toArray(hashtags);
                        for (int i = hashtags.length - 1; i >= 0; i--)
                            for (int j = 0; j < i; j++) {
                                if (tmp.get(hashtags[j]).get(0).tweet_id > tmp.get(hashtags[j+1]).get(0).tweet_id) {
                                    String t = hashtags[j];
                                    hashtags[j] = hashtags[j+1];
                                    hashtags[j+1] = t;
                                }
                                else if (tmp.get(hashtags[j]).get(0).tweet_id == tmp.get(hashtags[j+1]).get(0).tweet_id) {
                                    if (tmp.get(hashtags[j]).get(0).index > tmp.get(hashtags[j+1]).get(0).index) {
                                        String t = hashtags[j];
                                        hashtags[j] = hashtags[j+1];
                                        hashtags[j+1] = t;
                                    }
                                }
                            }

                        for (String hashtag : hashtags) {
                            System.out.print("" + rank + date + hashed_location + "\t");
                            rank++;
                            System.out.print(hashtag + ":<");
                            boolean first = true;
                            for (Tweet tweet : tmp.get(hashtag)) {
                                if (!first) {
                                    System.out.print(",");
                                }
                                first = false;
                                System.out.print(tweet.tweet_id);
                            }
                            System.out.print(">\n");
                        }
                    }
                    else {
                        System.out.print(""+ rank + date + hashed_location + "\t");
                        rank++;
                        System.out.print(reverseHashMap.get(size).get(0) + ":<");
                        boolean first = true;
                        for (Tweet tweet : tmp.get(reverseHashMap.get(size).get(0))) {
                            if (!first) {
                                System.out.print(",");
                            }
                            first = false;
                            System.out.print(tweet.tweet_id);
                        }
                        System.out.print(">\n");
                    }
                }



            }
        }

    }

    static class Tweet implements Comparable<Tweet> {
        long tweet_id;
        int hashed_location;
        int date;
        String hashtag;
        int index;

        public Tweet(long tweet_id, int hashed_location, int date, String hashtag, int index) {
            this.tweet_id = tweet_id;
            this.hashed_location = hashed_location;
            this.date = date;
            this.hashtag = hashtag;
            this.index = index;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Tweet tweet = (Tweet) o;

            if (tweet_id != tweet.tweet_id) return false;
            if (!hashtag.equals(tweet.hashtag)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = (int) (tweet_id ^ (tweet_id >>> 32));
            result = 31 * result + hashtag.hashCode();
            return result;
        }

        @Override
        public int compareTo(Tweet o) {
            if (tweet_id < o.tweet_id)
                return -1;
            return 1;
        }
    }
}
