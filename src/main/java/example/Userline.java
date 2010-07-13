package example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import example.models.Timeline;
import example.models.Tweet;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
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
        List<Tweet> tweets = timeline.getView();
        if (tweets.size() > 0) {
            add(new ListView<Tweet>("tweetlist", tweets) {
                @Override
                public void populateItem(final ListItem<Tweet> listitem) {
                    listitem.add(new Link("link") {
                        @Override
                        public void onClick() {
                            //TODO : link to TWISSJAVA/user page, alias to actual page
                        }
                    }.add(new Label("tuname",listitem.getModel().getObject().getUname())));
                    listitem.add(new Label("tbody", ": " + listitem.getModel().getObject().getBody()));
                }
            }).setVersioned(false);
            Long linktopaginate = timeline.getNextview();
            if (linktopaginate != null) {
                //TODO : Link the pagination
            }
        }
        else {
            ArrayList<String> hack = new ArrayList<String>(1);
            hack.add("There are no tweets yet. Post one!");
            add(new ListView<String>("tweetlist", hack ) {
                @Override
                public void populateItem(final ListItem<String> listitem) {
                    listitem.add(new Link("link") {
                        @Override
                        public void onClick() {
                        }
                    }.add(new Label("tuname","")));
                    listitem.add(new Label("tbody", listitem.getModel().getObject()));
                }
            }).setVersioned(false);
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
            setResponsePage(getPage().getClass());
        }
    }
}
