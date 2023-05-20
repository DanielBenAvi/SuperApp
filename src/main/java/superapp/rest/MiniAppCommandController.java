package superapp.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import superapp.logic.ASYNCSupport;
import superapp.logic.MiniAppCommandService;
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
/*
    @PostMapping(path = {"/superapp/miniapp/{miniAppName}"},
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public Object invokeCommand(@PathVariable("miniAppName") String miniAppName,
                                @RequestBody MiniAppCommandBoundary miniAppCommandBoundary) {

        miniAppCommandBoundary.getCommandId().setMiniapp(miniAppName);

        return miniAppCommandService.invokeCommand(miniAppCommandBoundary);
    }
*/

    @RequestMapping(
            path = {"/superapp/miniapp/{miniAppName}"},
            method = {RequestMethod.POST},
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public MiniAppCommandBoundary invokeCommand(@PathVariable("miniAppName") String miniAppName,
                                                @RequestParam(name="async", defaultValue = "false") boolean asyncFlag,
                                                @RequestBody MiniAppCommandBoundary miniAppCommandBoundary) {

        miniAppCommandBoundary.getCommandId().setMiniapp(miniAppName);

        try {
            if (asyncFlag) {
                return this.logic.asyncHandle((MiniAppCommandBoundary)logic.invokeCommand(miniAppCommandBoundary));
            } else
                return (MiniAppCommandBoundary)logic.invokeCommand(miniAppCommandBoundary);
            //TODO:need of casing because invoke returns Object
        } catch (RuntimeException re) {
            throw new RuntimeException("Cannot invoke command");
        }
    }

}
