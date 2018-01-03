package com.example.dao.mapper;

import com.example.entity.ArtWorkRecord;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

/**
 * Created by fy on 2017/12/28.
 */

public interface ArtworkMapper {
    @Select("SELECT * FROM artwork")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "author", column = "author"),
            @Result(property = "date", column = "date", javaType = Date.class),
            @Result(property = "description", column = "description"),
            @Result(property = "name", column = "name")
    })
    List<ArtWorkRecord> getAll();

    @Select("SELECT * FROM artwork WHERE id = #{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "author", column = "author"),
            @Result(property = "date", column = "date", javaType = Date.class),
            @Result(property = "description", column = "description"),
            @Result(property = "name", column = "name")
    })
    ArtWorkRecord getById(Long id);

    @Insert("INSERT INTO artwork(author, date, description, name) VALUES(#{author}, #{date}, #{description}, #{name})")
    void insert(ArtWorkRecord artWorkRecord);


    @Delete("DELETE FROM artwork WHERE id=#{id}")
    void delete(ArtWorkRecord artWorkRecord);

    @Update("UPDATE artwork SET (author, date, description, name) = (#{author}, #{date}, #{description}, #{name}) " +
            "WHERE id=#{id}")
    void update(ArtWorkRecord artWorkRecord);

    @Select("SELECT * FROM artwork WHERE author=#{user} AND name=#{name}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "author", column = "author"),
            @Result(property = "date", column = "date", javaType = Date.class),
            @Result(property = "description", column = "description"),
            @Result(property = "name", column = "name")
    })
    ArtWorkRecord getByNameAndAuthor(@Param("user") String user, @Param("name") String name);
}
