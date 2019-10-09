package cn.onehome.elasticsearch.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

/**
 * @author: onehome
 * @version: 1.0
 * @date: 2019/10/8 17:42
 */
@Document(indexName = "dynamic-#{T(cn.onehome.elasticsearch.config.IndexConfig).getSuffix()}", type = "item")
@Setting(settingPath = "/settings.json")
public class DynamicIndex {
    @Id
    private Long id;

    @MultiField(mainField = @Field(type = FieldType.Text, analyzer = "ik_max_word"), otherFields = {@InnerField(suffix = "my_pinyin", type = FieldType.Text, analyzer = "ik_pinyin_analyzer", searchAnalyzer = "ik_pinyin_analyzer"), @InnerField(suffix = "keyword", type = FieldType.Keyword)})
    private String category;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DynamicIndex() {
    }

    public DynamicIndex(Long id, String category) {
        this.id = id;
        this.category = category;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", category='" + category + '\'' +
                '}';
    }
}
