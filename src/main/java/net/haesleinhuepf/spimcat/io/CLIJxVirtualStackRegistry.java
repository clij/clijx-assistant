package net.haesleinhuepf.spimcat.io;

import ij.*;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class CLIJxVirtualStackRegistry implements ImageListener {
    private static CLIJxVirtualStackRegistry instance;

    private CLIJxVirtualStackRegistry() {}

    public static CLIJxVirtualStackRegistry getInstance() {
        if (instance == null) {
            instance = new CLIJxVirtualStackRegistry();

            int delay = 100;
            Timer heartbeat = new Timer();
            heartbeat.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    instance.checkForClosedImages();
                }
            }, delay, delay);
            ImagePlus.addImageListener(instance);
        }
        return instance;
    }

    private HashMap<ImagePlus, CLIJxVirtualStack> cache = new HashMap<ImagePlus, CLIJxVirtualStack>();

    public void register(ImagePlus imp) {
        if (imp.getStack() instanceof CLIJxVirtualStack) {
            register(imp, (CLIJxVirtualStack) imp.getStack());
        }
    }

    public void register(ImagePlus imp, CLIJxVirtualStack stack) {
        if (cache.containsKey(imp)) {
            unregister(imp);
        }
        cache.put(imp, stack);
    }

    public CLIJxVirtualStack get(ImagePlus imp) {
        return cache.get(imp);
    }

    public void unregister(ImagePlus imp) {
        cache.remove(imp);
    }

    public void checkForClosedImages() {
        ArrayList<ImagePlus> list = new ArrayList<>();
        list.addAll(cache.keySet());
        for (ImagePlus search : list) {
            boolean found = false;
            int[] idlist = WindowManager.getIDList();
            if (idlist != null) {
                for (int i : idlist) {
                    ImagePlus imp = WindowManager.getImage(i);
                    if (imp == search) {
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                closeImage(search);
            }
        }
    }

    public synchronized void closeImage(ImagePlus imp) {
        ImageStack stack = get(imp);
        if (stack instanceof CLIJxVirtualStack) {
            unregister(imp);
            int num_channels = ((CLIJxVirtualStack) stack).getNumberOfChannels();
            if (num_channels == 1) {
                ClearCLBuffer buffer = ((CLIJxVirtualStack) stack).getBuffer(0);
                buffer.close();
            } else {
                for (int c = 0; c < num_channels; c++) {
                    ClearCLBuffer buffer = ((CLIJxVirtualStack) stack).getBuffer(c);
                    buffer.close();
                }
            }
        }
    }

    @Override
    public void imageOpened(ImagePlus imagePlus) {
        if (imagePlus.getStack() instanceof CLIJxVirtualStack) {
            register(imagePlus);
        }
    }

    @Override
    public void imageClosed(ImagePlus imagePlus) {

    }

    @Override
    public void imageUpdated(ImagePlus imagePlus) {

    }

    public String report() {
        StringBuilder output = new StringBuilder();
        output.append("CLIJxVirtualStackRegistry content:\n");
        for (ImagePlus key : cache.keySet()) {
            output.append(key.getTitle() + "\n");
        }
        return output.toString();
    }
}
