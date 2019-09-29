package cn.onehome.elasticsearch.impl;

import cn.onehome.elasticsearch.pojo.Item;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author: onehome
 * @version: 1.0
 * @date: 2019/9/23 14:28
 */
public interface ItemRepository extends ElasticsearchRepository<Item, Long> {
}
