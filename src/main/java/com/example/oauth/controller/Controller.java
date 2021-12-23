package com.example.oauth.controller;


import com.example.oauth.dao.UserRepository;
import com.example.oauth.dto.UserDto;
import com.example.oauth.entry.User;
import com.example.oauth.service.MailService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
public class Controller {

    @Autowired
    UserRepository userRepository;

    @Autowired
    MailService mailService;

    @ApiOperation(value = "error handling")
    @GetMapping("/error")
    public String error(HttpServletRequest request) {
        String message = (String) request.getSession().getAttribute("error.message");
        request.getSession().removeAttribute("error.message");
        return message;
    }

    @ApiOperation(value = "account register")
    @GetMapping("/register")
    public String register(UserDto dto) {
        //註冊
        if (null == dto.getEmail()) {
            return "";
        }
        User user = userRepository.findByEmail(dto.getEmail());
        if (null == user) {
            BeanUtils.copyProperties(dto, user);
            userRepository.save(user);
            return "success";
        }
    }

    @ApiOperation(value = "get users")
    @GetMapping("/user")
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
        String name = principal.getAttribute("name");
        String email = principal.getAttribute("email");
        String password = principal.getAttribute("password");

        User user = userRepository.findByEmail(email);
        if (null == user) {
            user = User.builder()
                    .name(name)
                    .email(email)
                    .password(password).build();
            userRepository.save(user);
        }
        return Map.of("name", user.getName());
    }

    @RequestMapping(value = "/username", method = RequestMethod.GET)
    @ResponseBody
    public String currentUserName(Authentication authentication) {
        return authentication.toString();
    }

    @GetMapping("/not-restricted")
    public String notRestricted() {
        return "you don't need to be logged in";
    }

    @GetMapping("/restricted")
    public String restricted() {
        return "if you see this you are logged in";
    }

}