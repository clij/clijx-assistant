package net.haesleinhuepf.clijx.assistant.scriptgenerator;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.WaitForUserDialog;
import ij.plugin.PlugIn;
import net.haesleinhuepf.clij2.plugins.GaussianBlur3D;
import net.haesleinhuepf.clij2.plugins.MaximumImages;
import net.haesleinhuepf.clij2.plugins.MaximumZProjection;
import net.haesleinhuepf.clij2.plugins.Mean3DBox;
import net.haesleinhuepf.clijx.assistant.AssistantGUIStartingPoint;
import net.haesleinhuepf.clijx.assistant.interactive.generic.GenericAssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.interactive.handcrafted.Zoom;
import net.haesleinhuepf.clijx.assistant.optimize.Workflow;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;
import org.apache.commons.math3.fitting.leastsquares.GaussNewtonOptimizer;

import java.io.File;

import static net.haesleinhuepf.clijx.assistant.scriptgenerator.MavenJavaProjectGenerator.*;

public class MavenJavaProjectGeneratorPlugin implements PlugIn {


    static String plugin_name = "TestPluginName";
    static String plugin_description = "Plugin description";
    static String author_name = "AuthorName";
    static String author_id = "AuthorID";

    public static void main(String[] args) {
        new ImageJ();

        ImagePlus imp = IJ.openImage("C:/structure/data/t1-head.tif");
        imp.show();

        new AssistantGUIStartingPoint().run("");

        new GenericAssistantGUIPlugin(new GaussianBlur3D()).run("");
        new GenericAssistantGUIPlugin(new Mean3DBox()).run("");

        new GenericAssistantGUIPlugin(new MaximumImages()).run("");

        //new Zoom().run("");
        GenericAssistantGUIPlugin plugin = new GenericAssistantGUIPlugin(new MaximumZProjection());
        plugin.run("");

        //GenericAssistantGUIPlugin plugin1 = new GenericAssistantGUIPlugin(new MaximumZProjection());
        //plugin1.run("");


        new MavenJavaProjectGeneratorPlugin().run("");
    }

    @Override
    public void run(String arg) {
        AssistantGUIPlugin plugin = AssistantGUIStartingPoint.getCurrentPlugin();
        generate(plugin);
    }

    public void generate(AssistantGUIPlugin plugin) {
        if (plugin == null) {
            new WaitForUserDialog("Error: Only CLIJx-Assistant plugins allow generating CLIJx/Fiji plugins.");
        }

        Workflow workflow = plugin.getWorkflow();

        String temp_folder = IJ.getDirectory("temp") + "/temp" + System.currentTimeMillis() + "/";
        temp_folder = temp_folder.replace("\\/", "/");
        temp_folder = temp_folder.replace("\\", "/");

        new File(temp_folder).mkdirs();

        System.out.println(temp_folder);

        GenericDialog dialog = new GenericDialog("Generate CLIJx/Fiji plugin");
        dialog.addStringField("Plugin name", plugin_name, 20);
        dialog.addStringField("Plugin description", plugin_description, 20);
        dialog.addStringField("Author", author_name, 20);
        dialog.addStringField("Author ID", author_id, 20);
        dialog.addStringField("Working directory", temp_folder, 20);

        dialog.showDialog();
        if (dialog.wasCanceled()) {
            return;
        }

        plugin_name = dialog.getNextString();
        plugin_description = dialog.getNextString();
        author_name = dialog.getNextString();
        author_id = dialog.getNextString();
        temp_folder = dialog.getNextString();

        generate(workflow, temp_folder, plugin_name, plugin_description, author_name, author_id);
    }

    public static void generate(Workflow workflow, String temp_folder, String plugin_name, String plugin_description, String author_name, String author_id) {
        IJ.log("Cloning...");
        IJ.log("Template: " + TEMPLATE_REPOSITORY);
        MavenJavaProjectGenerator.git_clone(TEMPLATE_REPOSITORY, temp_folder);

        IJ.log("Generating Java...");
        MavenJavaProjectGenerator generator = new MavenJavaProjectGenerator(workflow, plugin_name, plugin_description, author_name, author_id);
        IJ.log("Parsing project folder...");
        generator.parseSubFolders(new File(temp_folder));

        IJ.log("Run maven...");
        MavenJavaProjectGenerator.mvn_package(temp_folder);

        IJ.log("Installing plugin...");
        if (generator.installTo(temp_folder, IJ.getDir("imagej"))) {
            IJ.log("\nBuild & installation successful.");
            IJ.log("\nThe compiled version is saved to your Fiji/plugins/" + generator.getJarFilename());
            IJ.log("The source code was saved to \n" + temp_folder);
            IJ.log("Make sure to make a copy before closing Fiji.");
            IJ.log("\nPlease restart Fiji to try out your new Plugin.");
            String dependencies = generator.getDependencies();
            if (dependencies.length() > 0) {
                IJ.log("If you plan to ship this plugin to others, you also need to ship these dependencies:\n" + dependencies);
            }
        } else {
            IJ.log("\nInstallation failed.");
            IJ.log("\nIf build succeeded, check if a plugin with the same name exists already.");
            IJ.log("The source code was saved to \n" + temp_folder);
            IJ.log("\nIf you need assistance, please provide the error message above and the source code to @haesleinhuepf on https://image.sc .");
        }

        IJ.log("Bye.");
    }
}
