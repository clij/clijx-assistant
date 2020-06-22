package net.haesleinhuepf.spimcat.annotation;

import autopilot.measures.FocusMeasures;
import ij.*;
import ij.gui.*;
import ij.measure.ResultsTable;
import ij.plugin.Duplicator;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.clijx.weka.ApplyWekaToTable;
import net.haesleinhuepf.clijx.weka.TrainWekaFromTable;
import net.haesleinhuepf.clijx.weka.CLIJxWeka2;
import net.haesleinhuepf.clijx.weka.gui.InteractivePanelPlugin;
import net.haesleinhuepf.spimcat.measurement.SliceAnalyser;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class PhaseAnnotator extends InteractivePanelPlugin implements PlugInFilter, ImageListener {

    CLIJx clijx;

    FocusMeasures.FocusMeasure[] focusMeasures = new FocusMeasures.FocusMeasure[]{
            FocusMeasures.FocusMeasure.StatisticMax,
            FocusMeasures.FocusMeasure.StatisticMean,
            FocusMeasures.FocusMeasure.StatisticVariance,
            FocusMeasures.FocusMeasure.StatisticNormalizedVariance,
            FocusMeasures.FocusMeasure.SpectralNormDCTEntropyShannon,
            FocusMeasures.FocusMeasure.DifferentialTotalVariation,
            FocusMeasures.FocusMeasure.DifferentialTenengrad
    };
    private static String[] phases = {"Phase1", "Phase2", "Phase3"};

    class Entry{
        public ImagePlus imp;
        // meta data
        int frame;
        String name;

        // measurements
        ResultsTable table;

        // ground truth
        int phaseIndex = 0;
    }

    Entry current = null;


    private static ResultsTable table = new ResultsTable();
    private Choice phaseChoice;

    ImagePlus imp;

    @Override
    public int setup(String arg, ImagePlus imp) {
        return DOES_ALL;
    }

    @Override
    public void run(ImageProcessor ip) {

        clijx = CLIJx.getInstance();
        imp = IJ.getImage();

        setupGUI();

        ImagePlus.addImageListener(this);
        imageUpdated(imp);


    }

    Button saveButton;
    Button predictButton;
    private void setupGUI() {
        attach(imp.getWindow());

        {
            Button loadButton = new Button("Load");
            loadButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (table.size() > 0) {
                        YesNoCancelDialog yncd = new YesNoCancelDialog(imp.getWindow(), "Sure?", "Your current annotations will be lost. Are you sure?");
                        if (!yncd.yesPressed()) {
                            return;
                        }
                    }
                    try {
                        table = ResultsTable.open(null);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    if (table.size() > 0) {
                        //table.show("Annotations");
                        saveButton.setEnabled(true);
                        predictButton.setEnabled(true);
                    }
                }
            });
            guiPanel.add(loadButton);
        }

        {
            saveButton = new Button("Save");
            saveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    table.save(null);
                }
            });
            saveButton.setEnabled(false);
            guiPanel.add(saveButton);
        }
        guiPanel.add(new Label(" "));

        {
            Button configButton = new Button("Config");
            configButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    configureClicked();
                }
            });
            guiPanel.add(configButton);
        }

        {
            phaseChoice = new Choice();
            for (String phase : phases) {
                //names[i] = phase.toString();
                phaseChoice.addItem(phase.toString());
            }
            guiPanel.add(phaseChoice);

        }

        guiPanel.add(new Label(" "));

        {
            Button annotateButton = new Button("Annotate");
            annotateButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    annotateClicked();
                }
            });
            guiPanel.add(annotateButton);
        }

        {
            predictButton = new Button("Predict");
            predictButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    predictClicked();
                }
            });
            predictButton.setEnabled(false);
            guiPanel.add(predictButton);
        }

        guiPanel.add(new Label(" "));

        {
            Button doneButton = new Button("Done");
            doneButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    doneClicked();
                }
            });
            guiPanel.add(doneButton);
        }
    }

    private void configureClicked() {
        GenericDialog gd = new GenericDialog("Configure phases");
        gd.addStringField("Phases (comma separated)", String.join(", " + phases));
        gd.showDialog();
        if (gd.wasCanceled()) {
            return;
        }

        phases = gd.getNextString().split(",");

        phaseChoice.removeAll();
        for (int i = 0; i < phases.length; i++) {
            phases[i] = phases[i].trim();
            phaseChoice.addItem(phases[i]);
        }
    }

    public CLIJxWeka2 train() {
        ResultsTable tableWithGroundTruth = new ResultsTable();
        for (int i = 0; i < table.size(); i++) {
            tableWithGroundTruth.incrementCounter();
            tableWithGroundTruth.addValue("Phase_index", table.getValue("Phase_index", i));
            for (FocusMeasures.FocusMeasure focusMeasure : focusMeasures) {
                tableWithGroundTruth.addValue(focusMeasure.getLongName(), table.getValue(focusMeasure.getLongName(), i));
            }
        }


        int numberOfTrees = 200;
        int numberOfFeatures = 2;
        int maxDepth = 3;
        CLIJxWeka2 clijxweka2 = TrainWekaFromTable.trainWekaFromTable(clijx, tableWithGroundTruth, "Phase_index", "test.model", numberOfTrees, numberOfFeatures, maxDepth);

        return clijxweka2;
        /*
        ResultsTable sendToGPUTable = new ResultsTable();
        indexToClassID = new HashMap<>();
        classIDTOIndex = new HashMap<>();

        int index = 1;
        for (int i = 0; i < table.size(); i++) {
            int class_id = (int) table.getValue("Phase_index", i);
            int current_phase_index;
            if (!classIDTOIndex.containsKey(class_id)) {
                indexToClassID.put(index, class_id);
                classIDTOIndex.put(class_id, index);
                current_phase_index = index;
                index++;
            } else {
                current_phase_index = classIDTOIndex.get(class_id);
            }

            sendToGPUTable.incrementCounter();
            sendToGPUTable.addValue("GROUND_TRUTH", current_phase_index);

            for (FocusMeasures.FocusMeasure focusMeasure : focusMeasures) {
                sendToGPUTable.addValue(focusMeasure.getLongName(), table.getValue(focusMeasure.getLongName(), i));
            }
        }


        //sendToGPUTable.show("Send to GPU");


        ClearCLBuffer tableOnGPU = clijx.create(sendToGPUTable.getHeadings().length, sendToGPUTable.size());

        clijx.resultsTableToImage2D(tableOnGPU, sendToGPUTable);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //table.show("My Results");

        ClearCLBuffer transposed1 = clijx.create(tableOnGPU.getHeight(), tableOnGPU.getWidth());
        ClearCLBuffer transposed2 = clijx.create(tableOnGPU.getHeight(), 1, tableOnGPU.getWidth());
        clijx.transposeXY(tableOnGPU, transposed1);
        clijx.transposeYZ(transposed1, transposed2);

        ClearCLBuffer ground_truth = clijx.create(transposed2.getWidth(), transposed2.getHeight(), 1);
        ClearCLBuffer featureStack = clijx.create(transposed2.getWidth(), transposed2.getHeight(), transposed2.getDepth() - 1);

        clijx.crop3D(transposed2, featureStack, 0, 0, 1);
        clijx.crop3D(transposed2, ground_truth, 0, 0, 0);

        //System.out.println("feature stack");
        //clijx.print(featureStack);
        //System.out.println("ground_truth");
        //clijx.print(ground_truth);

        //System.out.println("Ground truth:");
        //clijx.print(ground_truth);

        CLIJxWeka clijxweka = new CLIJxWeka(clijx, featureStack, ground_truth);

        clijxweka.getClassifier();


        clijx.release(ground_truth);
        clijx.release(featureStack);
        clijx.release(transposed1);
        clijx.release(transposed2);
        clijx.release(tableOnGPU);

        return clijxweka;
        */

    }


    private void predictClicked() {
        String stage = predict(train());

        IJ.log("Prediction: " + stage);
    }

    public String predict(CLIJxWeka2 clijxweka) {
        ResultsTable tableWithoutGroundTruth = new ResultsTable();

        tableWithoutGroundTruth.incrementCounter();
        for (FocusMeasures.FocusMeasure focusMeasure : focusMeasures) {
            tableWithoutGroundTruth.addValue(focusMeasure.getLongName(), current.table.getValue(focusMeasure.getLongName(), 0));
        }

        ApplyWekaToTable.applyWekaToTable(clijx, tableWithoutGroundTruth, "Phase_index", clijxweka);

        return phases[(int) tableWithoutGroundTruth.getValue("Phase_index", 0)];
        /*

        ResultsTable sendToGPUTable = new ResultsTable();
        sendToGPUTable.incrementCounter();
        for (FocusMeasures.FocusMeasure focusMeasure : focusMeasures) {
            sendToGPUTable.addValue(focusMeasure.getLongName(), current.table.getValue(focusMeasure.getLongName(), 0));
        }

        float[] values = new float[focusMeasures.length + 1];

        int i = 0;
        for (FocusMeasures.FocusMeasure focusMeasure : focusMeasures) {
            values[i] = (float) current.table.getValue(focusMeasure.getLongName(), 0);
            i++;
        }

        FloatBuffer featureBuffer = FloatBuffer.wrap(values);

        ClearCLBuffer featureStack = clijx.create(1, 1, sendToGPUTable.getHeadings().length);
        featureStack.readFrom(featureBuffer, true);

        ClearCLBuffer result = clijx.create(1, 1);

        ApplyOCLWekaModel.applyOCL(clijx, featureStack, result, clijxweka.getOCL());

        float[] resultArray = new float[(int) result.getWidth()];
        FloatBuffer buffer = FloatBuffer.wrap(resultArray);

        result.writeTo(buffer, true);

        int predictedIndex = (int) resultArray[resultArray.length - 1];
        int predictedClass = indexToClassID.get(predictedIndex + 1);


        clijx.release(result);
        clijx.release(featureStack);

        return phases[predictedClass];
        */
    }

    private void doneClicked() {
        dismantle();
    }

    public void annotateClicked() {
        //System.out.println("Choice: " + phaseChoice.getSelectedItem());
        //System.out.println("Choice index: " + phaseChoice.getSelectedIndex());

        table.incrementCounter();
        table.addValue("Dataset", current.name);
        table.addValue("Frame", current.frame);
        table.addValue("Phase_index", phaseChoice.getSelectedIndex());
        table.addValue("Phase_name", phaseChoice.getSelectedItem());

        copyTableRow(current.table, table);
        //table.show("Annotations");
        //table.save("backup.csv");

        saveButton.setEnabled(true);
        predictButton.setEnabled(true);
    }


    private void copyTableRow(ResultsTable table, ResultsTable table1) {
        for(String heading : table.getHeadings()) {
            int columnIndex = table.getColumnIndex(heading);
            float value = table.getColumn(columnIndex)[0];
            table1.addValue(heading, value);
        }
    }

    @Override
    public void imageOpened(ImagePlus imp) {

    }

    @Override
    public void imageClosed(ImagePlus imp) {

    }

    boolean acting = false;

    @Override
    public void imageUpdated(ImagePlus imp) {
        if (acting || this.imp != imp) {
            return;

        }
        acting = true;
        current = generateEntry(imp);
        acting = false;
    }

    private Entry generateEntry(ImagePlus imp) {
        Entry entry = new Entry();
        if (current != null && current.imp == imp) {
            entry = current;
        }

        entry.name = imp.getTitle();
        entry.frame = imp.getFrame() - 1;

        if (entry.imp == null) {
            entry.imp = imp;
        }
        if (entry.imp != null) {
            ClearCLBuffer buffer = clijx.push(new Duplicator().run(imp, 1,1, 1, 1, imp.getFrame(), imp.getFrame()));

            //ImagePlus imp = clijx.pull(buffer);

            entry.table = new ResultsTable();

            entry.table.incrementCounter();
            new SliceAnalyser(buffer, focusMeasures, entry.table).run();

            clijx.release(buffer);
        }
        return entry;
    }

    public static PhaseAnnotator special(String[] phases) {
        PhaseAnnotator.phases = phases;

        PhaseAnnotator pa = new PhaseAnnotator();
        pa.run(null);


        return pa;
    }

    public ResultsTable getTable() {
        return table;
    }

    public void select(int index) {
        phaseChoice.select(index);
    }
}
