package com.ximo.springbootes.service.impl;

import com.ximo.springbootes.form.Novel;
import com.ximo.springbootes.enums.ResultEnums;
import com.ximo.springbootes.exception.LibraryException;
import com.ximo.springbootes.service.LibraryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 朱文赵
 * 2017/9/26
 */
@Service
@Slf4j
public class LibraryServiceImpl implements LibraryService {

    @Autowired
    private TransportClient client;

    /**
     * 根据id查询方法
     * @param id
     * @return
     */
    @Override
    public Map<String, Object> get(String id) {
        this.checkId(id);
        GetResponse result = client.prepareGet("book", "novel", id).get();
        if( !result.isExists()){
            throw new LibraryException(ResultEnums.NOVEL_NOT_EXIST);
        }
        return result.getSource();
    }

    /**
     * 添加的方法
     * @param novel
     * @return
     */
    @Override
    public Map<String, Object> add(Novel novel) {
        try {
            //构建数据
            XContentBuilder content = XContentFactory.jsonBuilder()
                    .startObject()
                    .field("title", novel.getTitle())
                    .field("author", novel.getAuthor())
                    .field("word_count", novel.getWordCount())
                    .field("publish_date", novel.getPublishDate().getTime())
                    .endObject();//构建结束

            //构建索引
            IndexResponse result = this.client.prepareIndex("book","novel")
                    .setSource(content)//放入文档
                    .get();
            //由于返回的是id，所以构建一个id的map
            Map<String, Object> map = new HashMap<>();
            map.put("id", result.getId());
            return map;
        } catch (IOException e) {
            log.error("【添加novel】 io错误， novel={}, e={}", novel, e);
            throw new LibraryException(ResultEnums.UNKNOWN_ERROR);
        }
    }

    /**
     * 删除方法
     * @param id 文档的id
     */
    @Override
    public void delete(String id) {
        this.checkId(id);
        DeleteResponse result = this.client.prepareDelete("book", "novel", id)
                .get();
    }

    /**
     * 更新操作
     * @param id
     * @param novel
     */
    @Override
    public void update(String id, Novel novel){
        UpdateRequest update = new UpdateRequest("novel", "book", id);
        try {
            //构建json数据
            XContentBuilder builder = XContentFactory.jsonBuilder().
                    startObject()
                    .field("title", novel.getTitle())
                    .field("author", novel.getAuthor())
                    .field("word_count", novel.getWordCount())
                    .field("publish_date", novel.getPublishDate().getTime())
                    .endObject();
            //构建builder
            update.doc(builder);
            //执行更新
            this.client.update(update).get();
        } catch (Exception e) {
            log.error("【更新novel】 更新失败， id={}, novel={}, e={}", id, novel, e);
            throw new LibraryException(ResultEnums.UPDATE_ERROR);
        }
    }

    /**
     * 检查id是否为空
     * @param id
     */
    private void checkId(String id){
        if(StringUtils.isBlank(id)){
            throw new LibraryException(ResultEnums.ID_IS_BLANK);
        }
    }

}
