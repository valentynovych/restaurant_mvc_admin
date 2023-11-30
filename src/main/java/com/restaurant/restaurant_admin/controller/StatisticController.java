package com.restaurant.restaurant_admin.controller;

import com.restaurant.restaurant_admin.model.StatisticModel;
import com.restaurant.restaurant_admin.service.StatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin/statistic")
@RequiredArgsConstructor
public class StatisticController {
    private final StatisticService statisticService;

    @GetMapping
    public ModelAndView viewStatistic() {
        return new ModelAndView("admin/statistic/statistic");
    }

    @GetMapping("/getOrderStatisticOnCharts")
    public @ResponseBody ResponseEntity<?> getOrderStatisticOnCharts() {
        return new ResponseEntity<>(statisticService.getOrderStatistic(), HttpStatus.OK);
    }

    @GetMapping("/getGlobalStatistic")
    public @ResponseBody ResponseEntity<StatisticModel> getGlobalStatistic() {
        return new ResponseEntity<>(statisticService.getGlobalStatistic(), HttpStatus.OK);
    }

    @GetMapping("/getUsersAge")
    public @ResponseBody ResponseEntity<?> getUsersAge() {
        return new ResponseEntity<>(statisticService.getUsersAge(), HttpStatus.OK);
    }

    @GetMapping("/getPopularCategory")
    public @ResponseBody ResponseEntity<?> getPopularCategory() {
        return new ResponseEntity<>(statisticService.getPopularCategory(), HttpStatus.OK);
    }


}
