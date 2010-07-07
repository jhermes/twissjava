package example;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;

/**
 * AddFriends has a query section for finding a user that may exist
 *  on the system, as well as an action for friending/defriending that
 *   user if they exist. A user cannot friend himself.
 */
public class AddFriends extends Base {
    public AddFriends(final PageParameters parameters) {
        super(parameters);
    }

    public boolean queryFriend(final String query) {
        //User friend = USER.get(query);
        return true;
    }
}