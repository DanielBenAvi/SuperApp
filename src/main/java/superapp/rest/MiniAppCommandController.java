package superapp.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import superapp.logic.ASYNCSupport;
import superapp.logic.boundaries.CommandId;
import superapp.logic.boundaries.MiniAppCommandBoundary;


@RestController
public class MiniAppCommandController {

    private ASYNCSupport logic;

    @Autowired
    public void setLogic(ASYNCSupport logic) {
        this.logic = logic;
    }


    /**
     * @param miniAppName            String
     * @param miniAppCommandBoundary MiniAppCommandBoundary
     * @return commandResult Object
     */
    @PostMapping(path = {"/superapp/miniapp/{miniAppName}"},
                consumes = {MediaType.APPLICATION_JSON_VALUE},
                produces = {MediaType.APPLICATION_JSON_VALUE})
    public Object invokeCommand(@PathVariable("miniAppName") String miniAppName,
                                @RequestParam(name = "async", defaultValue = "false") boolean asyncFlag,
                                @RequestBody MiniAppCommandBoundary miniAppCommandBoundary) {


        miniAppCommandBoundary.setCommandId(new CommandId().setMiniapp(miniAppName));
        try {
            if (asyncFlag)
                return this.logic.asyncHandle((MiniAppCommandBoundary) logic.invokeCommand(miniAppCommandBoundary));
            else
                return logic.invokeCommand(miniAppCommandBoundary);
        } catch (Exception exception) {
            throw exception;
        }
    }

}
