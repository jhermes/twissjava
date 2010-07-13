package example;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.basic.Label;

/**
 * AddFriends has a query section for finding a user that may exist
 *  on the system, as well as an action for friending/defriending that
 *   user if they exist. A user cannot friend himself.
 */
public class AddFriends extends Base {
    private String query;
    private String username;

    public AddFriends(final PageParameters parameters) {
        super(parameters);
        query = parameters.getString("query");
        if (query == null) {
            query = "";
        }
        add(new FriendForm("friendfinder"));
        add(new ActionFriendForm("actionfriend"));
        WebMarkupContainer action = new WebMarkupContainer("action") {
            public boolean isVisible() {
                return !query.equals("");
            }
        };
        boolean friend = parameters.getAsBoolean("friend",false);
        if (friend) {
            action.add(new Label("actionname","Remove Friend"));
        }
        else {
            action.add(new Label("actionname","Add Friend"));
        }
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
            //TODO : Find that friend!  
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
            //TODO : Add or Remove that Friend!
        }
    }
}