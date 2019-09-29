package cn.onehome.elasticsearch;

import cn.onehome.elasticsearch.impl.ItemRepository;
import cn.onehome.elasticsearch.pojo.Item;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ElasticsearchApplicationTests {

    @Autowired
    private ElasticsearchTemplate template;

    @Autowired
    private ItemRepository repository;

    @Test
    public void createIndex() {
        boolean index = template.createIndex(Item.class);
        System.out.println(index);
    }

    @Test
    public void deleteIndex() {
        boolean b = template.deleteIndex("item");
        System.out.println(b);
    }

    @Test
    public void addData() {
        Item 金蝶云苍穹 = repository.save(new Item(1L, "金蝶云苍穹"));
        System.out.println(金蝶云苍穹);
    }

}