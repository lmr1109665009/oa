package com.suneee.datasource;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Created by pangkunkun on 2017/12/18.
 */
@Component
@ConfigurationProperties(prefix = "hikari")
public class DataSourceProperties {
    private String defaultDatasource;
    private Map<String, HikariDataSource> datasourceMap;

    public String getDefaultDatasource() {
        return defaultDatasource;
    }

    public void setDefaultDatasource(String defaultDatasource) {
        this.defaultDatasource = defaultDatasource;
    }

    public Map<String, HikariDataSource> getDatasourceMap() {
        return datasourceMap;
    }

    public void setDatasourceMap(Map<String, HikariDataSource> datasourceMap) {
        this.datasourceMap = datasourceMap;
    }
}