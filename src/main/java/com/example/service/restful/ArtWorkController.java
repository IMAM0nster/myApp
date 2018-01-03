package com.example.service.restful;

import com.example.entity.ArtWorkRecord;
import com.example.dao.mapper.ArtworkMapper;
import com.example.service.nosql.NoSQLService;
import io.swagger.annotations.*;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by fy on 2017/12/28.
 */
@RestController
@RequestMapping(value = "/artworks")
public class ArtWorkController {

    protected static Logger logger = LoggerFactory.getLogger(ArtWorkController.class);

    @Autowired
    private NoSQLService noSQLService;

    @Autowired
    private ArtworkMapper artworkMapper;

    @ApiOperation(value = "加载所有作品", notes = "不需要任何的认证需求")
    @ApiResponses({
            @ApiResponse(code = 200, message = "success")
    })
    @RequestMapping(value = {""}, method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getArtWorkList(){
        logger.info("加载了所有的作品");
        Map<String, Object> response = new HashMap<>();
        response.put("data", artworkMapper.getAll());
        response.put("result", "success");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequiresAuthentication
    @ApiOperation(value="根据id查找作品", notes = "根据id查找作品成功200，未登陆401, 未找到404")
    @ApiImplicitParam(name = "作品 id", value = "artwork id",  required = true, dataType = "Long")
    @ApiResponses({
            @ApiResponse(code = 404, message = "not found"),
            @ApiResponse(code = 401, message = "未登录"),
            @ApiResponse(code = 200, message = "success")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getArtWorkRecord(@PathVariable  Long id){
        Map<String, Object> response = new HashMap<>();
        logger.info("try to get an artwork record");
        ArtWorkRecord artWorkRecord = artworkMapper.getById(id);
        if (artWorkRecord == null){
            response.put("result", "success");
            response.put("data", artWorkRecord);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            response.put("result", "not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @RequiresAuthentication
    @ApiOperation(value = "添加一个新作品", notes = "未登陆返回401；重名文件400；找不到文件404， 成功创建201")
    @ApiImplicitParam(name = "artwork", value="artwork info", required = true, dataType = "ArtworkRecord")
    @ApiResponses({
            @ApiResponse(code = 404, message = "文件未找到"),
            @ApiResponse(code = 400, message = "name duplicated"),
            @ApiResponse(code = 401, message = "未登录"),
            @ApiResponse(code = 201, message = "success"),
            @ApiResponse(code = 500, message = "failed")
    })
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> addArtWorkRecord(@ModelAttribute ArtWorkRecord artWorkRecord,
                                                                @PathVariable MultipartFile file){
        Session session = SecurityUtils.getSubject().getSession();
        logger.info(session.getHost()+" 试图添加一个新作品");
        logger.info(session.getHost()+"试图上传一个图片文件，文件大小为"+file.getSize());
        String user = (String)session.getAttribute("username");
        Map<String, Object> response = new HashMap<>();

        ArtWorkRecord checkDuplicated = artworkMapper.getByNameAndAuthor(user, artWorkRecord.getName());
        if(checkDuplicated != null){
            logger.info(session.getHost()+" 的操作由于试图添加重名作品而失败");
            response.put("result", "name duplicated");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (!file.isEmpty()) {
            try {
                noSQLService.insertFile("gallery", "pic",
                        user+"_"+artWorkRecord.getName(), file.getInputStream());
                logger.info("文件上传成功");
                artWorkRecord.setDate(new Date());
                artWorkRecord.setAuthor(user);
                artworkMapper.insert(artWorkRecord);
                logger.info(session.getHost()+" 成功添加了一个新作品");
                response.put("result", "success");
                return new ResponseEntity<>(response, HttpStatus.CREATED);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                logger.info("文件未找到，上传失败,");
                response.put("result", "文件未找到");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            } catch (IOException e){
                e.printStackTrace();
                logger.error("文件上传失败");
                response.put("result", "failed");
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } else {
            logger.info("上传失败，因为文件是空的.");
            response.put("result", "empty file");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @RequiresAuthentication
    @ApiOperation(value = "更新作品信息", notes="修改作品成功200，未登录401；没权限403；作品不存在404")
    @ApiImplicitParam(name = "artwork record", value = "artwork record", required = true, dataType = "ArtworkRecord")
    @ApiResponses({
            @ApiResponse(code = 404, message = "该作品不存在"),
            @ApiResponse(code = 403, message = "您没有修改作品的权限"),
            @ApiResponse(code = 401, message = "未登录")
    })
    @RequestMapping(value="/", method = RequestMethod.PUT)
    public ResponseEntity<Map<String, Object>> updateArtworkRecord(@ModelAttribute ArtWorkRecord artWorkRecord){
        Map<String, Object> response = new HashMap<>();

        Subject subject = SecurityUtils.getSubject();

        if(artworkMapper.getById(artWorkRecord.getId()) == null){
            logger.info(subject.getSession().getHost()+" 试图修改一个不存在的作品");
            response.put("result", "该作品不存在");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        if(!subject.getSession().getAttribute("username").equals(artWorkRecord.getAuthor())){
            logger.info(subject.getSession().getHost()+" 试图修改不属于自己的作品");
            response.put("result", "您没有修改作品的权限");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        artWorkRecord.setDate(new Date());
        artworkMapper.update(artWorkRecord);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequiresAuthentication
    @ApiOperation(value = "删除一个作品", notes = "删除不存在的文件204；未登录401；没权限403；成功200")
    @ApiImplicitParam(name = "目标作品 id", value = "delete artwork", required = true, dataType = "Long")
    @ApiResponses({
            @ApiResponse(code = 204, message = "试图删除一个不存在的作品"),
            @ApiResponse(code = 403, message = "没有权限删除一个作品"),
            @ApiResponse(code = 401, message = "未登录")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Map<String, Object>> deleteArtwork(@PathVariable Long id){
        Map<String, Object> response = new HashMap<>();
        ArtWorkRecord artWorkRecord = artworkMapper.getById(id);
        Subject subject = SecurityUtils.getSubject();
        if(artWorkRecord == null){
            logger.info(subject.getSession().getHost() + " 试图删除一个不存在的作品");
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        }
        if(!subject.hasRole("admin") &&
                !subject.getSession().getAttribute("username").equals(artWorkRecord.getAuthor())){
            logger.info(subject.getSession().getHost()+" 没有权限删除一个作品");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }
        artworkMapper.delete(artWorkRecord);
        logger.info(subject.getSession().getHost()+" 成功删除一个作品");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
