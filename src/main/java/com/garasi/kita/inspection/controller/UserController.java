package com.garasi.kita.inspection.controller;

import com.garasi.kita.inspection.model.UserInfo;
import com.garasi.kita.inspection.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

//@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = {"", "/", "/users"}, method = RequestMethod.GET)
    public String getUsers(Model model) {
      /*  List<UserInfo> users = userService.getUsers();
        model.addAttribute("users", users);*/
        model.addAttribute("userInfo", new UserInfo());
        return "users";
    }

    @RequestMapping(value =  {"", "/", "/users"}, method = RequestMethod.POST)
    public String createUser(Model model, @ModelAttribute UserInfo userInfo) {
        UserInfo user = userService.createUser(userInfo);
        return "redirect:/users/";
    }
}
