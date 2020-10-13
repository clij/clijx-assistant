package net.haesleinhuepf.clijx.assistant.scriptgenerator;

import ij.ImagePlus;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.utilities.AssistantUtilities;

public class PyclesperantoJupyterNotebookGenerator extends PyclesperantoGenerator {

    public PyclesperantoJupyterNotebookGenerator() {
        super(false);
    }

    @Override
    public String fileEnding() {
        return ".ipynb";
    }

    @Override
    public String push(ImagePlus source) {
        return "," + codeCell(super.push(source));
    }

    @Override
    public String execute(AssistantGUIPlugin plugin) {
        return "," + markdownCell("## " + AssistantUtilities.niceNameWithoutDimShape(plugin.getName())) +
                "," + codeCell(super.execute(plugin));
    }

    @Override
    public String pull(AssistantGUIPlugin result) {
        return "," + codeCell(super.pull(result));
    }

    @Override
    public String header() {
        String[] head = super.header().split("\n\n");
        StringBuilder output = new StringBuilder();
        for (String part : head) {
            if (output.length() == 0) {
                output.append("{\n" +
                        " \"cells\": [\n");
                output.append(markdownCell(part.replace("# ", "")));
            } else {
                output.append("," + codeCell(part));
            }
        }

        return output.toString();
    }

    @Override
    public String finish(String all) {
        return super.finish(all) + "" +
                "],\n" +
                " \"metadata\": {\n" +
                "  \"kernelspec\": {\n" +
                "   \"display_name\": \"Python 3\",\n" +
                "   \"language\": \"python\",\n" +
                "   \"name\": \"python3\"\n" +
                "  },\n" +
                "  \"language_info\": {\n" +
                "   \"codemirror_mode\": {\n" +
                "    \"name\": \"ipython\",\n" +
                "    \"version\": 3\n" +
                "   },\n" +
                "   \"file_extension\": \".py\",\n" +
                "   \"mimetype\": \"text/x-python\",\n" +
                "   \"name\": \"python\",\n" +
                "   \"nbconvert_exporter\": \"python\",\n" +
                "   \"pygments_lexer\": \"ipython3\",\n" +
                "   \"version\": \"3.7.6\"\n" +
                "  }\n" +
                " },\n" +
                " \"nbformat\": 4,\n" +
                " \"nbformat_minor\": 4\n" +
                "}\n";
    }

    private String codeCell(String content) {
        return "  {\n" +
                "   \"cell_type\": \"code\",\n" +
                "   \"execution_count\": 1,\n" +
                "   \"metadata\": {},\n" +
                "   \"outputs\": [],\n" +
                "   \"source\": [\n" +
                "    \"" + content.replace("\"", "'").replace("\n", "\\n\",\n\"") + "\"\n" +
                "   ]\n" +
                "  }";
    }

    private String markdownCell(String content) {
        return "  {\n" +
                "   \"cell_type\": \"markdown\",\n" +
                "   \"metadata\": {},\n" +
                "   \"source\": [\n" +
                "    \"" + content.replace("\n", "\\n\",\n\"") + "\"\n" +
                "   ]\n" +
                "  }";
    }
}
