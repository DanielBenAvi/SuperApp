package superapp.logic;

import superapp.logic.boundaries.MiniAppCommandBoundary;

public interface MiniAppCommandWithAsyncSupport extends MiniAppCommandWithPaging{
    public Object asyncHandle(MiniAppCommandBoundary miniappBoundary);
}
