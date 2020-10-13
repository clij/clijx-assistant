package net.haesleinhuepf.clijx.assistant.scriptgenerator;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.WindowManager;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij2.plugins.ConnectedComponentsLabelingBox;
import net.haesleinhuepf.clij2.plugins.GaussianBlur3D;
import net.haesleinhuepf.clij2.plugins.ThresholdOtsu;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.clijx.assistant.AssistantGUIStartingPoint;
import net.haesleinhuepf.clijx.assistant.ScriptGenerator;
import net.haesleinhuepf.clijx.assistant.interactive.generic.GenericAssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;

import java.util.ArrayList;
import java.util.HashMap;

public class IcyProtocolGenerator extends AbstractScriptGenerator {

    private StringBuilder blocks = new StringBuilder();
    private StringBuilder links = new StringBuilder();

    private int min_x = -1;
    private int min_y = -1;

    private float zoom = 1.2f;

    int line_height = 50;
    int block_width = 220;

    public IcyProtocolGenerator() {
        for (int id : WindowManager.getIDList()) {
            ImagePlus imp = WindowManager.getImage(id);
            if (imp.getWindow() != null) {
                int x = imp.getWindow().getX();
                int y = imp.getWindow().getY();
                if (min_x == -1 && min_y == -1) {
                    min_x = x;
                    min_y = y;
                }
                if (min_x > x) {
                    min_x = x;
                }
                if (min_y > y) {
                    min_y = y;
                }
            }
        }
    }

    int getXProperCoordinate(int x) {
        return (int)((x - min_x) * zoom);
    }
    int getYProperCoordinate(int y) {
        return (int)((y - min_y) * zoom);
    }

    long id_count = 0;
    private String newID() {
        id_count++;
        return "" + id_count;
    }

    ArrayList<ImagePlus> last_used_image = new ArrayList<>();
    HashMap<ImagePlus, String> image_id_store = new HashMap<>();
    HashMap<ImagePlus, Integer> x_coords = new HashMap<ImagePlus, Integer>();
    HashMap<ImagePlus, Integer> y_coords = new HashMap<ImagePlus, Integer>();
    private void updateIDStore(ImagePlus imp, String block_id, String var_name, int x, int y) {
        if (image_id_store.containsKey(imp)) {
            image_id_store.remove(imp);
            x_coords.remove(imp);
            y_coords.remove(imp);
            last_used_image.remove(imp);
        }
        image_id_store.put(imp, "" + block_id + "#" + var_name);
        x_coords.put(imp, x);
        y_coords.put(imp, y);
        last_used_image.add(imp);
    }

    @Override
    public String push(ImagePlus source) {
        if (image_id_store.containsKey(source)) {
            return "";
        }
        String block_id = newID();

        System.out.println(source);
        System.out.println(source.getTitle());
        System.out.println(source.getWindow());

        int x = source.getWindow().getX();
        int y = source.getWindow().getY();

        String content =
                "<block ID=\"" + block_id + "\" blockType=\"plugins.haesleinhuepf.implementations.CLIJ2_PushSequenceBlock\" className=\"plugins.haesleinhuepf.CLIJ2Blocks\" collapsed=\"false\" definedName=\"CLI j2_ push sequence\" height=\"83\" keepsResults=\"true\" width=\"" + block_width + "\" xLocation=\"" + getXProperCoordinate(x) + "\" yLocation=\"" + getYProperCoordinate(y) + "\">\n" +
                "<variables>\n" +
                "<input>\n" +
                "<variable ID=\"input\" name=\"input\" runtime=\"false\" value=\"Active Sequence\" visible=\"true\"/>\n" +
                "</input>\n" +
                "<output>\n" +
                "<variable ID=\"output\" name=\"input\" runtime=\"false\" visible=\"true\"/>\n" +
                "</output>\n" +
                "</variables>\n" +
                "</block>\n";

        updateIDStore(source, block_id, "output", x, y + line_height);

        blocks.append(content);

        return "";
    }


