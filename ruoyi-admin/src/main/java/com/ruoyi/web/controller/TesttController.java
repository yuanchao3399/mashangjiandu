package com.ruoyi.web.controller;


import com.ruoyi.common.annotation.RepeatSubmit;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 举报信息Controller
 * 
 * @author ruoyi
 * @date 2022-08-04
 */
@RestController
@RequestMapping("/test1/complaints")
public class TesttController
{

    /**
     * 查询举报信息列表
     */
    @GetMapping("/list")
    @RepeatSubmit
    public String list()
    {

        return "getDataTable(list)";
    }

}
