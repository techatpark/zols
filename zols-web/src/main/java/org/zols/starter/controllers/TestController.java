package org.zols.starter.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/test")
class TestController {
    /**
     * get all the access.
     * @return Public Content
     */
    @GetMapping("/all")
    public String allAccess() {
        return "Public Content.";
    }

    /**
     * userAccess.
     * @return User Content
     */
    @GetMapping("/user")
    public String userAccess() {
        return "User Content.";
    }

    /**
     * moderatorAccess.
     * @return Moderator Board
     */
    @GetMapping("/mod")
    public String moderatorAccess() {
        return "Moderator Board.";
    }
    /**
     * adminAccess.
     * @return Admin Board
     */
    @GetMapping("/admin")
    public String adminAccess() {
        return "Admin Board.";
    }
}
