package example;

import org.apache.wicket.protocol.http.WebApplication;
import org.wyki.cassandra.pelops.Pelops;
import org.wyki.cassandra.pelops.Policy;

/**
 * Application object for your web application. If you want to run this application without deploying, run the Start class.
 */
public class WicketApplication extends WebApplication {
    /**
     * Constructor
     */
	public WicketApplication() {
        Pelops.addPool("Twissjava Pool", new String[] {"localhost"}, 9160, false, "not used", new Policy());        
	}
	
	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	public Class<HomePage> getHomePage() {
		return HomePage.class;
	}

}