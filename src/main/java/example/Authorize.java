package example;

import example.models.User;
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
    public Authorize(final PageParameters parameters) {
        super(parameters);
        //Make and define a LoginForm.
        add(new LoginForm("login"));
        //Make and define a RegisterForm. Same as above.
        add(new RegisterForm("signup"));
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
                //TODO : SHOW USER THE DOOR
                System.out.println("Invalid username or password.");
                return;
            }
            if (!test.comparePasswords(password)){
                //TODO : SHOW USER THE DOOR
                System.out.println("Invalid username or password.");
                return;
            }
            //TODO : LOG IN
            System.out.println("Welcome back " + username);
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
                //TODO : SHOW USER THE DOOR
                System.out.println("Username already taken.");
                return;
            }
            if (!password1.equals(password2)) {
                //TODO : SHOW USER THE DOOR
                System.out.println("Passwords do not match.");
                return;
            }
            test = new User(new_username.getBytes(), password1);
            saveUser(test);
            //TODO : REGISTER
            System.out.println("Welcome " + new_username);
        }
    }
}