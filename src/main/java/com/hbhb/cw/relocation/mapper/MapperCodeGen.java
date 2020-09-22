package com.hbhb.cw.relocation.mapper;

import org.beetl.core.Template;
import org.beetl.sql.core.db.TableDesc;
import org.beetl.sql.core.kit.GenKit;
import org.beetl.sql.ext.gen.CodeGen;
import org.beetl.sql.ext.gen.GenConfig;
import org.beetl.sql.ext.gen.SourceGen;

import java.io.IOException;

/**
 * @author xiaokang
 * @since 2020-09-22
 */
public class MapperCodeGen implements CodeGen {
    String pkg;
    private String mapperTemplate;

    public MapperCodeGen() {
        this.pkg = null;
        this.mapperTemplate = "";
        this.mapperTemplate = (new GenConfig()).getTemplate("/gen/mapper.btl");
    }

    public MapperCodeGen(String pkg) {
        this();
        this.pkg = pkg;
    }

    public String getMapperTemplate() {
        return this.mapperTemplate;
    }

    public void setMapperTemplate(String mapperTemplate) {
        this.mapperTemplate = mapperTemplate;
    }

    @Override
    public void genCode(String entityPkg, String entityClass, TableDesc tableDesc, GenConfig config, boolean isDisplay) {
        if (this.pkg == null) {
            this.pkg = entityPkg;
        }

        Template template = SourceGen.getGt().getTemplate(this.mapperTemplate);
        String mapperClass = entityClass + "Mapper";
        template.binding("className", mapperClass);
        template.binding("package", this.pkg);
        template.binding("entityClass", entityClass);
        String mapperHead = "import " + entityPkg + ".*;" + SourceGen.CR;
        template.binding("imports", mapperHead);
        String mapperCode = template.render();
        if (isDisplay) {
            System.out.println(mapperCode);
        } else {
            try {
                SourceGen.saveSourceFile(GenKit.getJavaSRCPath(), this.pkg, mapperClass, mapperCode);
            } catch (IOException var11) {
                throw new RuntimeException("mapper代码生成失败", var11);
            }
        }

    }
}
