package net.haesleinhuepf.clijx.assistant.options;

import ij.IJ;
import ij.Prefs;

public class AssistantOptions {
    private static String GIT_EXECUTABLE = "git";
    private static String MAVEN_EXECUTABLE = "mvn";
    private static String JDK_HOME = "C:/Program Files/Java/jdk1.8.0_202/";
    private static String ICY_EXECUTABLE = "C:/Programs/icy_all_2.0.3.0/icy.exe";

    private static String CONDA_PATH = "";
    private static String CONDA_ENV = "te_oki";

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
        CONDA_PATH = Prefs.get("CLIJx-assistant.conda_path", CONDA_PATH);
        CONDA_ENV = Prefs.get("CLIJx-assistant.conda_env", CONDA_ENV);

        ICY_EXECUTABLE = Prefs.get("CLIJx-assistant.icy", ICY_EXECUTABLE);
    }

    public String getCondaPath() {
        return CONDA_PATH;
    }

    void setCondaPath(String conda_path) {
        AssistantOptions.CONDA_PATH = conda_path;
    }

    private void savePrefs() {
        Prefs.set("CLIJx-assistant.git", GIT_EXECUTABLE);
        Prefs.set("CLIJx-assistant.maven", MAVEN_EXECUTABLE);
        Prefs.set("CLIJx-assistant.jdk_home", JDK_HOME);
        Prefs.set("CLIJx-assistant.conda_path", CONDA_PATH);
        Prefs.set("CLIJx-assistant.conda_env", CONDA_ENV);
        Prefs.set("CLIJx-assistant.icy", ICY_EXECUTABLE);
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
        AssistantOptions.JDK_HOME = jdkHome;
        savePrefs();
    }

    public String getCondaEnv() {
        return AssistantOptions.CONDA_ENV;
    }

    void setCondaEnv(String CONDA_ENV) {
        AssistantOptions.CONDA_ENV = CONDA_ENV;
        savePrefs();
    }

    public String getIcyExecutable() {
        return ICY_EXECUTABLE;
    }

    void setIcyExecutable(String icy_executable) {
        AssistantOptions.ICY_EXECUTABLE = icy_executable;
        savePrefs();
    }
}
