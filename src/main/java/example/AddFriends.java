package example;

import java.util.ArrayList;
import java.util.List;

import example.models.User;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.protocol.http.WebSession;

/**
 * AddFriends has a query section for finding a user that may exist
 *  on the system, as well as an action for friending/defriending that
 *   user if they exist. A user cannot friend himself.
 */
public class AddFriends extends Base {
    private String username;
    private String query;
    private Boolean found;
    private Boolean act;

    public AddFriends(final PageParameters parameters) {
        super(parameters);
        username = ((TwissSession) WebSession.get()).getUname();
        
        query = parameters.getString("query");
        if (query == null) {
            query = "";
        }
        found = parameters.getAsBoolean("found");
        act = parameters.getAsBoolean("act");

        add(new FriendForm("friendfinder"));

        WebMarkupContainer action = new WebMarkupContainer("action") {
            @Override
            public boolean isVisible() {
                return (found != null) && (found) && (username != null) && (!query.equals(username));
            }
        };

        add(new Label("flash", getFlashMsg()));

        Form aff = new ActionFriendForm("actionfriend");
        action.add(aff);
        String buttontext = "";
        if (found != null) {
            if (query != username){
                if (found) {
                    buttontext = "Remove Friend";
                }
                else {
                    buttontext = "Add Friend";
                }
            }
        }
        //TODO : Button text is wrong
        Button submit = new Button("actionname");
        submit.setModelValue(new String[] {buttontext,""});
        aff.add(submit);
        add(action);
    }

    private String getFlashMsg() {
        if (act != null) {
            if (act) {
                return "Friendship was established.";
            }
            if (!act) {
                return "Friendship was broken.";
            }
        }
        if (found != null) {
            if (!found) {
                return query + " is not here.";
            }
            if (found) {
                if ((query != null) && (query.equals(username))) {
                    return "You attempt to befriend yourself and fail.";
                }
                else {
                    return query + " is here!";
                }
            }
        }
        //default nothing
        return "";
    }

    private class FriendForm extends Form {
        private String q;

        public FriendForm(String id) {
            super(id);
            add(new TextField("q", new PropertyModel(this,"q")));
        }
        @Override
        public void onSubmit() {
            User test = getUserByUsername(q);
            if (test == null) {
                PageParameters p = new PageParameters();
                p.put("query",q);
                p.put("found",false);
                setResponsePage(getPage().getClass(), p);
            }
            else {
                PageParameters p = new PageParameters();
                p.put("query",q);
                p.put("found",true);
                setResponsePage(getPage().getClass(), p);
            }
        }
    }

    //<p>Hooray, this page exists!</p>
    //<p>There was nobody with username {{ q }}</p>
    //<p>Enter a username above to see if they are on the site!</p>

    private class ActionFriendForm extends Form {
        public ActionFriendForm(String id) {
            super(id);
        }
        @Override
        public void onSubmit() {
            List<String> friendUnames = getFriendUnames(username);
            if (friendUnames.contains(query)) {
                List<String> friendname = new ArrayList<String>();
                friendname.add(query);
                removeFriends(username, friendname);
                setResponsePage(getPage().getClass(), new PageParameters("act=false"));
            }
            else {
                List<String> friendname = new ArrayList<String>();
                friendname.add(query);
                addFriends(username, friendname);
                setResponsePage(getPage().getClass(), new PageParameters("act=true"));
            }
        }
    }
}