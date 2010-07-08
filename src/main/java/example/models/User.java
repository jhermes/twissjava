package example.models;

public class User {
    private byte[] key;
    private String password;

    public User(final byte[] key, final String password) {
        this.key = key;
        //Do not store this in plain text.
        this.password = password;
    }

    public byte[] getKey() {
        return key;
    }

    public String getPassword() {
        return password;
    }

    public boolean comparePasswords(final String compare) {
        //Make this a constant time comparison please.
        return password.equals(compare);
    }
}
