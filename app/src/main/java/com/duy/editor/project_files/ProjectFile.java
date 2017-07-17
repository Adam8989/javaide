package com.duy.editor.project_files;

import android.util.Log;

import com.duy.editor.editor.completion.Template;
import com.duy.editor.file.FileManager;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/**
 * Created by Duy on 16-Jul-17.
 */

@SuppressWarnings("ResultOfMethodCallIgnored")
public class ProjectFile implements Serializable, Cloneable {
    //main class name, don't include package, ex: Main
    private ClassFile mainClass;

    /**
     * root director
     */
    private String rootDir;

    /**
     * java package name: eg: com.duy.example
     */
    private String packageName;

    //project name
    private String projectName;


    public ProjectFile(String mainClass, String packageName, String projectName) {
        this.mainClass = new ClassFile(mainClass, packageName);
        this.packageName = packageName;
        this.projectName = projectName;
    }

    public String getRootDir() {
        return rootDir;
    }

    @Override
    public String toString() {
        return "ProjectFile{" +
                "rootDir='" + rootDir + '\'' +
                ", packageName='" + packageName + '\'' +
                ", projectName='" + projectName + '\'' +
                ", mainClass='" + mainClass + '\'' +
                '}';
    }

    public ClassFile getMainClass() {
        return mainClass;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }


    private static final String TAG = "ProjectFile";

    public static File createClass(ProjectFile projectFile,
                                   String currentPackage, String className,
                                   String content) {
        File file = new File(projectFile.getRootDir(), "src/main/java/" + currentPackage.replace(".", "/"));
        if (!file.exists()) file.mkdirs();
        File classf = new File(file, className + ".java");
        FileManager.saveFile(classf, content);

        Log.d(TAG, "createClass() returned: " + classf);
        return classf;
    }

    /**
     * create new project as tree:
     * ├───bin
     * ├───build
     * ├───libs
     * └───src
     *      └─main
     *          └─ java
     *             └──com
     *                 └──...
     *                     └──Main.class
     *
     * @param dir - parent dir for project
     * @return - path of parent of  main class
     * @throws IOException
     */
    public File create(File dir) throws IOException {
        this.rootDir = new File(dir, projectName).getPath();

        //now create root director
        File root = new File(dir, projectName);
        if (!root.exists()) root.mkdirs();

        File build = new File(root, "build");
        if (!build.exists()) build.mkdirs();

        File bin = new File(root, "bin");
        if (!bin.exists()) bin.mkdirs();

        File src = new File(root, "src");
        if (!src.exists()) src.mkdirs();

        File main = new File(src, "main");
        if (!main.exists()) main.mkdirs();

        File javaF = new File(main, "java");
        if (!javaF.exists()) javaF.mkdirs();

        //create package file
        File packageF = new File(javaF, packageName.replace(".", "/"));
        if (!packageF.exists()) {
            packageF.getParentFile().mkdirs();
            packageF.mkdirs();
        }

        //create main class
        File mainFile = new File(packageF, mainClass.getSimpleName() + ".java");
        if (!mainFile.exists()) {
            mainFile.createNewFile();
            String content = Template.createClass(packageName, mainClass.getSimpleName());
            FileManager.saveFile(mainFile, content);
        }
        mainClass.setPath(mainFile.getPath());
        return mainFile;
    }

    public String getProjectDir() {
        return rootDir;
    }
}
