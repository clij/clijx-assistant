package net.haesleinhuepf.clijx.assistant.annotation;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.Toolbar;
import ij.plugin.frame.RoiManager;
import weka.gui.WrapLayout;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class AnnotationToolBar extends Dialog {

    static ArrayList<ClassificationClass> classes = new ArrayList<ClassificationClass>();
    static {
        ClassificationClass positive = new ClassificationClass(1);
        positive.setName("p");
        classes.add(positive);

        ClassificationClass negative = new ClassificationClass(2);
        negative.setName("n");
        classes.add(negative);
    }
    static ClassificationClass erase = new ClassificationClass(0);
    static int thickness = 1;

    static ClassificationClass current_class = classes.get(0);

    ArrayList<JToggleButton> buttons = new ArrayList<>();

    JSlider slider;

    Timer heartbeat = null;

    Panel contentPanel = null;

    private static AnnotationToolBar instance = null;

    public static AnnotationToolBar getInstance() {
        if (instance == null) {
            instance = new AnnotationToolBar();
        }
        return instance;
    }

    private AnnotationToolBar() {
        super(IJ.getInstance());
        setLayout(new FlowLayout());
        setUndecorated(true);

        buildUI();

        reposition();
        updateVisualisation();
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

    private void buildUI() {

        long[] lastTimeClicked = {-1};
        long double_click_delta_time = 1000;

        Panel content = new Panel();
        content.setLayout(new WrapLayout());
        buttons.clear();
        for (ClassificationClass klass : classes)
        {
            JToggleButton button = new JToggleButton(klass.getName());
            button.setSize(45, 45);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (System.currentTimeMillis() - lastTimeClicked[0] < double_click_delta_time) {
                        Color result = JColorChooser.showDialog(AnnotationToolBar.this, "Foreground", klass.getColor());//.getColor();
                        if (result == null) {
                            return;
                        }
                        klass.setColor(result);
                    }
                    current_class = klass;

                    updateVisualisation();
                    lastTimeClicked[0] = System.currentTimeMillis();
                }
            });
            button.setMaximumSize(new Dimension(45, 45));
            content.add(button);
            buttons.add(button);
        }

        /*
        {
            backgroundButton = new JToggleButton("N");
            backgroundButton.setSize(45, 45);
            backgroundButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (System.currentTimeMillis() - lastTimeClicked[0] < double_click_delta_time) {
                        Color result = JColorChooser.showDialog(AnnotationToolBar.this, "Background", backgroundColor);//.getColor();
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
*/

        {
            JToggleButton addNewButton = new JToggleButton("+");
            addNewButton.setSize(45, 45);
            addNewButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ClassificationClass new_class = new ClassificationClass(classes.size() + 1);
                    classes.add(new_class);
                    current_class = new_class;
                    buildUI();
                }
            });
            content.add(addNewButton);
            buttons.add(addNewButton);
        }

        {
            JToggleButton eraseButton = new JToggleButton("E");
            eraseButton.setSize(45, 45);
            eraseButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    current_class = erase;
                    updateVisualisation();
                }
            });
            content.add(eraseButton);
            buttons.add(eraseButton);
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
            content.add(slider);
        }

        if (contentPanel != null) {
            this.remove(contentPanel);
        }
        contentPanel = content;
        //contentPanel.setSize(45, slider.getHeight() + slider.getY());
        add(contentPanel);

        updateVisualisation();
    }

    private void reposition() {
        //IJ.log(IJ.getToolName());
        try {

            if (AnnotationTool.imp == null) {
                setVisible(false);
                return;
            }
            if (AnnotationTool.imp.getWindow() == null || !AnnotationTool.imp.getWindow().isVisible()) {
                setVisible(false);
                return;
            }


            if (IJ.getToolName().compareTo(AnnotationTool.TOOL_NAME) == 0) {
                setVisible(true);
                //IJ.log("Set pos" + AnnotationTool.imp.getWindow().getX());
                this.setLocation(Math.max(AnnotationTool.imp.getWindow().getX() - getWidth(), 0), AnnotationTool.imp.getWindow().getY() + 45);
            } else {
                setVisible(false);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        if (this.getHeight() < slider.getHeight() + slider.getY()) {
            updateVisualisation();
        }
    }

    void updateVisualisation() {

        for (int i = 0; i < classes.size(); i++ ) {
            buttons.get(i).setSelected(current_class == classes.get(i));
            buttons.get(i).setForeground(current_class == classes.get(i)?classes.get(i).getColor():null);
        }
        buttons.get(buttons.size() - 1).setSelected(current_class == erase);
        buttons.get(buttons.size() - 1).setForeground(current_class == erase?erase.getColor():null);

        if (contentPanel != null) {
            contentPanel.setSize(45, slider.getHeight() + slider.getY());
        }
        setSize(45, slider.getHeight() + slider.getY());

        RoiManager rm = RoiManager.getInstance();
        if (rm == null) {
            return;
        }
        AnnotationTool.showAll(rm);


    }

    public static void main(String[] args) {
        new ImageJ();

        ImagePlus imp = IJ.openImage("C:/structure/data/blobs.tif");
        imp.show();

        Toolbar.addPlugInTool(new AnnotationTool());
    }

    public void setVisible(boolean value) {
        if (isVisible() != value) {
            super.setVisible(value);
        }
    }
}
