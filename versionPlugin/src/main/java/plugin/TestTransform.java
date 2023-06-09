package plugin;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.api.transform.TransformOutputProvider;
import com.android.build.gradle.internal.pipeline.TransformManager;

import org.apache.commons.io.FileUtils;
import org.gradle.api.Project;
import org.gradle.api.file.Directory;
import org.gradle.api.file.RegularFile;
import org.gradle.api.provider.Property;
import org.gradle.internal.impldep.org.objectweb.asm.Opcodes;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

/**
 * @author: fuhejian
 * @date: 2023/3/23
 */
public class TestTransform extends Transform {

    private Project mProject;
    public TestTransform(Project project) {
        super();
        mProject = project;
    }

    @Override
    public Set<QualifiedContent.ContentType> getOutputTypes() {
        return super.getOutputTypes();
    }

    @Override
    public void setOutputDirectory(Property<Directory> directory) {
        super.setOutputDirectory(directory);
    }

    @Override
    public void setOutputFile(Property<RegularFile> file) {
        super.setOutputFile(file);
    }


    @Override
    public String getName() {
        return "BasePlugin";
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    public boolean isIncremental() {
        return false;
    }


    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {

        Collection<TransformInput> inputs = transformInvocation.getInputs();
        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();

        inputs.forEach(it->{

            Collection<DirectoryInput> directoryInputs = it.getDirectoryInputs();
            Collection<JarInput> jarInputs = it.getJarInputs();

            directoryInputs.forEach(directoryInput->{
                File outFile = outputProvider.getContentLocation(directoryInput.getName(),directoryInput.getContentTypes(),directoryInput.getScopes(), Format.DIRECTORY);

                System.out.println("test" + directoryInput.getName());
                try {
                    transformDir(directoryInput.getFile(),outFile);
                } catch (Exception e) {
                    System.out.println("");
                }

            });

            jarInputs.forEach(jarInput -> {
                File outFile = outputProvider.getContentLocation(jarInput.getName(),jarInput.getContentTypes(),jarInput.getScopes(), Format.JAR);

                System.out.println("test" + jarInput.getName());
                try {
                    FileUtils.copyFile(jarInput.getFile(),outFile);
                } catch (Exception e) {
                    System.out.println("");
                }
            });

        });

    }


    private static void transformDir(File input, File dest) throws Exception
    {
        if (dest.exists()) {
            FileUtils.forceDelete(dest);
        }

        FileUtils.forceMkdir(dest);
        String srcDirPath = input.getAbsolutePath();
        String destDirPath = dest.getAbsolutePath();

        for (File file : input.listFiles()) {
            String destFilePath = file.getAbsolutePath().replace(srcDirPath, destDirPath);
            File destFile = new File(destFilePath);
            if (file.isDirectory()) {
                transformDir(file, destFile);
            } else if (file.isFile()) {
                try {
                    FileUtils.touch(destFile);
                } catch (IOException e) {
                    System.out.println("");
                }
                transformSingleFile(file, destFile);
            }
        }
    }

    private static void transformSingleFile(File input, File dest) {
        weave(input.getAbsolutePath(), dest.getAbsolutePath());
    }

    private static void weave(String inputPath, String outputPath) {
        if(!inputPath.contains(".class"))return;
        try {
            FileInputStream is = new FileInputStream(inputPath);
            ClassReader cr = new ClassReader(is);
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            TestMethodClassAdapter adapter = new TestMethodClassAdapter(cw);
            cr.accept(adapter, 0);
            FileOutputStream fos = new FileOutputStream(outputPath);
            byte[] bytes = cw.toByteArray();
            fos.write(bytes);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
