package superapp.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import superapp.logic.MiniAppCommandWithAsyncSupport;
import superapp.logic.boundaries.CommandId;
import superapp.logic.boundaries.MiniAppCommandBoundary;


@RestController
public class MiniAppCommandController {

    private MiniAppCommandWithAsyncSupport commandService;

    @Autowired
    public void setCommandService(MiniAppCommandWithAsyncSupport commandService) {
        this.commandService = commandService;
    }


    /**
     * @param miniAppName String
     * @param miniAppCommandBoundary MiniAppCommandBoundary
     * @return commandResult Object
     */
    @PostMapping(path = {"/superapp/miniapp/{miniAppName}"},
                consumes = {MediaType.APPLICATION_JSON_VALUE},
                produces = {MediaType.APPLICATION_JSON_VALUE})
    public Object invokeCommand(@PathVariable("miniAppName") String miniAppName,
                                @RequestParam(name = "async", defaultValue = "false") Boolean asyncFlag,
                                @RequestBody MiniAppCommandBoundary miniAppCommandBoundary) {


        miniAppCommandBoundary.setCommandId(new CommandId().setMiniapp(miniAppName));

        try {
            if (asyncFlag == null || !asyncFlag) {
                return commandService.invokeCommand(miniAppCommandBoundary);
            }
            else {

                return this.commandService.asyncHandle(miniAppCommandBoundary);
            }
        } catch (Exception exception) {
            throw exception;
        }
    }

}
