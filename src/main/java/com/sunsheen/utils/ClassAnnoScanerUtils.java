package com.sunsheen.utils;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class ClassAnnoScanerUtils {


    /**
     * @Description: (用一句话描述这个变量表示什么)
     * @author: slm
     * @date: 2020年3月7日 下午6:07:06
     */
    public static List<Class> getClasssFromPackage(String packName, Class annnoClazz) {
        List<Class> clazzs = new ArrayList<Class>();

        if (StringUtils.isEmpty(packName)) {
            return clazzs;
        }
        // 是否循环搜索子包
        boolean recursive = true;
        // 包名字
        String packageName = packName;
        // 包名对应的路径名称
        String packageDirName = packageName.replace('.', '/');
        Enumeration<URL> dirs;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    try {
                        findClassInPackageByFile(packageName, filePath, recursive, clazzs, annnoClazz, packageName);
                    } catch (Exception e) {
                        System.out.println("报错了");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return clazzs;
    }

    /**
     * @Description: (用一句话描述这个变量表示什么)
     * @author: slm
     * @date: 2020年3月7日 下午6:06:52
     */
    private static void findClassInPackageByFile(String packageName, String filePath, final boolean recursive, List<Class> clazzs, Class annnoClazz, String scannPack) {
        File dir = new File(filePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }

        if (excludePackage(packageName, scannPack)) {
            return;
        }

        // 在给定的目录下找到所有的文件，并且进行条件过滤
        File[] dirFiles = dir.listFiles(new FileFilter() {

            @Override
            public boolean accept(File file) {
                boolean acceptDir = recursive && file.isDirectory();// 接受dir目录
                boolean acceptClass = file.getName().endsWith("class");// 接受class文件
                return acceptDir || acceptClass;
            }
        });

        for (File file : dirFiles) {
            try {
                if (file.isDirectory()) {
                    findClassInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, clazzs, annnoClazz, scannPack);
                } else {
                    addClaszz(file, packageName, clazzs, annnoClazz, scannPack);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * @Description: (符合條件的添加到容器)
     * @author: SLM
     * @date: 2020年3月18日 下午4:03:49
     */
    private static void addClaszz(File file, String packageName, List<Class> clazzs, Class annnoClazz, String scannPack) {
        try {
            String className = file.getName().substring(0, file.getName().length() - 6);

            if (!excludePackage(packageName, scannPack)) {
                String clazzName = packageName + "." + className;

                Class clazz = null;
                try {
                    clazz = Thread.currentThread().getContextClassLoader().loadClass(clazzName);
                } catch (Error e) {

                }

                // 只有在类上有才继续扫描
                if (null != clazz && null != clazz.getAnnotation(annnoClazz)) {
                    clazzs.add(clazz);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * @Description: (排他包 - 不想扫描的包)
     * @author: SLM
     * @date: 2020年3月18日 下午4:10:16
     */
    private static boolean excludePackage(String packageName, String scannPack) {
        String[] packs = {"cn", "dbcollectionservice", "hearken", "io/socket"};
        for (String pack : packs) {
            if (packageName.contains(scannPack + "." + pack)) {
                return true;
            }
        }
        return false;

    }
}
