package example;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.CSSPackageResource;

import org.apache.wicket.markup.html.WebPage;

/**
 * This page points to either Publicline (if viewer is not logged in) or
 *  Userline (for the viewer that is logged in).
 */
public class HomePage extends Base {

    /**
	 * Constructor that is invoked when page is invoked without a session.
	 * 
	 * @param parameters
	 *            Page parameters
	 */
    public HomePage(final PageParameters parameters) {
        super(parameters);
    }
}
