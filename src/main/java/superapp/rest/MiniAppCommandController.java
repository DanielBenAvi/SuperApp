package superapp.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import superapp.logic.MiniAppCommandService;
import superapp.logic.boundaries.MiniAppCommandBoundary;


@RestController
public class MiniAppCommandController {

    private MiniAppCommandService miniAppCommandService;

    @Autowired
    public void setMiniAppCmdService(MiniAppCommandService miniAppCmdService) {
        this.miniAppCommandService = miniAppCmdService;
    }

    /**
     * @param miniAppName            String
     * @param miniAppCommandBoundary MiniAppCommandBoundary
     * @return commandResult Object
     */
    @PostMapping(path = {"/superapp/miniapp/{miniAppName}"},
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public Object invokeCommand(@PathVariable("miniAppName") String miniAppName,
                                @RequestBody MiniAppCommandBoundary miniAppCommandBoundary) {

        miniAppCommandBoundary.getCommandId().setMiniapp(miniAppName);

        return miniAppCommandService.invokeCommand(miniAppCommandBoundary);
    }

}
