package superapp.logic.utils.convertors;

import superapp.data.MiniAppCommandEntity;
import superapp.logic.boundaries.CommandId;
import superapp.logic.boundaries.InvokedBy;
import superapp.logic.boundaries.MiniAppCommandBoundary;
import superapp.logic.boundaries.TargetObject;

public interface CommandConvertor {
    public MiniAppCommandBoundary toBoundary(MiniAppCommandEntity entity);
    public MiniAppCommandEntity toEntity(MiniAppCommandBoundary boundary);

    /** Command **/
    public CommandId commandIdToBoundary(String commandId);
    public String commandIdToEntity(CommandId commandId);
    public TargetObject targetObjectToBoundary(String targetObject);
    public String targetObjToEntity(TargetObject targetObject);
    public InvokedBy invokedByToBoundary(String invokedBy);
    public String invokedByToEntity(InvokedBy invokedBy);

}
