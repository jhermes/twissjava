package example;

import org.apache.wicket.Request;
import org.apache.wicket.protocol.http.WebSession;

public class TwissSession extends WebSession {
    private String uname;

    public TwissSession(Request request) {
        super(request);
    }

    public String getUname() {
        return uname;
    }
    
    public void authorize(String uname){
        this.uname = uname;
    }
}
