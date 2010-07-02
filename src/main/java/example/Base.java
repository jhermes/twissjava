package example;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;

public abstract class Base extends WebPage {
    public Base(final PageParameters parameters) {
        add(CSSPackageResource.getHeaderContribution(HomePage.class, "960.css"));
        add(CSSPackageResource.getHeaderContribution(HomePage.class, "reset.css"));
        add(CSSPackageResource.getHeaderContribution(HomePage.class, "screen.css"));
        add(CSSPackageResource.getHeaderContribution(HomePage.class, "text.css"));
    }
}
