// To make this script run in Fiji, please activate 
// the clij and clij2 update sites in your Fiji 
// installation. Read more: https://clij.github.io

// Init GPU
run("CLIJ2 Macro Extensions", "cl_device=");

// Overview
//  * Copy 
//    * Threshold Otsu 
//    * Binary Weka Pixel Classifier 
//      * Connected Components Labeling 
//        * Weka Label Classifier 
// 
image1 = "blobs.gif";
Ext.CLIJ2_push(image1);

// Copy
// image1 = "blobs.gif";
// image2 = "CLIJx Image of blobs.gif";
Ext.CLIJ2_copy(image1, image2);
Ext.CLIJ2_pull(image2); // consider removing this line if you don't need to see that image

// Threshold Otsu
// image2 = "CLIJx Image of blobs.gif";
// image3 = "Threshold Otsu of CLIJx Image of blobs.gif";
Ext.CLIJ2_thresholdOtsu(image2, image3);
Ext.CLIJ2_pull(image3); // consider removing this line if you don't need to see that image

// Binary Weka Pixel Classifier
// image2 = "CLIJx Image of blobs.gif";
// image4 = "Binary Weka Pixel Classifier of CLIJx Image of blobs.gif";
features = "original gaussianblur=1 gaussianblur=5 sobelofgaussian=1 sobelofgaussian=5";
modelfilename = "file.model";
Ext.CLIJx_binaryWekaPixelClassifier(image2, image4, features, modelfilename);
Ext.CLIJ2_pull(image4); // consider removing this line if you don't need to see that image

// Connected Components Labeling
// image4 = "Binary Weka Pixel Classifier of CLIJx Image of blobs.gif";
// image5 = "Connected Components Labeling of Binary Weka Pixel Classifier of CLIJx Image of blobs.gif";
Ext.CLIJ2_connectedComponentsLabelingBox(image4, image5);
Ext.CLIJ2_pull(image5); // consider removing this line if you don't need to see that image

// Weka Label Classifier
// image5 = "Connected Components Labeling of Binary Weka Pixel Classifier of CLIJx Image of blobs.gif";
// image6 = "Weka Label Classifier of Connected Components Labeling of Binary Weka Pixel Classifier of CLIJx Image of blobs.gif";
destination = "net.haesleinhuepf.clijx.assistant.interactive.handcrafted.WekaLabelClassifier ClearCLBuffer [mClearCLContext=ClearCLContext [device=ClearCLDevice [mClearCLPlatform=ClearCLPlatform [name=NVIDIA CUDA], name=GeForce RTX 2080 Ti]], mNativeType=Float, mNumberOfChannels=1, mDimensions=[256, 254], getMemAllocMode()=Best, getHostAccessType()=ReadWrite, getKernelAccessType()=ReadWrite, getBackend()=net.haesleinhuepf.clij.clearcl.backend.jocl.ClearCLBackendJOCL@403e1642, getPeerPointer()=net.haesleinhuepf.clij.clearcl.ClearCLPeerPointer@7451f7a2]";
features = "MEAN_INTENSITY STANDARD_DEVIATION_INTENSITY MAX_MEAN_DISTANCE_TO_CENTROID_RATIO PIXEL_COUNT count_touching_neighbors average_distance_of_touching_neighbors";
modelfilename = "label_classification.model";
Ext.CLIJx_wekaLabelClassifier(image5, image6, destination, features, modelfilename);
Ext.CLIJ2_pull(image6); // consider removing this line if you don't need to see that image


