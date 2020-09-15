package lab.BasicClasses;

public class User {
    private String name;
    private String password;
    public User(String name, String password){
        this.name=name;
        this.password=password;
    }

    public String getPass() {
        return password;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
