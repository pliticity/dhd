package pl.iticity.dbfds.controller.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.iticity.dbfds.controller.BaseController;
import pl.iticity.dbfds.model.mixins.quotation.DetailsQICMixin;
import pl.iticity.dbfds.model.project.ProjectInformationCarrier;
import pl.iticity.dbfds.service.project.PJCService;

@Controller
@RequestMapping("/project")
public class ProjectController extends BaseController {

    @Autowired
    private PJCService pjcService;

    @RequestMapping(value = "", params = {"new"}, method = RequestMethod.GET)
    public
    @ResponseBody
    String getNewProject() {
        return convertToString(ProjectInformationCarrier.class, DetailsQICMixin.class, new ProjectInformationCarrier());
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public
    @ResponseBody
    String postSaveProject(@RequestBody ProjectInformationCarrier pjc) {
        return convertToString(ProjectInformationCarrier.class, DetailsQICMixin.class, pjcService.savePJC(pjc));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public
    @ResponseBody
    String getProject(@PathVariable("id") String id) {
        return convertToString(ProjectInformationCarrier.class, DetailsQICMixin.class, pjcService.findById(id));
    }

}