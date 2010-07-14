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

/**
 * AddFriends has a query section for finding a user that may exist
 *  on the system, as well as an action for friending/defriending that
 *   user if they exist. A user cannot friend himself.
 */
public class AddFriends extends Base {
    private String query;
    private Boolean friend;
    private Boolean act;

    public AddFriends(final PageParameters parameters) {
        super(parameters);
        query = parameters.getString("query");
        if (query == null) {
            query = "";
        }
        friend = parameters.getAsBoolean("friend");
        act = parameters.getAsBoolean("act");
        //TODO : Remove these prints when you're done
        System.out.println("*****");
        System.out.println("query: " + query);
        System.out.println("friend: " + friend);
        System.out.println("act: " + act);
        System.out.println("*****");


        add(new FriendForm("friendfinder"));

        WebMarkupContainer action = new WebMarkupContainer("action") {
            public boolean isVisible() {
                return friend != null;
            }
        };

        //TODO : Flash notices here!
            //Act is false => "Friendship was broken."
            //Act is true => "Friendship was established."
            //friend is true => query + " is here!"
            //friend is false => query + " is not here."
            //friend is true && query == username => "You attempt to make friends with yourself and fail."

        Form aff = new ActionFriendForm("actionfriend");
        action.add(aff);
        String buttontext = "";
        if (friend != null) {
            if (friend) {
                buttontext = "Remove Friend";
            }
            else {
                buttontext = "Add Friend";
            }
        }
        Button submit = new Button("actionname");
        submit.setModelValue(new String[] {buttontext,""});
        aff.add(submit);
        add(action);
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
                //TODO : SHOW USER HAS NO FRIENDS, HA HA
            }
            else {
                PageParameters p = new PageParameters();
                p.put("query",q);
                p.put("friend",true);
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
            //TODO : Look up logged in username
            String username = "test";
            if (!friend) {
                List<String> friendname = new ArrayList<String>();
                friendname.add(query);
                removeFriends(username, friendname);
                setResponsePage(getPage().getClass(), new PageParameters("act=true"));
            }
            else {
                List<String> friendname = new ArrayList<String>();
                friendname.add(query);
                addFriends(username, friendname);
                setResponsePage(getPage().getClass(), new PageParameters("act=false"));
            }
        }
    }
}