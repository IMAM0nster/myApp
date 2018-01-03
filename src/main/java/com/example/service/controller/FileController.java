package com.example.service.controller;

import com.example.service.nosql.NoSQLService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fy on 2017/12/31.
 */
@Controller
@RequestMapping("/file")
public class FileController {

    protected  static Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private NoSQLService noSQLService;

    @RequestMapping("/download")
    public void getPicture(String fileName, HttpServletResponse response){
        InputStream inputStream = noSQLService
                .getFileStream("gallery", "pic", fileName);
        response.setContentType("img/jpg");
        try {
            OutputStream outputStream = response.getOutputStream();
            int len = 0;
            byte[] buf = new byte[1024];
            while((len = inputStream.read(buf, 0, 1024))!=-1){
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }


}
