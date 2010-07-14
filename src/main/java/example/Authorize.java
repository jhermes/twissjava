package example;

import example.models.User;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.PageParameters;
import org.apache.wicket.model.PropertyModel;

/**
 * Authorize allows viewers to log in as a user or create
 *  a user account.
 */
public class Authorize extends Base {
    private Boolean login;
    private Boolean register;

    public Authorize(final PageParameters parameters) {
        super(parameters);
        login = parameters.getAsBoolean("login",true);
        register = parameters.getAsBoolean("register");
        //Make and define a LoginForm.
        LoginForm l = new LoginForm("login");
        l.add(new Label("loginerror",loginErrorMsg()));
        add(l);
        //Make and define a RegisterForm. Same as above.
        RegisterForm r = new RegisterForm("signup");
        r.add(new Label("regerror",registerErrorMsg()));
        add(r);
    }

    private String loginErrorMsg() {
        //get login param
        if (!login) {
            return "Invalid username/password entered.";
        }
        return "";
    }

    private String registerErrorMsg() {
        //get regis param
        if (register != null) {
            if (register) {
                return "Username already taken.";
            }
            else {
                return "Passwords do not match.";
            }
        }
        return "";
    }

    private class LoginForm extends Form {
        private String username;
        private String password;

        public LoginForm(String id) {
            super(id);
            add(new TextField("username", new PropertyModel(this,"username")));
            add(new PasswordTextField("password", new PropertyModel(this,"password")));
        }
        @Override
        public void onSubmit() {
            User test = getUserByUsername(username);
            if (test == null) {
                setResponsePage(getPage().getClass(), new PageParameters("login=false"));
                return;
            }
            if (!test.comparePasswords(password)){
                setResponsePage(getPage().getClass(), new PageParameters("login=false"));
                return;
            }
            //TODO : LOG IN
            System.out.println("Welcome back " + username);
            setResponsePage(getPage().getClass());
        }
    }

    class RegisterForm extends Form {
        private String new_username;
        private String password1;
        private String password2;

        public RegisterForm(String id) {
            super(id);
            add(new TextField("new_username", new PropertyModel(this,"new_username")));
            add(new PasswordTextField("password1", new PropertyModel(this,"password1")));
            add(new PasswordTextField("password2", new PropertyModel(this,"password2")));
        }
        @Override
        public void onSubmit() {
            User test = getUserByUsername(new_username);
            if (test != null) {
                setResponsePage(getPage().getClass(), new PageParameters("register=true"));
                return;
            }
            if (!password1.equals(password2)) {
                setResponsePage(getPage().getClass(), new PageParameters("register=false"));
                return;
            }
            test = new User(new_username.getBytes(), password1);
            saveUser(test);
            //TODO : REGISTER
            System.out.println("Welcome " + new_username);
            setResponsePage(getPage().getClass());
        }
    }
}