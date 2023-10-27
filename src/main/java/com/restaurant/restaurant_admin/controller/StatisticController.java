package com.restaurant.restaurant_admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin/statistic")
public class StatisticController {

    @GetMapping
    public ModelAndView viewStatistic() {
        return new ModelAndView("admin/statistic/statistic");
    }
}
