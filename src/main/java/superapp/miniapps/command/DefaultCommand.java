package superapp.miniapps.command;

import org.springframework.stereotype.Component;
import superapp.logic.boundaries.MiniAppCommandBoundary;

@Component
public class DefaultCommand implements MiniAppsCommand{
    @Override
    public Object execute(MiniAppCommandBoundary commandBoundary) {
        return commandBoundary;
    }
}
