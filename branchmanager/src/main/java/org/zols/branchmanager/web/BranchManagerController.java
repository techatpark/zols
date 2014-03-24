/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.branchmanager.web;

import com.mangofactory.swagger.annotations.ApiIgnore;
import org.zols.branchmanager.BranchManager;
import org.zols.branchmanager.domain.Branch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 * @author monendra_s
 */
@Controller
public class BranchManagerController {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(BranchManagerController.class);

    @Autowired
    BranchManager branchManager;

    @RequestMapping(value = "/api/branches", method = POST)
    @ApiIgnore
    @ResponseBody
    public Branch create(@RequestBody Branch branch) {
        LOGGER.info("Creating new branch {}", branch);
        return branchManager.add(branch);
    }

    @RequestMapping(value = "/api/branches/{name}", method = PUT)
    @ApiIgnore
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@PathVariable(value = "name") String name,
            @RequestBody Branch branch) {
        LOGGER.info("Updating language with id {} with {}", name, branch);
        if (name.equals(branch.getName())) {
            branchManager.update(branch);
        }
    }

    @RequestMapping(value = "/api/branches/{name}", method = DELETE)
    @ApiIgnore
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(value = "name") String name) {
        LOGGER.info("Deleting branch with id {}", name);
        branchManager.delete(name);
    }

    @RequestMapping(value = "/branches/{name}", method = GET)
    @ApiIgnore
    public String edit(@PathVariable(value = "name") String name, Model model) {
        model.addAttribute("branch", branchManager.get(name));
        return "org/zols/branchmanager/branch";
    }

    @RequestMapping(value = "/branches/add", method = GET)
    @ApiIgnore
    public String add(Model model) {
        model.addAttribute("branch", new Branch());
        return "org/zols/branchmanager/branch";
    }

    @RequestMapping(value = "/api/branches", method = GET)
    @ApiIgnore
    @ResponseBody
    public Page<Branch> list(
            Pageable page) {
        LOGGER.info("Listing branch");
        return branchManager.list(page);
    }

    @RequestMapping(value = "/branches", method = GET)
    @ApiIgnore
    public String listing() {
        return "org/zols/branchmanager/listbranches";
    }

}
