package superapp.miniapps.chat.command.impl;

import org.springframework.stereotype.Component;
import superapp.logic.boundaries.MiniAppCommandBoundary;
import superapp.miniapps.chat.command.ChatCommand;

@Component
public class ChatSendMessageCommand implements ChatCommand {

    @Override
    public Object execute(MiniAppCommandBoundary commandBoundary) {
        return null;
    }
}
