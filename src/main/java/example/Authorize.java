package example;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.PageParameters;

public class Authorize extends Base {
    public Authorize(final PageParameters parameters) {
        super(parameters);
        Form loginform = new Form("signin");
        loginform.add(new TextField("username"));
        loginform.add(new TextField("password"));
        add(loginform);

        Form registerform = new Form("signup");
        registerform.add(new TextField("new_username"));
        registerform.add(new TextField("password1"));
        registerform.add(new TextField("password2"));
        add(registerform);
    }

    public void loginUser() {

    }

    public void registerUser() {
        
    }
}