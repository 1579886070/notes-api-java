package com.zxxwl.code.merchant;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.TemplateType;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.fill.Property;
import com.baomidou.mybatisplus.generator.query.SQLQuery;
import com.zxxwl.code.CustomMySqlTypeConvert;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * mybatis-plus 代码生成
 * 依赖 freemarker
 * {@code Exception in thread "main" java.lang.NoClassDefFoundError: freemarker/template/Configuration }需要 {@code org.freemarker:freemarker}
 * <a href="https://baomidou.com/pages/981406/#%E5%8C%85%E9%85%8D%E7%BD%AE-packageconfig">https://baomidou.com/pages/981406/#%E5%8C%85%E9%85%8D%E7%BD%AE-packageconfig}</a>
 *
 * @author zhouxin
 * @author qingyu
 */
public class CodeBaseGenerator {
    //  获取当前项目路径
    static final String PROJECT_PATH = System.getProperty("user.dir");
    static final String PROJECT_PATH_PREFIX = "";
    static final String PROJECT_PACKAGE_PREFIX = "com.zxxwl.api";
    static final String PROJECT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    //  文件生成地址
    static final String FILE_GENERATION_ADDRESS = PROJECT_PATH_PREFIX + "/merchant/src/main/java/";
    //  xml存放的路径
    static final String THE_PATH_WHERE_THE_XML_IS_STORED = PROJECT_PATH_PREFIX + "/merchant/src/main/resources/mapper";

//    static final String FILE_GENERATION_ADDRESS = PROJECT_PATH_PREFIX + "/merchant/src/main/java/";
//    //  xml存放的路径
//    static final String THE_PATH_WHERE_THE_XML_IS_STORED = PROJECT_PATH_PREFIX + "/merchant/src/main/resources/mapper";

    static String packageName = "";

    /**
     * 数据源配置
     * 仅.databaseQueryClass(SQLQuery.class)时 .typeConvert(new CustomMySqlTypeConvert())才会生效
     */
    private static final DataSourceConfig.Builder DATA_SOURCE_CONFIG = new DataSourceConfig
//            .Builder("jdbc:mysql://127.0.0.1:32491/cp_xiaota?tinyInt1isBit=false&characterEncoding=utf8&serverTimezone=GMT&tinyInt1isBit=false;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;MODE=MYSQL",
            .Builder("jdbc:mysql://127.0.0.1:32491/cp_service?tinyInt1isBit=false&characterEncoding=utf8&serverTimezone=GMT&tinyInt1isBit=false",
            "root",
            "123456")
//            .typeConvert(new MySqlTypeConvert())
            .databaseQueryClass(SQLQuery.class)
            .typeConvert(new CustomMySqlTypeConvert())
            .schema("baomidou");

    public static void main(String[] args) {

//        Class<?> baseEntityClass = BaseEntity.class;
//        Class<?> baseIServiceClass = BaseIService.class;

        FastAutoGenerator.create(DATA_SOURCE_CONFIG)

                // 全局配置
                .globalConfig((scanner, builder) ->

                        builder.author(scanner.apply("请输入作者名称？"))
                                // new time
                                .dateType(DateType.TIME_PACK)
                                .outputDir(PROJECT_PATH + FILE_GENERATION_ADDRESS)
                                .commentDate(PROJECT_DATETIME_FORMAT)
                )

                // 包配置
                .packageConfig((scanner, builder) -> {
                            String moduleName = scanner.apply("请输入模块名？");
                            String parent = "";
                            if (moduleName.lastIndexOf(".") > 0) {
                                parent = "." + moduleName.substring(0, moduleName.lastIndexOf("."));
                                moduleName = moduleName.substring(moduleName.lastIndexOf(".") + 1);
                            }

                            builder.parent(PROJECT_PACKAGE_PREFIX + parent)
                                    .moduleName(moduleName)
                                    .entity("entity")
                                    .service("service")
                                    .serviceImpl("service.impl")
                                    .mapper("mapper")
                                    .xml("mapper")
                                    .controller("controller")
//                                    .other("other")
                                    .pathInfo(Collections.singletonMap(OutputFile.xml, PROJECT_PATH + THE_PATH_WHERE_THE_XML_IS_STORED + "/" + moduleName));
                        }
                )
                // 策略配置
                .strategyConfig((scanner, builder) ->

                                builder.addInclude(getTables(scanner.apply("请输入表名，多个英文逗号分隔？所有输入 all")))
                                        .controllerBuilder()
                                        .enableRestStyle()
                                        .enableHyphenStyle()
                                        .entityBuilder()
                                        .enableLombok()
                                        .naming(NamingStrategy.underline_to_camel)//数据表映射实体命名策略：默认下划线转驼峰underline_to_camel
                                        .columnNaming(NamingStrategy.underline_to_camel)//表字段映射实体属性命名规则：默认null，不指定按照naming执行
//                                .addSuperEntityColumns("id", "created_by", "created_time", "updated_by", "updated_time")
                                        .addSuperEntityColumns("created_by", "created_time", "updated_by", "updated_time")
                                        .addTableFills(new Property("status", FieldFill.INSERT))
                                        .addTableFills(new Property("createTime", FieldFill.INSERT))
                                        .addTableFills(new Property("updateTime", FieldFill.INSERT_UPDATE))
                                        .addTableFills(new Property("modifyTime", FieldFill.INSERT_UPDATE))
                                        .idType(IdType.ASSIGN_ID)
                                        .build()
                )
                // 策略配置 过滤表前缀（后缀同理，支持多个）
                .strategyConfig((builder -> builder.addTablePrefix("cp_", "ta_")))
                .strategyConfig(builder ->
                        builder.mapperBuilder()
                                .enableBaseResultMap()//启用xml文件中的BaseResultMap 生成
                                .enableBaseColumnList() // 启用xml文件中的BaseColumnList
                                .build()
                )
                .strategyConfig(builder ->
                                builder.serviceBuilder()
                                        .formatServiceFileName("%sService")//格式化 service 接口文件名称
                                        .formatServiceImplFileName("%sServiceImpl")//格式化 service 接口文件名称
                                        // controller || Action 后续
//                                .controllerBuilder().formatFileName("%sAction")
                                        .build()
                )

                /*
                    模板引擎配置，默认 Velocity 可选模板引擎 Beetl 或 Freemarker
                   .templateEngine(new BeetlTemplateEngine())
                   .templateEngine(new FreemarkerTemplateEngine())
                 */
                .templateConfig(cfg ->
                        cfg.disable(TemplateType.ENTITY)
                                .entity("/templates/base/entity.java")
                                .service("/templates/base/service.java")
                                .serviceImpl("/templates/base/serviceImpl.java")
                                .mapper("/templates/base/mapper.java")
                                .xml("/templates/base/mapper.xml")
                                .controller("/templates/base/controller.java")
                )
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();


    }

    protected static List<String> getTables(String tables) {
        return "all".equals(tables) ? Collections.emptyList() : Arrays.asList(tables.split(","));
    }

}