    @Override
    public String pull(AssistantGUIPlugin plugin) {
        ImagePlus target = plugin.getTarget();
        String[] image_id_arr = image_id_store.get(target).split("#");
        String target_id = image_id_arr[0];
        String target_id_var = image_id_arr[1];

        int display_width = target.getWindow().getWidth();
        int display_height = target.getWindow().getHeight();

        int x = target.getWindow().getX();
        int y = target.getWindow().getY();

        String pull_block_id = "" + newID();
        String show_block_id = "" + newID();

        blocks.append(
                "<block ID=\"" + pull_block_id + "\" blockType=\"plugins.haesleinhuepf.implementations.CLIJ2_PullSequenceBlock\" className=\"plugins.haesleinhuepf.CLIJ2Blocks\" collapsed=\"true\" definedName=\"CLI j2_ pull sequence\" height=\"83\" keepsResults=\"true\" width=\"" + block_width + "\" xLocation=\"" + getXProperCoordinate(x) + "\" yLocation=\"" + getYProperCoordinate(y + line_height ) + "\">\n" +
                "<variables>\n" +
                "<input>\n" +
                "<variable ID=\"input\" name=\"input\" runtime=\"false\" visible=\"true\"/>\n" +
                "</input>\n" +
                "<output>\n" +
                "<variable ID=\"output\" name=\"input\" runtime=\"false\" visible=\"true\"/>\n" +
                "</output>\n" +
                "</variables>\n" +
                "</block>");

        updateIDStore(target, pull_block_id, "input", x, y + line_height);

        blocks.append(
                "<block ID=\"" + show_block_id +"\" blockType=\"plugins.adufour.blocks.tools.Display\" className=\"plugins.adufour.blocks.tools.Display\" collapsed=\"false\" definedName=\"Display\" height=\"" + display_height + "\" keepsResults=\"true\" width=\"" + display_width + "\" xLocation=\"" + getXProperCoordinate(x) + "\" yLocation=\"" + getYProperCoordinate(y + line_height * 2) + "\">\n" +
                "<variables>\n" +
                "<input>\n" +
                "<variable ID=\"object\" name=\"object\" runtime=\"false\" type=\"icy.sequence.Sequence\" visible=\"true\"/>\n" +
                "</input>\n" +
                "<output/>\n" +
                "</variables>\n" +
                "</block>\n");

        link(target_id, target_id_var, pull_block_id, "input");
        link(pull_block_id, "output", show_block_id, "object");

        return "";
    }

    @Override
    public String comment(String name) {
        return "<!-- " + name + "-->";
    }

    @Override
    public String execute(AssistantGUIPlugin plugin) {

        ImagePlus target = plugin.getTarget();
/*
        String[] image_id_arr = image_id_store.get(target).split("#");
        String target_id = image_id_arr[0];
        String target_id_var = image_id_arr[1];
*/
        int x = target.getWindow().getX();
        int y = target.getWindow().getY();

        CLIJMacroPlugin clijMacroPlugin = plugin.getCLIJMacroPlugin();
        String block_id = "" + newID();

        blocks.append(
                "<block ID=\"" + block_id + "\" blockType=\"" + pluginToBlockType(clijMacroPlugin) + "\" className=\"plugins.haesleinhuepf.CLIJ2Blocks\" collapsed=\"true\" definedName=\"" + pluginToBlockName(clijMacroPlugin) + "\" height=\"158\" keepsResults=\"true\" width=\"" + block_width + "\" xLocation=\"" + getXProperCoordinate(x) + "\" yLocation=\"" + getYProperCoordinate(y) + "\">\n" +
                "<variables>\n" +
                "<input>\n" +
                "<variable ID=\"cl_device\" name=\"cl_device\" runtime=\"false\" value=\"\" visible=\"true\"/>\n"
        );

        String output = "";

        String[] parameters = clijMacroPlugin.getParameterHelpText().split(",");
        for (int i = 0; i < parameters.length; i++) {
            String temp[] = parameters[i].trim().split(" ");
            String var_name = temp[temp.length - 1];

            boolean destination = parameters[i].trim().toLowerCase().startsWith("byref") || parameters[i].toLowerCase().contains("destination");
            if (plugin.getArgs()[i] instanceof ImagePlus || plugin.getArgs()[i] instanceof ClearCLBuffer || plugin.getArgs()[i] instanceof ClearCLBuffer[]) {
                if (!destination) {
                    blocks.append(
                        "<variable ID=\"" + var_name + "\" name=\"" + var_name + "\" runtime=\"false\" visible=\"true\"/>\n"
                    );

                    ImagePlus imp = plugin.getSource(i);
                    String[] image_id_arr = image_id_store.get(imp).split("#");
                    String source_id = image_id_arr[0];
                    String source_id_var = image_id_arr[1];
                    link(source_id, source_id_var, block_id, var_name);

                    updateIDStore(imp, block_id, var_name, x, y + (i + 1) * line_height );
                    //image_id_store.remove(imp);
                    //image_id_store.put(imp, block_id + "#" + name);

                } else {
                    ImagePlus imp = plugin.getTarget();
                    updateIDStore(imp, block_id, var_name, x, y + (i + 1) * line_height );

                    output = "<variable ID=\"" + var_name + "\" name=\"" + var_name + "\" runtime=\"false\" visible=\"true\"/>\n";
                }
            } else {
                blocks.append(
                        "<variable ID=\"" + var_name + "\" name=\"" + var_name + "\" runtime=\"false\" value=\"" + plugin.getArgs()[i] + "\" visible=\"true\"/>\n"
                );
            }
        }

        blocks.append(
                "</input>\n" +
                "<output>\n" +
                output +
                "</output>\n" +
                "</variables>\n" +
                "</block>\n"
        );

        return "";
    }

