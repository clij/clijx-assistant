package net.haesleinhuepf.clijx.incubator.optimize;

import ij.ImageListener;
import ij.ImagePlus;
import ij.gui.Overlay;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.plugin.frame.RoiManager;
import ij.plugin.tool.PlugInTool;
import ij.process.FloatPolygon;
import net.haesleinhuepf.clijx.gui.Utilities;
import net.haesleinhuepf.clijx.weka.gui.CLIJxWekaObjectClassification;

import java.awt.*;
import java.awt.event.MouseEvent;

public class AnnotationTool extends PlugInTool implements ImageListener {
    PolygonRoi line = null;
    ImagePlus imp = null;

    @Override
    public void mousePressed(ImagePlus imp, MouseEvent e) {
        this.imp = imp;
        int x = e.getX();
        int y = e.getY();
        line = new PolygonRoi(new float[]{
                imp.getWindow().getCanvas().offScreenX(x)}, new float[]{
                imp.getWindow().getCanvas().offScreenY(y)}, Roi.FREELINE);
        imp.setRoi(line);

        ImagePlus.removeImageListener(this);
        ImagePlus.addImageListener(this);
    }

    @Override
    public void mouseDragged(ImagePlus imp, MouseEvent e) {
        if (line == null) {
            return;
        }
        FloatPolygon floatPolygon = line.getFloatPolygon();

        float[] xes = new float[floatPolygon.xpoints.length + 1];
        float[] yes = new float[floatPolygon.ypoints.length + 1];

        System.arraycopy(floatPolygon.xpoints, 0, xes, 0, floatPolygon.xpoints.length);
        System.arraycopy(floatPolygon.ypoints, 0, yes, 0, floatPolygon.ypoints.length);

        xes[xes.length - 1] = imp.getWindow().getCanvas().offScreenX(e.getX());
        yes[yes.length - 1] = imp.getWindow().getCanvas().offScreenY(e.getY());

        FloatPolygon newPolygon = new FloatPolygon(xes, yes);
        line = new PolygonRoi(newPolygon, Roi.FREELINE);
        imp.setRoi(line);
    }

    @Override
    public void mouseReleased(ImagePlus imp, MouseEvent e) {
        if (line == null) {
            return;
        }

        int classID = e.getModifiersEx()==0?0:1;

        line.setName("" + (classID==0?"p":"n"));
        line.setStrokeColor((classID==0? Color.green:Color.magenta));

        RoiManager rm = RoiManager.getInstance();
        if (rm == null) {
            rm = new RoiManager();
        }
        line.setPosition(imp.getSlice());
        //line.setPosition(imp);//.getChannel(), imp.getSlice(), imp.getFrame());
        rm.addRoi(line);
        imp.killRoi();
        showAll(rm);
        //rm.runCommand(imp, "Show all");
        line = null;
    }

    private void showAll(RoiManager rm) {
        Overlay overlay = new Overlay();
        for (int i = 0; i < rm.getCount(); i++) {
            Roi roi = rm.getRoi(i);
            if (roi.getZPosition() == imp.getZ()) {
                overlay.add(roi);
            }
        }
        imp.setOverlay(overlay);
    }


    @Override
    public String getToolName() {
        return "Annotation";
    }

    @Override
    public String getToolIcon()
    {
        return Utilities.generateIconCodeString(
                getToolIconString()
        );

    }

    public String getToolIconString()
    {
        return
                //        0123456789ABCDEF
                /*0*/	 "#        #####  " +
                /*1*/	 " #      #     # " +
                /*2*/	 "  ##     #     #" +
                /*3*/	 "    #####      #" +
                /*4*/	 "              # " +
                /*5*/	 "        ######  " +
                /*6*/	 "       #        " +
                /*7*/	 "      #  ###### " +
                /*8*/	 "     #   #     #" +
                /*9*/	 "     #   #     #" +
                /*A*/	 "     #   #     #" +
                /*B*/	 "         ###### " +
                /*C*/	 "         #      " +
                /*D*/	 "         #      " +
                /*E*/	 "         #      " +
                /*F*/	 "         #      " ;
    }


    @Override
    public void imageOpened(ImagePlus imp) {
        if (this.imp == imp) {
            ImagePlus.removeImageListener(this);
        }
    }

    @Override
    public void imageClosed(ImagePlus imp) {

    }

    @Override
    public void imageUpdated(ImagePlus imp) {
        if (imp == this.imp) {
            RoiManager rm = RoiManager.getInstance();
            if (rm != null) {
                showAll(rm);
            }
        }
    }
}
