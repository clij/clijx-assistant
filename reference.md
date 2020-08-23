## CLIIJx-incubator operations
This is the list of currently supported [CLIJ2](https://clij.github.io/) and [CLIJx](https://clij.github.io/clijx) operations.

Please note: CLIJx-Incubator is under development. Hence, this list is subject to change.

* [Absolute](https://clij.github.io/clij2-docs/reference_absolute)
Computes the absolute value of every individual pixel x in a given image.

* [Automatic Threshold](https://clij.github.io/clij2-docs/reference_automaticThreshold)
The automatic thresholder utilizes the threshold methods from ImageJ on a histogram determined on 
the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.

* [Average Distance Of N Closest Neighbors Map](https://clij.github.io/clij2-docs/reference_averageDistanceOfNClosestNeighborsMap)
Takes a label map, determines distances between all centroids and replaces every label with the average distance to the n closest neighboring labels.

* [Average Neighbor Distance Map](https://clij.github.io/clij2-docs/reference_averageNeighborDistanceMap)
Takes a label map, determines which labels touch and replaces every label with the average distance to their neighboring labels.

* [Bilateral](https://clij.github.io/clij2-docs/reference_bilateral)
Applies a bilateral filter using a box neighborhood with sigma weights for space and intensity to the input image.

* [Binary Edge Detection](https://clij.github.io/clij2-docs/reference_binaryEdgeDetection)
Determines pixels/voxels which are on the surface of binary objects and sets only them to 1 in the 
destination image. All other pixels are set to 0.

* [Binary Fill Holes Slice By Slice](https://clij.github.io/clij2-docs/reference_binaryFillHolesSliceBySlice)
Fills holes (pixels with value 0 surrounded by pixels with value 1) in a binary image stack slice by slice.

* [Binary Fill Holes](https://clij.github.io/clij2-docs/reference_binaryFillHoles)
Fills holes (pixels with value 0 surrounded by pixels with value 1) in a binary image.

* [Binary Not](https://clij.github.io/clij2-docs/reference_binaryNot)
Computes a binary image (containing pixel values 0 and 1) from an image X by negating its pixel values
x using the binary NOT operator !

* [Bottom Hat](https://clij.github.io/clij2-docs/reference_bottomHatBox)
Apply a bottom-hat filter for background subtraction to the input image.

* [Centroids Of Background And Labels](https://clij.github.io/clij2-docs/reference_centroidsOfBackgroundAndLabels)
Determines the centroids of the background and all labels in a label image or image stack. 

* [Closing](https://clij.github.io/clij2-docs/reference_closingBox)
Apply a binary closing to the input image by calling n dilations and n erosions subsequenntly.

* [Connected Components Labeling](https://clij.github.io/clij2-docs/reference_connectedComponentsLabelingBox)
Performs connected components analysis inspecting the box neighborhood of every pixel to a binary image and generates a label map.

* [Copy Slice](https://clij.github.io/clij2-docs/reference_copySlice)
This method has two purposes: 
It copies a 2D image to a given slice z position in a 3D image stack or 
It copies a given slice at position z in an image stack to a 2D image.

* [Count Non Zero Voxels Sphere](https://clij.github.io/clij2-docs/reference_countNonZeroVoxels3DSphere)
Counts non-zero voxels in a sphere around every voxel. 

* [Crop](https://clij.github.io/clij2-docs/reference_crop3D)
Crops a given sub-stack out of a given image stack. 

* [Cylinder Transform](https://clij.github.io/clij2-docs/reference_cylinderTransform)
Applies a cylinder transform to an image stack assuming the center line goes in Y direction in the center of the stack.

* [Detect And Label Maxima Above Threshold](https://clij.github.io/clij2-docs/reference_detectAndLabelMaximaAboveThreshold)
Determines maximum regions in a Gaussian blurred version of the original image and excludes found pixels below a given intensity in the blurred image.

* [Detect And Label Maxima](https://clij.github.io/clij2-docs/reference_detectAndLabelMaxima)
Determines maximum regions in a Gaussian blurred version of the original image.

* [Detect Label Edges](https://clij.github.io/clij2-docs/reference_detectLabelEdges)
Takes a labelmap and returns an image where all pixels on label edges are set to 1 and all other pixels to 0.

* [Difference Of Gaussian](https://clij.github.io/clij2-docs/reference_differenceOfGaussian3D)
Applies Gaussian blur to the input image twice with different sigma values resulting in two images which are then subtracted from each other.

* [Distance Map](https://clij.github.io/clij2-docs/reference_distanceMap)
Generates a distance map from a binary image. 

* [Divide By Gaussian Background](https://clij.github.io/clij2-docs/reference_divideByGaussianBackground)
Applies Gaussian blur to the input image and divides the original by the result.

* [Draw Distance Mesh Between Touching Labels](https://clij.github.io/clij2-docs/reference_drawDistanceMeshBetweenTouchingLabels)
Starting from a label map, draw lines between touching neighbors resulting in a mesh.

* [Draw Mesh Between Touching Labels](https://clij.github.io/clij2-docs/reference_drawMeshBetweenTouchingLabels)
Starting from a label map, draw lines between touching neighbors resulting in a mesh.

* [Draw Touch Count Mesh Between Touching Labels](https://clij.github.io/clij2-docs/reference_drawTouchCountMeshBetweenTouchingLabels)
Starting from a label map, draw lines between touching neighbors resulting in a mesh.

* [Draw Touch Portion Mesh Between Touching Labels](https://clij.github.io/clij2-docs/reference_drawTouchPortionMeshBetweenTouchingLabels)
Starting from a label map, draw lines between touching neighbors resulting in a mesh.

* [Drift Correction By Center Of Mass Fixation](https://clij.github.io/clij2-docs/reference_driftCorrectionByCenterOfMassFixation)
Determines the centerOfMass of the image stack and translates it so that it stays in a defined position.

* [Drift Correction By Centroid Fixation](https://clij.github.io/clij2-docs/reference_driftCorrectionByCentroidFixation)
Threshold the image stack, determines the centroid of the resulting binary image and 
translates the image stack so that its centroid sits in a defined position.

* [Entropy](https://clij.github.io/clij2-docs/reference_entropyBox)
Determines the local entropy in a box with a given radius around every pixel.

* [Equal Constant](https://clij.github.io/clij2-docs/reference_equalConstant)
Determines if an image A and a constant b are equal.

* [Equalize Mean Intensities Of Slices](https://clij.github.io/clij2-docs/reference_equalizeMeanIntensitiesOfSlices)
Determines correction factors for each z-slice so that the average intensity in all slices can be made the same and multiplies these factors with the slices. 

* [Exclude Labels On Edges](https://clij.github.io/clij2-docs/reference_excludeLabelsOnEdges)
Removes all labels from a label map which touch the edges of the image (in X, Y and Z if the image is 3D). 

* [Exclude Labels Outside Size Range](https://clij.github.io/clij2-docs/reference_excludeLabelsOutsideSizeRange)
Removes labels from a label map which are not within a certain size range.

* [Exponential](https://clij.github.io/clij2-docs/reference_exponential)
Computes base exponential of all pixels values.

* [Extend Labeling Via Voronoi](https://clij.github.io/clij2-docs/reference_extendLabelingViaVoronoi)
Takes a label map image and dilates the regions using a octagon shape until they touch. 

* [Extend Labels With Maximum Radius](https://clij.github.io/clij2-docs/reference_extendLabelsWithMaximumRadius)
Extend labels with a given radius.

* [Find And Label Maxima](https://clij.github.io/clij2-docs/reference_findAndLabelMaxima)
Determine maxima with a given tolerance to surrounding maxima and background and label them.

* [Find Maxima Plateaus](https://clij.github.io/clij2-docs/reference_findMaximaPlateaus)
Finds local maxima, which might be groups of pixels with the same intensity and marks them in a binary image.

* [Flip](https://clij.github.io/clij2-docs/reference_flip3D)
Flips an image in X, Y and/or Z direction depending on boolean flags.

* [Gaussian Blur](https://clij.github.io/clij2-docs/reference_gaussianBlur3D)
Computes the Gaussian blurred image of an image given two sigma values in X, Y and Z. 

* [Generate Grey Value Cooccurrence Matrix](https://clij.github.io/clij2-docs/reference_generateGreyValueCooccurrenceMatrixBox)
Takes an image and an intensity range to determine a grey value co-occurrence matrix.

* [Greater Constant](https://clij.github.io/clij2-docs/reference_greaterConstant)
Determines if two images A and B greater pixel wise. 

* [Greater Or Equal Constant](https://clij.github.io/clij2-docs/reference_greaterOrEqualConstant)
Determines if two images A and B greater or equal pixel wise. 

* [Grey Level Atttribute Filtering](https://clij.github.io/clij2-docs/reference_greyLevelAtttributeFiltering)
Todo.

* [Intensity Correction Above Threshold Otsu](https://clij.github.io/clij2-docs/reference_intensityCorrectionAboveThresholdOtsu)
Determines the mean intensity of all pixel the image stack which are above a determined Threshold (Otsu et al. 1979) and multiplies it with a factor so that the mean intensity becomes equal to a given value.

* [Intensity Correction](https://clij.github.io/clij2-docs/reference_intensityCorrection)
Determines the mean intensity of the image stack and multiplies it with a factor so that the mean intensity becomes equal to a given value.

* [Invert](https://clij.github.io/clij2-docs/reference_invert)
Computes the negative value of all pixels in a given image. 

* [Label Maximum Extension Map](https://clij.github.io/clij2-docs/reference_labelMaximumExtensionMap)
Takes a label map, determines for every label the maximum distance of any pixel to the centroid and replaces every label with the that number.

* [Label Maximum Extension Ratio Map](https://clij.github.io/clij2-docs/reference_labelMaximumExtensionRatioMap)
Takes a label map, determines for every label the maximum distance of any pixel to the centroid and replaces every label with the that number.

* [Label Pixel Count Map](https://clij.github.io/clij2-docs/reference_labelPixelCountMap)
Takes a label map, determines the number of pixels per label and replaces every label with the that number.

* [Label Spots](https://clij.github.io/clij2-docs/reference_labelSpots)
Transforms a binary image with single pixles set to 1 to a labelled spots image. 

* [Label To Mask](https://clij.github.io/clij2-docs/reference_labelToMask)
Masks a single label in a label map. 

* [Label Voronoi Octagon](https://clij.github.io/clij2-docs/reference_labelVoronoiOctagon)
Takes a labelled image and dilates the labels using a octagon shape until they touch. 

* [Labeling Workflow A L X](https://clij.github.io/clij2-docs/reference_labelingWorkflowALX)
A segmentation workflow using maxima detection, thresholding, maximum filters and label edge detection.

* [Laplace Sphere](https://clij.github.io/clij2-docs/reference_laplaceSphere)
Applies the Laplace operator (Diamond neighborhood) to an image.

* [Laplace](https://clij.github.io/clij2-docs/reference_laplaceBox)
Applies the Laplace operator (Box neighborhood) to an image.

* [Laplacian Of Gaussian](https://clij.github.io/clij2-docs/reference_laplacianOfGaussian3D)
Determined the Laplacian of Gaussian of a give input image with a given sigma.

* [Local Maximum Average Distance Of N Closest Neighbors Map](https://clij.github.io/clij2-docs/reference_localMaximumAverageDistanceOfNClosestNeighborsMap)
Takes a label map, determines distances between all centroids, the mean distance of the n closest points for every point
 and replaces every label with the maximum distance of touching labels.

* [Local Maximum Average Neighbor Distance Map](https://clij.github.io/clij2-docs/reference_localMaximumAverageNeighborDistanceMap)
Takes a label map, determines which labels touch, the distance between their centroids and the maximum distancebetween touching neighbors. It then replaces every label with the that value.

* [Local Maximum Touching Neighbor Count Map](https://clij.github.io/clij2-docs/reference_localMaximumTouchingNeighborCountMap)
Takes a label map, determines which labels touch, determines for every label with the number of touching 
neighboring labels and replaces the label index with the local maximum of this count.

* [Local Mean Average Distance Of N Closest Neighbors Map](https://clij.github.io/clij2-docs/reference_localMeanAverageDistanceOfNClosestNeighborsMap)
Takes a label map, determines distances between all centroids, the mean distance of the n closest points for every point
 and replaces every label with the mean distance of touching labels.

* [Local Mean Average Neighbor Distance Map](https://clij.github.io/clij2-docs/reference_localMeanAverageNeighborDistanceMap)
Takes a label map, determines which labels touch, the distance between their centroids and the mean distancebetween touching neighbors. It then replaces every label with the that value.

* [Local Mean Touch Portion Map](https://clij.github.io/clij2-docs/reference_localMeanTouchPortionMap)
Takes a label map, determines which labels touch and how much, relatively taking the whole outline of 
each label into account, and determines for every label with the mean of this value and replaces the 
label index with that value.

* [Local Mean Touching Neighbor Count Map](https://clij.github.io/clij2-docs/reference_localMeanTouchingNeighborCountMap)
Takes a label map, determines which labels touch, determines for every label with the number of touching 
neighboring labels and replaces the label index with the local mean of this count.

* [Local Median Average Distance Of N Closest Neighbors Map](https://clij.github.io/clij2-docs/reference_localMedianAverageDistanceOfNClosestNeighborsMap)
Takes a label map, determines distances between all centroids, the mean distance of the n closest points for every point
 and replaces every label with the median distance of touching labels.

* [Local Median Average Neighbor Distance Map](https://clij.github.io/clij2-docs/reference_localMedianAverageNeighborDistanceMap)
Takes a label map, determines which labels touch, the distance between their centroids and the median distancebetween touching neighbors. It then replaces every label with the that value.

* [Local Median Touching Neighbor Count Map](https://clij.github.io/clij2-docs/reference_localMedianTouchingNeighborCountMap)
Takes a label map, determines which labels touch, determines for every label with the number of touching 
neighboring labels and replaces the label index with the local median of this count.

* [Local Minimum Average Distance Of N Closest Neighbors Map](https://clij.github.io/clij2-docs/reference_localMinimumAverageDistanceOfNClosestNeighborsMap)
Takes a label map, determines distances between all centroids, the mean distance of the n closest points for every point
 and replaces every label with the minimum distance of touching labels.

* [Local Minimum Average Neighbor Distance Map](https://clij.github.io/clij2-docs/reference_localMinimumAverageNeighborDistanceMap)
Takes a label map, determines which labels touch, the distance between their centroids and the minimum distancebetween touching neighbors. It then replaces every label with the that value.

* [Local Minimum Touching Neighbor Count Map](https://clij.github.io/clij2-docs/reference_localMinimumTouchingNeighborCountMap)
Takes a label map, determines which labels touch, determines for every label with the number of touching 
neighboring labels and replaces the label index with the local minimum of this count.

* [Local Standard Deviation Average Distance Of N Closest Neighbors Map](https://clij.github.io/clij2-docs/reference_localStandardDeviationAverageDistanceOfNClosestNeighborsMap)
Takes a label map, determines distances between all centroids, the mean distance of the n closest points for every point
 and replaces every label with the standard deviation distance of touching labels.

* [Local Standard Deviation Average Neighbor Distance Map](https://clij.github.io/clij2-docs/reference_localStandardDeviationAverageNeighborDistanceMap)
Takes a label map, determines which labels touch, the distance between their centroids and the standard deviation distancebetween touching neighbors. It then replaces every label with the that value.

* [Local Standard Deviation Touching Neighbor Count Map](https://clij.github.io/clij2-docs/reference_localStandardDeviationTouchingNeighborCountMap)
Takes a label map, determines which labels touch, determines for every label with the number of touching 
neighboring labels and replaces the label index with the local standard deviation of this count.

* [Logarithm](https://clij.github.io/clij2-docs/reference_logarithm)
Computes base e logarithm of all pixels values.

* [Make Isotropic](https://clij.github.io/clij2-docs/reference_makeIsotropic)
Applies a scaling operation using linear interpolation to generate an image stack with a given isotropic voxel size.

* [Maximum Image And Scalar](https://clij.github.io/clij2-docs/reference_maximumImageAndScalar)
Computes the maximum of a constant scalar s and each pixel value x in a given image X. 

* [Maximum Z Projection Bounded](https://clij.github.io/clij2-docs/reference_maximumZProjectionBounded)
Determines the maximum intensity projection of an image along Z within a given z range.

* [Maximum Z Projection](https://clij.github.io/clij2-docs/reference_maximumZProjection)
Determines the maximum intensity projection of an image along Z.

* [Maximum](https://clij.github.io/clij2-docs/reference_maximum3DBox)
Computes the local maximum of a pixels cube neighborhood. 

* [Mean Z Projection Above Threshold](https://clij.github.io/clij2-docs/reference_meanZProjectionAboveThreshold)
Determines the mean average intensity projection of an image along Z but only for pixels above a given threshold.

* [Mean Z Projection](https://clij.github.io/clij2-docs/reference_meanZProjection)
Determines the mean average intensity projection of an image along Z.

* [Mean](https://clij.github.io/clij2-docs/reference_mean3DBox)
Computes the local mean average of a pixels cube neighborhood. 

* [Median Z Projection](https://clij.github.io/clij2-docs/reference_medianZProjection)
Determines the median intensity projection of an image stack along Z.

* [Median](https://clij.github.io/clij2-docs/reference_median3DBox)
Computes the local median of a pixels cuboid neighborhood. 

* [Merge Touching Labels](https://clij.github.io/clij2-docs/reference_mergeTouchingLabels)


* [Minimum Z Projection](https://clij.github.io/clij2-docs/reference_minimumZProjection)
Determines the minimum intensity projection of an image along Z.

* [Minimum](https://clij.github.io/clij2-docs/reference_minimum3DBox)
Computes the local minimum of a pixels cube neighborhood. 

* [Multiply Image And Scalar](https://clij.github.io/clij2-docs/reference_multiplyImageAndScalar)
Multiplies all pixels value x in a given image X with a constant scalar s.

* [Non Local Means](https://clij.github.io/clij2-docs/reference_nonLocalMeans)
Applies a non-local means filter using a box neighborhood with a Gaussian weight specified with sigma to the input image.

* [Not Equal Constant](https://clij.github.io/clij2-docs/reference_notEqualConstant)
Determines if two images A and B equal pixel wise.

* [Opening](https://clij.github.io/clij2-docs/reference_openingBox)
Apply a binary opening to the input image by calling n erosions and n dilations subsequenntly.

* [Parametric Watershed](https://clij.github.io/clij2-docs/reference_parametricWatershed)
Apply a binary watershed to a binary image and introduce black pixels between objects.

* [Power](https://clij.github.io/clij2-docs/reference_power)
Computes all pixels value x to the power of a given exponent a.

* [Pull To ROIManager](https://clij.github.io/clij2-docs/reference_pullToROIManager)
Pulls a binary image from the GPU memory and puts it in the ROI Manager.

* [Reslice Bottom](https://clij.github.io/clij2-docs/reference_resliceBottom)
Flippes Y and Z axis of an image stack. This operation is similar to ImageJs 'Reslice [/]' method but
offers less flexibility such as interpolation.

* [Reslice Left](https://clij.github.io/clij2-docs/reference_resliceLeft)
Flippes X, Y and Z axis of an image stack. This operation is similar to ImageJs 'Reslice [/]' method 
 but offers less flexibility such as interpolation.

* [Reslice Right](https://clij.github.io/clij2-docs/reference_resliceRight)
Flippes X, Y and Z axis of an image stack. This operation is similar to ImageJs 'Reslice [/]' method 
 but offers less flexibility such as interpolation.

* [Reslice Top](https://clij.github.io/clij2-docs/reference_resliceTop)
Flippes Y and Z axis of an image stack. This operation is similar to ImageJs 'Reslice [/]' method but
offers less flexibility such as interpolation.

* [Rigid Transform](https://clij.github.io/clij2-docs/reference_rigidTransform)
Applies a rigid transform using linear interpolation to an image stack.

* [Rotate Clockwise](https://clij.github.io/clij2-docs/reference_rotateClockwise)
Rotates a given input image by 90 degrees clockwise. 

* [Rotate Counter Clockwise](https://clij.github.io/clij2-docs/reference_rotateCounterClockwise)
Rotates a given input image by 90 degrees counter-clockwise. 

* [Rotate Right](https://clij.github.io/clij2-docs/reference_rotateRight)
Rotates a given input image by 90 degrees counter-clockwise. 

* [Rotate](https://clij.github.io/clij2-docs/reference_rotate3D)
Rotates an image stack in 3D. 

* [Smaller Constant](https://clij.github.io/clij2-docs/reference_smallerConstant)
Determines if two images A and B smaller pixel wise.

* [Sobel](https://clij.github.io/clij2-docs/reference_sobel)
Convolve the image with the Sobel kernel.

* [Sphere Transform](https://clij.github.io/clij2-docs/reference_sphereTransform)
Turns an image stack in XYZ cartesian coordinate system to an AID polar coordinate system.

* [Standard Deviation Z Projection](https://clij.github.io/clij2-docs/reference_standardDeviationZProjection)
Determines the standard deviation intensity projection of an image stack along Z.

* [Subtract Gaussian Background](https://clij.github.io/clij2-docs/reference_subtractGaussianBackground)
Applies Gaussian blur to the input image and subtracts the result from the original image.

* [Subtract Image From Scalar](https://clij.github.io/clij2-docs/reference_subtractImageFromScalar)
Subtracts one image X from a scalar s pixel wise.

* [Sum Z Projection](https://clij.github.io/clij2-docs/reference_sumZProjection)
Determines the sum intensity projection of an image along Z.

* [Threshold Default](https://clij.github.io/clij2-docs/reference_thresholdDefault)
The automatic thresholder utilizes the Default threshold method implemented in ImageJ using a histogram determined on 
the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.

* [Threshold DoG](https://clij.github.io/clij2-docs/reference_thresholdDoG)
Applies a Difference-of-Gaussian filter to an image and thresholds it with given sigma and threshold values.

* [Threshold Huang](https://clij.github.io/clij2-docs/reference_thresholdHuang)
The automatic thresholder utilizes the Huang threshold method implemented in ImageJ using a histogram determined on 
the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.

* [Threshold IJ Iso Data](https://clij.github.io/clij2-docs/reference_thresholdIJ_IsoData)
The automatic thresholder utilizes the IJ_IsoData threshold method implemented in ImageJ using a histogram determined on 
the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.

* [Threshold Intermodes](https://clij.github.io/clij2-docs/reference_thresholdIntermodes)
The automatic thresholder utilizes the Intermodes threshold method implemented in ImageJ using a histogram determined on 
the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.

* [Threshold Iso Data](https://clij.github.io/clij2-docs/reference_thresholdIsoData)
The automatic thresholder utilizes the IsoData threshold method implemented in ImageJ using a histogram determined on 
the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.

* [Threshold Li](https://clij.github.io/clij2-docs/reference_thresholdLi)
The automatic thresholder utilizes the Li threshold method implemented in ImageJ using a histogram determined on 
the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.

* [Threshold Max Entropy](https://clij.github.io/clij2-docs/reference_thresholdMaxEntropy)
The automatic thresholder utilizes the MaxEntropy threshold method implemented in ImageJ using a histogram determined on 
the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.

* [Threshold Mean](https://clij.github.io/clij2-docs/reference_thresholdMean)
The automatic thresholder utilizes the Mean threshold method implemented in ImageJ using a histogram determined on 
the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.

* [Threshold Min Error](https://clij.github.io/clij2-docs/reference_thresholdMinError)
The automatic thresholder utilizes the MinError threshold method implemented in ImageJ using a histogram determined on 
the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.

* [Threshold Minimum](https://clij.github.io/clij2-docs/reference_thresholdMinimum)
The automatic thresholder utilizes the Minimum threshold method implemented in ImageJ using a histogram determined on 
the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.

* [Threshold Moments](https://clij.github.io/clij2-docs/reference_thresholdMoments)
The automatic thresholder utilizes the Moments threshold method implemented in ImageJ using a histogram determined on 
the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.

* [Threshold Otsu](https://clij.github.io/clij2-docs/reference_thresholdOtsu)
The automatic thresholder utilizes the Otsu threshold method implemented in ImageJ using a histogram determined on 
the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.

* [Threshold Percentile](https://clij.github.io/clij2-docs/reference_thresholdPercentile)
The automatic thresholder utilizes the Percentile threshold method implemented in ImageJ using a histogram determined on 
the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.

* [Threshold Renyi Entropy](https://clij.github.io/clij2-docs/reference_thresholdRenyiEntropy)
The automatic thresholder utilizes the RenyiEntropy threshold method implemented in ImageJ using a histogram determined on 
the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.

* [Threshold Shanbhag](https://clij.github.io/clij2-docs/reference_thresholdShanbhag)
The automatic thresholder utilizes the Shanbhag threshold method implemented in ImageJ using a histogram determined on 
the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.

* [Threshold Triangle](https://clij.github.io/clij2-docs/reference_thresholdTriangle)
The automatic thresholder utilizes the Triangle threshold method implemented in ImageJ using a histogram determined on 
the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.

* [Threshold Yen](https://clij.github.io/clij2-docs/reference_thresholdYen)
The automatic thresholder utilizes the Yen threshold method implemented in ImageJ using a histogram determined on 
the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.

* [Top Hat](https://clij.github.io/clij2-docs/reference_topHatBox)
Applies a top-hat filter for background subtraction to the input image.

* [Touching Neighbor Count Map](https://clij.github.io/clij2-docs/reference_touchingNeighborCountMap)
Takes a label map, determines which labels touch and replaces every label with the number of touching neighboring labels.

* [Translate](https://clij.github.io/clij2-docs/reference_translate3D)
Translate an image stack in X, Y and Z.

* [Transpose XY](https://clij.github.io/clij2-docs/reference_transposeXY)
Transpose X and Y axes of an image.

* [Transpose XZ](https://clij.github.io/clij2-docs/reference_transposeXZ)
Transpose X and Z axes of an image.

* [Transpose YZ](https://clij.github.io/clij2-docs/reference_transposeYZ)
Transpose Y and Z axes of an image.

* [Voronoi Labeling](https://clij.github.io/clij2-docs/reference_voronoiLabeling)
Takes a binary image, labels connected components and dilates the regions using a octagon shape until they touch. 

* [Voronoi Octagon](https://clij.github.io/clij2-docs/reference_voronoiOctagon)
Takes a binary image and dilates the regions using a octagon shape until they touch. 



137 operations listed.


Back to [CLIJx-Incubator](https://clij.github.io/incubator)
