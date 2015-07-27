package com.jingcai.apps.common.jdbc.mybatis;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.net.URI;

/**
 * Created by lejing on 15/7/25.
 */
public class MyBatisSqlSessionFactoryBean extends SqlSessionFactoryBean {
    private static final String SCHEME_JAR = "jar";
    private static final String SCHEME_FILE = "file";
    private static final String JAR_URL_SEPARATOR = "!" + File.separator;
    private static final String FILE_URL_SEPARATOR = "classes" + File.separator;

    public void setTypeAliasesPackageLocations(Resource[] typeAliasesPackageLocations) {
        StringBuffer buffer = new StringBuffer();
        try {
            if (typeAliasesPackageLocations.length > 0) {
                String classPathStr = null;
                for (int i = 0; i < typeAliasesPackageLocations.length; i++) {
                    Resource resource = typeAliasesPackageLocations[i];
                    URI uri = resource.getURI();
                    String path = uri.toString();
                    if (SCHEME_JAR.equals(uri.getScheme())) {
                        classPathStr = path.substring(path.indexOf(JAR_URL_SEPARATOR) + JAR_URL_SEPARATOR.length(), path.length());
                    } else if (SCHEME_FILE.equals(uri.getScheme())) {
                        classPathStr = path.substring(path.indexOf(FILE_URL_SEPARATOR) + FILE_URL_SEPARATOR.length(), path.length());
                    }
                    if (classPathStr.endsWith(File.separator)) {
                        classPathStr = classPathStr.substring(0, classPathStr.length() - 1);
                    }
                    if (0 != i) {
                        buffer.append(",");
                    }
                    buffer.append(classPathStr.replace(File.separatorChar, '.'));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (buffer.length() > 0) {
            setTypeAliasesPackage(buffer.toString());
        }
    }
}