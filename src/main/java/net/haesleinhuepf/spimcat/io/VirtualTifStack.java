package net.haesleinhuepf.spimcat.io;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;

public class VirtualTifStack extends ij.VirtualStack {
    private ArrayList<String> filenames;
    private String foldername;
    private final int depth;
    private int numberOfImageStacks;
    public static VirtualTifStack open(String foldername) throws FileNotFoundException {
        File folder = new File(foldername);

        File aFile = null;
        int numberOfImageStacks = 0;
        for (File file : folder.listFiles()) {
            String filename = file.getName().toLowerCase();
            if (file.isFile() && (filename.endsWith(".tif") || filename.endsWith(".tiff"))) {
                numberOfImageStacks++;
                if (aFile == null) {
                    aFile = file;
                }
            }
        }
        if (aFile == null) {
            throw new FileNotFoundException("Folder '" + foldername + "' doesn't contain tif files.");
        }

        ImagePlus imp = IJ.openImage(aFile.getAbsolutePath());
        int width = imp.getWidth();
        int height = imp.getWidth();
        int depth = imp.getNSlices();

        return new VirtualTifStack(foldername, width, height, depth, numberOfImageStacks);
    }

    public VirtualTifStack(String foldername, int width, int height, int depth, int numberOfImageStacks) {
        super(width, height, depth * numberOfImageStacks);
        this.foldername = foldername;
        if (!this.foldername.endsWith("/")) {
            this.foldername = this.foldername + "/";
        }

        this.depth = depth;
        this.numberOfImageStacks = numberOfImageStacks;

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
        int stackNumber = n / depth / channels;
        int sliceNumber = n % (depth * channels);
        int channel = sliceNumber % channels;

        //System.out.println("c/s/f " + channel + "/" + sliceNumber + "/" + stackNumber );

        ImagePlus imp = cachedStack(stackNumber, channel);
        if (sliceNumber <= imp.getNSlices()) {
            imp.setZ(sliceNumber);
        }
        return imp.getProcessor();
    }

    private ImagePlus[] cache = new ImagePlus[1];
    private int cacheN = -1;
    ImagePlus cachedStack(int n, int c) {
        if (n >= filenames.size()) {
            return new ImagePlus("", super.getProcessor(n));
        }
        synchronized (this) {
            if (cacheN != n) {
                for (int i = 0; i < channels; i++) {
                    int pos = (n + i * numberOfImageStacks / channels);
                    System.out.println("load " + pos);
                    ImagePlus imp = IJ.openImage(foldername + filenames.get(pos));
                    cache[i] = imp;
                }
                cacheN = n;
            }
        }
        return cache[c];
    }


    @Override
    public int size() {
        return numberOfImageStacks * depth;
    }

    @Override
    public int getSize() {
        return numberOfImageStacks * depth;
    }

    public int getDepth() {
        return depth;
    }

    public static void main(String... args) throws FileNotFoundException {
        VirtualTifStack stack = VirtualTifStack.open("C:\\structure\\data\\William_LLSM_data\\deconvolved data\\");
        stack.switchChannelsAndFrames(2);
    }

    int channels = 1;
    void switchChannelsAndFrames(int channels) {
        this.channels = channels;
        cache = new ImagePlus[channels];
        /*
        ArrayList<String> formerFilenames = filenames;
        filenames = new ArrayList<String>();

        for (int i = 0; i < formerFilenames.size(); i++ ){
            int j = i / channels + (i % channels) * formerFilenames.size() / channels;
            System.out.println("i " + i + " j " + j);
            filenames.add(formerFilenames.get(j));
        }*/
    }
}
