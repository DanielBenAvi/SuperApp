package superapp.logic.utils.convertors.impl;

import org.springframework.stereotype.Component;
import superapp.data.MiniAppCommandEntity;
import superapp.logic.boundaries.*;
import superapp.logic.utils.convertors.CommandConvertor;
import superapp.logic.utils.convertors.ConvertIdsHelper;

@Component
public class ConvertMiniappCommand implements CommandConvertor {


    @Override
    public MiniAppCommandBoundary toBoundary(MiniAppCommandEntity entity) {


        return new MiniAppCommandBoundary()
                .setCommandId(this.commandIdToBoundary(entity.getCommandId()))
                .setCommand(entity.getCommand())
                .setCommandAttributes(entity.getCommandAttributes())
                .setTargetObject(this.targetObjectToBoundary(entity.getTargetObject()))
                .setInvocationTimestamp(entity.getInvocationTimestamp())
                .setInvokedBy(this.invokedByToBoundary(entity.getInvokedBy()));
    }

    @Override
    public MiniAppCommandEntity toEntity(MiniAppCommandBoundary boundary) {

        return new MiniAppCommandEntity()
                .setCommandId(this.commandIdToEntity(boundary.getCommandId()))
                .setCommand(boundary.getCommand())
                .setCommandAttributes(boundary.getCommandAttributes())
                .setInvocationTimestamp(boundary.getInvocationTimestamp())
                .setTargetObject(this.targetObjToEntity(boundary.getTargetObject()))
                .setInvokedBy(this.invokedByToEntity(boundary.getInvokedBy()));
    }

    @Override
    public CommandId commandIdToBoundary(String commandId) {

        String[] str = ConvertIdsHelper.splitConcretedIds(commandId);

        return new CommandId()
                .setSuperapp(str[0])
                .setMiniapp(str[1])
                .setInternalCommandId(str[2]);
    }

    @Override
    public String commandIdToEntity(CommandId commandId) {

        return ConvertIdsHelper
                .concatenateIds(new String[]{ commandId.getSuperapp(),
                                            commandId.getMiniapp(),
                                            commandId.getInternalCommandId() });
    }

    @Override
    public TargetObject targetObjectToBoundary(String targetObject) {

        String[] str = ConvertIdsHelper.splitConcretedIds(targetObject);

        return new TargetObject()
                .setObjectId(new ObjectId()
                                        .setSuperapp(str[0])
                                        .setInternalObjectId(str[1]));

    }

    @Override
    public String targetObjToEntity(TargetObject targetObject) {

        return ConvertIdsHelper
                .concatenateIds(new String[]{targetObject.getObjectId().getSuperapp(),
                                targetObject.getObjectId().getInternalObjectId()});
    }

    @Override
    public InvokedBy invokedByToBoundary(String invokedBy) {

        String[] str = ConvertIdsHelper.splitConcretedIds(invokedBy);

        return new InvokedBy()
                .setUserId(new UserId()
                        .setSuperapp(str[0])
                        .setEmail(str[1]));
    }

    @Override
    public String invokedByToEntity(InvokedBy invokedBy) {

        return ConvertIdsHelper
                .concatenateIds(new String[]{ invokedBy.getUserId().getSuperapp(),
                                invokedBy.getUserId().getEmail() });
    }

}
