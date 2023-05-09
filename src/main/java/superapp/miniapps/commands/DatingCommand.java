package superapp.miniapps.commands;

public interface DatingCommand {
    int LIKE = 1;
    int UNKNOWN = 2;

    public Object execute();
}
