package example;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.*;

import example.models.Tweet;
import example.models.User;
import org.apache.cassandra.thrift.KeyRange;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.Column;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.WebPage;

import org.wyki.cassandra.pelops.Pelops;
import org.wyki.cassandra.pelops.Selector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class Base extends WebPage {
    final static Logger log = LoggerFactory.getLogger(Base.class);
    final ConsistencyLevel WCL = ConsistencyLevel.ONE;
    final ConsistencyLevel RCL = ConsistencyLevel.ONE;

    //UI settings
    public Base(final PageParameters parameters) {
        add(CSSPackageResource.getHeaderContribution(HomePage.class, "960.css"));
        add(CSSPackageResource.getHeaderContribution(HomePage.class, "reset.css"));
        add(CSSPackageResource.getHeaderContribution(HomePage.class, "screen.css"));
        add(CSSPackageResource.getHeaderContribution(HomePage.class, "text.css"));
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

    private FIXME getLine(String COL_FAM, String uname, String startkey, int count) {
        Selector selector = makeSel();
        List<Column> timeline;
        try {
            timeline = selector.getColumnsFromRow(uname, COL_FAM, Selector.newColumnsPredicateAll(true, count+1), RCL);
        }
        catch (Exception e) {
            log.error("Unable to retrieve timeline for uname: " + uname);
            return Collections.emptyList();
        }
        if (timeline.size() > count) {
            //find min timestamp
            long mintimestamp = Long.MAX_VALUE;
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
        List<String> tweetids = Collections.emptyList();
        for (Column c : timeline) {
            tweetids.add(bToS(c.value));
        }
        Map<String, List<Column>> unordered_tweets = Collections.emptyMap();
        try {
            unordered_tweets = selector.getColumnsFromRows(tweetids, COL_FAM, new SlicePredicate(), RCL);
        }
        catch (Exception e) {
            log.error("Unable to retrieve tweets from timeline for uname: " + uname);
            return Collections.emptyList();
        }
        //Order the tweets by the timeline ids
        //Send the tweet name and body down to the page.
        //Send the saved timestamp down as the paginator link.
    }


    //Data Reading
    public User getUserByUsername(String uname) {
        Selector selector = makeSel();
        List<Column> usercols;
        try {
            usercols = selector.getColumnsFromRow(uname, "USERS", new SlicePredicate(), RCL);
        }
        catch (Exception e) {
            log.error("Cannot find user by uname: " + uname);
            return null;
        }
        return new User(uname.getBytes(), bToS(usercols.get(0).value));
    }

    public List<String> getFriendUnames(String uname) {
        return getFriendUnames(uname, 5000);
    }
    public List<String> getFriendUnames(String uname, int count) {
        return getFriendOrFollowerUnames("FRIENDS", uname, count);
    }

    public List<String> getFollowerUnames(String uname) {
        return getFollowerUnames(uname, 5000);
    }
    public List<String> getFollowerUnames(String uname, int count) {
        return getFriendOrFollowerUnames("FOLLOWERS", uname, count);
    }

    public List<User> getUsersForUnames(List<String> unames) {
        Selector selector = makeSel();
        List<User> users = Collections.emptyList();
        Map<String, List<Column>> data;
        try {
            data = selector.getColumnsFromRows(unames, "USER", new SlicePredicate(), RCL);
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

    public FIXME getTimeline(String uname) {
        return getTimeline(uname, "", 40);
    }
    public FIXME getTimeline(String uname, String startkey, int limit) {
        return getLine("TIMELINE", uname, startkey, limit);
    }

    public FIXME getUserline(String uname) {
        return getUserline(uname, "", 40);
    }
    public FIXME getUserline(String uname, String startkey, int limit) {
        return getLine("USERLINE", uname, startkey, limit);
    }

    public Tweet getTweet(String tweetid) {
        Selector selector = makeSel();
        List<Column> tweetcols;
        try {
            tweetcols = selector.getColumnsFromRow(tweetid, "TWEETS", new SlicePredicate(), RCL);
        }
        catch (Exception e) {
            log.error("Could not locate tweet for id: " + tweetid);
            return null;
        }
        //maketweet from cols and return
    }

    public List<Tweet> getTweetsForTweetids(List<String> tweetids) {
        Selector selector = makeSel();
        List<Tweet> tweets = Collections.emptyList();
        Map<String, List<Column>> data;
        try {
            data = selector.getColumnsFromRows(tweetids, "TWEETS", new SlicePredicate(), RCL);
        }
        catch (Exception e) {
            log.error("Cannot get tweets for tweetids: " + tweetids);
            return tweets;
        }
        //loop maketweet from cols and return
    }


    //Data Writing
    public void saveUser(User user) {

    }
    public void saveTweet(Tweet tweet) {

    }
    public void addFriends(String from_uname, List<String> to_unames) {

    }
    public void removeFriends(String from_uname, List<String> to_unames) {

    }
 /**
def save_user(uname, user):
    """
    Saves the user record.
    """
    USER.insert(str(uname), user)

def save_tweet(tweet_id, uname, tweet):
    """
    Saves the tweet record.
    """
    # Generate a timestamp for the USER/TIMELINE
    ts = _long(time.time() * 1e6)
    # Insert the tweet, then into the user's timeline, then into the public one
    TWEET.insert(str(tweet_id), tweet)
    USERLINE.insert(str(uname), {ts: str(tweet_id)})
    USERLINE.insert(PUBLIC_USERLINE_KEY, {ts: str(tweet_id)})
    # Get the user's followers, and insert the tweet into all of their streams
    follower_unames = [uname] + get_follower_unames(uname)
    for follower_uname in follower_unames:
        TIMELINE.insert(str(follower_uname), {ts: str(tweet_id)})

def add_friends(from_uname, to_unames):
    """
    Adds a friendship relationship from one user to some others.
    """
    ts = str(int(time.time() * 1e6))
    dct = OrderedDict(((str(uname), ts) for uname in to_unames))
    FRIENDS.insert(str(from_uname), dct)
    for to_uname in to_unames:
        FOLLOWERS.insert(str(to_uname), {str(from_uname): ts})

def remove_friends(from_uname, to_unames):
    """
    Removes a friendship relationship from one user to some others.
    """
    for uname in to_unames:
        FRIENDS.remove(str(from_uname), column=str(uname))
    for to_uname in to_unames:
        FOLLOWERS.remove(str(to_uname), column=str(to_uname))
*/
}