package superapp.miniapps.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import superapp.data.ObjectCrud;
import superapp.logic.ObjectsService;

import static superapp.miniapps.commands.DatingCommand.LIKE;

@Component
@RequiredArgsConstructor
public class CommandFactory
{
    private final ObjectCrud objectCrud;
    private final ObjectsService objectsService;

    public DatingCommand create(int commandCode, Object... params)
    {
        switch (commandCode)
        {
            case LIKE:
                return new LikeDatingProfileCommand(objectCrud, objectsService);
            default:
                return null;
        }
    }
}
