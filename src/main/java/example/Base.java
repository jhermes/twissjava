package example;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.*;

import example.models.Timeline;
import example.models.Tweet;
import example.models.User;
import org.apache.cassandra.thrift.KeyRange;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.Column;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.WebPage;

import org.wyki.cassandra.pelops.Mutator;
import org.wyki.cassandra.pelops.NumberHelper;
import org.wyki.cassandra.pelops.Pelops;
import org.wyki.cassandra.pelops.Selector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base contains both the default header/footer things for the UI as
 *   well as all the shared code for all the child controllers.
 */
public abstract class Base extends WebPage {
    final static Logger log = LoggerFactory.getLogger(Base.class);

    //CLs
    final ConsistencyLevel WCL = ConsistencyLevel.ONE;
    final ConsistencyLevel RCL = ConsistencyLevel.ONE;

    //Column Family names
    final static String USERS = "User";
    final static String FRIENDS = "Friends";
    final static String FOLLOWERS = "Followers";
    final static String TWEETS = "Tweet";
    final static String TIMELINE = "Timeline";
    final static String USERLINE = "Userline";

    //UI settings
    public Base(final PageParameters parameters) {
        add(CSSPackageResource.getHeaderContribution(Base.class, "960.css"));
        add(CSSPackageResource.getHeaderContribution(Base.class, "reset.css"));
        add(CSSPackageResource.getHeaderContribution(Base.class, "screen.css"));
        add(CSSPackageResource.getHeaderContribution(Base.class, "text.css"));
    }

    //
    // SHARED CODE
    //

    //Space-savers
    private String bToS(byte[] bytes) {
        return new String(bytes, Charset.forName("UTF-8"));
    }
    private Selector makeSel() {
        return Pelops.createSelector("Twissjava Pool", "Twissandra");
    }
    private Mutator makeMut() {
        return Pelops.createMutator("Twissjava Pool", "Twissandra");
    }
    private SlicePredicate SPall(){
        return Selector.newColumnsPredicateAll(false,5000);
    }
    private Tweet makeTweet(byte[] key, List<Column> tweetcols) {
        return new Tweet(key, bToS(tweetcols.get(1).value), bToS(tweetcols.get(0).value));
    }


    //Helpers
    private List<String> getFriendOrFollowerUnames(String COL_FAM, String uname, int count) {
        Selector selector = makeSel();
        List<Column> row;
        try {
            row = selector.getColumnsFromRow(uname, COL_FAM, Selector.newColumnsPredicateAll(false, count), RCL);
        }
        catch (Exception e) {
            log.error("No record found for uname: " + uname + ", COL_FAM: " + COL_FAM);
            return Collections.emptyList();
        }
        ArrayList<String> unames = new ArrayList<String>(row.size());
        for(Column c : row) {
            unames.add(bToS(c.name));
        }
        return unames;
    }

    private Timeline getLine(String COL_FAM, String uname, String startkey, int count) {
        Selector selector = makeSel();
        List<Column> timeline;
        try {
            timeline = selector.getColumnsFromRow(uname, COL_FAM, Selector.newColumnsPredicateAll(true, count+1), RCL);
        }
        catch (Exception e) {
            log.error("Unable to retrieve timeline for uname: " + uname);
            return null;
        }
        Long mintimestamp = null;
        if (timeline.size() > count) {
            //find min timestamp
            mintimestamp = Long.MAX_VALUE;
            Column removeme = timeline.get(0); //This cannot fail. Count is 0+, and size is thus 1+. Only needed for initialization.
            for (Column c : timeline) {
                long ctime = ByteBuffer.wrap(c.name).getLong();
                if (ctime < mintimestamp) {
                    mintimestamp = ctime;
                    removeme = c;
                }
            }
            //eject column from list after saving the timestamp
            timeline.remove(removeme);
        }
        ArrayList<String> tweetids = new ArrayList<String>(timeline.size());
        for (Column c : timeline) {
            tweetids.add(bToS(c.value));
        }
        Map<String, List<Column>> unordered_tweets = Collections.emptyMap();
        try {
            unordered_tweets = selector.getColumnsFromRows(tweetids, TWEETS, SPall(), RCL);
        }
        catch (Exception e) {
            log.error("Unable to retrieve tweets from timeline for uname: " + uname);
            return null;
        }
        //Order the tweets by the ordered tweetids
        ArrayList<Tweet> ordered_tweets = new ArrayList<Tweet>(tweetids.size());
        for (String tweetid : tweetids) {
            ordered_tweets.add(makeTweet(tweetid.getBytes(),unordered_tweets.get(tweetid)));
        }
        return new Timeline(ordered_tweets, mintimestamp);
    }


    //Data Reading
    public User getUserByUsername(String uname) {
        Selector selector = makeSel();
        List<Column> usercols;
        try {
            usercols = selector.getColumnsFromRow(uname, USERS, Selector.newColumnsPredicateAll(false,5000), RCL);
        }
        catch (Exception e) {
            log.error("Cannot find user by uname: " + uname);
            return null;
        }
        if (usercols.size() == 0) {
            log.error("User does not exist: " + uname);
            return null;
        }
        return new User(uname.getBytes(), bToS(usercols.get(0).value));
    }

    public List<String> getFriendUnames(String uname) {
        return getFriendUnames(uname, 5000);
    }
    public List<String> getFriendUnames(String uname, int count) {
        return getFriendOrFollowerUnames(FRIENDS, uname, count);
    }

