package example;

import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.WebPage;

/**
 * This is the typical twitter page. A form for submitting a 140-character
 *  tweet, and all the tweets that user has made and tweets from everyone
 *  that he is following.
 */
public class Userline extends HomePage {
    public Userline(final PageParameters parameters) {
        super(parameters);
 //           setResponsePage(Publicline.class);
    }
}
