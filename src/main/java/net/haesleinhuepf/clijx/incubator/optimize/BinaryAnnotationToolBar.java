package net.haesleinhuepf.clijx.incubator.optimize;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.Toolbar;
import ij.plugin.frame.RoiManager;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

public class BinaryAnnotationToolBar extends Dialog {
    static Color foregroundColor = Color.green;
    static Color backgroundColor = Color.magenta;
    static Color eraseColor = Color.yellow;
    static int thickness = 1;

    static Color current_color = foregroundColor;

    JToggleButton foregroundButton;
    JToggleButton backgroundButton;
    JToggleButton eraseButton;

    JSlider slider;

    Timer heartbeat = null;

    private static BinaryAnnotationToolBar instance = null;

    public static BinaryAnnotationToolBar getInstance() {
        if (instance == null) {
            instance = new BinaryAnnotationToolBar();
        }
        return instance;
    }

    private BinaryAnnotationToolBar() {
        super(IJ.getInstance());
        setLayout(new FlowLayout());
        setUndecorated(true);

        setSize(45, 135);

        long[] lastTimeClicked = {-1};
        long double_click_delta_time = 1000;

        {
            foregroundButton = new JToggleButton("F");
            foregroundButton.setSize(45, 45);
            foregroundButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (System.currentTimeMillis() - lastTimeClicked[0] < double_click_delta_time) {
                        Color result = JColorChooser.showDialog(BinaryAnnotationToolBar.this, "Foreground", foregroundColor);//.getColor();
                        if (result == null) {
                            return;
                        }
                        foregroundColor = result;
                    }
                    current_color = foregroundColor;

                    updateVisualisation();
                    lastTimeClicked[0] = System.currentTimeMillis();
                }
            });
            add(foregroundButton);
        }

        {
            backgroundButton = new JToggleButton("B");
            backgroundButton.setSize(45, 45);
            backgroundButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (System.currentTimeMillis() - lastTimeClicked[0] < double_click_delta_time) {
                        Color result = JColorChooser.showDialog(BinaryAnnotationToolBar.this, "Background", backgroundColor);//.getColor();
                        if (result == null) {
                            return;
                        }
                        backgroundColor = result;
                    }
                    current_color = backgroundColor;

                    updateVisualisation();
                    lastTimeClicked[0] = System.currentTimeMillis();
                }
            });
            add(backgroundButton);
        }


        {

            eraseButton = new JToggleButton("E");
            eraseButton.setSize(45, 45);
            eraseButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    current_color = eraseColor;
                    updateVisualisation();
                }
            });
            add(eraseButton);
        }


        {
            slider = new JSlider( JSlider.VERTICAL, 1, 100, thickness);
            slider.setSize(45, 45);
            slider.setValue((int) (thickness));
            slider.setToolTipText("Thickness");
            slider.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    thickness = (int) slider.getValue();
                    updateVisualisation();
                }
            });
            add(slider);
        }


        reposition();
        //setVisible(true);
        //requestFocus();

        int delay = 100;
        heartbeat = new Timer();
        heartbeat.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                reposition();
            }
        }, delay, delay);

    }

    private void reposition() {
        //IJ.log(IJ.getToolName());
        try {

            if (BinaryAnnotationTool.imp == null) {
                setVisible(false);
                return;
            }
            if (BinaryAnnotationTool.imp.getWindow() == null || !BinaryAnnotationTool.imp.getWindow().isVisible()) {
                setVisible(false);
                return;
            }


            if (IJ.getToolName().compareTo(BinaryAnnotationTool.TOOL_NAME) == 0) {
                setVisible(true);
                //IJ.log("Set pos" + BinaryAnnotationTool.imp.getWindow().getX());
                this.setLocation(Math.max(BinaryAnnotationTool.imp.getWindow().getX() - getWidth(), 0), BinaryAnnotationTool.imp.getWindow().getY() + 45);
            } else {
                setVisible(false);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    void updateVisualisation() {
        //IJ.log("Update vis");
        foregroundButton.setSelected(current_color == foregroundColor);
        backgroundButton.setSelected(current_color == backgroundColor);
        eraseButton.setSelected(current_color == eraseColor);

        foregroundButton.setForeground(current_color == foregroundColor?foregroundColor:null);
        backgroundButton.setForeground(current_color == backgroundColor?backgroundColor:null);
        eraseButton.setForeground(current_color == eraseColor?eraseColor:null);

        setSize(45, slider.getHeight() + slider.getY());

        RoiManager rm = RoiManager.getInstance();
        if (rm == null) {
            return;
        }
        BinaryAnnotationTool.showAll(rm);


    }

    public static void main(String[] args) {
        new ImageJ();

        ImagePlus imp = IJ.openImage("C:/structure/data/blobs.tif");
        imp.show();

        Toolbar.addPlugInTool(new BinaryAnnotationTool());
    }

    public void setVisible(boolean value) {
        if (isVisible() != value) {
            super.setVisible(value);
        }
    }
}
