package com.sunsheen.entity;


/**
 * @Description: (配置信息实现)
 * @author: SLM
 * @date: 2020年10月5日 上午10:05:57
 **/
@SuppressWarnings("all")
public class SwaggerConfigBuilder {


    private SwaggerConfig swaggerConfig;

    public static SwaggerConfigBuilder newInstance() {
        return new SwaggerConfigBuilder();
    }

    public SwaggerConfigBuilder() {
        this.swaggerConfig = new SwaggerConfig();
    }


    public SwaggerConfigBuilder enable(boolean enable) {
        swaggerConfig.setEnable(enable);
        return this;
    }

    public SwaggerConfigBuilder description(String description) {
        swaggerConfig.setDescription(description);
        return this;
    }

    public SwaggerConfigBuilder scanner(String scanner) {
        swaggerConfig.setScanner(scanner);
        return this;
    }

    public SwaggerConfigBuilder title(String title) {
        swaggerConfig.setTitle(title);
        return this;
    }

    public SwaggerConfigBuilder url(String url) {
        swaggerConfig.setUrl(url);
        return this;
    }

    public SwaggerConfig build() {
        return swaggerConfig;
    }
}
