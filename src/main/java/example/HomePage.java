package example;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.CSSPackageResource;

import org.apache.wicket.markup.html.WebPage;

/**
 * Homepage
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
