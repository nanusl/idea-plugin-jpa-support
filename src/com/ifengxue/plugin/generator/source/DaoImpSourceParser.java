package com.ifengxue.plugin.generator.source;

import com.ifengxue.plugin.entity.Table;
import com.ifengxue.plugin.generator.config.GeneratorConfig;
import com.ifengxue.plugin.generator.config.TablesConfig;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;

public class DaoImpSourceParser implements SourceParser, VelocityEngineAware {

    private VelocityEngine velocityEngine;
    private String encoding;

    @Override
    public String parse(GeneratorConfig config, Table table) {
        VelocityContext context = new VelocityContext();
        TablesConfig tablesConfig = config.getTablesConfig();
        if (tablesConfig.getBasePackageName().isEmpty()) {
            context.put("package", "");
            context.put("importClassList", Collections.emptyList());
        } else {
            context.put("package", tablesConfig.getRepositoryPackageName() + ".imp");
            context.put("importClassList", Arrays.asList(tablesConfig.getEntityPackageName() + "." + table.getEntityName() + "Vo",
                    tablesConfig.getRepositoryPackageName() + "." + "I" + table.getEntityName() + "Dao"));
        }
        context.put("simpleName", table.getEntityName() + "DaoImp");
        context.put("author", System.getProperty("user.name"));
        context.put("comment", table.getTableComment());
        context.put("date", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(LocalDateTime.now()));
        context.put("DaoName", "I" + table.getEntityName() + "Dao");
        context.put("entitySimpleName", table.getEntityName() + "Vo");
        context.put("entitySimpleClassName", table.getEntityName() + "Vo.class");
        context.put("primaryKeyDataType", "Long");
        StringWriter writer = new StringWriter();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("template/DaoImp.vm")) {
            byte[] buffer = new byte[input.available()];
            input.read(buffer);
            velocityEngine.evaluate(context, writer, "DaoImp", new String(buffer, encoding));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return writer.toString();
    }

    @Override
    public void setVelocityEngine(VelocityEngine ve, String encoding) {
        this.velocityEngine = ve;
        this.encoding = encoding;
    }
}
