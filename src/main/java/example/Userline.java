package example;

import java.util.List;

import example.models.Timeline;
import example.models.Tweet;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.PropertyModel;
import org.wyki.cassandra.pelops.UuidHelper;

/**
 * This is the typical twitter page. A form for submitting a 140-character
 *  tweet, and all the tweets that user has made and tweets from everyone
 *  that he is following.
 */
public class Userline extends HomePage {
    private String username;

    public Userline(final PageParameters parameters) {
        super(parameters);
        //TODO : get username from session, redirect if not logged in
        username = "test";
        if (username == null) {
            setRedirect(true);
            setResponsePage(Publicline.class);
        }

        add(new TweetForm("poster"));

        Timeline timeline = getTimeline(username);
        List<Tweet> x = timeline.getView();
        if (x.size() > 0) {
            //TODO : Render tweets to the page
            Long linktopaginate = timeline.getNextview();
            if (linktopaginate != null) {
                //TODO : Link the pagination
            }
        }
        else {
            //TODO : Render "no tweets yet!"
        }
    }

    private class TweetForm extends Form {
        private String tweetbody;

        public TweetForm(String id) {
            super(id);
            add(new TextArea("tweetbody", new PropertyModel(this,"tweetbody")));
        }
        @Override
        public void onSubmit() {
            saveTweet(new Tweet(UuidHelper.newTimeUuid().toString().getBytes(), username, tweetbody));
        }
    }
}
