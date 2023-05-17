package plugin;


import com.android.build.gradle.internal.dependency.JetifyTransform;
import com.android.build.gradle.internal.utils.DesugarConfigJson;

import org.gradle.api.artifacts.transform.InputArtifact;
import org.gradle.api.artifacts.transform.TransformAction;
import org.gradle.api.artifacts.transform.TransformOutputs;
import org.gradle.api.artifacts.transform.TransformParameters;
import org.gradle.api.file.FileSystemLocation;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;

/**
 * plugin
 */
public abstract class TransformTest implements TransformAction<TransformTest.Parameter> {

/*
    @PathSensitive(PathSensitivity.NAME_ONLY)
    Provider<FileSystemLocation> inputArtifact;

    @InputArtifact
    public abstract Provider<FileSystemLocation> getInputArtifact();*/

    @Override
    public void transform(TransformOutputs transformOutputs) {
        System.out.println("付和健");
    }

    public static class Parameter implements TransformParameters{



    }


}
