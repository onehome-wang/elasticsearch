package cn.onehome.elasticsearch;

import cn.onehome.elasticsearch.config.IndexConfig;
import cn.onehome.elasticsearch.impl.ItemRepository;
import cn.onehome.elasticsearch.pojo.DynamicIndex;
import cn.onehome.elasticsearch.pojo.Item;
import com.carrotsearch.hppc.cursors.ObjectObjectCursor;
import org.elasticsearch.action.admin.indices.alias.exists.AliasesExistResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.index.query.*;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.ReindexAction;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.AliasQuery;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ElasticsearchApplicationTests {

    @Autowired
    private ElasticsearchTemplate template;

    @Autowired
    private ItemRepository repository;

    @Test
    public void createIndex() {
        boolean index = template.createIndex(DynamicIndex.class);
        boolean b = template.putMapping(DynamicIndex.class);
        System.out.println(index);
        System.out.println(b);
    }

    @Test
    public void deleteIndex() {
        boolean b = template.deleteIndex("dynamic-20191009100347");
        System.out.println(b);
    }

    @Test
    public void addData() {
        Item 金蝶云苍穹 = repository.save(new Item(1L, "金蝶云苍穹"));
        Item 金蝶云苍穹_eas = repository.save(new Item(2L, "金蝶云苍穹 EAS"));
        System.out.println(金蝶云苍穹);
        System.out.println(金蝶云苍穹_eas);
    }

    @Test
    public void findOne() {
        CommonTermsQueryBuilder commonTermsQueryBuilder = QueryBuilders.commonTermsQuery("category.my_pinyin", "金蝶云苍穹");
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("category.keyword", "金蝶云苍穹");
        FuzzyQueryBuilder fuzzyQueryBuilder = QueryBuilders.fuzzyQuery("category", "云");
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("category.my_pinyin", "金蝶云苍穹");
        Iterable<Item> search = repository.search(matchQueryBuilder);
        for (Item item : search) {
            String category = item.getCategory();
            System.out.println(category);
        }
    }

    /**
     * 使用ES自带插件进行重建索引
     */
    @Test
    public void reindex() {
        Client client = template.getClient();
        template.createIndex(DynamicIndex.class);
        template.putMapping(DynamicIndex.class);
        BulkByScrollResponse item =
                ReindexAction.INSTANCE.newRequestBuilder(client).source("my-alias")
                        .destination("dynamic-" + IndexConfig.getSuffix()).get();
        List<String> indexNames = this.getIndexName("my-alias");
        indexNames.forEach(x -> this.removeAlias(x));
        this.addAlias("dynamic-" + IndexConfig.getSuffix(), "my-alias");
        System.out.println(item.getCreated());
    }

    /**
     * 添加别名
     *
     * @param indexName
     * @param alias
     */
    public void addAlias(String indexName, String alias) {
        AliasQuery aliasQuery = new AliasQuery();
        aliasQuery.setIndexName(indexName);
        aliasQuery.setAliasName(alias);
        Boolean aBoolean = template.addAlias(aliasQuery);
        Assert.assertTrue(aBoolean);
    }

    @Test
    public void addAlias() {
        this.addAlias("dynamic-20191009164342", "my-alias");
    }

    /**
     * 根据索引移除别名
     *
     * @param indexName
     */
    public void removeAlias(String indexName) {
        List<AliasMetaData> metaDataList = template.queryForAlias(indexName);
        for (AliasMetaData aliasMetaData : metaDataList) {
            String alias = aliasMetaData.alias();
            AliasQuery aliasQuery = new AliasQuery();
            aliasQuery.setIndexName(indexName);
            aliasQuery.setAliasName(alias);
            Boolean aBoolean = template.removeAlias(aliasQuery);
            Assert.assertTrue(aBoolean);
        }
    }

    @Test
    public void removeAlias() {
        this.removeAlias("dynamic-20191009164342");
    }

    @Test
    public void findIndexByAlias() {
        List<String> indexName = this.getIndexName("my-alias");
        System.out.println(indexName.get(0));
    }

    /**
     * 根据索引别名查找index
     *
     * @param indexAlias
     * @return
     */
    private List<String> getIndexName(String indexAlias) {
        Client client = template.getClient();
        ArrayList indexList = new ArrayList<>(16);
        AliasesExistResponse aliasesExistResponse = client.admin().indices().prepareAliasesExist(indexAlias).execute().actionGet();
        if(!aliasesExistResponse.isExists()){
            return indexList;
        }
        ImmutableOpenMap<String, List<AliasMetaData>> aliases = client.admin().indices().prepareGetAliases().get().getAliases();
        if (aliases != null) {
            for (ObjectObjectCursor<String, List<AliasMetaData>> cursor : aliases) {
                String indexName = cursor.key;
                List<AliasMetaData> aliasList = cursor.value;
                for (AliasMetaData aliasMetaData : aliasList) {
                    if (indexAlias.equals(aliasMetaData.getAlias())) {
                        indexList.add(indexName);
                    }
                }
            }
        }
        return indexList;
    }

}
