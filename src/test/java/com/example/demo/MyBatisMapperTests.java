package com.example.demo;

import com.example.dao.mapper.ArtworkMapper;
import com.example.entity.ArtWorkRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

/**
 * Created by fy on 2017/12/28.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class MyBatisMapperTests {

    @Autowired
    private ArtworkMapper artworkMapper;

    @Test
    public void testGetArtwork(){
        ArtWorkRecord artWorkRecord = artworkMapper.getById(1l);
        System.out.println("test get"+artWorkRecord.getAuthor());
    }

    @Test
    public void testInsertArtwork(){
        ArtWorkRecord newRecord = new ArtWorkRecord();
        newRecord.setAuthor("陈二狗");
        newRecord.setDate(new Date());
        newRecord.setDescription("毛都百度不出来");
        newRecord.setName("真的秀");
        artworkMapper.insert(newRecord);
    }
}
