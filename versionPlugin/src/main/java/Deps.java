import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * @author: fuhejian
 * @date: 2023/3/15
 */
public class Deps implements Plugin<Project> {
    @Override
    public void apply(Project project) {

    }

    public static final DependenciesClass dependencies = new DependenciesClass();

    private static class DependenciesClass{
        public final int minSdkVersion = 28;
        public final int compileSdkVersion = 31;
        public final int targetSdkVersion = 31;
        //ijkplayer
        private String ijkplayer = "0.8.8";
        public String ijkplayerJava =  "tv.danmaku.ijk.media:ijkplayer-java:"+ijkplayer;
        public String ijkplayerArmV7a =  "tv.danmaku.ijk.media:ijkplayer-armv7a:"+ijkplayer;
        public String ijkplayerExo = "tv.danmaku.ijk.media:ijkplayer-exo:"+ijkplayer;
    }

}
