package com.github.acticfox.mybatis.starter;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.github.acticfox.base.dao.QueryDAO;
import com.github.acticfox.base.dao.UpdateDAO;
import com.github.acticfox.base.mybatis.QueryDAOMyBatisImpl;
import com.github.acticfox.base.mybatis.QueryRowHandlerDAOMyBatisImpl;
import com.github.acticfox.base.mybatis.UpdateDAOMyBatisImpl;
import com.github.acticfox.base.mybatis.orm.EntityManager;

@Configuration
public class BaseMybatisAutoConfiguration {

    @Primary
    @Bean("dataSource")
    public DataSource dataSourceOne() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource") DataSource dataSource,
        @Value("classpath:mybatis-config.xml") org.springframework.core.io.Resource configLocation,
        @Value("classpath*:mapper/*-manual.xml") org.springframework.core.io.Resource[] mapperLocations)
        throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setConfigLocation(configLocation);
        sqlSessionFactoryBean.setMapperLocations(mapperLocations);
        return sqlSessionFactoryBean.getObject();
    }

    @Bean
    public DataSourceTransactionManager transactionManager(@Qualifier("dataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Primary
    @Bean("entityManager")
    public EntityManager entityManager(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory)
        throws Exception {
        EntityManager entityManager = new EntityManager();
        entityManager.setSqlSessionFactory(sqlSessionFactory);
        entityManager.setCreateTimeColumnName("createdTime");
        entityManager.setUpdateTimeColumnName("updatedTime");
        return entityManager;
    }

    @Primary
    @Bean("queryDAO")
    public QueryDAO queryDAO(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        QueryDAOMyBatisImpl queryDAOMyBatis = new QueryDAOMyBatisImpl();
        queryDAOMyBatis.setSqlSessionFactory(sqlSessionFactory);
        return queryDAOMyBatis;
    }

    @Primary
    @Bean("updateDAO")
    public UpdateDAO updateDAO(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        UpdateDAOMyBatisImpl updateDAO = new UpdateDAOMyBatisImpl();
        updateDAO.setSqlSessionFactory(sqlSessionFactory);
        return updateDAO;
    }

    @Primary
    @Bean("queryRowHandlerDAO")
    public QueryRowHandlerDAOMyBatisImpl
        queryRowHandlerDAO(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        QueryRowHandlerDAOMyBatisImpl queryRowHandlerDAOMyBatis = new QueryRowHandlerDAOMyBatisImpl();
        queryRowHandlerDAOMyBatis.setSqlSessionFactory(sqlSessionFactory);
        return queryRowHandlerDAOMyBatis;
    }

}
