## CLIIJx-Assistant operations
This is the list of currently supported [CLIJ2](https://clij.github.io/) and [CLIJx](https://clij.github.io/clijx) operations.

Please note: CLIJx-Incubator is under development. Hence, this list is subject to change.

* [Absolute Difference](https://clij.github.io/clij2-docs/reference_absoluteDifference)
Determines the absolute difference pixel by pixel between two images.

* [Absolute](https://clij.github.io/clij2-docs/reference_absolute)
Computes the absolute value of every individual pixel x in a given image.

* [Add Image And Scalar](https://clij.github.io/clij2-docs/reference_addImageAndScalar)
Adds a scalar value s to all pixels x of a given image X.

* [Add Images Weighted](https://clij.github.io/clij2-docs/reference_addImagesWeighted)
Calculates the sum of pairs of pixels x and y from images X and Y weighted with factors a and b.

* [Add Images](https://clij.github.io/clij2-docs/reference_addImages)
Calculates the sum of pairs of pixels x and y of two images X and Y.

* [Automatic Threshold](https://clij.github.io/clij2-docs/reference_automaticThreshold)
The automatic thresholder utilizes the threshold methods from ImageJ on a histogram determined on 
the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.

* [Average Distance Of N Closest Neighbors Map](https://clij.github.io/clij2-docs/reference_averageDistanceOfNClosestNeighborsMap)
Takes a label map, determines distances between all centroids and replaces every label with the average distance to the n closest neighboring labels.

* [Average Neighbor Distance Map](https://clij.github.io/clij2-docs/reference_averageNeighborDistanceMap)
Takes a label map, determines which labels touch and replaces every label with the average distance to their neighboring labels.

* [Bilateral](https://clij.github.io/clij2-docs/reference_bilateral)
Applies a bilateral filter using a box neighborhood with sigma weights for space and intensity to the input image.

* [Binary And](https://clij.github.io/clij2-docs/reference_binaryAnd)
Computes a binary image (containing pixel values 0 and 1) from two images X and Y by connecting pairs of
pixels x and y with the binary AND operator &.
All pixel values except 0 in the input images are interpreted as 1.

* [Binary Edge Detection](https://clij.github.io/clij2-docs/reference_binaryEdgeDetection)
Determines pixels/voxels which are on the surface of binary objects and sets only them to 1 in the 
destination image. All other pixels are set to 0.

* [Binary Intersection](https://clij.github.io/clij2-docs/reference_binaryIntersection)
Computes a binary image (containing pixel values 0 and 1) from two images X and Y by connecting pairs of
pixels x and y with the binary intersection operator &.
All pixel values except 0 in the input images are interpreted as 1.

* [Binary Not](https://clij.github.io/clij2-docs/reference_binaryNot)
Computes a binary image (containing pixel values 0 and 1) from an image X by negating its pixel values
x using the binary NOT operator !

* [Binary Or](https://clij.github.io/clij2-docs/reference_binaryOr)
Computes a binary image (containing pixel values 0 and 1) from two images X and Y by connecting pairs of
pixels x and y with the binary OR operator |.

* [Binary Subtract](https://clij.github.io/clij2-docs/reference_binarySubtract)
Subtracts one binary image from another.

* [Binary Union](https://clij.github.io/clij2-docs/reference_binaryUnion)
Computes a binary image (containing pixel values 0 and 1) from two images X and Y by connecting pairs of
pixels x and y with the binary union operator |.

* [Binary Weka Pixel Classifier](https://clij.github.io/clij2-docs/reference_binaryWekaPixelClassifier)
Applies a pre-trained CLIJx-Weka model to a 2D image. 

* [Binary XOr](https://clij.github.io/clij2-docs/reference_binaryXOr)
Computes a binary image (containing pixel values 0 and 1) from two images X and Y by connecting pairs of
pixels x and y with the binary operators AND &, OR | and NOT ! implementing the XOR operator.

* [Bottom Hat](https://clij.github.io/clij2-docs/reference_bottomHatBox)
Apply a bottom-hat filter for background subtraction to the input image.

* [Closing](https://clij.github.io/clij2-docs/reference_closingBox)
Apply a binary closing to the input image by calling n dilations and n erosions subsequenntly.

* [Combine Horizontally](https://clij.github.io/clij2-docs/reference_combineHorizontally)
Combines two images or stacks in X.

* [Combine Vertically](https://clij.github.io/clij2-docs/reference_combineVertically)
Combines two images or stacks in Y.

* [Concatenate Stacks](https://clij.github.io/clij2-docs/reference_concatenateStacks)
Concatenates two stacks in Z.

* [Connected Components Labeling](https://clij.github.io/clij2-docs/reference_connectedComponentsLabelingBox)
Performs connected components analysis inspecting the box neighborhood of every pixel to a binary image and generates a label map.

* [Convolve](https://clij.github.io/clij2-docs/reference_convolve)
Convolve the image with a given kernel image.

* [Copy Slice](https://clij.github.io/clij2-docs/reference_copySlice)
This method has two purposes: 
It copies a 2D image to a given slice z position in a 3D image stack or 
It copies a given slice at position z in an image stack to a 2D image.

* [Count Non Zero Pixels Slice By Slice Sphere](https://clij.github.io/clij2-docs/reference_countNonZeroPixelsSliceBySliceSphere)
Counts non-zero pixels in a sphere around every pixel slice by slice in a stack. 

* [Crop](https://clij.github.io/clij2-docs/reference_crop3D)
Crops a given sub-stack out of a given image stack. 

* [Cylinder Transform](https://clij.github.io/clij2-docs/reference_cylinderTransform)
Applies a cylinder transform to an image stack assuming the center line goes in Y direction in the center of the stack.

* [Detect Label Edges](https://clij.github.io/clij2-docs/reference_detectLabelEdges)
Takes a labelmap and returns an image where all pixels on label edges are set to 1 and all other pixels to 0.

* [Detect Maxima](https://clij.github.io/clij2-docs/reference_detectMaxima3DBox)
Detects local maxima in a given square/cubic neighborhood. 

* [Difference Of Gaussian](https://clij.github.io/clij2-docs/reference_differenceOfGaussian3D)
Applies Gaussian blur to the input image twice with different sigma values resulting in two images which are then subtracted from each other.

* [Dilate](https://clij.github.io/clij2-docs/reference_dilateBox)
Computes a binary image with pixel values 0 and 1 containing the binary dilation of a given input image.

* [Divide By Gaussian Background](https://clij.github.io/clij2-docs/reference_divideByGaussianBackground)
Applies Gaussian blur to the input image and divides the original by the result.

* [Divide Images](https://clij.github.io/clij2-docs/reference_divideImages)
Divides two images X and Y by each other pixel wise. 

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

* [Equal](https://clij.github.io/clij2-docs/reference_equal)
Determines if two images A and B equal pixel wise. 

* [Equalize Mean Intensities Of Slices](https://clij.github.io/clij2-docs/reference_equalizeMeanIntensitiesOfSlices)
Determines correction factors for each z-slice so that the average intensity in all slices can be made the same and multiplies these factors with the slices. 

* [Erode](https://clij.github.io/clij2-docs/reference_erodeBox)
Computes a binary image with pixel values 0 and 1 containing the binary erosion of a given input image. 

* [Exclude Labels On Edges](https://clij.github.io/clij2-docs/reference_excludeLabelsOnEdges)
Removes all labels from a label map which touch the edges of the image (in X, Y and Z if the image is 3D). 

* [Exclude Labels Outside Size Range](https://clij.github.io/clij2-docs/reference_excludeLabelsOutsideSizeRange)
Removes labels from a label map which are not within a certain size range.

* [Exclude Labels With Values Out Of Range](https://clij.github.io/clij2-docs/reference_excludeLabelsWithValuesOutOfRange)
This operation removes labels from a labelmap and renumbers the remaining labels. 

* [Exclude Labels With Values Within Range](https://clij.github.io/clij2-docs/reference_excludeLabelsWithValuesWithinRange)
This operation removes labels from a labelmap and renumbers the remaining labels. 

* [Exponential](https://clij.github.io/clij2-docs/reference_exponential)
Computes base exponential of all pixels values.

* [Extend Labeling Via Voronoi](https://clij.github.io/clij2-docs/reference_extendLabelingViaVoronoi)
Takes a label map image and dilates the regions using a octagon shape until they touch. 

* [Extend Labels With Maximum Radius](https://clij.github.io/clij2-docs/reference_extendLabelsWithMaximumRadius)
Extend labels with a given radius.

* [Flip](https://clij.github.io/clij2-docs/reference_flip3D)
Flips an image in X, Y and/or Z direction depending on boolean flags.

* [Gaussian Blur](https://clij.github.io/clij2-docs/reference_gaussianBlur3D)
Computes the Gaussian blurred image of an image given two sigma values in X, Y and Z. 

* [Greater Constant](https://clij.github.io/clij2-docs/reference_greaterConstant)
Determines if two images A and B greater pixel wise. 

* [Greater Or Equal Constant](https://clij.github.io/clij2-docs/reference_greaterOrEqualConstant)
Determines if two images A and B greater or equal pixel wise. 

* [Greater Or Equal](https://clij.github.io/clij2-docs/reference_greaterOrEqual)
Determines if two images A and B greater or equal pixel wise. 

* [Greater](https://clij.github.io/clij2-docs/reference_greater)
Determines if two images A and B greater pixel wise.

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

* [Label Standard Deviation Intensity Map](https://clij.github.io/clij2-docs/reference_labelStandardDeviationIntensityMap)
Takes an image and a corresponding label map, determines the standard deviation of the intensity per label and replaces every label with the that number.

* [Label To Mask](https://clij.github.io/clij2-docs/reference_labelToMask)
Masks a single label in a label map. 

* [Label Voronoi Octagon](https://clij.github.io/clij2-docs/reference_labelVoronoiOctagon)
Takes a labelled image and dilates the labels using a octagon shape until they touch. 

* [Labeling Workflow A L X](https://clij.github.io/clij2-docs/reference_labelingWorkflowALX)
A segmentation workflow using maxima detection, thresholding, maximum filters and label edge detection.

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

* [Local Threshold](https://clij.github.io/clij2-docs/reference_localThreshold)
Computes a binary image with pixel values 0 and 1 depending on if a pixel value x in image X 
was above of equal to the pixel value m in mask image M.

* [Logarithm](https://clij.github.io/clij2-docs/reference_logarithm)
Computes base e logarithm of all pixels values.

* [Make Isotropic](https://clij.github.io/clij2-docs/reference_makeIsotropic)
Applies a scaling operation using linear interpolation to generate an image stack with a given isotropic voxel size.

* [Mask Label](https://clij.github.io/clij2-docs/reference_maskLabel)
Computes a masked image by applying a label mask to an image. 

* [Mask Stack With Plane](https://clij.github.io/clij2-docs/reference_maskStackWithPlane)
Computes a masked image by applying a binary 2D mask to an image stack. 

* [Mask](https://clij.github.io/clij2-docs/reference_mask)
Computes a masked image by applying a binary mask to an image. 

* [Maximum Image And Scalar](https://clij.github.io/clij2-docs/reference_maximumImageAndScalar)
Computes the maximum of a constant scalar s and each pixel value x in a given image X. 

* [Maximum Images](https://clij.github.io/clij2-docs/reference_maximumImages)
Computes the maximum of a pair of pixel values x, y from two given images X and Y. 

* [Maximum X Projection](https://clij.github.io/clij2-docs/reference_maximumXProjection)
Determines the maximum intensity projection of an image along X.

* [Maximum Y Projection](https://clij.github.io/clij2-docs/reference_maximumYProjection)
Determines the maximum intensity projection of an image along X.

* [Maximum Z Projection Bounded](https://clij.github.io/clij2-docs/reference_maximumZProjectionBounded)
Determines the maximum intensity projection of an image along Z within a given z range.

* [Maximum Z Projection](https://clij.github.io/clij2-docs/reference_maximumZProjection)
Determines the maximum intensity projection of an image along Z.

* [Maximum](https://clij.github.io/clij2-docs/reference_maximum3DBox)
Computes the local maximum of a pixels cube neighborhood. 

* [Mean X Projection](https://clij.github.io/clij2-docs/reference_meanXProjection)
Determines the mean average intensity projection of an image along X.

* [Mean Y Projection](https://clij.github.io/clij2-docs/reference_meanYProjection)
Determines the mean average intensity projection of an image along Y.

* [Mean Z Projection Above Threshold](https://clij.github.io/clij2-docs/reference_meanZProjectionAboveThreshold)
Determines the mean average intensity projection of an image along Z but only for pixels above a given threshold.

* [Mean Z Projection Bounded](https://clij.github.io/clij2-docs/reference_meanZProjectionBounded)
Determines the mean average intensity projection of an image along Z within a given z range.

* [Mean Z Projection](https://clij.github.io/clij2-docs/reference_meanZProjection)
Determines the mean average intensity projection of an image along Z.

* [Mean](https://clij.github.io/clij2-docs/reference_mean3DBox)
Computes the local mean average of a pixels cube neighborhood. 

* [Median Z Projection](https://clij.github.io/clij2-docs/reference_medianZProjection)
Determines the median intensity projection of an image stack along Z.

* [Median](https://clij.github.io/clij2-docs/reference_median3DBox)
Computes the local median of a pixels cuboid neighborhood. 

* [Merge Touching Labels](https://clij.github.io/clij2-docs/reference_mergeTouchingLabels)


* [Minimum Image And Scalar](https://clij.github.io/clij2-docs/reference_minimumImageAndScalar)
Computes the minimum of a constant scalar s and each pixel value x in a given image X.

* [Minimum Images](https://clij.github.io/clij2-docs/reference_minimumImages)
Computes the minimum of a pair of pixel values x, y from two given images X and Y.

* [Minimum X Projection](https://clij.github.io/clij2-docs/reference_minimumXProjection)
Determines the minimum intensity projection of an image along Y.

* [Minimum Y Projection](https://clij.github.io/clij2-docs/reference_minimumYProjection)
Determines the minimum intensity projection of an image along Y.

* [Minimum Z Projection Bounded](https://clij.github.io/clij2-docs/reference_minimumZProjectionBounded)
Determines the minimum intensity projection of an image along Z within a given z range.

* [Minimum Z Projection](https://clij.github.io/clij2-docs/reference_minimumZProjection)
Determines the minimum intensity projection of an image along Z.

* [Minimum](https://clij.github.io/clij2-docs/reference_minimum3DBox)
Computes the local minimum of a pixels cube neighborhood. 

* [Multiply Image And Scalar](https://clij.github.io/clij2-docs/reference_multiplyImageAndScalar)
Multiplies all pixels value x in a given image X with a constant scalar s.

* [Multiply Images](https://clij.github.io/clij2-docs/reference_multiplyImages)
Multiplies all pairs of pixel values x and y from two image X and Y.

* [Multiply Stack With Plane](https://clij.github.io/clij2-docs/reference_multiplyStackWithPlane)
Multiplies all pairs of pixel values x and y from an image stack X and a 2D image Y. 

* [Non Local Means](https://clij.github.io/clij2-docs/reference_nonLocalMeans)
Applies a non-local means filter using a box neighborhood with a Gaussian weight specified with sigma to the input image.

* [Not Equal Constant](https://clij.github.io/clij2-docs/reference_notEqualConstant)
Determines if two images A and B equal pixel wise.

* [Not Equal](https://clij.github.io/clij2-docs/reference_notEqual)
Determines if two images A and B equal pixel wise.

* [Opening](https://clij.github.io/clij2-docs/reference_openingBox)
Apply a binary opening to the input image by calling n erosions and n dilations subsequenntly.

* [Power Images](https://clij.github.io/clij2-docs/reference_powerImages)
Calculates x to the power of y pixel wise of two images X and Y.

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

* [Rotate](https://clij.github.io/clij2-docs/reference_rotate3D)
Rotates an image stack in 3D. 

* [Smaller Constant](https://clij.github.io/clij2-docs/reference_smallerConstant)
Determines if two images A and B smaller pixel wise.

* [Smaller Or Equal](https://clij.github.io/clij2-docs/reference_smallerOrEqual)
Determines if two images A and B smaller or equal pixel wise.

* [Smaller](https://clij.github.io/clij2-docs/reference_smaller)
Determines if two images A and B smaller pixel wise.

* [Sobel](https://clij.github.io/clij2-docs/reference_sobel)
Convolve the image with the Sobel kernel.

* [Sphere Transform](https://clij.github.io/clij2-docs/reference_sphereTransform)
Turns an image stack in XYZ cartesian coordinate system to an AID polar coordinate system.

* [Squared Difference](https://clij.github.io/clij2-docs/reference_squaredDifference)
Determines the squared difference pixel by pixel between two images.

* [Standard Deviation Z Projection](https://clij.github.io/clij2-docs/reference_standardDeviationZProjection)
Determines the standard deviation intensity projection of an image stack along Z.

* [Subtract Gaussian Background](https://clij.github.io/clij2-docs/reference_subtractGaussianBackground)
Applies Gaussian blur to the input image and subtracts the result from the original image.

* [Subtract Image From Scalar](https://clij.github.io/clij2-docs/reference_subtractImageFromScalar)
Subtracts one image X from a scalar s pixel wise.

* [Subtract Images](https://clij.github.io/clij2-docs/reference_subtractImages)
Subtracts one image X from another image Y pixel wise.

* [Sum X Projection](https://clij.github.io/clij2-docs/reference_sumXProjection)
Determines the sum intensity projection of an image along Z.

* [Sum Y Projection](https://clij.github.io/clij2-docs/reference_sumYProjection)
Determines the sum intensity projection of an image along Z.

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

* [Undefined To Zero](https://clij.github.io/clij2-docs/reference_undefinedToZero)
Copies all pixels instead those which are not a number (NaN) or infinity (inf), which are replaced by 0.

* [Voronoi Labeling](https://clij.github.io/clij2-docs/reference_voronoiLabeling)
Takes a binary image, labels connected components and dilates the regions using a octagon shape until they touch. 

* [Voronoi Octagon](https://clij.github.io/clij2-docs/reference_voronoiOctagon)
Takes a binary image and dilates the regions using a octagon shape until they touch. 

* [Weka Label Classifier](https://clij.github.io/clij2-docs/reference_wekaLabelClassifier)
Applies a pre-trained CLIJx-Weka model to an image and a corresponding label map. 

* [Within Intensity Range](https://clij.github.io/clij2-docs/reference_withinIntensityRange)
Generates a binary image where pixels with intensity within the given range are 1 and others are 0.

* [Zoom](https://clij.github.io/clij2-docs/reference_zoom)
See Scale2D and Scale3D.



178 operations listed.


Back to [CLIJx-Assistant](https://clij.github.io/assistant)
