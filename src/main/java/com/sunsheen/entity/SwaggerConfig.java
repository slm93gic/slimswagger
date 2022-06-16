package com.sunsheen.entity;

/**
 * @Description: (配置信息实体)
 * @author: SLM
 * @date: 2020年10月5日 上午10:05:57
 **/
public class SwaggerConfig {

    /**
     * 是否启用
     */
    private boolean enable = true;

    /**
     * 功能描述
     */
    private String description = "接口管理工具";

    /**
     * 扫描的包
     */
    private String scanner = "com.sunsheen";

    /**
     * 标题
     */
    private String title = "http://localhost:8888";

    /**
     * 访问地址
     */
    private String url;


    public boolean isEnable() {
        return enable;
    }

    public String getDescription() {
        return description;
    }

    public String getScanner() {
        return scanner;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setScanner(String scanner) {
        this.scanner = scanner;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "SwaggerConfig{" +
                "enable=" + enable +
                ", description='" + description + '\'' +
                ", scanner='" + scanner + '\'' +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
