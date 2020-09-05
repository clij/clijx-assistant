package net.haesleinhuepf.clijx.assistant.annotation;

import ij.*;
import ij.gui.Overlay;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.plugin.frame.RoiManager;
import ij.plugin.tool.PlugInTool;
import ij.process.FloatPolygon;
import net.haesleinhuepf.clijx.gui.Utilities;

import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;

import static net.haesleinhuepf.clijx.assistant.utilities.AssistantUtilities.ignoreEvent;

public class AnnotationTool extends PlugInTool implements ImageListener {
    PolygonRoi line = null;
    static ImagePlus imp = null;

    static String TOOL_NAME = "Binary annotation tool";
    public AnnotationTool(){
        IJ.addEventListener(new IJEventListener() {
            @Override
            public void eventOccurred(int eventID) {
                if (ignoreEvent) {
                    return;
                }
                if (eventID == IJEventListener.TOOL_CHANGED) {
                    if (IJ.getToolName().compareTo(getToolName()) == 0 ) {
                        if (WindowManager.getCurrentImage() != null) {
                            imp = WindowManager.getCurrentImage();
                        }
                        AnnotationToolBar.getInstance();
                    }
                }
            }
        });
    }

    @Override
    public void mousePressed(ImagePlus imp, MouseEvent e) {
        this.imp = imp;
        int x = e.getX();
        int y = e.getY();
        line = new PolygonRoi(new float[]{
                imp.getWindow().getCanvas().offScreenX(x)}, new float[]{
                imp.getWindow().getCanvas().offScreenY(y)}, Roi.FREELINE);
        line.setStrokeWidth(AnnotationToolBar.thickness);
//        IJ.log("thick " + AnnotationToolBar.thickness);
        imp.setRoi(line);
        AnnotationToolBar.getInstance().updateVisualisation();

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
        line.setStrokeWidth(AnnotationToolBar.thickness);
        line.setStrokeColor(AnnotationToolBar.current_class.getColor());
        imp.setRoi(line);
    }

    @Override
    public void mouseReleased(ImagePlus imp, MouseEvent e) {
        if (line == null) {
            return;
        }


        RoiManager rm = RoiManager.getInstance();
        if (rm == null) {
            rm = new RoiManager();
        }
        line.setPosition(imp.getSlice());
        //line.setPosition(imp);//.getChannel(), imp.getSlice(), imp.getFrame());

        if (AnnotationToolBar.current_class == AnnotationToolBar.erase) {

            for (int i = rm.getCount() - 1; i >= 0; i--) {
                Roi otherRoi = rm.getRoi(i);
                if (otherRoi.getZPosition() == imp.getZ()) {
                    if (otherRoi instanceof PolygonRoi) {
                        PolygonRoi polyline1 = line;
                        PolygonRoi polyline2 = (PolygonRoi) otherRoi;
                        if (linesIntersect(polyline1, polyline2)) {
                            rm.select(i);
                            rm.runCommand("delete");
                            //.remove(polyline2);
                        }
                    }
                }
            }
        } else {
            line.setName("" + (AnnotationToolBar.current_class.getName()));
            rm.addRoi(line);
        }

        imp.killRoi();
        showAll(rm);
        AnnotationToolBar.getInstance().updateVisualisation();

        //rm.runCommand(imp, "Show all");
        line = null;
    }


    private boolean linesIntersect(PolygonRoi polyline1, PolygonRoi polyline2) {
        FloatPolygon polygon1 = polyline1.getFloatPolygon();
        FloatPolygon polygon2 = polyline2.getFloatPolygon();

        for (int i = 1; i < polygon1.npoints; i++) {
            for (int j = 1; j < polygon2.npoints; j++) {
                double ax1 = polygon1.xpoints[i];
                double ay1 = polygon1.ypoints[i];
                double ax2 = polygon1.xpoints[i - 1];
                double ay2 = polygon1.ypoints[i - 1];

                double bx1 = polygon2.xpoints[j];
                double by1 = polygon2.ypoints[j];
                double bx2 = polygon2.xpoints[j - 1];
                double by2 = polygon2.ypoints[j - 1];

                if (Line2D.linesIntersect(ax1, ay1, ax2, ay2, bx1, by1, bx2, by2)) {
                    return true;
                }
            }
        }
        return false;
    }

    static void showAll(RoiManager rm) {
        Overlay overlay = new Overlay();
        for (int i = 0; i < rm.getCount(); i++) {
            Roi roi = rm.getRoi(i);
            if (roi.getZPosition() == imp.getZ()) {
                //roi.setStrokeColor(AnnotationToolBar.);
                overlay.add(roi);
            }
        }
        imp.setOverlay(overlay);
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
                /*7*/	 "      #         " +
                /*8*/	 "     #          " +
                /*9*/	 "     #   #######" +
                /*A*/	 "     #   #     #" +
                /*B*/	 "         #    ##" +
                /*C*/	 "         #   ###" +
                /*D*/	 "         #  ####" +
                /*E*/	 "         # #####" +
                /*F*/	 "         #######" ;
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

    @Override
    public String getToolName() {
        return TOOL_NAME;
    }
}
