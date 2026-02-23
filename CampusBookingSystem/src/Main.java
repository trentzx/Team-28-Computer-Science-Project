import model.users.*;
import service.DataLoader;

public class Main {
    public static void main(String[] args) {
        DataLoader loader = new DataLoader();
        loader.loadUsers("users.csv");

        for (User u : loader.getUsers()) {
            System.out.println(u);
        }
    }
}