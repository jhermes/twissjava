package example;

import org.apache.wicket.protocol.http.WebSession;

public class TwissSession extends WebSession {
    private String uname;

    public TwissSession(org.apache.wicket.Request request) {
        super(request);
        uname = null;
    }

    public String getUname() {
        return uname;
    }
    
    public void authorize(String uname){
        this.uname = uname;
    }
}
