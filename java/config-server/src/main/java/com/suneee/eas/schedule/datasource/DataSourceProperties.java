package com.suneee.eas.schedule.datasource;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Created by pangkunkun on 2017/12/18.
 */
@Component
@ConfigurationProperties(prefix = "hikari")
public class DataSourceProperties {
    private HikariDataSource master;
    private HikariDataSource slave;

    public HikariDataSource getMaster() {
        return master;
    }

    public void setMaster(HikariDataSource master) {
        this.master = master;
    }

    public HikariDataSource getSlave() {
        return slave;
    }

    public void setSlave(HikariDataSource slave) {
        this.slave = slave;
    }
}