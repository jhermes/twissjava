package example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import example.models.Timeline;
import example.models.Tweet;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;
import org.wyki.cassandra.pelops.UuidHelper;

/**
 * This is the default home page when not logged in.
 *  It contains the 40 most recent global tweets.
 */
public class Publicline extends HomePage {
    private String username = "!PUBLIC!";
    private Long nextpage;

    public Publicline(final PageParameters parameters) {
        super(parameters);
        nextpage = parameters.getAsLong("nextpage");

        Timeline timeline = getUserline(username, nextpage);
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
                nextpage = linktopaginate;
                WebMarkupContainer pagediv = new WebMarkupContainer("pagedown");
                PageForm pager = new PageForm("pageform");
                pagediv.add(pager);
                add(pagediv);
            }
            else {
                add(new WebMarkupContainer("pagedown") {
                    @Override
                    public boolean isVisible() {
                        return false;
                    }
                });
            }
        }
        else {
            ArrayList<String> hack = new ArrayList<String>(1);
            hack.add("There are no tweets yet. Log in and post one!");
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
            add(new WebMarkupContainer("pagedown") {
                @Override
                public boolean isVisible() {
                    return false;
                }
            });
        }
    }

    private class PageForm extends Form {
        public PageForm(String id) {
            super(id);
        }
        @Override
        public void onSubmit() {
            PageParameters p = new PageParameters();
            p.put("nextpage", nextpage);
            setResponsePage(getPage().getClass(), p);
        }
    }
}
