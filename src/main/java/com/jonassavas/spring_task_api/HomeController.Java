package com.jonassavas.spring_task_api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller // This class should be used as a web controller for receiving web traffic
public class HomeController {

    @RequestMapping("/") //When a request goes to the root of our website, this method is called
    public String index(){
        // Return the name of the view that should be returned to the browser.
        return "index.html";
    }

}