    public List<String> getFollowerUnames(String uname) {
        return getFollowerUnames(uname, 5000);
    }
    public List<String> getFollowerUnames(String uname, int count) {
        return getFriendOrFollowerUnames(FOLLOWERS, uname, count);
    }

    public List<User> getUsersForUnames(List<String> unames) {
        Selector selector = makeSel();
        ArrayList<User> users = new ArrayList<User>();
        Map<String, List<Column>> data;
        try {
            data = selector.getColumnsFromRows(unames, USERS, SPall(), RCL);
        }
        catch (Exception e) {
            log.error("Cannot get users for unames: " + unames);
            return users;
        }
        for (Map.Entry<String,List<Column>> row : data.entrySet()) {
            users.add(new User(row.getKey().getBytes(), bToS(row.getValue().get(0).value)));
        }
        return users;
    }

    public List<User> getFriends(String uname) {
        return getFriends(uname, 5000);
    }
    public List<User> getFriends(String uname, int count) {
        List<String> friendUnames = getFriendUnames(uname, count);
        return getUsersForUnames(friendUnames);
    }

    public List<User> getFollowers(String uname) {
        return getFollowers(uname, 5000);
    }
    public List<User> getFollowers(String uname, int count) {
        List<String> followerUnames = getFollowerUnames(uname, count);
        return getUsersForUnames(followerUnames);
    }

    public Timeline getTimeline(String uname) {
        return getTimeline(uname, "", 40);
    }
    public Timeline getTimeline(String uname, String startkey, int limit) {
        return getLine(TIMELINE, uname, startkey, limit);
    }

    public Timeline getUserline(String uname) {
        return getUserline(uname, "", 40);
    }
    public Timeline getUserline(String uname, String startkey, int limit) {
        return getLine(USERLINE, uname, startkey, limit);
    }

    public Tweet getTweet(String tweetid) {
        Selector selector = makeSel();
        List<Column> tweetcols;
        try {
            tweetcols = selector.getColumnsFromRow(tweetid, TWEETS, SPall(), RCL);
        }
        catch (Exception e) {
            log.error("Could not locate tweet for id: " + tweetid);
            return null;
        }
        //maketweet from cols and return
        return makeTweet(tweetid.getBytes(),tweetcols);
    }

    public List<Tweet> getTweetsForTweetids(List<String> tweetids) {
        Selector selector = makeSel();
        Map<String, List<Column>> data;
        ArrayList<Tweet> tweets = new ArrayList<Tweet>();
        try {
            data = selector.getColumnsFromRows(tweetids, TWEETS, SPall(), RCL);
        }
        catch (Exception e) {
            log.error("Cannot get tweets for tweetids: " + tweetids);
            return tweets;
        }
        //loop maketweet from cols and return
        for (Map.Entry<String, List<Column>> datarow : data.entrySet()) {
            tweets.add(makeTweet(datarow.getKey().getBytes(), datarow.getValue()));
        }
        return tweets;
    }


    //Data Writing
    public void saveUser(User user) {
        Mutator mutator = makeMut();
        mutator.writeColumn(bToS(user.getKey()), USERS, mutator.newColumn("password",user.getPassword()));
        try {
            mutator.execute(WCL);
        }
        catch (Exception e) {
            log.error("Unable to save user: " + bToS(user.getKey()));
        }
    }
    public void saveTweet(Tweet tweet) {
        long timestamp = System.currentTimeMillis();
        Mutator mutator = makeMut();

        //Insert the tweet into tweets cf
        String key = bToS(tweet.getKey());
        mutator.writeColumn(key, TWEETS, mutator.newColumn("uname",tweet.getUname()));
        mutator.writeColumn(key, TWEETS, mutator.newColumn("body",tweet.getBody()));
        //Insert into the user's timeline
        mutator.writeColumn(tweet.getUname(), USERLINE, mutator.newColumn(NumberHelper.toBytes(timestamp), key));
        //Insert into the public timeline
        mutator.writeColumn("!PUBLIC!", USERLINE, mutator.newColumn(NumberHelper.toBytes(timestamp), key));
        //Insert into all followers streams
        ArrayList<String> followerUnames = new ArrayList<String>(getFollowerUnames(tweet.getUname()));
        followerUnames.add(tweet.getUname());
        for (String follower : followerUnames) {
           mutator.writeColumn(follower, TIMELINE, mutator.newColumn(NumberHelper.toBytes(timestamp), key));
        }
        try {
            mutator.execute(WCL);
        }
        catch (Exception e) {
            log.error("Unable to save tweet: " + tweet.getUname() + ": " + tweet.getBody());
        }
    }

    public void addFriends(String from_uname, List<String> to_unames) {
        long timestamp = System.currentTimeMillis();
        Mutator mutator = makeMut();
        ArrayList<Column> friends = new ArrayList<Column>();
        for (String uname : to_unames) {
            friends.add(mutator.newColumn(uname, String.valueOf(timestamp)));
            mutator.writeColumn(uname, FOLLOWERS, mutator.newColumn(from_uname, String.valueOf(timestamp)));
        }
        mutator.writeColumns(from_uname, FRIENDS, friends);
        try {
            mutator.execute(WCL);
        }
        catch (Exception e) {
            log.error("Unable to add friendship from: " + from_uname + ", to: " + to_unames);
        }
    }

    public void removeFriends(String from_uname, List<String> to_unames) {
        Mutator mutator = makeMut();
        for (String uname : to_unames) {
            mutator.deleteColumn(from_uname, FRIENDS, uname);
            mutator.deleteColumn(uname, FOLLOWERS, from_uname);
        }
    }
    
}