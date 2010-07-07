package example;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;

public class AddFriends extends Base {
    public AddFriends(final PageParameters parameters) {
        super(parameters);
    }

    public boolean queryFriend(final String query) {
        //User friend = USER.get(query);
        return true;
    }
}