package net.haesleinhuepf.clijx.assistant.options;

import ij.IJ;
import ij.Prefs;

public class AssistantOptions {
    private static String GIT_EXECUTABLE = "git";
    private static String MAVEN_EXECUTABLE = "mvn";
    private static String JDK_HOME = "C:/Program Files/Java/jdk1.8.0_202/";

    private static String conda_path = "";
    private static String conda_env = "te_oki";

    private static AssistantOptions instance = null;
    public static synchronized AssistantOptions getInstance() {
        if (instance == null) {
            instance = new AssistantOptions();
        }
        return instance;
    }

    private AssistantOptions() {

        GIT_EXECUTABLE = Prefs.get("CLIJx-assistant.git", GIT_EXECUTABLE);

        if (IJ.isWindows() && MAVEN_EXECUTABLE.compareTo("mvn") == 0) {
            MAVEN_EXECUTABLE = "mvn.cmd";
        }
        MAVEN_EXECUTABLE = Prefs.get("CLIJx-assistant.maven", MAVEN_EXECUTABLE);
        JDK_HOME = Prefs.get("CLIJx-assistant.jdk_home", JDK_HOME);
        conda_env = Prefs.get("CLIJx-assistant.git", conda_env);
    }

    public String getCondaPath() {
        return conda_path;
    }

    void setCondaPath(String conda_path) {
        AssistantOptions.conda_path = conda_path;
    }

    private void savePrefs() {
        Prefs.set("CLIJx-assistant.git", GIT_EXECUTABLE);
        Prefs.get("CLIJx-assistant.maven", MAVEN_EXECUTABLE);
        Prefs.get("CLIJx-assistant.jdk_home", JDK_HOME);
        Prefs.get("CLIJx-assistant.git", conda_env);
    }

    public String getGitExecutable() {
        return GIT_EXECUTABLE;
    }

    void setGitExecutable(String gitExecutable) {
        GIT_EXECUTABLE = gitExecutable;
        savePrefs();
    }

    public String getMavenExecutable() {
        return MAVEN_EXECUTABLE;
    }

    void setMavenExecutable(String mavenExecutable) {
        MAVEN_EXECUTABLE = mavenExecutable;
        savePrefs();
    }

    public String getJdkHome() {
        return JDK_HOME;
    }

    void setJdkHome(String jdkHome) {
        JDK_HOME = jdkHome;
        savePrefs();
    }

    public String getCondaEnv() {
        return conda_env;
    }

    void setCondaEnv(String conda_env) {
        AssistantOptions.conda_env = conda_env;
        savePrefs();
    }
}
