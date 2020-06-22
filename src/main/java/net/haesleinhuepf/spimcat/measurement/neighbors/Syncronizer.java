package net.haesleinhuepf.spimcat.measurement.neighbors;

import ij.ImageListener;
import ij.ImagePlus;

import java.util.ArrayList;

public class Syncronizer implements ImageListener {
    private ArrayList<ImagePlus> synced;
    boolean syncing = false;

    public Syncronizer(ArrayList<ImagePlus> synced) {

        this.synced = synced;
    }

    @Override
    public void imageOpened(ImagePlus imagePlus) {

    }

    @Override
    public void imageClosed(ImagePlus imagePlus) {
        synced.remove(imagePlus);
    }

    @Override
    public void imageUpdated(ImagePlus imagePlus) {
        if (syncing) {
            return;
        }
        syncing = true;

        for (ImagePlus imp : synced){
            if (imp != imagePlus) {
                imp.setT(imagePlus.getT());
                imp.setZ(imagePlus.getZ());
            }
        }

        syncing = false;
    }
}
