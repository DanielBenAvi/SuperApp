package superapp.logic;

import superapp.logic.boundaries.MiniAppCommandBoundary;

public interface ASYNCSupport extends MiniAppCommandWithPaging{
    public MiniAppCommandBoundary asyncHandle(MiniAppCommandBoundary miniappBoundary);
}
