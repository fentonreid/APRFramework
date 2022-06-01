package com.Output.Full;

import java.nio.file.Path;

// Given a root path generate a jar...
public class Jar {
    private String jarFile;
    private Path rootPath;

    public String getJarFile() { return jarFile; }
    public void setJarFile(String jarFile) { this.jarFile = jarFile; }

    public Path getRootPath() { return rootPath; }
    public void setRootPath(Path rootPath) { this.rootPath = rootPath; }

    public Jar(String jarFile, Path tempJavaStructure) {
        this.jarFile = jarFile;
        this.rootPath = tempJavaStructure;
    }




}
