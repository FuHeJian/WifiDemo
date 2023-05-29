package com.example.wifidemo1.Executors.MyShadow;

import java.io.File;

import dalvik.system.BaseDexClassLoader;

/**
 * @author: fuhejian
 * @date: 2023/5/25
 */
public class MyClassLoader extends BaseDexClassLoader {


    public MyClassLoader(String dexPath, String optimizedDirectory, String librarySearchPath, ClassLoader parent) {
        super(dexPath, optimizedDirectory == null ? null : new File(optimizedDirectory), librarySearchPath, parent);
    }

    /**
     * 不去父类中寻找
     *
     * @param name The <a href="#name">binary name</a> of the class
     * @return
     * @throws ClassNotFoundException
     */
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {

        // First, check if the class has already been loaded
        Class<?> c = findLoadedClass(name);
        if (c == null) {
            try {
                c = findClass(name);
            } catch (Exception e) {
                if (c == null) {
                    c = getParent() != null ? getParent().loadClass(name) : null;
                }
            }
        }

        return c;
    }

}
