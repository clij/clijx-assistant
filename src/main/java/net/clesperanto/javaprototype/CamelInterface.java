package net.clesperanto.javaprototype;

import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.clijx.weka.CLIJxWeka2;
import net.haesleinhuepf.clij.CLIJ;
import net.haesleinhuepf.clij.clearcl.ClearCLKernel;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.clearcl.ClearCLImage;
import net.haesleinhuepf.clij.clearcl.interfaces.ClearCLImageInterface;
import ij.measure.ResultsTable;
import ij.gui.Roi;
import ij.plugin.frame.RoiManager;
import java.util.HashMap;
import ij.ImagePlus;
import java.util.List;
import java.util.ArrayList;
import net.haesleinhuepf.clij2.plugins.BinaryUnion;
import net.haesleinhuepf.clij2.plugins.BinaryIntersection;
import net.haesleinhuepf.clij2.plugins.ConnectedComponentsLabeling;
import net.haesleinhuepf.clij2.plugins.CountNonZeroPixels;
import net.haesleinhuepf.clijx.plugins.CrossCorrelation;
import net.haesleinhuepf.clij2.plugins.DifferenceOfGaussian2D;
import net.haesleinhuepf.clij2.plugins.DifferenceOfGaussian3D;
import net.haesleinhuepf.clijx.plugins.Extrema;
import net.haesleinhuepf.clijx.plugins.LocalExtremaBox;
import net.haesleinhuepf.clijx.plugins.LocalID;
import net.haesleinhuepf.clij2.plugins.MaskLabel;
import net.haesleinhuepf.clij2.plugins.MeanClosestSpotDistance;
import net.haesleinhuepf.clij2.plugins.MeanSquaredError;
import net.haesleinhuepf.clij2.plugins.MedianZProjection;
import net.haesleinhuepf.clij2.plugins.NonzeroMinimumDiamond;
import net.haesleinhuepf.clij2.plugins.Paste2D;
import net.haesleinhuepf.clij2.plugins.Paste3D;
import net.haesleinhuepf.clijx.plugins.Presign;
import net.haesleinhuepf.clij2.plugins.JaccardIndex;
import net.haesleinhuepf.clij2.plugins.SorensenDiceCoefficient;
import net.haesleinhuepf.clij2.plugins.StandardDeviationZProjection;
import net.haesleinhuepf.clij2.plugins.StackToTiles;
import net.haesleinhuepf.clijx.plugins.SubtractBackground2D;
import net.haesleinhuepf.clijx.plugins.SubtractBackground3D;
import net.haesleinhuepf.clij2.plugins.TopHatBox;
import net.haesleinhuepf.clij2.plugins.TopHatSphere;
import net.haesleinhuepf.clij2.plugins.Exponential;
import net.haesleinhuepf.clij2.plugins.Logarithm;
import net.haesleinhuepf.clij2.plugins.GenerateDistanceMatrix;
import net.haesleinhuepf.clij2.plugins.ShortestDistances;
import net.haesleinhuepf.clij2.plugins.SpotsToPointList;
import net.haesleinhuepf.clij2.plugins.TransposeXY;
import net.haesleinhuepf.clij2.plugins.TransposeXZ;
import net.haesleinhuepf.clij2.plugins.TransposeYZ;
import net.haesleinhuepf.clijx.piv.FastParticleImageVelocimetry;
import net.haesleinhuepf.clijx.piv.ParticleImageVelocimetry;
import net.haesleinhuepf.clijx.piv.ParticleImageVelocimetryTimelapse;
import net.haesleinhuepf.clijx.registration.DeformableRegistration2D;
import net.haesleinhuepf.clijx.registration.TranslationRegistration;
import net.haesleinhuepf.clijx.registration.TranslationTimelapseRegistration;
import net.haesleinhuepf.clij2.plugins.SetWhereXequalsY;
import net.haesleinhuepf.clij2.plugins.LaplaceDiamond;
import net.haesleinhuepf.clij2.plugins.Image2DToResultsTable;
import net.haesleinhuepf.clij2.plugins.WriteValuesToPositions;
import net.haesleinhuepf.clij2.plugins.GetSize;
import net.haesleinhuepf.clij2.plugins.MultiplyMatrix;
import net.haesleinhuepf.clij2.plugins.MatrixEqual;
import net.haesleinhuepf.clij2.plugins.PowerImages;
import net.haesleinhuepf.clij2.plugins.Equal;
import net.haesleinhuepf.clij2.plugins.GreaterOrEqual;
import net.haesleinhuepf.clij2.plugins.Greater;
import net.haesleinhuepf.clij2.plugins.Smaller;
import net.haesleinhuepf.clij2.plugins.SmallerOrEqual;
import net.haesleinhuepf.clij2.plugins.NotEqual;
import net.haesleinhuepf.clijx.io.ReadImageFromDisc;
import net.haesleinhuepf.clijx.io.ReadRawImageFromDisc;
import net.haesleinhuepf.clijx.io.PreloadFromDisc;
import net.haesleinhuepf.clij2.plugins.EqualConstant;
import net.haesleinhuepf.clij2.plugins.GreaterOrEqualConstant;
import net.haesleinhuepf.clij2.plugins.GreaterConstant;
import net.haesleinhuepf.clij2.plugins.SmallerConstant;
import net.haesleinhuepf.clij2.plugins.SmallerOrEqualConstant;
import net.haesleinhuepf.clij2.plugins.NotEqualConstant;
import net.haesleinhuepf.clij2.plugins.DrawBox;
import net.haesleinhuepf.clij2.plugins.DrawLine;
import net.haesleinhuepf.clij2.plugins.DrawSphere;
import net.haesleinhuepf.clij2.plugins.ReplaceIntensity;
import net.haesleinhuepf.clij2.plugins.BoundingBox;
import net.haesleinhuepf.clij2.plugins.MinimumOfMaskedPixels;
import net.haesleinhuepf.clij2.plugins.MaximumOfMaskedPixels;
import net.haesleinhuepf.clij2.plugins.MeanOfMaskedPixels;
import net.haesleinhuepf.clij2.plugins.LabelToMask;
import net.haesleinhuepf.clij2.plugins.NClosestPoints;
import net.haesleinhuepf.clijx.plugins.GaussJordan;
import net.haesleinhuepf.clij2.plugins.StatisticsOfLabelledPixels;
import net.haesleinhuepf.clij2.plugins.VarianceOfAllPixels;
import net.haesleinhuepf.clij2.plugins.StandardDeviationOfAllPixels;
import net.haesleinhuepf.clij2.plugins.VarianceOfMaskedPixels;
import net.haesleinhuepf.clij2.plugins.StandardDeviationOfMaskedPixels;
import net.haesleinhuepf.clij2.plugins.ExcludeLabelsOnEdges;
import net.haesleinhuepf.clij2.plugins.BinarySubtract;
import net.haesleinhuepf.clij2.plugins.BinaryEdgeDetection;
import net.haesleinhuepf.clij2.plugins.DistanceMap;
import net.haesleinhuepf.clij2.plugins.PullAsROI;
import net.haesleinhuepf.clij2.plugins.PullLabelsToROIManager;
import net.haesleinhuepf.clij2.plugins.NonzeroMaximumDiamond;
import net.haesleinhuepf.clij2.plugins.OnlyzeroOverwriteMaximumDiamond;
import net.haesleinhuepf.clij2.plugins.OnlyzeroOverwriteMaximumBox;
import net.haesleinhuepf.clij2.plugins.GenerateTouchMatrix;
import net.haesleinhuepf.clij2.plugins.DetectLabelEdges;
import net.haesleinhuepf.clijx.plugins.StopWatch;
import net.haesleinhuepf.clij2.plugins.CountTouchingNeighbors;
import net.haesleinhuepf.clij2.plugins.ReplaceIntensities;
import net.haesleinhuepf.clijx.plugins.DrawTwoValueLine;
import net.haesleinhuepf.clij2.plugins.AverageDistanceOfNClosestPoints;
import net.haesleinhuepf.clij2.plugins.SaveAsTIF;
import net.haesleinhuepf.clijx.plugins.ConnectedComponentsLabelingInplace;
import net.haesleinhuepf.clij2.plugins.TouchMatrixToMesh;
import net.haesleinhuepf.clijx.plugins.AutomaticThresholdInplace;
import net.haesleinhuepf.clijx.plugins.DifferenceOfGaussianInplace3D;
import net.haesleinhuepf.clijx.plugins.AbsoluteInplace;
import net.haesleinhuepf.clij2.plugins.Resample;
import net.haesleinhuepf.clij2.plugins.EqualizeMeanIntensitiesOfSlices;
import net.haesleinhuepf.clij2.plugins.Watershed;
import net.haesleinhuepf.clij2.plugins.ResliceRadial;
import net.haesleinhuepf.clijx.plugins.ShowRGB;
import net.haesleinhuepf.clijx.plugins.ShowGrey;
import net.haesleinhuepf.clij2.plugins.Sobel;
import net.haesleinhuepf.clij2.plugins.Absolute;
import net.haesleinhuepf.clij2.plugins.LaplaceBox;
import net.haesleinhuepf.clij2.plugins.BottomHatBox;
import net.haesleinhuepf.clij2.plugins.BottomHatSphere;
import net.haesleinhuepf.clij2.plugins.ClosingBox;
import net.haesleinhuepf.clij2.plugins.ClosingDiamond;
import net.haesleinhuepf.clij2.plugins.OpeningBox;
import net.haesleinhuepf.clij2.plugins.OpeningDiamond;
import net.haesleinhuepf.clij2.plugins.MaximumXProjection;
import net.haesleinhuepf.clij2.plugins.MaximumYProjection;
import net.haesleinhuepf.clij2.plugins.MaximumZProjectionBounded;
import net.haesleinhuepf.clij2.plugins.MinimumZProjectionBounded;
import net.haesleinhuepf.clij2.plugins.MeanZProjectionBounded;
import net.haesleinhuepf.clij2.plugins.NonzeroMaximumBox;
import net.haesleinhuepf.clij2.plugins.NonzeroMinimumBox;
import net.haesleinhuepf.clij2.plugins.MinimumZProjectionThresholdedBounded;
import net.haesleinhuepf.clij2.plugins.MeanOfPixelsAboveThreshold;
import net.haesleinhuepf.clijx.gui.OrganiseWindows;
import net.haesleinhuepf.clij2.plugins.DistanceMatrixToMesh;
import net.haesleinhuepf.clij2.plugins.PointIndexListToMesh;
import net.haesleinhuepf.clij2.plugins.MinimumOctagon;
import net.haesleinhuepf.clij2.plugins.MaximumOctagon;
import net.haesleinhuepf.clijx.plugins.TopHatOctagon;
import net.haesleinhuepf.clij2.plugins.AddImages;
import net.haesleinhuepf.clij2.plugins.AddImagesWeighted;
import net.haesleinhuepf.clij2.plugins.SubtractImages;
import net.haesleinhuepf.clijx.plugins.ShowGlasbeyOnGrey;
import net.haesleinhuepf.clij2.plugins.AffineTransform2D;
import net.haesleinhuepf.clij2.plugins.AffineTransform3D;
import net.haesleinhuepf.clij2.plugins.ApplyVectorField2D;
import net.haesleinhuepf.clij2.plugins.ApplyVectorField3D;
import net.haesleinhuepf.clij2.plugins.ArgMaximumZProjection;
import net.haesleinhuepf.clij2.plugins.Histogram;
import net.haesleinhuepf.clij2.plugins.AutomaticThreshold;
import net.haesleinhuepf.clij2.plugins.Threshold;
import net.haesleinhuepf.clij2.plugins.BinaryOr;
import net.haesleinhuepf.clij2.plugins.BinaryAnd;
import net.haesleinhuepf.clij2.plugins.BinaryXOr;
import net.haesleinhuepf.clij2.plugins.BinaryNot;
import net.haesleinhuepf.clij2.plugins.ErodeSphere;
import net.haesleinhuepf.clij2.plugins.ErodeBox;
import net.haesleinhuepf.clij2.plugins.ErodeSphereSliceBySlice;
import net.haesleinhuepf.clij2.plugins.ErodeBoxSliceBySlice;
import net.haesleinhuepf.clij2.plugins.DilateSphere;
import net.haesleinhuepf.clij2.plugins.DilateBox;
import net.haesleinhuepf.clij2.plugins.DilateSphereSliceBySlice;
import net.haesleinhuepf.clij2.plugins.DilateBoxSliceBySlice;
import net.haesleinhuepf.clij2.plugins.Copy;
import net.haesleinhuepf.clij2.plugins.CopySlice;
import net.haesleinhuepf.clij2.plugins.Crop2D;
import net.haesleinhuepf.clij2.plugins.Crop3D;
import net.haesleinhuepf.clij2.plugins.Set;
import net.haesleinhuepf.clij2.plugins.Flip2D;
import net.haesleinhuepf.clij2.plugins.Flip3D;
import net.haesleinhuepf.clij2.plugins.RotateCounterClockwise;
import net.haesleinhuepf.clij2.plugins.RotateClockwise;
import net.haesleinhuepf.clij2.plugins.Mask;
import net.haesleinhuepf.clij2.plugins.MaskStackWithPlane;
import net.haesleinhuepf.clij2.plugins.MaximumZProjection;
import net.haesleinhuepf.clij2.plugins.MeanZProjection;
import net.haesleinhuepf.clij2.plugins.MinimumZProjection;
import net.haesleinhuepf.clij2.plugins.Power;
import net.haesleinhuepf.clij2.plugins.DivideImages;
import net.haesleinhuepf.clij2.plugins.MaximumImages;
import net.haesleinhuepf.clij2.plugins.MaximumImageAndScalar;
import net.haesleinhuepf.clij2.plugins.MinimumImages;
import net.haesleinhuepf.clij2.plugins.MinimumImageAndScalar;
import net.haesleinhuepf.clij2.plugins.MultiplyImageAndScalar;
import net.haesleinhuepf.clij2.plugins.MultiplyStackWithPlane;
import net.haesleinhuepf.clij2.plugins.CountNonZeroPixels2DSphere;
import net.haesleinhuepf.clij2.plugins.CountNonZeroPixelsSliceBySliceSphere;
import net.haesleinhuepf.clij2.plugins.CountNonZeroVoxels3DSphere;
import net.haesleinhuepf.clij2.plugins.SumZProjection;
import net.haesleinhuepf.clij2.plugins.SumOfAllPixels;
import net.haesleinhuepf.clij2.plugins.CenterOfMass;
import net.haesleinhuepf.clij2.plugins.Invert;
import net.haesleinhuepf.clij2.plugins.Downsample2D;
import net.haesleinhuepf.clij2.plugins.Downsample3D;
import net.haesleinhuepf.clij2.plugins.DownsampleSliceBySliceHalfMedian;
import net.haesleinhuepf.clij2.plugins.LocalThreshold;
import net.haesleinhuepf.clij2.plugins.GradientX;
import net.haesleinhuepf.clij2.plugins.GradientY;
import net.haesleinhuepf.clij2.plugins.GradientZ;
import net.haesleinhuepf.clij2.plugins.MultiplyImageAndCoordinate;
import net.haesleinhuepf.clij2.plugins.Mean2DBox;
import net.haesleinhuepf.clij2.plugins.Mean2DSphere;
import net.haesleinhuepf.clij2.plugins.Mean3DBox;
import net.haesleinhuepf.clij2.plugins.Mean3DSphere;
import net.haesleinhuepf.clij2.plugins.MeanSliceBySliceSphere;
import net.haesleinhuepf.clij2.plugins.MeanOfAllPixels;
import net.haesleinhuepf.clij2.plugins.Median2DBox;
import net.haesleinhuepf.clij2.plugins.Median2DSphere;
import net.haesleinhuepf.clij2.plugins.Median3DBox;
import net.haesleinhuepf.clij2.plugins.Median3DSphere;
import net.haesleinhuepf.clij2.plugins.MedianSliceBySliceBox;
import net.haesleinhuepf.clij2.plugins.MedianSliceBySliceSphere;
import net.haesleinhuepf.clij2.plugins.Maximum2DSphere;
import net.haesleinhuepf.clij2.plugins.Maximum3DSphere;
import net.haesleinhuepf.clij2.plugins.Maximum2DBox;
import net.haesleinhuepf.clij2.plugins.Maximum3DBox;
import net.haesleinhuepf.clij2.plugins.MaximumSliceBySliceSphere;
import net.haesleinhuepf.clij2.plugins.Minimum2DSphere;
import net.haesleinhuepf.clij2.plugins.Minimum3DSphere;
import net.haesleinhuepf.clij2.plugins.Minimum2DBox;
import net.haesleinhuepf.clij2.plugins.Minimum3DBox;
import net.haesleinhuepf.clij2.plugins.MinimumSliceBySliceSphere;
import net.haesleinhuepf.clij2.plugins.MultiplyImages;
import net.haesleinhuepf.clij2.plugins.GaussianBlur2D;
import net.haesleinhuepf.clij2.plugins.GaussianBlur3D;
import net.haesleinhuepf.clijx.plugins.BlurSliceBySlice;
import net.haesleinhuepf.clij2.plugins.ResliceBottom;
import net.haesleinhuepf.clij2.plugins.ResliceTop;
import net.haesleinhuepf.clij2.plugins.ResliceLeft;
import net.haesleinhuepf.clij2.plugins.ResliceRight;
import net.haesleinhuepf.clij2.plugins.Rotate2D;
import net.haesleinhuepf.clij2.plugins.Rotate3D;
import net.haesleinhuepf.clij2.plugins.Scale2D;
import net.haesleinhuepf.clij2.plugins.Scale3D;
import net.haesleinhuepf.clij2.plugins.Translate2D;
import net.haesleinhuepf.clij2.plugins.Translate3D;
import net.haesleinhuepf.clij2.plugins.Clear;
import net.haesleinhuepf.clij2.plugins.ClInfo;
import net.haesleinhuepf.clij2.plugins.ConvertFloat;
import net.haesleinhuepf.clij2.plugins.ConvertUInt8;
import net.haesleinhuepf.clij2.plugins.ConvertUInt16;
import net.haesleinhuepf.clij2.plugins.Create2D;
import net.haesleinhuepf.clij2.plugins.Create3D;
import net.haesleinhuepf.clij2.plugins.Pull;
import net.haesleinhuepf.clij2.plugins.PullBinary;
import net.haesleinhuepf.clij2.plugins.Push;
import net.haesleinhuepf.clij2.plugins.PushCurrentSlice;
import net.haesleinhuepf.clij2.plugins.PushCurrentZStack;
import net.haesleinhuepf.clij2.plugins.PushCurrentSelection;
import net.haesleinhuepf.clij2.plugins.PushCurrentSliceSelection;
import net.haesleinhuepf.clij2.plugins.Release;
import net.haesleinhuepf.clij2.plugins.AddImageAndScalar;
import net.haesleinhuepf.clij2.plugins.DetectMinimaBox;
import net.haesleinhuepf.clij2.plugins.DetectMaximaBox;
import net.haesleinhuepf.clij2.plugins.DetectMaximaSliceBySliceBox;
import net.haesleinhuepf.clij2.plugins.DetectMinimaSliceBySliceBox;
import net.haesleinhuepf.clij2.plugins.MaximumOfAllPixels;
import net.haesleinhuepf.clij2.plugins.MinimumOfAllPixels;
import net.haesleinhuepf.clij2.plugins.ReportMemory;
import net.haesleinhuepf.clijx.plugins.splitstack.AbstractSplitStack;
import net.haesleinhuepf.clijx.plugins.TopHatOctagonSliceBySlice;
import net.haesleinhuepf.clij2.plugins.SetColumn;
import net.haesleinhuepf.clij2.plugins.SetRow;
import net.haesleinhuepf.clij2.plugins.SumYProjection;
import net.haesleinhuepf.clij2.plugins.AverageDistanceOfTouchingNeighbors;
import net.haesleinhuepf.clij2.plugins.LabelledSpotsToPointList;
import net.haesleinhuepf.clij2.plugins.LabelSpots;
import net.haesleinhuepf.clij2.plugins.MinimumDistanceOfTouchingNeighbors;
import net.haesleinhuepf.clijx.io.WriteVTKLineListToDisc;
import net.haesleinhuepf.clijx.io.WriteXYZPointListToDisc;
import net.haesleinhuepf.clij2.plugins.SetWhereXgreaterThanY;
import net.haesleinhuepf.clij2.plugins.SetWhereXsmallerThanY;
import net.haesleinhuepf.clij2.plugins.SetNonZeroPixelsToPixelIndex;
import net.haesleinhuepf.clij2.plugins.CloseIndexGapsInLabelMap;
import net.haesleinhuepf.clij2.plugins.AffineTransform;
import net.haesleinhuepf.clij2.plugins.Scale;
import net.haesleinhuepf.clij2.plugins.CentroidsOfLabels;
import net.haesleinhuepf.clij2.plugins.SetRampX;
import net.haesleinhuepf.clij2.plugins.SetRampY;
import net.haesleinhuepf.clij2.plugins.SetRampZ;
import net.haesleinhuepf.clij2.plugins.SubtractImageFromScalar;
import net.haesleinhuepf.clij2.plugins.ThresholdDefault;
import net.haesleinhuepf.clij2.plugins.ThresholdOtsu;
import net.haesleinhuepf.clij2.plugins.ThresholdHuang;
import net.haesleinhuepf.clij2.plugins.ThresholdIntermodes;
import net.haesleinhuepf.clij2.plugins.ThresholdIsoData;
import net.haesleinhuepf.clij2.plugins.ThresholdIJ_IsoData;
import net.haesleinhuepf.clij2.plugins.ThresholdLi;
import net.haesleinhuepf.clij2.plugins.ThresholdMaxEntropy;
import net.haesleinhuepf.clij2.plugins.ThresholdMean;
import net.haesleinhuepf.clij2.plugins.ThresholdMinError;
import net.haesleinhuepf.clij2.plugins.ThresholdMinimum;
import net.haesleinhuepf.clij2.plugins.ThresholdMoments;
import net.haesleinhuepf.clij2.plugins.ThresholdPercentile;
import net.haesleinhuepf.clij2.plugins.ThresholdRenyiEntropy;
import net.haesleinhuepf.clij2.plugins.ThresholdShanbhag;
import net.haesleinhuepf.clij2.plugins.ThresholdTriangle;
import net.haesleinhuepf.clij2.plugins.ThresholdYen;
import net.haesleinhuepf.clij2.plugins.ExcludeLabelsSubSurface;
import net.haesleinhuepf.clij2.plugins.ExcludeLabelsOnSurface;
import net.haesleinhuepf.clij2.plugins.SetPlane;
import net.haesleinhuepf.clijx.plugins.tenengradfusion.TenengradFusion;
import net.haesleinhuepf.clij2.plugins.ImageToStack;
import net.haesleinhuepf.clij2.plugins.SumXProjection;
import net.haesleinhuepf.clij2.plugins.SumImageSliceBySlice;
import net.haesleinhuepf.clij2.plugins.MultiplyImageStackWithScalars;
import net.haesleinhuepf.clij2.plugins.Print;
import net.haesleinhuepf.clij2.plugins.VoronoiOctagon;
import net.haesleinhuepf.clij2.plugins.SetImageBorders;
import net.haesleinhuepf.clijx.plugins.Skeletonize;
import net.haesleinhuepf.clij2.plugins.FloodFillDiamond;
import net.haesleinhuepf.clij2.plugins.BinaryFillHoles;
import net.haesleinhuepf.clij2.plugins.ConnectedComponentsLabelingDiamond;
import net.haesleinhuepf.clij2.plugins.ConnectedComponentsLabelingBox;
import net.haesleinhuepf.clij2.plugins.SetRandom;
import net.haesleinhuepf.clij2.plugins.InvalidateKernelCache;
import net.haesleinhuepf.clij2.plugins.EntropyBox;
import net.haesleinhuepf.clij2.plugins.PushTile;
import net.haesleinhuepf.clij2.plugins.PullTile;
import net.haesleinhuepf.clij2.plugins.ConcatenateStacks;
import net.haesleinhuepf.clij2.plugins.ResultsTableToImage2D;
import net.haesleinhuepf.clij2.plugins.GetAutomaticThreshold;
import net.haesleinhuepf.clij2.plugins.GetDimensions;
import net.haesleinhuepf.clij2.plugins.CustomOperation;
import net.haesleinhuepf.clijx.weka.autocontext.ApplyAutoContextWekaModel;
import net.haesleinhuepf.clijx.weka.autocontext.TrainAutoContextWekaModel;
import net.haesleinhuepf.clijx.weka.ApplyWekaModel;
import net.haesleinhuepf.clijx.weka.ApplyWekaToTable;
import net.haesleinhuepf.clijx.weka.GenerateFeatureStack;
import net.haesleinhuepf.clijx.weka.TrainWekaModel;
import net.haesleinhuepf.clijx.weka.TrainWekaFromTable;
import net.haesleinhuepf.clijx.weka.TrainWekaModelWithOptions;
import net.haesleinhuepf.clijx.plugins.StartContinuousWebcamAcquisition;
import net.haesleinhuepf.clijx.plugins.StopContinuousWebcamAcquisition;
import net.haesleinhuepf.clijx.plugins.CaptureWebcamImage;
import net.haesleinhuepf.clijx.plugins.ConvertRGBStackToGraySlice;
import net.haesleinhuepf.clij2.plugins.PullLabelsToROIList;
import net.haesleinhuepf.clij2.plugins.MeanOfTouchingNeighbors;
import net.haesleinhuepf.clij2.plugins.MinimumOfTouchingNeighbors;
import net.haesleinhuepf.clij2.plugins.MaximumOfTouchingNeighbors;
import net.haesleinhuepf.clij2.plugins.ResultsTableColumnToImage;
import net.haesleinhuepf.clij2.plugins.StatisticsOfBackgroundAndLabelledPixels;
import net.haesleinhuepf.clij2.plugins.GetGPUProperties;
import net.haesleinhuepf.clij2.plugins.GetSumOfAllPixels;
import net.haesleinhuepf.clij2.plugins.GetSorensenDiceCoefficient;
import net.haesleinhuepf.clij2.plugins.GetMinimumOfAllPixels;
import net.haesleinhuepf.clij2.plugins.GetMaximumOfAllPixels;
import net.haesleinhuepf.clij2.plugins.GetMeanOfAllPixels;
import net.haesleinhuepf.clij2.plugins.GetJaccardIndex;
import net.haesleinhuepf.clij2.plugins.GetCenterOfMass;
import net.haesleinhuepf.clij2.plugins.GetBoundingBox;
import net.haesleinhuepf.clij2.plugins.PushArray;
import net.haesleinhuepf.clij2.plugins.PullString;
import net.haesleinhuepf.clij2.plugins.PushString;
import net.haesleinhuepf.clij2.plugins.MedianOfTouchingNeighbors;
import net.haesleinhuepf.clij2.plugins.PushResultsTableColumn;
import net.haesleinhuepf.clij2.plugins.PushResultsTable;
import net.haesleinhuepf.clij2.plugins.PullToResultsTable;
import net.haesleinhuepf.clij2.plugins.LabelVoronoiOctagon;
import net.haesleinhuepf.clij2.plugins.TouchMatrixToAdjacencyMatrix;
import net.haesleinhuepf.clij2.plugins.AdjacencyMatrixToTouchMatrix;
import net.haesleinhuepf.clij2.plugins.PointlistToLabelledSpots;
import net.haesleinhuepf.clij2.plugins.StatisticsOfImage;
import net.haesleinhuepf.clij2.plugins.NClosestDistances;
import net.haesleinhuepf.clij2.plugins.ExcludeLabels;
import net.haesleinhuepf.clij2.plugins.AverageDistanceOfNFarOffPoints;
import net.haesleinhuepf.clij2.plugins.StandardDeviationOfTouchingNeighbors;
import net.haesleinhuepf.clij2.plugins.NeighborsOfNeighbors;
import net.haesleinhuepf.clij2.plugins.GenerateParametricImage;
import net.haesleinhuepf.clij2.plugins.GenerateParametricImageFromResultsTableColumn;
import net.haesleinhuepf.clij2.plugins.ExcludeLabelsWithValuesOutOfRange;
import net.haesleinhuepf.clij2.plugins.ExcludeLabelsWithValuesWithinRange;
import net.haesleinhuepf.clij2.plugins.CombineVertically;
import net.haesleinhuepf.clij2.plugins.CombineHorizontally;
import net.haesleinhuepf.clij2.plugins.ReduceStack;
import net.haesleinhuepf.clij2.plugins.DetectMinima2DBox;
import net.haesleinhuepf.clij2.plugins.DetectMaxima2DBox;
import net.haesleinhuepf.clij2.plugins.DetectMinima3DBox;
import net.haesleinhuepf.clij2.plugins.DetectMaxima3DBox;
import net.haesleinhuepf.clij2.plugins.DepthColorProjection;
import net.haesleinhuepf.clij2.plugins.GenerateBinaryOverlapMatrix;
import net.haesleinhuepf.clij2.plugins.ResliceRadialTop;
import net.haesleinhuepf.clij2.plugins.Convolve;
import net.haesleinhuepf.clijx.plugins.NonLocalMeans;
import net.haesleinhuepf.clijx.plugins.Bilateral;
import net.haesleinhuepf.clij2.plugins.UndefinedToZero;
import net.haesleinhuepf.clij2.plugins.GenerateJaccardIndexMatrix;
import net.haesleinhuepf.clij2.plugins.GenerateTouchCountMatrix;
import net.haesleinhuepf.clij2.plugins.MinimumXProjection;
import net.haesleinhuepf.clij2.plugins.MinimumYProjection;
import net.haesleinhuepf.clij2.plugins.MeanXProjection;
import net.haesleinhuepf.clij2.plugins.MeanYProjection;
import net.haesleinhuepf.clij2.plugins.SquaredDifference;
import net.haesleinhuepf.clij2.plugins.AbsoluteDifference;
import net.haesleinhuepf.clij2.plugins.ReplacePixelsIfZero;
import net.haesleinhuepf.clij2.plugins.VoronoiLabeling;
import net.haesleinhuepf.clij2.plugins.ExtendLabelingViaVoronoi;
import net.haesleinhuepf.clijx.plugins.FindMaxima;
import net.haesleinhuepf.clij2.plugins.MergeTouchingLabels;
import net.haesleinhuepf.clij2.plugins.AverageNeighborDistanceMap;
import net.haesleinhuepf.clij2.plugins.CylinderTransform;
import net.haesleinhuepf.clijx.plugins.DetectAndLabelMaxima;
import net.haesleinhuepf.clij2.plugins.DrawDistanceMeshBetweenTouchingLabels;
import net.haesleinhuepf.clij2.plugins.DrawMeshBetweenTouchingLabels;
import net.haesleinhuepf.clij2.plugins.ExcludeLabelsOutsideSizeRange;
import net.haesleinhuepf.clij2.plugins.DilateLabels;
import net.haesleinhuepf.clijx.plugins.FindAndLabelMaxima;
import net.haesleinhuepf.clij2.plugins.MakeIsotropic;
import net.haesleinhuepf.clij2.plugins.TouchingNeighborCountMap;
import net.haesleinhuepf.clij2.plugins.RigidTransform;
import net.haesleinhuepf.clij2.plugins.SphereTransform;
import net.haesleinhuepf.clijx.plugins.SubtractGaussianBackground;
import net.haesleinhuepf.clijx.plugins.ThresholdDoG;
import net.haesleinhuepf.clijx.plugins.DriftCorrectionByCenterOfMassFixation;
import net.haesleinhuepf.clijx.plugins.DriftCorrectionByCentroidFixation;
import net.haesleinhuepf.clijx.plugins.IntensityCorrection;
import net.haesleinhuepf.clijx.plugins.IntensityCorrectionAboveThresholdOtsu;
import net.haesleinhuepf.clij2.plugins.MeanIntensityMap;
import net.haesleinhuepf.clij2.plugins.StandardDeviationIntensityMap;
import net.haesleinhuepf.clij2.plugins.PixelCountMap;
import net.haesleinhuepf.clijx.plugins.ParametricWatershed;
import net.haesleinhuepf.clijx.plugins.MeanZProjectionAboveThreshold;
import net.haesleinhuepf.clij2.plugins.CentroidsOfBackgroundAndLabels;
import net.haesleinhuepf.clijx.plugins.SeededWatershed;
import net.haesleinhuepf.clijx.plugins.PushMetaData;
import net.haesleinhuepf.clijx.plugins.PopMetaData;
import net.haesleinhuepf.clijx.plugins.ResetMetaData;
import net.haesleinhuepf.clij2.plugins.AverageDistanceOfNClosestNeighborsMap;
import net.haesleinhuepf.clijx.plugins.DrawTouchCountMeshBetweenTouchingLabels;
import net.haesleinhuepf.clijx.plugins.LocalMaximumAverageDistanceOfNClosestNeighborsMap;
import net.haesleinhuepf.clijx.plugins.LocalMaximumAverageNeighborDistanceMap;
import net.haesleinhuepf.clijx.plugins.LocalMaximumTouchingNeighborCountMap;
import net.haesleinhuepf.clijx.plugins.LocalMeanAverageDistanceOfNClosestNeighborsMap;
import net.haesleinhuepf.clijx.plugins.LocalMeanAverageNeighborDistanceMap;
import net.haesleinhuepf.clijx.plugins.LocalMeanTouchingNeighborCountMap;
import net.haesleinhuepf.clijx.plugins.LocalMeanTouchPortionMap;
import net.haesleinhuepf.clijx.plugins.LocalMedianAverageDistanceOfNClosestNeighborsMap;
import net.haesleinhuepf.clijx.plugins.LocalMedianAverageNeighborDistanceMap;
import net.haesleinhuepf.clijx.plugins.LocalMedianTouchingNeighborCountMap;
import net.haesleinhuepf.clijx.plugins.LocalMinimumAverageDistanceOfNClosestNeighborsMap;
import net.haesleinhuepf.clijx.plugins.LocalMinimumAverageNeighborDistanceMap;
import net.haesleinhuepf.clijx.plugins.LocalMinimumTouchingNeighborCountMap;
import net.haesleinhuepf.clijx.plugins.LocalStandardDeviationAverageDistanceOfNClosestNeighborsMap;
import net.haesleinhuepf.clijx.plugins.LocalStandardDeviationAverageNeighborDistanceMap;
import net.haesleinhuepf.clijx.plugins.LocalStandardDeviationTouchingNeighborCountMap;
import net.haesleinhuepf.clij2.plugins.MinimumIntensityMap;
import net.haesleinhuepf.clij2.plugins.MaximumIntensityMap;
import net.haesleinhuepf.clij2.plugins.ExtensionRatioMap;
import net.haesleinhuepf.clij2.plugins.MaximumExtensionMap;
import net.haesleinhuepf.clijx.plugins.GenerateIntegerGreyValueCooccurrenceCountMatrixHalfBox;
import net.haesleinhuepf.clijx.plugins.GenerateIntegerGreyValueCooccurrenceCountMatrixHalfDiamond;
import net.haesleinhuepf.clij2.plugins.GetMeanOfMaskedPixels;
import net.haesleinhuepf.clij2.plugins.DivideByGaussianBackground;
import net.haesleinhuepf.clijx.plugins.GenerateGreyValueCooccurrenceMatrixBox;
import net.haesleinhuepf.clijx.plugins.GreyLevelAtttributeFiltering;
import net.haesleinhuepf.clijx.plugins.BinaryFillHolesSliceBySlice;
import net.haesleinhuepf.clijx.weka.BinaryWekaPixelClassifier;
import net.haesleinhuepf.clijx.weka.WekaLabelClassifier;
import net.haesleinhuepf.clijx.weka.GenerateLabelFeatureImage;
import net.haesleinhuepf.clij2.plugins.LabelSurface;
import net.haesleinhuepf.clij2.plugins.ReduceLabelsToCentroids;
import net.haesleinhuepf.clij2.plugins.MeanExtensionMap;
import net.haesleinhuepf.clijx.plugins.MeanZProjectionBelowThreshold;
import net.haesleinhuepf.clij2.plugins.EuclideanDistanceFromLabelCentroidMap;
import net.haesleinhuepf.clij2.plugins.GammaCorrection;
import net.haesleinhuepf.clij2.plugins.ZPositionOfMaximumZProjection;
import net.haesleinhuepf.clij2.plugins.ZPositionProjection;
import net.haesleinhuepf.clij2.plugins.ZPositionRangeProjection;
import net.haesleinhuepf.clij2.plugins.VarianceSphere;
import net.haesleinhuepf.clij2.plugins.StandardDeviationSphere;
import net.haesleinhuepf.clij2.plugins.VarianceBox;
import net.haesleinhuepf.clij2.plugins.StandardDeviationBox;
import net.haesleinhuepf.clij2.plugins.Tenengrad;
import net.haesleinhuepf.clij2.plugins.TenengradSliceBySlice;
import net.haesleinhuepf.clij2.plugins.SobelSliceBySlice;
import net.haesleinhuepf.clij2.plugins.ExtendedDepthOfFocusSobelProjection;
import net.haesleinhuepf.clij2.plugins.ExtendedDepthOfFocusTenengradProjection;
import net.haesleinhuepf.clij2.plugins.ExtendedDepthOfFocusVarianceProjection;
import net.haesleinhuepf.clij2.plugins.DrawMeshBetweenNClosestLabels;
import net.haesleinhuepf.clij2.plugins.DrawMeshBetweenProximalLabels;
import net.haesleinhuepf.clij2.plugins.Cosinus;
import net.haesleinhuepf.clij2.plugins.Sinus;
import net.haesleinhuepf.clijx.plugins.GenerateDistanceMatrixAlongAxis;
import net.haesleinhuepf.clij2.plugins.MaximumDistanceOfTouchingNeighbors;
import net.haesleinhuepf.clij2.plugins.MaximumTouchingNeighborDistanceMap;
import net.haesleinhuepf.clij2.plugins.MinimumTouchingNeighborDistanceMap;
import net.haesleinhuepf.clijx.plugins.GenerateAngleMatrix;
import net.haesleinhuepf.clij2.plugins.TouchingNeighborDistanceRangeRatioMap;
import net.haesleinhuepf.clij2.plugins.VoronoiOtsuLabeling;
import net.haesleinhuepf.clijx.plugins.VisualizeOutlinesOnOriginal;
import net.haesleinhuepf.clijx.plugins.FlagLabelsOnEdges;
import net.haesleinhuepf.clij2.plugins.MaskedVoronoiLabeling;
import net.haesleinhuepf.clij2.plugins.PullToResultsTableColumn;
import net.haesleinhuepf.clijx.plugins.KMeansLabelClusterer;
import net.haesleinhuepf.clij2.plugins.ModeOfTouchingNeighbors;
import net.haesleinhuepf.clij2.plugins.GenerateProximalNeighborsMatrix;
import net.haesleinhuepf.clijx.plugins.ReadIntensitiesFromMap;
import net.haesleinhuepf.clij2.plugins.MaximumOfTouchingNeighborsMap;
import net.haesleinhuepf.clij2.plugins.MinimumOfTouchingNeighborsMap;
import net.haesleinhuepf.clij2.plugins.MeanOfTouchingNeighborsMap;
import net.haesleinhuepf.clij2.plugins.ModeOfTouchingNeighborsMap;
import net.haesleinhuepf.clij2.plugins.StandardDeviationOfTouchingNeighborsMap;
import net.haesleinhuepf.clij2.plugins.PointIndexListToTouchMatrix;
import net.haesleinhuepf.clij2.plugins.GenerateNNearestNeighborsMatrix;
import net.haesleinhuepf.clij2.plugins.MaximumOfNNearestNeighborsMap;
import net.haesleinhuepf.clij2.plugins.MinimumOfNNearestNeighborsMap;
import net.haesleinhuepf.clij2.plugins.MeanOfNNearestNeighborsMap;
import net.haesleinhuepf.clij2.plugins.ModeOfNNearestNeighborsMap;
import net.haesleinhuepf.clij2.plugins.StandardDeviationOfNNearestNeighborsMap;
import net.haesleinhuepf.clij2.plugins.MaximumOfProximalNeighborsMap;
import net.haesleinhuepf.clij2.plugins.MinimumOfProximalNeighborsMap;
import net.haesleinhuepf.clij2.plugins.MeanOfProximalNeighborsMap;
import net.haesleinhuepf.clij2.plugins.ModeOfProximalNeighborsMap;
import net.haesleinhuepf.clij2.plugins.StandardDeviationOfProximalNeighborsMap;
import net.haesleinhuepf.clij2.plugins.LabelOverlapCountMap;
import net.haesleinhuepf.clij2.plugins.LabelProximalNeighborCountMap;
import net.haesleinhuepf.clij2.plugins.ReduceLabelsToLabelEdges;
import net.haesleinhuepf.clij2.plugins.OutOfIntensityRange;
import net.haesleinhuepf.clij2.plugins.ErodeLabels;
import net.haesleinhuepf.clij2.plugins.Similar;
import net.haesleinhuepf.clij2.plugins.Different;
import net.haesleinhuepf.clijx.weka.WekaRegionalLabelClassifier;
import net.haesleinhuepf.clijx.plugins.LabelMeanOfLaplacianMap;
import net.haesleinhuepf.clij2.plugins.MedianZProjectionMasked;
import net.haesleinhuepf.clijx.plugins.MedianTouchPortionMap;
import net.haesleinhuepf.clijx.plugins.NeighborCountWithTouchPortionAboveThresholdMap;
import net.haesleinhuepf.clij2.plugins.DivideScalarByImage;
import net.haesleinhuepf.clij2.plugins.ReadValuesFromMap;
import net.haesleinhuepf.clij2.plugins.ReadValuesFromPositions;
import net.haesleinhuepf.clij2.plugins.ZPositionOfMinimumZProjection;
import net.haesleinhuepf.clijx.plugins.LocalThresholdPhansalkar;
import net.haesleinhuepf.clijx.plugins.LocalThresholdBernsen;
import net.haesleinhuepf.clijx.plugins.LocalThresholdContrast;
import net.haesleinhuepf.clijx.plugins.LocalThresholdMean;
import net.haesleinhuepf.clijx.plugins.LocalThresholdMedian;
import net.haesleinhuepf.clijx.plugins.LocalThresholdMidGrey;
import net.haesleinhuepf.clijx.plugins.LocalThresholdNiblack;
import net.haesleinhuepf.clijx.plugins.LocalThresholdSauvola;
import net.haesleinhuepf.clijx.plugins.ColorDeconvolution;
import net.haesleinhuepf.clij2.plugins.GreyscaleOpeningBox;
import net.haesleinhuepf.clij2.plugins.GreyscaleOpeningSphere;
import net.haesleinhuepf.clij2.plugins.GreyscaleClosingBox;
import net.haesleinhuepf.clij2.plugins.GreyscaleClosingSphere;
import net.haesleinhuepf.clij2.plugins.ProximalNeighborCountMap;
import net.haesleinhuepf.clij2.plugins.SubStack;
import net.haesleinhuepf.clij2.plugins.DrawMeshBetweenNNearestLabels;
// this is generated code. See src/test/java/net/haesleinhuepf/clijx/codegenerator for details
abstract class CamelInterface extends CommonAPI{
   static CLIJ getCLIJ() {
       return CLIJ.getInstance();
   }
   static CLIJ2 getCLIJ2() {
       return CLIJ2.getInstance();
   }
   static CLIJx getCLIJx() {
       return CLIJx.getInstance();
   }
   

    // net.haesleinhuepf.clij2.plugins.BinaryUnion
    //----------------------------------------------------
    /**
     * Computes a binary image (containing pixel values 0 and 1) from two images X and Y by connecting pairs of
     * pixels x and y with the binary union operator |.
     * 
     * All pixel values except 0 in the input images are interpreted as 1.<pre>f(x, y) = x | y</pre>
     * 
     * Parameters
     * ----------
     * operand1 : Image
     *     The first binary input image to be processed.
     * operand2 : Image
     *     The second binary input image to be processed.
     * destination : Image
     *     The output image where results are written into.
     * 
     */
    public static ClearCLBuffer binaryUnion(ClearCLBuffer operand1, ClearCLBuffer operand2, ClearCLBuffer destination) {
        BinaryUnion.binaryUnion(getCLIJ2(), operand1, operand2, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.BinaryIntersection
    //----------------------------------------------------
    /**
     * Computes a binary image (containing pixel values 0 and 1) from two images X and Y by connecting pairs of
     * pixels x and y with the binary intersection operator &.
     * All pixel values except 0 in the input images are interpreted as 1.
     * 
     * <pre>f(x, y) = x & y</pre>
     * 
     * Parameters
     * ----------
     * operand1 : Image
     *     The first binary input image to be processed.
     * operand2 : Image
     *     The second binary input image to be processed.
     * destination : Image
     *     The output image where results are written into.
     * 
     */
    public static ClearCLBuffer binaryIntersection(ClearCLBuffer operand1, ClearCLBuffer operand2, ClearCLBuffer destination) {
        BinaryIntersection.binaryIntersection(getCLIJ2(), operand1, operand2, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.ConnectedComponentsLabeling
    //----------------------------------------------------

    // net.haesleinhuepf.clij2.plugins.CountNonZeroPixels
    //----------------------------------------------------
    /**
     * Determines the number of all pixels in a given image which are not equal to 0. 
     * 
     * It will be stored in a new row of ImageJs
     * Results table in the column 'CountNonZero'.
     */
    public static ClearCLBuffer countNonZeroPixels(ClearCLBuffer source) {
        CountNonZeroPixels.countNonZeroPixels(getCLIJ2(), source);
        return source;
    }


    // net.haesleinhuepf.clijx.plugins.CrossCorrelation
    //----------------------------------------------------
    /**
     * Performs cross correlation analysis between two images. 
     * 
     * The second image is shifted by deltaPos in the given dimension. The cross correlation coefficient is calculated for each pixel in a range around the given pixel with given radius in the given dimension. Together with the original images it is recommended to hand over mean filtered images using the same radius.  
     */
    public static ClearCLBuffer crossCorrelation(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, ClearCLBuffer arg4, ClearCLBuffer arg5, double arg6, double arg7, double arg8) {
        CrossCorrelation.crossCorrelation(getCLIJ2(), arg1, arg2, arg3, arg4, arg5, new Double (arg6).intValue(), new Double (arg7).intValue(), new Double (arg8).intValue());
        return arg5;
    }

    /**
     * Performs cross correlation analysis between two images. 
     * 
     * The second image is shifted by deltaPos in the given dimension. The cross correlation coefficient is calculated for each pixel in a range around the given pixel with given radius in the given dimension. Together with the original images it is recommended to hand over mean filtered images using the same radius.  
     */
    public static ClearCLImage crossCorrelation(ClearCLImage arg1, ClearCLImage arg2, ClearCLImage arg3, ClearCLImage arg4, ClearCLImage arg5, double arg6, double arg7, double arg8) {
        CrossCorrelation.crossCorrelation(getCLIJ2(), arg1, arg2, arg3, arg4, arg5, new Double (arg6).intValue(), new Double (arg7).intValue(), new Double (arg8).intValue());
        return arg5;
    }


    // net.haesleinhuepf.clij2.plugins.DifferenceOfGaussian2D
    //----------------------------------------------------
    /**
     * Applies Gaussian blur to the input image twice with different sigma values resulting in two images which are then subtracted from each other.
     * 
     * It is recommended to apply this operation to images of type Float (32 bit) as results might be negative.
     * 
     * Parameters
     * ----------
     * input : Image
     *     The input image to be processed.
     * destination : Image
     *     The output image where results are written into.
     * sigma1_x : float
     *     Sigma of the first Gaussian filter in x
     * sigma1_y : float
     *     Sigma of the first Gaussian filter in y
     * sigma2_x : float
     *     Sigma of the second Gaussian filter in x
     * sigma2_y : float
     *     Sigma of the second Gaussian filter in y
     */
    public static ClearCLBuffer differenceOfGaussian(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, double arg6) {
        DifferenceOfGaussian2D.differenceOfGaussian(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), new Double (arg6).floatValue());
        return arg2;
    }

    /**
     * Applies Gaussian blur to the input image twice with different sigma values resulting in two images which are then subtracted from each other.
     * 
     * It is recommended to apply this operation to images of type Float (32 bit) as results might be negative.
     * 
     * Parameters
     * ----------
     * input : Image
     *     The input image to be processed.
     * destination : Image
     *     The output image where results are written into.
     * sigma1_x : float
     *     Sigma of the first Gaussian filter in x
     * sigma1_y : float
     *     Sigma of the first Gaussian filter in y
     * sigma2_x : float
     *     Sigma of the second Gaussian filter in x
     * sigma2_y : float
     *     Sigma of the second Gaussian filter in y
     */
    public static ClearCLBuffer differenceOfGaussian2D(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, double arg6) {
        DifferenceOfGaussian2D.differenceOfGaussian2D(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), new Double (arg6).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.DifferenceOfGaussian3D
    //----------------------------------------------------
    /**
     * Applies Gaussian blur to the input image twice with different sigma values resulting in two images which are then subtracted from each other.
     * 
     * It is recommended to apply this operation to images of type Float (32 bit) as results might be negative.
     * 
     * Parameters
     * ----------
     * input : Image
     *     The input image to be processed.
     * destination : Image
     *     The output image where results are written into.
     * sigma1_x : float
     *     Sigma of the first Gaussian filter in x
     * sigma1_y : float
     *     Sigma of the first Gaussian filter in y
     * sigma2_x : float
     *     Sigma of the second Gaussian filter in x
     * sigma2_y : float
     *     Sigma of the second Gaussian filter in y
     */
    public static ClearCLBuffer differenceOfGaussian(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8) {
        DifferenceOfGaussian3D.differenceOfGaussian(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), new Double (arg6).floatValue(), new Double (arg7).floatValue(), new Double (arg8).floatValue());
        return arg2;
    }

    /**
     * Applies Gaussian blur to the input image twice with different sigma values resulting in two images which are then subtracted from each other.
     * 
     * It is recommended to apply this operation to images of type Float (32 bit) as results might be negative.
     * 
     * Parameters
     * ----------
     * input : Image
     *     The input image to be processed.
     * destination : Image
     *     The output image where results are written into.
     * sigma1_x : float
     *     Sigma of the first Gaussian filter in x
     * sigma1_y : float
     *     Sigma of the first Gaussian filter in y
     * sigma1_z : float
     *     Sigma of the first Gaussian filter in z
     * sigma2_x : float
     *     Sigma of the second Gaussian filter in x
     * sigma2_y : float
     *     Sigma of the second Gaussian filter in y
     * sigma2_z : float
     *     Sigma of the second Gaussian filter in z
     */
    public static ClearCLBuffer differenceOfGaussian3D(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8) {
        DifferenceOfGaussian3D.differenceOfGaussian3D(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), new Double (arg6).floatValue(), new Double (arg7).floatValue(), new Double (arg8).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clijx.plugins.Extrema
    //----------------------------------------------------
    /**
     * Returns an image with pixel values most distant from 0: 
     * 
     * f(x, y) = x if abs(x) > abs(y), y else.
     */
    public static ClearCLBuffer extrema(ClearCLBuffer input1, ClearCLBuffer input2, ClearCLBuffer destination) {
        Extrema.extrema(getCLIJ(), input1, input2, destination);
        return destination;
    }


    // net.haesleinhuepf.clijx.plugins.LocalExtremaBox
    //----------------------------------------------------
    /**
     * Applies a local minimum and maximum filter. 
     * 
     * Afterwards, the value is returned which is more far from zero.
     */
    public static ClearCLBuffer localExtremaBox(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5) {
        LocalExtremaBox.localExtremaBox(getCLIJ(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clijx.plugins.LocalID
    //----------------------------------------------------
    /**
     * local id
     */
    public static ClearCLBuffer localID(ClearCLBuffer input, ClearCLBuffer destination) {
        LocalID.localID(getCLIJ(), input, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.MaskLabel
    //----------------------------------------------------
    /**
     * Computes a masked image by applying a label mask to an image. 
     * 
     * All pixel values x of image X will be copied
     * to the destination image in case pixel value m at the same position in the label_map image has the right index value i.
     * 
     * f(x,m,i) = (x if (m == i); (0 otherwise))
     */
    public static ClearCLBuffer maskLabel(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4) {
        MaskLabel.maskLabel(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).floatValue());
        return arg3;
    }


    // net.haesleinhuepf.clij2.plugins.MeanClosestSpotDistance
    //----------------------------------------------------
    /**
     * Determines the distance between pairs of closest spots in two binary images. 
     * 
     * Takes two binary images A and B with marked spots and determines for each spot in image A the closest spot in image B. Afterwards, it saves the average shortest distances from image A to image B as 'mean_closest_spot_distance_A_B' and from image B to image A as 'mean_closest_spot_distance_B_A' to the results table. The distance between B and A is only determined if the `bidirectional` checkbox is checked.
     */
    public static ClearCLBuffer meanClosestSpotDistance(ClearCLBuffer arg1, ClearCLBuffer arg2) {
        MeanClosestSpotDistance.meanClosestSpotDistance(getCLIJ2(), arg1, arg2);
        return arg2;
    }

    /**
     * Determines the distance between pairs of closest spots in two binary images. 
     * 
     * Takes two binary images A and B with marked spots and determines for each spot in image A the closest spot in image B. Afterwards, it saves the average shortest distances from image A to image B as 'mean_closest_spot_distance_A_B' and from image B to image A as 'mean_closest_spot_distance_B_A' to the results table. The distance between B and A is only determined if the `bidirectional` checkbox is checked.
     */
    public static ClearCLBuffer meanClosestSpotDistance(ClearCLBuffer arg1, ClearCLBuffer arg2, boolean arg3) {
        MeanClosestSpotDistance.meanClosestSpotDistance(getCLIJ2(), arg1, arg2, arg3);
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.MeanSquaredError
    //----------------------------------------------------
    /**
     * Determines the mean squared error (MSE) between two images. 
     * 
     * The MSE will be stored in a new row of ImageJs
     * Results table in the column 'MSE'.
     */
    public static ClearCLBuffer meanSquaredError(ClearCLBuffer source1, ClearCLBuffer source2) {
        MeanSquaredError.meanSquaredError(getCLIJ2(), source1, source2);
        return source2;
    }


    // net.haesleinhuepf.clij2.plugins.MedianZProjection
    //----------------------------------------------------
    /**
     * Determines the median intensity projection of an image stack along Z.
     */
    public static ClearCLImageInterface medianZProjection(ClearCLImageInterface source, ClearCLImageInterface destination) {
        MedianZProjection.medianZProjection(getCLIJ2(), source, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.NonzeroMinimumDiamond
    //----------------------------------------------------
    /**
     * Apply a minimum filter (diamond shape) to the input image. 
     * 
     * The radius is fixed to 1 and pixels with value 0 are ignored.Note: Pixels with 0 value in the input image will not be overwritten in the output image.
     * Thus, the result image should be initialized by copying the original image in advance.
     */
    public static ClearCLImageInterface nonzeroMinimumDiamond(ClearCLImageInterface input, ClearCLImageInterface destination) {
        NonzeroMinimumDiamond.nonzeroMinimumDiamond(getCLIJ2(), input, destination);
        return destination;
    }

    /**
     * Apply a minimum filter (diamond shape) to the input image. 
     * 
     * The radius is fixed to 1 and pixels with value 0 are ignored.Note: Pixels with 0 value in the input image will not be overwritten in the output image.
     * Thus, the result image should be initialized by copying the original image in advance.
     */
    public static ClearCLImageInterface nonzeroMinimumDiamond(ClearCLImageInterface arg1, ClearCLImageInterface arg2, ClearCLImageInterface arg3) {
        NonzeroMinimumDiamond.nonzeroMinimumDiamond(getCLIJ2(), arg1, arg2, arg3);
        return arg3;
    }

    /**
     * Apply a minimum filter (diamond shape) to the input image. 
     * 
     * The radius is fixed to 1 and pixels with value 0 are ignored.Note: Pixels with 0 value in the input image will not be overwritten in the output image.
     * Thus, the result image should be initialized by copying the original image in advance.
     */
    public static ClearCLKernel nonzeroMinimumDiamond(ClearCLImageInterface arg1, ClearCLImageInterface arg2, ClearCLImageInterface arg3, ClearCLKernel arg4) {
        NonzeroMinimumDiamond.nonzeroMinimumDiamond(getCLIJ2(), arg1, arg2, arg3, arg4);
        return arg4;
    }


    // net.haesleinhuepf.clij2.plugins.Paste2D
    //----------------------------------------------------
    /**
     * Pastes an image into another image at a given position.
     */
    public static ClearCLImageInterface paste(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        Paste2D.paste(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return arg2;
    }

    /**
     * Pastes an image into another image at a given position.
     */
    public static ClearCLImageInterface paste2D(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        Paste2D.paste2D(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.Paste3D
    //----------------------------------------------------
    /**
     * Pastes an image into another image at a given position.
     */
    public static ClearCLImageInterface paste(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        Paste3D.paste(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return arg2;
    }

    /**
     * Pastes an image into another image at a given position.
     */
    public static ClearCLImageInterface paste3D(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        Paste3D.paste3D(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clijx.plugins.Presign
    //----------------------------------------------------
    /**
     * Determines the extrema of pixel values: 
     * 
     * f(x) = x / abs(x).
     */
    public static ClearCLBuffer presign(ClearCLBuffer input, ClearCLBuffer destination) {
        Presign.presign(getCLIJ(), input, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.JaccardIndex
    //----------------------------------------------------
    /**
     * Determines the overlap of two binary images using the Jaccard index. 
     * 
     * A value of 0 suggests no overlap, 1 means perfect overlap.
     * The resulting Jaccard index is saved to the results table in the 'Jaccard_Index' column.
     * Note that the Sorensen-Dice coefficient can be calculated from the Jaccard index j using this formula:
     * <pre>s = f(j) = 2 j / (j + 1)</pre>
     */
    public static ClearCLBuffer jaccardIndex(ClearCLBuffer source1, ClearCLBuffer source2) {
        JaccardIndex.jaccardIndex(getCLIJ2(), source1, source2);
        return source2;
    }


    // net.haesleinhuepf.clij2.plugins.SorensenDiceCoefficient
    //----------------------------------------------------
    /**
     * Determines the overlap of two binary images using the Sorensen-Dice coefficent. 
     * 
     * A value of 0 suggests no overlap, 1 means perfect overlap.
     * The Sorensen-Dice coefficient is saved in the colum 'Sorensen_Dice_coefficient'.
     * Note that the Sorensen-Dice coefficient s can be calculated from the Jaccard index j using this formula:
     * <pre>s = f(j) = 2 j / (j + 1)</pre>
     */
    public static ClearCLBuffer sorensenDiceCoefficient(ClearCLBuffer source1, ClearCLBuffer source2) {
        SorensenDiceCoefficient.sorensenDiceCoefficient(getCLIJ2(), source1, source2);
        return source2;
    }


    // net.haesleinhuepf.clij2.plugins.StandardDeviationZProjection
    //----------------------------------------------------
    /**
     * Determines the standard deviation intensity projection of an image stack along Z.
     */
    public static ClearCLImageInterface standardDeviationZProjection(ClearCLImageInterface source, ClearCLImageInterface destination) {
        StandardDeviationZProjection.standardDeviationZProjection(getCLIJ2(), source, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.StackToTiles
    //----------------------------------------------------
    /**
     * Stack to tiles.
     */
    public static ClearCLImageInterface stackToTiles(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        StackToTiles.stackToTiles(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clijx.plugins.SubtractBackground2D
    //----------------------------------------------------
    /**
     * Applies Gaussian blur to the input image and subtracts the result from the original image.
     * 
     * Deprecated: Use topHat() or differenceOfGaussian() instead.
     */
    public static ClearCLImageInterface subtractBackground(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        SubtractBackground2D.subtractBackground(getCLIJx(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue());
        return arg2;
    }

    /**
     * Applies Gaussian blur to the input image and subtracts the result from the original image.
     * 
     * Deprecated: Use topHat() or differenceOfGaussian() instead.
     */
    public static ClearCLImageInterface subtractBackground2D(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        SubtractBackground2D.subtractBackground2D(getCLIJx(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clijx.plugins.SubtractBackground3D
    //----------------------------------------------------
    /**
     * Applies Gaussian blur to the input image and subtracts the result from the original image.
     * 
     * Deprecated: Use topHat() or differenceOfGaussian() instead.
     */
    public static ClearCLImageInterface subtractBackground(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        SubtractBackground3D.subtractBackground(getCLIJx(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue());
        return arg2;
    }

    /**
     * Applies Gaussian blur to the input image and subtracts the result from the original image.
     * 
     * Deprecated: Use topHat() or differenceOfGaussian() instead.
     */
    public static ClearCLImageInterface subtractBackground3D(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        SubtractBackground3D.subtractBackground3D(getCLIJx(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.TopHatBox
    //----------------------------------------------------
    /**
     * Applies a top-hat filter for background subtraction to the input image.
     * 
     * Parameters
     * ----------
     * input : Image
     *     The input image where the background is subtracted from.
     * destination : Image
     *     The output image where results are written into.
     * radius_x : Image
     *     Radius of the background determination region in X.
     * radius_y : Image
     *     Radius of the background determination region in Y.
     * radius_z : Image
     *     Radius of the background determination region in Z.
     * 
     */
    public static ClearCLBuffer topHatBox(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5) {
        TopHatBox.topHatBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.TopHatSphere
    //----------------------------------------------------
    /**
     * Applies a top-hat filter for background subtraction to the input image.
     * 
     * Parameters
     * ----------
     * input : Image
     *     The input image where the background is subtracted from.
     * destination : Image
     *     The output image where results are written into.
     * radius_x : Image
     *     Radius of the background determination region in X.
     * radius_y : Image
     *     Radius of the background determination region in Y.
     * radius_z : Image
     *     Radius of the background determination region in Z.
     * 
     */
    public static ClearCLBuffer topHatSphere(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5) {
        TopHatSphere.topHatSphere(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.Exponential
    //----------------------------------------------------
    /**
     * Computes base exponential of all pixels values.
     * 
     * f(x) = exp(x)
     */
    public static ClearCLImageInterface exponential(ClearCLImageInterface source, ClearCLImageInterface destination) {
        Exponential.exponential(getCLIJ2(), source, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.Logarithm
    //----------------------------------------------------
    /**
     * Computes base e logarithm of all pixels values.
     * 
     * f(x) = log(x)
     */
    public static ClearCLImageInterface logarithm(ClearCLImageInterface source, ClearCLImageInterface destination) {
        Logarithm.logarithm(getCLIJ2(), source, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.GenerateDistanceMatrix
    //----------------------------------------------------
    /**
     * Computes the distance between all point coordinates given in two point lists.
     * 
     * Takes two images containing pointlists (dimensionality n * d, n: number of points and d: dimensionality) and builds up a matrix containing the distances between these points. 
     * 
     * Convention: Given two point lists with dimensionality n * d and m * d, the distance matrix will be of size(n + 1) * (m + 1). The first row and column contain zeros. They represent the distance of the objects to a theoretical background object. In that way, distance matrices are of the same size as touch matrices (see generateTouchMatrix). Thus, one can threshold a distance matrix to generate a touch matrix out of it for drawing meshes.
     */
    public static ClearCLBuffer generateDistanceMatrix(ClearCLBuffer coordinate_list1, ClearCLBuffer coordinate_list2, ClearCLBuffer distance_matrix_destination) {
        GenerateDistanceMatrix.generateDistanceMatrix(getCLIJ2(), coordinate_list1, coordinate_list2, distance_matrix_destination);
        return distance_matrix_destination;
    }


    // net.haesleinhuepf.clij2.plugins.ShortestDistances
    //----------------------------------------------------
    /**
     * Determine the shortest distance from a distance matrix. 
     * 
     * This corresponds to the minimum for each individial column.
     */
    public static ClearCLBuffer shortestDistances(ClearCLBuffer distance_matrix, ClearCLBuffer destination_minimum_distances) {
        ShortestDistances.shortestDistances(getCLIJ2(), distance_matrix, destination_minimum_distances);
        return destination_minimum_distances;
    }


    // net.haesleinhuepf.clij2.plugins.SpotsToPointList
    //----------------------------------------------------
    /**
     * Transforms a spots image as resulting from maximum/minimum detection in an image where every column contains d 
     * pixels (with d = dimensionality of the original image) with the coordinates of the maxima/minima.
     */
    public static ClearCLBuffer spotsToPointList(ClearCLBuffer input_spots, ClearCLBuffer destination_pointlist) {
        SpotsToPointList.spotsToPointList(getCLIJ2(), input_spots, destination_pointlist);
        return destination_pointlist;
    }


    // net.haesleinhuepf.clij2.plugins.TransposeXY
    //----------------------------------------------------
    /**
     * Transpose X and Y axes of an image.
     * 
     * Parameters
     * ----------
     * input : Image
     *     The input image.
     * destination : Image
     *     The output image where results are written into.
     * 
     */
    public static ClearCLBuffer transposeXY(ClearCLBuffer input, ClearCLBuffer destination) {
        TransposeXY.transposeXY(getCLIJ2(), input, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.TransposeXZ
    //----------------------------------------------------
    /**
     * Transpose X and Z axes of an image.
     * 
     * Parameters
     * ----------
     * input : Image
     *     The input image.
     * destination : Image
     *     The output image where results are written into.
     * 
     */
    public static ClearCLBuffer transposeXZ(ClearCLBuffer input, ClearCLBuffer destination) {
        TransposeXZ.transposeXZ(getCLIJ2(), input, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.TransposeYZ
    //----------------------------------------------------
    /**
     * Transpose Y and Z axes of an image.
     * 
     * Parameters
     * ----------
     * input : Image
     *     The input image.
     * destination : Image
     *     The output image where results are written into.
     * 
     */
    public static ClearCLBuffer transposeYZ(ClearCLBuffer input, ClearCLBuffer destination) {
        TransposeYZ.transposeYZ(getCLIJ2(), input, destination);
        return destination;
    }


    // net.haesleinhuepf.clijx.piv.FastParticleImageVelocimetry
    //----------------------------------------------------
    /**
     * 
     */
    public static ClearCLBuffer particleImageVelocimetry2D(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, ClearCLBuffer arg4, double arg5) {
        FastParticleImageVelocimetry.particleImageVelocimetry2D(getCLIJ2(), arg1, arg2, arg3, arg4, new Double (arg5).intValue());
        return arg4;
    }


    // net.haesleinhuepf.clijx.piv.ParticleImageVelocimetry
    //----------------------------------------------------
    /**
     * For every pixel in source image 1, determine the pixel with the most similar intensity in 
     *  the local neighborhood with a given radius in source image 2. Write the distance in 
     * X, Y and Z in the three corresponding destination images.
     */
    public static ClearCLBuffer particleImageVelocimetry(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, ClearCLBuffer arg4, ClearCLBuffer arg5, double arg6, double arg7, double arg8) {
        ParticleImageVelocimetry.particleImageVelocimetry(getCLIJ2(), arg1, arg2, arg3, arg4, arg5, new Double (arg6).intValue(), new Double (arg7).intValue(), new Double (arg8).intValue());
        return arg5;
    }


    // net.haesleinhuepf.clijx.piv.ParticleImageVelocimetryTimelapse
    //----------------------------------------------------
    /**
     * Run particle image velocimetry on a 2D+t timelapse.
     */
    public static ClearCLBuffer particleImageVelocimetryTimelapse(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, ClearCLBuffer arg4, double arg5, double arg6, double arg7, boolean arg8) {
        ParticleImageVelocimetryTimelapse.particleImageVelocimetryTimelapse(getCLIJ2(), arg1, arg2, arg3, arg4, new Double (arg5).intValue(), new Double (arg6).intValue(), new Double (arg7).intValue(), arg8);
        return arg4;
    }


    // net.haesleinhuepf.clijx.registration.DeformableRegistration2D
    //----------------------------------------------------
    /**
     * Applies particle image velocimetry to two images and registers them afterwards by warping input image 2 with a smoothed vector field.
     */
    public static ClearCLBuffer deformableRegistration2D(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4, double arg5) {
        DeformableRegistration2D.deformableRegistration2D(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).intValue(), new Double (arg5).intValue());
        return arg3;
    }


    // net.haesleinhuepf.clijx.registration.TranslationRegistration
    //----------------------------------------------------
    /**
     * Measures center of mass of thresholded objects in the two input images and translates the second image so that it better fits to the first image.
     */
    public static ClearCLBuffer translationRegistration(ClearCLBuffer arg1, ClearCLBuffer arg2, double[] arg3) {
        TranslationRegistration.translationRegistration(getCLIJ(), arg1, arg2, arg3);
        return arg2;
    }

    /**
     * Measures center of mass of thresholded objects in the two input images and translates the second image so that it better fits to the first image.
     */
    public static ClearCLBuffer translationRegistration(ClearCLBuffer input1, ClearCLBuffer input2, ClearCLBuffer destination) {
        TranslationRegistration.translationRegistration(getCLIJ(), input1, input2, destination);
        return destination;
    }


    // net.haesleinhuepf.clijx.registration.TranslationTimelapseRegistration
    //----------------------------------------------------
    /**
     * Applies 2D translation registration to every pair of t, t+1 slices of a 2D+t image stack.
     */
    public static ClearCLBuffer translationTimelapseRegistration(ClearCLBuffer input, ClearCLBuffer output) {
        TranslationTimelapseRegistration.translationTimelapseRegistration(getCLIJ(), input, output);
        return output;
    }


    // net.haesleinhuepf.clij2.plugins.SetWhereXequalsY
    //----------------------------------------------------
    /**
     * Sets all pixel values a of a given image A to a constant value v in case its coordinates x == y. 
     * 
     * Otherwise the pixel is not overwritten.
     * If you want to initialize an identity transfrom matrix, set all pixels to 0 first.
     */
    public static ClearCLImageInterface setWhereXequalsY(ClearCLImageInterface arg1, double arg2) {
        SetWhereXequalsY.setWhereXequalsY(getCLIJ2(), arg1, new Double (arg2).floatValue());
        return arg1;
    }


    // net.haesleinhuepf.clij2.plugins.LaplaceDiamond
    //----------------------------------------------------
    /**
     * Applies the Laplace operator (Diamond neighborhood) to an image.
     */
    public static ClearCLBuffer laplaceSphere(ClearCLBuffer input, ClearCLBuffer destination) {
        LaplaceDiamond.laplaceSphere(getCLIJ2(), input, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.Image2DToResultsTable
    //----------------------------------------------------

    // net.haesleinhuepf.clij2.plugins.WriteValuesToPositions
    //----------------------------------------------------
    /**
     * Takes an image with three/four rows (2D: height = 3; 3D: height = 4): x, y [, z] and v and target image. 
     * 
     * The value v will be written at position x/y[/z] in the target image.
     */
    public static ClearCLBuffer writeValuesToPositions(ClearCLBuffer positions_and_values, ClearCLBuffer destination) {
        WriteValuesToPositions.writeValuesToPositions(getCLIJ2(), positions_and_values, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.GetSize
    //----------------------------------------------------

    // net.haesleinhuepf.clij2.plugins.MultiplyMatrix
    //----------------------------------------------------
    /**
     * Multiplies two matrices with each other.
     */
    public static ClearCLBuffer multiplyMatrix(ClearCLBuffer matrix1, ClearCLBuffer matrix2, ClearCLBuffer matrix_destination) {
        MultiplyMatrix.multiplyMatrix(getCLIJ2(), matrix1, matrix2, matrix_destination);
        return matrix_destination;
    }


    // net.haesleinhuepf.clij2.plugins.MatrixEqual
    //----------------------------------------------------
    /**
     * Checks if all elements of a matrix are different by less than or equal to a given tolerance. 
     * 
     * The result will be put in the results table in column "MatrixEqual" as 1 if yes and 0 otherwise.
     */
    public static ClearCLBuffer matrixEqual(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        MatrixEqual.matrixEqual(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.PowerImages
    //----------------------------------------------------
    /**
     * Calculates x to the power of y pixel wise of two images X and Y.
     */
    public static ClearCLBuffer powerImages(ClearCLBuffer input, ClearCLBuffer exponent, ClearCLBuffer destination) {
        PowerImages.powerImages(getCLIJ2(), input, exponent, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.Equal
    //----------------------------------------------------
    /**
     * Determines if two images A and B equal pixel wise.
     * 
     * <pre>f(a, b) = 1 if a == b; 0 otherwise.</pre>
     * 
     * Parameters
     * ----------
     * source1 : Image
     *     The first image to be compared with.
     * source2 : Image
     *     The second image to be compared with the first.
     * destination : Image
     *     The resulting binary image where pixels will be 1 only if source1 and source2 equal in the given pixel.
     * 
     */
    public static ClearCLImageInterface equal(ClearCLImageInterface source1, ClearCLImageInterface source2, ClearCLImageInterface destination) {
        Equal.equal(getCLIJ2(), source1, source2, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.GreaterOrEqual
    //----------------------------------------------------
    /**
     * Determines if two images A and B greater or equal pixel wise. 
     * 
     * f(a, b) = 1 if a >= b; 0 otherwise. 
     */
    public static ClearCLImageInterface greaterOrEqual(ClearCLImageInterface source1, ClearCLImageInterface source2, ClearCLImageInterface destination) {
        GreaterOrEqual.greaterOrEqual(getCLIJ2(), source1, source2, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.Greater
    //----------------------------------------------------
    /**
     * Determines if two images A and B greater pixel wise.
     * 
     * f(a, b) = 1 if a > b; 0 otherwise. 
     */
    public static ClearCLImageInterface greater(ClearCLImageInterface source1, ClearCLImageInterface source2, ClearCLImageInterface destination) {
        Greater.greater(getCLIJ2(), source1, source2, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.Smaller
    //----------------------------------------------------
    /**
     * Determines if two images A and B smaller pixel wise.
     * 
     * f(a, b) = 1 if a < b; 0 otherwise. 
     */
    public static ClearCLBuffer smaller(ClearCLBuffer source1, ClearCLBuffer source2, ClearCLBuffer destination) {
        Smaller.smaller(getCLIJ2(), source1, source2, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.SmallerOrEqual
    //----------------------------------------------------
    /**
     * Determines if two images A and B smaller or equal pixel wise.
     * 
     * f(a, b) = 1 if a <= b; 0 otherwise. 
     */
    public static ClearCLBuffer smallerOrEqual(ClearCLBuffer source1, ClearCLBuffer source2, ClearCLBuffer destination) {
        SmallerOrEqual.smallerOrEqual(getCLIJ2(), source1, source2, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.NotEqual
    //----------------------------------------------------
    /**
     * Determines if two images A and B equal pixel wise.
     * 
     * f(a, b) = 1 if a != b; 0 otherwise.
     * 
     * Parameters
     * ----------
     * source1 : Image
     *     The first image to be compared with.
     * source2 : Image
     *     The second image to be compared with the first.
     * destination : Image
     *     The resulting binary image where pixels will be 1 only if source1 and source2 are not equal in the given pixel.
     * 
     */
    public static ClearCLBuffer notEqual(ClearCLImageInterface source1, ClearCLImageInterface source2, ClearCLBuffer destination) {
        NotEqual.notEqual(getCLIJ2(), source1, source2, destination);
        return destination;
    }


    // net.haesleinhuepf.clijx.io.ReadImageFromDisc
    //----------------------------------------------------
    /**
     * Read an image from disc.
     */
    public static ClearCLBuffer readImageFromDisc(String arg1) {
        ClearCLBuffer result = ReadImageFromDisc.readImageFromDisc(getCLIJ(), arg1);
        return result;
    }


    // net.haesleinhuepf.clijx.io.ReadRawImageFromDisc
    //----------------------------------------------------
    /**
     * Reads a raw file from disc and pushes it immediately to the GPU.
     */
    public static ClearCLBuffer readRawImageFromDisc(ClearCLBuffer arg1, String arg2) {
        ReadRawImageFromDisc.readRawImageFromDisc(getCLIJ(), arg1, arg2);
        return arg1;
    }

    /**
     * Reads a raw file from disc and pushes it immediately to the GPU.
     */
    public static ClearCLBuffer readRawImageFromDisc(String arg1, double arg2, double arg3, double arg4, double arg5) {
        ClearCLBuffer result = ReadRawImageFromDisc.readRawImageFromDisc(getCLIJ(), arg1, new Double (arg2).intValue(), new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return result;
    }


    // net.haesleinhuepf.clijx.io.PreloadFromDisc
    //----------------------------------------------------
    /**
     * This plugin takes two image filenames and loads them into RAM. The first image is returned immediately, the second image is loaded in the background and  will be returned when the plugin is called again.
     * 
     *  It is assumed that all images have the same size. If this is not the case, call release(image) before  getting the second image.
     */
    public static ClearCLBuffer preloadFromDisc(ClearCLBuffer destination, String filename, String nextFilename, String loaderId) {
        PreloadFromDisc.preloadFromDisc(getCLIJ(), destination, filename, nextFilename, loaderId);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.EqualConstant
    //----------------------------------------------------
    /**
     * Determines if an image A and a constant b are equal.
     * 
     * <pre>f(a, b) = 1 if a == b; 0 otherwise.</pre>
     * 
     * Parameters
     * ----------
     * source : Image
     *     The image where every pixel is compared to the constant.
     * destination : Image
     *     The resulting binary image where pixels will be 1 only if source1 and source2 equal in the given pixel.
     * constant : float
     *     The constant where every pixel is compared to.
     * 
     */
    public static ClearCLImageInterface equalConstant(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3) {
        EqualConstant.equalConstant(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.GreaterOrEqualConstant
    //----------------------------------------------------
    /**
     * Determines if two images A and B greater or equal pixel wise. 
     * 
     * f(a, b) = 1 if a >= b; 0 otherwise. 
     */
    public static ClearCLImageInterface greaterOrEqualConstant(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3) {
        GreaterOrEqualConstant.greaterOrEqualConstant(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.GreaterConstant
    //----------------------------------------------------
    /**
     * Determines if two images A and B greater pixel wise. 
     * 
     * f(a, b) = 1 if a > b; 0 otherwise. 
     */
    public static ClearCLImageInterface greaterConstant(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3) {
        GreaterConstant.greaterConstant(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.SmallerConstant
    //----------------------------------------------------
    /**
     * Determines if two images A and B smaller pixel wise.
     * 
     * f(a, b) = 1 if a < b; 0 otherwise. 
     */
    public static ClearCLBuffer smallerConstant(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        SmallerConstant.smallerConstant(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.SmallerOrEqualConstant
    //----------------------------------------------------
    /**
     * Determines if two images A and B smaller or equal pixel wise.
     * 
     * f(a, b) = 1 if a <= b; 0 otherwise. 
     */
    public static ClearCLBuffer smallerOrEqualConstant(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        SmallerOrEqualConstant.smallerOrEqualConstant(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.NotEqualConstant
    //----------------------------------------------------
    /**
     * Determines if two images A and B equal pixel wise.
     * 
     * f(a, b) = 1 if a != b; 0 otherwise.Parameters
     * ----------
     * source : Image
     *     The image where every pixel is compared to the constant.
     * destination : Image
     *     The resulting binary image where pixels will be 1 only if source1 and source2 equal in the given pixel.
     * constant : float
     *     The constant where every pixel is compared to.
     * 
     */
    public static ClearCLBuffer notEqualConstant(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        NotEqualConstant.notEqualConstant(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.DrawBox
    //----------------------------------------------------
    /**
     * Draws a box at a given start point with given size. 
     * All pixels other than in the box are untouched. Consider using `set(buffer, 0);` in advance.
     */
    public static ClearCLImageInterface drawBox(ClearCLImageInterface arg1, double arg2, double arg3, double arg4, double arg5) {
        DrawBox.drawBox(getCLIJ2(), arg1, new Double (arg2).floatValue(), new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue());
        return arg1;
    }

    /**
     * Draws a box at a given start point with given size. 
     * All pixels other than in the box are untouched. Consider using `set(buffer, 0);` in advance.
     */
    public static ClearCLImageInterface drawBox(ClearCLImageInterface arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7) {
        DrawBox.drawBox(getCLIJ2(), arg1, new Double (arg2).floatValue(), new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), new Double (arg6).floatValue(), new Double (arg7).floatValue());
        return arg1;
    }

    /**
     * Draws a box at a given start point with given size. 
     * All pixels other than in the box are untouched. Consider using `set(buffer, 0);` in advance.
     */
    public static ClearCLImageInterface drawBox(ClearCLImageInterface arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8) {
        DrawBox.drawBox(getCLIJ2(), arg1, new Double (arg2).floatValue(), new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), new Double (arg6).floatValue(), new Double (arg7).floatValue(), new Double (arg8).floatValue());
        return arg1;
    }


    // net.haesleinhuepf.clij2.plugins.DrawLine
    //----------------------------------------------------
    /**
     * Draws a line between two points with a given thickness. 
     * 
     * All pixels other than on the line are untouched. Consider using `set(buffer, 0);` in advance.
     */
    public static ClearCLImageInterface drawLine(ClearCLImageInterface arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8) {
        DrawLine.drawLine(getCLIJ2(), arg1, new Double (arg2).floatValue(), new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), new Double (arg6).floatValue(), new Double (arg7).floatValue(), new Double (arg8).floatValue());
        return arg1;
    }

    /**
     * Draws a line between two points with a given thickness. 
     * 
     * All pixels other than on the line are untouched. Consider using `set(buffer, 0);` in advance.
     */
    public static ClearCLImageInterface drawLine(ClearCLImageInterface arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8, double arg9) {
        DrawLine.drawLine(getCLIJ2(), arg1, new Double (arg2).floatValue(), new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), new Double (arg6).floatValue(), new Double (arg7).floatValue(), new Double (arg8).floatValue(), new Double (arg9).floatValue());
        return arg1;
    }


    // net.haesleinhuepf.clij2.plugins.DrawSphere
    //----------------------------------------------------
    /**
     * Draws a sphere around a given point with given radii in x, y and z (if 3D). 
     * 
     *  All pixels other than in the sphere are untouched. Consider using `set(buffer, 0);` in advance.
     */
    public static ClearCLImageInterface drawSphere(ClearCLImageInterface arg1, double arg2, double arg3, double arg4, double arg5) {
        DrawSphere.drawSphere(getCLIJ2(), arg1, new Double (arg2).floatValue(), new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue());
        return arg1;
    }

    /**
     * Draws a sphere around a given point with given radii in x, y and z (if 3D). 
     * 
     *  All pixels other than in the sphere are untouched. Consider using `set(buffer, 0);` in advance.
     */
    public static ClearCLImageInterface drawSphere(ClearCLImageInterface arg1, double arg2, double arg3, double arg4, double arg5, double arg6) {
        DrawSphere.drawSphere(getCLIJ2(), arg1, new Double (arg2).floatValue(), new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), new Double (arg6).floatValue());
        return arg1;
    }

    /**
     * Draws a sphere around a given point with given radii in x, y and z (if 3D). 
     * 
     *  All pixels other than in the sphere are untouched. Consider using `set(buffer, 0);` in advance.
     */
    public static ClearCLImageInterface drawSphere(ClearCLImageInterface arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7) {
        DrawSphere.drawSphere(getCLIJ2(), arg1, new Double (arg2).floatValue(), new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), new Double (arg6).floatValue(), new Double (arg7).floatValue());
        return arg1;
    }

    /**
     * Draws a sphere around a given point with given radii in x, y and z (if 3D). 
     * 
     *  All pixels other than in the sphere are untouched. Consider using `set(buffer, 0);` in advance.
     */
    public static ClearCLImageInterface drawSphere(ClearCLImageInterface arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8) {
        DrawSphere.drawSphere(getCLIJ2(), arg1, new Double (arg2).floatValue(), new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), new Double (arg6).floatValue(), new Double (arg7).floatValue(), new Double (arg8).floatValue());
        return arg1;
    }


    // net.haesleinhuepf.clij2.plugins.ReplaceIntensity
    //----------------------------------------------------
    /**
     * Replaces a specific intensity in an image with a given new value.
     */
    public static ClearCLImageInterface replaceIntensity(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        ReplaceIntensity.replaceIntensity(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.BoundingBox
    //----------------------------------------------------
    /**
     * Determines the bounding box of all non-zero pixels in a binary image. 
     * 
     * If called from macro, the positions will be stored in a new row of ImageJs Results table in the columns 'BoundingBoxX', 'BoundingBoxY', 'BoundingBoxZ', 'BoundingBoxWidth', 'BoundingBoxHeight' 'BoundingBoxDepth'.In case of 2D images Z and depth will be zero.
     */
    public static ClearCLBuffer boundingBox(ClearCLBuffer source) {
        BoundingBox.boundingBox(getCLIJ2(), source);
        return source;
    }


    // net.haesleinhuepf.clij2.plugins.MinimumOfMaskedPixels
    //----------------------------------------------------
    /**
     * Determines the minimum intensity in a masked image. 
     * 
     * But only in pixels which have non-zero values in another mask image.
     */
    public static ClearCLBuffer minimumOfMaskedPixels(ClearCLBuffer source, ClearCLBuffer mask) {
        MinimumOfMaskedPixels.minimumOfMaskedPixels(getCLIJ2(), source, mask);
        return mask;
    }


    // net.haesleinhuepf.clij2.plugins.MaximumOfMaskedPixels
    //----------------------------------------------------
    /**
     * Determines the maximum intensity in an image, but only in pixels which have non-zero values in another mask image.
     * 
     * Parameters
     * ----------
     * source : Image
     *     The image of which the minimum of all pixels or voxels where mask=1 will be determined.
     * mask : Image
     *     A binary image marking all pixels with 1 which should be taken into accout.
     * 
     */
    public static ClearCLBuffer maximumOfMaskedPixels(ClearCLBuffer source, ClearCLBuffer mask) {
        MaximumOfMaskedPixels.maximumOfMaskedPixels(getCLIJ2(), source, mask);
        return mask;
    }


    // net.haesleinhuepf.clij2.plugins.MeanOfMaskedPixels
    //----------------------------------------------------
    /**
     * Determines the mean intensity in a masked image. 
     * 
     * Only in pixels which have non-zero values in another binary mask image.
     */
    public static ClearCLBuffer meanOfMaskedPixels(ClearCLBuffer source, ClearCLBuffer mask) {
        MeanOfMaskedPixels.meanOfMaskedPixels(getCLIJ2(), source, mask);
        return mask;
    }


    // net.haesleinhuepf.clij2.plugins.LabelToMask
    //----------------------------------------------------
    /**
     * Masks a single label in a label map. 
     * 
     * Sets all pixels in the target image to 1, where the given label index was present in the label map. Other pixels are set to 0.
     */
    public static ClearCLBuffer labelToMask(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        LabelToMask.labelToMask(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.NClosestPoints
    //----------------------------------------------------
    /**
     * Determine the n point indices with shortest distance for all points in a distance matrix. 
     * 
     * This corresponds to the n row indices with minimum values for each column of the distance matrix.
     */
    public static ClearCLBuffer nClosestPoints(ClearCLBuffer arg1, ClearCLBuffer arg2) {
        NClosestPoints.nClosestPoints(getCLIJ2(), arg1, arg2);
        return arg2;
    }

    /**
     * Determine the n point indices with shortest distance for all points in a distance matrix. 
     * 
     * This corresponds to the n row indices with minimum values for each column of the distance matrix.
     */
    public static ClearCLBuffer nClosestPoints(ClearCLBuffer arg1, ClearCLBuffer arg2, boolean arg3, boolean arg4) {
        NClosestPoints.nClosestPoints(getCLIJ2(), arg1, arg2, arg3, arg4);
        return arg2;
    }


    // net.haesleinhuepf.clijx.plugins.GaussJordan
    //----------------------------------------------------
    /**
     * Gauss Jordan elimination algorithm for solving linear equation systems. 
     * 
     * Ent the equation coefficients as an n*n sized image A and an n*1 sized image B:
     * <pre>a(1,1)*x + a(2,1)*y + a(3,1)+z = b(1)
     * a(2,1)*x + a(2,2)*y + a(3,2)+z = b(2)
     * a(3,1)*x + a(3,2)*y + a(3,3)+z = b(3)
     * </pre>
     * The results will then be given in an n*1 image with values [x, y, z].
     * 
     * Adapted from: 
     * https://github.com/qbunia/rodinia/blob/master/opencl/gaussian/gaussianElim_kernels.cl
     * L.G. Szafaryn, K. Skadron and J. Saucerman. "Experiences Accelerating MATLAB Systems
     * //Biology Applications." in Workshop on Biomedicine in Computing (BiC) at the International
     * //Symposium on Computer Architecture (ISCA), June 2009.
     */
    public static ClearCLBuffer gaussJordan(ClearCLBuffer A_matrix, ClearCLBuffer B_result_vector, ClearCLBuffer solution_destination) {
        GaussJordan.gaussJordan(getCLIJ(), A_matrix, B_result_vector, solution_destination);
        return solution_destination;
    }


    // net.haesleinhuepf.clij2.plugins.StatisticsOfLabelledPixels
    //----------------------------------------------------
    /**
     * Determines bounding box, area (in pixels/voxels), min, max and mean intensity 
     *  of labelled objects in a label map and corresponding pixels in the original image. 
     * 
     * Instead of a label map, you can also use a binary image as a binary image is a label map with just one label.
     * 
     * This method is executed on the CPU and not on the GPU/OpenCL device.
     */
    public static ClearCLBuffer statisticsOfLabelledPixels(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        StatisticsOfLabelledPixels.statisticsOfLabelledPixels(getCLIJ2(), arg1, arg2, new Double (arg3).intValue());
        return arg2;
    }

    /**
     * Determines bounding box, area (in pixels/voxels), min, max and mean intensity 
     *  of labelled objects in a label map and corresponding pixels in the original image. 
     * 
     * Instead of a label map, you can also use a binary image as a binary image is a label map with just one label.
     * 
     * This method is executed on the CPU and not on the GPU/OpenCL device.
     */
    public static ClearCLBuffer statisticsOfLabelledPixels(ClearCLBuffer input, ClearCLBuffer labelmap) {
        StatisticsOfLabelledPixels.statisticsOfLabelledPixels(getCLIJ2(), input, labelmap);
        return labelmap;
    }

    /**
     * Determines bounding box, area (in pixels/voxels), min, max and mean intensity 
     *  of labelled objects in a label map and corresponding pixels in the original image. 
     * 
     * Instead of a label map, you can also use a binary image as a binary image is a label map with just one label.
     * 
     * This method is executed on the CPU and not on the GPU/OpenCL device.
     */
    public static ClearCLBuffer statisticsOfLabelledPixels(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4) {
        StatisticsOfLabelledPixels.statisticsOfLabelledPixels(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return arg2;
    }

    /**
     * 
     */
    public static ClearCLBuffer statisticsOfLabelledPixels_single_threaded(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4) {
        StatisticsOfLabelledPixels.statisticsOfLabelledPixels_single_threaded(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return arg2;
    }

    /**
     * Determines bounding box, area (in pixels/voxels), min, max and mean intensity 
     *  of labelled objects in a label map and corresponding pixels in the original image. 
     * 
     * Instead of a label map, you can also use a binary image as a binary image is a label map with just one label.
     * 
     * This method is executed on the CPU and not on the GPU/OpenCL device.
     */
    public static ClearCLBuffer statisticsOfLabelledPixels(ClearCLBuffer arg1, ClearCLBuffer arg2, ResultsTable arg3) {
        StatisticsOfLabelledPixels.statisticsOfLabelledPixels(getCLIJ2(), arg1, arg2, arg3);
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.VarianceOfAllPixels
    //----------------------------------------------------
    /**
     * Determines the variance of all pixels in an image. 
     * 
     * The value will be stored in a new row of ImageJs
     * Results table in the column 'Variance'.
     */
    public static ClearCLBuffer varianceOfAllPixels(ClearCLBuffer source) {
        VarianceOfAllPixels.varianceOfAllPixels(getCLIJ2(), source);
        return source;
    }

    /**
     * Determines the variance of all pixels in an image. 
     * 
     * The value will be stored in a new row of ImageJs
     * Results table in the column 'Variance'.
     */
    public static ClearCLImageInterface varianceOfAllPixels(ClearCLImageInterface arg1, double arg2) {
        VarianceOfAllPixels.varianceOfAllPixels(getCLIJ2(), arg1, new Double (arg2).floatValue());
        return arg1;
    }


    // net.haesleinhuepf.clij2.plugins.StandardDeviationOfAllPixels
    //----------------------------------------------------
    /**
     * Determines the standard deviation of all pixels in an image. 
     * 
     * The value will be stored in a new row of ImageJs
     * Results table in the column 'Standard_deviation'.
     */
    public static ClearCLImageInterface standardDeviationOfAllPixels(ClearCLImageInterface source) {
        StandardDeviationOfAllPixels.standardDeviationOfAllPixels(getCLIJ2(), source);
        return source;
    }

    /**
     * Determines the standard deviation of all pixels in an image. 
     * 
     * The value will be stored in a new row of ImageJs
     * Results table in the column 'Standard_deviation'.
     */
    public static ClearCLImageInterface standardDeviationOfAllPixels(ClearCLImageInterface arg1, double arg2) {
        StandardDeviationOfAllPixels.standardDeviationOfAllPixels(getCLIJ2(), arg1, new Double (arg2).floatValue());
        return arg1;
    }


    // net.haesleinhuepf.clij2.plugins.VarianceOfMaskedPixels
    //----------------------------------------------------
    /**
     * Determines the variance in an image, but only in pixels which have non-zero values in another binary mask image. 
     * 
     * The result is put in the results table as new column named 'Masked_variance'.
     */
    public static ClearCLBuffer varianceOfMaskedPixels(ClearCLBuffer source, ClearCLBuffer mask) {
        VarianceOfMaskedPixels.varianceOfMaskedPixels(getCLIJ2(), source, mask);
        return mask;
    }

    /**
     * Determines the variance in an image, but only in pixels which have non-zero values in another binary mask image. 
     * 
     * The result is put in the results table as new column named 'Masked_variance'.
     */
    public static ClearCLBuffer varianceOfMaskedPixels(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        VarianceOfMaskedPixels.varianceOfMaskedPixels(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.StandardDeviationOfMaskedPixels
    //----------------------------------------------------
    /**
     * Determines the standard deviation of all pixels in an image which have non-zero value in a corresponding mask image. 
     * 
     * The value will be stored in a new row of ImageJs
     * Results table in the column 'Masked_standard_deviation'.
     */
    public static ClearCLBuffer standardDeviationOfMaskedPixels(ClearCLBuffer source, ClearCLBuffer mask) {
        StandardDeviationOfMaskedPixels.standardDeviationOfMaskedPixels(getCLIJ2(), source, mask);
        return mask;
    }

    /**
     * Determines the standard deviation of all pixels in an image which have non-zero value in a corresponding mask image. 
     * 
     * The value will be stored in a new row of ImageJs
     * Results table in the column 'Masked_standard_deviation'.
     */
    public static ClearCLBuffer standardDeviationOfMaskedPixels(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        StandardDeviationOfMaskedPixels.standardDeviationOfMaskedPixels(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.ExcludeLabelsOnEdges
    //----------------------------------------------------
    /**
     * Removes all labels from a label map which touch the edges of the image (in X, Y and Z if the image is 3D). 
     * 
     * Remaining label elements are renumbered afterwards.
     */
    public static ClearCLBuffer excludeLabelsOnEdges(ClearCLBuffer label_map_input, ClearCLBuffer label_map_destination) {
        ExcludeLabelsOnEdges.excludeLabelsOnEdges(getCLIJ2(), label_map_input, label_map_destination);
        return label_map_destination;
    }


    // net.haesleinhuepf.clij2.plugins.BinarySubtract
    //----------------------------------------------------
    /**
     * Subtracts one binary image from another.
     * 
     * Parameters
     * ----------
     * minuend : Image
     *     The first binary input image to be processed.
     * suubtrahend : Image
     *     The second binary input image to be subtracted from the first.
     * destination : Image
     *     The output image where results are written into.
     * 
     */
    public static ClearCLImageInterface binarySubtract(ClearCLImageInterface minuend, ClearCLImageInterface subtrahend, ClearCLImageInterface destination) {
        BinarySubtract.binarySubtract(getCLIJ2(), minuend, subtrahend, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.BinaryEdgeDetection
    //----------------------------------------------------
    /**
     * Determines pixels/voxels which are on the surface of binary objects and sets only them to 1 in the 
     * destination image. All other pixels are set to 0.
     * 
     * Parameters
     * ----------
     * source : Image
     *     The binary input image where edges will be searched.
     * destination : Image
     *     The output image where edge pixels will be 1.
     * 
     */
    public static ClearCLImageInterface binaryEdgeDetection(ClearCLImageInterface source, ClearCLImageInterface destination) {
        BinaryEdgeDetection.binaryEdgeDetection(getCLIJ2(), source, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.DistanceMap
    //----------------------------------------------------
    /**
     * Generates a distance map from a binary image. 
     * 
     * Pixels with non-zero value in the binary image are set to a number representing the distance to the closest zero-value pixel.
     * 
     * Note: This function is known to be slow. See the web for alternatives: 
     * Note: This is not a distance matrix. See generateDistanceMatrix for details.
     */
    public static ClearCLBuffer distanceMap(ClearCLBuffer source, ClearCLBuffer destination) {
        DistanceMap.distanceMap(getCLIJ2(), source, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.PullAsROI
    //----------------------------------------------------
    /**
     * Pulls a binary image from the GPU memory and puts it on the currently active ImageJ window as region of interest.
     */
    public static ClearCLBuffer pullAsROI(ClearCLBuffer binary_input) {
        PullAsROI.pullAsROI(getCLIJ2(), binary_input);
        return binary_input;
    }


    // net.haesleinhuepf.clij2.plugins.PullLabelsToROIManager
    //----------------------------------------------------
    /**
     * Pulls all labels in a label map as ROIs to the ROI manager.
     */
    public static ClearCLBuffer pullLabelsToROIManager(ClearCLBuffer labelmap_input) {
        PullLabelsToROIManager.pullLabelsToROIManager(getCLIJ2(), labelmap_input);
        return labelmap_input;
    }

    /**
     * Pulls all labels in a label map as ROIs to the ROI manager.
     */
    public static ClearCLBuffer pullLabelsToROIManager(ClearCLBuffer arg1, RoiManager arg2) {
        PullLabelsToROIManager.pullLabelsToROIManager(getCLIJ2(), arg1, arg2);
        return arg1;
    }


    // net.haesleinhuepf.clij2.plugins.NonzeroMaximumDiamond
    //----------------------------------------------------
    /**
     * Apply a maximum filter (diamond shape) to the input image. 
     * 
     * The radius is fixed to 1 and pixels with value 0 are ignored.
     * Note: Pixels with 0 value in the input image will not be overwritten in the output image.
     * Thus, the result image should be initialized by copying the original image in advance.
     */
    public static ClearCLImageInterface nonzeroMaximumDiamond(ClearCLImageInterface input, ClearCLImageInterface destination) {
        NonzeroMaximumDiamond.nonzeroMaximumDiamond(getCLIJ2(), input, destination);
        return destination;
    }

    /**
     * Apply a maximum filter (diamond shape) to the input image. 
     * 
     * The radius is fixed to 1 and pixels with value 0 are ignored.
     * Note: Pixels with 0 value in the input image will not be overwritten in the output image.
     * Thus, the result image should be initialized by copying the original image in advance.
     */
    public static ClearCLImageInterface nonzeroMaximumDiamond(ClearCLImageInterface arg1, ClearCLImageInterface arg2, ClearCLImageInterface arg3) {
        NonzeroMaximumDiamond.nonzeroMaximumDiamond(getCLIJ2(), arg1, arg2, arg3);
        return arg3;
    }

    /**
     * Apply a maximum filter (diamond shape) to the input image. 
     * 
     * The radius is fixed to 1 and pixels with value 0 are ignored.
     * Note: Pixels with 0 value in the input image will not be overwritten in the output image.
     * Thus, the result image should be initialized by copying the original image in advance.
     */
    public static ClearCLKernel nonzeroMaximumDiamond(ClearCLImageInterface arg1, ClearCLImageInterface arg2, ClearCLImageInterface arg3, ClearCLKernel arg4) {
        NonzeroMaximumDiamond.nonzeroMaximumDiamond(getCLIJ2(), arg1, arg2, arg3, arg4);
        return arg4;
    }


    // net.haesleinhuepf.clij2.plugins.OnlyzeroOverwriteMaximumDiamond
    //----------------------------------------------------
    /**
     * Apply a local maximum filter to an image which only overwrites pixels with value 0.
     */
    public static ClearCLImageInterface onlyzeroOverwriteMaximumDiamond(ClearCLImageInterface input, ClearCLImageInterface destination) {
        OnlyzeroOverwriteMaximumDiamond.onlyzeroOverwriteMaximumDiamond(getCLIJ2(), input, destination);
        return destination;
    }

    /**
     * Apply a local maximum filter to an image which only overwrites pixels with value 0.
     */
    public static ClearCLImageInterface onlyzeroOverwriteMaximumDiamond(ClearCLImageInterface arg1, ClearCLImageInterface arg2, ClearCLImageInterface arg3) {
        OnlyzeroOverwriteMaximumDiamond.onlyzeroOverwriteMaximumDiamond(getCLIJ2(), arg1, arg2, arg3);
        return arg3;
    }

    /**
     * Apply a local maximum filter to an image which only overwrites pixels with value 0.
     */
    public static ClearCLKernel onlyzeroOverwriteMaximumDiamond(ClearCLImageInterface arg1, ClearCLImageInterface arg2, ClearCLImageInterface arg3, ClearCLKernel arg4) {
        OnlyzeroOverwriteMaximumDiamond.onlyzeroOverwriteMaximumDiamond(getCLIJ2(), arg1, arg2, arg3, arg4);
        return arg4;
    }


    // net.haesleinhuepf.clij2.plugins.OnlyzeroOverwriteMaximumBox
    //----------------------------------------------------
    /**
     * Apply a local maximum filter to an image which only overwrites pixels with value 0.
     */
    public static ClearCLImageInterface onlyzeroOverwriteMaximumBox(ClearCLImageInterface input, ClearCLImageInterface destination) {
        OnlyzeroOverwriteMaximumBox.onlyzeroOverwriteMaximumBox(getCLIJ2(), input, destination);
        return destination;
    }

    /**
     * Apply a local maximum filter to an image which only overwrites pixels with value 0.
     */
    public static ClearCLImageInterface onlyzeroOverwriteMaximumBox(ClearCLImageInterface arg1, ClearCLImageInterface arg2, ClearCLImageInterface arg3) {
        OnlyzeroOverwriteMaximumBox.onlyzeroOverwriteMaximumBox(getCLIJ2(), arg1, arg2, arg3);
        return arg3;
    }

    /**
     * Apply a local maximum filter to an image which only overwrites pixels with value 0.
     */
    public static ClearCLKernel onlyzeroOverwriteMaximumBox(ClearCLImageInterface arg1, ClearCLImageInterface arg2, ClearCLImageInterface arg3, ClearCLKernel arg4) {
        OnlyzeroOverwriteMaximumBox.onlyzeroOverwriteMaximumBox(getCLIJ2(), arg1, arg2, arg3, arg4);
        return arg4;
    }


    // net.haesleinhuepf.clij2.plugins.GenerateTouchMatrix
    //----------------------------------------------------
    /**
     * Takes a labelmap with n labels and generates a (n+1)*(n+1) matrix where all pixels are set to 0 exept those where labels are touching. 
     * 
     * Only half of the matrix is filled (with x < y). For example, if labels 3 and 4 are touching then the pixel (3,4) in the matrix will be set to 1.
     * The touch matrix is a representation of a region adjacency graph
     * 
     */
    public static ClearCLBuffer generateTouchMatrix(ClearCLBuffer label_map, ClearCLBuffer touch_matrix_destination) {
        GenerateTouchMatrix.generateTouchMatrix(getCLIJ2(), label_map, touch_matrix_destination);
        return touch_matrix_destination;
    }


    // net.haesleinhuepf.clij2.plugins.DetectLabelEdges
    //----------------------------------------------------
    /**
     * Takes a labelmap and returns an image where all pixels on label edges are set to 1 and all other pixels to 0.
     * 
     * Parameters
     * ----------
     * label_map : Image
     *     The label image where edges between labels will be detected.
     * edge_image_destination : Number
     *     Binary image where edges were marked with value 1 and all other pixels will be set to 0.
     * 
     */
    public static ClearCLBuffer detectLabelEdges(ClearCLImageInterface label_map, ClearCLBuffer edge_image_destination) {
        DetectLabelEdges.detectLabelEdges(getCLIJ2(), label_map, edge_image_destination);
        return edge_image_destination;
    }


    // net.haesleinhuepf.clijx.plugins.StopWatch
    //----------------------------------------------------
    /**
     * Measures time and outputs delay to last call.
     */
    public static boolean stopWatch(String text) {
        boolean result = StopWatch.stopWatch(getCLIJ(), text);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.CountTouchingNeighbors
    //----------------------------------------------------
    /**
     * Takes a touch matrix as input and delivers a vector with number of touching neighbors per label as a vector.
     */
    public static ClearCLBuffer countTouchingNeighbors(ClearCLBuffer touch_matrix, ClearCLBuffer touching_neighbors_count_destination) {
        CountTouchingNeighbors.countTouchingNeighbors(getCLIJ2(), touch_matrix, touching_neighbors_count_destination);
        return touching_neighbors_count_destination;
    }


    // net.haesleinhuepf.clij2.plugins.ReplaceIntensities
    //----------------------------------------------------
    /**
     * Replaces integer intensities specified in a vector image. 
     * 
     * The vector image must be 3D with size (m, 1, 1) where m corresponds to the maximum intensity in the original image. Assuming the vector image contains values (0, 1, 0, 2) means: 
     *  * All pixels with value 0 (first entry in the vector image) get value 0
     *  * All pixels with value 1 get value 1
     *  * All pixels with value 2 get value 0
     *  * All pixels with value 3 get value 2
     * 
     */
    public static ClearCLImageInterface replaceIntensities(ClearCLImageInterface input, ClearCLImageInterface new_values_vector, ClearCLImageInterface destination) {
        ReplaceIntensities.replaceIntensities(getCLIJ2(), input, new_values_vector, destination);
        return destination;
    }


    // net.haesleinhuepf.clijx.plugins.DrawTwoValueLine
    //----------------------------------------------------
    /**
     * Draws a line between two points with a given thickness. 
     * 
     * Pixels close to point 1 are set to value1. Pixels closer to point 2 are set to value2 All pixels other than on the line are untouched. Consider using clij.set(buffer, 0); in advance.
     */
    public static ClearCLBuffer drawTwoValueLine(ClearCLBuffer arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8, double arg9, double arg10) {
        DrawTwoValueLine.drawTwoValueLine(getCLIJx(), arg1, new Double (arg2).floatValue(), new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), new Double (arg6).floatValue(), new Double (arg7).floatValue(), new Double (arg8).floatValue(), new Double (arg9).floatValue(), new Double (arg10).floatValue());
        return arg1;
    }


    // net.haesleinhuepf.clij2.plugins.AverageDistanceOfNClosestPoints
    //----------------------------------------------------
    /**
     * Determines the average of the n closest points for every point in a distance matrix.
     * 
     * This corresponds to the average of the n minimum values (rows) for each column of the distance matrix.
     * 
     * Parameters
     * ----------
     * distance_matrix : Image
     *     The a distance matrix to be processed.
     * distance_list_destination : Image
     *     A vector image with the same width as the distance matrix and height=1, depth=1.
     *     Determined average distances will be written into this vector.
     * n_closest_points_to_find : Number
     *     Number of smallest distances which should be averaged.
     * 
     */
    public static ClearCLBuffer averageDistanceOfNClosestPoints(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        AverageDistanceOfNClosestPoints.averageDistanceOfNClosestPoints(getCLIJ2(), arg1, arg2, new Double (arg3).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.SaveAsTIF
    //----------------------------------------------------
    /**
     * Pulls an image from the GPU memory and saves it as TIF to disc.
     */
    public static ClearCLBuffer saveAsTIF(ClearCLBuffer input, String filename) {
        SaveAsTIF.saveAsTIF(getCLIJ2(), input, filename);
        return input;
    }


    // net.haesleinhuepf.clijx.plugins.ConnectedComponentsLabelingInplace
    //----------------------------------------------------

    // net.haesleinhuepf.clij2.plugins.TouchMatrixToMesh
    //----------------------------------------------------
    /**
     * Takes a pointlist with dimensions n*d with n point coordinates in d dimensions and a touch matrix of 
     * size n*n to draw lines from all points to points if the corresponding pixel in the touch matrix is 1.
     * 
     * Parameters
     * ----------
     * pointlist : Image
     *     n*d matrix representing n coordinates with d dimensions.
     * touch_matrix : Image
     *     A 2D binary matrix with 1 in pixels (i,j) where label i touches label j.
     * mesh_destination : Image
     *     The output image where results are written into.
     * 
     */
    public static ClearCLBuffer touchMatrixToMesh(ClearCLBuffer pointlist, ClearCLBuffer touch_matrix, ClearCLBuffer mesh_destination) {
        TouchMatrixToMesh.touchMatrixToMesh(getCLIJ2(), pointlist, touch_matrix, mesh_destination);
        return mesh_destination;
    }


    // net.haesleinhuepf.clijx.plugins.AutomaticThresholdInplace
    //----------------------------------------------------

    // net.haesleinhuepf.clijx.plugins.DifferenceOfGaussianInplace3D
    //----------------------------------------------------

    // net.haesleinhuepf.clijx.plugins.AbsoluteInplace
    //----------------------------------------------------

    // net.haesleinhuepf.clij2.plugins.Resample
    //----------------------------------------------------
    /**
     * 
     */
    public static ClearCLImageInterface resample2D(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, boolean arg5) {
        Resample.resample2D(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), arg5);
        return arg2;
    }

    /**
     * 
     */
    public static ClearCLImageInterface resample3D(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5, boolean arg6) {
        Resample.resample3D(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), arg6);
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.EqualizeMeanIntensitiesOfSlices
    //----------------------------------------------------
    /**
     * Determines correction factors for each z-slice so that the average intensity in all slices can be made the same and multiplies these factors with the slices. 
     * 
     * This functionality is similar to the 'Simple Ratio Bleaching Correction' in Fiji.
     */
    public static ClearCLBuffer equalizeMeanIntensitiesOfSlices(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        EqualizeMeanIntensitiesOfSlices.equalizeMeanIntensitiesOfSlices(getCLIJ2(), arg1, arg2, new Double (arg3).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.Watershed
    //----------------------------------------------------
    /**
     * Apply a binary watershed to a binary image and introduces black pixels between objects.
     * 
     * Note: This parallel GPU-accelerated approach delivers results of limited quality.See the web for alternatives: https://github.com/clij/clij2/issues/18
     */
    public static ClearCLBuffer watershed(ClearCLBuffer binary_source, ClearCLBuffer destination) {
        Watershed.watershed(getCLIJ2(), binary_source, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.ResliceRadial
    //----------------------------------------------------
    /**
     * Computes a radial projection of an image stack. 
     * 
     * Starting point for the line is the given point in any 
     * X/Y-plane of a given input image stack. Furthermore, radius of the resulting projection must be given and scaling factors in X and Y in case pixels are not isotropic.This operation is similar to ImageJs 'Radial Reslice' method but offers less flexibility.
     */
    public static ClearCLBuffer resliceRadial(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        ResliceRadial.resliceRadial(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return arg2;
    }

    /**
     * Computes a radial projection of an image stack. 
     * 
     * Starting point for the line is the given point in any 
     * X/Y-plane of a given input image stack. Furthermore, radius of the resulting projection must be given and scaling factors in X and Y in case pixels are not isotropic.This operation is similar to ImageJs 'Radial Reslice' method but offers less flexibility.
     */
    public static ClearCLBuffer resliceRadial(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5) {
        ResliceRadial.resliceRadial(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue());
        return arg2;
    }

    /**
     * Computes a radial projection of an image stack. 
     * 
     * Starting point for the line is the given point in any 
     * X/Y-plane of a given input image stack. Furthermore, radius of the resulting projection must be given and scaling factors in X and Y in case pixels are not isotropic.This operation is similar to ImageJs 'Radial Reslice' method but offers less flexibility.
     */
    public static ClearCLBuffer resliceRadial(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8) {
        ResliceRadial.resliceRadial(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), new Double (arg6).floatValue(), new Double (arg7).floatValue(), new Double (arg8).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clijx.plugins.ShowRGB
    //----------------------------------------------------
    /**
     * Visualises three 2D images as one RGB image
     */
    public static ClearCLBuffer showRGB(ClearCLBuffer red, ClearCLBuffer green, ClearCLBuffer blue, String title) {
        ShowRGB.showRGB(getCLIJ(), red, green, blue, title);
        return blue;
    }


    // net.haesleinhuepf.clijx.plugins.ShowGrey
    //----------------------------------------------------
    /**
     * Visualises a single 2D image.
     */
    public static ClearCLBuffer showGrey(ClearCLBuffer input, String title) {
        ShowGrey.showGrey(getCLIJ(), input, title);
        return input;
    }


    // net.haesleinhuepf.clij2.plugins.Sobel
    //----------------------------------------------------
    /**
     * Convolve the image with the Sobel kernel.
     */
    public static ClearCLBuffer sobel(ClearCLBuffer source, ClearCLBuffer destination) {
        Sobel.sobel(getCLIJ2(), source, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.Absolute
    //----------------------------------------------------
    /**
     * Computes the absolute value of every individual pixel x in a given image.
     * 
     * <pre>f(x) = |x| </pre>
     * 
     * Parameters
     * ----------
     * source : Image
     *     The input image to be processed.
     * destination : Image
     *     The output image where results are written into.
     * 
     */
    public static ClearCLImageInterface absolute(ClearCLImageInterface source, ClearCLImageInterface destination) {
        Absolute.absolute(getCLIJ2(), source, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.LaplaceBox
    //----------------------------------------------------
    /**
     * Applies the Laplace operator (Box neighborhood) to an image.
     */
    public static ClearCLBuffer laplaceBox(ClearCLBuffer input, ClearCLBuffer destination) {
        LaplaceBox.laplaceBox(getCLIJ2(), input, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.BottomHatBox
    //----------------------------------------------------
    /**
     * Apply a bottom-hat filter for background subtraction to the input image.
     * 
     * Parameters
     * ----------
     * input : Image
     *     The input image where the background is subtracted from.
     * destination : Image
     *     The output image where results are written into.
     * radius_x : Image
     *     Radius of the background determination region in X.
     * radius_y : Image
     *     Radius of the background determination region in Y.
     * radius_z : Image
     *     Radius of the background determination region in Z.
     * 
     */
    public static ClearCLBuffer bottomHatBox(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5) {
        BottomHatBox.bottomHatBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.BottomHatSphere
    //----------------------------------------------------
    /**
     * Applies a bottom-hat filter for background subtraction to the input image.
     * 
     * Parameters
     * ----------
     * input : Image
     *     The input image where the background is subtracted from.
     * destination : Image
     *     The output image where results are written into.
     * radius_x : Image
     *     Radius of the background determination region in X.
     * radius_y : Image
     *     Radius of the background determination region in Y.
     * radius_z : Image
     *     Radius of the background determination region in Z.
     * 
     */
    public static ClearCLBuffer bottomHatSphere(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5) {
        BottomHatSphere.bottomHatSphere(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.ClosingBox
    //----------------------------------------------------
    /**
     * Apply a binary closing to the input image by calling n dilations and n erosions subsequenntly.
     */
    public static ClearCLBuffer closingBox(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        ClosingBox.closingBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.ClosingDiamond
    //----------------------------------------------------
    /**
     * Apply a binary closing to the input image by calling n dilations and n erosions subsequently.
     */
    public static ClearCLBuffer closingDiamond(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        ClosingDiamond.closingDiamond(getCLIJ2(), arg1, arg2, new Double (arg3).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.OpeningBox
    //----------------------------------------------------
    /**
     * Apply a binary opening to the input image by calling n erosions and n dilations subsequenntly.
     */
    public static ClearCLBuffer openingBox(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        OpeningBox.openingBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.OpeningDiamond
    //----------------------------------------------------
    /**
     * Apply a binary opening to the input image by calling n erosions and n dilations subsequenntly.
     */
    public static ClearCLBuffer openingDiamond(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        OpeningDiamond.openingDiamond(getCLIJ2(), arg1, arg2, new Double (arg3).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.MaximumXProjection
    //----------------------------------------------------
    /**
     * Determines the maximum intensity projection of an image along X.
     */
    public static ClearCLImageInterface maximumXProjection(ClearCLImageInterface source, ClearCLImageInterface destination_max) {
        MaximumXProjection.maximumXProjection(getCLIJ2(), source, destination_max);
        return destination_max;
    }


    // net.haesleinhuepf.clij2.plugins.MaximumYProjection
    //----------------------------------------------------
    /**
     * Determines the maximum intensity projection of an image along X.
     */
    public static ClearCLImageInterface maximumYProjection(ClearCLImageInterface source, ClearCLImageInterface destination_max) {
        MaximumYProjection.maximumYProjection(getCLIJ2(), source, destination_max);
        return destination_max;
    }


    // net.haesleinhuepf.clij2.plugins.MaximumZProjectionBounded
    //----------------------------------------------------
    /**
     * Determines the maximum intensity projection of an image along Z within a given z range.
     */
    public static ClearCLImageInterface maximumZProjectionBounded(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        MaximumZProjectionBounded.maximumZProjectionBounded(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.MinimumZProjectionBounded
    //----------------------------------------------------
    /**
     * Determines the minimum intensity projection of an image along Z within a given z range.
     */
    public static ClearCLImageInterface minimumZProjectionBounded(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        MinimumZProjectionBounded.minimumZProjectionBounded(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.MeanZProjectionBounded
    //----------------------------------------------------
    /**
     * Determines the mean average intensity projection of an image along Z within a given z range.
     */
    public static ClearCLImageInterface meanZProjectionBounded(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        MeanZProjectionBounded.meanZProjectionBounded(getCLIJ(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.NonzeroMaximumBox
    //----------------------------------------------------
    /**
     * Apply a maximum filter (box shape) to the input image. 
     * 
     * The radius is fixed to 1 and pixels with value 0 are ignored.
     * Note: Pixels with 0 value in the input image will not be overwritten in the output image.
     * Thus, the result image should be initialized by copying the original image in advance.
     */
    public static ClearCLImageInterface nonzeroMaximumBox(ClearCLImageInterface input, ClearCLImageInterface destination) {
        NonzeroMaximumBox.nonzeroMaximumBox(getCLIJ2(), input, destination);
        return destination;
    }

    /**
     * Apply a maximum filter (box shape) to the input image. 
     * 
     * The radius is fixed to 1 and pixels with value 0 are ignored.
     * Note: Pixels with 0 value in the input image will not be overwritten in the output image.
     * Thus, the result image should be initialized by copying the original image in advance.
     */
    public static ClearCLImageInterface nonzeroMaximumBox(ClearCLImageInterface arg1, ClearCLImageInterface arg2, ClearCLImageInterface arg3) {
        NonzeroMaximumBox.nonzeroMaximumBox(getCLIJ2(), arg1, arg2, arg3);
        return arg3;
    }

    /**
     * Apply a maximum filter (box shape) to the input image. 
     * 
     * The radius is fixed to 1 and pixels with value 0 are ignored.
     * Note: Pixels with 0 value in the input image will not be overwritten in the output image.
     * Thus, the result image should be initialized by copying the original image in advance.
     */
    public static ClearCLKernel nonzeroMaximumBox(ClearCLImageInterface arg1, ClearCLImageInterface arg2, ClearCLImageInterface arg3, ClearCLKernel arg4) {
        NonzeroMaximumBox.nonzeroMaximumBox(getCLIJ2(), arg1, arg2, arg3, arg4);
        return arg4;
    }


    // net.haesleinhuepf.clij2.plugins.NonzeroMinimumBox
    //----------------------------------------------------
    /**
     * Apply a minimum filter (box shape) to the input image. 
     * 
     * The radius is fixed to 1 and pixels with value 0 are ignored.
     * Note: Pixels with 0 value in the input image will not be overwritten in the output image.
     * Thus, the result image should be initialized by copying the original image in advance.
     */
    public static ClearCLImageInterface nonzeroMinimumBox(ClearCLImageInterface input, ClearCLImageInterface destination) {
        NonzeroMinimumBox.nonzeroMinimumBox(getCLIJ2(), input, destination);
        return destination;
    }

    /**
     * Apply a minimum filter (box shape) to the input image. 
     * 
     * The radius is fixed to 1 and pixels with value 0 are ignored.
     * Note: Pixels with 0 value in the input image will not be overwritten in the output image.
     * Thus, the result image should be initialized by copying the original image in advance.
     */
    public static ClearCLImageInterface nonzeroMinimumBox(ClearCLImageInterface arg1, ClearCLImageInterface arg2, ClearCLImageInterface arg3) {
        NonzeroMinimumBox.nonzeroMinimumBox(getCLIJ2(), arg1, arg2, arg3);
        return arg3;
    }

    /**
     * Apply a minimum filter (box shape) to the input image. 
     * 
     * The radius is fixed to 1 and pixels with value 0 are ignored.
     * Note: Pixels with 0 value in the input image will not be overwritten in the output image.
     * Thus, the result image should be initialized by copying the original image in advance.
     */
    public static ClearCLKernel nonzeroMinimumBox(ClearCLImageInterface arg1, ClearCLImageInterface arg2, ClearCLImageInterface arg3, ClearCLKernel arg4) {
        NonzeroMinimumBox.nonzeroMinimumBox(getCLIJ2(), arg1, arg2, arg3, arg4);
        return arg4;
    }


    // net.haesleinhuepf.clij2.plugins.MinimumZProjectionThresholdedBounded
    //----------------------------------------------------
    /**
     * Determines the minimum intensity projection of all pixels in an image above a given threshold along Z within a given z range.
     */
    public static ClearCLBuffer minimumZProjectionThresholdedBounded(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5) {
        MinimumZProjectionThresholdedBounded.minimumZProjectionThresholdedBounded(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.MeanOfPixelsAboveThreshold
    //----------------------------------------------------
    /**
     * Determines the mean intensity in a threshleded image. 
     * 
     * But only in pixels which are above a given threshold.
     */
    public static ClearCLBuffer meanOfPixelsAboveThreshold(ClearCLBuffer arg1, double arg2) {
        MeanOfPixelsAboveThreshold.meanOfPixelsAboveThreshold(getCLIJ2(), arg1, new Double (arg2).floatValue());
        return arg1;
    }


    // net.haesleinhuepf.clijx.gui.OrganiseWindows
    //----------------------------------------------------
    /**
     * Organises windows on screen.
     */
    public static boolean organiseWindows(double arg1, double arg2, double arg3, double arg4, double arg5, double arg6) {
        boolean result = OrganiseWindows.organiseWindows(getCLIJ(), new Double (arg1).intValue(), new Double (arg2).intValue(), new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue(), new Double (arg6).intValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.DistanceMatrixToMesh
    //----------------------------------------------------
    /**
     * Generates a mesh from a distance matric and a list of point coordinates.
     * 
     * Takes a pointlist with dimensions n*d with n point coordinates in d dimensions and a distance matrix of size n*n to draw lines from all points to points if the corresponding pixel in the distance matrix is smaller than a given distance threshold.
     */
    public static ClearCLBuffer distanceMatrixToMesh(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4) {
        DistanceMatrixToMesh.distanceMatrixToMesh(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).floatValue());
        return arg3;
    }


    // net.haesleinhuepf.clij2.plugins.PointIndexListToMesh
    //----------------------------------------------------
    /**
     * Meshes all points in a given point list which are indiced in a corresponding index list.
     */
    public static ClearCLBuffer pointIndexListToMesh(ClearCLBuffer pointlist, ClearCLBuffer indexlist, ClearCLBuffer mesh_destination) {
        PointIndexListToMesh.pointIndexListToMesh(getCLIJ2(), pointlist, indexlist, mesh_destination);
        return mesh_destination;
    }


    // net.haesleinhuepf.clij2.plugins.MinimumOctagon
    //----------------------------------------------------
    /**
     * Applies a minimum filter with kernel size 3x3 n times to an image iteratively. 
     * 
     * Odd iterations are done with box neighborhood, even iterations with a diamond. Thus, with n > 2, the filter shape is an octagon. The given number of iterations makes the filter result very similar to minimum sphere. Approximately:radius = iterations - 2
     */
    public static ClearCLBuffer minimumOctagon(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        MinimumOctagon.minimumOctagon(getCLIJ2(), arg1, arg2, new Double (arg3).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.MaximumOctagon
    //----------------------------------------------------
    /**
     * Applies a maximum filter with kernel size 3x3 n times to an image iteratively. 
     * 
     * Odd iterations are done with box neighborhood, even iterations with a diamond. 
     * Thus, with n > 2, the filter shape is an octagon. The given number of iterations makes the filter 
     * result very similar to minimum sphere. Approximately:radius = iterations - 2
     */
    public static ClearCLBuffer maximumOctagon(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        MaximumOctagon.maximumOctagon(getCLIJ2(), arg1, arg2, new Double (arg3).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clijx.plugins.TopHatOctagon
    //----------------------------------------------------
    /**
     * Applies a minimum filter with kernel size 3x3 n times to an image iteratively. 
     * 
     *  Odd iterations are done with box neighborhood, even iterations with a diamond. Thus, with n > 2, the filter shape is an octagon. The given number of iterations - 2 makes the filter result very similar to minimum sphere.
     */
    public static ClearCLBuffer topHatOctagon(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        TopHatOctagon.topHatOctagon(getCLIJx(), arg1, arg2, new Double (arg3).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.AddImages
    //----------------------------------------------------
    /**
     * Calculates the sum of pairs of pixels x and y of two images X and Y.
     * 
     * <pre>f(x, y) = x + y</pre>
     * 
     * Parameters
     * ----------
     * summand1 : Image
     *     The first input image to added.
     * summand2 : Image
     *     The second image to be added.
     * destination : Image
     *     The output image where results are written into.
     * 
     */
    public static ClearCLImageInterface addImages(ClearCLImageInterface summand1, ClearCLImageInterface summand2, ClearCLImageInterface destination) {
        AddImages.addImages(getCLIJ2(), summand1, summand2, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.AddImagesWeighted
    //----------------------------------------------------
    /**
     * Calculates the sum of pairs of pixels x and y from images X and Y weighted with factors a and b.
     * 
     * <pre>f(x, y, a, b) = x * a + y * b</pre>
     * 
     * Parameters
     * ----------
     * summand1 : Image
     *     The first input image to added.
     * summand2 : Image
     *     The second image to be added.
     * destination : Image
     *     The output image where results are written into.
     * factor1 : float
     *     The constant number which will be multiplied with each pixel of summand1 before adding it.
     * factor2 : float
     *     The constant number which will be multiplied with each pixel of summand2 before adding it.
     * 
     */
    public static ClearCLImageInterface addImagesWeighted(ClearCLImageInterface arg1, ClearCLImageInterface arg2, ClearCLImageInterface arg3, double arg4, double arg5) {
        AddImagesWeighted.addImagesWeighted(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).floatValue(), new Double (arg5).floatValue());
        return arg3;
    }


    // net.haesleinhuepf.clij2.plugins.SubtractImages
    //----------------------------------------------------
    /**
     * Subtracts one image X from another image Y pixel wise.
     * 
     * <pre>f(x, y) = x - y</pre>
     */
    public static ClearCLImageInterface subtractImages(ClearCLImageInterface subtrahend, ClearCLImageInterface minuend, ClearCLImageInterface destination) {
        SubtractImages.subtractImages(getCLIJ2(), subtrahend, minuend, destination);
        return destination;
    }


    // net.haesleinhuepf.clijx.plugins.ShowGlasbeyOnGrey
    //----------------------------------------------------
    /**
     * Visualises two 2D images as one RGB image. 
     * 
     * The first channel is shown in grey, the second with glasbey LUT.
     */
    public static ClearCLBuffer showGlasbeyOnGrey(ClearCLBuffer red, ClearCLBuffer labelling, String title) {
        ShowGlasbeyOnGrey.showGlasbeyOnGrey(getCLIJ(), red, labelling, title);
        return labelling;
    }


    // net.haesleinhuepf.clij2.plugins.AffineTransform2D
    //----------------------------------------------------
    /**
     * Applies an affine transform to a 2D image.
     * 
     * The transform describes how coordinates in the target image are transformed to coordinates in the source image.
     * This may appear unintuitive and will be changed in the next major release. The replacement 
     * affineTransform (currently part of CLIJx) will apply inverted transforms compared to this operation.
     * Individual transforms must be separated by spaces.
     * Parameters
     * ----------
     * source : Image
     *     The input image to be processed.
     * destination : Image
     *     The output image where results are written into.
     * transform : String
     *     A space-separated list of individual transforms. Syntrax see below.
     * 
     * Supported transforms:
     * 
     * * -center: translate the coordinate origin to the center of the image
     * * center: translate the coordinate origin back to the initial origin
     * * rotate=[angle]: rotate in X/Y plane (around Z-axis) by the given angle in degrees
     * * scale=[factor]: isotropic scaling according to given zoom factor
     * * scaleX=[factor]: scaling along X-axis according to given zoom factor
     * * scaleY=[factor]: scaling along Y-axis according to given zoom factor
     * * shearXY=[factor]: shearing along X-axis in XY plane according to given factor
     * * translateX=[distance]: translate along X-axis by distance given in pixels
     * * translateY=[distance]: translate along X-axis by distance given in pixels
     * 
     * Example transform:
     * transform = "-center scale=2 rotate=45 center";
     */
    public static ClearCLImageInterface affineTransform2D(ClearCLBuffer arg1, ClearCLImageInterface arg2, float[] arg3) {
        AffineTransform2D.affineTransform2D(getCLIJ2(), arg1, arg2, arg3);
        return arg2;
    }

    /**
     * Applies an affine transform to a 2D image.
     * 
     * The transform describes how coordinates in the target image are transformed to coordinates in the source image.
     * This may appear unintuitive and will be changed in the next major release. The replacement 
     * affineTransform (currently part of CLIJx) will apply inverted transforms compared to this operation.
     * Individual transforms must be separated by spaces.
     * Parameters
     * ----------
     * source : Image
     *     The input image to be processed.
     * destination : Image
     *     The output image where results are written into.
     * transform : String
     *     A space-separated list of individual transforms. Syntrax see below.
     * 
     * Supported transforms:
     * 
     * * -center: translate the coordinate origin to the center of the image
     * * center: translate the coordinate origin back to the initial origin
     * * rotate=[angle]: rotate in X/Y plane (around Z-axis) by the given angle in degrees
     * * scale=[factor]: isotropic scaling according to given zoom factor
     * * scaleX=[factor]: scaling along X-axis according to given zoom factor
     * * scaleY=[factor]: scaling along Y-axis according to given zoom factor
     * * shearXY=[factor]: shearing along X-axis in XY plane according to given factor
     * * translateX=[distance]: translate along X-axis by distance given in pixels
     * * translateY=[distance]: translate along X-axis by distance given in pixels
     * 
     * Example transform:
     * transform = "-center scale=2 rotate=45 center";
     */
    public static ClearCLImageInterface affineTransform2D(ClearCLBuffer source, ClearCLImageInterface destination, String transform) {
        AffineTransform2D.affineTransform2D(getCLIJ2(), source, destination, transform);
        return destination;
    }

    /**
     * Applies an affine transform to a 2D image.
     * 
     * The transform describes how coordinates in the target image are transformed to coordinates in the source image.
     * This may appear unintuitive and will be changed in the next major release. The replacement 
     * affineTransform (currently part of CLIJx) will apply inverted transforms compared to this operation.
     * Individual transforms must be separated by spaces.
     * Parameters
     * ----------
     * source : Image
     *     The input image to be processed.
     * destination : Image
     *     The output image where results are written into.
     * transform : String
     *     A space-separated list of individual transforms. Syntrax see below.
     * 
     * Supported transforms:
     * 
     * * -center: translate the coordinate origin to the center of the image
     * * center: translate the coordinate origin back to the initial origin
     * * rotate=[angle]: rotate in X/Y plane (around Z-axis) by the given angle in degrees
     * * scale=[factor]: isotropic scaling according to given zoom factor
     * * scaleX=[factor]: scaling along X-axis according to given zoom factor
     * * scaleY=[factor]: scaling along Y-axis according to given zoom factor
     * * shearXY=[factor]: shearing along X-axis in XY plane according to given factor
     * * translateX=[distance]: translate along X-axis by distance given in pixels
     * * translateY=[distance]: translate along X-axis by distance given in pixels
     * 
     * Example transform:
     * transform = "-center scale=2 rotate=45 center";
     */
    public static ClearCLImageInterface affineTransform2D(ClearCLBuffer arg1, ClearCLImageInterface arg2, net.imglib2.realtransform.AffineTransform2D arg3) {
        AffineTransform2D.affineTransform2D(getCLIJ2(), arg1, arg2, arg3);
        return arg2;
    }

    /**
     * Applies an affine transform to a 2D image.
     * 
     * The transform describes how coordinates in the target image are transformed to coordinates in the source image.
     * This may appear unintuitive and will be changed in the next major release. The replacement 
     * affineTransform (currently part of CLIJx) will apply inverted transforms compared to this operation.
     * Individual transforms must be separated by spaces.
     * Parameters
     * ----------
     * source : Image
     *     The input image to be processed.
     * destination : Image
     *     The output image where results are written into.
     * transform : String
     *     A space-separated list of individual transforms. Syntrax see below.
     * 
     * Supported transforms:
     * 
     * * -center: translate the coordinate origin to the center of the image
     * * center: translate the coordinate origin back to the initial origin
     * * rotate=[angle]: rotate in X/Y plane (around Z-axis) by the given angle in degrees
     * * scale=[factor]: isotropic scaling according to given zoom factor
     * * scaleX=[factor]: scaling along X-axis according to given zoom factor
     * * scaleY=[factor]: scaling along Y-axis according to given zoom factor
     * * shearXY=[factor]: shearing along X-axis in XY plane according to given factor
     * * translateX=[distance]: translate along X-axis by distance given in pixels
     * * translateY=[distance]: translate along X-axis by distance given in pixels
     * 
     * Example transform:
     * transform = "-center scale=2 rotate=45 center";
     */
    public static ClearCLImageInterface affineTransform2D(ClearCLImage arg1, ClearCLImageInterface arg2, float[] arg3) {
        AffineTransform2D.affineTransform2D(getCLIJ2(), arg1, arg2, arg3);
        return arg2;
    }

    /**
     * Applies an affine transform to a 2D image.
     * 
     * The transform describes how coordinates in the target image are transformed to coordinates in the source image.
     * This may appear unintuitive and will be changed in the next major release. The replacement 
     * affineTransform (currently part of CLIJx) will apply inverted transforms compared to this operation.
     * Individual transforms must be separated by spaces.
     * Parameters
     * ----------
     * source : Image
     *     The input image to be processed.
     * destination : Image
     *     The output image where results are written into.
     * transform : String
     *     A space-separated list of individual transforms. Syntrax see below.
     * 
     * Supported transforms:
     * 
     * * -center: translate the coordinate origin to the center of the image
     * * center: translate the coordinate origin back to the initial origin
     * * rotate=[angle]: rotate in X/Y plane (around Z-axis) by the given angle in degrees
     * * scale=[factor]: isotropic scaling according to given zoom factor
     * * scaleX=[factor]: scaling along X-axis according to given zoom factor
     * * scaleY=[factor]: scaling along Y-axis according to given zoom factor
     * * shearXY=[factor]: shearing along X-axis in XY plane according to given factor
     * * translateX=[distance]: translate along X-axis by distance given in pixels
     * * translateY=[distance]: translate along X-axis by distance given in pixels
     * 
     * Example transform:
     * transform = "-center scale=2 rotate=45 center";
     */
    public static ClearCLImageInterface affineTransform2D(ClearCLImage arg1, ClearCLImageInterface arg2, net.imglib2.realtransform.AffineTransform2D arg3) {
        AffineTransform2D.affineTransform2D(getCLIJ2(), arg1, arg2, arg3);
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.AffineTransform3D
    //----------------------------------------------------
    /**
     * Applies an affine transform to a 3D image.
     * 
     * The transform describes how coordinates in the target image are transformed to coordinates in the source image.
     * This may appear unintuitive and will be changed in the next major release. The replacement 
     * affineTransform (currently part of CLIJx) will apply inverted transforms compared to this operation.
     * Individual transforms must be separated by spaces.
     * Parameters
     * ----------
     * source : Image
     *     The input image to be processed.
     * destination : Image
     *     The output image where results are written into.
     * transform : String
     *     A space-separated list of individual transforms. Syntrax see below.
     * 
     * Supported transforms:
     * 
     * * -center: translate the coordinate origin to the center of the image
     * * center: translate the coordinate origin back to the initial origin
     * * rotate=[angle]: rotate in X/Y plane (around Z-axis) by the given angle in degrees
     * * rotateX=[angle]: rotate in Y/Z plane (around X-axis) by the given angle in degrees
     * * rotateY=[angle]: rotate in X/Z plane (around Y-axis) by the given angle in degrees
     * * rotateZ=[angle]: rotate in X/Y plane (around Z-axis) by the given angle in degrees
     * * scale=[factor]: isotropic scaling according to given zoom factor
     * * scaleX=[factor]: scaling along X-axis according to given zoom factor
     * * scaleY=[factor]: scaling along Y-axis according to given zoom factor
     * * scaleZ=[factor]: scaling along Z-axis according to given zoom factor
     * * shearXY=[factor]: shearing along X-axis in XY plane according to given factor
     * * shearXZ=[factor]: shearing along X-axis in XZ plane according to given factor
     * * shearYX=[factor]: shearing along Y-axis in XY plane according to given factor
     * * shearYZ=[factor]: shearing along Y-axis in YZ plane according to given factor
     * * shearZX=[factor]: shearing along Z-axis in XZ plane according to given factor
     * * shearZY=[factor]: shearing along Z-axis in YZ plane according to given factor
     * * translateX=[distance]: translate along X-axis by distance given in pixels
     * * translateY=[distance]: translate along X-axis by distance given in pixels
     * * translateZ=[distance]: translate along X-axis by distance given in pixels
     * 
     * Example transform:
     * transform = "-center scale=2 rotate=45 center";
     */
    public static ClearCLImageInterface affineTransform3D(ClearCLBuffer arg1, ClearCLImageInterface arg2, float[] arg3) {
        AffineTransform3D.affineTransform3D(getCLIJ2(), arg1, arg2, arg3);
        return arg2;
    }

    /**
     * Applies an affine transform to a 3D image.
     * 
     * The transform describes how coordinates in the target image are transformed to coordinates in the source image.
     * This may appear unintuitive and will be changed in the next major release. The replacement 
     * affineTransform (currently part of CLIJx) will apply inverted transforms compared to this operation.
     * Individual transforms must be separated by spaces.
     * Parameters
     * ----------
     * source : Image
     *     The input image to be processed.
     * destination : Image
     *     The output image where results are written into.
     * transform : String
     *     A space-separated list of individual transforms. Syntrax see below.
     * 
     * Supported transforms:
     * 
     * * -center: translate the coordinate origin to the center of the image
     * * center: translate the coordinate origin back to the initial origin
     * * rotate=[angle]: rotate in X/Y plane (around Z-axis) by the given angle in degrees
     * * rotateX=[angle]: rotate in Y/Z plane (around X-axis) by the given angle in degrees
     * * rotateY=[angle]: rotate in X/Z plane (around Y-axis) by the given angle in degrees
     * * rotateZ=[angle]: rotate in X/Y plane (around Z-axis) by the given angle in degrees
     * * scale=[factor]: isotropic scaling according to given zoom factor
     * * scaleX=[factor]: scaling along X-axis according to given zoom factor
     * * scaleY=[factor]: scaling along Y-axis according to given zoom factor
     * * scaleZ=[factor]: scaling along Z-axis according to given zoom factor
     * * shearXY=[factor]: shearing along X-axis in XY plane according to given factor
     * * shearXZ=[factor]: shearing along X-axis in XZ plane according to given factor
     * * shearYX=[factor]: shearing along Y-axis in XY plane according to given factor
     * * shearYZ=[factor]: shearing along Y-axis in YZ plane according to given factor
     * * shearZX=[factor]: shearing along Z-axis in XZ plane according to given factor
     * * shearZY=[factor]: shearing along Z-axis in YZ plane according to given factor
     * * translateX=[distance]: translate along X-axis by distance given in pixels
     * * translateY=[distance]: translate along X-axis by distance given in pixels
     * * translateZ=[distance]: translate along X-axis by distance given in pixels
     * 
     * Example transform:
     * transform = "-center scale=2 rotate=45 center";
     */
    public static ClearCLImageInterface affineTransform3D(ClearCLBuffer source, ClearCLImageInterface destination, String transform) {
        AffineTransform3D.affineTransform3D(getCLIJ2(), source, destination, transform);
        return destination;
    }

    /**
     * Applies an affine transform to a 3D image.
     * 
     * The transform describes how coordinates in the target image are transformed to coordinates in the source image.
     * This may appear unintuitive and will be changed in the next major release. The replacement 
     * affineTransform (currently part of CLIJx) will apply inverted transforms compared to this operation.
     * Individual transforms must be separated by spaces.
     * Parameters
     * ----------
     * source : Image
     *     The input image to be processed.
     * destination : Image
     *     The output image where results are written into.
     * transform : String
     *     A space-separated list of individual transforms. Syntrax see below.
     * 
     * Supported transforms:
     * 
     * * -center: translate the coordinate origin to the center of the image
     * * center: translate the coordinate origin back to the initial origin
     * * rotate=[angle]: rotate in X/Y plane (around Z-axis) by the given angle in degrees
     * * rotateX=[angle]: rotate in Y/Z plane (around X-axis) by the given angle in degrees
     * * rotateY=[angle]: rotate in X/Z plane (around Y-axis) by the given angle in degrees
     * * rotateZ=[angle]: rotate in X/Y plane (around Z-axis) by the given angle in degrees
     * * scale=[factor]: isotropic scaling according to given zoom factor
     * * scaleX=[factor]: scaling along X-axis according to given zoom factor
     * * scaleY=[factor]: scaling along Y-axis according to given zoom factor
     * * scaleZ=[factor]: scaling along Z-axis according to given zoom factor
     * * shearXY=[factor]: shearing along X-axis in XY plane according to given factor
     * * shearXZ=[factor]: shearing along X-axis in XZ plane according to given factor
     * * shearYX=[factor]: shearing along Y-axis in XY plane according to given factor
     * * shearYZ=[factor]: shearing along Y-axis in YZ plane according to given factor
     * * shearZX=[factor]: shearing along Z-axis in XZ plane according to given factor
     * * shearZY=[factor]: shearing along Z-axis in YZ plane according to given factor
     * * translateX=[distance]: translate along X-axis by distance given in pixels
     * * translateY=[distance]: translate along X-axis by distance given in pixels
     * * translateZ=[distance]: translate along X-axis by distance given in pixels
     * 
     * Example transform:
     * transform = "-center scale=2 rotate=45 center";
     */
    public static ClearCLImageInterface affineTransform3D(ClearCLBuffer arg1, ClearCLImageInterface arg2, net.imglib2.realtransform.AffineTransform3D arg3) {
        AffineTransform3D.affineTransform3D(getCLIJ2(), arg1, arg2, arg3);
        return arg2;
    }

    /**
     * Applies an affine transform to a 3D image.
     * 
     * The transform describes how coordinates in the target image are transformed to coordinates in the source image.
     * This may appear unintuitive and will be changed in the next major release. The replacement 
     * affineTransform (currently part of CLIJx) will apply inverted transforms compared to this operation.
     * Individual transforms must be separated by spaces.
     * Parameters
     * ----------
     * source : Image
     *     The input image to be processed.
     * destination : Image
     *     The output image where results are written into.
     * transform : String
     *     A space-separated list of individual transforms. Syntrax see below.
     * 
     * Supported transforms:
     * 
     * * -center: translate the coordinate origin to the center of the image
     * * center: translate the coordinate origin back to the initial origin
     * * rotate=[angle]: rotate in X/Y plane (around Z-axis) by the given angle in degrees
     * * rotateX=[angle]: rotate in Y/Z plane (around X-axis) by the given angle in degrees
     * * rotateY=[angle]: rotate in X/Z plane (around Y-axis) by the given angle in degrees
     * * rotateZ=[angle]: rotate in X/Y plane (around Z-axis) by the given angle in degrees
     * * scale=[factor]: isotropic scaling according to given zoom factor
     * * scaleX=[factor]: scaling along X-axis according to given zoom factor
     * * scaleY=[factor]: scaling along Y-axis according to given zoom factor
     * * scaleZ=[factor]: scaling along Z-axis according to given zoom factor
     * * shearXY=[factor]: shearing along X-axis in XY plane according to given factor
     * * shearXZ=[factor]: shearing along X-axis in XZ plane according to given factor
     * * shearYX=[factor]: shearing along Y-axis in XY plane according to given factor
     * * shearYZ=[factor]: shearing along Y-axis in YZ plane according to given factor
     * * shearZX=[factor]: shearing along Z-axis in XZ plane according to given factor
     * * shearZY=[factor]: shearing along Z-axis in YZ plane according to given factor
     * * translateX=[distance]: translate along X-axis by distance given in pixels
     * * translateY=[distance]: translate along X-axis by distance given in pixels
     * * translateZ=[distance]: translate along X-axis by distance given in pixels
     * 
     * Example transform:
     * transform = "-center scale=2 rotate=45 center";
     */
    public static ClearCLImageInterface affineTransform3D(ClearCLImage arg1, ClearCLImageInterface arg2, float[] arg3) {
        AffineTransform3D.affineTransform3D(getCLIJ2(), arg1, arg2, arg3);
        return arg2;
    }

    /**
     * Applies an affine transform to a 3D image.
     * 
     * The transform describes how coordinates in the target image are transformed to coordinates in the source image.
     * This may appear unintuitive and will be changed in the next major release. The replacement 
     * affineTransform (currently part of CLIJx) will apply inverted transforms compared to this operation.
     * Individual transforms must be separated by spaces.
     * Parameters
     * ----------
     * source : Image
     *     The input image to be processed.
     * destination : Image
     *     The output image where results are written into.
     * transform : String
     *     A space-separated list of individual transforms. Syntrax see below.
     * 
     * Supported transforms:
     * 
     * * -center: translate the coordinate origin to the center of the image
     * * center: translate the coordinate origin back to the initial origin
     * * rotate=[angle]: rotate in X/Y plane (around Z-axis) by the given angle in degrees
     * * rotateX=[angle]: rotate in Y/Z plane (around X-axis) by the given angle in degrees
     * * rotateY=[angle]: rotate in X/Z plane (around Y-axis) by the given angle in degrees
     * * rotateZ=[angle]: rotate in X/Y plane (around Z-axis) by the given angle in degrees
     * * scale=[factor]: isotropic scaling according to given zoom factor
     * * scaleX=[factor]: scaling along X-axis according to given zoom factor
     * * scaleY=[factor]: scaling along Y-axis according to given zoom factor
     * * scaleZ=[factor]: scaling along Z-axis according to given zoom factor
     * * shearXY=[factor]: shearing along X-axis in XY plane according to given factor
     * * shearXZ=[factor]: shearing along X-axis in XZ plane according to given factor
     * * shearYX=[factor]: shearing along Y-axis in XY plane according to given factor
     * * shearYZ=[factor]: shearing along Y-axis in YZ plane according to given factor
     * * shearZX=[factor]: shearing along Z-axis in XZ plane according to given factor
     * * shearZY=[factor]: shearing along Z-axis in YZ plane according to given factor
     * * translateX=[distance]: translate along X-axis by distance given in pixels
     * * translateY=[distance]: translate along X-axis by distance given in pixels
     * * translateZ=[distance]: translate along X-axis by distance given in pixels
     * 
     * Example transform:
     * transform = "-center scale=2 rotate=45 center";
     */
    public static ClearCLImageInterface affineTransform3D(ClearCLImage arg1, ClearCLImageInterface arg2, net.imglib2.realtransform.AffineTransform3D arg3) {
        AffineTransform3D.affineTransform3D(getCLIJ2(), arg1, arg2, arg3);
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.ApplyVectorField2D
    //----------------------------------------------------
    /**
     * Deforms an image according to distances provided in the given vector images.
     * 
     *  It is recommended to use 32-bit images for input, output and vector images.
     * 
     * Parameters
     * ----------
     * source : Image
     *     The input image to be processed.
     * vector_x : Image
     *     Pixels in this image describe the distance in X direction pixels should be shifted during warping.
     * vector_y : Image
     *     Pixels in this image describe the distance in Y direction pixels should be shifted during warping.
     * destination : Image
     *     The output image where results are written into.
     * 
     */
    public static ClearCLImageInterface applyVectorField(ClearCLImageInterface source, ClearCLImageInterface vector_x, ClearCLImageInterface vector_y, ClearCLImageInterface destination) {
        ApplyVectorField2D.applyVectorField(getCLIJ2(), source, vector_x, vector_y, destination);
        return destination;
    }

    /**
     * Deforms an image according to distances provided in the given vector images.
     * 
     *  It is recommended to use 32-bit images for input, output and vector images.
     * 
     * Parameters
     * ----------
     * source : Image
     *     The input image to be processed.
     * vector_x : Image
     *     Pixels in this image describe the distance in X direction pixels should be shifted during warping.
     * vector_y : Image
     *     Pixels in this image describe the distance in Y direction pixels should be shifted during warping.
     * destination : Image
     *     The output image where results are written into.
     * 
     */
    public static ClearCLImageInterface applyVectorField2D(ClearCLImageInterface source, ClearCLImageInterface vector_x, ClearCLImageInterface vector_y, ClearCLImageInterface destination) {
        ApplyVectorField2D.applyVectorField2D(getCLIJ2(), source, vector_x, vector_y, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.ApplyVectorField3D
    //----------------------------------------------------
    /**
     * Deforms an image according to distances provided in the given vector images.
     * 
     *  It is recommended to use 32-bit images for input, output and vector images.
     * 
     * Parameters
     * ----------
     * source : Image
     *     The input image to be processed.
     * vector_x : Image
     *     Pixels in this image describe the distance in X direction pixels should be shifted during warping.
     * vector_y : Image
     *     Pixels in this image describe the distance in Y direction pixels should be shifted during warping.
     * destination : Image
     *     The output image where results are written into.
     * 
     */
    public static ClearCLImageInterface applyVectorField(ClearCLImageInterface arg1, ClearCLImageInterface arg2, ClearCLImageInterface arg3, ClearCLImageInterface arg4, ClearCLImageInterface arg5) {
        ApplyVectorField3D.applyVectorField(getCLIJ2(), arg1, arg2, arg3, arg4, arg5);
        return arg5;
    }

    /**
     * Deforms an image stack according to distances provided in the given vector image stacks.
     * 
     * It is recommended to use 32-bit image stacks for input, output and vector image stacks.
     * 
     * Parameters
     * ----------
     * source : Image
     *     The input image to be processed.
     * vector_x : Image
     *     Pixels in this image describe the distance in X direction pixels should be shifted during warping.
     * vector_y : Image
     *     Pixels in this image describe the distance in Y direction pixels should be shifted during warping.
     * vector_z : Image
     *     Pixels in this image describe the distance in Z direction pixels should be shifted during warping.
     * destination : Image
     *     The output image where results are written into.
     * 
     */
    public static ClearCLImageInterface applyVectorField3D(ClearCLImageInterface source, ClearCLImageInterface vectorX, ClearCLImageInterface vectorY, ClearCLImageInterface vectorZ, ClearCLImageInterface destination) {
        ApplyVectorField3D.applyVectorField3D(getCLIJ2(), source, vectorX, vectorY, vectorZ, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.ArgMaximumZProjection
    //----------------------------------------------------
    /**
     * Determines the maximum projection of an image stack along Z.
     * 
     * Furthermore, another 2D image is generated with pixels containing the z-index where the maximum was found (zero based).
     */
    public static ClearCLImageInterface argMaximumZProjection(ClearCLImageInterface source, ClearCLImageInterface destination_max, ClearCLImageInterface destination_arg_max) {
        ArgMaximumZProjection.argMaximumZProjection(getCLIJ2(), source, destination_max, destination_arg_max);
        return destination_arg_max;
    }


    // net.haesleinhuepf.clij2.plugins.Histogram
    //----------------------------------------------------
    /**
     * Determines the histogram of a given image.
     * 
     * The histogram image is of dimensions number_of_bins/1/1; a 3D image with height=1 and depth=1. 
     * Histogram bins contain the number of pixels with intensity in this corresponding bin. 
     * The histogram bins are uniformly distributed between given minimum and maximum grey value intensity. 
     * If the flag determine_min_max is set, minimum and maximum intensity will be determined. 
     * When calling this operation many times, it is recommended to determine minimum and maximum intensity 
     * once at the beginning and handing over these values.
     */
    public static ClearCLBuffer histogram(ClearCLBuffer arg1, ClearCLBuffer arg2) {
        Histogram.histogram(getCLIJ2(), arg1, arg2);
        return arg2;
    }

    /**
     * Determines the histogram of a given image.
     * 
     * The histogram image is of dimensions number_of_bins/1/1; a 3D image with height=1 and depth=1. 
     * Histogram bins contain the number of pixels with intensity in this corresponding bin. 
     * The histogram bins are uniformly distributed between given minimum and maximum grey value intensity. 
     * If the flag determine_min_max is set, minimum and maximum intensity will be determined. 
     * When calling this operation many times, it is recommended to determine minimum and maximum intensity 
     * once at the beginning and handing over these values.
     */
    public static ClearCLBuffer histogram(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, boolean arg6) {
        Histogram.histogram(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), arg6);
        return arg2;
    }

    /**
     * Determines the histogram of a given image.
     * 
     * The histogram image is of dimensions number_of_bins/1/1; a 3D image with height=1 and depth=1. 
     * Histogram bins contain the number of pixels with intensity in this corresponding bin. 
     * The histogram bins are uniformly distributed between given minimum and maximum grey value intensity. 
     * If the flag determine_min_max is set, minimum and maximum intensity will be determined. 
     * When calling this operation many times, it is recommended to determine minimum and maximum intensity 
     * once at the beginning and handing over these values.
     */
    public static ClearCLBuffer histogram(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, boolean arg6, boolean arg7) {
        Histogram.histogram(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), arg6, arg7);
        return arg2;
    }

    /**
     * Determines the histogram of a given image.
     * 
     * The histogram image is of dimensions number_of_bins/1/1; a 3D image with height=1 and depth=1. 
     * Histogram bins contain the number of pixels with intensity in this corresponding bin. 
     * The histogram bins are uniformly distributed between given minimum and maximum grey value intensity. 
     * If the flag determine_min_max is set, minimum and maximum intensity will be determined. 
     * When calling this operation many times, it is recommended to determine minimum and maximum intensity 
     * once at the beginning and handing over these values.
     */
    public static ClearCLBuffer histogram(ClearCLBuffer arg1, double arg2, double arg3, double arg4) {
        Histogram.histogram(getCLIJ2(), arg1, new Double (arg2).floatValue(), new Double (arg3).floatValue(), new Double (arg4).intValue());
        return arg1;
    }

    /**
     * Determines the histogram of a given image.
     * 
     * The histogram image is of dimensions number_of_bins/1/1; a 3D image with height=1 and depth=1. 
     * Histogram bins contain the number of pixels with intensity in this corresponding bin. 
     * The histogram bins are uniformly distributed between given minimum and maximum grey value intensity. 
     * If the flag determine_min_max is set, minimum and maximum intensity will be determined. 
     * When calling this operation many times, it is recommended to determine minimum and maximum intensity 
     * once at the beginning and handing over these values.
     */
    public static ClearCLBuffer histogram(ClearCLBuffer arg1) {
        Histogram.histogram(getCLIJ2(), arg1);
        return arg1;
    }


    // net.haesleinhuepf.clij2.plugins.AutomaticThreshold
    //----------------------------------------------------
    /**
     * The automatic thresholder utilizes the threshold methods from ImageJ on a histogram determined on 
     * the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.
     * 
     *  Enter one 
     * of these methods in the method text field:
     * [Default, Huang, Intermodes, IsoData, IJ_IsoData, Li, MaxEntropy, Mean, MinError, Minimum, Moments, Otsu, Percentile, RenyiEntropy, Shanbhag, Triangle, Yen]
     */
    public static ClearCLBuffer automaticThreshold(ClearCLBuffer input, ClearCLBuffer destination, String method) {
        AutomaticThreshold.automaticThreshold(getCLIJ2(), input, destination, method);
        return destination;
    }

    /**
     * The automatic thresholder utilizes the threshold methods from ImageJ on a histogram determined on 
     * the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.
     * 
     *  Enter one 
     * of these methods in the method text field:
     * [Default, Huang, Intermodes, IsoData, IJ_IsoData, Li, MaxEntropy, Mean, MinError, Minimum, Moments, Otsu, Percentile, RenyiEntropy, Shanbhag, Triangle, Yen]
     */
    public static ClearCLBuffer automaticThreshold(ClearCLBuffer arg1, ClearCLBuffer arg2, String arg3, double arg4, double arg5, double arg6) {
        AutomaticThreshold.automaticThreshold(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).floatValue(), new Double (arg5).floatValue(), new Double (arg6).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.Threshold
    //----------------------------------------------------
    /**
     * Computes a binary image with pixel values 0 and 1. 
     * 
     * All pixel values x of a given input image with 
     * value larger or equal to a given threshold t will be set to 1.
     * 
     * f(x,t) = (1 if (x >= t); (0 otherwise))
     * 
     * This plugin is comparable to setting a raw threshold in ImageJ and using the 'Convert to Mask' menu.
     */
    public static ClearCLImageInterface threshold(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3) {
        Threshold.threshold(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.BinaryOr
    //----------------------------------------------------
    /**
     * Computes a binary image (containing pixel values 0 and 1) from two images X and Y by connecting pairs of
     * pixels x and y with the binary OR operator |.
     * 
     * All pixel values except 0 in the input images are interpreted as 1.<pre>f(x, y) = x | y</pre>
     * 
     * Parameters
     * ----------
     * operand1 : Image
     *     The first binary input image to be processed.
     * operand2 : Image
     *     The second binary input image to be processed.
     * destination : Image
     *     The output image where results are written into.
     * 
     */
    public static ClearCLImageInterface binaryOr(ClearCLImageInterface operand1, ClearCLImageInterface operand2, ClearCLImageInterface destination) {
        BinaryOr.binaryOr(getCLIJ2(), operand1, operand2, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.BinaryAnd
    //----------------------------------------------------
    /**
     * Computes a binary image (containing pixel values 0 and 1) from two images X and Y by connecting pairs of
     * pixels x and y with the binary AND operator &.
     * All pixel values except 0 in the input images are interpreted as 1.
     * 
     * <pre>f(x, y) = x & y</pre>
     * 
     * Parameters
     * ----------
     * operand1 : Image
     *     The first binary input image to be processed.
     * operand2 : Image
     *     The second binary input image to be processed.
     * destination : Image
     *     The output image where results are written into.
     * 
     */
    public static ClearCLImageInterface binaryAnd(ClearCLImageInterface operand1, ClearCLImageInterface operand2, ClearCLImageInterface destination) {
        BinaryAnd.binaryAnd(getCLIJ2(), operand1, operand2, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.BinaryXOr
    //----------------------------------------------------
    /**
     * Computes a binary image (containing pixel values 0 and 1) from two images X and Y by connecting pairs of
     * pixels x and y with the binary operators AND &, OR | and NOT ! implementing the XOR operator.
     * 
     * All pixel values except 0 in the input images are interpreted as 1.
     * 
     * <pre>f(x, y) = (x & !y) | (!x & y)</pre>
     * 
     * Parameters
     * ----------
     * operand1 : Image
     *     The first binary input image to be processed.
     * operand2 : Image
     *     The second binary input image to be processed.
     * destination : Image
     *     The output image where results are written into.
     * 
     */
    public static ClearCLImageInterface binaryXOr(ClearCLImageInterface operand1, ClearCLImageInterface operand2, ClearCLImageInterface destination) {
        BinaryXOr.binaryXOr(getCLIJ2(), operand1, operand2, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.BinaryNot
    //----------------------------------------------------
    /**
     * Computes a binary image (containing pixel values 0 and 1) from an image X by negating its pixel values
     * x using the binary NOT operator !
     * 
     * All pixel values except 0 in the input image are interpreted as 1.
     * 
     * <pre>f(x) = !x</pre>
     * 
     * Parameters
     * ----------
     * source : Image
     *     The binary input image to be inverted.
     * destination : Image
     *     The output image where results are written into.
     * 
     */
    public static ClearCLImageInterface binaryNot(ClearCLImageInterface source, ClearCLImageInterface destination) {
        BinaryNot.binaryNot(getCLIJ2(), source, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.ErodeSphere
    //----------------------------------------------------
    /**
     * Computes a binary image with pixel values 0 and 1 containing the binary erosion of a given input image. 
     * 
     * The erosion takes the von-Neumann-neighborhood (4 pixels in 2D and 6 pixels in 3d) into account.
     * The pixels in the input image with pixel value not equal to 0 will be interpreted as 1.
     */
    public static ClearCLImageInterface erodeSphere(ClearCLImageInterface source, ClearCLImageInterface destination) {
        ErodeSphere.erodeSphere(getCLIJ2(), source, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.ErodeBox
    //----------------------------------------------------
    /**
     * Computes a binary image with pixel values 0 and 1 containing the binary erosion of a given input image. 
     * 
     * The erosion takes the Moore-neighborhood (8 pixels in 2D and 26 pixels in 3d) into account.
     * The pixels in the input image with pixel value not equal to 0 will be interpreted as 1.
     * 
     * This method is comparable to the 'Erode' menu in ImageJ in case it is applied to a 2D image. The only
     * difference is that the output image contains values 0 and 1 instead of 0 and 255.
     */
    public static ClearCLImageInterface erodeBox(ClearCLImageInterface source, ClearCLImageInterface destination) {
        ErodeBox.erodeBox(getCLIJ2(), source, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.ErodeSphereSliceBySlice
    //----------------------------------------------------
    /**
     * Computes a binary image with pixel values 0 and 1 containing the binary erosion of a given input image. 
     * 
     * The erosion takes the von-Neumann-neighborhood (4 pixels in 2D and 6 pixels in 3d) into account.
     * The pixels in the input image with pixel value not equal to 0 will be interpreted as 1.
     * 
     * This filter is applied slice by slice in 2D.
     */
    public static ClearCLImageInterface erodeSphereSliceBySlice(ClearCLImageInterface source, ClearCLImageInterface destination) {
        ErodeSphereSliceBySlice.erodeSphereSliceBySlice(getCLIJ2(), source, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.ErodeBoxSliceBySlice
    //----------------------------------------------------
    /**
     * Computes a binary image with pixel values 0 and 1 containing the binary erosion of a given input image. 
     * 
     * The erosion takes the Moore-neighborhood (8 pixels in 2D and 26 pixels in 3d) into account.
     * The pixels in the input image with pixel value not equal to 0 will be interpreted as 1.
     * 
     * This method is comparable to the 'Erode' menu in ImageJ in case it is applied to a 2D image. The only
     * difference is that the output image contains values 0 and 1 instead of 0 and 255.
     * 
     * This filter is applied slice by slice in 2D.
     */
    public static ClearCLImageInterface erodeBoxSliceBySlice(ClearCLImageInterface source, ClearCLImageInterface destination) {
        ErodeBoxSliceBySlice.erodeBoxSliceBySlice(getCLIJ2(), source, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.DilateSphere
    //----------------------------------------------------
    /**
     * Computes a binary image with pixel values 0 and 1 containing the binary dilation of a given input image.
     * 
     * The dilation takes the von-Neumann-neighborhood (4 pixels in 2D and 6 pixels in 3d) into account.
     * The pixels in the input image with pixel value not equal to 0 will be interpreted as 1.
     */
    public static ClearCLImageInterface dilateSphere(ClearCLImageInterface source, ClearCLImageInterface destination) {
        DilateSphere.dilateSphere(getCLIJ2(), source, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.DilateBox
    //----------------------------------------------------
    /**
     * Computes a binary image with pixel values 0 and 1 containing the binary dilation of a given input image.
     * 
     * The dilation takes the Moore-neighborhood (8 pixels in 2D and 26 pixels in 3d) into account.
     * The pixels in the input image with pixel value not equal to 0 will be interpreted as 1.
     * 
     * This method is comparable to the 'Dilate' menu in ImageJ in case it is applied to a 2D image. The only
     * difference is that the output image contains values 0 and 1 instead of 0 and 255.
     */
    public static ClearCLImageInterface dilateBox(ClearCLImageInterface source, ClearCLImageInterface destination) {
        DilateBox.dilateBox(getCLIJ2(), source, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.DilateSphereSliceBySlice
    //----------------------------------------------------
    /**
     * Computes a binary image with pixel values 0 and 1 containing the binary dilation of a given input image.
     * 
     * The dilation takes the von-Neumann-neighborhood (4 pixels in 2D and 6 pixels in 3d) into account.
     * The pixels in the input image with pixel value not equal to 0 will be interpreted as 1.
     * 
     * This filter is applied slice by slice in 2D.
     */
    public static ClearCLImageInterface dilateSphereSliceBySlice(ClearCLImageInterface source, ClearCLImageInterface destination) {
        DilateSphereSliceBySlice.dilateSphereSliceBySlice(getCLIJ2(), source, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.DilateBoxSliceBySlice
    //----------------------------------------------------
    /**
     * Computes a binary image with pixel values 0 and 1 containing the binary dilation of a given input image.
     * 
     * The dilation takes the Moore-neighborhood (8 pixels in 2D and 26 pixels in 3d) into account.
     * The pixels in the input image with pixel value not equal to 0 will be interpreted as 1.
     * 
     * This method is comparable to the 'Dilate' menu in ImageJ in case it is applied to a 2D image. The only
     * difference is that the output image contains values 0 and 1 instead of 0 and 255.
     * 
     * This filter is applied slice by slice in 2D.
     */
    public static ClearCLImageInterface dilateBoxSliceBySlice(ClearCLImageInterface source, ClearCLImageInterface destination) {
        DilateBoxSliceBySlice.dilateBoxSliceBySlice(getCLIJ2(), source, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.Copy
    //----------------------------------------------------
    /**
     * Copies an image.
     * 
     * <pre>f(x) = x</pre>
     */
    public static ClearCLImageInterface copy(ClearCLImageInterface source, ClearCLImageInterface destination) {
        Copy.copy(getCLIJ2(), source, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.CopySlice
    //----------------------------------------------------
    /**
     * This method has two purposes: 
     * It copies a 2D image to a given slice z position in a 3D image stack or 
     * It copies a given slice at position z in an image stack to a 2D image.
     * 
     * The first case is only available via ImageJ macro. If you are using it, it is recommended that the 
     * target 3D image already pre-exists in GPU memory before calling this method. Otherwise, CLIJ create 
     * the image stack with z planes.
     */
    public static ClearCLImageInterface copySlice(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3) {
        CopySlice.copySlice(getCLIJ2(), arg1, arg2, new Double (arg3).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.Crop2D
    //----------------------------------------------------
    /**
     * Crops a given rectangle out of a given image. 
     * 
     * Note: If the destination image pre-exists already, it will be overwritten and keep it's dimensions.
     * 
     * Parameters
     * ----------
     * source : Image
     *     The image where a part will be cropped out.
     * destination : Image
     *     The cropped image will be stored in this variable.
     * start_x : Number
     *     The horizontal position of the region to crop in the source image.
     * start_y : Number
     *     The vertical position of the region to crop in the source image.
     * width : Number
     *     The width of the region to crop in the source image.
     * height : Number
     *     The height of the region to crop in the source image.
     * 
     */
    public static ClearCLImageInterface crop(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        Crop2D.crop(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return arg2;
    }

    /**
     * Crops a given rectangle out of a given image. 
     * 
     * Note: If the destination image pre-exists already, it will be overwritten and keep it's dimensions.
     * 
     * Parameters
     * ----------
     * source : Image
     *     The image where a part will be cropped out.
     * destination : Image
     *     The cropped image will be stored in this variable.
     * start_x : Number
     *     The horizontal position of the region to crop in the source image.
     * start_y : Number
     *     The vertical position of the region to crop in the source image.
     * width : Number
     *     The width of the region to crop in the source image.
     * height : Number
     *     The height of the region to crop in the source image.
     * 
     */
    public static ClearCLImageInterface crop2D(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        Crop2D.crop2D(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.Crop3D
    //----------------------------------------------------
    /**
     * Crops a given rectangle out of a given image. 
     * 
     * Note: If the destination image pre-exists already, it will be overwritten and keep it's dimensions.
     * 
     * Parameters
     * ----------
     * source : Image
     *     The image where a part will be cropped out.
     * destination : Image
     *     The cropped image will be stored in this variable.
     * start_x : Number
     *     The horizontal position of the region to crop in the source image.
     * start_y : Number
     *     The vertical position of the region to crop in the source image.
     * width : Number
     *     The width of the region to crop in the source image.
     * height : Number
     *     The height of the region to crop in the source image.
     * 
     */
    public static ClearCLImageInterface crop(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        Crop3D.crop(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return arg2;
    }

    /**
     * Crops a given sub-stack out of a given image stack. 
     * 
     * Note: If the destination image pre-exists already, it will be overwritten and keep it's dimensions.
     * 
     * Parameters
     * ----------
     * source : Image
     *     The image where a part will be cropped out.
     * destination : Image
     *     The cropped image will be stored in this variable.
     * start_x : Number
     *     The horizontal position of the region to crop in the source image.
     * start_y : Number
     *     The vertical position of the region to crop in the source image.
     * start_z : Number
     *     The slice position of the region to crop in the source image. Slices are counted 0-based; the first slice is z=0.
     * width : Number
     *     The width of the region to crop in the source image.
     * height : Number
     *     The height of the region to crop in the source image.
     * depth : Number
     *     The depth of the region to crop in the source image.
     * 
     */
    public static ClearCLImageInterface crop3D(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        Crop3D.crop3D(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.Set
    //----------------------------------------------------
    /**
     * Sets all pixel values x of a given image X to a constant value v.
     * 
     * <pre>f(x) = v</pre>
     */
    public static ClearCLImageInterface set(ClearCLImageInterface arg1, double arg2) {
        Set.set(getCLIJ2(), arg1, new Double (arg2).floatValue());
        return arg1;
    }


    // net.haesleinhuepf.clij2.plugins.Flip2D
    //----------------------------------------------------
    /**
     * Flips an image in X and/or Y direction depending on if flip_x and/or flip_y are set to true or false.
     */
    public static ClearCLImageInterface flip(ClearCLImageInterface arg1, ClearCLImageInterface arg2, boolean arg3, boolean arg4) {
        Flip2D.flip(getCLIJ2(), arg1, arg2, arg3, arg4);
        return arg2;
    }

    /**
     * Flips an image in X and/or Y direction depending on if flip_x and/or flip_y are set to true or false.
     */
    public static ClearCLImageInterface flip2D(ClearCLImageInterface arg1, ClearCLImageInterface arg2, boolean arg3, boolean arg4) {
        Flip2D.flip2D(getCLIJ2(), arg1, arg2, arg3, arg4);
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.Flip3D
    //----------------------------------------------------
    /**
     * Flips an image in X and/or Y direction depending on if flip_x and/or flip_y are set to true or false.
     */
    public static ClearCLImageInterface flip(ClearCLImageInterface arg1, ClearCLImageInterface arg2, boolean arg3, boolean arg4, boolean arg5) {
        Flip3D.flip(getCLIJ2(), arg1, arg2, arg3, arg4, arg5);
        return arg2;
    }

    /**
     * Flips an image in X, Y and/or Z direction depending on if flip_x, flip_y and/or flip_z are set to true or false.
     */
    public static ClearCLImageInterface flip3D(ClearCLImageInterface arg1, ClearCLImageInterface arg2, boolean arg3, boolean arg4, boolean arg5) {
        Flip3D.flip3D(getCLIJ2(), arg1, arg2, arg3, arg4, arg5);
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.RotateCounterClockwise
    //----------------------------------------------------
    /**
     * Rotates a given input image by 90 degrees counter-clockwise. 
     * 
     * For that, X and Y axis of an image stack
     * are flipped. This operation is similar to ImageJs 'Reslice [/]' method but offers less flexibility 
     * such as interpolation.
     */
    public static ClearCLImageInterface rotateCounterClockwise(ClearCLImageInterface source, ClearCLImageInterface destination) {
        RotateCounterClockwise.rotateCounterClockwise(getCLIJ2(), source, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.RotateClockwise
    //----------------------------------------------------
    /**
     * Rotates a given input image by 90 degrees clockwise. 
     * 
     * For that, X and Y axis of an image stack
     * are flipped. This operation is similar to ImageJs 'Reslice [/]' method but offers less flexibility 
     * such as interpolation.
     */
    public static ClearCLImageInterface rotateClockwise(ClearCLImageInterface source, ClearCLImageInterface destination) {
        RotateClockwise.rotateClockwise(getCLIJ2(), source, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.Mask
    //----------------------------------------------------
    /**
     * Computes a masked image by applying a binary mask to an image. 
     * 
     * All pixel values x of image X will be copied
     * to the destination image in case pixel value m at the same position in the mask image is not equal to 
     * zero.
     * 
     * <pre>f(x,m) = (x if (m != 0); (0 otherwise))</pre>
     */
    public static ClearCLImageInterface mask(ClearCLImageInterface source, ClearCLImageInterface mask, ClearCLImageInterface destination) {
        Mask.mask(getCLIJ2(), source, mask, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.MaskStackWithPlane
    //----------------------------------------------------
    /**
     * Computes a masked image by applying a binary 2D mask to an image stack. 
     * 
     * All pixel values x of image X will be copied
     * to the destination image in case pixel value m at the same spatial position in the mask image is not equal to 
     * zero.
     * 
     * <pre>f(x,m) = (x if (m != 0); (0 otherwise))</pre>
     */
    public static ClearCLImageInterface maskStackWithPlane(ClearCLImageInterface source, ClearCLImageInterface mask, ClearCLImageInterface destination) {
        MaskStackWithPlane.maskStackWithPlane(getCLIJ2(), source, mask, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.MaximumZProjection
    //----------------------------------------------------
    /**
     * Determines the maximum intensity projection of an image along Z.
     */
    public static ClearCLImageInterface maximumZProjection(ClearCLImageInterface source, ClearCLImageInterface destination_max) {
        MaximumZProjection.maximumZProjection(getCLIJ2(), source, destination_max);
        return destination_max;
    }


    // net.haesleinhuepf.clij2.plugins.MeanZProjection
    //----------------------------------------------------
    /**
     * Determines the mean average intensity projection of an image along Z.
     */
    public static ClearCLImageInterface meanZProjection(ClearCLImageInterface source, ClearCLImageInterface destination) {
        MeanZProjection.meanZProjection(getCLIJ2(), source, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.MinimumZProjection
    //----------------------------------------------------
    /**
     * Determines the minimum intensity projection of an image along Z.
     */
    public static ClearCLImageInterface minimumZProjection(ClearCLImageInterface source, ClearCLImageInterface destination_min) {
        MinimumZProjection.minimumZProjection(getCLIJ2(), source, destination_min);
        return destination_min;
    }


    // net.haesleinhuepf.clij2.plugins.Power
    //----------------------------------------------------
    /**
     * Computes all pixels value x to the power of a given exponent a.
     * 
     * <pre>f(x, a) = x ^ a</pre>
     */
    public static ClearCLImageInterface power(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3) {
        Power.power(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.DivideImages
    //----------------------------------------------------
    /**
     * Divides two images X and Y by each other pixel wise. 
     * 
     * <pre>f(x, y) = x / y</pre>
     */
    public static ClearCLImageInterface divideImages(ClearCLImageInterface divident, ClearCLImageInterface divisor, ClearCLImageInterface destination) {
        DivideImages.divideImages(getCLIJ2(), divident, divisor, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.MaximumImages
    //----------------------------------------------------
    /**
     * Computes the maximum of a pair of pixel values x, y from two given images X and Y. 
     * 
     * <pre>f(x, y) = max(x, y)</pre>
     */
    public static ClearCLImageInterface maximumImages(ClearCLImageInterface source1, ClearCLImageInterface source2, ClearCLImageInterface destination) {
        MaximumImages.maximumImages(getCLIJ2(), source1, source2, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.MaximumImageAndScalar
    //----------------------------------------------------
    /**
     * Computes the maximum of a constant scalar s and each pixel value x in a given image X. 
     * 
     * <pre>f(x, s) = max(x, s)</pre>
     */
    public static ClearCLImageInterface maximumImageAndScalar(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3) {
        MaximumImageAndScalar.maximumImageAndScalar(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.MinimumImages
    //----------------------------------------------------
    /**
     * Computes the minimum of a pair of pixel values x, y from two given images X and Y.
     * 
     * <pre>f(x, y) = min(x, y)</pre>
     */
    public static ClearCLImageInterface minimumImages(ClearCLImageInterface source1, ClearCLImageInterface source2, ClearCLImageInterface destination) {
        MinimumImages.minimumImages(getCLIJ2(), source1, source2, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.MinimumImageAndScalar
    //----------------------------------------------------
    /**
     * Computes the minimum of a constant scalar s and each pixel value x in a given image X.
     * 
     * <pre>f(x, s) = min(x, s)</pre>
     */
    public static ClearCLImageInterface minimumImageAndScalar(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3) {
        MinimumImageAndScalar.minimumImageAndScalar(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.MultiplyImageAndScalar
    //----------------------------------------------------
    /**
     * Multiplies all pixels value x in a given image X with a constant scalar s.
     * 
     * <pre>f(x, s) = x * s</pre>
     * 
     * Parameters
     * ----------
     * source : Image
     *     The input image to be multiplied with a constant.
     * destination : Image
     *     The output image where results are written into.
     * scalar : float
     *     The number with which every pixel will be multiplied with.
     * 
     */
    public static ClearCLImageInterface multiplyImageAndScalar(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3) {
        MultiplyImageAndScalar.multiplyImageAndScalar(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.MultiplyStackWithPlane
    //----------------------------------------------------
    /**
     * Multiplies all pairs of pixel values x and y from an image stack X and a 2D image Y. 
     * 
     * x and y are at 
     * the same spatial position within a plane.
     * 
     * <pre>f(x, y) = x * y</pre>
     */
    public static ClearCLImageInterface multiplyStackWithPlane(ClearCLImageInterface sourceStack, ClearCLImageInterface sourcePlane, ClearCLImageInterface destination) {
        MultiplyStackWithPlane.multiplyStackWithPlane(getCLIJ2(), sourceStack, sourcePlane, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.CountNonZeroPixels2DSphere
    //----------------------------------------------------
    /**
     * Counts non-zero pixels in a sphere around every pixel. 
     * 
     * Put the number in the result image.
     */
    public static ClearCLBuffer countNonZeroPixels2DSphere(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4) {
        CountNonZeroPixels2DSphere.countNonZeroPixels2DSphere(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.CountNonZeroPixelsSliceBySliceSphere
    //----------------------------------------------------
    /**
     * Counts non-zero pixels in a sphere around every pixel slice by slice in a stack. 
     * 
     *  It puts the resulting number in the destination image stack.
     */
    public static ClearCLBuffer countNonZeroPixelsSliceBySliceSphere(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4) {
        CountNonZeroPixelsSliceBySliceSphere.countNonZeroPixelsSliceBySliceSphere(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.CountNonZeroVoxels3DSphere
    //----------------------------------------------------
    /**
     * Counts non-zero voxels in a sphere around every voxel. 
     * 
     * Put the number in the result image.
     */
    public static ClearCLBuffer countNonZeroVoxels3DSphere(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5) {
        CountNonZeroVoxels3DSphere.countNonZeroVoxels3DSphere(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.SumZProjection
    //----------------------------------------------------
    /**
     * Determines the sum intensity projection of an image along Z.
     */
    public static ClearCLImageInterface sumZProjection(ClearCLImageInterface source, ClearCLImageInterface destination_sum) {
        SumZProjection.sumZProjection(getCLIJ2(), source, destination_sum);
        return destination_sum;
    }


    // net.haesleinhuepf.clij2.plugins.SumOfAllPixels
    //----------------------------------------------------
    /**
     * Determines the sum of all pixels in a given image. 
     * 
     * It will be stored in a new row of ImageJs
     * Results table in the column 'Sum'.
     * 
     * Parameters
     * ----------
     * source : Image
     *     The image of which all pixels or voxels will be summed.
     * 
     */
    public static ClearCLImageInterface sumOfAllPixels(ClearCLImageInterface source) {
        SumOfAllPixels.sumOfAllPixels(getCLIJ2(), source);
        return source;
    }


    // net.haesleinhuepf.clij2.plugins.CenterOfMass
    //----------------------------------------------------
    /**
     * Determines the center of mass of an image or image stack. 
     * 
     * It writes the result in the results table
     * in the columns MassX, MassY and MassZ.
     */
    public static ClearCLBuffer centerOfMass(ClearCLBuffer source) {
        CenterOfMass.centerOfMass(getCLIJ2(), source);
        return source;
    }


    // net.haesleinhuepf.clij2.plugins.Invert
    //----------------------------------------------------
    /**
     * Computes the negative value of all pixels in a given image. 
     * 
     * It is recommended to convert images to 
     * 32-bit float before applying this operation.
     * 
     * <pre>f(x) = - x</pre>
     * 
     * For binary images, use binaryNot.
     */
    public static ClearCLImageInterface invert(ClearCLImageInterface source, ClearCLImageInterface destination) {
        Invert.invert(getCLIJ2(), source, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.Downsample2D
    //----------------------------------------------------

    // net.haesleinhuepf.clij2.plugins.Downsample3D
    //----------------------------------------------------

    // net.haesleinhuepf.clij2.plugins.DownsampleSliceBySliceHalfMedian
    //----------------------------------------------------
    /**
     * Scales an image using scaling factors 0.5 for X and Y dimensions. The Z dimension stays untouched. 
     * 
     * Thus, each slice is processed separately.
     * The median method is applied. Thus, each pixel value in the destination image equals to the median of
     * four corresponding pixels in the source image.
     */
    public static ClearCLImageInterface downsampleSliceBySliceHalfMedian(ClearCLImageInterface source, ClearCLImageInterface destination) {
        DownsampleSliceBySliceHalfMedian.downsampleSliceBySliceHalfMedian(getCLIJ2(), source, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.LocalThreshold
    //----------------------------------------------------
    /**
     * Computes a binary image with pixel values 0 and 1 depending on if a pixel value x in image X 
     * was above of equal to the pixel value m in mask image M.
     * 
     * <pre>f(x) = (1 if (x >=  m)); (0 otherwise)</pre>
     */
    public static ClearCLImageInterface localThreshold(ClearCLImageInterface source, ClearCLImageInterface localThreshold, ClearCLImageInterface destination) {
        LocalThreshold.localThreshold(getCLIJ2(), source, localThreshold, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.GradientX
    //----------------------------------------------------
    /**
     * Computes the gradient of gray values along X. 
     * 
     * Assuming a, b and c are three adjacent
     *  pixels in X direction. In the target image will be saved as: <pre>b' = c - a;</pre>
     */
    public static ClearCLBuffer gradientX(ClearCLBuffer source, ClearCLBuffer destination) {
        GradientX.gradientX(getCLIJ2(), source, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.GradientY
    //----------------------------------------------------
    /**
     * Computes the gradient of gray values along Y. 
     * 
     * Assuming a, b and c are three adjacent
     *  pixels in Y direction. In the target image will be saved as: <pre>b' = c - a;</pre>
     */
    public static ClearCLBuffer gradientY(ClearCLBuffer source, ClearCLBuffer destination) {
        GradientY.gradientY(getCLIJ2(), source, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.GradientZ
    //----------------------------------------------------
    /**
     * Computes the gradient of gray values along Z. 
     * 
     * Assuming a, b and c are three adjacent
     *  pixels in Z direction. In the target image will be saved as: <pre>b' = c - a;</pre>
     */
    public static ClearCLBuffer gradientZ(ClearCLBuffer source, ClearCLBuffer destination) {
        GradientZ.gradientZ(getCLIJ2(), source, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.MultiplyImageAndCoordinate
    //----------------------------------------------------
    /**
     * Multiplies all pixel intensities with the x, y or z coordinate, depending on specified dimension.
     */
    public static ClearCLImageInterface multiplyImageAndCoordinate(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3) {
        MultiplyImageAndCoordinate.multiplyImageAndCoordinate(getCLIJ2(), arg1, arg2, new Double (arg3).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.Mean2DBox
    //----------------------------------------------------
    /**
     * Computes the local mean average of a pixels rectangular neighborhood. 
     * 
     * The rectangles size is specified by 
     * its half-width and half-height (radius).
     */
    public static ClearCLImageInterface mean2DBox(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        Mean2DBox.mean2DBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.Mean2DSphere
    //----------------------------------------------------
    /**
     * Computes the local mean average of a pixels ellipsoidal neighborhood. 
     * 
     * The ellipses size is specified by 
     * its half-width and half-height (radius).
     */
    public static ClearCLImageInterface mean2DSphere(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        Mean2DSphere.mean2DSphere(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.Mean3DBox
    //----------------------------------------------------
    /**
     * Computes the local mean average of a pixels cube neighborhood. 
     * 
     * The cubes size is specified by 
     * its half-width, half-height and half-depth (radius).
     */
    public static ClearCLImageInterface mean3DBox(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        Mean3DBox.mean3DBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return arg2;
    }

    /**
     * Computes the local mean average of a pixels rectangular neighborhood. 
     * 
     * The rectangles size is specified by 
     * its half-width and half-height (radius).
     */
    public static ClearCLImageInterface meanBox(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        Mean3DBox.meanBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.Mean3DSphere
    //----------------------------------------------------
    /**
     * Computes the local mean average of a pixels spherical neighborhood. 
     * 
     * The spheres size is specified by 
     * its half-width, half-height and half-depth (radius).
     */
    public static ClearCLImageInterface mean3DSphere(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        Mean3DSphere.mean3DSphere(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.MeanSliceBySliceSphere
    //----------------------------------------------------
    /**
     * Computes the local mean average of a pixels ellipsoidal 2D neighborhood in an image stack 
     * slice by slice. 
     * 
     * The ellipses size is specified by its half-width and half-height (radius).
     * 
     * This filter is applied slice by slice in 2D.
     */
    public static ClearCLImageInterface meanSliceBySliceSphere(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        MeanSliceBySliceSphere.meanSliceBySliceSphere(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.MeanOfAllPixels
    //----------------------------------------------------
    /**
     * Determines the mean average of all pixels in a given image. 
     * 
     * It will be stored in a new row of ImageJs
     * Results table in the column 'Mean'.Parameters
     * ----------
     * source : Image
     *     The image of which the mean average of all pixels or voxels will be determined.
     * 
     */
    public static ClearCLImageInterface meanOfAllPixels(ClearCLImageInterface source) {
        MeanOfAllPixels.meanOfAllPixels(getCLIJ2(), source);
        return source;
    }


    // net.haesleinhuepf.clij2.plugins.Median2DBox
    //----------------------------------------------------
    /**
     * Computes the local median of a pixels rectangular neighborhood. 
     * 
     * The rectangle is specified by 
     * its half-width and half-height (radius).
     * 
     * For technical reasons, the area of the rectangle must have less than 1000 pixels.
     */
    public static ClearCLImageInterface median2DBox(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        Median2DBox.median2DBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.Median2DSphere
    //----------------------------------------------------
    /**
     * Computes the local median of a pixels ellipsoidal neighborhood. 
     * 
     * The ellipses size is specified by 
     * its half-width and half-height (radius).
     * 
     * For technical reasons, the area of the ellipse must have less than 1000 pixels.
     */
    public static ClearCLImageInterface median2DSphere(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        Median2DSphere.median2DSphere(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.Median3DBox
    //----------------------------------------------------
    /**
     * Computes the local median of a pixels cuboid neighborhood. 
     * 
     * The cuboid size is specified by 
     * its half-width, half-height and half-depth (radius).
     * 
     * For technical reasons, the volume of the cuboid must contain less than 1000 voxels.
     */
    public static ClearCLImageInterface median3DBox(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        Median3DBox.median3DBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.Median3DSphere
    //----------------------------------------------------
    /**
     * Computes the local median of a pixels spherical neighborhood. 
     * 
     * The spheres size is specified by 
     * its half-width, half-height and half-depth (radius).
     * 
     * For technical reasons, the volume of the sphere must contain less than 1000 voxels.
     */
    public static ClearCLImageInterface median3DSphere(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        Median3DSphere.median3DSphere(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.MedianSliceBySliceBox
    //----------------------------------------------------
    /**
     * Computes the local median of a pixels rectangular neighborhood. 
     * 
     * This is done slice-by-slice in a 3D 
     * image stack. The rectangle is specified by its half-width and half-height (radius).
     * 
     * For technical reasons, the area of the rectangle must have less than 1000 pixels.
     */
    public static ClearCLImageInterface median3DSliceBySliceBox(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        MedianSliceBySliceBox.median3DSliceBySliceBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.MedianSliceBySliceSphere
    //----------------------------------------------------
    /**
     * Computes the local median of a pixels ellipsoidal neighborhood. 
     * 
     * This is done slice-by-slice in a 3D 
     * image stack. The ellipses size is specified by its half-width and half-height (radius).
     * 
     * For technical reasons, the area of the ellipse must have less than 1000 pixels.
     */
    public static ClearCLImageInterface median3DSliceBySliceSphere(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        MedianSliceBySliceSphere.median3DSliceBySliceSphere(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.Maximum2DSphere
    //----------------------------------------------------
    /**
     * Computes the local maximum of a pixels ellipsoidal neighborhood. 
     * 
     * The ellipses size is specified by 
     * its half-width and half-height (radius).
     */
    public static ClearCLImageInterface maximum2DSphere(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        Maximum2DSphere.maximum2DSphere(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.Maximum3DSphere
    //----------------------------------------------------
    /**
     * Computes the local maximum of a pixels spherical neighborhood. 
     * 
     * The spheres size is specified by 
     * its half-width, half-height and half-depth (radius).
     */
    public static ClearCLImageInterface maximum3DSphere(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        Maximum3DSphere.maximum3DSphere(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.Maximum2DBox
    //----------------------------------------------------
    /**
     * Computes the local maximum of a pixels rectangular neighborhood. 
     * 
     * The rectangles size is specified by 
     * its half-width and half-height (radius).
     */
    public static ClearCLImageInterface maximum2DBox(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        Maximum2DBox.maximum2DBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return arg2;
    }

    /**
     * Computes the local maximum of a pixels rectangular neighborhood. 
     * 
     * The rectangles size is specified by 
     * its half-width and half-height (radius).
     */
    public static ClearCLImageInterface maximumBox(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        Maximum2DBox.maximumBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.Maximum3DBox
    //----------------------------------------------------
    /**
     * Computes the local maximum of a pixels cube neighborhood. 
     * 
     * The cubes size is specified by 
     * its half-width, half-height and half-depth (radius).
     */
    public static ClearCLImageInterface maximum3DBox(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        Maximum3DBox.maximum3DBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return arg2;
    }

    /**
     * Computes the local maximum of a pixels rectangular neighborhood. 
     * 
     * The rectangles size is specified by 
     * its half-width and half-height (radius).
     */
    public static ClearCLImageInterface maximumBox(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        Maximum3DBox.maximumBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.MaximumSliceBySliceSphere
    //----------------------------------------------------
    /**
     * Computes the local maximum of a pixels ellipsoidal 2D neighborhood in an image stack slice by slice. 
     * 
     * The ellipses size is specified by its half-width and half-height (radius).
     * 
     * This filter is applied slice by slice in 2D.
     */
    public static ClearCLImageInterface maximum3DSliceBySliceSphere(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        MaximumSliceBySliceSphere.maximum3DSliceBySliceSphere(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.Minimum2DSphere
    //----------------------------------------------------
    /**
     * Computes the local minimum of a pixels ellipsoidal neighborhood. 
     * 
     * The ellipses size is specified by 
     * its half-width and half-height (radius).
     */
    public static ClearCLImageInterface minimum2DSphere(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        Minimum2DSphere.minimum2DSphere(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.Minimum3DSphere
    //----------------------------------------------------
    /**
     * Computes the local minimum of a pixels spherical neighborhood. 
     * 
     * The spheres size is specified by 
     * its half-width, half-height and half-depth (radius).
     */
    public static ClearCLImageInterface minimum3DSphere(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        Minimum3DSphere.minimum3DSphere(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.Minimum2DBox
    //----------------------------------------------------
    /**
     * Computes the local minimum of a pixels rectangular neighborhood. 
     * 
     * The rectangles size is specified by 
     * its half-width and half-height (radius).
     */
    public static ClearCLImageInterface minimum2DBox(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        Minimum2DBox.minimum2DBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return arg2;
    }

    /**
     * Computes the local minimum of a pixels rectangular neighborhood. 
     * 
     * The rectangles size is specified by 
     * its half-width and half-height (radius).
     */
    public static ClearCLImageInterface minimumBox(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        Minimum2DBox.minimumBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.Minimum3DBox
    //----------------------------------------------------
    /**
     * Computes the local minimum of a pixels cube neighborhood. 
     * 
     * The cubes size is specified by 
     * its half-width, half-height and half-depth (radius).
     */
    public static ClearCLImageInterface minimum3DBox(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        Minimum3DBox.minimum3DBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return arg2;
    }

    /**
     * Computes the local minimum of a pixels rectangular neighborhood. 
     * 
     * The rectangles size is specified by 
     * its half-width and half-height (radius).
     */
    public static ClearCLImageInterface minimumBox(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        Minimum3DBox.minimumBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.MinimumSliceBySliceSphere
    //----------------------------------------------------
    /**
     * Computes the local minimum of a pixels ellipsoidal 2D neighborhood in an image stack 
     * slice by slice. 
     * 
     * The ellipses size is specified by its half-width and half-height (radius).
     * 
     * This filter is applied slice by slice in 2D.
     */
    public static ClearCLImageInterface minimum3DSliceBySliceSphere(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        MinimumSliceBySliceSphere.minimum3DSliceBySliceSphere(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.MultiplyImages
    //----------------------------------------------------
    /**
     * Multiplies all pairs of pixel values x and y from two images X and Y.
     * 
     * <pre>f(x, y) = x * y</pre>
     * 
     * Parameters
     * ----------
     * factor1 : Image
     *     The first input image to be multiplied.
     * factor2 : Image
     *     The second image to be multiplied.
     * destination : Image
     *     The output image where results are written into.
     * 
     */
    public static ClearCLImageInterface multiplyImages(ClearCLImageInterface factor1, ClearCLImageInterface factor2, ClearCLImageInterface destination) {
        MultiplyImages.multiplyImages(getCLIJ2(), factor1, factor2, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.GaussianBlur2D
    //----------------------------------------------------
    /**
     * Computes the Gaussian blurred image of an image given two sigma values in X and Y. 
     * 
     * Thus, the filterkernel can have non-isotropic shape.
     * 
     * The implementation is done separable. In case a sigma equals zero, the direction is not blurred.
     */
    public static ClearCLImageInterface gaussianBlur(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        GaussianBlur2D.gaussianBlur(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue());
        return arg2;
    }

    /**
     * Computes the Gaussian blurred image of an image given two sigma values in X and Y. 
     * 
     * Thus, the filterkernel can have non-isotropic shape.
     * 
     * The implementation is done separable. In case a sigma equals zero, the direction is not blurred.
     */
    public static ClearCLImageInterface gaussianBlur2D(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        GaussianBlur2D.gaussianBlur2D(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.GaussianBlur3D
    //----------------------------------------------------
    /**
     * Computes the Gaussian blurred image of an image given two sigma values in X and Y. 
     * 
     * Thus, the filterkernel can have non-isotropic shape.
     * 
     * The implementation is done separable. In case a sigma equals zero, the direction is not blurred.
     */
    public static ClearCLImageInterface gaussianBlur(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        GaussianBlur3D.gaussianBlur(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue());
        return arg2;
    }

    /**
     * Computes the Gaussian blurred image of an image given two sigma values in X, Y and Z. 
     * 
     * Thus, the filterkernel can have non-isotropic shape.
     * 
     * The implementation is done separable. In case a sigma equals zero, the direction is not blurred.
     */
    public static ClearCLImageInterface gaussianBlur3D(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        GaussianBlur3D.gaussianBlur3D(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clijx.plugins.BlurSliceBySlice
    //----------------------------------------------------

    // net.haesleinhuepf.clij2.plugins.ResliceBottom
    //----------------------------------------------------
    /**
     * Flippes Y and Z axis of an image stack. This operation is similar to ImageJs 'Reslice [/]' method but
     * offers less flexibility such as interpolation.
     */
    public static ClearCLImageInterface resliceBottom(ClearCLImageInterface source, ClearCLImageInterface destination) {
        ResliceBottom.resliceBottom(getCLIJ2(), source, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.ResliceTop
    //----------------------------------------------------
    /**
     * Flippes Y and Z axis of an image stack. This operation is similar to ImageJs 'Reslice [/]' method but
     * offers less flexibility such as interpolation.
     */
    public static ClearCLImageInterface resliceTop(ClearCLImageInterface source, ClearCLImageInterface destination) {
        ResliceTop.resliceTop(getCLIJ2(), source, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.ResliceLeft
    //----------------------------------------------------
    /**
     * Flippes X, Y and Z axis of an image stack. This operation is similar to ImageJs 'Reslice [/]' method 
     *  but offers less flexibility such as interpolation.
     */
    public static ClearCLImageInterface resliceLeft(ClearCLImageInterface source, ClearCLImageInterface destination) {
        ResliceLeft.resliceLeft(getCLIJ2(), source, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.ResliceRight
    //----------------------------------------------------
    /**
     * Flippes X, Y and Z axis of an image stack. This operation is similar to ImageJs 'Reslice [/]' method 
     *  but offers less flexibility such as interpolation.
     */
    public static ClearCLImageInterface resliceRight(ClearCLImageInterface source, ClearCLImageInterface destination) {
        ResliceRight.resliceRight(getCLIJ2(), source, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.Rotate2D
    //----------------------------------------------------
    /**
     * Rotates an image in plane. 
     * 
     * All angles are entered in degrees. If the image is not rotated around 
     * the center, it is rotated around the coordinate origin.
     * 
     * It is recommended to apply the rotation to an isotropic image.
     */
    public static ClearCLBuffer rotate2D(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, boolean arg4) {
        Rotate2D.rotate2D(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), arg4);
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.Rotate3D
    //----------------------------------------------------
    /**
     * Rotates an image stack in 3D. 
     * 
     * All angles are entered in degrees. If the image is not rotated around 
     * the center, it is rotated around the coordinate origin.
     * 
     * It is recommended to apply the rotation to an isotropic image stack.
     */
    public static ClearCLBuffer rotate3D(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, boolean arg6) {
        Rotate3D.rotate3D(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), arg6);
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.Scale2D
    //----------------------------------------------------
    /**
     * Scales an image with a given factor.
     */
    public static ClearCLBuffer scale(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        Scale2D.scale(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return arg2;
    }

    /**
     * Scales an image with a given factor.
     */
    public static ClearCLBuffer scale(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4) {
        Scale2D.scale(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue());
        return arg2;
    }

    /**
     * Scales an image with a given factor.
     */
    public static ClearCLBuffer scale2D(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4) {
        Scale2D.scale2D(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue());
        return arg2;
    }

    /**
     * Scales an image with a given factor.
     */
    public static ClearCLBuffer scale2D(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, boolean arg5) {
        Scale2D.scale2D(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), arg5);
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.Scale3D
    //----------------------------------------------------
    /**
     * Scales an image with a given factor.
     */
    public static ClearCLBuffer scale3D(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5) {
        Scale3D.scale3D(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue());
        return arg2;
    }

    /**
     * Scales an image with a given factor.
     */
    public static ClearCLBuffer scale3D(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, boolean arg6) {
        Scale3D.scale3D(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), arg6);
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.Translate2D
    //----------------------------------------------------
    /**
     * Translate an image stack in X and Y.
     */
    public static ClearCLBuffer translate2D(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4) {
        Translate2D.translate2D(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.Translate3D
    //----------------------------------------------------
    /**
     * Translate an image stack in X, Y and Z.
     */
    public static ClearCLBuffer translate3D(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5) {
        Translate3D.translate3D(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.Clear
    //----------------------------------------------------

    // net.haesleinhuepf.clij2.plugins.ClInfo
    //----------------------------------------------------

    // net.haesleinhuepf.clij2.plugins.ConvertFloat
    //----------------------------------------------------

    // net.haesleinhuepf.clij2.plugins.ConvertUInt8
    //----------------------------------------------------

    // net.haesleinhuepf.clij2.plugins.ConvertUInt16
    //----------------------------------------------------

    // net.haesleinhuepf.clij2.plugins.Create2D
    //----------------------------------------------------

    // net.haesleinhuepf.clij2.plugins.Create3D
    //----------------------------------------------------

    // net.haesleinhuepf.clij2.plugins.Pull
    //----------------------------------------------------

    // net.haesleinhuepf.clij2.plugins.PullBinary
    //----------------------------------------------------

    // net.haesleinhuepf.clij2.plugins.Push
    //----------------------------------------------------

    // net.haesleinhuepf.clij2.plugins.PushCurrentSlice
    //----------------------------------------------------

    // net.haesleinhuepf.clij2.plugins.PushCurrentZStack
    //----------------------------------------------------

    // net.haesleinhuepf.clij2.plugins.PushCurrentSelection
    //----------------------------------------------------

    // net.haesleinhuepf.clij2.plugins.PushCurrentSliceSelection
    //----------------------------------------------------

    // net.haesleinhuepf.clij2.plugins.Release
    //----------------------------------------------------

    // net.haesleinhuepf.clij2.plugins.AddImageAndScalar
    //----------------------------------------------------
    /**
     * Adds a scalar value s to all pixels x of a given image X.
     * 
     * <pre>f(x, s) = x + s</pre>
     * 
     * Parameters
     * ----------
     * source : Image
     *     The input image where scalare should be added.
     * destination : Image
     *     The output image where results are written into.
     * scalar : float
     *     The constant number which will be added to all pixels.
     * 
     */
    public static ClearCLImageInterface addImageAndScalar(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3) {
        AddImageAndScalar.addImageAndScalar(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.DetectMinimaBox
    //----------------------------------------------------
    /**
     * Detects local minima in a given square/cubic neighborhood. 
     * 
     * Pixels in the resulting image are set to 1 if there is no other pixel in a given radius which has a 
     * lower intensity, and to 0 otherwise.
     */
    public static ClearCLImageInterface detectMinimaBox(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        DetectMinimaBox.detectMinimaBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.DetectMaximaBox
    //----------------------------------------------------
    /**
     * Detects local maxima in a given square/cubic neighborhood. 
     * 
     * Pixels in the resulting image are set to 1 if there is no other pixel in a given radius which has a 
     * higher intensity, and to 0 otherwise.
     */
    public static ClearCLImageInterface detectMaximaBox(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        DetectMaximaBox.detectMaximaBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.DetectMaximaSliceBySliceBox
    //----------------------------------------------------
    /**
     * Detects local maxima in a given square neighborhood of an input image stack. 
     * 
     * The input image stack is processed slice by slice. Pixels in the resulting image are set to 1 if 
     * there is no other pixel in a given radius which has a higher intensity, and to 0 otherwise.
     */
    public static ClearCLBuffer detectMaximaSliceBySliceBox(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4) {
        DetectMaximaSliceBySliceBox.detectMaximaSliceBySliceBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.DetectMinimaSliceBySliceBox
    //----------------------------------------------------
    /**
     * Detects local minima in a given square neighborhood of an input image stack. 
     * 
     * The input image stack is processed slice by slice. Pixels in the resulting image are set to 1 if 
     * there is no other pixel in a given radius which has a lower intensity, and to 0 otherwise.
     */
    public static ClearCLBuffer detectMinimaSliceBySliceBox(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4) {
        DetectMinimaSliceBySliceBox.detectMinimaSliceBySliceBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.MaximumOfAllPixels
    //----------------------------------------------------
    /**
     * Determines the maximum of all pixels in a given image. 
     * 
     * It will be stored in a new row of ImageJs
     * Results table in the column 'Max'.
     * 
     * Parameters
     * ----------
     * source : Image
     *     The image of which the maximum of all pixels or voxels will be determined.
     * 
     */
    public static ClearCLImageInterface maximumOfAllPixels(ClearCLImageInterface source) {
        MaximumOfAllPixels.maximumOfAllPixels(getCLIJ2(), source);
        return source;
    }


    // net.haesleinhuepf.clij2.plugins.MinimumOfAllPixels
    //----------------------------------------------------
    /**
     * Determines the minimum of all pixels in a given image. 
     * 
     * It will be stored in a new row of ImageJs
     * Results table in the column 'Min'.
     * 
     * Parameters
     * ----------
     * source : Image
     *     The image of which the minimum of all pixels or voxels will be determined.
     * 
     */
    public static ClearCLImageInterface minimumOfAllPixels(ClearCLImageInterface source) {
        MinimumOfAllPixels.minimumOfAllPixels(getCLIJ2(), source);
        return source;
    }


    // net.haesleinhuepf.clij2.plugins.ReportMemory
    //----------------------------------------------------

    // net.haesleinhuepf.clijx.plugins.splitstack.AbstractSplitStack
    //----------------------------------------------------

    // net.haesleinhuepf.clijx.plugins.TopHatOctagonSliceBySlice
    //----------------------------------------------------
    /**
     * Applies a minimum filter with kernel size 3x3 n times to an image iteratively. 
     * 
     * Odd iterations are done with box neighborhood, even iterations with a diamond. Thus, with n > 2, the filter shape is an octagon. The given number of iterations - 2 makes the filter result very similar to minimum sphere.
     */
    public static ClearCLBuffer topHatOctagonSliceBySlice(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        TopHatOctagonSliceBySlice.topHatOctagonSliceBySlice(getCLIJx(), arg1, arg2, new Double (arg3).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.SetColumn
    //----------------------------------------------------
    /**
     * Sets all pixel values x of a given column in X to a constant value v.
     */
    public static ClearCLImageInterface setColumn(ClearCLImageInterface arg1, double arg2, double arg3) {
        SetColumn.setColumn(getCLIJ2(), arg1, new Double (arg2).intValue(), new Double (arg3).floatValue());
        return arg1;
    }


    // net.haesleinhuepf.clij2.plugins.SetRow
    //----------------------------------------------------
    /**
     * Sets all pixel values x of a given row in X to a constant value v.
     */
    public static ClearCLImageInterface setRow(ClearCLImageInterface arg1, double arg2, double arg3) {
        SetRow.setRow(getCLIJ2(), arg1, new Double (arg2).intValue(), new Double (arg3).floatValue());
        return arg1;
    }


    // net.haesleinhuepf.clij2.plugins.SumYProjection
    //----------------------------------------------------
    /**
     * Determines the sum intensity projection of an image along Z.
     */
    public static ClearCLImageInterface sumYProjection(ClearCLImageInterface source, ClearCLImageInterface destination) {
        SumYProjection.sumYProjection(getCLIJ2(), source, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.AverageDistanceOfTouchingNeighbors
    //----------------------------------------------------
    /**
     * Takes a touch matrix and a distance matrix to determine the average distance of touching neighbors 
     *  for every object.
     * 
     * Parameters
     * ----------
     * distance_matrix : Image
     *     The a distance matrix to be processed.
     * touch_matrix : Image
     *     The binary touch matrix describing which distances should be taken into account.
     * distance_list_destination : Image
     *     A vector image with the same width as the distance matrix and height=1, depth=1.
     *     Determined average distances will be written into this vector.
     * 
     */
    public static ClearCLBuffer averageDistanceOfTouchingNeighbors(ClearCLBuffer distance_matrix, ClearCLBuffer touch_matrix, ClearCLBuffer average_distancelist_destination) {
        AverageDistanceOfTouchingNeighbors.averageDistanceOfTouchingNeighbors(getCLIJ2(), distance_matrix, touch_matrix, average_distancelist_destination);
        return average_distancelist_destination;
    }


    // net.haesleinhuepf.clij2.plugins.LabelledSpotsToPointList
    //----------------------------------------------------
    /**
     * Generates a coordinate list of points in a labelled spot image. 
     * 
     * Transforms a labelmap of spots (single pixels with values 1, 2, ..., n for n spots) as resulting 
     * from connected components analysis in an image where every column contains d 
     * pixels (with d = dimensionality of the original image) with the coordinates of the maxima/minima.
     */
    public static ClearCLBuffer labelledSpotsToPointList(ClearCLBuffer input_labelled_spots, ClearCLBuffer destination_pointlist) {
        LabelledSpotsToPointList.labelledSpotsToPointList(getCLIJ2(), input_labelled_spots, destination_pointlist);
        return destination_pointlist;
    }


    // net.haesleinhuepf.clij2.plugins.LabelSpots
    //----------------------------------------------------
    /**
     * Transforms a binary image with single pixles set to 1 to a labelled spots image. 
     * 
     * Transforms a spots image as resulting from maximum/minimum detection in an image of the same size where every spot has a number 1, 2, ... n.
     */
    public static ClearCLBuffer labelSpots(ClearCLBuffer input_spots, ClearCLBuffer labelled_spots_destination) {
        LabelSpots.labelSpots(getCLIJ2(), input_spots, labelled_spots_destination);
        return labelled_spots_destination;
    }


    // net.haesleinhuepf.clij2.plugins.MinimumDistanceOfTouchingNeighbors
    //----------------------------------------------------
    /**
     * Takes a touch matrix and a distance matrix to determine the shortest distance of touching neighbors for every object.
     */
    public static ClearCLBuffer minimumDistanceOfTouchingNeighbors(ClearCLBuffer distance_matrix, ClearCLBuffer touch_matrix, ClearCLBuffer minimum_distancelist_destination) {
        MinimumDistanceOfTouchingNeighbors.minimumDistanceOfTouchingNeighbors(getCLIJ2(), distance_matrix, touch_matrix, minimum_distancelist_destination);
        return minimum_distancelist_destination;
    }


    // net.haesleinhuepf.clijx.io.WriteVTKLineListToDisc
    //----------------------------------------------------
    /**
     * Takes a point list image representing n points (n*2 for 2D points, n*3 for 3D points) and a corresponding touch matrix , sized (n+1)*(n+1), and exports them in VTK format.
     */
    public static ClearCLBuffer writeVTKLineListToDisc(ClearCLBuffer pointlist, ClearCLBuffer touch_matrix, String filename) {
        WriteVTKLineListToDisc.writeVTKLineListToDisc(getCLIJx(), pointlist, touch_matrix, filename);
        return touch_matrix;
    }


    // net.haesleinhuepf.clijx.io.WriteXYZPointListToDisc
    //----------------------------------------------------
    /**
     * Takes a point list image representing n points (n*2 for 2D points, n*3 for 3D points) and exports them in XYZ format.
     */
    public static ClearCLBuffer writeXYZPointListToDisc(ClearCLBuffer pointlist, String filename) {
        WriteXYZPointListToDisc.writeXYZPointListToDisc(getCLIJx(), pointlist, filename);
        return pointlist;
    }


    // net.haesleinhuepf.clij2.plugins.SetWhereXgreaterThanY
    //----------------------------------------------------
    /**
     * Sets all pixel values a of a given image A to a constant value v in case its coordinates x > y. 
     * 
     * Otherwise the pixel is not overwritten.
     * If you want to initialize an identity transfrom matrix, set all pixels to 0 first.
     */
    public static ClearCLImageInterface setWhereXgreaterThanY(ClearCLImageInterface arg1, double arg2) {
        SetWhereXgreaterThanY.setWhereXgreaterThanY(getCLIJ2(), arg1, new Double (arg2).floatValue());
        return arg1;
    }


    // net.haesleinhuepf.clij2.plugins.SetWhereXsmallerThanY
    //----------------------------------------------------
    /**
     * Sets all pixel values a of a given image A to a constant value v in case its coordinates x < y. 
     * 
     * Otherwise the pixel is not overwritten.
     * If you want to initialize an identity transfrom matrix, set all pixels to 0 first.
     */
    public static ClearCLImageInterface setWhereXsmallerThanY(ClearCLImageInterface arg1, double arg2) {
        SetWhereXsmallerThanY.setWhereXsmallerThanY(getCLIJ2(), arg1, new Double (arg2).floatValue());
        return arg1;
    }


    // net.haesleinhuepf.clij2.plugins.SetNonZeroPixelsToPixelIndex
    //----------------------------------------------------
    /**
     * Sets all pixels in an image which are not zero to the index of the pixel. 
     * 
     * This can be used for Connected Components Analysis.
     */
    public static ClearCLImageInterface setNonZeroPixelsToPixelIndex(ClearCLImageInterface source, ClearCLImageInterface destination) {
        SetNonZeroPixelsToPixelIndex.setNonZeroPixelsToPixelIndex(getCLIJ2(), source, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.CloseIndexGapsInLabelMap
    //----------------------------------------------------
    /**
     * Analyses a label map and if there are gaps in the indexing (e.g. label 5 is not present) all 
     * subsequent labels will be relabelled. 
     * 
     * Thus, afterwards number of labels and maximum label index are equal.
     * This operation is mostly performed on the CPU.
     */
    public static ClearCLImageInterface closeIndexGapsInLabelMap(ClearCLBuffer labeling_input, ClearCLImageInterface labeling_destination) {
        CloseIndexGapsInLabelMap.closeIndexGapsInLabelMap(getCLIJ2(), labeling_input, labeling_destination);
        return labeling_destination;
    }


    // net.haesleinhuepf.clij2.plugins.AffineTransform
    //----------------------------------------------------

    // net.haesleinhuepf.clij2.plugins.Scale
    //----------------------------------------------------

    // net.haesleinhuepf.clij2.plugins.CentroidsOfLabels
    //----------------------------------------------------
    /**
     * Determines the centroids of all labels in a label image or image stack. 
     * 
     * It writes the resulting  coordinates in a pointlist image. Depending on the dimensionality d of the labelmap and the number  of labels n, the pointlist image will have n*d pixels.
     */
    public static ClearCLBuffer centroidsOfLabels(ClearCLBuffer source, ClearCLBuffer pointlist_destination) {
        CentroidsOfLabels.centroidsOfLabels(getCLIJ2(), source, pointlist_destination);
        return pointlist_destination;
    }


    // net.haesleinhuepf.clij2.plugins.SetRampX
    //----------------------------------------------------
    /**
     * Sets all pixel values to their X coordinate
     */
    public static ClearCLImageInterface setRampX(ClearCLImageInterface source) {
        SetRampX.setRampX(getCLIJ2(), source);
        return source;
    }


    // net.haesleinhuepf.clij2.plugins.SetRampY
    //----------------------------------------------------
    /**
     * Sets all pixel values to their Y coordinate
     */
    public static ClearCLImageInterface setRampY(ClearCLImageInterface source) {
        SetRampY.setRampY(getCLIJ2(), source);
        return source;
    }


    // net.haesleinhuepf.clij2.plugins.SetRampZ
    //----------------------------------------------------
    /**
     * Sets all pixel values to their Z coordinate
     */
    public static ClearCLImageInterface setRampZ(ClearCLImageInterface source) {
        SetRampZ.setRampZ(getCLIJ2(), source);
        return source;
    }


    // net.haesleinhuepf.clij2.plugins.SubtractImageFromScalar
    //----------------------------------------------------
    /**
     * Subtracts one image X from a scalar s pixel wise.
     * 
     * <pre>f(x, s) = s - x</pre>
     */
    public static ClearCLImageInterface subtractImageFromScalar(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3) {
        SubtractImageFromScalar.subtractImageFromScalar(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.ThresholdDefault
    //----------------------------------------------------
    /**
     * The automatic thresholder utilizes the Default threshold method implemented in ImageJ using a histogram determined on 
     * the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.
     */
    public static ClearCLBuffer thresholdDefault(ClearCLBuffer input, ClearCLBuffer destination) {
        ThresholdDefault.thresholdDefault(getCLIJ2(), input, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.ThresholdOtsu
    //----------------------------------------------------
    /**
     * The automatic thresholder utilizes the Otsu threshold method implemented in ImageJ using a histogram determined on 
     * the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.
     */
    public static ClearCLBuffer thresholdOtsu(ClearCLBuffer input, ClearCLBuffer destination) {
        ThresholdOtsu.thresholdOtsu(getCLIJ2(), input, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.ThresholdHuang
    //----------------------------------------------------
    /**
     * The automatic thresholder utilizes the Huang threshold method implemented in ImageJ using a histogram determined on 
     * the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.
     */
    public static ClearCLBuffer thresholdHuang(ClearCLBuffer input, ClearCLBuffer destination) {
        ThresholdHuang.thresholdHuang(getCLIJ2(), input, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.ThresholdIntermodes
    //----------------------------------------------------
    /**
     * The automatic thresholder utilizes the Intermodes threshold method implemented in ImageJ using a histogram determined on 
     * the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.
     */
    public static ClearCLBuffer thresholdIntermodes(ClearCLBuffer input, ClearCLBuffer destination) {
        ThresholdIntermodes.thresholdIntermodes(getCLIJ2(), input, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.ThresholdIsoData
    //----------------------------------------------------
    /**
     * The automatic thresholder utilizes the IsoData threshold method implemented in ImageJ using a histogram determined on 
     * the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.
     */
    public static ClearCLBuffer thresholdIsoData(ClearCLBuffer input, ClearCLBuffer destination) {
        ThresholdIsoData.thresholdIsoData(getCLIJ2(), input, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.ThresholdIJ_IsoData
    //----------------------------------------------------
    /**
     * The automatic thresholder utilizes the IJ_IsoData threshold method implemented in ImageJ using a histogram determined on 
     * the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.
     */
    public static ClearCLBuffer thresholdIJ_IsoData(ClearCLBuffer input, ClearCLBuffer destination) {
        ThresholdIJ_IsoData.thresholdIJ_IsoData(getCLIJ2(), input, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.ThresholdLi
    //----------------------------------------------------
    /**
     * The automatic thresholder utilizes the Li threshold method implemented in ImageJ using a histogram determined on 
     * the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.
     */
    public static ClearCLBuffer thresholdLi(ClearCLBuffer input, ClearCLBuffer destination) {
        ThresholdLi.thresholdLi(getCLIJ2(), input, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.ThresholdMaxEntropy
    //----------------------------------------------------
    /**
     * The automatic thresholder utilizes the MaxEntropy threshold method implemented in ImageJ using a histogram determined on 
     * the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.
     */
    public static ClearCLBuffer thresholdMaxEntropy(ClearCLBuffer input, ClearCLBuffer destination) {
        ThresholdMaxEntropy.thresholdMaxEntropy(getCLIJ2(), input, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.ThresholdMean
    //----------------------------------------------------
    /**
     * The automatic thresholder utilizes the Mean threshold method implemented in ImageJ using a histogram determined on 
     * the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.
     */
    public static ClearCLBuffer thresholdMean(ClearCLBuffer input, ClearCLBuffer destination) {
        ThresholdMean.thresholdMean(getCLIJ2(), input, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.ThresholdMinError
    //----------------------------------------------------
    /**
     * The automatic thresholder utilizes the MinError threshold method implemented in ImageJ using a histogram determined on 
     * the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.
     */
    public static ClearCLBuffer thresholdMinError(ClearCLBuffer input, ClearCLBuffer destination) {
        ThresholdMinError.thresholdMinError(getCLIJ2(), input, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.ThresholdMinimum
    //----------------------------------------------------
    /**
     * The automatic thresholder utilizes the Minimum threshold method implemented in ImageJ using a histogram determined on 
     * the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.
     */
    public static ClearCLBuffer thresholdMinimum(ClearCLBuffer input, ClearCLBuffer destination) {
        ThresholdMinimum.thresholdMinimum(getCLIJ2(), input, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.ThresholdMoments
    //----------------------------------------------------
    /**
     * The automatic thresholder utilizes the Moments threshold method implemented in ImageJ using a histogram determined on 
     * the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.
     */
    public static ClearCLBuffer thresholdMoments(ClearCLBuffer input, ClearCLBuffer destination) {
        ThresholdMoments.thresholdMoments(getCLIJ2(), input, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.ThresholdPercentile
    //----------------------------------------------------
    /**
     * The automatic thresholder utilizes the Percentile threshold method implemented in ImageJ using a histogram determined on 
     * the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.
     */
    public static ClearCLBuffer thresholdPercentile(ClearCLBuffer input, ClearCLBuffer destination) {
        ThresholdPercentile.thresholdPercentile(getCLIJ2(), input, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.ThresholdRenyiEntropy
    //----------------------------------------------------
    /**
     * The automatic thresholder utilizes the RenyiEntropy threshold method implemented in ImageJ using a histogram determined on 
     * the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.
     */
    public static ClearCLBuffer thresholdRenyiEntropy(ClearCLBuffer input, ClearCLBuffer destination) {
        ThresholdRenyiEntropy.thresholdRenyiEntropy(getCLIJ2(), input, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.ThresholdShanbhag
    //----------------------------------------------------
    /**
     * The automatic thresholder utilizes the Shanbhag threshold method implemented in ImageJ using a histogram determined on 
     * the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.
     */
    public static ClearCLBuffer thresholdShanbhag(ClearCLBuffer input, ClearCLBuffer destination) {
        ThresholdShanbhag.thresholdShanbhag(getCLIJ2(), input, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.ThresholdTriangle
    //----------------------------------------------------
    /**
     * The automatic thresholder utilizes the Triangle threshold method implemented in ImageJ using a histogram determined on 
     * the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.
     */
    public static ClearCLBuffer thresholdTriangle(ClearCLBuffer input, ClearCLBuffer destination) {
        ThresholdTriangle.thresholdTriangle(getCLIJ2(), input, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.ThresholdYen
    //----------------------------------------------------
    /**
     * The automatic thresholder utilizes the Yen threshold method implemented in ImageJ using a histogram determined on 
     * the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.
     */
    public static ClearCLBuffer thresholdYen(ClearCLBuffer input, ClearCLBuffer destination) {
        ThresholdYen.thresholdYen(getCLIJ2(), input, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.ExcludeLabelsSubSurface
    //----------------------------------------------------
    /**
     * This operation follows a ray from a given position towards a label (or opposite direction) and checks if  there is another label between the label an the image border. 
     * 
     * If yes, this label is eliminated from the label map.
     */
    public static ClearCLBuffer excludeLabelsSubSurface(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4, double arg5, double arg6) {
        ExcludeLabelsSubSurface.excludeLabelsSubSurface(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).floatValue(), new Double (arg5).floatValue(), new Double (arg6).floatValue());
        return arg3;
    }


    // net.haesleinhuepf.clij2.plugins.ExcludeLabelsOnSurface
    //----------------------------------------------------
    /**
     * This operation follows a ray from a given position towards a label (or opposite direction) and checks if  there is another label between the label an the image border. 
     * 
     * If yes, this label is eliminated from the label map.
     */
    public static ClearCLBuffer excludeLabelsOnSurface(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4, double arg5, double arg6) {
        ExcludeLabelsOnSurface.excludeLabelsOnSurface(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).floatValue(), new Double (arg5).floatValue(), new Double (arg6).floatValue());
        return arg3;
    }


    // net.haesleinhuepf.clij2.plugins.SetPlane
    //----------------------------------------------------
    /**
     * Sets all pixel values x of a given plane in X to a constant value v.
     */
    public static ClearCLImageInterface setPlane(ClearCLImageInterface arg1, double arg2, double arg3) {
        SetPlane.setPlane(getCLIJ2(), arg1, new Double (arg2).intValue(), new Double (arg3).floatValue());
        return arg1;
    }


    // net.haesleinhuepf.clijx.plugins.tenengradfusion.TenengradFusion
    //----------------------------------------------------
    /**
     * Fuses #n# image stacks using Tenengrads algorithm.
     */
    public static ClearCLBuffer[] tenengradFusion(ClearCLBuffer arg1, float[] arg2, float arg3, ClearCLBuffer[] arg4) {
        TenengradFusion.tenengradFusion(getCLIJx(), arg1, arg2, arg3, arg4);
        return arg4;
    }

    /**
     * Fuses #n# image stacks using Tenengrads algorithm.
     */
    public static ClearCLBuffer tenengradFusion(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, double arg6, double arg7) {
        TenengradFusion.tenengradFusion(getCLIJx(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), new Double (arg6).floatValue(), new Double (arg7).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.ImageToStack
    //----------------------------------------------------
    /**
     * Copies a single slice into a stack a given number of times.
     */
    public static ClearCLBuffer imageToStack(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        ImageToStack.imageToStack(getCLIJ2(), arg1, arg2, new Double (arg3).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.SumXProjection
    //----------------------------------------------------
    /**
     * Determines the sum intensity projection of an image along Z.
     */
    public static ClearCLImageInterface sumXProjection(ClearCLImageInterface source, ClearCLImageInterface destination) {
        SumXProjection.sumXProjection(getCLIJ2(), source, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.SumImageSliceBySlice
    //----------------------------------------------------
    /**
     * Sums all pixels slice by slice and returns the sums in a vector.
     */
    public static ClearCLImageInterface sumImageSliceBySlice(ClearCLImageInterface source, ClearCLImageInterface destination) {
        SumImageSliceBySlice.sumImageSliceBySlice(getCLIJ2(), source, destination);
        return destination;
    }

    /**
     * Sums all pixels slice by slice and returns the sums in a vector.
     */
    public static ClearCLImageInterface sumImageSliceBySlice(ClearCLImageInterface arg1) {
        SumImageSliceBySlice.sumImageSliceBySlice(getCLIJ2(), arg1);
        return arg1;
    }


    // net.haesleinhuepf.clij2.plugins.MultiplyImageStackWithScalars
    //----------------------------------------------------
    /**
     * Multiplies all pixels value x in a given image X with a constant scalar s from a list of scalars.
     * 
     * <pre>f(x, s) = x * s</pre>
     */
    public static ClearCLImageInterface multiplyImageStackWithScalars(ClearCLImageInterface arg1, ClearCLImageInterface arg2, float[] arg3) {
        MultiplyImageStackWithScalars.multiplyImageStackWithScalars(getCLIJ2(), arg1, arg2, arg3);
        return arg2;
    }

    /**
     * Multiplies all pixels value x in a given image X with a constant scalar s from a list of scalars.
     * 
     * <pre>f(x, s) = x * s</pre>
     */
    public static ClearCLBuffer multiplyImageStackWithScalars(ClearCLImageInterface arg1, ClearCLImageInterface arg2, ClearCLBuffer arg3) {
        MultiplyImageStackWithScalars.multiplyImageStackWithScalars(getCLIJ2(), arg1, arg2, arg3);
        return arg3;
    }


    // net.haesleinhuepf.clij2.plugins.Print
    //----------------------------------------------------
    /**
     * Visualises an image on standard out (console).
     */
    public static ClearCLImageInterface print(ClearCLImageInterface input) {
        Print.print(getCLIJ2(), input);
        return input;
    }


    // net.haesleinhuepf.clij2.plugins.VoronoiOctagon
    //----------------------------------------------------
    /**
     * Takes a binary image and dilates the regions using a octagon shape until they touch. 
     * 
     * The pixels where  the regions touched are afterwards returned as binary image which corresponds to the Voronoi diagram.
     */
    public static ClearCLBuffer voronoiOctagon(ClearCLBuffer input, ClearCLBuffer destination) {
        VoronoiOctagon.voronoiOctagon(getCLIJ2(), input, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.SetImageBorders
    //----------------------------------------------------
    /**
     * Sets all pixel values at the image border to a given value.
     */
    public static ClearCLImageInterface setImageBorders(ClearCLImageInterface arg1, double arg2) {
        SetImageBorders.setImageBorders(getCLIJ2(), arg1, new Double (arg2).floatValue());
        return arg1;
    }


    // net.haesleinhuepf.clijx.plugins.Skeletonize
    //----------------------------------------------------

    // net.haesleinhuepf.clij2.plugins.FloodFillDiamond
    //----------------------------------------------------
    /**
     * Replaces recursively all pixels of value a with value b if the pixels have a neighbor with value b.
     */
    public static ClearCLBuffer floodFillDiamond(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4) {
        FloodFillDiamond.floodFillDiamond(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.BinaryFillHoles
    //----------------------------------------------------
    /**
     * Fills holes (pixels with value 0 surrounded by pixels with value 1) in a binary image.
     * 
     * Note: This function is known to perform slowly on large images. Consider using the extension 
     * CLIJx_morphoLibJFillHoles(input, destination) instead.
     * Read more: http://clij.github.io/assistant/installation#extensions
     * 
     * Parameters
     * ----------
     * source : Image
     *     The binary input image where holes will be filled.
     * destination : Image
     *     The output image where true pixels will be 1.
     * 
     */
    public static ClearCLImageInterface binaryFillHoles(ClearCLImageInterface source, ClearCLImageInterface destination) {
        BinaryFillHoles.binaryFillHoles(getCLIJ2(), source, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.ConnectedComponentsLabelingDiamond
    //----------------------------------------------------
    /**
     * Performs connected components analysis inspecting the diamond neighborhood of every pixel to a binary image and generates a label map.
     */
    public static ClearCLImageInterface connectedComponentsLabelingDiamond(ClearCLImageInterface binary_input, ClearCLImageInterface labeling_destination) {
        ConnectedComponentsLabelingDiamond.connectedComponentsLabelingDiamond(getCLIJ2(), binary_input, labeling_destination);
        return labeling_destination;
    }

    /**
     * Performs connected components analysis inspecting the diamond neighborhood of every pixel to a binary image and generates a label map.
     */
    public static ClearCLImageInterface connectedComponentsLabelingDiamond(ClearCLImageInterface arg1, ClearCLImageInterface arg2, boolean arg3) {
        ConnectedComponentsLabelingDiamond.connectedComponentsLabelingDiamond(getCLIJ2(), arg1, arg2, arg3);
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.ConnectedComponentsLabelingBox
    //----------------------------------------------------
    /**
     * Performs connected components analysis inspecting the box neighborhood of every pixel to a binary image and generates a label map.
     */
    public static ClearCLImageInterface connectedComponentsLabelingBox(ClearCLImageInterface binary_input, ClearCLImageInterface labeling_destination) {
        ConnectedComponentsLabelingBox.connectedComponentsLabelingBox(getCLIJ2(), binary_input, labeling_destination);
        return labeling_destination;
    }

    /**
     * Performs connected components analysis inspecting the box neighborhood of every pixel to a binary image and generates a label map.
     */
    public static ClearCLImageInterface connectedComponentsLabelingBox(ClearCLImageInterface arg1, ClearCLImageInterface arg2, boolean arg3) {
        ConnectedComponentsLabelingBox.connectedComponentsLabelingBox(getCLIJ2(), arg1, arg2, arg3);
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.SetRandom
    //----------------------------------------------------
    /**
     * Fills an image or image stack with uniformly distributed random numbers between given minimum and maximum values. 
     * 
     * Recommendation: For the seed, use getTime().
     */
    public static ClearCLBuffer setRandom(ClearCLBuffer arg1, double arg2, double arg3) {
        SetRandom.setRandom(getCLIJ2(), arg1, new Double (arg2).floatValue(), new Double (arg3).floatValue());
        return arg1;
    }

    /**
     * Fills an image or image stack with uniformly distributed random numbers between given minimum and maximum values. 
     * 
     * Recommendation: For the seed, use getTime().
     */
    public static ClearCLBuffer setRandom(ClearCLBuffer arg1, double arg2, double arg3, double arg4) {
        SetRandom.setRandom(getCLIJ2(), arg1, new Double (arg2).floatValue(), new Double (arg3).floatValue(), new Double (arg4).floatValue());
        return arg1;
    }


    // net.haesleinhuepf.clij2.plugins.InvalidateKernelCache
    //----------------------------------------------------

    // net.haesleinhuepf.clij2.plugins.EntropyBox
    //----------------------------------------------------
    /**
     * Determines the local entropy in a box with a given radius around every pixel.
     */
    public static ClearCLBuffer entropyBox(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5) {
        EntropyBox.entropyBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return arg2;
    }

    /**
     * Determines the local entropy in a box with a given radius around every pixel.
     */
    public static ClearCLBuffer entropyBox(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, double arg6, double arg7) {
        EntropyBox.entropyBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue(), new Double (arg6).floatValue(), new Double (arg7).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.PushTile
    //----------------------------------------------------
    /**
     * Push a tile in an image specified by its name, position and size to GPU memory in order to process it there later.
     */
    public static ClearCLBuffer pushTile(ImagePlus arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8, double arg9, double arg10) {
        ClearCLBuffer result = PushTile.pushTile(getCLIJ2(), arg1, new Double (arg2).intValue(), new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue(), new Double (arg6).intValue(), new Double (arg7).intValue(), new Double (arg8).intValue(), new Double (arg9).intValue(), new Double (arg10).intValue());
        return result;
    }

    /**
     * Push a tile in an image specified by its name, position and size to GPU memory in order to process it there later.
     */
    public static ClearCLBuffer pushTile(ClearCLBuffer arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8, double arg9, double arg10) {
        PushTile.pushTile(getCLIJ2(), arg1, new Double (arg2).intValue(), new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue(), new Double (arg6).intValue(), new Double (arg7).intValue(), new Double (arg8).intValue(), new Double (arg9).intValue(), new Double (arg10).intValue());
        return arg1;
    }

    /**
     * Push a tile in an image specified by its name, position and size to GPU memory in order to process it there later.
     */
    public static void pushTile(ImagePlus arg1, String arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8, double arg9, double arg10, double arg11) {
        PushTile.pushTile(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue(), new Double (arg6).intValue(), new Double (arg7).intValue(), new Double (arg8).intValue(), new Double (arg9).intValue(), new Double (arg10).intValue(), new Double (arg11).intValue());
    }


    // net.haesleinhuepf.clij2.plugins.PullTile
    //----------------------------------------------------
    /**
     * Pushes a tile in an image specified by its name, position and size from GPU memory.
     */
    public static void pullTile(ImagePlus arg1, String arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8, double arg9, double arg10, double arg11) {
        PullTile.pullTile(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue(), new Double (arg6).intValue(), new Double (arg7).intValue(), new Double (arg8).intValue(), new Double (arg9).intValue(), new Double (arg10).intValue(), new Double (arg11).intValue());
    }

    /**
     * Pushes a tile in an image specified by its name, position and size from GPU memory.
     */
    public static ClearCLBuffer pullTile(ImagePlus arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8, double arg9, double arg10, double arg11) {
        PullTile.pullTile(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue(), new Double (arg6).intValue(), new Double (arg7).intValue(), new Double (arg8).intValue(), new Double (arg9).intValue(), new Double (arg10).intValue(), new Double (arg11).intValue());
        return arg2;
    }

    /**
     * Pushes a tile in an image specified by its name, position and size from GPU memory.
     */
    public static ClearCLBuffer pullTile(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8, double arg9, double arg10, double arg11) {
        PullTile.pullTile(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue(), new Double (arg6).intValue(), new Double (arg7).intValue(), new Double (arg8).intValue(), new Double (arg9).intValue(), new Double (arg10).intValue(), new Double (arg11).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.ConcatenateStacks
    //----------------------------------------------------
    /**
     * Concatenates two stacks in Z.
     */
    public static ClearCLImageInterface concatenateStacks(ClearCLImageInterface stack1, ClearCLImageInterface stack2, ClearCLImageInterface destination) {
        ConcatenateStacks.concatenateStacks(getCLIJ2(), stack1, stack2, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.ResultsTableToImage2D
    //----------------------------------------------------

    // net.haesleinhuepf.clij2.plugins.GetAutomaticThreshold
    //----------------------------------------------------
    /**
     * Determines a threshold according to a given method and saves it to the threshold_value variable.
     * 
     * The automatic thresholder utilizes the threshold methods from ImageJ on a histogram determined on 
     * the GPU to determine a threshold value as similar as possible to ImageJ 'Apply Threshold' method. 
     * 
     * Enter one 
     * of these methods in the method text field:
     * [Default, Huang, Intermodes, IsoData, IJ_IsoData, Li, MaxEntropy, Mean, MinError, Minimum, Moments, Otsu, Percentile, RenyiEntropy, Shanbhag, Triangle, Yen]
     */
    public static ClearCLBuffer getAutomaticThreshold(ClearCLBuffer arg1, String arg2) {
        GetAutomaticThreshold.getAutomaticThreshold(getCLIJ2(), arg1, arg2);
        return arg1;
    }

    /**
     * Determines a threshold according to a given method and saves it to the threshold_value variable.
     * 
     * The automatic thresholder utilizes the threshold methods from ImageJ on a histogram determined on 
     * the GPU to determine a threshold value as similar as possible to ImageJ 'Apply Threshold' method. 
     * 
     * Enter one 
     * of these methods in the method text field:
     * [Default, Huang, Intermodes, IsoData, IJ_IsoData, Li, MaxEntropy, Mean, MinError, Minimum, Moments, Otsu, Percentile, RenyiEntropy, Shanbhag, Triangle, Yen]
     */
    public static ClearCLBuffer getAutomaticThreshold(ClearCLBuffer arg1, String arg2, double arg3, double arg4, double arg5) {
        GetAutomaticThreshold.getAutomaticThreshold(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).intValue());
        return arg1;
    }


    // net.haesleinhuepf.clij2.plugins.GetDimensions
    //----------------------------------------------------
    /**
     * Reads out the size of an image [stack] and writes it to the variables 'width', 'height' and 'depth'.
     */
    public static ClearCLBuffer getDimensions(ClearCLBuffer arg1) {
        GetDimensions.getDimensions(getCLIJ2(), arg1);
        return arg1;
    }


    // net.haesleinhuepf.clij2.plugins.CustomOperation
    //----------------------------------------------------
    /**
     * Executes a custom operation wirtten in OpenCL on a custom list of images. 
     * 
     * All images must be created before calling this method. Image parameters should be handed over as an array with parameter names and image names alternating, e.g.
     * 
     * Ext.CLIJ2_customOperation(..., ..., newArray("image1", "blobs.gif", "image2", "Processed_blobs.gif"))
     * 
     * In the custom code, you can use the predefined variables x, y and z to deal with coordinates.
     * You can for example use it to access pixel intensities like this:
     * 
     * float value = READ_IMAGE(image, sampler, POS_image_INSTANCE(x, y, z, 0)).x;
     * WRITE_IMAGE(image, POS_image_INSTANCE(x, y, z, 0), CONVERT_image_PIXEL_TYPE(value));
     * 
     * Note: replace `image` with the given image parameter name. You can furthermore use custom function to organise code in the global_code parameter. In OpenCL they may look like this:
     * 
     * inline float sum(float a, float b) {
     *     return a + b;
     * }
     * 
     */
    public static boolean customOperation(String arg1, String arg2, HashMap arg3) {
        boolean result = CustomOperation.customOperation(getCLIJ2(), arg1, arg2, arg3);
        return result;
    }


    // net.haesleinhuepf.clijx.weka.autocontext.ApplyAutoContextWekaModel
    //----------------------------------------------------
    /**
     * 
     */
    public static ClearCLBuffer applyAutoContextWekaModelWithOptions(ClearCLBuffer arg1, ClearCLBuffer arg2, String arg3, String arg4, double arg5) {
        ApplyAutoContextWekaModel.applyAutoContextWekaModelWithOptions(getCLIJ2(), arg1, arg2, arg3, arg4, new Double (arg5).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clijx.weka.autocontext.TrainAutoContextWekaModel
    //----------------------------------------------------
    /**
     * 
     */
    public static ClearCLBuffer trainAutoContextWekaModelWithOptions(ClearCLBuffer arg1, ClearCLBuffer arg2, String arg3, String arg4, double arg5, double arg6, double arg7, double arg8) {
        TrainAutoContextWekaModel.trainAutoContextWekaModelWithOptions(getCLIJ2(), arg1, arg2, arg3, arg4, new Double (arg5).intValue(), new Double (arg6).intValue(), new Double (arg7).intValue(), new Double (arg8).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clijx.weka.ApplyWekaModel
    //----------------------------------------------------
    /**
     * Applies a Weka model using functionality of Fijis Trainable Weka Segmentation plugin. 
     * 
     * It takes a 3D feature stack (e.g. first plane original image, second plane blurred, third plane edge image)and applies a pre-trained a Weka model. Take care that the feature stack has been generated in the sameway as for training the model!
     */
    public static ClearCLBuffer applyWekaModel(ClearCLBuffer arg1, ClearCLBuffer arg2, CLIJxWeka2 arg3) {
        ApplyWekaModel.applyWekaModel(getCLIJ2(), arg1, arg2, arg3);
        return arg2;
    }

    /**
     * Applies a Weka model using functionality of Fijis Trainable Weka Segmentation plugin. 
     * 
     * It takes a 3D feature stack (e.g. first plane original image, second plane blurred, third plane edge image)and applies a pre-trained a Weka model. Take care that the feature stack has been generated in the sameway as for training the model!
     */
    public static ClearCLBuffer applyWekaModel(ClearCLBuffer featureStack3D, ClearCLBuffer prediction2D_destination, String loadModelFilename) {
        ApplyWekaModel.applyWekaModel(getCLIJ2(), featureStack3D, prediction2D_destination, loadModelFilename);
        return prediction2D_destination;
    }


    // net.haesleinhuepf.clijx.weka.ApplyWekaToTable
    //----------------------------------------------------
    /**
     * Applies a Weka model using functionality of Fijis Trainable Weka Segmentation plugin. 
     * 
     * It takes a Results Table, sorts its columns by name alphabetically and uses it as extracted features (rows correspond to feature vectors) and applies a pre-trained a Weka model. Take care that the table has been generated in the sameway as for training the model!
     */
    public static CLIJxWeka2 applyWekaToTable(ResultsTable arg1, String arg2, String arg3) {
        CLIJxWeka2 result = ApplyWekaToTable.applyWekaToTable(getCLIJ2(), arg1, arg2, arg3);
        return result;
    }

    /**
     * Applies a Weka model using functionality of Fijis Trainable Weka Segmentation plugin. 
     * 
     * It takes a Results Table, sorts its columns by name alphabetically and uses it as extracted features (rows correspond to feature vectors) and applies a pre-trained a Weka model. Take care that the table has been generated in the sameway as for training the model!
     */
    public static CLIJxWeka2 applyWekaToTable(ResultsTable arg1, String arg2, CLIJxWeka2 arg3) {
        CLIJxWeka2 result = ApplyWekaToTable.applyWekaToTable(getCLIJ2(), arg1, arg2, arg3);
        return result;
    }


    // net.haesleinhuepf.clijx.weka.GenerateFeatureStack
    //----------------------------------------------------
    /**
     * Generates a feature stack for Trainable Weka Segmentation. 
     * 
     * Use this terminology to specifiy which stacks should be generated:
     * * "original" original slice
     * * "GaussianBlur=s" Gaussian blurred image with sigma s
     * * "LaplacianOfGaussian=s" Laplacian of Gaussian blurred image with sigma s
     * * "SobelOfGaussian=s" Sobel filter applied to Gaussian blurred image with sigma s
     * * "minimum=r" local minimum with radius r
     * * "maximum=r" local maximum with radius r
     * * "mean=r" local mean with radius r
     * * "entropy=r" local entropy with radius r
     * * "gradientX" local gradient in X direction
     * * "gradientY" local gradient in Y direction
     * 
     * Use sigma=0 to apply a filter to the original image. Feature definitions are not case sensitive.
     * 
     * Example: "original gaussianBlur=1 gaussianBlur=5 laplacianOfGaussian=1 laplacianOfGaussian=7 entropy=3"
     */
    public static ClearCLBuffer generateFeatureStack(ClearCLBuffer input, ClearCLBuffer feature_stack_destination, String feature_definitions) {
        GenerateFeatureStack.generateFeatureStack(getCLIJ2(), input, feature_stack_destination, feature_definitions);
        return feature_stack_destination;
    }

    /**
     * Generates a feature stack for Trainable Weka Segmentation. 
     * 
     * Use this terminology to specifiy which stacks should be generated:
     * * "original" original slice
     * * "GaussianBlur=s" Gaussian blurred image with sigma s
     * * "LaplacianOfGaussian=s" Laplacian of Gaussian blurred image with sigma s
     * * "SobelOfGaussian=s" Sobel filter applied to Gaussian blurred image with sigma s
     * * "minimum=r" local minimum with radius r
     * * "maximum=r" local maximum with radius r
     * * "mean=r" local mean with radius r
     * * "entropy=r" local entropy with radius r
     * * "gradientX" local gradient in X direction
     * * "gradientY" local gradient in Y direction
     * 
     * Use sigma=0 to apply a filter to the original image. Feature definitions are not case sensitive.
     * 
     * Example: "original gaussianBlur=1 gaussianBlur=5 laplacianOfGaussian=1 laplacianOfGaussian=7 entropy=3"
     */
    public static ClearCLBuffer generateFeatureStack(ClearCLBuffer arg1, String arg2) {
        GenerateFeatureStack.generateFeatureStack(getCLIJ2(), arg1, arg2);
        return arg1;
    }


    // net.haesleinhuepf.clijx.weka.TrainWekaModel
    //----------------------------------------------------
    /**
     * Trains a Weka model using functionality of Fijis Trainable Weka Segmentation plugin. 
     * 
     * It takes a 3D feature stack (e.g. first plane original image, second plane blurred, third plane edge image)and trains a Weka model. This model will be saved to disc.
     * The given groundTruth image is supposed to be a label map where pixels with value 1 represent class 1, pixels with value 2 represent class 2 and so on. Pixels with value 0 will be ignored for training.
     */
    public static ClearCLBuffer trainWekaModel(ClearCLBuffer featureStack3D, ClearCLBuffer groundTruth2D, String saveModelFilename) {
        TrainWekaModel.trainWekaModel(getCLIJ2(), featureStack3D, groundTruth2D, saveModelFilename);
        return groundTruth2D;
    }


    // net.haesleinhuepf.clijx.weka.TrainWekaFromTable
    //----------------------------------------------------
    /**
     * Trains a Weka model using functionality of Fijis Trainable Weka Segmentation plugin. 
     * 
     * It takes the given Results Table, sorts its columns alphabetically as extracted features (rows correspond to feature vectors) and a given column name containing the ground truth to train a Weka model. This model will be saved to disc.
     * The given groundTruth column is supposed to be numeric with values 1 represent class 1,  value 2 represent class 2 and so on. Value 0 will be ignored for training.
     * 
     * Default values for options are:
     * * trees = 200
     * * features = 2
     * * maxDepth = 0
     */
    public static CLIJxWeka2 trainWekaFromTable(ResultsTable arg1, String arg2, double arg3, double arg4, double arg5) {
        CLIJxWeka2 result = TrainWekaFromTable.trainWekaFromTable(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return result;
    }

    /**
     * Trains a Weka model using functionality of Fijis Trainable Weka Segmentation plugin. 
     * 
     * It takes the given Results Table, sorts its columns alphabetically as extracted features (rows correspond to feature vectors) and a given column name containing the ground truth to train a Weka model. This model will be saved to disc.
     * The given groundTruth column is supposed to be numeric with values 1 represent class 1,  value 2 represent class 2 and so on. Value 0 will be ignored for training.
     * 
     * Default values for options are:
     * * trees = 200
     * * features = 2
     * * maxDepth = 0
     */
    public static CLIJxWeka2 trainWekaFromTable(ResultsTable arg1, String arg2, String arg3, double arg4, double arg5, double arg6) {
        CLIJxWeka2 result = TrainWekaFromTable.trainWekaFromTable(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).intValue(), new Double (arg5).intValue(), new Double (arg6).intValue());
        return result;
    }


    // net.haesleinhuepf.clijx.weka.TrainWekaModelWithOptions
    //----------------------------------------------------
    /**
     * Trains a Weka model using functionality of Fijis Trainable Weka Segmentation plugin. 
     * 
     * It takes a 3D feature stack (e.g. first plane original image, second plane blurred, third plane edge image)and trains a Weka model. This model will be saved to disc.
     * The given groundTruth image is supposed to be a label map where pixels with value 1 represent class 1, pixels with value 2 represent class 2 and so on. Pixels with value 0 will be ignored for training.
     * 
     * Default values for options are:
     * * trees = 200
     * * features = 2
     * * maxDepth = 0
     */
    public static ClearCLBuffer trainWekaModelWithOptions(ClearCLBuffer arg1, ClearCLBuffer arg2, String arg3, double arg4, double arg5, double arg6) {
        TrainWekaModelWithOptions.trainWekaModelWithOptions(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).intValue(), new Double (arg5).intValue(), new Double (arg6).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clijx.plugins.StartContinuousWebcamAcquisition
    //----------------------------------------------------
    /**
     * Starts acquistion of images from a webcam.
     */
    public static boolean startContinuousWebcamAcquisition(double arg1, double arg2, double arg3) {
        boolean result = StartContinuousWebcamAcquisition.startContinuousWebcamAcquisition(getCLIJx(), new Double (arg1).intValue(), new Double (arg2).intValue(), new Double (arg3).intValue());
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.StopContinuousWebcamAcquisition
    //----------------------------------------------------
    /**
     * Stops continous acquistion from a webcam.
     */
    public static boolean stopContinuousWebcamAcquisition(double arg1) {
        boolean result = StopContinuousWebcamAcquisition.stopContinuousWebcamAcquisition(getCLIJx(), new Double (arg1).intValue());
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.CaptureWebcamImage
    //----------------------------------------------------
    /**
     * Acquires an image (in fact an RGB image stack with three slices) of given size using a webcam. 
     * 
     * It uses the webcam-capture library by Bartosz Firyn.https://github.com/sarxos/webcam-capture
     */
    public static ClearCLBuffer captureWebcamImage(ClearCLBuffer arg1, double arg2, double arg3, double arg4) {
        CaptureWebcamImage.captureWebcamImage(getCLIJx(), arg1, new Double (arg2).intValue(), new Double (arg3).intValue(), new Double (arg4).intValue());
        return arg1;
    }


    // net.haesleinhuepf.clijx.plugins.ConvertRGBStackToGraySlice
    //----------------------------------------------------
    /**
     * Converts a three channel image (stack with three slices) to a single channel image (2D image) by multiplying with factors 0.299, 0.587, 0.114.
     */
    public static ClearCLBuffer convertRGBStackToGraySlice(ClearCLBuffer stack_source, ClearCLBuffer slice_destination) {
        ConvertRGBStackToGraySlice.convertRGBStackToGraySlice(getCLIJx(), stack_source, slice_destination);
        return slice_destination;
    }


    // net.haesleinhuepf.clij2.plugins.PullLabelsToROIList
    //----------------------------------------------------
    /**
     * Pulls all labels in a label map as ROIs to a list. 
     * 
     * From ImageJ macro this list is written to the log 
     * window. From ImageJ macro conside using pullLabelsToROIManager.
     */
    public static ClearCLBuffer pullLabelsToROIList(ClearCLBuffer arg1, List arg2) {
        PullLabelsToROIList.pullLabelsToROIList(getCLIJ2(), arg1, arg2);
        return arg1;
    }

    /**
     * Pulls all labels in a label map as ROIs to a list. 
     * 
     * From ImageJ macro this list is written to the log 
     * window. From ImageJ macro conside using pullLabelsToROIManager.
     */
    public static ClearCLBuffer pullLabelsToROIList(ClearCLBuffer labelmap_input) {
        PullLabelsToROIList.pullLabelsToROIList(getCLIJ2(), labelmap_input);
        return labelmap_input;
    }


    // net.haesleinhuepf.clij2.plugins.MeanOfTouchingNeighbors
    //----------------------------------------------------
    /**
     * Takes a touch matrix and a vector of values to determine the mean value among touching neighbors for every object. 
     * 
     * Parameters
     * ----------
     * values : Image
     *     A vector of values corresponding to the labels of which the mean average should be determined.
     * touch_matrix : Image
     *     A touch_matrix specifying which labels are taken into account for neighborhood relationships.
     * mean_values_destination : Image
     *     A the resulting vector of mean average values in the neighborhood.
     * 
     */
    public static ClearCLBuffer meanOfTouchingNeighbors(ClearCLBuffer values, ClearCLBuffer touch_matrix, ClearCLBuffer mean_values_destination) {
        MeanOfTouchingNeighbors.meanOfTouchingNeighbors(getCLIJ2(), values, touch_matrix, mean_values_destination);
        return mean_values_destination;
    }


    // net.haesleinhuepf.clij2.plugins.MinimumOfTouchingNeighbors
    //----------------------------------------------------
    /**
     * Takes a touch matrix and a vector of values to determine the minimum value among touching neighbors for every object. 
     * 
     * Parameters
     * ----------
     * values : Image
     *     A vector of values corresponding to the labels of which the minimum should be determined.
     * touch_matrix : Image
     *     A touch_matrix specifying which labels are taken into account for neighborhood relationships.
     * minimum_values_destination : Image
     *     A the resulting vector of minimum values in the neighborhood.
     * 
     */
    public static ClearCLBuffer minimumOfTouchingNeighbors(ClearCLBuffer values, ClearCLBuffer touch_matrix, ClearCLBuffer minimum_values_destination) {
        MinimumOfTouchingNeighbors.minimumOfTouchingNeighbors(getCLIJ2(), values, touch_matrix, minimum_values_destination);
        return minimum_values_destination;
    }


    // net.haesleinhuepf.clij2.plugins.MaximumOfTouchingNeighbors
    //----------------------------------------------------
    /**
     * Takes a touch matrix and a vector of values to determine the maximum value among touching neighbors for every object. 
     * 
     * Parameters
     * ----------
     * values : Image
     *     A vector of values corresponding to the labels of which the maximum should be determined.
     * touch_matrix : Image
     *     A touch_matrix specifying which labels are taken into account for neighborhood relationships.
     * maximum_values_destination : Image
     *     A the resulting vector of maximum values in the neighborhood.
     * 
     */
    public static ClearCLBuffer maximumOfTouchingNeighbors(ClearCLBuffer values, ClearCLBuffer touch_matrix, ClearCLBuffer maximum_values_destination) {
        MaximumOfTouchingNeighbors.maximumOfTouchingNeighbors(getCLIJ2(), values, touch_matrix, maximum_values_destination);
        return maximum_values_destination;
    }


    // net.haesleinhuepf.clij2.plugins.ResultsTableColumnToImage
    //----------------------------------------------------
    /**
     * Converts a table column to an image. 
     * 
     * The values are stored in x dimension.
     */
    public static ClearCLBuffer resultsTableColumnToImage(ClearCLBuffer arg1, ResultsTable arg2, String arg3) {
        ResultsTableColumnToImage.resultsTableColumnToImage(getCLIJ2(), arg1, arg2, arg3);
        return arg1;
    }


    // net.haesleinhuepf.clij2.plugins.StatisticsOfBackgroundAndLabelledPixels
    //----------------------------------------------------
    /**
     * Determines bounding box, area (in pixels/voxels), min, max and mean intensity 
     *  of background and labelled objects in a label map and corresponding pixels in the original image.
     * 
     * Instead of a label map, you can also use a binary image as a binary image is a label map with just one label.
     * 
     * This method is executed on the CPU and not on the GPU/OpenCL device.
     */
    public static ClearCLBuffer statisticsOfBackgroundAndLabelledPixels(ClearCLBuffer input, ClearCLBuffer labelmap) {
        StatisticsOfBackgroundAndLabelledPixels.statisticsOfBackgroundAndLabelledPixels(getCLIJ2(), input, labelmap);
        return labelmap;
    }

    /**
     * Determines bounding box, area (in pixels/voxels), min, max and mean intensity 
     *  of background and labelled objects in a label map and corresponding pixels in the original image.
     * 
     * Instead of a label map, you can also use a binary image as a binary image is a label map with just one label.
     * 
     * This method is executed on the CPU and not on the GPU/OpenCL device.
     */
    public static ClearCLBuffer statisticsOfBackgroundAndLabelledPixels(ClearCLBuffer arg1, ClearCLBuffer arg2, ResultsTable arg3) {
        StatisticsOfBackgroundAndLabelledPixels.statisticsOfBackgroundAndLabelledPixels(getCLIJ2(), arg1, arg2, arg3);
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.GetGPUProperties
    //----------------------------------------------------

    // net.haesleinhuepf.clij2.plugins.GetSumOfAllPixels
    //----------------------------------------------------
    /**
     * Determines the sum of all pixels in a given image. 
     * 
     * It will be stored in the variable sum_of_all_pixels.
     */
    public static ClearCLImageInterface getSumOfAllPixels(ClearCLImageInterface arg1) {
        GetSumOfAllPixels.getSumOfAllPixels(getCLIJ2(), arg1);
        return arg1;
    }


    // net.haesleinhuepf.clij2.plugins.GetSorensenDiceCoefficient
    //----------------------------------------------------
    /**
     * Determines the overlap of two binary images using the Sorensen-Dice coefficent. 
     * 
     * A value of 0 suggests no overlap, 1 means perfect overlap.
     * The Sorensen-Dice coefficient is saved in the colum 'Sorensen_Dice_coefficient'.
     * Note that the Sorensen-Dice coefficient s can be calculated from the Jaccard index j using this formula:
     * <pre>s = f(j) = 2 j / (j + 1)</pre>
     */
    public static ClearCLBuffer getSorensenDiceCoefficient(ClearCLBuffer arg1, ClearCLBuffer arg2) {
        GetSorensenDiceCoefficient.getSorensenDiceCoefficient(getCLIJ2(), arg1, arg2);
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.GetMinimumOfAllPixels
    //----------------------------------------------------
    /**
     * Determines the minimum of all pixels in a given image. 
     * 
     * It will be stored in the variable minimum_of_all_pixels.
     */
    public static ClearCLImageInterface getMinimumOfAllPixels(ClearCLImageInterface arg1) {
        GetMinimumOfAllPixels.getMinimumOfAllPixels(getCLIJ2(), arg1);
        return arg1;
    }


    // net.haesleinhuepf.clij2.plugins.GetMaximumOfAllPixels
    //----------------------------------------------------
    /**
     * Determines the maximum of all pixels in a given image. 
     * 
     * It will be stored in the variable maximum_of_all_pixels.
     */
    public static ClearCLImageInterface getMaximumOfAllPixels(ClearCLImageInterface arg1) {
        GetMaximumOfAllPixels.getMaximumOfAllPixels(getCLIJ2(), arg1);
        return arg1;
    }


    // net.haesleinhuepf.clij2.plugins.GetMeanOfAllPixels
    //----------------------------------------------------
    /**
     * Determines the mean of all pixels in a given image. 
     * 
     * It will be stored in the variable mean_of_all_pixels.
     */
    public static ClearCLImageInterface getMeanOfAllPixels(ClearCLImageInterface arg1) {
        GetMeanOfAllPixels.getMeanOfAllPixels(getCLIJ2(), arg1);
        return arg1;
    }


    // net.haesleinhuepf.clij2.plugins.GetJaccardIndex
    //----------------------------------------------------
    /**
     * Determines the overlap of two binary images using the Jaccard index. 
     * 
     * A value of 0 suggests no overlap, 1 means perfect overlap.
     * The resulting Jaccard index is saved to the results table in the 'Jaccard_Index' column.
     * Note that the Sorensen-Dice coefficient can be calculated from the Jaccard index j using this formula:
     * <pre>s = f(j) = 2 j / (j + 1)</pre>
     */
    public static ClearCLBuffer getJaccardIndex(ClearCLBuffer arg1, ClearCLBuffer arg2) {
        GetJaccardIndex.getJaccardIndex(getCLIJ2(), arg1, arg2);
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.GetCenterOfMass
    //----------------------------------------------------
    /**
     * Determines the center of mass of an image or image stack.
     * 
     *  It writes the result in the variables
     *  centerOfMassX, centerOfMassY and centerOfMassZ.
     */
    public static ClearCLBuffer getCenterOfMass(ClearCLBuffer arg1) {
        GetCenterOfMass.getCenterOfMass(getCLIJ2(), arg1);
        return arg1;
    }


    // net.haesleinhuepf.clij2.plugins.GetBoundingBox
    //----------------------------------------------------
    /**
     * Determines the bounding box of all non-zero pixels in a binary image. 
     * 
     * If called from macro, the positions will be stored in the variables 'boundingBoxX', 'boundingBoxY', 'boundingBoxZ', 'boundingBoxWidth', 'boundingBoxHeight' and 'boundingBoxDepth'.In case of 2D images Z and depth will be zero.
     */
    public static ClearCLBuffer getBoundingBox(ClearCLBuffer arg1) {
        GetBoundingBox.getBoundingBox(getCLIJ2(), arg1);
        return arg1;
    }


    // net.haesleinhuepf.clij2.plugins.PushArray
    //----------------------------------------------------
    /**
     * Converts an array to a 3D image stack.
     */
    public static ClearCLBuffer pushArray(ClearCLBuffer arg1, Object arg2) {
        PushArray.pushArray(getCLIJ2(), arg1, arg2);
        return arg1;
    }

    /**
     * Converts an array to a 3D image stack.
     */
    public static ClearCLBuffer pushArray(float[] arg1, double arg2, double arg3, double arg4) {
        ClearCLBuffer result = PushArray.pushArray(getCLIJ2(), arg1, new Double (arg2).intValue(), new Double (arg3).intValue(), new Double (arg4).intValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.PullString
    //----------------------------------------------------
    /**
     * Writes an image into a string.
     */
    public static ClearCLImageInterface pullString(ClearCLImageInterface arg1) {
        PullString.pullString(getCLIJ2(), arg1);
        return arg1;
    }


    // net.haesleinhuepf.clij2.plugins.PushString
    //----------------------------------------------------
    /**
     * Converts an string to an image. 
     * 
     * The formatting works with double line breaks for slice switches, single line breaks for y swithces and 
     * spaces for x. For example this string is converted to an image with width=4, height=3 and depth=2:
     * 
     * 1 2 3 4
     * 5 6 7 8
     * 9 0 1 2
     * 
     * 3 4 5 6
     * 7 8 9 0
     * 1 2 3 4
     * 
     */
    public static ClearCLBuffer pushString(ClearCLBuffer arg1, String arg2) {
        PushString.pushString(getCLIJ2(), arg1, arg2);
        return arg1;
    }

    /**
     * Converts an string to an image. 
     * 
     * The formatting works with double line breaks for slice switches, single line breaks for y swithces and 
     * spaces for x. For example this string is converted to an image with width=4, height=3 and depth=2:
     * 
     * 1 2 3 4
     * 5 6 7 8
     * 9 0 1 2
     * 
     * 3 4 5 6
     * 7 8 9 0
     * 1 2 3 4
     * 
     */
    public static ClearCLBuffer pushString(String arg1) {
        ClearCLBuffer result = PushString.pushString(getCLIJ2(), arg1);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.MedianOfTouchingNeighbors
    //----------------------------------------------------
    /**
     * Takes a touch matrix and a vector of values to determine the median value among touching neighbors for every object. 
     * 
     * 
     */
    public static ClearCLBuffer medianOfTouchingNeighbors(ClearCLBuffer values, ClearCLBuffer touch_matrix, ClearCLBuffer median_values_destination) {
        MedianOfTouchingNeighbors.medianOfTouchingNeighbors(getCLIJ2(), values, touch_matrix, median_values_destination);
        return median_values_destination;
    }


    // net.haesleinhuepf.clij2.plugins.PushResultsTableColumn
    //----------------------------------------------------
    /**
     * Converts a table column to an image. 
     * 
     * The values are stored in x dimension.
     */
    public static ClearCLBuffer pushResultsTableColumn(ClearCLBuffer arg1, ResultsTable arg2, String arg3) {
        PushResultsTableColumn.pushResultsTableColumn(getCLIJ2(), arg1, arg2, arg3);
        return arg1;
    }


    // net.haesleinhuepf.clij2.plugins.PushResultsTable
    //----------------------------------------------------
    /**
     * Converts a table to an image. 
     * 
     * Rows stay rows, columns stay columns.
     */
    public static ClearCLBuffer pushResultsTable(ClearCLBuffer arg1, ResultsTable arg2) {
        PushResultsTable.pushResultsTable(getCLIJ2(), arg1, arg2);
        return arg1;
    }


    // net.haesleinhuepf.clij2.plugins.PullToResultsTable
    //----------------------------------------------------
    /**
     * Converts an image into a table.
     */
    public static ClearCLBuffer pullToResultsTable(ClearCLBuffer arg1, ResultsTable arg2) {
        PullToResultsTable.pullToResultsTable(getCLIJ2(), arg1, arg2);
        return arg1;
    }

    /**
     * Converts an image into a table.
     */
    public static ClearCLImage pullToResultsTable(ClearCLImage arg1, ResultsTable arg2) {
        PullToResultsTable.pullToResultsTable(getCLIJ2(), arg1, arg2);
        return arg1;
    }


    // net.haesleinhuepf.clij2.plugins.LabelVoronoiOctagon
    //----------------------------------------------------
    /**
     * Takes a labeled image and dilates the labels using a octagon shape until they touch. 
     * 
     * The pixels where  the regions touched are afterwards returned as binary image which corresponds to the Voronoi diagram.
     */
    public static ClearCLBuffer labelVoronoiOctagon(ClearCLBuffer label_map, ClearCLBuffer label_voronoi_destination) {
        LabelVoronoiOctagon.labelVoronoiOctagon(getCLIJ2(), label_map, label_voronoi_destination);
        return label_voronoi_destination;
    }


    // net.haesleinhuepf.clij2.plugins.TouchMatrixToAdjacencyMatrix
    //----------------------------------------------------
    /**
     * Converts a touch matrix in an adjacency matrix
     */
    public static ClearCLBuffer touchMatrixToAdjacencyMatrix(ClearCLBuffer touch_matrix, ClearCLBuffer adjacency_matrix) {
        TouchMatrixToAdjacencyMatrix.touchMatrixToAdjacencyMatrix(getCLIJ2(), touch_matrix, adjacency_matrix);
        return adjacency_matrix;
    }


    // net.haesleinhuepf.clij2.plugins.AdjacencyMatrixToTouchMatrix
    //----------------------------------------------------
    /**
     * Converts a adjacency matrix in a touch matrix.
     * 
     * An adjacency matrix is symmetric while a touch matrix is typically not.
     * 
     * Parameters
     * ----------
     * adjacency_matrix : Image
     *     The input adjacency matrix to be read from.
     * touch_matrix : Image
     *     The output touch matrix to be written into.
     * 
     */
    public static ClearCLBuffer adjacencyMatrixToTouchMatrix(ClearCLBuffer adjacency_matrix, ClearCLBuffer touch_matrix) {
        AdjacencyMatrixToTouchMatrix.adjacencyMatrixToTouchMatrix(getCLIJ2(), adjacency_matrix, touch_matrix);
        return touch_matrix;
    }


    // net.haesleinhuepf.clij2.plugins.PointlistToLabelledSpots
    //----------------------------------------------------
    /**
     * Takes a pointlist with dimensions n times d with n point coordinates in d dimensions and labels corresponding pixels.
     */
    public static ClearCLBuffer pointlistToLabelledSpots(ClearCLBuffer pointlist, ClearCLBuffer spots_destination) {
        PointlistToLabelledSpots.pointlistToLabelledSpots(getCLIJ2(), pointlist, spots_destination);
        return spots_destination;
    }


    // net.haesleinhuepf.clij2.plugins.StatisticsOfImage
    //----------------------------------------------------
    /**
     * Determines image size (bounding box), area (in pixels/voxels), min, max and mean intensity 
     *  of all pixels in the original image.
     * 
     * This method is executed on the CPU and not on the GPU/OpenCL device.
     */
    public static ClearCLBuffer statisticsOfImage(ClearCLBuffer arg1, ResultsTable arg2) {
        StatisticsOfImage.statisticsOfImage(getCLIJ2(), arg1, arg2);
        return arg1;
    }


    // net.haesleinhuepf.clij2.plugins.NClosestDistances
    //----------------------------------------------------
    /**
     * Determine the n point indices with shortest distance for all points in a distance matrix. 
     * 
     * This corresponds to the n row indices with minimum values for each column of the distance matrix.Returns the n shortest distances in one image and the point indices in another image.
     */
    public static ClearCLBuffer nClosestDistances(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3) {
        NClosestDistances.nClosestDistances(getCLIJ2(), arg1, arg2, arg3);
        return arg3;
    }


    // net.haesleinhuepf.clij2.plugins.ExcludeLabels
    //----------------------------------------------------
    /**
     * This operation removes labels from a labelmap and renumbers the remaining labels. 
     * 
     * Hand over a binary flag list vector starting with a flag for the background, continuing with label1, label2, ...
     * 
     * For example if you pass 0,1,0,0,1: Labels 1 and 4 will be removed (those with a 1 in the vector will be excluded). Labels 2 and 3 will be kept and renumbered to 1 and 2.
     */
    public static ClearCLBuffer excludeLabels(ClearCLBuffer binary_flaglist, ClearCLBuffer label_map_input, ClearCLBuffer label_map_destination) {
        ExcludeLabels.excludeLabels(getCLIJ2(), binary_flaglist, label_map_input, label_map_destination);
        return label_map_destination;
    }


    // net.haesleinhuepf.clij2.plugins.AverageDistanceOfNFarOffPoints
    //----------------------------------------------------
    /**
     * Determines the average of the n far off (most distant) points for every point in a distance matrix.
     * 
     * This corresponds to the average of the n maximum values (rows) for each column of the distance matrix.
     * 
     * Parameters
     * ----------
     * distance_matrix : Image
     *     The a distance matrix to be processed.
     * distance_list_destination : Image
     *     A vector image with the same width as the distance matrix and height=1, depth=1.
     *     Determined average distances will be written into this vector.
     * n_far_off_points_to_find : Number
     *     Number of largest distances which should be averaged.
     * 
     */
    public static ClearCLBuffer averageDistanceOfNFarOffPoints(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        AverageDistanceOfNFarOffPoints.averageDistanceOfNFarOffPoints(getCLIJ2(), arg1, arg2, new Double (arg3).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.StandardDeviationOfTouchingNeighbors
    //----------------------------------------------------
    /**
     * Takes a touch matrix and a vector of values to determine the standard deviation value among touching neighbors for every object. 
     * 
     * 
     */
    public static ClearCLBuffer standardDeviationOfTouchingNeighbors(ClearCLBuffer values, ClearCLBuffer touch_matrix, ClearCLBuffer standard_deviation_values_destination) {
        StandardDeviationOfTouchingNeighbors.standardDeviationOfTouchingNeighbors(getCLIJ2(), values, touch_matrix, standard_deviation_values_destination);
        return standard_deviation_values_destination;
    }


    // net.haesleinhuepf.clij2.plugins.NeighborsOfNeighbors
    //----------------------------------------------------
    /**
     * Determines neighbors of neigbors from touch matrix and saves the result as a new touch matrix.
     */
    public static ClearCLBuffer neighborsOfNeighbors(ClearCLBuffer touch_matrix, ClearCLBuffer neighbor_matrix_destination) {
        NeighborsOfNeighbors.neighborsOfNeighbors(getCLIJ2(), touch_matrix, neighbor_matrix_destination);
        return neighbor_matrix_destination;
    }


    // net.haesleinhuepf.clij2.plugins.GenerateParametricImage
    //----------------------------------------------------
    /**
     * Take a labelmap and a vector of values to replace label 1 with the 1st value in the vector. 
     * 
     * Note that indexing in the vector starts at zero. The 0th entry corresponds to background in the label map.Internally this method just calls ReplaceIntensities.
     * 
     */
    public static ClearCLImageInterface generateParametricImage(ClearCLImageInterface label_map, ClearCLImageInterface parameter_value_vector, ClearCLImageInterface parametric_image_destination) {
        GenerateParametricImage.generateParametricImage(getCLIJ2(), label_map, parameter_value_vector, parametric_image_destination);
        return parametric_image_destination;
    }


    // net.haesleinhuepf.clij2.plugins.GenerateParametricImageFromResultsTableColumn
    //----------------------------------------------------
    /**
     * Take a labelmap and a column from the results table to replace label 1 with the 1st value in the vector. 
     * 
     * Note that indexing in the table column starts at zero. The results table should contain a line at the beginningrepresenting the background.
     * 
     */
    public static ClearCLImageInterface generateParametricImageFromResultsTableColumn(ClearCLImageInterface arg1, ClearCLImageInterface arg2, ResultsTable arg3, String arg4) {
        GenerateParametricImageFromResultsTableColumn.generateParametricImageFromResultsTableColumn(getCLIJ2(), arg1, arg2, arg3, arg4);
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.ExcludeLabelsWithValuesOutOfRange
    //----------------------------------------------------
    /**
     * This operation removes labels from a labelmap and renumbers the remaining labels. 
     * 
     * Hand over a vector of values and a range specifying which labels with which values are eliminated.
     */
    public static ClearCLBuffer excludeLabelsWithValuesOutOfRange(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4, double arg5) {
        ExcludeLabelsWithValuesOutOfRange.excludeLabelsWithValuesOutOfRange(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).floatValue(), new Double (arg5).floatValue());
        return arg3;
    }


    // net.haesleinhuepf.clij2.plugins.ExcludeLabelsWithValuesWithinRange
    //----------------------------------------------------
    /**
     * This operation removes labels from a labelmap and renumbers the remaining labels. 
     * 
     * Hand over a vector of values and a range specifying which labels with which values are eliminated.
     */
    public static ClearCLBuffer excludeLabelsWithValuesWithinRange(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4, double arg5) {
        ExcludeLabelsWithValuesWithinRange.excludeLabelsWithValuesWithinRange(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).floatValue(), new Double (arg5).floatValue());
        return arg3;
    }


    // net.haesleinhuepf.clij2.plugins.CombineVertically
    //----------------------------------------------------
    /**
     * Combines two images or stacks in Y.
     */
    public static ClearCLImageInterface combineVertically(ClearCLImageInterface stack1, ClearCLImageInterface stack2, ClearCLImageInterface destination) {
        CombineVertically.combineVertically(getCLIJ2(), stack1, stack2, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.CombineHorizontally
    //----------------------------------------------------
    /**
     * Combines two images or stacks in X.
     */
    public static ClearCLImageInterface combineHorizontally(ClearCLImageInterface stack1, ClearCLImageInterface stack2, ClearCLImageInterface destination) {
        CombineHorizontally.combineHorizontally(getCLIJ2(), stack1, stack2, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.ReduceStack
    //----------------------------------------------------
    /**
     * Reduces the number of slices in a stack by a given factor.
     * With the offset you have control which slices stay: 
     * * With factor 3 and offset 0, slices 0, 3, 6,... are kept. * With factor 4 and offset 1, slices 1, 5, 9,... are kept.
     */
    public static ClearCLImageInterface reduceStack(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        ReduceStack.reduceStack(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.DetectMinima2DBox
    //----------------------------------------------------
    /**
     * Detects local minima in a given square/cubic neighborhood. 
     * 
     * Pixels in the resulting image are set to 1 if there is no other pixel in a given radius which has a 
     * lower intensity, and to 0 otherwise.
     */
    public static ClearCLBuffer detectMinima2DBox(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4) {
        DetectMinima2DBox.detectMinima2DBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.DetectMaxima2DBox
    //----------------------------------------------------
    /**
     * Detects local maxima in a given square/cubic neighborhood. 
     * 
     * Pixels in the resulting image are set to 1 if there is no other pixel in a given radius which has a 
     * higher intensity, and to 0 otherwise.
     */
    public static ClearCLBuffer detectMaxima2DBox(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4) {
        DetectMaxima2DBox.detectMaxima2DBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.DetectMinima3DBox
    //----------------------------------------------------
    /**
     * Detects local minima in a given square/cubic neighborhood. 
     * 
     * Pixels in the resulting image are set to 1 if there is no other pixel in a given radius which has a 
     * lower intensity, and to 0 otherwise.
     */
    public static ClearCLBuffer detectMinima3DBox(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5) {
        DetectMinima3DBox.detectMinima3DBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.DetectMaxima3DBox
    //----------------------------------------------------
    /**
     * Detects local maxima in a given square/cubic neighborhood. 
     * 
     * Pixels in the resulting image are set to 1 if there is no other pixel in a given radius which has a 
     * higher intensity, and to 0 otherwise.
     */
    public static ClearCLBuffer detectMaxima3DBox(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5) {
        DetectMaxima3DBox.detectMaxima3DBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.DepthColorProjection
    //----------------------------------------------------
    /**
     * Determines a maximum projection of an image stack and does a color coding of the determined arg Z (position of the found maximum). 
     * 
     * Second parameter is a Lookup-Table in the form of an 8-bit image stack 255 pixels wide, 1 pixel high with 3 planes representing red, green and blue intensities.
     * Resulting image is a 3D image with three Z-planes representing red, green and blue channels.
     */
    public static ClearCLBuffer depthColorProjection(ClearCLImageInterface arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4, double arg5) {
        DepthColorProjection.depthColorProjection(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).floatValue(), new Double (arg5).floatValue());
        return arg3;
    }


    // net.haesleinhuepf.clij2.plugins.GenerateBinaryOverlapMatrix
    //----------------------------------------------------
    /**
     * Takes two labelmaps with n and m labels and generates a (n+1)*(m+1) matrix where all pixels are set to 0 exept those where labels overlap between the label maps. 
     * 
     * For example, if labels 3 in labelmap1 and 4 in labelmap2 are touching then the pixel (3,4) in the matrix will be set to 1.
     */
    public static ClearCLBuffer generateBinaryOverlapMatrix(ClearCLBuffer label_map1, ClearCLBuffer label_map2, ClearCLBuffer binary_overlap_matrix_destination) {
        GenerateBinaryOverlapMatrix.generateBinaryOverlapMatrix(getCLIJ2(), label_map1, label_map2, binary_overlap_matrix_destination);
        return binary_overlap_matrix_destination;
    }


    // net.haesleinhuepf.clij2.plugins.ResliceRadialTop
    //----------------------------------------------------

    // net.haesleinhuepf.clij2.plugins.Convolve
    //----------------------------------------------------
    /**
     * Convolve the image with a given kernel image.
     * 
     * It is recommended that the kernel image has an odd size in X, Y and Z.
     */
    public static ClearCLBuffer convolve(ClearCLBuffer source, ClearCLBuffer convolution_kernel, ClearCLBuffer destination) {
        Convolve.convolve(getCLIJ2(), source, convolution_kernel, destination);
        return destination;
    }


    // net.haesleinhuepf.clijx.plugins.NonLocalMeans
    //----------------------------------------------------
    /**
     * Applies a non-local means filter using a box neighborhood with a Gaussian weight specified with sigma to the input image.
     */
    public static ClearCLBuffer nonLocalMeans(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, double arg6) {
        NonLocalMeans.nonLocalMeans(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue(), new Double (arg6).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clijx.plugins.Bilateral
    //----------------------------------------------------

    // net.haesleinhuepf.clij2.plugins.UndefinedToZero
    //----------------------------------------------------
    /**
     * Copies all pixels instead those which are not a number (NaN) or infinity (inf), which are replaced by 0.
     */
    public static ClearCLBuffer undefinedToZero(ClearCLBuffer source, ClearCLBuffer destination) {
        UndefinedToZero.undefinedToZero(getCLIJ2(), source, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.GenerateJaccardIndexMatrix
    //----------------------------------------------------
    /**
     * Takes two labelmaps with n and m labels_2 and generates a (n+1)*(m+1) matrix where all labels_1 are set to 0 exept those where labels_2 overlap between the label maps. 
     * 
     * For the remaining labels_1, the value will be between 0 and 1 indicating the overlap as measured by the Jaccard Index.
     * Major parts of this operation run on the CPU.
     */
    public static ClearCLBuffer generateJaccardIndexMatrix(ClearCLBuffer label_map1, ClearCLBuffer label_map2, ClearCLBuffer jaccard_index_matrix_destination) {
        GenerateJaccardIndexMatrix.generateJaccardIndexMatrix(getCLIJ2(), label_map1, label_map2, jaccard_index_matrix_destination);
        return jaccard_index_matrix_destination;
    }


    // net.haesleinhuepf.clij2.plugins.GenerateTouchCountMatrix
    //----------------------------------------------------
    /**
     * Takes a label map with n labels and generates a (n+1)*(n+1) matrix where all pixels are set the number of pixels where labels touch (diamond neighborhood). 
     * 
     * Major parts of this operation run on the CPU.
     */
    public static ClearCLBuffer generateTouchCountMatrix(ClearCLBuffer label_map, ClearCLBuffer touch_count_matrix_destination) {
        GenerateTouchCountMatrix.generateTouchCountMatrix(getCLIJ2(), label_map, touch_count_matrix_destination);
        return touch_count_matrix_destination;
    }


    // net.haesleinhuepf.clij2.plugins.MinimumXProjection
    //----------------------------------------------------
    /**
     * Determines the minimum intensity projection of an image along Y.
     */
    public static ClearCLImageInterface minimumXProjection(ClearCLImageInterface source, ClearCLImageInterface destination_min) {
        MinimumXProjection.minimumXProjection(getCLIJ2(), source, destination_min);
        return destination_min;
    }


    // net.haesleinhuepf.clij2.plugins.MinimumYProjection
    //----------------------------------------------------
    /**
     * Determines the minimum intensity projection of an image along Y.
     */
    public static ClearCLImageInterface minimumYProjection(ClearCLImageInterface source, ClearCLImageInterface destination_min) {
        MinimumYProjection.minimumYProjection(getCLIJ2(), source, destination_min);
        return destination_min;
    }


    // net.haesleinhuepf.clij2.plugins.MeanXProjection
    //----------------------------------------------------
    /**
     * Determines the mean average intensity projection of an image along X.
     */
    public static ClearCLImageInterface meanXProjection(ClearCLImageInterface source, ClearCLImageInterface destination) {
        MeanXProjection.meanXProjection(getCLIJ2(), source, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.MeanYProjection
    //----------------------------------------------------
    /**
     * Determines the mean average intensity projection of an image along Y.
     */
    public static ClearCLImageInterface meanYProjection(ClearCLImageInterface source, ClearCLImageInterface destination) {
        MeanYProjection.meanYProjection(getCLIJ2(), source, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.SquaredDifference
    //----------------------------------------------------
    /**
     * Determines the squared difference pixel by pixel between two images.
     */
    public static ClearCLBuffer squaredDifference(ClearCLBuffer source1, ClearCLBuffer source2, ClearCLBuffer destination) {
        SquaredDifference.squaredDifference(getCLIJ2(), source1, source2, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.AbsoluteDifference
    //----------------------------------------------------
    /**
     * Determines the absolute difference pixel by pixel between two images.
     * 
     * <pre>f(x, y) = |x - y| </pre>
     * 
     * Parameters
     * ----------
     * source1 : Image
     *     The input image to be subtracted from.
     * source2 : Image
     *     The input image which is subtracted.
     * destination : Image
     *     The output image  where results are written into.
     * 
     */
    public static ClearCLBuffer absoluteDifference(ClearCLBuffer source1, ClearCLBuffer source2, ClearCLBuffer destination) {
        AbsoluteDifference.absoluteDifference(getCLIJ2(), source1, source2, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.ReplacePixelsIfZero
    //----------------------------------------------------
    /**
     * Replaces pixel values x with y in case x is zero.
     * 
     * This functionality is comparable to ImageJs image calculator operator 'transparent zero'.
     */
    public static ClearCLImageInterface replacePixelsIfZero(ClearCLImageInterface input1, ClearCLImageInterface input2, ClearCLImageInterface destination) {
        ReplacePixelsIfZero.replacePixelsIfZero(getCLIJ2(), input1, input2, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.VoronoiLabeling
    //----------------------------------------------------
    /**
     * Takes a binary image, labels connected components and dilates the regions using a octagon shape until they touch. 
     * 
     * The resulting label map is written to the output.
     */
    public static ClearCLImageInterface voronoiLabeling(ClearCLBuffer input, ClearCLImageInterface destination) {
        VoronoiLabeling.voronoiLabeling(getCLIJ2(), input, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.ExtendLabelingViaVoronoi
    //----------------------------------------------------
    /**
     * Takes a label map image and dilates the regions using a octagon shape until they touch. 
     * 
     * The resulting label map is written to the output.
     */
    public static ClearCLImageInterface extendLabelingViaVoronoi(ClearCLBuffer input, ClearCLImageInterface destination) {
        ExtendLabelingViaVoronoi.extendLabelingViaVoronoi(getCLIJ2(), input, destination);
        return destination;
    }


    // net.haesleinhuepf.clijx.plugins.FindMaxima
    //----------------------------------------------------
    /**
     * Finds and labels local maxima with neighboring maxima and background above a given tolerance threshold.
     * 
     * 
     */
    public static ClearCLBuffer findMaxima(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        FindMaxima.findMaxima(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return arg2;
    }

    /**
     * 
     */
    public static ClearCLBuffer mergeTouchingLabelsSpecial(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, ClearCLBuffer arg4) {
        FindMaxima.mergeTouchingLabelsSpecial(getCLIJ2(), arg1, arg2, arg3, arg4);
        return arg4;
    }


    // net.haesleinhuepf.clij2.plugins.MergeTouchingLabels
    //----------------------------------------------------
    /**
     * 
     */
    public static ClearCLBuffer mergeTouchingLabels(ClearCLBuffer source, ClearCLBuffer destination) {
        MergeTouchingLabels.mergeTouchingLabels(getCLIJ2(), source, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.AverageNeighborDistanceMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines which labels touch and replaces every label with the average distance to their neighboring labels.
     * 
     * To determine the distances, the centroid of the labels is determined internally.
     */
    public static ClearCLBuffer averageNeighborDistanceMap(ClearCLBuffer input, ClearCLBuffer destination) {
        AverageNeighborDistanceMap.averageNeighborDistanceMap(getCLIJ2(), input, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.CylinderTransform
    //----------------------------------------------------
    /**
     * Applies a cylinder transform to an image stack assuming the center line goes in Y direction in the center of the stack.
     * 
     * This transforms an image stack from an XYZ coordinate system to a AYD coordinate system with 
     * A the angle around the center line, 
     * Y the original Y coordinate and 
     * D, the distance from the center.
     * 
     * Thus, going in virtual Z direction (actually D) in the resulting stack, you go from the center to theperiphery.
     */
    public static ClearCLBuffer cylinderTransform(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, double arg6) {
        CylinderTransform.cylinderTransform(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), new Double (arg6).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clijx.plugins.DetectAndLabelMaxima
    //----------------------------------------------------
    /**
     * Determines maximum regions in a Gaussian blurred version of the original image.
     * 
     * The regions do not not necessarily have to be single pixels. 
     * It is also possible to invert the image before determining the maxima.
     */
    public static ClearCLBuffer detectAndLabelMaxima(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, boolean arg6) {
        DetectAndLabelMaxima.detectAndLabelMaxima(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), arg6);
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.DrawDistanceMeshBetweenTouchingLabels
    //----------------------------------------------------
    /**
     * Starting from a label map, draw lines between touching neighbors resulting in a mesh.
     * 
     * The end points of the lines correspond to the centroids of the labels. The intensity of the lines 
     * corresponds to the distance between these labels (in pixels or voxels).
     */
    public static ClearCLBuffer drawDistanceMeshBetweenTouchingLabels(ClearCLBuffer input, ClearCLBuffer destination) {
        DrawDistanceMeshBetweenTouchingLabels.drawDistanceMeshBetweenTouchingLabels(getCLIJ2(), input, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.DrawMeshBetweenTouchingLabels
    //----------------------------------------------------
    /**
     * Starting from a label map, draw lines between touching neighbors resulting in a mesh.
     * 
     * The end points of the lines correspond to the centroids of the labels. 
     */
    public static ClearCLBuffer drawMeshBetweenTouchingLabels(ClearCLBuffer input, ClearCLBuffer destination) {
        DrawMeshBetweenTouchingLabels.drawMeshBetweenTouchingLabels(getCLIJ2(), input, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.ExcludeLabelsOutsideSizeRange
    //----------------------------------------------------
    /**
     * Removes labels from a label map which are not within a certain size range.
     * 
     * Size of the labels is given as the number of pixel or voxels per label.
     */
    public static ClearCLBuffer excludeLabelsOutsideSizeRange(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4) {
        ExcludeLabelsOutsideSizeRange.excludeLabelsOutsideSizeRange(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.DilateLabels
    //----------------------------------------------------
    /**
     * Extend labels with a given radius.
     * 
     * This is actually a local maximum filter applied to a label map which does not overwrite labels.
     * It is recommended to apply this operation to isotropic images only.
     */
    public static ClearCLBuffer dilateLabels(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        DilateLabels.dilateLabels(getCLIJ2(), arg1, arg2, new Double (arg3).intValue());
        return arg2;
    }

    /**
     * 
     */
    public static ClearCLBuffer extendLabelsWithMaximumRadius(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        DilateLabels.extendLabelsWithMaximumRadius(getCLIJ2(), arg1, arg2, new Double (arg3).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clijx.plugins.FindAndLabelMaxima
    //----------------------------------------------------
    /**
     * Determine maxima with a given tolerance to surrounding maxima and background and label them.
     */
    public static ClearCLBuffer findAndLabelMaxima(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, boolean arg4) {
        FindAndLabelMaxima.findAndLabelMaxima(getCLIJx(), arg1, arg2, new Double (arg3).floatValue(), arg4);
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.MakeIsotropic
    //----------------------------------------------------
    /**
     * Applies a scaling operation using linear interpolation to generate an image stack with a given isotropic voxel size.
     */
    public static ClearCLBuffer makeIsotropic(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, double arg6) {
        MakeIsotropic.makeIsotropic(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), new Double (arg6).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.TouchingNeighborCountMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines which labels touch and replaces every label with the number of touching neighbor labels.
     * 
     * 
     */
    public static ClearCLBuffer touchingNeighborCountMap(ClearCLBuffer input, ClearCLBuffer destination) {
        TouchingNeighborCountMap.touchingNeighborCountMap(getCLIJ2(), input, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.RigidTransform
    //----------------------------------------------------
    /**
     * Applies a rigid transform using linear interpolation to an image stack.
     */
    public static ClearCLBuffer rigidTransform(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8) {
        RigidTransform.rigidTransform(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), new Double (arg6).floatValue(), new Double (arg7).floatValue(), new Double (arg8).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.SphereTransform
    //----------------------------------------------------
    /**
     * Turns an image stack in XYZ cartesian coordinate system to an AID polar coordinate system.
     * 
     * A corresponds to azimut,I to inclination and D to the distance from the center.Thus, going in virtual Z direction (actually D) in the resulting stack, you go from the center to theperiphery.
     */
    public static ClearCLBuffer sphereTransform(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, double arg6, double arg7) {
        SphereTransform.sphereTransform(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), new Double (arg6).floatValue(), new Double (arg7).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clijx.plugins.SubtractGaussianBackground
    //----------------------------------------------------
    /**
     * Applies Gaussian blur to the input image and subtracts the result from the original image.
     * 
     * Deprecated: Use differenceOfGaussian() instead.
     */
    public static ClearCLImageInterface subtractGaussianBackground(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        SubtractGaussianBackground.subtractGaussianBackground(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clijx.plugins.ThresholdDoG
    //----------------------------------------------------
    /**
     * Applies a Difference-of-Gaussian filter to an image and thresholds it with given sigma and threshold values.
     */
    public static ClearCLBuffer thresholdDoG(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, boolean arg6) {
        ThresholdDoG.thresholdDoG(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), arg6);
        return arg2;
    }


    // net.haesleinhuepf.clijx.plugins.DriftCorrectionByCenterOfMassFixation
    //----------------------------------------------------
    /**
     * Determines the centerOfMass of the image stack and translates it so that it stays in a defined position.
     */
    public static ClearCLBuffer driftCorrectionByCenterOfMassFixation(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5) {
        DriftCorrectionByCenterOfMassFixation.driftCorrectionByCenterOfMassFixation(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clijx.plugins.DriftCorrectionByCentroidFixation
    //----------------------------------------------------
    /**
     * Threshold the image stack, determines the centroid of the resulting binary image and 
     * translates the image stack so that its centroid sits in a defined position.
     */
    public static ClearCLBuffer driftCorrectionByCentroidFixation(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, double arg6) {
        DriftCorrectionByCentroidFixation.driftCorrectionByCentroidFixation(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), new Double (arg6).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clijx.plugins.IntensityCorrection
    //----------------------------------------------------
    /**
     * Determines the mean intensity of the image stack and multiplies it with a factor so that the mean intensity becomes equal to a given value.
     */
    public static ClearCLBuffer intensityCorrection(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        IntensityCorrection.intensityCorrection(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clijx.plugins.IntensityCorrectionAboveThresholdOtsu
    //----------------------------------------------------
    /**
     * Determines the mean intensity of all pixel the image stack which are above a determined Threshold (Otsu et al. 1979) and multiplies it with a factor so that the mean intensity becomes equal to a given value.
     */
    public static ClearCLBuffer intensityCorrectionAboveThresholdOtsu(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        IntensityCorrectionAboveThresholdOtsu.intensityCorrectionAboveThresholdOtsu(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.MeanIntensityMap
    //----------------------------------------------------
    /**
     * Takes an image and a corresponding label map, determines the mean intensity per label and replaces every label with the that number.
     * 
     * This results in a parametric image expressing mean object intensity.
     */
    public static ClearCLBuffer meanIntensityMap(ClearCLBuffer intensity_image, ClearCLBuffer label_map, ClearCLBuffer destination) {
        MeanIntensityMap.meanIntensityMap(getCLIJ2(), intensity_image, label_map, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.StandardDeviationIntensityMap
    //----------------------------------------------------
    /**
     * Takes an image and a corresponding label map, determines the standard deviation of the intensity per label and replaces every label with the that number.
     * 
     * This results in a parametric image expressing standard deviation of object intensity.
     */
    public static ClearCLBuffer standardDeviationIntensityMap(ClearCLBuffer intensity_image, ClearCLBuffer label_map, ClearCLBuffer destination) {
        StandardDeviationIntensityMap.standardDeviationIntensityMap(getCLIJ2(), intensity_image, label_map, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.PixelCountMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines the number of pixels per label and replaces every label with the that number.
     * 
     * This results in a parametric image expressing area or volume.
     */
    public static ClearCLBuffer pixelCountMap(ClearCLBuffer input, ClearCLBuffer destination) {
        PixelCountMap.pixelCountMap(getCLIJ2(), input, destination);
        return destination;
    }


    // net.haesleinhuepf.clijx.plugins.ParametricWatershed
    //----------------------------------------------------
    /**
     * Apply a binary watershed to a binary image and introduce black pixels between objects.
     * 
     * To have control about where objects are cut, the sigma parameters allow to control a Gaussian blur filter applied to the internally used distance map.
     */
    public static ClearCLBuffer parametricWatershed(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5) {
        ParametricWatershed.parametricWatershed(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clijx.plugins.MeanZProjectionAboveThreshold
    //----------------------------------------------------
    /**
     * Determines the mean average intensity projection of an image along Z but only for pixels above a given threshold.
     */
    public static ClearCLImageInterface meanZProjectionAboveThreshold(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3) {
        MeanZProjectionAboveThreshold.meanZProjectionAboveThreshold(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.CentroidsOfBackgroundAndLabels
    //----------------------------------------------------
    /**
     * Determines the centroids of the background and all labels in a label image or image stack. 
     * 
     * It writes the resulting  coordinates in a pointlist image. Depending on the dimensionality d of the labelmap and the number  of labels n, the pointlist image will have n*d pixels.
     */
    public static ClearCLBuffer centroidsOfBackgroundAndLabels(ClearCLBuffer source, ClearCLBuffer pointlist_destination) {
        CentroidsOfBackgroundAndLabels.centroidsOfBackgroundAndLabels(getCLIJ2(), source, pointlist_destination);
        return pointlist_destination;
    }


    // net.haesleinhuepf.clijx.plugins.SeededWatershed
    //----------------------------------------------------
    /**
     * Takes a label map (seeds) and an input image with gray values to apply the watershed algorithm and split the image above a given threshold in labels.
     */
    public static ClearCLBuffer seededWatershed(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4) {
        SeededWatershed.seededWatershed(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).floatValue());
        return arg3;
    }


    // net.haesleinhuepf.clijx.plugins.PushMetaData
    //----------------------------------------------------

    // net.haesleinhuepf.clijx.plugins.PopMetaData
    //----------------------------------------------------

    // net.haesleinhuepf.clijx.plugins.ResetMetaData
    //----------------------------------------------------

    // net.haesleinhuepf.clij2.plugins.AverageDistanceOfNClosestNeighborsMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines distances between all centroids and replaces every label with the average distance to the n closest neighboring labels.
     */
    public static ClearCLBuffer averageDistanceOfNClosestNeighborsMap(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        AverageDistanceOfNClosestNeighborsMap.averageDistanceOfNClosestNeighborsMap(getCLIJ2(), arg1, arg2, new Double (arg3).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clijx.plugins.DrawTouchCountMeshBetweenTouchingLabels
    //----------------------------------------------------
    /**
     * Starting from a label map, draw lines between touching neighbors resulting in a mesh.
     * 
     * The end points of the lines correspond to the centroids of the labels. The intensity of the lines 
     * corresponds to the touch count between these labels.
     */
    public static ClearCLBuffer drawTouchCountMeshBetweenTouchingLabels(ClearCLBuffer input, ClearCLBuffer destination) {
        DrawTouchCountMeshBetweenTouchingLabels.drawTouchCountMeshBetweenTouchingLabels(getCLIJ2(), input, destination);
        return destination;
    }


    // net.haesleinhuepf.clijx.plugins.LocalMaximumAverageDistanceOfNClosestNeighborsMap
    //----------------------------------------------------

    // net.haesleinhuepf.clijx.plugins.LocalMaximumAverageNeighborDistanceMap
    //----------------------------------------------------

    // net.haesleinhuepf.clijx.plugins.LocalMaximumTouchingNeighborCountMap
    //----------------------------------------------------

    // net.haesleinhuepf.clijx.plugins.LocalMeanAverageDistanceOfNClosestNeighborsMap
    //----------------------------------------------------

    // net.haesleinhuepf.clijx.plugins.LocalMeanAverageNeighborDistanceMap
    //----------------------------------------------------

    // net.haesleinhuepf.clijx.plugins.LocalMeanTouchingNeighborCountMap
    //----------------------------------------------------

    // net.haesleinhuepf.clijx.plugins.LocalMeanTouchPortionMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines which labels touch and how much, relatively taking the whole outline of 
     * each label into account, and determines for every label with the mean of this value and replaces the 
     * label index with that value.
     * 
     * 
     */
    public static ClearCLBuffer localMeanTouchPortionMap(ClearCLBuffer input, ClearCLBuffer destination) {
        LocalMeanTouchPortionMap.localMeanTouchPortionMap(getCLIJ2(), input, destination);
        return destination;
    }


    // net.haesleinhuepf.clijx.plugins.LocalMedianAverageDistanceOfNClosestNeighborsMap
    //----------------------------------------------------

    // net.haesleinhuepf.clijx.plugins.LocalMedianAverageNeighborDistanceMap
    //----------------------------------------------------

    // net.haesleinhuepf.clijx.plugins.LocalMedianTouchingNeighborCountMap
    //----------------------------------------------------

    // net.haesleinhuepf.clijx.plugins.LocalMinimumAverageDistanceOfNClosestNeighborsMap
    //----------------------------------------------------

    // net.haesleinhuepf.clijx.plugins.LocalMinimumAverageNeighborDistanceMap
    //----------------------------------------------------

    // net.haesleinhuepf.clijx.plugins.LocalMinimumTouchingNeighborCountMap
    //----------------------------------------------------

    // net.haesleinhuepf.clijx.plugins.LocalStandardDeviationAverageDistanceOfNClosestNeighborsMap
    //----------------------------------------------------

    // net.haesleinhuepf.clijx.plugins.LocalStandardDeviationAverageNeighborDistanceMap
    //----------------------------------------------------

    // net.haesleinhuepf.clijx.plugins.LocalStandardDeviationTouchingNeighborCountMap
    //----------------------------------------------------

    // net.haesleinhuepf.clij2.plugins.MinimumIntensityMap
    //----------------------------------------------------
    /**
     * Takes an image and a corresponding label map, determines the minimum intensity per label and replaces every label with the that number.
     * 
     * This results in a parametric image expressing mean object intensity.
     */
    public static ClearCLBuffer minimumIntensityMap(ClearCLBuffer intensity_image, ClearCLBuffer label_map, ClearCLBuffer destination) {
        MinimumIntensityMap.minimumIntensityMap(getCLIJ2(), intensity_image, label_map, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.MaximumIntensityMap
    //----------------------------------------------------
    /**
     * Takes an image and a corresponding label map, determines the maximum intensity per label and replaces every label with the that number.
     * 
     * This results in a parametric image expressing mean object intensity.
     */
    public static ClearCLBuffer maximumIntensityMap(ClearCLBuffer intensity_image, ClearCLBuffer label_map, ClearCLBuffer destination) {
        MaximumIntensityMap.maximumIntensityMap(getCLIJ2(), intensity_image, label_map, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.ExtensionRatioMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines for every label the extension ratio and replaces every label with the that number.
     * 
     * The extension ratio is the maximum distance of any pixel in the label to the label centroid divided by the average distance of all pixels in the label to the centroid.
     */
    public static ClearCLBuffer extensionRatioMap(ClearCLBuffer input, ClearCLBuffer destination) {
        ExtensionRatioMap.extensionRatioMap(getCLIJ2(), input, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.MaximumExtensionMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines for every label the maximum distance of any pixel to the centroid and replaces every label with the that number.
     * 
     * 
     */
    public static ClearCLBuffer maximumExtensionMap(ClearCLBuffer input, ClearCLBuffer destination) {
        MaximumExtensionMap.maximumExtensionMap(getCLIJ2(), input, destination);
        return destination;
    }


    // net.haesleinhuepf.clijx.plugins.GenerateIntegerGreyValueCooccurrenceCountMatrixHalfBox
    //----------------------------------------------------
    /**
     * Takes an image and assumes its grey values are integers. It builds up a grey-level co-occurrence matrix of neighboring (west, south-west, south, south-east, in 3D 9 pixels on the next plane) pixel intensities. 
     * 
     * Major parts of this operation run on the CPU.
     */
    public static ClearCLBuffer generateIntegerGreyValueCooccurrenceCountMatrixHalfBox(ClearCLBuffer integer_image, ClearCLBuffer grey_value_cooccurrence_matrix_destination) {
        GenerateIntegerGreyValueCooccurrenceCountMatrixHalfBox.generateIntegerGreyValueCooccurrenceCountMatrixHalfBox(getCLIJ2(), integer_image, grey_value_cooccurrence_matrix_destination);
        return grey_value_cooccurrence_matrix_destination;
    }


    // net.haesleinhuepf.clijx.plugins.GenerateIntegerGreyValueCooccurrenceCountMatrixHalfDiamond
    //----------------------------------------------------
    /**
     * Takes an image and assumes its grey values are integers. It builds up a grey-level co-occurrence matrix of neighboring (left, bottom, back) pixel intensities. 
     * 
     * Major parts of this operation run on the CPU.
     */
    public static ClearCLBuffer generateIntegerGreyValueCooccurrenceCountMatrixHalfDiamond(ClearCLBuffer integer_image, ClearCLBuffer grey_value_cooccurrence_matrix_destination) {
        GenerateIntegerGreyValueCooccurrenceCountMatrixHalfDiamond.generateIntegerGreyValueCooccurrenceCountMatrixHalfDiamond(getCLIJ2(), integer_image, grey_value_cooccurrence_matrix_destination);
        return grey_value_cooccurrence_matrix_destination;
    }


    // net.haesleinhuepf.clij2.plugins.GetMeanOfMaskedPixels
    //----------------------------------------------------
    /**
     * Determines the mean of all pixels in a given image which have non-zero value in a corresponding mask image. 
     * 
     * It will be stored in the variable mean_of_masked_pixels.
     */
    public static ClearCLBuffer getMeanOfMaskedPixels(ClearCLBuffer arg1, ClearCLBuffer arg2) {
        GetMeanOfMaskedPixels.getMeanOfMaskedPixels(getCLIJ2(), arg1, arg2);
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.DivideByGaussianBackground
    //----------------------------------------------------
    /**
     * Applies Gaussian blur to the input image and divides the original by the result.
     */
    public static ClearCLImageInterface divideByGaussianBackground(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        DivideByGaussianBackground.divideByGaussianBackground(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clijx.plugins.GenerateGreyValueCooccurrenceMatrixBox
    //----------------------------------------------------
    /**
     * Takes an image and an intensity range to determine a grey value co-occurrence matrix.
     * 
     * For determining which pixel intensities are neighbors, the box neighborhood is taken into account.
     * Pixels with intensity below minimum of the given range are considered having the minimum intensity.
     * Pixels with intensity above the maximimum of the given range are treated analogously.
     * The resulting co-occurrence matrix contains probability values between 0 and 1.
     */
    public static ClearCLBuffer generateGreyValueCooccurrenceMatrixBox(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4) {
        GenerateGreyValueCooccurrenceMatrixBox.generateGreyValueCooccurrenceMatrixBox(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clijx.plugins.GreyLevelAtttributeFiltering
    //----------------------------------------------------
    /**
     * Inspired by Grayscale attribute filtering from MorpholibJ library by David Legland & Ignacio Arganda-Carreras.
     * 
     * This plugin will remove components in a grayscale image based on user-specified area (for 2D: pixels) or volume (3D: voxels).
     * For each gray level specified in the number of bins, binary images will be generated, followed by exclusion of objects (labels)
     * below a minimum pixel count.
     * All the binary images for each gray level are combined to form the final image. The output is a grayscale image, where bright objects
     * below pixel count are removed.
     * It is recommended that low values be used for number of bins, especially for large 3D images, or it may take long time.
     */
    public static ClearCLBuffer greyLevelAtttributeFiltering(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4) {
        GreyLevelAtttributeFiltering.greyLevelAtttributeFiltering(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clijx.plugins.BinaryFillHolesSliceBySlice
    //----------------------------------------------------
    /**
     * Fills holes (pixels with value 0 surrounded by pixels with value 1) in a binary image stack slice by slice.
     */
    public static ClearCLImageInterface binaryFillHolesSliceBySlice(ClearCLImageInterface source, ClearCLImageInterface destination) {
        BinaryFillHolesSliceBySlice.binaryFillHolesSliceBySlice(getCLIJ2(), source, destination);
        return destination;
    }


    // net.haesleinhuepf.clijx.weka.BinaryWekaPixelClassifier
    //----------------------------------------------------
    /**
     * Applies a pre-trained CLIJx-Weka model to a 2D image. 
     * 
     * You can train your own model using menu Plugins > Segmentation > CLIJx Binary Weka Pixel ClassifierMake sure that the handed over feature list is the same used while training the model.
     */
    public static ClearCLBuffer binaryWekaPixelClassifier(ClearCLBuffer input, ClearCLBuffer destination, String features, String modelfilename) {
        BinaryWekaPixelClassifier.binaryWekaPixelClassifier(getCLIJ2(), input, destination, features, modelfilename);
        return destination;
    }


    // net.haesleinhuepf.clijx.weka.WekaLabelClassifier
    //----------------------------------------------------
    /**
     * Applies a pre-trained CLIJx-Weka model to an image and a corresponding label map to classify labeled objects. 
     * 
     * Make sure that the handed over feature list is the same used while training the model.
     */
    public static ClearCLBuffer wekaLabelClassifier(ClearCLBuffer input, ClearCLBuffer label_map, ClearCLBuffer destination, String features, String modelfilename) {
        WekaLabelClassifier.wekaLabelClassifier(getCLIJ2(), input, label_map, destination, features, modelfilename);
        return destination;
    }


    // net.haesleinhuepf.clijx.weka.GenerateLabelFeatureImage
    //----------------------------------------------------
    /**
     * Generates a feature image for Trainable Weka Segmentation. 
     * 
     * Use this terminology to specify which features should be generated:
     * * BOUNDING_BOX_DEPTH
     * * BOUNDING_BOX_WIDTH
     * * BOUNDING_BOX_HEIGHT
     * * CENTROID_X
     * * CENTROID_Y
     * * CENTROID_Z
     * * MASS_CENTER_X
     * * MASS_CENTER_Y
     * * MASS_CENTER_Z
     * * MAX_DISTANCE_TO_CENTROID
     * * MAX_DISTANCE_TO_MASS_CENTER
     * * MEAN_DISTANCE_TO_CENTROID
     * * MEAN_DISTANCE_TO_MASS_CENTER
     * * MAX_MEAN_DISTANCE_TO_CENTROID_RATIO
     * * MAX_MEAN_DISTANCE_TO_MASS_CENTER_RATIO
     * * MAXIMUM_INTENSITY
     * * MEAN_INTENSITY
     * * MINIMUM_INTENSITY
     * * SUM_INTENSITY
     * * STANDARD_DEVIATION_INTENSITY
     * * PIXEL_COUNT
     * * count_touching_neighbors
     * * average_touch_pixel_count
     * * average_distance_of_touching_neighbors
     * * MEAN_OF_LAPLACIAN
     * 
     * Example: "MEAN_INTENSITY count_touching_neighbors"
     */
    public static ClearCLBuffer generateLabelFeatureImage(ClearCLBuffer input, ClearCLBuffer label_map, ClearCLBuffer label_feature_image_destination, String feature_definitions) {
        GenerateLabelFeatureImage.generateLabelFeatureImage(getCLIJ2(), input, label_map, label_feature_image_destination, feature_definitions);
        return label_feature_image_destination;
    }

    /**
     * Generates a feature image for Trainable Weka Segmentation. 
     * 
     * Use this terminology to specify which features should be generated:
     * * BOUNDING_BOX_DEPTH
     * * BOUNDING_BOX_WIDTH
     * * BOUNDING_BOX_HEIGHT
     * * CENTROID_X
     * * CENTROID_Y
     * * CENTROID_Z
     * * MASS_CENTER_X
     * * MASS_CENTER_Y
     * * MASS_CENTER_Z
     * * MAX_DISTANCE_TO_CENTROID
     * * MAX_DISTANCE_TO_MASS_CENTER
     * * MEAN_DISTANCE_TO_CENTROID
     * * MEAN_DISTANCE_TO_MASS_CENTER
     * * MAX_MEAN_DISTANCE_TO_CENTROID_RATIO
     * * MAX_MEAN_DISTANCE_TO_MASS_CENTER_RATIO
     * * MAXIMUM_INTENSITY
     * * MEAN_INTENSITY
     * * MINIMUM_INTENSITY
     * * SUM_INTENSITY
     * * STANDARD_DEVIATION_INTENSITY
     * * PIXEL_COUNT
     * * count_touching_neighbors
     * * average_touch_pixel_count
     * * average_distance_of_touching_neighbors
     * * MEAN_OF_LAPLACIAN
     * 
     * Example: "MEAN_INTENSITY count_touching_neighbors"
     */
    public static ClearCLBuffer generateLabelFeatureImage(ClearCLBuffer arg1, ClearCLBuffer arg2, String arg3) {
        GenerateLabelFeatureImage.generateLabelFeatureImage(getCLIJ2(), arg1, arg2, arg3);
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.LabelSurface
    //----------------------------------------------------
    /**
     * Takes a label map and excludes all labels which are not on the surface.
     * 
     * For each label, a ray from a given center towards the label. If the ray crosses another label, the labelin question is not at the surface and thus, removed.
     */
    public static ClearCLBuffer labelSurface(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5) {
        LabelSurface.labelSurface(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.ReduceLabelsToCentroids
    //----------------------------------------------------
    /**
     * Takes a label map and reduces all labels to their center spots. Label IDs stay and background will be zero.
     */
    public static ClearCLBuffer reduceLabelsToCentroids(ClearCLBuffer input_labels, ClearCLBuffer destination_labels) {
        ReduceLabelsToCentroids.reduceLabelsToCentroids(getCLIJ2(), input_labels, destination_labels);
        return destination_labels;
    }

    /**
     * Takes a label map and reduces all labels to their center spots. Label IDs stay and background will be zero.
     */
    public static ClearCLBuffer reduceLabelsToLabelledSpots(ClearCLBuffer input_labels, ClearCLBuffer destination_labels) {
        ReduceLabelsToCentroids.reduceLabelsToLabelledSpots(getCLIJ2(), input_labels, destination_labels);
        return destination_labels;
    }


    // net.haesleinhuepf.clij2.plugins.MeanExtensionMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines for every label the mean distance of any pixel to the centroid and replaces every label with the that number.
     * 
     * 
     */
    public static ClearCLBuffer meanExtensionMap(ClearCLBuffer input, ClearCLBuffer destination) {
        MeanExtensionMap.meanExtensionMap(getCLIJ2(), input, destination);
        return destination;
    }


    // net.haesleinhuepf.clijx.plugins.MeanZProjectionBelowThreshold
    //----------------------------------------------------
    /**
     * Determines the mean average intensity projection of an image along Z but only for pixels below a given threshold.
     */
    public static ClearCLImageInterface meanZProjectionBelowThreshold(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3) {
        MeanZProjectionBelowThreshold.meanZProjectionBelowThreshold(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.EuclideanDistanceFromLabelCentroidMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines the centroids of all labels and writes the distance of all labelled pixels to their centroid in the result image.
     * Background pixels stay zero.
     */
    public static ClearCLBuffer euclideanDistanceFromLabelCentroidMap(ClearCLBuffer labelmap_input, ClearCLBuffer destination) {
        EuclideanDistanceFromLabelCentroidMap.euclideanDistanceFromLabelCentroidMap(getCLIJ2(), labelmap_input, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.GammaCorrection
    //----------------------------------------------------
    /**
     * Applies a gamma correction to an image.
     * 
     * Therefore, all pixels x of the Image X are normalized and the power to gamma g is computed, before normlization is reversed (^ is the power operator):f(x) = (x / max(X)) ^ gamma * max(X)
     */
    public static ClearCLBuffer gammaCorrection(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        GammaCorrection.gammaCorrection(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.ZPositionOfMaximumZProjection
    //----------------------------------------------------
    /**
     * Determines a Z-position of the maximum intensity along Z and writes it into the resulting image.
     * 
     * If there are multiple z-slices with the same value, the smallest Z will be chosen.
     */
    public static ClearCLImageInterface zPositionOfMaximumZProjection(ClearCLImageInterface source, ClearCLImageInterface destination) {
        ZPositionOfMaximumZProjection.zPositionOfMaximumZProjection(getCLIJ2(), source, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.ZPositionProjection
    //----------------------------------------------------
    /**
     * Project a defined Z-slice of a 3D stack into a 2D image.
     * 
     * Which Z-slice is defined as the z_position image, which represents an altitude map.
     */
    public static ClearCLImageInterface zPositionProjection(ClearCLImageInterface source_stack, ClearCLImageInterface z_position, ClearCLImageInterface destination) {
        ZPositionProjection.zPositionProjection(getCLIJ2(), source_stack, z_position, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.ZPositionRangeProjection
    //----------------------------------------------------
    /**
     * Project multiple Z-slices of a 3D stack into a new 3D stack.
     * 
     * Which Z-slice is defined as the z_position image, which represents an altitude map. The two additional numbers define the range relative to the given z-position.
     */
    public static ClearCLImageInterface zPositionRangeProjection(ClearCLImageInterface arg1, ClearCLImageInterface arg2, ClearCLImageInterface arg3, int arg4, int arg5) {
        ZPositionRangeProjection.zPositionRangeProjection(getCLIJ2(), arg1, arg2, arg3, arg4, arg5);
        return arg3;
    }


    // net.haesleinhuepf.clij2.plugins.VarianceSphere
    //----------------------------------------------------
    /**
     * Computes the local variance of a pixels spherical neighborhood. 
     * 
     * The spheres size is specified by 
     * its half-width, half-height and half-depth (radius). If 2D images are given, radius_z will be ignored. 
     */
    public static ClearCLImageInterface varianceSphere(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        VarianceSphere.varianceSphere(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.StandardDeviationSphere
    //----------------------------------------------------
    /**
     * Computes the local standard deviation of a pixels spherical neighborhood. 
     * 
     * The spheres size is specified by 
     * its half-width, half-height and half-depth (radius). If 2D images are given, radius_z will be ignored. 
     */
    public static ClearCLImageInterface standardDeviationSphere(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        StandardDeviationSphere.standardDeviationSphere(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.VarianceBox
    //----------------------------------------------------
    /**
     * Computes the local variance of a pixels box neighborhood. 
     * 
     * The box size is specified by 
     * its half-width, half-height and half-depth (radius). If 2D images are given, radius_z will be ignored. 
     */
    public static ClearCLImageInterface varianceBox(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        VarianceBox.varianceBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.StandardDeviationBox
    //----------------------------------------------------
    /**
     * Computes the local standard deviation of a pixels box neighborhood. 
     * 
     * The box size is specified by 
     * its half-width, half-height and half-depth (radius). If 2D images are given, radius_z will be ignored. 
     */
    public static ClearCLImageInterface standardDeviationBox(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        StandardDeviationBox.standardDeviationBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.Tenengrad
    //----------------------------------------------------
    /**
     * Convolve the image with the Tenengrad kernel slice by slice.
     */
    public static ClearCLImageInterface tenengrad(ClearCLImageInterface source, ClearCLImageInterface destination) {
        Tenengrad.tenengrad(getCLIJ2(), source, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.TenengradSliceBySlice
    //----------------------------------------------------
    /**
     * Convolve the image with the Tenengrad kernel slice by slice.
     */
    public static ClearCLImageInterface tenengradSliceBySlice(ClearCLImageInterface source, ClearCLImageInterface destination) {
        TenengradSliceBySlice.tenengradSliceBySlice(getCLIJ2(), source, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.SobelSliceBySlice
    //----------------------------------------------------
    /**
     * Convolve the image with the Sobel kernel slice by slice.
     */
    public static ClearCLImageInterface sobelSliceBySlice(ClearCLImageInterface source, ClearCLImageInterface destination) {
        SobelSliceBySlice.sobelSliceBySlice(getCLIJ2(), source, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.ExtendedDepthOfFocusSobelProjection
    //----------------------------------------------------
    /**
     * Extended depth of focus projection maximizing local pixel intensity variance.
     * 
     * The sigma parameter allows controlling an Gaussian blur which should smooth the altitude map.
     */
    public static ClearCLImageInterface extendedDepthOfFocusVarianceProjection(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3) {
        ExtendedDepthOfFocusSobelProjection.extendedDepthOfFocusVarianceProjection(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.ExtendedDepthOfFocusTenengradProjection
    //----------------------------------------------------
    /**
     * Extended depth of focus projection maximizing intensity in the local sobel image.
     * 
     * The sigma parameter allows controlling an Gaussian blur which should smooth the altitude map.
     */
    public static ClearCLImageInterface extendedDepthOfFocusTenengradProjection(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3) {
        ExtendedDepthOfFocusTenengradProjection.extendedDepthOfFocusTenengradProjection(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.ExtendedDepthOfFocusVarianceProjection
    //----------------------------------------------------
    /**
     * Extended depth of focus projection maximizing local pixel intensity variance.
     * 
     * The sigma parameter allows controlling an Gaussian blur which should smooth the altitude map.
     */
    public static ClearCLImageInterface extendedDepthOfFocusVarianceProjection(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        ExtendedDepthOfFocusVarianceProjection.extendedDepthOfFocusVarianceProjection(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.DrawMeshBetweenNClosestLabels
    //----------------------------------------------------

    // net.haesleinhuepf.clij2.plugins.DrawMeshBetweenProximalLabels
    //----------------------------------------------------
    /**
     * Starting from a label map, draw lines between labels that are closer than a given distance resulting in a mesh.
     * 
     * The end points of the lines correspond to the centroids of the labels.
     */
    public static ClearCLBuffer drawMeshBetweenProximalLabels(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        DrawMeshBetweenProximalLabels.drawMeshBetweenProximalLabels(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.Cosinus
    //----------------------------------------------------
    /**
     * Computes the cosinus of all pixels value x.
     * 
     * <pre>f(x) = cos(x)</pre>
     */
    public static ClearCLImageInterface cosinus(ClearCLImageInterface source, ClearCLImageInterface destination) {
        Cosinus.cosinus(getCLIJ2(), source, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.Sinus
    //----------------------------------------------------
    /**
     * Computes the sinus of all pixels value x.
     * 
     * <pre>f(x) = sin(x)</pre>
     */
    public static ClearCLImageInterface sinus(ClearCLImageInterface source, ClearCLImageInterface destination) {
        Sinus.sinus(getCLIJ2(), source, destination);
        return destination;
    }


    // net.haesleinhuepf.clijx.plugins.GenerateDistanceMatrixAlongAxis
    //----------------------------------------------------
    /**
     * Computes the distance in X, Y or Z (specified with parameter axis) between all point coordinates given in two point lists.
     * 
     * Takes two images containing pointlists (dimensionality n * d, n: number of points and d: dimensionality) and builds up a matrix containing the distances between these points. 
     * 
     * Convention: Given two point lists with dimensionality n * d and m * d, the distance matrix will be of size(n + 1) * (m + 1). The first row and column contain zeros. They represent the distance of the objects to a theoretical background object. In that way, distance matrices are of the same size as touch matrices (see generateTouchMatrix). Thus, one can threshold a distance matrix to generate a touch matrix out of it for drawing meshes.
     */
    public static ClearCLBuffer generateDistanceMatrixAlongAxis(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4) {
        GenerateDistanceMatrixAlongAxis.generateDistanceMatrixAlongAxis(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).intValue());
        return arg3;
    }


    // net.haesleinhuepf.clij2.plugins.MaximumDistanceOfTouchingNeighbors
    //----------------------------------------------------
    /**
     * Takes a touch matrix and a distance matrix to determine the maximum distance of touching neighbors for every object.
     */
    public static ClearCLBuffer maximumDistanceOfTouchingNeighbors(ClearCLBuffer distance_matrix, ClearCLBuffer touch_matrix, ClearCLBuffer distancelist_destination) {
        MaximumDistanceOfTouchingNeighbors.maximumDistanceOfTouchingNeighbors(getCLIJ2(), distance_matrix, touch_matrix, distancelist_destination);
        return distancelist_destination;
    }


    // net.haesleinhuepf.clij2.plugins.MaximumTouchingNeighborDistanceMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines which labels touch and replaces every label with the maximum distance to their neighboring labels.
     * 
     * To determine the distances, the centroid of the labels is determined internally.
     */
    public static ClearCLBuffer maximumTouchingNeighborDistanceMap(ClearCLBuffer input, ClearCLBuffer destination) {
        MaximumTouchingNeighborDistanceMap.maximumTouchingNeighborDistanceMap(getCLIJ2(), input, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.MinimumTouchingNeighborDistanceMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines which labels touch and replaces every label with the minimum distance to their neighboring labels.
     * 
     * To determine the distances, the centroid of the labels is determined internally.
     */
    public static ClearCLBuffer minimumTouchingNeighborDistanceMap(ClearCLBuffer input, ClearCLBuffer destination) {
        MinimumTouchingNeighborDistanceMap.minimumTouchingNeighborDistanceMap(getCLIJ2(), input, destination);
        return destination;
    }


    // net.haesleinhuepf.clijx.plugins.GenerateAngleMatrix
    //----------------------------------------------------
    /**
     * Computes the angle in radians between all point coordinates given in two point lists.
     * 
     *  Takes two images containing pointlists (dimensionality n * d, n: number of 
     * points and d: dimensionality) and builds up a matrix containing the 
     * angles between these points.
     * 
     * Convention: Values range from -90 to 90 degrees (-0.5 to 0.5 pi radians)
     * * -90 degreess (-0.5 pi radians): Top
     * * 0 defrees (0 radians): Right
     * * 90 degrees (0.5 pi radians): Bottom
     * 
     * Convention: Given two point lists with dimensionality n * d and m * d, the distance 
     * matrix will be of size(n + 1) * (m + 1). The first row and column 
     * contain zeros. They represent the distance of the objects to a 
     * theoretical background object. In that way, distance matrices are of 
     * the same size as touch matrices (see generateTouchMatrix). Thus, one 
     * can threshold a distance matrix to generate a touch matrix out of it 
     * for drawing meshes. 
     * 
     * Implemented for 2D only at the moment.
     * 
     * Parameters
     * ----------
     * coordinate_list1 : Image
     * coordinate_list2 : Image
     * angle_matrix_destination : Image
     * 
     * Returns
     * -------
     * angle_matrix_destination
     */
    public static ClearCLBuffer generateAngleMatrix(ClearCLBuffer coordinate_list1, ClearCLBuffer coordinate_list2, ClearCLBuffer angle_matrix_destination) {
        GenerateAngleMatrix.generateAngleMatrix(getCLIJ2(), coordinate_list1, coordinate_list2, angle_matrix_destination);
        return angle_matrix_destination;
    }


    // net.haesleinhuepf.clij2.plugins.TouchingNeighborDistanceRangeRatioMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines which labels touch and replaces every label with the distance range ratio (maximum distance divided by minimum distance) to their neighboring labels.
     * 
     * To determine the distances, the centroids of the labels is determined internally.
     */
    public static ClearCLBuffer touchingNeighborDistanceRangeRatioMap(ClearCLBuffer input, ClearCLBuffer destination) {
        TouchingNeighborDistanceRangeRatioMap.touchingNeighborDistanceRangeRatioMap(getCLIJ2(), input, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.VoronoiOtsuLabeling
    //----------------------------------------------------
    /**
     * Labeles objects directly from grey-value images.
     * 
     * The two sigma parameters allow tuning the segmentation result. The first sigma controls how close detected cells can be (spot_sigma) and the second controls how precise segmented objects are outlined (outline_sigma).Under the hood, this filter applies two Gaussian blurs, spot detection, Otsu-thresholding and Voronoi-labeling.
     * The thresholded binary image is flooded using the Voronoi approach starting from the found local maxima.
     * Noise-removal sigma for spot detection and thresholding can be configured separately.
     */
    public static ClearCLBuffer voronoiOtsuLabeling(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4) {
        VoronoiOtsuLabeling.voronoiOtsuLabeling(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clijx.plugins.VisualizeOutlinesOnOriginal
    //----------------------------------------------------
    /**
     * Combines an intensity image and a label (or binary) image so that you can see segmentation outlines on the intensity image.
     */
    public static ClearCLBuffer visualizeOutlinesOnOriginal(ClearCLBuffer intensity, ClearCLBuffer labels, ClearCLBuffer destination) {
        VisualizeOutlinesOnOriginal.visualizeOutlinesOnOriginal(getCLIJ2(), intensity, labels, destination);
        return destination;
    }


    // net.haesleinhuepf.clijx.plugins.FlagLabelsOnEdges
    //----------------------------------------------------
    /**
     * Determines which labels in a label map touch the edges of the image (in X, Y and Z if the image is 3D). 
     * 
     * It results in a vector image with values 1 (touches edges) and 0 (does not touch edge).
     * The entry in the vector (index 0) corresponds to background, following entries correspond to labels.
     */
    public static ClearCLBuffer flagLabelsOnEdges(ClearCLBuffer label_map_input, ClearCLBuffer flag_vector_destination) {
        FlagLabelsOnEdges.flagLabelsOnEdges(getCLIJ2(), label_map_input, flag_vector_destination);
        return flag_vector_destination;
    }


    // net.haesleinhuepf.clij2.plugins.MaskedVoronoiLabeling
    //----------------------------------------------------
    /**
     * Takes a binary image, labels connected components and dilates the regions using a octagon shape until they touch and only inside another binary mask image.
     * 
     * The resulting label map is written to the output.
     * 
     * Hint: Process isotropic images only.
     */
    public static ClearCLImageInterface maskedVoronoiLabeling(ClearCLBuffer input, ClearCLBuffer mask, ClearCLImageInterface destination) {
        MaskedVoronoiLabeling.maskedVoronoiLabeling(getCLIJ2(), input, mask, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.PullToResultsTableColumn
    //----------------------------------------------------
    /**
     * Copies the content of a vector image to a column in the results table.
     * You can configure if new rows should be appended or if existing values should be overwritten.
     */
    public static ClearCLBuffer pullToResultsTableColumn(ClearCLBuffer arg1, ResultsTable arg2, String arg3, boolean arg4) {
        PullToResultsTableColumn.pullToResultsTableColumn(getCLIJ2(), arg1, arg2, arg3, arg4);
        return arg1;
    }


    // net.haesleinhuepf.clijx.plugins.KMeansLabelClusterer
    //----------------------------------------------------
    /**
     * Applies K-Means clustering to an image and a corresponding label map. 
     * 
     * See also: https://commons.apache.org/proper/commons-math/javadocs/api-3.6/org/apache/commons/math3/ml/clustering/KMeansPlusPlusClusterer.html
     * Make sure that the handed over feature list is the same used while training the model.
     * The neighbor_radius specifies a correction step which allows to use a region where the mode of 
     * classification results (the most popular class) will be determined after clustering.
     */
    public static ClearCLBuffer kMeansLabelClusterer(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, String arg4, String arg5, double arg6, double arg7, boolean arg8) {
        KMeansLabelClusterer.kMeansLabelClusterer(getCLIJ2(), arg1, arg2, arg3, arg4, arg5, new Double (arg6).intValue(), new Double (arg7).intValue(), arg8);
        return arg3;
    }


    // net.haesleinhuepf.clij2.plugins.ModeOfTouchingNeighbors
    //----------------------------------------------------
    /**
     * Takes a touch matrix and a vector of values to determine the most popular integer value among touching neighbors for every object.
     * TODO: This only works for values between 0 and 255 for now.
     */
    public static ClearCLBuffer modeOfTouchingNeighbors(ClearCLBuffer values, ClearCLBuffer touch_matrix, ClearCLBuffer mode_values_destination) {
        ModeOfTouchingNeighbors.modeOfTouchingNeighbors(getCLIJ2(), values, touch_matrix, mode_values_destination);
        return mode_values_destination;
    }


    // net.haesleinhuepf.clij2.plugins.GenerateProximalNeighborsMatrix
    //----------------------------------------------------
    /**
     * Produces a touch-matrix where the neighbors within a given distance range are marked as touching neighbors.
     * 
     * Takes a distance matrix (e.g. derived from a pointlist of centroids) and marks for every column the neighbors whose
     * distance lie within a given distance range (>= min and <= max). 
     * The resulting matrix can be use as if it was a touch-matrix (a.k.a. adjacency graph matrix). 
     * 
     * Parameters
     * ----------
     * distance_marix : Image
     * touch_matrix_destination : Image
     * min_distance : float, optional
     *     default : 0
     * max_distance : float, optional
     *     default: 10 
     * 
     * Returns
     * -------
     * touch_matrix_destination
     */
    public static ClearCLBuffer generateProximalNeighborsMatrix(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4) {
        GenerateProximalNeighborsMatrix.generateProximalNeighborsMatrix(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clijx.plugins.ReadIntensitiesFromMap
    //----------------------------------------------------
    /**
     * Takes a label image and an parametric image and reads parametric values from the labels positions.
     * 
     * The read intensity valus are stored in a new vector.
     * 
     * Note: This will only work if all labels have number of voxels == 1 or if all pixels in each label have the same value.
     * 
     * DEPRECATED: Use ReadValuesFromMap instead
     */
    public static ClearCLImageInterface readIntensitiesFromMap(ClearCLImageInterface labels, ClearCLImageInterface map_image, ClearCLImageInterface values_destination) {
        ReadIntensitiesFromMap.readIntensitiesFromMap(getCLIJ2(), labels, map_image, values_destination);
        return values_destination;
    }


    // net.haesleinhuepf.clij2.plugins.MaximumOfTouchingNeighborsMap
    //----------------------------------------------------
    /**
     * Takes a label image and a parametric intensity image and will replace each labels value in the parametric image
     * by the maximum value of neighboring labels. The radius of the neighborhood can be configured:
     * * radius 0: Nothing is replaced
     * * radius 1: direct neighbors are taken into account
     * * radius 2: neighbors and neighbors or neighbors are taken into account
     * * radius n: ...
     * 
     * Note: Values of all pixels in a label each must be identical.
     * 
     * Parameters
     * ----------
     * parametric_map : Image
     * label_map : Image
     * parametric_map_destination : Image
     * radius : int
     * ignore_touching_background : bool
     * 
     * Returns
     * -------
     * parametric_map_destination
     */
    public static ClearCLBuffer maximumOfTouchingNeighborsMap(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4, boolean arg5) {
        MaximumOfTouchingNeighborsMap.maximumOfTouchingNeighborsMap(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).intValue(), arg5);
        return arg3;
    }


    // net.haesleinhuepf.clij2.plugins.MinimumOfTouchingNeighborsMap
    //----------------------------------------------------
    /**
     * Takes a label image and a parametric intensity image and will replace each labels value in the parametric image
     * by the minimum value of neighboring labels. The radius of the neighborhood can be configured:
     * * radius 0: Nothing is replaced
     * * radius 1: direct neighbors are taken into account
     * * radius 2: neighbors and neighbors or neighbors are taken into account
     * * radius n: ...
     * 
     * Note: Values of all pixels in a label each must be identical.
     * 
     * Parameters
     * ----------
     * parametric_map : Image
     * label_map : Image
     * parametric_map_destination : Image
     * radius : int
     * ignore_touching_background : bool
     * 
     * Returns
     * -------
     * parametric_map_destination
     */
    public static ClearCLBuffer minimumOfTouchingNeighborsMap(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4, boolean arg5) {
        MinimumOfTouchingNeighborsMap.minimumOfTouchingNeighborsMap(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).intValue(), arg5);
        return arg3;
    }


    // net.haesleinhuepf.clij2.plugins.MeanOfTouchingNeighborsMap
    //----------------------------------------------------
    /**
     * Takes a label image and a parametric intensity image and will replace each labels value in the parametric image
     * by the mean average value of neighboring labels. The radius of the neighborhood can be configured:
     * * radius 0: Nothing is replaced
     * * radius 1: direct neighbors are taken into account
     * * radius 2: neighbors and neighbors or neighbors are taken into account
     * * radius n: ...
     * 
     * Note: Values of all pixels in a label each must be identical.
     * 
     * Parameters
     * ----------
     * parametric_map : Image
     * label_map : Image
     * parametric_map_destination : Image
     * radius : int
     * ignore_touching_background : bool
     * 
     * Returns
     * -------
     * parametric_map_destination
     */
    public static ClearCLBuffer meanOfTouchingNeighborsMap(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4, boolean arg5) {
        MeanOfTouchingNeighborsMap.meanOfTouchingNeighborsMap(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).intValue(), arg5);
        return arg3;
    }


    // net.haesleinhuepf.clij2.plugins.ModeOfTouchingNeighborsMap
    //----------------------------------------------------
    /**
     * Takes a label image and a parametric intensity image and will replace each labels value in the parametric image
     * by the most popular value of neighboring labels. The radius of the neighborhood can be configured:
     * * radius 0: Nothing is replaced
     * * radius 1: direct neighbors are taken into account
     * * radius 2: neighbors and neighbors or neighbors are taken into account
     * * radius n: ...
     * 
     * Note: Values of all pixels in a label each must be identical.
     * 
     * Parameters
     * ----------
     * parametric_map : Image
     * label_map : Image
     * parametric_map_destination : Image
     * radius : int
     * ignore_touching_background : bool
     * 
     * Returns
     * -------
     * parametric_map_destination
     */
    public static ClearCLBuffer modeOfTouchingNeighborsMap(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4, boolean arg5) {
        ModeOfTouchingNeighborsMap.modeOfTouchingNeighborsMap(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).intValue(), arg5);
        return arg3;
    }


    // net.haesleinhuepf.clij2.plugins.StandardDeviationOfTouchingNeighborsMap
    //----------------------------------------------------
    /**
     * Takes a label image and a parametric intensity image and will replace each labels value in the parametric image
     * by the standard deviation value of touching neighbor labels. The radius of the neighborhood can be configured:
     * * radius 0: Nothing is replaced
     * * radius 1: direct neighbors are taken into account
     * * radius 2: neighbors and neighbors or neighbors are taken into account
     * * radius n: ...
     * 
     * Note: Values of all pixels in a label each must be identical.
     * 
     * Parameters
     * ----------
     * parametric_map : Image
     * label_map : Image
     * parametric_map_destination : Image
     * radius : int
     * ignore_touching_background : bool
     * 
     * Returns
     * -------
     * parametric_map_destination
     */
    public static ClearCLBuffer standardDeviationOfTouchingNeighborsMap(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4, boolean arg5) {
        StandardDeviationOfTouchingNeighborsMap.standardDeviationOfTouchingNeighborsMap(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).intValue(), arg5);
        return arg3;
    }


    // net.haesleinhuepf.clij2.plugins.PointIndexListToTouchMatrix
    //----------------------------------------------------
    /**
     * Takes a list of point indices to generate a touch matrix (a.k.a. adjacency graph matrix) out of it. 
     * 
     * The list hasa dimensionality of m*n for the points 1... m (0 a.k.a. background is not in this list). In the n rows, there are
     * indices to points which should be connected.
     * 
     * Parameters
     * ----------
     * indexlist : Image
     * matrix_destination : Image
     */
    public static ClearCLBuffer pointIndexListToTouchMatrix(ClearCLBuffer indexlist, ClearCLBuffer matrix_destination) {
        PointIndexListToTouchMatrix.pointIndexListToTouchMatrix(getCLIJ2(), indexlist, matrix_destination);
        return matrix_destination;
    }


    // net.haesleinhuepf.clij2.plugins.GenerateNNearestNeighborsMatrix
    //----------------------------------------------------
    /**
     * Produces a touch-matrix where the n nearest neighbors are marked as touching neighbors. 
     * 
     * Takes a distance matrix (e.g. derived from a pointlist of centroids) and marks for every column the n smallest
     * distances as neighbors. The resulting matrix can be use as if it was a touch-matrix (a.k.a. adjacency graph matrix). 
     * 
     * Inspired by a similar implementation in imglib2 [1]
     * 
     * Note: The implementation is limited to square matrices.
     * 
     * Parameters
     * ----------
     * distance_marix : Image
     * touch_matrix_destination : Image
     * n : int
     *    number of neighbors
     *    
     * References
     * ----------
     * [1] https://github.com/imglib/imglib2/blob/master/src/main/java/net/imglib2/interpolation/neighborsearch/InverseDistanceWeightingInterpolator.java
     * 
     */
    public static ClearCLBuffer generateNNearestNeighborsMatrix(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        GenerateNNearestNeighborsMatrix.generateNNearestNeighborsMatrix(getCLIJ2(), arg1, arg2, new Double (arg3).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.MaximumOfNNearestNeighborsMap
    //----------------------------------------------------
    /**
     * Takes a label image and a parametric intensity image and will replace each labels value in the parametric image
     * by the maximum value of neighboring labels. The distance number of nearest neighbors can be configured.
     * Note: Values of all pixels in a label each must be identical.
     * 
     * Parameters
     * ----------
     * parametric_map : Image
     * label_map : Image
     * parametric_map_destination : Image
     * n : int
     *     number of nearest neighbors
     */
    public static ClearCLBuffer maximumOfNNearestNeighborsMap(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4) {
        MaximumOfNNearestNeighborsMap.maximumOfNNearestNeighborsMap(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).intValue());
        return arg3;
    }


    // net.haesleinhuepf.clij2.plugins.MinimumOfNNearestNeighborsMap
    //----------------------------------------------------
    /**
     * Takes a label image and a parametric intensity image and will replace each labels value in the parametric image
     * by the minimum value of neighboring labels. The distance number of nearest neighbors can be configured.
     * Note: Values of all pixels in a label each must be identical.
     * 
     * Parameters
     * ----------
     * parametric_map : Image
     * label_map : Image
     * parametric_map_destination : Image
     * n : int
     *     number of nearest neighbors
     */
    public static ClearCLBuffer minimumOfNNearestNeighborsMap(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4) {
        MinimumOfNNearestNeighborsMap.minimumOfNNearestNeighborsMap(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).intValue());
        return arg3;
    }


    // net.haesleinhuepf.clij2.plugins.MeanOfNNearestNeighborsMap
    //----------------------------------------------------
    /**
     * Takes a label image and a parametric intensity image and will replace each labels value in the parametric image
     * by the minimum value of neighboring labels. The distance number of nearest neighbors can be configured.
     * Note: Values of all pixels in a label each must be identical.
     * 
     * Parameters
     * ----------
     * parametric_map : Image
     * label_map : Image
     * parametric_map_destination : Image
     * n : int
     *     number of nearest neighbors
     */
    public static ClearCLBuffer meanOfNNearestNeighborsMap(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4) {
        MeanOfNNearestNeighborsMap.meanOfNNearestNeighborsMap(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).intValue());
        return arg3;
    }


    // net.haesleinhuepf.clij2.plugins.ModeOfNNearestNeighborsMap
    //----------------------------------------------------
    /**
     * Takes a label image and a parametric intensity image and will replace each labels value in the parametric image
     * by the mode value of neighboring labels. The distance number of nearest neighbors can be configured.
     * Note: Values of all pixels in a label each must be identical.
     * 
     * Parameters
     * ----------
     * parametric_map : Image
     * label_map : Image
     * parametric_map_destination : Image
     * n : int
     *     number of nearest neighbors
     */
    public static ClearCLBuffer modeOfNNearestNeighborsMap(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4) {
        ModeOfNNearestNeighborsMap.modeOfNNearestNeighborsMap(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).intValue());
        return arg3;
    }


    // net.haesleinhuepf.clij2.plugins.StandardDeviationOfNNearestNeighborsMap
    //----------------------------------------------------
    /**
     * Takes a label image and a parametric intensity image and will replace each labels value in the parametric image
     * by the standard deviation value of neighboring labels. The distance number of nearest neighbors can be configured.
     * Note: Values of all pixels in a label each must be identical.
     * 
     * Parameters
     * ----------
     * parametric_map : Image
     * label_map : Image
     * parametric_map_destination : Image
     * n : int
     *     number of nearest neighbors
     */
    public static ClearCLBuffer standardDeviationOfNNearestNeighborsMap(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4) {
        StandardDeviationOfNNearestNeighborsMap.standardDeviationOfNNearestNeighborsMap(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).intValue());
        return arg3;
    }


    // net.haesleinhuepf.clij2.plugins.MaximumOfProximalNeighborsMap
    //----------------------------------------------------
    /**
     * Takes a label image and a parametric intensity image and will replace each labels value in the parametric image by the maximum value of neighboring labels.
     * 
     *  The distance range of the centroids of the neighborhood can be configured.
     * Note: Values of all pixels in a label each must be identical.
     * 
     * Parameters
     * ----------
     * parametric_map : Image
     * label_map : Image
     * parametric_map_destination : Image
     * min_distance : float, optional
     *     default : 0
     * max_distance : float, optional
     *     default: maximum float value
     */
    public static ClearCLBuffer maximumOfProximalNeighborsMap(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4, double arg5) {
        MaximumOfProximalNeighborsMap.maximumOfProximalNeighborsMap(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).floatValue(), new Double (arg5).floatValue());
        return arg3;
    }


    // net.haesleinhuepf.clij2.plugins.MinimumOfProximalNeighborsMap
    //----------------------------------------------------
    /**
     * Takes a label image and a parametric intensity image and will replace each labels value in the parametric image by the minimum value of neighboring labels.
     * 
     *  The distance range of the centroids of the neighborhood can be configured.
     * Note: Values of all pixels in a label each must be identical.
     * 
     * Parameters
     * ----------
     * parametric_map : Image
     * label_map : Image
     * parametric_map_destination : Image
     * min_distance : float, optional
     *     default : 0
     * max_distance : float, optional
     *     default: maximum float value
     */
    public static ClearCLBuffer minimumOfProximalNeighborsMap(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4, double arg5) {
        MinimumOfProximalNeighborsMap.minimumOfProximalNeighborsMap(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).floatValue(), new Double (arg5).floatValue());
        return arg3;
    }


    // net.haesleinhuepf.clij2.plugins.MeanOfProximalNeighborsMap
    //----------------------------------------------------
    /**
     * Takes a label image and a parametric intensity image and will replace each labels value in the parametric image by the mean average value of neighboring labels.
     * 
     *  The distance range of the centroids of the neighborhood can be configured.
     * Note: Values of all pixels in a label each must be identical.
     * 
     * Parameters
     * ----------
     * parametric_map : Image
     * label_map : Image
     * parametric_map_destination : Image
     * min_distance : float, optional
     *     default : 0
     * max_distance : float, optional
     *     default: maximum float value
     */
    public static ClearCLBuffer meanOfProximalNeighborsMap(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4, double arg5) {
        MeanOfProximalNeighborsMap.meanOfProximalNeighborsMap(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).floatValue(), new Double (arg5).floatValue());
        return arg3;
    }


    // net.haesleinhuepf.clij2.plugins.ModeOfProximalNeighborsMap
    //----------------------------------------------------
    /**
     * Takes a label image and a parametric intensity image and will replace each labels value in the parametric image by the most popular value of neighboring labels.
     * 
     *  The distance range of the centroids of the neighborhood can be configured.
     * Note: Values of all pixels in a label each must be identical.
     * 
     * Parameters
     * ----------
     * parametric_map : Image
     * label_map : Image
     * parametric_map_destination : Image
     * min_distance : float, optional
     *     default : 0
     * max_distance : float, optional
     *     default: maximum float value
     */
    public static ClearCLBuffer modeOfProximalNeighborsMap(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4, double arg5) {
        ModeOfProximalNeighborsMap.modeOfProximalNeighborsMap(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).floatValue(), new Double (arg5).floatValue());
        return arg3;
    }


    // net.haesleinhuepf.clij2.plugins.StandardDeviationOfProximalNeighborsMap
    //----------------------------------------------------
    /**
     * Takes a label image and a parametric intensity image and will replace each labels value in the parametric image by the standard deviation value of neighboring labels.
     * 
     *  The distance range of the centroids of the neighborhood can be configured.
     * Note: Values of all pixels in a label each must be identical.
     * 
     * Parameters
     * ----------
     * parametric_map : Image
     * label_map : Image
     * parametric_map_destination : Image
     * min_distance : float, optional
     *     default : 0
     * max_distance : float, optional
     *     default: maximum float value
     */
    public static ClearCLBuffer standardDeviationOfProximalNeighborsMap(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4, double arg5) {
        StandardDeviationOfProximalNeighborsMap.standardDeviationOfProximalNeighborsMap(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).floatValue(), new Double (arg5).floatValue());
        return arg3;
    }


    // net.haesleinhuepf.clij2.plugins.LabelOverlapCountMap
    //----------------------------------------------------
    /**
     * Takes two label maps, and counts for every label in label map 1 how many labels overlap with it in label map 2.
     * 
     * The resulting map is generated from the label map 1 by replacing the labels with the respective count.
     */
    public static ClearCLBuffer labelOverlapCountMap(ClearCLBuffer label_map1, ClearCLBuffer label_map2, ClearCLBuffer overlap_count_map_destination) {
        LabelOverlapCountMap.labelOverlapCountMap(getCLIJ2(), label_map1, label_map2, overlap_count_map_destination);
        return overlap_count_map_destination;
    }


    // net.haesleinhuepf.clij2.plugins.LabelProximalNeighborCountMap
    //----------------------------------------------------
    /**
     * Takes two label maps, and counts for every label in label map 1 how many labels are in a given distance range to it in label map 2.
     * 
     * Distances are computed from the centroids of the labels. The resulting map is generated from the label map 1 by replacing the labels with the respective count.
     */
    public static ClearCLBuffer labelProximalNeighborCountMap(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4, double arg5) {
        LabelProximalNeighborCountMap.labelProximalNeighborCountMap(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).floatValue(), new Double (arg5).floatValue());
        return arg3;
    }


    // net.haesleinhuepf.clij2.plugins.ReduceLabelsToLabelEdges
    //----------------------------------------------------
    /**
     * Takes a label map and reduces all labels to their edges. Label IDs stay the same and background will be zero.
     */
    public static ClearCLBuffer reduceLabelsToLabelEdges(ClearCLBuffer input_labels, ClearCLBuffer destination_labels) {
        ReduceLabelsToLabelEdges.reduceLabelsToLabelEdges(getCLIJ2(), input_labels, destination_labels);
        return destination_labels;
    }


    // net.haesleinhuepf.clij2.plugins.OutOfIntensityRange
    //----------------------------------------------------
    /**
     * Sets all pixels to 1 if their intensity lies out of a given range, and 0 otherwise.
     * 
     * Given minimum and maximum are considered part of the range.
     */
    public static ClearCLImageInterface outOfIntensityRange(ClearCLBuffer arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        OutOfIntensityRange.outOfIntensityRange(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.ErodeLabels
    //----------------------------------------------------
    /**
     * Extend labels with a given radius.
     * 
     * This is actually a local minimum filter applied to a label map after introducing background-gaps between labels.
     * In case relabel_islands is set, split objects will get new labels each. In this case, more labels might be in the result.
     * It is recommended to apply this operation to isotropic images only.
     * Warning: If labels were too small, they may be missing in the resulting label image.
     */
    public static ClearCLBuffer erodeLabels(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, boolean arg4) {
        ErodeLabels.erodeLabels(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), arg4);
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.Similar
    //----------------------------------------------------
    /**
     * Determines the absolute difference between two images and sets all pixels to 1 where it is below or equal a given tolerance, and 0 otherwise.
     */
    public static ClearCLBuffer similar(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4) {
        Similar.similar(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).floatValue());
        return arg3;
    }


    // net.haesleinhuepf.clij2.plugins.Different
    //----------------------------------------------------
    /**
     * Determines the absolute difference between two images and sets all pixels to 1 where it is above a given tolerance, and 0 otherwise.
     */
    public static ClearCLBuffer different(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4) {
        Different.different(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).floatValue());
        return arg3;
    }


    // net.haesleinhuepf.clijx.weka.WekaRegionalLabelClassifier
    //----------------------------------------------------
    /**
     * Applies a pre-trained CLIJx-Weka model to an image and a corresponding label map to classify labeled objects.
     * 
     * Given radii allow to configure if values of proximal neighbors, other labels with centroids closer 
     * than given radius, should be taken into account, e.g. for determining the regional maximum.
     * 
     * Make sure that the handed over feature list and radii are the same used while training the model.
     */
    public static ClearCLBuffer wekaRegionalLabelClassifier(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, String arg4, String arg5, double arg6, double arg7, double arg8, double arg9) {
        WekaRegionalLabelClassifier.wekaRegionalLabelClassifier(getCLIJ2(), arg1, arg2, arg3, arg4, arg5, new Double (arg6).intValue(), new Double (arg7).intValue(), new Double (arg8).intValue(), new Double (arg9).intValue());
        return arg3;
    }

    /**
     * 
     */
    public static ClearCLBuffer generateRegionalLabelFeatureImage(ClearCLBuffer arg1, ClearCLBuffer arg2, String arg3, int arg4, int arg5, int arg6, int arg7) {
        WekaRegionalLabelClassifier.generateRegionalLabelFeatureImage(getCLIJ2(), arg1, arg2, arg3, arg4, arg5, arg6, arg7);
        return arg2;
    }


    // net.haesleinhuepf.clijx.plugins.LabelMeanOfLaplacianMap
    //----------------------------------------------------
    /**
     * Takes an image and a corresponding label map, determines the mean intensity in the laplacian of the image per label and replaces every label with the that number.
     * 
     * This results in a parametric image visualizing local contrast.
     */
    public static ClearCLBuffer labelMeanOfLaplacianMap(ClearCLBuffer input, ClearCLBuffer label_map, ClearCLBuffer destination) {
        LabelMeanOfLaplacianMap.labelMeanOfLaplacianMap(getCLIJ2(), input, label_map, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.MedianZProjectionMasked
    //----------------------------------------------------
    /**
     * Determines the median intensity projection of an image stack along Z where pixels in a corresponding mask image are unequal to zero.
     */
    public static ClearCLImageInterface medianZProjectionMasked(ClearCLImageInterface arg1, ClearCLImageInterface arg2, ClearCLImageInterface arg3) {
        MedianZProjectionMasked.medianZProjectionMasked(getCLIJ2(), arg1, arg2, arg3);
        return arg3;
    }


    // net.haesleinhuepf.clijx.plugins.MedianTouchPortionMap
    //----------------------------------------------------
    /**
     * Starts from a label map, determines median touch portion to neighbors (between 0 and 1) and draws a map.
     * 
     * 
     */
    public static ClearCLBuffer medianTouchPortionMap(ClearCLBuffer labels, ClearCLBuffer map_destination) {
        MedianTouchPortionMap.medianTouchPortionMap(getCLIJ2(), labels, map_destination);
        return map_destination;
    }


    // net.haesleinhuepf.clijx.plugins.NeighborCountWithTouchPortionAboveThresholdMap
    //----------------------------------------------------
    /**
     * Starts from a label map, determines touch portion to neighbors, counts those above a given value (between 0 and 1) and draws a map.
     * 
     * 
     */
    public static ClearCLBuffer neighborCountWithTouchPortionAboveThresholdMap(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        NeighborCountWithTouchPortionAboveThresholdMap.neighborCountWithTouchPortionAboveThresholdMap(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.DivideScalarByImage
    //----------------------------------------------------
    /**
     * Divides a scalar s by image X pixel wise. 
     * 
     * <pre>f(s, x) = s / x</pre>
     */
    public static ClearCLImageInterface divideScalarByImage(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3) {
        DivideScalarByImage.divideScalarByImage(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.ReadValuesFromMap
    //----------------------------------------------------
    /**
     * Takes a label image and an parametric image and reads parametric values from the labels positions.
     * 
     * The read intensity valus are stored in a new vector.
     * 
     * Note: This will only work if all labels have number of voxels == 1 or if all pixels in each label have the same value.
     * 
     * Parameters
     * ----------
     * labels
     * map_image
     * values_destination
     */
    public static ClearCLImageInterface readValuesFromMap(ClearCLImageInterface labels, ClearCLImageInterface map_image, ClearCLImageInterface values_destination) {
        ReadValuesFromMap.readValuesFromMap(getCLIJ2(), labels, map_image, values_destination);
        return values_destination;
    }


    // net.haesleinhuepf.clij2.plugins.ReadValuesFromPositions
    //----------------------------------------------------
    /**
     * Takes a pointlist and a parametric image and reads parametric values from the positions.
     * 
     * The read intensity values are stored in a new vector.
     * 
     * Parameters
     * ----------
     * pointlist
     * map_image
     * values_destination
     */
    public static ClearCLImageInterface readValuesFromPositions(ClearCLImageInterface pointlist, ClearCLImageInterface map_image, ClearCLImageInterface values_destination) {
        ReadValuesFromPositions.readValuesFromPositions(getCLIJ2(), pointlist, map_image, values_destination);
        return values_destination;
    }


    // net.haesleinhuepf.clij2.plugins.ZPositionOfMinimumZProjection
    //----------------------------------------------------
    /**
     * Determines a Z-position of the minimum intensity along Z and writes it into the resulting image.
     * 
     * If there are multiple z-slices with the same value, the smallest Z will be chosen.
     */
    public static ClearCLImageInterface zPositionOfMinimumZProjection(ClearCLImageInterface source, ClearCLImageInterface destination) {
        ZPositionOfMinimumZProjection.zPositionOfMinimumZProjection(getCLIJ2(), source, destination);
        return destination;
    }


    // net.haesleinhuepf.clijx.plugins.LocalThresholdPhansalkar
    //----------------------------------------------------
    /**
     * Computes the local threshold (Fast version) based on 
     *  Auto Local Threshold (Phansalkar method) see: https://imagej.net/Auto_Local_Threshold 
     *  see code in: 
     *  https://github.com/fiji/Auto_Local_Threshold/blob/c955dc18cff58ac61df82f3f001799f7ffaec5cb/src/main/java/fiji/threshold/Auto_Local_Threshold.java#L636 
     *  Formulary: 
     * <pre>t = mean * (1 + p * exp(-q * mean) + k * ((stdev / r) - 1))</pre>
     */
    public static ClearCLBuffer localThresholdPhansalkar(ClearCLBuffer arg1, ClearCLBuffer arg2, float arg3, float arg4, float arg5) {
        LocalThresholdPhansalkar.localThresholdPhansalkar(getCLIJx(), arg1, arg2, arg3, arg4, arg5);
        return arg2;
    }


    // net.haesleinhuepf.clijx.plugins.LocalThresholdBernsen
    //----------------------------------------------------
    /**
     * Computes the local threshold based on 
     *  Auto Local Threshold (Bernsen method) see: https://imagej.net/Auto_Local_Threshold 
     *  see code in: 
     *  https://github.com/fiji/Auto_Local_Threshold/blob/master/src/main/java/fiji/threshold/Auto_Local_Threshold.java 
     *  Formular: 
     * <pre>if (tcontrast > max - min){ if ((max + min)/2.0 >= 128) res = 0} else if (val > (max + min)/2.0) res =0</pre>
     */
    public static ClearCLBuffer localThresholdBernsen(ClearCLBuffer arg1, ClearCLBuffer arg2, float arg3, float arg4) {
        LocalThresholdBernsen.localThresholdBernsen(getCLIJx(), arg1, arg2, arg3, arg4);
        return arg2;
    }


    // net.haesleinhuepf.clijx.plugins.LocalThresholdContrast
    //----------------------------------------------------
    /**
     * Computes the local threshold based on 
     *  Auto Local Threshold (Contrast method) see: https://imagej.net/Auto_Local_Threshold 
     *  see code in: 
     *  https://github.com/fiji/Auto_Local_Threshold/blob/master/src/main/java/fiji/threshold/Auto_Local_Threshold.java 
     *  Formular: 
     * <pre>if (abs(value - min) >= abs(max - value) && (value != 0)) value = 0 </pre>
     */
    public static ClearCLBuffer localThresholdContrast(ClearCLBuffer arg1, ClearCLBuffer arg2, float arg3) {
        LocalThresholdContrast.localThresholdContrast(getCLIJx(), arg1, arg2, arg3);
        return arg2;
    }


    // net.haesleinhuepf.clijx.plugins.LocalThresholdMean
    //----------------------------------------------------
    /**
     * Computes the local threshold based on 
     *  Auto Local Threshold (Mean method) see: https://imagej.net/Auto_Local_Threshold 
     *  see code in: 
     *  https://github.com/fiji/Auto_Local_Threshold/blob/master/src/main/java/fiji/threshold/Auto_Local_Threshold.java 
     *  Formular: 
     * <pre>if(value > (mean - c_value)) value = 0 </pre>
     */
    public static ClearCLBuffer localThresholdMean(ClearCLBuffer arg1, ClearCLBuffer arg2, float arg3, float arg4) {
        LocalThresholdMean.localThresholdMean(getCLIJx(), arg1, arg2, arg3, arg4);
        return arg2;
    }


    // net.haesleinhuepf.clijx.plugins.LocalThresholdMedian
    //----------------------------------------------------
    /**
     * Computes the local threshold based on 
     *  Auto Local Threshold (Median method) see: https://imagej.net/Auto_Local_Threshold 
     *  see code in: 
     *  https://github.com/fiji/Auto_Local_Threshold/blob/master/src/main/java/fiji/threshold/Auto_Local_Threshold.java 
     *  Formular: 
     * <pre>if(value > (median - c_value)) value = 0 </pre>
     */
    public static ClearCLBuffer localThresholdMedian(ClearCLBuffer arg1, ClearCLBuffer arg2, float arg3, float arg4) {
        LocalThresholdMedian.localThresholdMedian(getCLIJx(), arg1, arg2, arg3, arg4);
        return arg2;
    }


    // net.haesleinhuepf.clijx.plugins.LocalThresholdMidGrey
    //----------------------------------------------------
    /**
     * Computes the local threshold based on 
     *  Auto Local Threshold (MidGrey method) see: https://imagej.net/Auto_Local_Threshold 
     *  see code in: 
     *  https://github.com/fiji/Auto_Local_Threshold/blob/master/src/main/java/fiji/threshold/Auto_Local_Threshold.java 
     *  Formular: 
     * <pre>if (value > ( (max + min)/2.0 - c_value) ) value = 0 </pre>
     */
    public static ClearCLBuffer localThresholdMidGrey(ClearCLBuffer arg1, ClearCLBuffer arg2, float arg3, float arg4) {
        LocalThresholdMidGrey.localThresholdMidGrey(getCLIJx(), arg1, arg2, arg3, arg4);
        return arg2;
    }


    // net.haesleinhuepf.clijx.plugins.LocalThresholdNiblack
    //----------------------------------------------------
    /**
     * Computes the local threshold based on 
     *  Auto Local Threshold (Niblack method) see: https://imagej.net/Auto_Local_Threshold 
     *  see code in: 
     *  https://github.com/fiji/Auto_Local_Threshold/blob/master/src/main/java/fiji/threshold/Auto_Local_Threshold.java 
     *  Formular: 
     * <pre>t = mean + k_value * sqrt(var - c_value) </pre>
     */
    public static ClearCLBuffer localThresholdNiblack(ClearCLBuffer arg1, ClearCLBuffer arg2, float arg3, float arg4, float arg5) {
        LocalThresholdNiblack.localThresholdNiblack(getCLIJx(), arg1, arg2, arg3, arg4, arg5);
        return arg2;
    }


    // net.haesleinhuepf.clijx.plugins.LocalThresholdSauvola
    //----------------------------------------------------
    /**
     * Computes the local threshold based on 
     *  Auto Local Threshold (Sauvola method) see: https://imagej.net/Auto_Local_Threshold 
     *  see code in: 
     *  https://github.com/fiji/Auto_Local_Threshold/blob/master/src/main/java/fiji/threshold/Auto_Local_Threshold.java 
     *  Formular: 
     * <pre>t = mean * (1.0 + k_value * (stddev / r_value - 1.0)) </pre>
     */
    public static ClearCLBuffer localThresholdSauvola(ClearCLBuffer arg1, ClearCLBuffer arg2, float arg3, float arg4, float arg5) {
        LocalThresholdSauvola.localThresholdSauvola(getCLIJx(), arg1, arg2, arg3, arg4, arg5);
        return arg2;
    }


    // net.haesleinhuepf.clijx.plugins.ColorDeconvolution
    //----------------------------------------------------
    /**
     * Computes the color deconvolution of an 8bit RGB stack color image 
     *  with a given 3x3 matrix of color vectors.
     *  Note: The input image has to be a stack with three z-slices corresponding to the red, green and blue channel.)
     * 
     *  Additional information see Supplementary Information to: 
     * 
     *  Haub, P., Meckel, T. A Model based Survey of Colour Deconvolution in 
     *  Diagnostic Brightfield Microscopy: Error Estimation and Spectral Consideration. 
     *  Sci Rep 5, 12096 (2015). https://doi.org/10.1038/srep12096 
     * 
     */
    public static ClearCLBuffer colorDeconvolution(ClearCLBuffer source, ClearCLBuffer color_vectors, ClearCLBuffer destination) {
        ColorDeconvolution.colorDeconvolution(getCLIJ2(), source, color_vectors, destination);
        return destination;
    }


    // net.haesleinhuepf.clij2.plugins.GreyscaleOpeningBox
    //----------------------------------------------------
    /**
     * Apply a greyscale morphological opening to the input image.
     * 
     * It applies a minimum filter first and the result is processed by a maximum filter with given radii.
     * High intensity regions smaller than radius will disappear.
     */
    public static ClearCLBuffer greyscaleOpeningBox(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5) {
        GreyscaleOpeningBox.greyscaleOpeningBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.GreyscaleOpeningSphere
    //----------------------------------------------------
    /**
     * Apply a greyscale morphological opening to the input image.
     * 
     * It applies a minimum filter first and the result is processed by a maximum filter with given radii.
     * High intensity regions smaller than radius will disappear.
     */
    public static ClearCLBuffer greyscaleOpeningSphere(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5) {
        GreyscaleOpeningSphere.greyscaleOpeningSphere(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.GreyscaleClosingBox
    //----------------------------------------------------
    /**
     * Apply a greyscale morphological closing to the input image.
     * 
     * It applies a maximum filter first and the result is processed by a minimum filter with given radii.
     * Low intensity regions smaller than radius will disappear.
     */
    public static ClearCLBuffer greyscaleClosingBox(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5) {
        GreyscaleClosingBox.greyscaleClosingBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.GreyscaleClosingSphere
    //----------------------------------------------------
    /**
     * Apply a greyscale morphological closing to the input image.
     * 
     * It applies a maximum filter first and the result is processed by a minimum filter with given radii.
     * Low intensity regions smaller than radius will disappear.
     */
    public static ClearCLBuffer greyscaleClosingSphere(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5) {
        GreyscaleClosingSphere.greyscaleClosingSphere(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.ProximalNeighborCountMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines which labels are within a given distance range and replaces every label with the number of neighboring labels.
     * 
     * 
     */
    public static ClearCLBuffer proximalNeighborCountMap(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4) {
        ProximalNeighborCountMap.proximalNeighborCountMap(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue());
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.SubStack
    //----------------------------------------------------
    /**
     * Crops multiple Z-slices of a 3D stack into a new 3D stack.
     * 
     * 
     */
    public static ClearCLImageInterface subStack(ClearCLImageInterface arg1, ClearCLImageInterface arg2, int arg3, int arg4) {
        SubStack.subStack(getCLIJ2(), arg1, arg2, arg3, arg4);
        return arg2;
    }


    // net.haesleinhuepf.clij2.plugins.DrawMeshBetweenNNearestLabels
    //----------------------------------------------------
    /**
     * Starting from a label map, draw lines between n closest labels for each label resulting in a mesh.
     * 
     * The end points of the lines correspond to the centroids of the labels. 
     */
    public static ClearCLBuffer drawMeshBetweenNNearestLabels(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        DrawMeshBetweenNNearestLabels.drawMeshBetweenNNearestLabels(getCLIJ2(), arg1, arg2, new Double (arg3).intValue());
        return arg2;
    }

}
// 581 methods generated.
