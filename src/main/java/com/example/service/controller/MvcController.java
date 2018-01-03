package com.example.service.controller;

import com.example.dao.mapper.ArtworkMapper;
import com.example.entity.ArtWorkRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by fy on 2017/12/29.
 */
@Controller
public class MvcController {

    protected static Logger logger = LoggerFactory.getLogger(MvcController.class);

    @Autowired
    private ArtworkMapper artworkMapper;

    @RequestMapping("/toLogin")
    public String login(){
        return "login";
    }

    @RequestMapping(value = "/toEdit", method = RequestMethod.GET)
    public String edit(Long id, Model model){
        ArtWorkRecord artWorkRecord = artworkMapper.getById(id);
        if(artWorkRecord == null)
            return "redirect:/";
        model.addAttribute("artwork", artWorkRecord);
        model.addAttribute("fileUrl", "/file/download?fileName="+artWorkRecord.getAuthor()+"_"+artWorkRecord.getName());
        return "edit";
    }
}
