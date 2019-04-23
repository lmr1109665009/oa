package com.suneee.datasource;

import com.suneee.eas.common.datasource.DynamicDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据源配置
 */
@Configuration
@EnableScheduling
@ConfigurationProperties(prefix = "hikari")
public class DataSourceConfig {
    private List<String> datasourceList;
    private String defaultDatasource;
    private Map<String, HikariDataSource> datasourceMap;


    @Bean(name = "dataSource")
    public DataSource dataSource() {
        //按照目标数据源名称和目标数据源对象的映射存放在Map中
        Map<Object, Object> targetDataSources = new HashMap<>();
        for (String key:datasourceMap.keySet()){
            targetDataSources.put(key,datasourceMap.get(key));
        }
        //采用是想AbstractRoutingDataSource的对象包装多数据源
        DynamicDataSource dataSource = new DynamicDataSource();
        dataSource.setTargetDataSources(targetDataSources);
        //设置默认的数据源，当拿不到数据源时，使用此配置
        dataSource.setDefaultTargetDataSource(datasourceMap.get(defaultDatasource));
        return dataSource;
    }

    @Bean
    public PlatformTransactionManager txManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    public List<String> getDatasourceList() {
        return datasourceList;
    }

    public void setDatasourceList(List<String> datasourceList) {
        this.datasourceList = datasourceList;
    }

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