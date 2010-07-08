package example;

import example.models.User;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.PageParameters;

/**
 * Authorize allows viewers to log in as a user or create
 *  a user account.
 */
public class Authorize extends Base {
    private TextField username;
    private PasswordTextField password;

    private TextField new_username;
    private PasswordTextField password1;
    private PasswordTextField password2;

    public Authorize(final PageParameters parameters) {
        super(parameters);
        //Make and define a LoginForm. Save the page data for getting later.
        Form loginform = new LoginForm("login");
        username = new TextField("username");
        password = new PasswordTextField("password");
        loginform.add(username);
        loginform.add(password);
        add(loginform);

        //Make and define a RegisterForm. Same as above.
        Form registerform = new RegisterForm("signup");
        new_username = new TextField("new_username");
        password1 = new PasswordTextField("password1");
        password2 = new PasswordTextField("password2");
        registerform.add(new_username);
        registerform.add(password1);
        registerform.add(password2);
        add(registerform);
    }

    class LoginForm extends Form {
        public LoginForm(String id) {
            super(id);
        }
        @Override
        public void onSubmit() {
            String uname = Authorize.this.getUsername();
            User test = getUserByUsername(uname);
            if (test == null) {
                System.out.println("Invalid username or password.");
                return;
            }
            String password = Authorize.this.getPassword();
            if (!test.comparePasswords(password)){
                System.out.println("Invalid username or password.");
                return;
            }
            this.getSession().authorize(uname);
        }
    }
    private String getUsername() {
        return username.getModelObject().toString();
    }
    private String getPassword() {
        return password.getModelObject();
    }

    class RegisterForm extends Form {
        public RegisterForm(String id) {
            super(id);
        }
        @Override
        public void onSubmit() {
            String uname = Authorize.this.getNewUsername();
            User test = getUserByUsername(uname);
            if (test != null) {
                System.out.println("Username already taken.");
                return;
            }
            String pass1 = Authorize.this.getPassword1();
            String pass2 = Authorize.this.getPassword2();
            if (!pass1.equals(pass2)) {
                System.out.println("Passwords do not match.");
                return;
            }
            test = new User(uname.getBytes(), pass1);
            saveUser(test);
            this.getSession().authorize(uname);
        }
    }
    private String getNewUsername() {
        return new_username.getModelObject().toString();
    }
    private String getPassword1() {
        return password1.getModelObject();
    }
    private String getPassword2() {
        return password2.getModelObject();
    }
}