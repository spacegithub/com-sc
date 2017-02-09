使用方法:
1.在dao model增加插件配置
<build>
        <plugins>
            <plugin>
                <groupId>org.mybatis.generator</groupId>
                <artifactId>mybatis-generator-maven-plugin</artifactId>
                <version>1.3.2</version>
                <configuration>
                    <configurationFile>${basedir}/src/main/resources/generator/generatorConfig.xml</configurationFile>
                    <verbose>true</verbose>
                    <overwrite>true</overwrite>
                </configuration>
                <dependencies>
                    <dependency>
                        <groupId>mysql</groupId>
                        <artifactId>mysql-connector-java</artifactId>
                        <version>5.1.31</version>
                    </dependency>
                    <dependency>
                        <groupId>com.sc</groupId>
                        <artifactId>sc-mybatis-generator</artifactId>
                        <version>1.0-SNAPSHOT</version>
                    </dependency>
                </dependencies>
            </plugin>
        </plugins>
    </build>

2.在${basedir}/src/main/resources/generator/ 路径下增加generatorConfig.xml,并配置如下
<?xml version="1.0" encoding="UTF-8" ?>
<!--
  ~ Copyright (C),2015-2015. 城家酒店管理有限公司
  ~ FileName: generatorConfig.xml
  ~ Author:   zhengyong
  ~ Date:     2015-12-03 15:40:55
  ~ Description: //模块目的、功能描述
  ~ History: //修改记录 修改人姓名 修改时间 版本号 描述
  ~ <zhengyong>  <2015-12-03 15:40:55> <version>   <desc>
  -->

<!DOCTYPE generatorConfiguration PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
        "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd" >
<generatorConfiguration>

    <!--<commentGenerator>-->
    <!--<property name="suppressDate" value="true"/>-->
    <!--</commentGenerator>-->

    <!--如果你希望不生成和Example查询有关的内容，那么可以按照如下进行配置:-->
    <context id="cjia" targetRuntime="MyBatis3Simple"  defaultModelType="flat" >
        <!-- 是否去除自动生成的注释 true：是 ： false:否 -->
        <!--<property name="suppressAllComments" value="true" />-->
        <property name="beginningDelimiter" value="`"/>
        <property name="endingDelimiter" value="`"/>


        <plugin type="com.fg.commons.mybatis.generator.MapperPlugin">
            <property name="mappers" value="com.fg.commons.mapper.Mapper"/>  //此mapper是需要使用框架的https://github.com/abel533/Mapper
            <!-- caseSensitive默认false，当数据库表名区分大小写时，可以将该属性设置为true -->
            <property name="caseSensitive" value="true"/>
            <!--caseAlias 是否生成别名注解 默认是true -->
            <property name="caseAlias" value="true" />
        </plugin>

        <jdbcConnection driverClass="com.mysql.jdbc.Driver"
                        connectionURL="jdbc:mysql://10.1.41.32:3306/account_db?useUnicode=true&amp;characterEncoding=UTF-8"
                        userId="root" password="cjadmin" />
        <javaModelGenerator targetPackage="cjia.account.core.accounts.model"
                            targetProject="C:\work\workspace\pay-account\cjia-account-core-dao\src\main\java" />
        <sqlMapGenerator targetPackage="mapper.accounts"
                         targetProject="C:\work\workspace\pay-account\cjia-account-core-dao\src\main\resources" />
        <javaClientGenerator targetPackage="cjia.account.core.accounts.dao"
                             targetProject="C:\work\workspace\pay-account\cjia-account-core-dao\src\main\java" type="XMLMAPPER" >
            <property name="enableSubPackages" value="true" />
        </javaClientGenerator>

        <!--生成所有表的实力-->
        <table schema="" tableName="AccountOrderToken" domainObjectName="AccountOrderToken">
            <property name="useActualColumnNames" value="true"/>
            <generatedKey column="pid" sqlStatement="Mysql" identity="true"/>
        </table>
    </context>
</generatorConfiguration>