    private String pluginToBlockName(CLIJMacroPlugin clijMacroPlugin) {
        String string = clijMacroPlugin.getClass().getSimpleName();

        // the following code snippet was adapted from plugins.adufour.blocks.util.BlocksFinder
        String[] words = string.split("(?=[A-Z])");
        String output = words[0];
        if (words.length > 1) {
            int nextWordIndex = 1;

            for(int i = nextWordIndex; i < words.length; ++i) {
                String word = words[i];
                if (word.length() == 1) {
                    if (words[i - 1].length() == 1) {
                        output = output + word;
                    } else {
                        output = output + " " + word;
                    }
                } else {
                    output = output + " " + word.toLowerCase();
                }
            }
        }

        return output;
    }


        @Override
    public String fileEnding() {
        return ".protocol";
    }

    @Override
    public String header() {
        return comment("This is an Icy protocol auto generated by CLIJx-assistant.\n" +
                "In order to make it run in Icy, download Icy and install Clicy. Read more: \n" +
                "http://icy.bioimageanalysis.org/\n" +
                "http://icy.bioimageanalysis.org/plugin/clicy-blocks/ ");
    }

    @Override
    public String close(String image) {
        return "";
    }

    @Override
    public String finish(String all) {

        for (ImagePlus imp : last_used_image) {
            String block_id = newID();

            int x = x_coords.get(imp);
            int y = y_coords.get(imp);

            blocks.append(
                "<block ID=\"" + block_id + "\" blockType=\"plugins.haesleinhuepf.implementations.CLIJ2_ReleaseBufferBlock\" className=\"plugins.haesleinhuepf.CLIJ2Blocks\" collapsed=\"false\" definedName=\"CLI j2_ release buffer\" height=\"61\" keepsResults=\"true\" width=\"" + block_width + "\" xLocation=\"" + (int)(x + block_width * zoom) + "\" yLocation=\"" + y + "\">\n" +
                "<variables>\n" +
                "<input>\n" +
                "<variable ID=\"input\" name=\"input\" runtime=\"false\" visible=\"true\"/>\n" +
                "</input>\n" +
                "<output/>\n" +
                "</variables>\n" +
                "</block>\n"
            );

            String[] image_id_arr = image_id_store.get(imp).split("#");
            String source_id = image_id_arr[0];
            String source_id_var = image_id_arr[1];
            link(source_id, source_id_var, block_id, "input");

        }

        String output =
            "<protocol VERSION=\"4\">\n" +
            "<blocks>\n" +

            blocks.toString() +

            "</blocks>\n" +
            "<links>\n" +

            links.toString() +

            "</links>\n" +
            "</protocol>\n";

        return output;
    }

    private void link(String source_block, String source_block_var, String target_block, String target_block_var) {
        links.append("<link dstBlockID=\"" + target_block + "\" dstVarID=\"" + target_block_var + "\" srcBlockID=\"" + source_block + "\" srcVarID=\"" + source_block_var + "\"/>\n");
    }

    private String pluginToBlockType(CLIJMacroPlugin plugin) {
        String pluginClassname = plugin.getClass().getSimpleName();
        return "plugins.haesleinhuepf.implementations.generated.CLIJ2_" + pluginClassname + "Block";
    }

    public static void main(String[] args) throws InterruptedException {
        new ImageJ();
        CLIJx.getInstance("RTX");

        ImagePlus imp = IJ.openImage("C:/structure/data/blobs.tif");
        imp.show();

        new AssistantGUIStartingPoint().run("");
        Thread.sleep(200);

        new GenericAssistantGUIPlugin(new GaussianBlur3D()).run("");
        Thread.sleep(200);
        new GenericAssistantGUIPlugin(new ThresholdOtsu()).run("");
        Thread.sleep(200);
        GenericAssistantGUIPlugin agp = new GenericAssistantGUIPlugin(new ConnectedComponentsLabelingBox());
        agp.run("");
        Thread.sleep(200);

        agp.generateScriptFile(new IcyProtocolGenerator());
    }
}
