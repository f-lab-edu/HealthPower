package com.example.HealthPower.controller;

import com.example.HealthPower.dto.JoinDTO;
import com.example.HealthPower.service.JoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller //@RestController
@RequiredArgsConstructor
@ResponseBody
public class JoinController {

    private final JoinService joinService;

    @PostMapping("/join")
    public String join(JoinDTO joinDTO, Model model) {

        try {
            joinService.join(joinDTO);
            return "home";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "join";
        }
    }
}
