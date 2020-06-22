package net.haesleinhuepf.spimcat.io;

import ij.measure.Calibration;
import ij.plugin.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;

import ij.*;
import ij.io.*;
import ij.gui.*;
import ij.process.*;


/**
 * VirtualRawStack
 * <p>
 * <p>
 * <p>
 * Author: @haesleinhuepf
 * 12 2019
 */
public class VirtualRawStack extends ij.VirtualStack {

    private String foldername;
    private boolean intelByteOrder;
    private double pixelSizeX;
    private double pixelSizeY;
    private double pixelSizeZ;
    private String pixelUnit;
    private int width;
    private int height;
    private int depth;
    private int numberOfImageStacks;
    private int bitDepth;
    ArrayList<String> filenames;

    public VirtualRawStack(String foldername, int width, int height, int depth, int numberOfImageStacks, int bitDepth, boolean intelByteOrder, double pixelSizeX, double pixelSizeY, double pixelSizeZ, String pixelUnit) {
        super(width, height, depth * numberOfImageStacks);
        this.foldername = foldername;
        this.intelByteOrder = intelByteOrder;
        this.pixelSizeX = pixelSizeX;
        this.pixelSizeY = pixelSizeY;
        this.pixelSizeZ = pixelSizeZ;
        this.pixelUnit = pixelUnit;
        this.foldername = this.foldername.replace("\\", "/");
        if (!this.foldername.endsWith("/")) {
            this.foldername = this.foldername + "/";
        }


        this.width = width;
        this.height = height;
        this.depth = depth;
        this.numberOfImageStacks = numberOfImageStacks;
        this.bitDepth = bitDepth;

        super.setBitDepth(bitDepth);

        filenames = new ArrayList<>();
        File folder = new File(foldername);
        if (folder.exists()) {
            for (File file : folder.listFiles()) {
                String filename = file.getName();
                if (filename.startsWith(".") || filename.toLowerCase().compareTo("thumbs.db") == 0 || filename.endsWith(".txt")) {
                    continue;
                }
                filenames.add(filename);
            }
            if (filenames.size() < numberOfImageStacks) {
                this.numberOfImageStacks = filenames.size();

            }
        }
        Collections.sort(filenames);
    }

    @Override
    public ImageProcessor getProcessor(int n) {
        int stackNumber = n / depth;
        int sliceNumber = n % depth;

        ImagePlus imp = cachedStack(stackNumber);
        if (sliceNumber <= imp.getNSlices()) {
            imp.setZ(sliceNumber);
        }
        return imp.getProcessor();
    }

    private ImagePlus cache = null;
    private int cacheN = -1;
    ImagePlus cachedStack(int n) {
        if (n >= filenames.size() || n < 0) {
            return new ImagePlus("", super.getProcessor(n));
        }
        synchronized (this) {
            if (cacheN != n) {
                // load ImagePlus
                FileInfo fileInfo = new FileInfo();
                fileInfo.fileName = foldername + filenames.get(n);
                fileInfo.fileFormat = FileInfo.RAW;
                if (bitDepth == 8) {
                    fileInfo.fileType = FileInfo.GRAY8;
                } else if (bitDepth == 16) {
                    fileInfo.fileType = FileInfo.GRAY16_UNSIGNED;
                } else {
                    fileInfo.fileType = FileInfo.GRAY32_FLOAT;
                }
                fileInfo.intelByteOrder = intelByteOrder;
                fileInfo.width = width;
                fileInfo.height = height;
                fileInfo.nImages = depth;

                ImagePlus imp = Raw.open(fileInfo.fileName, fileInfo);
                Calibration calibration = imp.getCalibration();
                calibration.pixelWidth = pixelSizeX;
                calibration.pixelHeight = pixelSizeY;
                calibration.pixelDepth = pixelSizeZ;
                calibration.setUnit(pixelUnit);

                cache = imp;
                cacheN = n;
            }
        }
        return cache;
    }

    @Override
    public int size() {
        return numberOfImageStacks * depth;
    }

    @Override
    public int getSize() {
        return numberOfImageStacks * depth;
    }
}

