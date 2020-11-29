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
import net.haesleinhuepf.clijx.plugins.StackToTiles;
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
import net.haesleinhuepf.clijx.plugins.PushTile;
import net.haesleinhuepf.clijx.plugins.PullTile;
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
import net.haesleinhuepf.clijx.plugins.MergeTouchingLabels;
import net.haesleinhuepf.clijx.plugins.AverageNeighborDistanceMap;
import net.haesleinhuepf.clijx.plugins.CylinderTransform;
import net.haesleinhuepf.clijx.plugins.DetectAndLabelMaxima;
import net.haesleinhuepf.clijx.plugins.DrawDistanceMeshBetweenTouchingLabels;
import net.haesleinhuepf.clijx.plugins.DrawMeshBetweenTouchingLabels;
import net.haesleinhuepf.clijx.plugins.ExcludeLabelsOutsideSizeRange;
import net.haesleinhuepf.clijx.plugins.ExtendLabelsWithMaximumRadius;
import net.haesleinhuepf.clijx.plugins.FindAndLabelMaxima;
import net.haesleinhuepf.clijx.plugins.MakeIsotropic;
import net.haesleinhuepf.clijx.plugins.TouchingNeighborCountMap;
import net.haesleinhuepf.clijx.plugins.RigidTransform;
import net.haesleinhuepf.clijx.plugins.SphereTransform;
import net.haesleinhuepf.clijx.plugins.SubtractGaussianBackground;
import net.haesleinhuepf.clijx.plugins.ThresholdDoG;
import net.haesleinhuepf.clijx.plugins.DriftCorrectionByCenterOfMassFixation;
import net.haesleinhuepf.clijx.plugins.DriftCorrectionByCentroidFixation;
import net.haesleinhuepf.clijx.plugins.IntensityCorrection;
import net.haesleinhuepf.clijx.plugins.IntensityCorrectionAboveThresholdOtsu;
import net.haesleinhuepf.clijx.plugins.LabelMeanIntensityMap;
import net.haesleinhuepf.clijx.plugins.LabelStandardDeviationIntensityMap;
import net.haesleinhuepf.clijx.plugins.LabelPixelCountMap;
import net.haesleinhuepf.clijx.plugins.ParametricWatershed;
import net.haesleinhuepf.clijx.plugins.MeanZProjectionAboveThreshold;
import net.haesleinhuepf.clij2.plugins.CentroidsOfBackgroundAndLabels;
import net.haesleinhuepf.clijx.plugins.SeededWatershed;
import net.haesleinhuepf.clijx.plugins.PushMetaData;
import net.haesleinhuepf.clijx.plugins.PopMetaData;
import net.haesleinhuepf.clijx.plugins.ResetMetaData;
import net.haesleinhuepf.clijx.plugins.AverageDistanceOfNClosestNeighborsMap;
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
import net.haesleinhuepf.clijx.plugins.LabelMinimumIntensityMap;
import net.haesleinhuepf.clijx.plugins.LabelMaximumIntensityMap;
import net.haesleinhuepf.clijx.plugins.LabelMaximumExtensionRatioMap;
import net.haesleinhuepf.clijx.plugins.LabelMaximumExtensionMap;
import net.haesleinhuepf.clijx.plugins.GenerateIntegerGreyValueCooccurrenceCountMatrixHalfBox;
import net.haesleinhuepf.clijx.plugins.GenerateIntegerGreyValueCooccurrenceCountMatrixHalfDiamond;
import net.haesleinhuepf.clij2.plugins.GetMeanOfMaskedPixels;
import net.haesleinhuepf.clijx.plugins.DivideByGaussianBackground;
import net.haesleinhuepf.clijx.plugins.GenerateGreyValueCooccurrenceMatrixBox;
import net.haesleinhuepf.clijx.plugins.GreyLevelAtttributeFiltering;
import net.haesleinhuepf.clijx.plugins.BinaryFillHolesSliceBySlice;
import net.haesleinhuepf.clijx.weka.BinaryWekaPixelClassifier;
import net.haesleinhuepf.clijx.weka.WekaLabelClassifier;
import net.haesleinhuepf.clijx.weka.GenerateLabelFeatureImage;
import net.haesleinhuepf.clijx.plugins.LabelSurface;
import net.haesleinhuepf.clijx.plugins.ReduceLabelsToLabelledSpots;
import net.haesleinhuepf.clijx.plugins.LabelMeanExtensionMap;
import net.haesleinhuepf.clijx.plugins.MeanZProjectionBelowThreshold;
import net.haesleinhuepf.clijx.plugins.EuclideanDistanceFromLabelCentroidMap;
import net.haesleinhuepf.clijx.plugins.GammaCorrection;
import net.haesleinhuepf.clijx.plugins.ZPositionOfMaximumZProjection;
import net.haesleinhuepf.clijx.plugins.ZPositionProjection;
import net.haesleinhuepf.clijx.plugins.ZPositionRangeProjection;
import net.haesleinhuepf.clijx.plugins.VarianceSphere;
import net.haesleinhuepf.clijx.plugins.StandardDeviationSphere;
import net.haesleinhuepf.clijx.plugins.VarianceBox;
import net.haesleinhuepf.clijx.plugins.StandardDeviationBox;
import net.haesleinhuepf.clijx.plugins.Tenengrad;
import net.haesleinhuepf.clijx.plugins.TenengradSliceBySlice;
import net.haesleinhuepf.clijx.plugins.SobelSliceBySlice;
import net.haesleinhuepf.clijx.plugins.ExtendedDepthOfFocusSobelProjection;
import net.haesleinhuepf.clijx.plugins.ExtendedDepthOfFocusTenengradProjection;
import net.haesleinhuepf.clijx.plugins.ExtendedDepthOfFocusVarianceProjection;
import net.haesleinhuepf.clijx.plugins.DrawMeshBetweenNClosestLabels;
import net.haesleinhuepf.clijx.plugins.DrawMeshBetweenProximalLabels;
// this is generated code. See src/test/java/net/haesleinhuepf/clijx/codegenerator for details
interface SnakeInterface extends CommonAPI {
   static CLIJ getCLIJ() {
       return CLIJ.getInstance();
   }
   static CLIJ2 getCLIJ2() {
       return CLIJ2.getInstance();
   }
   static CLIJx getCLIJx() {
       return CLIJx.getInstance();
   }
   static void select_device(String device_name) {
       CLIJx.getInstance(device_name);
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
    static boolean binary_union(ClearCLBuffer operand1, ClearCLBuffer operand2, ClearCLBuffer destination) {
        boolean result = BinaryUnion.binaryUnion(getCLIJ2(), operand1, operand2, destination);
        return result;
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
    static boolean binary_intersection(ClearCLBuffer operand1, ClearCLBuffer operand2, ClearCLBuffer destination) {
        boolean result = BinaryIntersection.binaryIntersection(getCLIJ2(), operand1, operand2, destination);
        return result;
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
    static double count_non_zero_pixels(ClearCLBuffer source) {
        double result = CountNonZeroPixels.countNonZeroPixels(getCLIJ2(), source);
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.CrossCorrelation
    //----------------------------------------------------
    /**
     * Performs cross correlation analysis between two images. 
     * 
     * The second image is shifted by deltaPos in the given dimension. The cross correlation coefficient is calculated for each pixel in a range around the given pixel with given radius in the given dimension. Together with the original images it is recommended to hand over mean filtered images using the same radius.  
     */
    static boolean cross_correlation(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, ClearCLBuffer arg4, ClearCLBuffer arg5, double arg6, double arg7, double arg8) {
        boolean result = CrossCorrelation.crossCorrelation(getCLIJ2(), arg1, arg2, arg3, arg4, arg5, new Double (arg6).intValue(), new Double (arg7).intValue(), new Double (arg8).intValue());
        return result;
    }

    /**
     * Performs cross correlation analysis between two images. 
     * 
     * The second image is shifted by deltaPos in the given dimension. The cross correlation coefficient is calculated for each pixel in a range around the given pixel with given radius in the given dimension. Together with the original images it is recommended to hand over mean filtered images using the same radius.  
     */
    static boolean cross_correlation(ClearCLImage arg1, ClearCLImage arg2, ClearCLImage arg3, ClearCLImage arg4, ClearCLImage arg5, double arg6, double arg7, double arg8) {
        boolean result = CrossCorrelation.crossCorrelation(getCLIJ2(), arg1, arg2, arg3, arg4, arg5, new Double (arg6).intValue(), new Double (arg7).intValue(), new Double (arg8).intValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.DifferenceOfGaussian2D
    //----------------------------------------------------
    /**
     * Applies Gaussian blur to the input image twice with different sigma values resulting in two images which are then subtracted from each other.
     * 
     * It is recommended to apply this operation to images of type Float (32 bit) as results might be negative.
     */
    static boolean difference_of_gaussian(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, double arg6) {
        boolean result = DifferenceOfGaussian2D.differenceOfGaussian(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), new Double (arg6).floatValue());
        return result;
    }

    /**
     * Applies Gaussian blur to the input image twice with different sigma values resulting in two images which are then subtracted from each other.
     * 
     * It is recommended to apply this operation to images of type Float (32 bit) as results might be negative.
     */
    static boolean difference_of_gaussian2d(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, double arg6) {
        boolean result = DifferenceOfGaussian2D.differenceOfGaussian2D(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), new Double (arg6).floatValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.DifferenceOfGaussian3D
    //----------------------------------------------------
    /**
     * Applies Gaussian blur to the input image twice with different sigma values resulting in two images which are then subtracted from each other.
     * 
     * It is recommended to apply this operation to images of type Float (32 bit) as results might be negative.
     */
    static boolean difference_of_gaussian(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8) {
        boolean result = DifferenceOfGaussian3D.differenceOfGaussian(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), new Double (arg6).floatValue(), new Double (arg7).floatValue(), new Double (arg8).floatValue());
        return result;
    }

    /**
     * Applies Gaussian blur to the input image twice with different sigma values resulting in two images which are then subtracted from each other.
     * 
     * It is recommended to apply this operation to images of type Float (32 bit) as results might be negative.
     */
    static boolean difference_of_gaussian3d(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8) {
        boolean result = DifferenceOfGaussian3D.differenceOfGaussian3D(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), new Double (arg6).floatValue(), new Double (arg7).floatValue(), new Double (arg8).floatValue());
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.Extrema
    //----------------------------------------------------
    /**
     * Returns an image with pixel values most distant from 0: 
     * 
     * f(x, y) = x if abs(x) > abs(y), y else.
     */
    static boolean extrema(ClearCLBuffer input1, ClearCLBuffer input2, ClearCLBuffer destination) {
        boolean result = Extrema.extrema(getCLIJ(), input1, input2, destination);
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LocalExtremaBox
    //----------------------------------------------------
    /**
     * Applies a local minimum and maximum filter. 
     * 
     * Afterwards, the value is returned which is more far from zero.
     */
    static boolean local_extrema_box(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5) {
        boolean result = LocalExtremaBox.localExtremaBox(getCLIJ(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LocalID
    //----------------------------------------------------
    /**
     * local id
     */
    static boolean local_i_d(ClearCLBuffer input, ClearCLBuffer destination) {
        boolean result = LocalID.localID(getCLIJ(), input, destination);
        return result;
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
    static boolean mask_label(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4) {
        boolean result = MaskLabel.maskLabel(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).floatValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.MeanClosestSpotDistance
    //----------------------------------------------------
    /**
     * Determines the distance between pairs of closest spots in two binary images. 
     * 
     * Takes two binary images A and B with marked spots and determines for each spot in image A the closest spot in image B. Afterwards, it saves the average shortest distances from image A to image B as 'mean_closest_spot_distance_A_B' and from image B to image A as 'mean_closest_spot_distance_B_A' to the results table. The distance between B and A is only determined if the `bidirectional` checkbox is checked.
     */
    static double mean_closest_spot_distance(ClearCLBuffer arg1, ClearCLBuffer arg2) {
        double result = MeanClosestSpotDistance.meanClosestSpotDistance(getCLIJ2(), arg1, arg2);
        return result;
    }

    /**
     * Determines the distance between pairs of closest spots in two binary images. 
     * 
     * Takes two binary images A and B with marked spots and determines for each spot in image A the closest spot in image B. Afterwards, it saves the average shortest distances from image A to image B as 'mean_closest_spot_distance_A_B' and from image B to image A as 'mean_closest_spot_distance_B_A' to the results table. The distance between B and A is only determined if the `bidirectional` checkbox is checked.
     */
    static double[] mean_closest_spot_distance(ClearCLBuffer arg1, ClearCLBuffer arg2, boolean arg3) {
        double[] result = MeanClosestSpotDistance.meanClosestSpotDistance(getCLIJ2(), arg1, arg2, arg3);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.MeanSquaredError
    //----------------------------------------------------
    /**
     * Determines the mean squared error (MSE) between two images. 
     * 
     * The MSE will be stored in a new row of ImageJs
     * Results table in the column 'MSE'.
     */
    static double mean_squared_error(ClearCLBuffer source1, ClearCLBuffer source2) {
        double result = MeanSquaredError.meanSquaredError(getCLIJ2(), source1, source2);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.MedianZProjection
    //----------------------------------------------------
    /**
     * Determines the median intensity projection of an image stack along Z.
     */
    static boolean median_z_projection(ClearCLImageInterface source, ClearCLImageInterface destination) {
        boolean result = MedianZProjection.medianZProjection(getCLIJ2(), source, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.NonzeroMinimumDiamond
    //----------------------------------------------------
    /**
     * Apply a minimum filter (diamond shape) to the input image. 
     * 
     * The radius is fixed to 1 and pixels with value 0 are ignored.Note: Pixels with 0 value in the input image will not be overwritten in the output image.
     * Thus, the result image should be initialized by copying the original image in advance.
     */
    static boolean nonzero_minimum_diamond(ClearCLImageInterface input, ClearCLImageInterface destination) {
        boolean result = NonzeroMinimumDiamond.nonzeroMinimumDiamond(getCLIJ2(), input, destination);
        return result;
    }

    /**
     * Apply a minimum filter (diamond shape) to the input image. 
     * 
     * The radius is fixed to 1 and pixels with value 0 are ignored.Note: Pixels with 0 value in the input image will not be overwritten in the output image.
     * Thus, the result image should be initialized by copying the original image in advance.
     */
    static boolean nonzero_minimum_diamond(ClearCLImageInterface arg1, ClearCLImageInterface arg2, ClearCLImageInterface arg3) {
        boolean result = NonzeroMinimumDiamond.nonzeroMinimumDiamond(getCLIJ2(), arg1, arg2, arg3);
        return result;
    }

    /**
     * Apply a minimum filter (diamond shape) to the input image. 
     * 
     * The radius is fixed to 1 and pixels with value 0 are ignored.Note: Pixels with 0 value in the input image will not be overwritten in the output image.
     * Thus, the result image should be initialized by copying the original image in advance.
     */
    static ClearCLKernel nonzero_minimum_diamond(ClearCLImageInterface arg1, ClearCLImageInterface arg2, ClearCLImageInterface arg3, ClearCLKernel arg4) {
        ClearCLKernel result = NonzeroMinimumDiamond.nonzeroMinimumDiamond(getCLIJ2(), arg1, arg2, arg3, arg4);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.Paste2D
    //----------------------------------------------------
    /**
     * Pastes an image into another image at a given position.
     */
    static boolean paste(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        boolean result = Paste2D.paste(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return result;
    }

    /**
     * Pastes an image into another image at a given position.
     */
    static boolean paste2d(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        boolean result = Paste2D.paste2D(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.Paste3D
    //----------------------------------------------------
    /**
     * Pastes an image into another image at a given position.
     */
    static boolean paste(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        boolean result = Paste3D.paste(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return result;
    }

    /**
     * Pastes an image into another image at a given position.
     */
    static boolean paste3d(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        boolean result = Paste3D.paste3D(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.Presign
    //----------------------------------------------------
    /**
     * Determines the extrema of pixel values: 
     * 
     * f(x) = x / abs(x).
     */
    static boolean presign(ClearCLBuffer input, ClearCLBuffer destination) {
        boolean result = Presign.presign(getCLIJ(), input, destination);
        return result;
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
    static double jaccard_index(ClearCLBuffer source1, ClearCLBuffer source2) {
        double result = JaccardIndex.jaccardIndex(getCLIJ2(), source1, source2);
        return result;
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
    static double sorensen_dice_coefficient(ClearCLBuffer source1, ClearCLBuffer source2) {
        double result = SorensenDiceCoefficient.sorensenDiceCoefficient(getCLIJ2(), source1, source2);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.StandardDeviationZProjection
    //----------------------------------------------------
    /**
     * Determines the standard deviation intensity projection of an image stack along Z.
     */
    static boolean standard_deviation_z_projection(ClearCLImageInterface source, ClearCLImageInterface destination) {
        boolean result = StandardDeviationZProjection.standardDeviationZProjection(getCLIJ2(), source, destination);
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.StackToTiles
    //----------------------------------------------------
    /**
     * Stack to tiles.
     */
    static boolean stack_to_tiles(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        boolean result = StackToTiles.stackToTiles(getCLIJx(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.SubtractBackground2D
    //----------------------------------------------------
    /**
     * Applies Gaussian blur to the input image and subtracts the result from the original image.
     * 
     * Deprecated: Use topHat() or differenceOfGaussian() instead.
     */
    static boolean subtract_background(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        boolean result = SubtractBackground2D.subtractBackground(getCLIJx(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue());
        return result;
    }

    /**
     * Applies Gaussian blur to the input image and subtracts the result from the original image.
     * 
     * Deprecated: Use topHat() or differenceOfGaussian() instead.
     */
    static boolean subtract_background2d(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        boolean result = SubtractBackground2D.subtractBackground2D(getCLIJx(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue());
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.SubtractBackground3D
    //----------------------------------------------------
    /**
     * Applies Gaussian blur to the input image and subtracts the result from the original image.
     * 
     * Deprecated: Use topHat() or differenceOfGaussian() instead.
     */
    static boolean subtract_background(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        boolean result = SubtractBackground3D.subtractBackground(getCLIJx(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue());
        return result;
    }

    /**
     * Applies Gaussian blur to the input image and subtracts the result from the original image.
     * 
     * Deprecated: Use topHat() or differenceOfGaussian() instead.
     */
    static boolean subtract_background3d(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        boolean result = SubtractBackground3D.subtractBackground3D(getCLIJx(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue());
        return result;
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
    static boolean top_hat_box(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5) {
        boolean result = TopHatBox.topHatBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return result;
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
    static boolean top_hat_sphere(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5) {
        boolean result = TopHatSphere.topHatSphere(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.Exponential
    //----------------------------------------------------
    /**
     * Computes base exponential of all pixels values.
     * 
     * f(x) = exp(x)
     */
    static boolean exponential(ClearCLImageInterface source, ClearCLImageInterface destination) {
        boolean result = Exponential.exponential(getCLIJ2(), source, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.Logarithm
    //----------------------------------------------------
    /**
     * Computes base e logarithm of all pixels values.
     * 
     * f(x) = log(x)
     */
    static boolean logarithm(ClearCLImageInterface source, ClearCLImageInterface destination) {
        boolean result = Logarithm.logarithm(getCLIJ2(), source, destination);
        return result;
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
    static boolean generate_distance_matrix(ClearCLBuffer coordinate_list1, ClearCLBuffer coordinate_list2, ClearCLBuffer distance_matrix_destination) {
        boolean result = GenerateDistanceMatrix.generateDistanceMatrix(getCLIJ2(), coordinate_list1, coordinate_list2, distance_matrix_destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.ShortestDistances
    //----------------------------------------------------
    /**
     * Determine the shortest distance from a distance matrix. 
     * 
     * This corresponds to the minimum for each individial column.
     */
    static boolean shortest_distances(ClearCLBuffer distance_matrix, ClearCLBuffer destination_minimum_distances) {
        boolean result = ShortestDistances.shortestDistances(getCLIJ2(), distance_matrix, destination_minimum_distances);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.SpotsToPointList
    //----------------------------------------------------
    /**
     * Transforms a spots image as resulting from maximum/minimum detection in an image where every column contains d 
     * pixels (with d = dimensionality of the original image) with the coordinates of the maxima/minima.
     */
    static boolean spots_to_point_list(ClearCLBuffer input_spots, ClearCLBuffer destination_pointlist) {
        boolean result = SpotsToPointList.spotsToPointList(getCLIJ2(), input_spots, destination_pointlist);
        return result;
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
    static boolean transpose_xy(ClearCLBuffer input, ClearCLBuffer destination) {
        boolean result = TransposeXY.transposeXY(getCLIJ2(), input, destination);
        return result;
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
    static boolean transpose_xz(ClearCLBuffer input, ClearCLBuffer destination) {
        boolean result = TransposeXZ.transposeXZ(getCLIJ2(), input, destination);
        return result;
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
    static boolean transpose_yz(ClearCLBuffer input, ClearCLBuffer destination) {
        boolean result = TransposeYZ.transposeYZ(getCLIJ2(), input, destination);
        return result;
    }


    // net.haesleinhuepf.clijx.piv.FastParticleImageVelocimetry
    //----------------------------------------------------
    /**
     * 
     */
    static boolean particle_image_velocimetry2d(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, ClearCLBuffer arg4, double arg5) {
        boolean result = FastParticleImageVelocimetry.particleImageVelocimetry2D(getCLIJ2(), arg1, arg2, arg3, arg4, new Double (arg5).intValue());
        return result;
    }


    // net.haesleinhuepf.clijx.piv.ParticleImageVelocimetry
    //----------------------------------------------------
    /**
     * For every pixel in source image 1, determine the pixel with the most similar intensity in 
     *  the local neighborhood with a given radius in source image 2. Write the distance in 
     * X, Y and Z in the three corresponding destination images.
     */
    static boolean particle_image_velocimetry(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, ClearCLBuffer arg4, ClearCLBuffer arg5, double arg6, double arg7, double arg8) {
        boolean result = ParticleImageVelocimetry.particleImageVelocimetry(getCLIJ2(), arg1, arg2, arg3, arg4, arg5, new Double (arg6).intValue(), new Double (arg7).intValue(), new Double (arg8).intValue());
        return result;
    }


    // net.haesleinhuepf.clijx.piv.ParticleImageVelocimetryTimelapse
    //----------------------------------------------------
    /**
     * Run particle image velocimetry on a 2D+t timelapse.
     */
    static boolean particle_image_velocimetry_timelapse(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, ClearCLBuffer arg4, double arg5, double arg6, double arg7, boolean arg8) {
        boolean result = ParticleImageVelocimetryTimelapse.particleImageVelocimetryTimelapse(getCLIJ2(), arg1, arg2, arg3, arg4, new Double (arg5).intValue(), new Double (arg6).intValue(), new Double (arg7).intValue(), arg8);
        return result;
    }


    // net.haesleinhuepf.clijx.registration.DeformableRegistration2D
    //----------------------------------------------------
    /**
     * Applies particle image velocimetry to two images and registers them afterwards by warping input image 2 with a smoothed vector field.
     */
    static boolean deformable_registration2d(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4, double arg5) {
        boolean result = DeformableRegistration2D.deformableRegistration2D(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).intValue(), new Double (arg5).intValue());
        return result;
    }


    // net.haesleinhuepf.clijx.registration.TranslationRegistration
    //----------------------------------------------------
    /**
     * Measures center of mass of thresholded objects in the two input images and translates the second image so that it better fits to the first image.
     */
    static boolean translation_registration(ClearCLBuffer arg1, ClearCLBuffer arg2, double[] arg3) {
        boolean result = TranslationRegistration.translationRegistration(getCLIJ(), arg1, arg2, arg3);
        return result;
    }

    /**
     * Measures center of mass of thresholded objects in the two input images and translates the second image so that it better fits to the first image.
     */
    static boolean translation_registration(ClearCLBuffer input1, ClearCLBuffer input2, ClearCLBuffer destination) {
        boolean result = TranslationRegistration.translationRegistration(getCLIJ(), input1, input2, destination);
        return result;
    }


    // net.haesleinhuepf.clijx.registration.TranslationTimelapseRegistration
    //----------------------------------------------------
    /**
     * Applies 2D translation registration to every pair of t, t+1 slices of a 2D+t image stack.
     */
    static boolean translation_timelapse_registration(ClearCLBuffer input, ClearCLBuffer output) {
        boolean result = TranslationTimelapseRegistration.translationTimelapseRegistration(getCLIJ(), input, output);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.SetWhereXequalsY
    //----------------------------------------------------
    /**
     * Sets all pixel values a of a given image A to a constant value v in case its coordinates x == y. 
     * 
     * Otherwise the pixel is not overwritten.
     * If you want to initialize an identity transfrom matrix, set all pixels to 0 first.
     */
    static boolean set_where_xequals_y(ClearCLImageInterface arg1, double arg2) {
        boolean result = SetWhereXequalsY.setWhereXequalsY(getCLIJ2(), arg1, new Double (arg2).floatValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.LaplaceDiamond
    //----------------------------------------------------
    /**
     * Applies the Laplace operator (Diamond neighborhood) to an image.
     */
    static boolean laplace_sphere(ClearCLBuffer input, ClearCLBuffer destination) {
        boolean result = LaplaceDiamond.laplaceSphere(getCLIJ2(), input, destination);
        return result;
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
    static boolean write_values_to_positions(ClearCLBuffer positionsAndValues, ClearCLBuffer destination) {
        boolean result = WriteValuesToPositions.writeValuesToPositions(getCLIJ2(), positionsAndValues, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.GetSize
    //----------------------------------------------------

    // net.haesleinhuepf.clij2.plugins.MultiplyMatrix
    //----------------------------------------------------
    /**
     * Multiplies two matrices with each other.
     */
    static boolean multiply_matrix(ClearCLBuffer matrix1, ClearCLBuffer matrix2, ClearCLBuffer matrix_destination) {
        boolean result = MultiplyMatrix.multiplyMatrix(getCLIJ2(), matrix1, matrix2, matrix_destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.MatrixEqual
    //----------------------------------------------------
    /**
     * Checks if all elements of a matrix are different by less than or equal to a given tolerance. 
     * 
     * The result will be put in the results table in column "MatrixEqual" as 1 if yes and 0 otherwise.
     */
    static boolean matrix_equal(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        boolean result = MatrixEqual.matrixEqual(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.PowerImages
    //----------------------------------------------------
    /**
     * Calculates x to the power of y pixel wise of two images X and Y.
     */
    static boolean power_images(ClearCLBuffer input, ClearCLBuffer exponent, ClearCLBuffer destination) {
        boolean result = PowerImages.powerImages(getCLIJ2(), input, exponent, destination);
        return result;
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
    static boolean equal(ClearCLImageInterface source1, ClearCLImageInterface source2, ClearCLImageInterface destination) {
        boolean result = Equal.equal(getCLIJ2(), source1, source2, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.GreaterOrEqual
    //----------------------------------------------------
    /**
     * Determines if two images A and B greater or equal pixel wise. 
     * 
     * f(a, b) = 1 if a >= b; 0 otherwise. 
     */
    static boolean greater_or_equal(ClearCLImageInterface source1, ClearCLImageInterface source2, ClearCLImageInterface destination) {
        boolean result = GreaterOrEqual.greaterOrEqual(getCLIJ2(), source1, source2, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.Greater
    //----------------------------------------------------
    /**
     * Determines if two images A and B greater pixel wise.
     * 
     * f(a, b) = 1 if a > b; 0 otherwise. 
     */
    static boolean greater(ClearCLImageInterface source1, ClearCLImageInterface source2, ClearCLImageInterface destination) {
        boolean result = Greater.greater(getCLIJ2(), source1, source2, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.Smaller
    //----------------------------------------------------
    /**
     * Determines if two images A and B smaller pixel wise.
     * 
     * f(a, b) = 1 if a < b; 0 otherwise. 
     */
    static boolean smaller(ClearCLBuffer source1, ClearCLBuffer source2, ClearCLBuffer destination) {
        boolean result = Smaller.smaller(getCLIJ2(), source1, source2, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.SmallerOrEqual
    //----------------------------------------------------
    /**
     * Determines if two images A and B smaller or equal pixel wise.
     * 
     * f(a, b) = 1 if a <= b; 0 otherwise. 
     */
    static boolean smaller_or_equal(ClearCLBuffer source1, ClearCLBuffer source2, ClearCLBuffer destination) {
        boolean result = SmallerOrEqual.smallerOrEqual(getCLIJ2(), source1, source2, destination);
        return result;
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
    static boolean not_equal(ClearCLImageInterface source1, ClearCLImageInterface source2, ClearCLBuffer destination) {
        boolean result = NotEqual.notEqual(getCLIJ2(), source1, source2, destination);
        return result;
    }


    // net.haesleinhuepf.clijx.io.ReadImageFromDisc
    //----------------------------------------------------
    /**
     * Read an image from disc.
     */
    static ClearCLBuffer read_image_from_disc(String arg1) {
        ClearCLBuffer result = ReadImageFromDisc.readImageFromDisc(getCLIJ(), arg1);
        return result;
    }


    // net.haesleinhuepf.clijx.io.ReadRawImageFromDisc
    //----------------------------------------------------
    /**
     * Reads a raw file from disc and pushes it immediately to the GPU.
     */
    static boolean read_raw_image_from_disc(ClearCLBuffer arg1, String arg2) {
        boolean result = ReadRawImageFromDisc.readRawImageFromDisc(getCLIJ(), arg1, arg2);
        return result;
    }

    /**
     * Reads a raw file from disc and pushes it immediately to the GPU.
     */
    static ClearCLBuffer read_raw_image_from_disc(String arg1, double arg2, double arg3, double arg4, double arg5) {
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
    static ClearCLBuffer preload_from_disc(ClearCLBuffer destination, String filename, String nextFilename, String loaderId) {
        ClearCLBuffer result = PreloadFromDisc.preloadFromDisc(getCLIJ(), destination, filename, nextFilename, loaderId);
        return result;
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
    static boolean equal_constant(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3) {
        boolean result = EqualConstant.equalConstant(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.GreaterOrEqualConstant
    //----------------------------------------------------
    /**
     * Determines if two images A and B greater or equal pixel wise. 
     * 
     * f(a, b) = 1 if a >= b; 0 otherwise. 
     */
    static boolean greater_or_equal_constant(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3) {
        boolean result = GreaterOrEqualConstant.greaterOrEqualConstant(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.GreaterConstant
    //----------------------------------------------------
    /**
     * Determines if two images A and B greater pixel wise. 
     * 
     * f(a, b) = 1 if a > b; 0 otherwise. 
     */
    static boolean greater_constant(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3) {
        boolean result = GreaterConstant.greaterConstant(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.SmallerConstant
    //----------------------------------------------------
    /**
     * Determines if two images A and B smaller pixel wise.
     * 
     * f(a, b) = 1 if a < b; 0 otherwise. 
     */
    static boolean smaller_constant(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        boolean result = SmallerConstant.smallerConstant(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.SmallerOrEqualConstant
    //----------------------------------------------------
    /**
     * Determines if two images A and B smaller or equal pixel wise.
     * 
     * f(a, b) = 1 if a <= b; 0 otherwise. 
     */
    static boolean smaller_or_equal_constant(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        boolean result = SmallerOrEqualConstant.smallerOrEqualConstant(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return result;
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
    static boolean not_equal_constant(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        boolean result = NotEqualConstant.notEqualConstant(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.DrawBox
    //----------------------------------------------------
    /**
     * Draws a box at a given start point with given size. 
     * All pixels other than in the box are untouched. Consider using `set(buffer, 0);` in advance.
     */
    static boolean draw_box(ClearCLImageInterface arg1, double arg2, double arg3, double arg4, double arg5) {
        boolean result = DrawBox.drawBox(getCLIJ2(), arg1, new Double (arg2).floatValue(), new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue());
        return result;
    }

    /**
     * Draws a box at a given start point with given size. 
     * All pixels other than in the box are untouched. Consider using `set(buffer, 0);` in advance.
     */
    static boolean draw_box(ClearCLImageInterface arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7) {
        boolean result = DrawBox.drawBox(getCLIJ2(), arg1, new Double (arg2).floatValue(), new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), new Double (arg6).floatValue(), new Double (arg7).floatValue());
        return result;
    }

    /**
     * Draws a box at a given start point with given size. 
     * All pixels other than in the box are untouched. Consider using `set(buffer, 0);` in advance.
     */
    static boolean draw_box(ClearCLImageInterface arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8) {
        boolean result = DrawBox.drawBox(getCLIJ2(), arg1, new Double (arg2).floatValue(), new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), new Double (arg6).floatValue(), new Double (arg7).floatValue(), new Double (arg8).floatValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.DrawLine
    //----------------------------------------------------
    /**
     * Draws a line between two points with a given thickness. 
     * 
     * All pixels other than on the line are untouched. Consider using `set(buffer, 0);` in advance.
     */
    static boolean draw_line(ClearCLImageInterface arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8) {
        boolean result = DrawLine.drawLine(getCLIJ2(), arg1, new Double (arg2).floatValue(), new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), new Double (arg6).floatValue(), new Double (arg7).floatValue(), new Double (arg8).floatValue());
        return result;
    }

    /**
     * Draws a line between two points with a given thickness. 
     * 
     * All pixels other than on the line are untouched. Consider using `set(buffer, 0);` in advance.
     */
    static boolean draw_line(ClearCLImageInterface arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8, double arg9) {
        boolean result = DrawLine.drawLine(getCLIJ2(), arg1, new Double (arg2).floatValue(), new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), new Double (arg6).floatValue(), new Double (arg7).floatValue(), new Double (arg8).floatValue(), new Double (arg9).floatValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.DrawSphere
    //----------------------------------------------------
    /**
     * Draws a sphere around a given point with given radii in x, y and z (if 3D). 
     * 
     *  All pixels other than in the sphere are untouched. Consider using `set(buffer, 0);` in advance.
     */
    static boolean draw_sphere(ClearCLImageInterface arg1, double arg2, double arg3, double arg4, double arg5) {
        boolean result = DrawSphere.drawSphere(getCLIJ2(), arg1, new Double (arg2).floatValue(), new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue());
        return result;
    }

    /**
     * Draws a sphere around a given point with given radii in x, y and z (if 3D). 
     * 
     *  All pixels other than in the sphere are untouched. Consider using `set(buffer, 0);` in advance.
     */
    static boolean draw_sphere(ClearCLImageInterface arg1, double arg2, double arg3, double arg4, double arg5, double arg6) {
        boolean result = DrawSphere.drawSphere(getCLIJ2(), arg1, new Double (arg2).floatValue(), new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), new Double (arg6).floatValue());
        return result;
    }

    /**
     * Draws a sphere around a given point with given radii in x, y and z (if 3D). 
     * 
     *  All pixels other than in the sphere are untouched. Consider using `set(buffer, 0);` in advance.
     */
    static boolean draw_sphere(ClearCLImageInterface arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7) {
        boolean result = DrawSphere.drawSphere(getCLIJ2(), arg1, new Double (arg2).floatValue(), new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), new Double (arg6).floatValue(), new Double (arg7).floatValue());
        return result;
    }

    /**
     * Draws a sphere around a given point with given radii in x, y and z (if 3D). 
     * 
     *  All pixels other than in the sphere are untouched. Consider using `set(buffer, 0);` in advance.
     */
    static boolean draw_sphere(ClearCLImageInterface arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8) {
        boolean result = DrawSphere.drawSphere(getCLIJ2(), arg1, new Double (arg2).floatValue(), new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), new Double (arg6).floatValue(), new Double (arg7).floatValue(), new Double (arg8).floatValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.ReplaceIntensity
    //----------------------------------------------------
    /**
     * Replaces a specific intensity in an image with a given new value.
     */
    static boolean replace_intensity(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        boolean result = ReplaceIntensity.replaceIntensity(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.BoundingBox
    //----------------------------------------------------
    /**
     * Determines the bounding box of all non-zero pixels in a binary image. 
     * 
     * If called from macro, the positions will be stored in a new row of ImageJs Results table in the columns 'BoundingBoxX', 'BoundingBoxY', 'BoundingBoxZ', 'BoundingBoxWidth', 'BoundingBoxHeight' 'BoundingBoxDepth'.In case of 2D images Z and depth will be zero.
     */
    static double[] bounding_box(ClearCLBuffer source) {
        double[] result = BoundingBox.boundingBox(getCLIJ2(), source);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.MinimumOfMaskedPixels
    //----------------------------------------------------
    /**
     * Determines the minimum intensity in a masked image. 
     * 
     * But only in pixels which have non-zero values in another mask image.
     */
    static double minimum_of_masked_pixels(ClearCLBuffer source, ClearCLBuffer mask) {
        double result = MinimumOfMaskedPixels.minimumOfMaskedPixels(getCLIJ2(), source, mask);
        return result;
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
    static double maximum_of_masked_pixels(ClearCLBuffer source, ClearCLBuffer mask) {
        double result = MaximumOfMaskedPixels.maximumOfMaskedPixels(getCLIJ2(), source, mask);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.MeanOfMaskedPixels
    //----------------------------------------------------
    /**
     * Determines the mean intensity in a masked image. 
     * 
     * Only in pixels which have non-zero values in another binary mask image.
     */
    static double mean_of_masked_pixels(ClearCLBuffer source, ClearCLBuffer mask) {
        double result = MeanOfMaskedPixels.meanOfMaskedPixels(getCLIJ2(), source, mask);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.LabelToMask
    //----------------------------------------------------
    /**
     * Masks a single label in a label map. 
     * 
     * Sets all pixels in the target image to 1, where the given label index was present in the label map. Other pixels are set to 0.
     */
    static boolean label_to_mask(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        boolean result = LabelToMask.labelToMask(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.NClosestPoints
    //----------------------------------------------------
    /**
     * Determine the n point indices with shortest distance for all points in a distance matrix. 
     * 
     * This corresponds to the n row indices with minimum values for each column of the distance matrix.
     */
    static boolean n_closest_points(ClearCLBuffer arg1, ClearCLBuffer arg2) {
        boolean result = NClosestPoints.nClosestPoints(getCLIJ2(), arg1, arg2);
        return result;
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
    static boolean gauss_jordan(ClearCLBuffer A_matrix, ClearCLBuffer B_result_vector, ClearCLBuffer solution_destination) {
        boolean result = GaussJordan.gaussJordan(getCLIJ(), A_matrix, B_result_vector, solution_destination);
        return result;
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
    static double[] statistics_of_labelled_pixels(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        double[] result = StatisticsOfLabelledPixels.statisticsOfLabelledPixels(getCLIJ2(), arg1, arg2, new Double (arg3).intValue());
        return result;
    }

    /**
     * Determines bounding box, area (in pixels/voxels), min, max and mean intensity 
     *  of labelled objects in a label map and corresponding pixels in the original image. 
     * 
     * Instead of a label map, you can also use a binary image as a binary image is a label map with just one label.
     * 
     * This method is executed on the CPU and not on the GPU/OpenCL device.
     */
    static double[][] statistics_of_labelled_pixels(ClearCLBuffer input, ClearCLBuffer labelmap) {
        double[][] result = StatisticsOfLabelledPixels.statisticsOfLabelledPixels(getCLIJ2(), input, labelmap);
        return result;
    }

    /**
     * Determines bounding box, area (in pixels/voxels), min, max and mean intensity 
     *  of labelled objects in a label map and corresponding pixels in the original image. 
     * 
     * Instead of a label map, you can also use a binary image as a binary image is a label map with just one label.
     * 
     * This method is executed on the CPU and not on the GPU/OpenCL device.
     */
    static double[][] statistics_of_labelled_pixels(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4) {
        double[][] result = StatisticsOfLabelledPixels.statisticsOfLabelledPixels(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return result;
    }

    /**
     * 
     */
    static double[][] statistics_of_labelled_pixels_single_threaded(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4) {
        double[][] result = StatisticsOfLabelledPixels.statisticsOfLabelledPixels_single_threaded(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return result;
    }

    /**
     * Determines bounding box, area (in pixels/voxels), min, max and mean intensity 
     *  of labelled objects in a label map and corresponding pixels in the original image. 
     * 
     * Instead of a label map, you can also use a binary image as a binary image is a label map with just one label.
     * 
     * This method is executed on the CPU and not on the GPU/OpenCL device.
     */
    static ResultsTable statistics_of_labelled_pixels(ClearCLBuffer arg1, ClearCLBuffer arg2, ResultsTable arg3) {
        ResultsTable result = StatisticsOfLabelledPixels.statisticsOfLabelledPixels(getCLIJ2(), arg1, arg2, arg3);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.VarianceOfAllPixels
    //----------------------------------------------------
    /**
     * Determines the variance of all pixels in an image. 
     * 
     * The value will be stored in a new row of ImageJs
     * Results table in the column 'Variance'.
     */
    static double variance_of_all_pixels(ClearCLBuffer source) {
        double result = VarianceOfAllPixels.varianceOfAllPixels(getCLIJ2(), source);
        return result;
    }

    /**
     * Determines the variance of all pixels in an image. 
     * 
     * The value will be stored in a new row of ImageJs
     * Results table in the column 'Variance'.
     */
    static double variance_of_all_pixels(ClearCLImageInterface arg1, double arg2) {
        double result = VarianceOfAllPixels.varianceOfAllPixels(getCLIJ2(), arg1, new Double (arg2).floatValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.StandardDeviationOfAllPixels
    //----------------------------------------------------
    /**
     * Determines the standard deviation of all pixels in an image. 
     * 
     * The value will be stored in a new row of ImageJs
     * Results table in the column 'Standard_deviation'.
     */
    static double standard_deviation_of_all_pixels(ClearCLImageInterface source) {
        double result = StandardDeviationOfAllPixels.standardDeviationOfAllPixels(getCLIJ2(), source);
        return result;
    }

    /**
     * Determines the standard deviation of all pixels in an image. 
     * 
     * The value will be stored in a new row of ImageJs
     * Results table in the column 'Standard_deviation'.
     */
    static double standard_deviation_of_all_pixels(ClearCLImageInterface arg1, double arg2) {
        double result = StandardDeviationOfAllPixels.standardDeviationOfAllPixels(getCLIJ2(), arg1, new Double (arg2).floatValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.VarianceOfMaskedPixels
    //----------------------------------------------------
    /**
     * Determines the variance in an image, but only in pixels which have non-zero values in another binary mask image. 
     * 
     * The result is put in the results table as new column named 'Masked_variance'.
     */
    static double variance_of_masked_pixels(ClearCLBuffer source, ClearCLBuffer mask) {
        double result = VarianceOfMaskedPixels.varianceOfMaskedPixels(getCLIJ2(), source, mask);
        return result;
    }

    /**
     * Determines the variance in an image, but only in pixels which have non-zero values in another binary mask image. 
     * 
     * The result is put in the results table as new column named 'Masked_variance'.
     */
    static double variance_of_masked_pixels(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        double result = VarianceOfMaskedPixels.varianceOfMaskedPixels(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.StandardDeviationOfMaskedPixels
    //----------------------------------------------------
    /**
     * Determines the standard deviation of all pixels in an image which have non-zero value in a corresponding mask image. 
     * 
     * The value will be stored in a new row of ImageJs
     * Results table in the column 'Masked_standard_deviation'.
     */
    static double standard_deviation_of_masked_pixels(ClearCLBuffer source, ClearCLBuffer mask) {
        double result = StandardDeviationOfMaskedPixels.standardDeviationOfMaskedPixels(getCLIJ2(), source, mask);
        return result;
    }

    /**
     * Determines the standard deviation of all pixels in an image which have non-zero value in a corresponding mask image. 
     * 
     * The value will be stored in a new row of ImageJs
     * Results table in the column 'Masked_standard_deviation'.
     */
    static double standard_deviation_of_masked_pixels(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        double result = StandardDeviationOfMaskedPixels.standardDeviationOfMaskedPixels(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.ExcludeLabelsOnEdges
    //----------------------------------------------------
    /**
     * Removes all labels from a label map which touch the edges of the image (in X, Y and Z if the image is 3D). 
     * 
     * Remaining label elements are renumbered afterwards.
     */
    static boolean exclude_labels_on_edges(ClearCLBuffer label_map_input, ClearCLBuffer label_map_destination) {
        boolean result = ExcludeLabelsOnEdges.excludeLabelsOnEdges(getCLIJ2(), label_map_input, label_map_destination);
        return result;
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
    static boolean binary_subtract(ClearCLImageInterface minuend, ClearCLImageInterface subtrahend, ClearCLImageInterface destination) {
        boolean result = BinarySubtract.binarySubtract(getCLIJ2(), minuend, subtrahend, destination);
        return result;
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
    static boolean binary_edge_detection(ClearCLImageInterface source, ClearCLImageInterface destination) {
        boolean result = BinaryEdgeDetection.binaryEdgeDetection(getCLIJ2(), source, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.DistanceMap
    //----------------------------------------------------
    /**
     * Generates a distance map from a binary image. 
     * 
     * Pixels with non-zero value in the binary image are set to a number representing the distance to the closest zero-value pixel.
     * 
     * Note: This is not a distance matrix. See generateDistanceMatrix for details.
     */
    static boolean distance_map(ClearCLBuffer source, ClearCLBuffer destination) {
        boolean result = DistanceMap.distanceMap(getCLIJ2(), source, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.PullAsROI
    //----------------------------------------------------
    /**
     * Pulls a binary image from the GPU memory and puts it on the currently active ImageJ window as region of interest.
     */
    static Roi pull_as_r_o_i(ClearCLBuffer binary_input) {
        Roi result = PullAsROI.pullAsROI(getCLIJ2(), binary_input);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.PullLabelsToROIManager
    //----------------------------------------------------
    /**
     * Pulls all labels in a label map as ROIs to the ROI manager.
     */
    static boolean pull_labels_to_roimanager(ClearCLBuffer labelmap_input) {
        boolean result = PullLabelsToROIManager.pullLabelsToROIManager(getCLIJ2(), labelmap_input);
        return result;
    }

    /**
     * Pulls all labels in a label map as ROIs to the ROI manager.
     */
    static boolean pull_labels_to_roimanager(ClearCLBuffer arg1, RoiManager arg2) {
        boolean result = PullLabelsToROIManager.pullLabelsToROIManager(getCLIJ2(), arg1, arg2);
        return result;
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
    static boolean nonzero_maximum_diamond(ClearCLImageInterface input, ClearCLImageInterface destination) {
        boolean result = NonzeroMaximumDiamond.nonzeroMaximumDiamond(getCLIJ2(), input, destination);
        return result;
    }

    /**
     * Apply a maximum filter (diamond shape) to the input image. 
     * 
     * The radius is fixed to 1 and pixels with value 0 are ignored.
     * Note: Pixels with 0 value in the input image will not be overwritten in the output image.
     * Thus, the result image should be initialized by copying the original image in advance.
     */
    static boolean nonzero_maximum_diamond(ClearCLImageInterface arg1, ClearCLImageInterface arg2, ClearCLImageInterface arg3) {
        boolean result = NonzeroMaximumDiamond.nonzeroMaximumDiamond(getCLIJ2(), arg1, arg2, arg3);
        return result;
    }

    /**
     * Apply a maximum filter (diamond shape) to the input image. 
     * 
     * The radius is fixed to 1 and pixels with value 0 are ignored.
     * Note: Pixels with 0 value in the input image will not be overwritten in the output image.
     * Thus, the result image should be initialized by copying the original image in advance.
     */
    static ClearCLKernel nonzero_maximum_diamond(ClearCLImageInterface arg1, ClearCLImageInterface arg2, ClearCLImageInterface arg3, ClearCLKernel arg4) {
        ClearCLKernel result = NonzeroMaximumDiamond.nonzeroMaximumDiamond(getCLIJ2(), arg1, arg2, arg3, arg4);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.OnlyzeroOverwriteMaximumDiamond
    //----------------------------------------------------
    /**
     * Apply a local maximum filter to an image which only overwrites pixels with value 0.
     */
    static boolean onlyzero_overwrite_maximum_diamond(ClearCLImageInterface input, ClearCLImageInterface destination) {
        boolean result = OnlyzeroOverwriteMaximumDiamond.onlyzeroOverwriteMaximumDiamond(getCLIJ2(), input, destination);
        return result;
    }

    /**
     * Apply a local maximum filter to an image which only overwrites pixels with value 0.
     */
    static boolean onlyzero_overwrite_maximum_diamond(ClearCLImageInterface arg1, ClearCLImageInterface arg2, ClearCLImageInterface arg3) {
        boolean result = OnlyzeroOverwriteMaximumDiamond.onlyzeroOverwriteMaximumDiamond(getCLIJ2(), arg1, arg2, arg3);
        return result;
    }

    /**
     * Apply a local maximum filter to an image which only overwrites pixels with value 0.
     */
    static ClearCLKernel onlyzero_overwrite_maximum_diamond(ClearCLImageInterface arg1, ClearCLImageInterface arg2, ClearCLImageInterface arg3, ClearCLKernel arg4) {
        ClearCLKernel result = OnlyzeroOverwriteMaximumDiamond.onlyzeroOverwriteMaximumDiamond(getCLIJ2(), arg1, arg2, arg3, arg4);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.OnlyzeroOverwriteMaximumBox
    //----------------------------------------------------
    /**
     * Apply a local maximum filter to an image which only overwrites pixels with value 0.
     */
    static boolean onlyzero_overwrite_maximum_box(ClearCLImageInterface input, ClearCLImageInterface destination) {
        boolean result = OnlyzeroOverwriteMaximumBox.onlyzeroOverwriteMaximumBox(getCLIJ2(), input, destination);
        return result;
    }

    /**
     * Apply a local maximum filter to an image which only overwrites pixels with value 0.
     */
    static boolean onlyzero_overwrite_maximum_box(ClearCLImageInterface arg1, ClearCLImageInterface arg2, ClearCLImageInterface arg3) {
        boolean result = OnlyzeroOverwriteMaximumBox.onlyzeroOverwriteMaximumBox(getCLIJ2(), arg1, arg2, arg3);
        return result;
    }

    /**
     * Apply a local maximum filter to an image which only overwrites pixels with value 0.
     */
    static ClearCLKernel onlyzero_overwrite_maximum_box(ClearCLImageInterface arg1, ClearCLImageInterface arg2, ClearCLImageInterface arg3, ClearCLKernel arg4) {
        ClearCLKernel result = OnlyzeroOverwriteMaximumBox.onlyzeroOverwriteMaximumBox(getCLIJ2(), arg1, arg2, arg3, arg4);
        return result;
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
    static boolean generate_touch_matrix(ClearCLBuffer label_map, ClearCLBuffer touch_matrix_destination) {
        boolean result = GenerateTouchMatrix.generateTouchMatrix(getCLIJ2(), label_map, touch_matrix_destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.DetectLabelEdges
    //----------------------------------------------------
    /**
     * Takes a labelmap and returns an image where all pixels on label edges are set to 1 and all other pixels to 0.
     */
    static boolean detect_label_edges(ClearCLImageInterface label_map, ClearCLBuffer edge_image_destination) {
        boolean result = DetectLabelEdges.detectLabelEdges(getCLIJ2(), label_map, edge_image_destination);
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.StopWatch
    //----------------------------------------------------
    /**
     * Measures time and outputs delay to last call.
     */
    static boolean stop_watch(String text) {
        boolean result = StopWatch.stopWatch(getCLIJ(), text);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.CountTouchingNeighbors
    //----------------------------------------------------
    /**
     * Takes a touch matrix as input and delivers a vector with number of touching neighbors per label as a vector.
     */
    static boolean count_touching_neighbors(ClearCLBuffer touch_matrix, ClearCLBuffer touching_neighbors_count_destination) {
        boolean result = CountTouchingNeighbors.countTouchingNeighbors(getCLIJ2(), touch_matrix, touching_neighbors_count_destination);
        return result;
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
    static boolean replace_intensities(ClearCLImageInterface input, ClearCLImageInterface new_values_vector, ClearCLImageInterface destination) {
        boolean result = ReplaceIntensities.replaceIntensities(getCLIJ2(), input, new_values_vector, destination);
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.DrawTwoValueLine
    //----------------------------------------------------
    /**
     * Draws a line between two points with a given thickness. 
     * 
     * Pixels close to point 1 are set to value1. Pixels closer to point 2 are set to value2 All pixels other than on the line are untouched. Consider using clij.set(buffer, 0); in advance.
     */
    static boolean draw_two_value_line(ClearCLBuffer arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8, double arg9, double arg10) {
        boolean result = DrawTwoValueLine.drawTwoValueLine(getCLIJx(), arg1, new Double (arg2).floatValue(), new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), new Double (arg6).floatValue(), new Double (arg7).floatValue(), new Double (arg8).floatValue(), new Double (arg9).floatValue(), new Double (arg10).floatValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.AverageDistanceOfNClosestPoints
    //----------------------------------------------------
    /**
     * Determines the average of the n closest points for every point in a distance matrix.
     * 
     * This corresponds to the average of the n minimum values (rows) for each column of the distance matrix.
     */
    static boolean average_distance_of_n_closest_points(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        boolean result = AverageDistanceOfNClosestPoints.averageDistanceOfNClosestPoints(getCLIJ2(), arg1, arg2, new Double (arg3).intValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.SaveAsTIF
    //----------------------------------------------------
    /**
     * Pulls an image from the GPU memory and saves it as TIF to disc.
     */
    static boolean save_as_t_i_f(ClearCLBuffer input, String filename) {
        boolean result = SaveAsTIF.saveAsTIF(getCLIJ2(), input, filename);
        return result;
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
    static boolean touch_matrix_to_mesh(ClearCLBuffer pointlist, ClearCLBuffer touch_matrix, ClearCLBuffer mesh_destination) {
        boolean result = TouchMatrixToMesh.touchMatrixToMesh(getCLIJ2(), pointlist, touch_matrix, mesh_destination);
        return result;
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
    static boolean resample2d(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, boolean arg5) {
        boolean result = Resample.resample2D(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), arg5);
        return result;
    }

    /**
     * 
     */
    static boolean resample3d(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5, boolean arg6) {
        boolean result = Resample.resample3D(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), arg6);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.EqualizeMeanIntensitiesOfSlices
    //----------------------------------------------------
    /**
     * Determines correction factors for each z-slice so that the average intensity in all slices can be made the same and multiplies these factors with the slices. 
     * 
     * This functionality is similar to the 'Simple Ratio Bleaching Correction' in Fiji.
     */
    static boolean equalize_mean_intensities_of_slices(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        boolean result = EqualizeMeanIntensitiesOfSlices.equalizeMeanIntensitiesOfSlices(getCLIJ2(), arg1, arg2, new Double (arg3).intValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.Watershed
    //----------------------------------------------------
    /**
     * Apply a binary watershed to a binary image and introduces black pixels between objects.
     */
    static boolean watershed(ClearCLBuffer binary_source, ClearCLBuffer destination) {
        boolean result = Watershed.watershed(getCLIJ2(), binary_source, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.ResliceRadial
    //----------------------------------------------------
    /**
     * Computes a radial projection of an image stack. 
     * 
     * Starting point for the line is the given point in any 
     * X/Y-plane of a given input image stack. Furthermore, radius of the resulting projection must be given and scaling factors in X and Y in case pixels are not isotropic.This operation is similar to ImageJs 'Radial Reslice' method but offers less flexibility.
     */
    static boolean reslice_radial(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        boolean result = ResliceRadial.resliceRadial(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return result;
    }

    /**
     * Computes a radial projection of an image stack. 
     * 
     * Starting point for the line is the given point in any 
     * X/Y-plane of a given input image stack. Furthermore, radius of the resulting projection must be given and scaling factors in X and Y in case pixels are not isotropic.This operation is similar to ImageJs 'Radial Reslice' method but offers less flexibility.
     */
    static boolean reslice_radial(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5) {
        boolean result = ResliceRadial.resliceRadial(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue());
        return result;
    }

    /**
     * Computes a radial projection of an image stack. 
     * 
     * Starting point for the line is the given point in any 
     * X/Y-plane of a given input image stack. Furthermore, radius of the resulting projection must be given and scaling factors in X and Y in case pixels are not isotropic.This operation is similar to ImageJs 'Radial Reslice' method but offers less flexibility.
     */
    static boolean reslice_radial(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8) {
        boolean result = ResliceRadial.resliceRadial(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), new Double (arg6).floatValue(), new Double (arg7).floatValue(), new Double (arg8).floatValue());
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.ShowRGB
    //----------------------------------------------------
    /**
     * Visualises three 2D images as one RGB image
     */
    static boolean show_r_g_b(ClearCLBuffer red, ClearCLBuffer green, ClearCLBuffer blue, String title) {
        boolean result = ShowRGB.showRGB(getCLIJ(), red, green, blue, title);
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.ShowGrey
    //----------------------------------------------------
    /**
     * Visualises a single 2D image.
     */
    static ImagePlus show_grey(ClearCLBuffer input, String title) {
        ImagePlus result = ShowGrey.showGrey(getCLIJ(), input, title);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.Sobel
    //----------------------------------------------------
    /**
     * Convolve the image with the Sobel kernel.
     */
    static boolean sobel(ClearCLBuffer source, ClearCLBuffer destination) {
        boolean result = Sobel.sobel(getCLIJ2(), source, destination);
        return result;
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
    static boolean absolute(ClearCLImageInterface source, ClearCLImageInterface destination) {
        boolean result = Absolute.absolute(getCLIJ2(), source, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.LaplaceBox
    //----------------------------------------------------
    /**
     * Applies the Laplace operator (Box neighborhood) to an image.
     */
    static boolean laplace_box(ClearCLBuffer input, ClearCLBuffer destination) {
        boolean result = LaplaceBox.laplaceBox(getCLIJ2(), input, destination);
        return result;
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
    static boolean bottom_hat_box(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5) {
        boolean result = BottomHatBox.bottomHatBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return result;
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
    static boolean bottom_hat_sphere(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5) {
        boolean result = BottomHatSphere.bottomHatSphere(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.ClosingBox
    //----------------------------------------------------
    /**
     * Apply a binary closing to the input image by calling n dilations and n erosions subsequenntly.
     */
    static boolean closing_box(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        boolean result = ClosingBox.closingBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.ClosingDiamond
    //----------------------------------------------------
    /**
     * Apply a binary closing to the input image by calling n dilations and n erosions subsequently.
     */
    static boolean closing_diamond(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        boolean result = ClosingDiamond.closingDiamond(getCLIJ2(), arg1, arg2, new Double (arg3).intValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.OpeningBox
    //----------------------------------------------------
    /**
     * Apply a binary opening to the input image by calling n erosions and n dilations subsequenntly.
     */
    static boolean opening_box(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        boolean result = OpeningBox.openingBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.OpeningDiamond
    //----------------------------------------------------
    /**
     * Apply a binary opening to the input image by calling n erosions and n dilations subsequenntly.
     */
    static boolean opening_diamond(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        boolean result = OpeningDiamond.openingDiamond(getCLIJ2(), arg1, arg2, new Double (arg3).intValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.MaximumXProjection
    //----------------------------------------------------
    /**
     * Determines the maximum intensity projection of an image along X.
     */
    static boolean maximum_x_projection(ClearCLImageInterface source, ClearCLImageInterface destination_max) {
        boolean result = MaximumXProjection.maximumXProjection(getCLIJ2(), source, destination_max);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.MaximumYProjection
    //----------------------------------------------------
    /**
     * Determines the maximum intensity projection of an image along X.
     */
    static boolean maximum_y_projection(ClearCLImageInterface source, ClearCLImageInterface destination_max) {
        boolean result = MaximumYProjection.maximumYProjection(getCLIJ2(), source, destination_max);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.MaximumZProjectionBounded
    //----------------------------------------------------
    /**
     * Determines the maximum intensity projection of an image along Z within a given z range.
     */
    static boolean maximum_z_projection_bounded(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        boolean result = MaximumZProjectionBounded.maximumZProjectionBounded(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.MinimumZProjectionBounded
    //----------------------------------------------------
    /**
     * Determines the minimum intensity projection of an image along Z within a given z range.
     */
    static boolean minimum_z_projection_bounded(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        boolean result = MinimumZProjectionBounded.minimumZProjectionBounded(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.MeanZProjectionBounded
    //----------------------------------------------------
    /**
     * Determines the mean average intensity projection of an image along Z within a given z range.
     */
    static boolean mean_z_projection_bounded(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        boolean result = MeanZProjectionBounded.meanZProjectionBounded(getCLIJ(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return result;
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
    static boolean nonzero_maximum_box(ClearCLImageInterface input, ClearCLImageInterface destination) {
        boolean result = NonzeroMaximumBox.nonzeroMaximumBox(getCLIJ2(), input, destination);
        return result;
    }

    /**
     * Apply a maximum filter (box shape) to the input image. 
     * 
     * The radius is fixed to 1 and pixels with value 0 are ignored.
     * Note: Pixels with 0 value in the input image will not be overwritten in the output image.
     * Thus, the result image should be initialized by copying the original image in advance.
     */
    static boolean nonzero_maximum_box(ClearCLImageInterface arg1, ClearCLImageInterface arg2, ClearCLImageInterface arg3) {
        boolean result = NonzeroMaximumBox.nonzeroMaximumBox(getCLIJ2(), arg1, arg2, arg3);
        return result;
    }

    /**
     * Apply a maximum filter (box shape) to the input image. 
     * 
     * The radius is fixed to 1 and pixels with value 0 are ignored.
     * Note: Pixels with 0 value in the input image will not be overwritten in the output image.
     * Thus, the result image should be initialized by copying the original image in advance.
     */
    static ClearCLKernel nonzero_maximum_box(ClearCLImageInterface arg1, ClearCLImageInterface arg2, ClearCLImageInterface arg3, ClearCLKernel arg4) {
        ClearCLKernel result = NonzeroMaximumBox.nonzeroMaximumBox(getCLIJ2(), arg1, arg2, arg3, arg4);
        return result;
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
    static boolean nonzero_minimum_box(ClearCLImageInterface input, ClearCLImageInterface destination) {
        boolean result = NonzeroMinimumBox.nonzeroMinimumBox(getCLIJ2(), input, destination);
        return result;
    }

    /**
     * Apply a minimum filter (box shape) to the input image. 
     * 
     * The radius is fixed to 1 and pixels with value 0 are ignored.
     * Note: Pixels with 0 value in the input image will not be overwritten in the output image.
     * Thus, the result image should be initialized by copying the original image in advance.
     */
    static boolean nonzero_minimum_box(ClearCLImageInterface arg1, ClearCLImageInterface arg2, ClearCLImageInterface arg3) {
        boolean result = NonzeroMinimumBox.nonzeroMinimumBox(getCLIJ2(), arg1, arg2, arg3);
        return result;
    }

    /**
     * Apply a minimum filter (box shape) to the input image. 
     * 
     * The radius is fixed to 1 and pixels with value 0 are ignored.
     * Note: Pixels with 0 value in the input image will not be overwritten in the output image.
     * Thus, the result image should be initialized by copying the original image in advance.
     */
    static ClearCLKernel nonzero_minimum_box(ClearCLImageInterface arg1, ClearCLImageInterface arg2, ClearCLImageInterface arg3, ClearCLKernel arg4) {
        ClearCLKernel result = NonzeroMinimumBox.nonzeroMinimumBox(getCLIJ2(), arg1, arg2, arg3, arg4);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.MinimumZProjectionThresholdedBounded
    //----------------------------------------------------
    /**
     * Determines the minimum intensity projection of all pixels in an image above a given threshold along Z within a given z range.
     */
    static boolean minimum_z_projection_thresholded_bounded(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5) {
        boolean result = MinimumZProjectionThresholdedBounded.minimumZProjectionThresholdedBounded(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.MeanOfPixelsAboveThreshold
    //----------------------------------------------------
    /**
     * Determines the mean intensity in a threshleded image. 
     * 
     * But only in pixels which are above a given threshold.
     */
    static double mean_of_pixels_above_threshold(ClearCLBuffer arg1, double arg2) {
        double result = MeanOfPixelsAboveThreshold.meanOfPixelsAboveThreshold(getCLIJ2(), arg1, new Double (arg2).floatValue());
        return result;
    }


    // net.haesleinhuepf.clijx.gui.OrganiseWindows
    //----------------------------------------------------
    /**
     * Organises windows on screen.
     */
    static boolean organise_windows(double arg1, double arg2, double arg3, double arg4, double arg5, double arg6) {
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
    static boolean distance_matrix_to_mesh(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4) {
        boolean result = DistanceMatrixToMesh.distanceMatrixToMesh(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).floatValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.PointIndexListToMesh
    //----------------------------------------------------
    /**
     * Meshes all points in a given point list which are indiced in a corresponding index list.
     */
    static boolean point_index_list_to_mesh(ClearCLBuffer pointlist, ClearCLBuffer indexList, ClearCLBuffer mesh_destination) {
        boolean result = PointIndexListToMesh.pointIndexListToMesh(getCLIJ2(), pointlist, indexList, mesh_destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.MinimumOctagon
    //----------------------------------------------------
    /**
     * Applies a minimum filter with kernel size 3x3 n times to an image iteratively. 
     * 
     * Odd iterations are done with box neighborhood, even iterations with a diamond. Thus, with n > 2, the filter shape is an octagon. The given number of iterations makes the filter result very similar to minimum sphere. Approximately:radius = iterations - 2
     */
    static boolean minimum_octagon(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        boolean result = MinimumOctagon.minimumOctagon(getCLIJ2(), arg1, arg2, new Double (arg3).intValue());
        return result;
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
    static boolean maximum_octagon(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        boolean result = MaximumOctagon.maximumOctagon(getCLIJ2(), arg1, arg2, new Double (arg3).intValue());
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.TopHatOctagon
    //----------------------------------------------------
    /**
     * Applies a minimum filter with kernel size 3x3 n times to an image iteratively. 
     * 
     *  Odd iterations are done with box neighborhood, even iterations with a diamond. Thus, with n > 2, the filter shape is an octagon. The given number of iterations - 2 makes the filter result very similar to minimum sphere.
     */
    static boolean top_hat_octagon(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        boolean result = TopHatOctagon.topHatOctagon(getCLIJx(), arg1, arg2, new Double (arg3).intValue());
        return result;
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
    static boolean add_images(ClearCLImageInterface summand1, ClearCLImageInterface summand2, ClearCLImageInterface destination) {
        boolean result = AddImages.addImages(getCLIJ2(), summand1, summand2, destination);
        return result;
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
    static boolean add_images_weighted(ClearCLImageInterface arg1, ClearCLImageInterface arg2, ClearCLImageInterface arg3, double arg4, double arg5) {
        boolean result = AddImagesWeighted.addImagesWeighted(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).floatValue(), new Double (arg5).floatValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.SubtractImages
    //----------------------------------------------------
    /**
     * Subtracts one image X from another image Y pixel wise.
     * 
     * <pre>f(x, y) = x - y</pre>
     */
    static boolean subtract_images(ClearCLImageInterface subtrahend, ClearCLImageInterface minuend, ClearCLImageInterface destination) {
        boolean result = SubtractImages.subtractImages(getCLIJ2(), subtrahend, minuend, destination);
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.ShowGlasbeyOnGrey
    //----------------------------------------------------
    /**
     * Visualises two 2D images as one RGB image. 
     * 
     * The first channel is shown in grey, the second with glasbey LUT.
     */
    static boolean show_glasbey_on_grey(ClearCLBuffer red, ClearCLBuffer labelling, String title) {
        boolean result = ShowGlasbeyOnGrey.showGlasbeyOnGrey(getCLIJ(), red, labelling, title);
        return result;
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
    static boolean affine_transform2d(ClearCLBuffer arg1, ClearCLImageInterface arg2, float[] arg3) {
        boolean result = AffineTransform2D.affineTransform2D(getCLIJ2(), arg1, arg2, arg3);
        return result;
    }

    /**
     * Applies an affine transform to a 2D image.
     * 
     * The transform describes how coordinates in the target image are transformed to coordinates in the source image.
     * This may appear unintuitive and will be changed in the next major release. The replacement 
     * affineTransform (currently part of CLIJx) will apply inverted transforms compared to this operation.
     * Individual transforms must be separated by spaces.
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
    static boolean affine_transform2d(ClearCLBuffer source, ClearCLImageInterface destination, String transform) {
        boolean result = AffineTransform2D.affineTransform2D(getCLIJ2(), source, destination, transform);
        return result;
    }

    /**
     * Applies an affine transform to a 2D image.
     * 
     * The transform describes how coordinates in the target image are transformed to coordinates in the source image.
     * This may appear unintuitive and will be changed in the next major release. The replacement 
     * affineTransform (currently part of CLIJx) will apply inverted transforms compared to this operation.
     * Individual transforms must be separated by spaces.
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
    static boolean affine_transform2d(ClearCLBuffer arg1, ClearCLImageInterface arg2, net.imglib2.realtransform.AffineTransform2D arg3) {
        boolean result = AffineTransform2D.affineTransform2D(getCLIJ2(), arg1, arg2, arg3);
        return result;
    }

    /**
     * Applies an affine transform to a 2D image.
     * 
     * The transform describes how coordinates in the target image are transformed to coordinates in the source image.
     * This may appear unintuitive and will be changed in the next major release. The replacement 
     * affineTransform (currently part of CLIJx) will apply inverted transforms compared to this operation.
     * Individual transforms must be separated by spaces.
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
    static boolean affine_transform2d(ClearCLImage arg1, ClearCLImageInterface arg2, float[] arg3) {
        boolean result = AffineTransform2D.affineTransform2D(getCLIJ2(), arg1, arg2, arg3);
        return result;
    }

    /**
     * Applies an affine transform to a 2D image.
     * 
     * The transform describes how coordinates in the target image are transformed to coordinates in the source image.
     * This may appear unintuitive and will be changed in the next major release. The replacement 
     * affineTransform (currently part of CLIJx) will apply inverted transforms compared to this operation.
     * Individual transforms must be separated by spaces.
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
    static boolean affine_transform2d(ClearCLImage arg1, ClearCLImageInterface arg2, net.imglib2.realtransform.AffineTransform2D arg3) {
        boolean result = AffineTransform2D.affineTransform2D(getCLIJ2(), arg1, arg2, arg3);
        return result;
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
    static boolean affine_transform3d(ClearCLBuffer arg1, ClearCLImageInterface arg2, float[] arg3) {
        boolean result = AffineTransform3D.affineTransform3D(getCLIJ2(), arg1, arg2, arg3);
        return result;
    }

    /**
     * Applies an affine transform to a 3D image.
     * 
     * The transform describes how coordinates in the target image are transformed to coordinates in the source image.
     * This may appear unintuitive and will be changed in the next major release. The replacement 
     * affineTransform (currently part of CLIJx) will apply inverted transforms compared to this operation.
     * Individual transforms must be separated by spaces.
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
    static boolean affine_transform3d(ClearCLBuffer source, ClearCLImageInterface destination, String transform) {
        boolean result = AffineTransform3D.affineTransform3D(getCLIJ2(), source, destination, transform);
        return result;
    }

    /**
     * Applies an affine transform to a 3D image.
     * 
     * The transform describes how coordinates in the target image are transformed to coordinates in the source image.
     * This may appear unintuitive and will be changed in the next major release. The replacement 
     * affineTransform (currently part of CLIJx) will apply inverted transforms compared to this operation.
     * Individual transforms must be separated by spaces.
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
    static boolean affine_transform3d(ClearCLBuffer arg1, ClearCLImageInterface arg2, net.imglib2.realtransform.AffineTransform3D arg3) {
        boolean result = AffineTransform3D.affineTransform3D(getCLIJ2(), arg1, arg2, arg3);
        return result;
    }

    /**
     * Applies an affine transform to a 3D image.
     * 
     * The transform describes how coordinates in the target image are transformed to coordinates in the source image.
     * This may appear unintuitive and will be changed in the next major release. The replacement 
     * affineTransform (currently part of CLIJx) will apply inverted transforms compared to this operation.
     * Individual transforms must be separated by spaces.
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
    static boolean affine_transform3d(ClearCLImage arg1, ClearCLImageInterface arg2, float[] arg3) {
        boolean result = AffineTransform3D.affineTransform3D(getCLIJ2(), arg1, arg2, arg3);
        return result;
    }

    /**
     * Applies an affine transform to a 3D image.
     * 
     * The transform describes how coordinates in the target image are transformed to coordinates in the source image.
     * This may appear unintuitive and will be changed in the next major release. The replacement 
     * affineTransform (currently part of CLIJx) will apply inverted transforms compared to this operation.
     * Individual transforms must be separated by spaces.
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
    static boolean affine_transform3d(ClearCLImage arg1, ClearCLImageInterface arg2, net.imglib2.realtransform.AffineTransform3D arg3) {
        boolean result = AffineTransform3D.affineTransform3D(getCLIJ2(), arg1, arg2, arg3);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.ApplyVectorField2D
    //----------------------------------------------------
    /**
     * Deforms an image according to distances provided in the given vector images.
     * 
     *  It is recommended to use 32-bit images for input, output and vector images. 
     */
    static boolean apply_vector_field(ClearCLImageInterface source, ClearCLImageInterface vectorX, ClearCLImageInterface vectorY, ClearCLImageInterface destination) {
        boolean result = ApplyVectorField2D.applyVectorField(getCLIJ2(), source, vectorX, vectorY, destination);
        return result;
    }

    /**
     * Deforms an image according to distances provided in the given vector images.
     * 
     *  It is recommended to use 32-bit images for input, output and vector images. 
     */
    static boolean apply_vector_field2d(ClearCLImageInterface source, ClearCLImageInterface vectorX, ClearCLImageInterface vectorY, ClearCLImageInterface destination) {
        boolean result = ApplyVectorField2D.applyVectorField2D(getCLIJ2(), source, vectorX, vectorY, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.ApplyVectorField3D
    //----------------------------------------------------
    /**
     * Deforms an image according to distances provided in the given vector images.
     * 
     *  It is recommended to use 32-bit images for input, output and vector images. 
     */
    static boolean apply_vector_field(ClearCLImageInterface arg1, ClearCLImageInterface arg2, ClearCLImageInterface arg3, ClearCLImageInterface arg4, ClearCLImageInterface arg5) {
        boolean result = ApplyVectorField3D.applyVectorField(getCLIJ2(), arg1, arg2, arg3, arg4, arg5);
        return result;
    }

    /**
     * Deforms an image stack according to distances provided in the given vector image stacks.
     * 
     * It is recommended to use 32-bit image stacks for input, output and vector image stacks. 
     */
    static boolean apply_vector_field3d(ClearCLImageInterface source, ClearCLImageInterface vectorX, ClearCLImageInterface vectorY, ClearCLImageInterface vectorZ, ClearCLImageInterface destination) {
        boolean result = ApplyVectorField3D.applyVectorField3D(getCLIJ2(), source, vectorX, vectorY, vectorZ, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.ArgMaximumZProjection
    //----------------------------------------------------
    /**
     * Determines the maximum projection of an image stack along Z.
     * 
     * Furthermore, another 2D image is generated with pixels containing the z-index where the maximum was found (zero based).
     */
    static boolean arg_maximum_z_projection(ClearCLImageInterface source, ClearCLImageInterface destination_max, ClearCLImageInterface destination_arg_max) {
        boolean result = ArgMaximumZProjection.argMaximumZProjection(getCLIJ2(), source, destination_max, destination_arg_max);
        return result;
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
    static boolean histogram(ClearCLBuffer arg1, ClearCLBuffer arg2) {
        boolean result = Histogram.histogram(getCLIJ2(), arg1, arg2);
        return result;
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
    static boolean histogram(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, boolean arg6) {
        boolean result = Histogram.histogram(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), arg6);
        return result;
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
    static boolean histogram(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, boolean arg6, boolean arg7) {
        boolean result = Histogram.histogram(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), arg6, arg7);
        return result;
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
    static float[] histogram(ClearCLBuffer arg1, double arg2, double arg3, double arg4) {
        float[] result = Histogram.histogram(getCLIJ2(), arg1, new Double (arg2).floatValue(), new Double (arg3).floatValue(), new Double (arg4).intValue());
        return result;
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
    static ClearCLBuffer histogram(ClearCLBuffer arg1) {
        ClearCLBuffer result = Histogram.histogram(getCLIJ2(), arg1);
        return result;
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
    static boolean automatic_threshold(ClearCLBuffer input, ClearCLBuffer destination, String method) {
        boolean result = AutomaticThreshold.automaticThreshold(getCLIJ2(), input, destination, method);
        return result;
    }

    /**
     * The automatic thresholder utilizes the threshold methods from ImageJ on a histogram determined on 
     * the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.
     * 
     *  Enter one 
     * of these methods in the method text field:
     * [Default, Huang, Intermodes, IsoData, IJ_IsoData, Li, MaxEntropy, Mean, MinError, Minimum, Moments, Otsu, Percentile, RenyiEntropy, Shanbhag, Triangle, Yen]
     */
    static boolean automatic_threshold(ClearCLBuffer arg1, ClearCLBuffer arg2, String arg3, double arg4, double arg5, double arg6) {
        boolean result = AutomaticThreshold.automaticThreshold(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).floatValue(), new Double (arg5).floatValue(), new Double (arg6).intValue());
        return result;
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
    static boolean threshold(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3) {
        boolean result = Threshold.threshold(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return result;
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
    static boolean binary_or(ClearCLImageInterface operand1, ClearCLImageInterface operand2, ClearCLImageInterface destination) {
        boolean result = BinaryOr.binaryOr(getCLIJ2(), operand1, operand2, destination);
        return result;
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
    static boolean binary_and(ClearCLImageInterface operand1, ClearCLImageInterface operand2, ClearCLImageInterface destination) {
        boolean result = BinaryAnd.binaryAnd(getCLIJ2(), operand1, operand2, destination);
        return result;
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
    static boolean binary_xor(ClearCLImageInterface operand1, ClearCLImageInterface operand2, ClearCLImageInterface destination) {
        boolean result = BinaryXOr.binaryXOr(getCLIJ2(), operand1, operand2, destination);
        return result;
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
    static boolean binary_not(ClearCLImageInterface source, ClearCLImageInterface destination) {
        boolean result = BinaryNot.binaryNot(getCLIJ2(), source, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.ErodeSphere
    //----------------------------------------------------
    /**
     * Computes a binary image with pixel values 0 and 1 containing the binary erosion of a given input image. 
     * 
     * The erosion takes the von-Neumann-neighborhood (4 pixels in 2D and 6 pixels in 3d) into account.
     * The pixels in the input image with pixel value not equal to 0 will be interpreted as 1.
     */
    static boolean erode_sphere(ClearCLImageInterface source, ClearCLImageInterface destination) {
        boolean result = ErodeSphere.erodeSphere(getCLIJ2(), source, destination);
        return result;
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
    static boolean erode_box(ClearCLImageInterface source, ClearCLImageInterface destination) {
        boolean result = ErodeBox.erodeBox(getCLIJ2(), source, destination);
        return result;
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
    static boolean erode_sphere_slice_by_slice(ClearCLImageInterface source, ClearCLImageInterface destination) {
        boolean result = ErodeSphereSliceBySlice.erodeSphereSliceBySlice(getCLIJ2(), source, destination);
        return result;
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
    static boolean erode_box_slice_by_slice(ClearCLImageInterface source, ClearCLImageInterface destination) {
        boolean result = ErodeBoxSliceBySlice.erodeBoxSliceBySlice(getCLIJ2(), source, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.DilateSphere
    //----------------------------------------------------
    /**
     * Computes a binary image with pixel values 0 and 1 containing the binary dilation of a given input image.
     * 
     * The dilation takes the von-Neumann-neighborhood (4 pixels in 2D and 6 pixels in 3d) into account.
     * The pixels in the input image with pixel value not equal to 0 will be interpreted as 1.
     */
    static boolean dilate_sphere(ClearCLImageInterface source, ClearCLImageInterface destination) {
        boolean result = DilateSphere.dilateSphere(getCLIJ2(), source, destination);
        return result;
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
    static boolean dilate_box(ClearCLImageInterface source, ClearCLImageInterface destination) {
        boolean result = DilateBox.dilateBox(getCLIJ2(), source, destination);
        return result;
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
    static boolean dilate_sphere_slice_by_slice(ClearCLImageInterface source, ClearCLImageInterface destination) {
        boolean result = DilateSphereSliceBySlice.dilateSphereSliceBySlice(getCLIJ2(), source, destination);
        return result;
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
    static boolean dilate_box_slice_by_slice(ClearCLImageInterface source, ClearCLImageInterface destination) {
        boolean result = DilateBoxSliceBySlice.dilateBoxSliceBySlice(getCLIJ2(), source, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.Copy
    //----------------------------------------------------
    /**
     * Copies an image.
     * 
     * <pre>f(x) = x</pre>
     */
    static boolean copy(ClearCLImageInterface source, ClearCLImageInterface destination) {
        boolean result = Copy.copy(getCLIJ2(), source, destination);
        return result;
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
    static boolean copy_slice(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3) {
        boolean result = CopySlice.copySlice(getCLIJ2(), arg1, arg2, new Double (arg3).intValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.Crop2D
    //----------------------------------------------------
    /**
     * Crops a given rectangle out of a given image. 
     * 
     * Note: If the destination image pre-exists already, it will be overwritten and keep it's dimensions.
     */
    static boolean crop(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        boolean result = Crop2D.crop(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return result;
    }

    /**
     * Crops a given rectangle out of a given image. 
     * 
     * Note: If the destination image pre-exists already, it will be overwritten and keep it's dimensions.
     */
    static boolean crop2d(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        boolean result = Crop2D.crop2D(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.Crop3D
    //----------------------------------------------------
    /**
     * Crops a given rectangle out of a given image. 
     * 
     * Note: If the destination image pre-exists already, it will be overwritten and keep it's dimensions.
     */
    static boolean crop(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        boolean result = Crop3D.crop(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return result;
    }

    /**
     * Crops a given sub-stack out of a given image stack. 
     * 
     * Note: If the destination image pre-exists already, it will be overwritten and keep it's dimensions.
     */
    static boolean crop3d(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        boolean result = Crop3D.crop3D(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.Set
    //----------------------------------------------------
    /**
     * Sets all pixel values x of a given image X to a constant value v.
     * 
     * <pre>f(x) = v</pre>
     */
    static boolean set(ClearCLImageInterface arg1, double arg2) {
        boolean result = Set.set(getCLIJ2(), arg1, new Double (arg2).floatValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.Flip2D
    //----------------------------------------------------
    /**
     * Flips an image in X and/or Y direction depending on boolean flags.
     */
    static boolean flip(ClearCLImageInterface arg1, ClearCLImageInterface arg2, boolean arg3, boolean arg4) {
        boolean result = Flip2D.flip(getCLIJ2(), arg1, arg2, arg3, arg4);
        return result;
    }

    /**
     * Flips an image in X and/or Y direction depending on boolean flags.
     */
    static boolean flip2d(ClearCLImageInterface arg1, ClearCLImageInterface arg2, boolean arg3, boolean arg4) {
        boolean result = Flip2D.flip2D(getCLIJ2(), arg1, arg2, arg3, arg4);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.Flip3D
    //----------------------------------------------------
    /**
     * Flips an image in X and/or Y direction depending on boolean flags.
     */
    static boolean flip(ClearCLImageInterface arg1, ClearCLImageInterface arg2, boolean arg3, boolean arg4, boolean arg5) {
        boolean result = Flip3D.flip(getCLIJ2(), arg1, arg2, arg3, arg4, arg5);
        return result;
    }

    /**
     * Flips an image in X, Y and/or Z direction depending on boolean flags.
     */
    static boolean flip3d(ClearCLImageInterface arg1, ClearCLImageInterface arg2, boolean arg3, boolean arg4, boolean arg5) {
        boolean result = Flip3D.flip3D(getCLIJ2(), arg1, arg2, arg3, arg4, arg5);
        return result;
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
    static boolean rotate_counter_clockwise(ClearCLImageInterface source, ClearCLImageInterface destination) {
        boolean result = RotateCounterClockwise.rotateCounterClockwise(getCLIJ2(), source, destination);
        return result;
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
    static boolean rotate_clockwise(ClearCLImageInterface source, ClearCLImageInterface destination) {
        boolean result = RotateClockwise.rotateClockwise(getCLIJ2(), source, destination);
        return result;
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
    static boolean mask(ClearCLImageInterface source, ClearCLImageInterface mask, ClearCLImageInterface destination) {
        boolean result = Mask.mask(getCLIJ2(), source, mask, destination);
        return result;
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
    static boolean mask_stack_with_plane(ClearCLImageInterface source, ClearCLImageInterface mask, ClearCLImageInterface destination) {
        boolean result = MaskStackWithPlane.maskStackWithPlane(getCLIJ2(), source, mask, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.MaximumZProjection
    //----------------------------------------------------
    /**
     * Determines the maximum intensity projection of an image along Z.
     */
    static boolean maximum_z_projection(ClearCLImageInterface source, ClearCLImageInterface destination_max) {
        boolean result = MaximumZProjection.maximumZProjection(getCLIJ2(), source, destination_max);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.MeanZProjection
    //----------------------------------------------------
    /**
     * Determines the mean average intensity projection of an image along Z.
     */
    static boolean mean_z_projection(ClearCLImageInterface source, ClearCLImageInterface destination) {
        boolean result = MeanZProjection.meanZProjection(getCLIJ2(), source, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.MinimumZProjection
    //----------------------------------------------------
    /**
     * Determines the minimum intensity projection of an image along Z.
     */
    static boolean minimum_z_projection(ClearCLImageInterface source, ClearCLImageInterface destination_min) {
        boolean result = MinimumZProjection.minimumZProjection(getCLIJ2(), source, destination_min);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.Power
    //----------------------------------------------------
    /**
     * Computes all pixels value x to the power of a given exponent a.
     * 
     * <pre>f(x, a) = x ^ a</pre>
     */
    static boolean power(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3) {
        boolean result = Power.power(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.DivideImages
    //----------------------------------------------------
    /**
     * Divides two images X and Y by each other pixel wise. 
     * 
     * <pre>f(x, y) = x / y</pre>
     */
    static boolean divide_images(ClearCLImageInterface divident, ClearCLImageInterface divisor, ClearCLImageInterface destination) {
        boolean result = DivideImages.divideImages(getCLIJ2(), divident, divisor, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.MaximumImages
    //----------------------------------------------------
    /**
     * Computes the maximum of a pair of pixel values x, y from two given images X and Y. 
     * 
     * <pre>f(x, y) = max(x, y)</pre>
     */
    static boolean maximum_images(ClearCLImageInterface source1, ClearCLImageInterface source2, ClearCLImageInterface destination) {
        boolean result = MaximumImages.maximumImages(getCLIJ2(), source1, source2, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.MaximumImageAndScalar
    //----------------------------------------------------
    /**
     * Computes the maximum of a constant scalar s and each pixel value x in a given image X. 
     * 
     * <pre>f(x, s) = max(x, s)</pre>
     */
    static boolean maximum_image_and_scalar(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3) {
        boolean result = MaximumImageAndScalar.maximumImageAndScalar(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.MinimumImages
    //----------------------------------------------------
    /**
     * Computes the minimum of a pair of pixel values x, y from two given images X and Y.
     * 
     * <pre>f(x, y) = min(x, y)</pre>
     */
    static boolean minimum_images(ClearCLImageInterface source1, ClearCLImageInterface source2, ClearCLImageInterface destination) {
        boolean result = MinimumImages.minimumImages(getCLIJ2(), source1, source2, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.MinimumImageAndScalar
    //----------------------------------------------------
    /**
     * Computes the minimum of a constant scalar s and each pixel value x in a given image X.
     * 
     * <pre>f(x, s) = min(x, s)</pre>
     */
    static boolean minimum_image_and_scalar(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3) {
        boolean result = MinimumImageAndScalar.minimumImageAndScalar(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return result;
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
    static boolean multiply_image_and_scalar(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3) {
        boolean result = MultiplyImageAndScalar.multiplyImageAndScalar(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return result;
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
    static boolean multiply_stack_with_plane(ClearCLImageInterface sourceStack, ClearCLImageInterface sourcePlane, ClearCLImageInterface destination) {
        boolean result = MultiplyStackWithPlane.multiplyStackWithPlane(getCLIJ2(), sourceStack, sourcePlane, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.CountNonZeroPixels2DSphere
    //----------------------------------------------------
    /**
     * Counts non-zero pixels in a sphere around every pixel. 
     * 
     * Put the number in the result image.
     */
    static boolean count_non_zero_pixels2d_sphere(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4) {
        boolean result = CountNonZeroPixels2DSphere.countNonZeroPixels2DSphere(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.CountNonZeroPixelsSliceBySliceSphere
    //----------------------------------------------------
    /**
     * Counts non-zero pixels in a sphere around every pixel slice by slice in a stack. 
     * 
     *  It puts the resulting number in the destination image stack.
     */
    static boolean count_non_zero_pixels_slice_by_slice_sphere(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4) {
        boolean result = CountNonZeroPixelsSliceBySliceSphere.countNonZeroPixelsSliceBySliceSphere(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.CountNonZeroVoxels3DSphere
    //----------------------------------------------------
    /**
     * Counts non-zero voxels in a sphere around every voxel. 
     * 
     * Put the number in the result image.
     */
    static boolean count_non_zero_voxels3d_sphere(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5) {
        boolean result = CountNonZeroVoxels3DSphere.countNonZeroVoxels3DSphere(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.SumZProjection
    //----------------------------------------------------
    /**
     * Determines the sum intensity projection of an image along Z.
     */
    static boolean sum_z_projection(ClearCLImageInterface source, ClearCLImageInterface destination_sum) {
        boolean result = SumZProjection.sumZProjection(getCLIJ2(), source, destination_sum);
        return result;
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
    static double sum_of_all_pixels(ClearCLImageInterface source) {
        double result = SumOfAllPixels.sumOfAllPixels(getCLIJ2(), source);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.CenterOfMass
    //----------------------------------------------------
    /**
     * Determines the center of mass of an image or image stack. 
     * 
     * It writes the result in the results table
     * in the columns MassX, MassY and MassZ.
     */
    static double[] center_of_mass(ClearCLBuffer source) {
        double[] result = CenterOfMass.centerOfMass(getCLIJ2(), source);
        return result;
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
    static boolean invert(ClearCLImageInterface source, ClearCLImageInterface destination) {
        boolean result = Invert.invert(getCLIJ2(), source, destination);
        return result;
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
    static boolean downsample_slice_by_slice_half_median(ClearCLImageInterface source, ClearCLImageInterface destination) {
        boolean result = DownsampleSliceBySliceHalfMedian.downsampleSliceBySliceHalfMedian(getCLIJ2(), source, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.LocalThreshold
    //----------------------------------------------------
    /**
     * Computes a binary image with pixel values 0 and 1 depending on if a pixel value x in image X 
     * was above of equal to the pixel value m in mask image M.
     * 
     * <pre>f(x) = (1 if (x >=  m)); (0 otherwise)</pre>
     */
    static boolean local_threshold(ClearCLImageInterface source, ClearCLImageInterface localThreshold, ClearCLImageInterface destination) {
        boolean result = LocalThreshold.localThreshold(getCLIJ2(), source, localThreshold, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.GradientX
    //----------------------------------------------------
    /**
     * Computes the gradient of gray values along X. 
     * 
     * Assuming a, b and c are three adjacent
     *  pixels in X direction. In the target image will be saved as: <pre>b' = c - a;</pre>
     */
    static boolean gradient_x(ClearCLBuffer source, ClearCLBuffer destination) {
        boolean result = GradientX.gradientX(getCLIJ2(), source, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.GradientY
    //----------------------------------------------------
    /**
     * Computes the gradient of gray values along Y. 
     * 
     * Assuming a, b and c are three adjacent
     *  pixels in Y direction. In the target image will be saved as: <pre>b' = c - a;</pre>
     */
    static boolean gradient_y(ClearCLBuffer source, ClearCLBuffer destination) {
        boolean result = GradientY.gradientY(getCLIJ2(), source, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.GradientZ
    //----------------------------------------------------
    /**
     * Computes the gradient of gray values along Z. 
     * 
     * Assuming a, b and c are three adjacent
     *  pixels in Z direction. In the target image will be saved as: <pre>b' = c - a;</pre>
     */
    static boolean gradient_z(ClearCLBuffer source, ClearCLBuffer destination) {
        boolean result = GradientZ.gradientZ(getCLIJ2(), source, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.MultiplyImageAndCoordinate
    //----------------------------------------------------
    /**
     * Multiplies all pixel intensities with the x, y or z coordinate, depending on specified dimension.
     */
    static boolean multiply_image_and_coordinate(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3) {
        boolean result = MultiplyImageAndCoordinate.multiplyImageAndCoordinate(getCLIJ2(), arg1, arg2, new Double (arg3).intValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.Mean2DBox
    //----------------------------------------------------
    /**
     * Computes the local mean average of a pixels rectangular neighborhood. 
     * 
     * The rectangles size is specified by 
     * its half-width and half-height (radius).
     */
    static boolean mean2d_box(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        boolean result = Mean2DBox.mean2DBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.Mean2DSphere
    //----------------------------------------------------
    /**
     * Computes the local mean average of a pixels ellipsoidal neighborhood. 
     * 
     * The ellipses size is specified by 
     * its half-width and half-height (radius).
     */
    static boolean mean2d_sphere(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        boolean result = Mean2DSphere.mean2DSphere(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.Mean3DBox
    //----------------------------------------------------
    /**
     * Computes the local mean average of a pixels cube neighborhood. 
     * 
     * The cubes size is specified by 
     * its half-width, half-height and half-depth (radius).
     */
    static boolean mean3d_box(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        boolean result = Mean3DBox.mean3DBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return result;
    }

    /**
     * Computes the local mean average of a pixels rectangular neighborhood. 
     * 
     * The rectangles size is specified by 
     * its half-width and half-height (radius).
     */
    static boolean mean_box(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        boolean result = Mean3DBox.meanBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.Mean3DSphere
    //----------------------------------------------------
    /**
     * Computes the local mean average of a pixels spherical neighborhood. 
     * 
     * The spheres size is specified by 
     * its half-width, half-height and half-depth (radius).
     */
    static boolean mean3d_sphere(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        boolean result = Mean3DSphere.mean3DSphere(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return result;
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
    static boolean mean_slice_by_slice_sphere(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        boolean result = MeanSliceBySliceSphere.meanSliceBySliceSphere(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return result;
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
    static double mean_of_all_pixels(ClearCLImageInterface source) {
        double result = MeanOfAllPixels.meanOfAllPixels(getCLIJ2(), source);
        return result;
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
    static boolean median2d_box(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        boolean result = Median2DBox.median2DBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return result;
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
    static boolean median2d_sphere(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        boolean result = Median2DSphere.median2DSphere(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return result;
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
    static boolean median3d_box(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        boolean result = Median3DBox.median3DBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return result;
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
    static boolean median3d_sphere(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        boolean result = Median3DSphere.median3DSphere(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return result;
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
    static boolean median3d_slice_by_slice_box(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        boolean result = MedianSliceBySliceBox.median3DSliceBySliceBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return result;
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
    static boolean median3d_slice_by_slice_sphere(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        boolean result = MedianSliceBySliceSphere.median3DSliceBySliceSphere(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.Maximum2DSphere
    //----------------------------------------------------
    /**
     * Computes the local maximum of a pixels ellipsoidal neighborhood. 
     * 
     * The ellipses size is specified by 
     * its half-width and half-height (radius).
     */
    static boolean maximum2d_sphere(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        boolean result = Maximum2DSphere.maximum2DSphere(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.Maximum3DSphere
    //----------------------------------------------------
    /**
     * Computes the local maximum of a pixels spherical neighborhood. 
     * 
     * The spheres size is specified by 
     * its half-width, half-height and half-depth (radius).
     */
    static boolean maximum3d_sphere(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        boolean result = Maximum3DSphere.maximum3DSphere(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.Maximum2DBox
    //----------------------------------------------------
    /**
     * Computes the local maximum of a pixels rectangular neighborhood. 
     * 
     * The rectangles size is specified by 
     * its half-width and half-height (radius).
     */
    static boolean maximum2d_box(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        boolean result = Maximum2DBox.maximum2DBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return result;
    }

    /**
     * Computes the local maximum of a pixels rectangular neighborhood. 
     * 
     * The rectangles size is specified by 
     * its half-width and half-height (radius).
     */
    static boolean maximum_box(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        boolean result = Maximum2DBox.maximumBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.Maximum3DBox
    //----------------------------------------------------
    /**
     * Computes the local maximum of a pixels cube neighborhood. 
     * 
     * The cubes size is specified by 
     * its half-width, half-height and half-depth (radius).
     */
    static boolean maximum3d_box(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        boolean result = Maximum3DBox.maximum3DBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return result;
    }

    /**
     * Computes the local maximum of a pixels rectangular neighborhood. 
     * 
     * The rectangles size is specified by 
     * its half-width and half-height (radius).
     */
    static boolean maximum_box(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        boolean result = Maximum3DBox.maximumBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return result;
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
    static boolean maximum3d_slice_by_slice_sphere(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        boolean result = MaximumSliceBySliceSphere.maximum3DSliceBySliceSphere(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.Minimum2DSphere
    //----------------------------------------------------
    /**
     * Computes the local minimum of a pixels ellipsoidal neighborhood. 
     * 
     * The ellipses size is specified by 
     * its half-width and half-height (radius).
     */
    static boolean minimum2d_sphere(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        boolean result = Minimum2DSphere.minimum2DSphere(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.Minimum3DSphere
    //----------------------------------------------------
    /**
     * Computes the local minimum of a pixels spherical neighborhood. 
     * 
     * The spheres size is specified by 
     * its half-width, half-height and half-depth (radius).
     */
    static boolean minimum3d_sphere(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        boolean result = Minimum3DSphere.minimum3DSphere(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.Minimum2DBox
    //----------------------------------------------------
    /**
     * Computes the local minimum of a pixels rectangular neighborhood. 
     * 
     * The rectangles size is specified by 
     * its half-width and half-height (radius).
     */
    static boolean minimum2d_box(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        boolean result = Minimum2DBox.minimum2DBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return result;
    }

    /**
     * Computes the local minimum of a pixels rectangular neighborhood. 
     * 
     * The rectangles size is specified by 
     * its half-width and half-height (radius).
     */
    static boolean minimum_box(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        boolean result = Minimum2DBox.minimumBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.Minimum3DBox
    //----------------------------------------------------
    /**
     * Computes the local minimum of a pixels cube neighborhood. 
     * 
     * The cubes size is specified by 
     * its half-width, half-height and half-depth (radius).
     */
    static boolean minimum3d_box(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        boolean result = Minimum3DBox.minimum3DBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return result;
    }

    /**
     * Computes the local minimum of a pixels rectangular neighborhood. 
     * 
     * The rectangles size is specified by 
     * its half-width and half-height (radius).
     */
    static boolean minimum_box(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        boolean result = Minimum3DBox.minimumBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return result;
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
    static boolean minimum3d_slice_by_slice_sphere(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        boolean result = MinimumSliceBySliceSphere.minimum3DSliceBySliceSphere(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.MultiplyImages
    //----------------------------------------------------
    /**
     * Multiplies all pairs of pixel values x and y from two image X and Y.
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
    static boolean multiply_images(ClearCLImageInterface factor1, ClearCLImageInterface factor2, ClearCLImageInterface destination) {
        boolean result = MultiplyImages.multiplyImages(getCLIJ2(), factor1, factor2, destination);
        return result;
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
    static boolean gaussian_blur(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        boolean result = GaussianBlur2D.gaussianBlur(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue());
        return result;
    }

    /**
     * Computes the Gaussian blurred image of an image given two sigma values in X and Y. 
     * 
     * Thus, the filterkernel can have non-isotropic shape.
     * 
     * The implementation is done separable. In case a sigma equals zero, the direction is not blurred.
     */
    static boolean gaussian_blur2d(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        boolean result = GaussianBlur2D.gaussianBlur2D(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue());
        return result;
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
    static boolean gaussian_blur(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        boolean result = GaussianBlur3D.gaussianBlur(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue());
        return result;
    }

    /**
     * Computes the Gaussian blurred image of an image given two sigma values in X, Y and Z. 
     * 
     * Thus, the filterkernel can have non-isotropic shape.
     * 
     * The implementation is done separable. In case a sigma equals zero, the direction is not blurred.
     */
    static boolean gaussian_blur3d(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        boolean result = GaussianBlur3D.gaussianBlur3D(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue());
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.BlurSliceBySlice
    //----------------------------------------------------

    // net.haesleinhuepf.clij2.plugins.ResliceBottom
    //----------------------------------------------------
    /**
     * Flippes Y and Z axis of an image stack. This operation is similar to ImageJs 'Reslice [/]' method but
     * offers less flexibility such as interpolation.
     */
    static boolean reslice_bottom(ClearCLImageInterface source, ClearCLImageInterface destination) {
        boolean result = ResliceBottom.resliceBottom(getCLIJ2(), source, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.ResliceTop
    //----------------------------------------------------
    /**
     * Flippes Y and Z axis of an image stack. This operation is similar to ImageJs 'Reslice [/]' method but
     * offers less flexibility such as interpolation.
     */
    static boolean reslice_top(ClearCLImageInterface source, ClearCLImageInterface destination) {
        boolean result = ResliceTop.resliceTop(getCLIJ2(), source, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.ResliceLeft
    //----------------------------------------------------
    /**
     * Flippes X, Y and Z axis of an image stack. This operation is similar to ImageJs 'Reslice [/]' method 
     *  but offers less flexibility such as interpolation.
     */
    static boolean reslice_left(ClearCLImageInterface source, ClearCLImageInterface destination) {
        boolean result = ResliceLeft.resliceLeft(getCLIJ2(), source, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.ResliceRight
    //----------------------------------------------------
    /**
     * Flippes X, Y and Z axis of an image stack. This operation is similar to ImageJs 'Reslice [/]' method 
     *  but offers less flexibility such as interpolation.
     */
    static boolean reslice_right(ClearCLImageInterface source, ClearCLImageInterface destination) {
        boolean result = ResliceRight.resliceRight(getCLIJ2(), source, destination);
        return result;
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
    static boolean rotate2d(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, boolean arg4) {
        boolean result = Rotate2D.rotate2D(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), arg4);
        return result;
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
    static boolean rotate3d(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, boolean arg6) {
        boolean result = Rotate3D.rotate3D(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), arg6);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.Scale2D
    //----------------------------------------------------
    /**
     * Scales an image with a given factor.
     */
    static boolean scale(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        boolean result = Scale2D.scale(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return result;
    }

    /**
     * Scales an image with a given factor.
     */
    static boolean scale(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4) {
        boolean result = Scale2D.scale(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue());
        return result;
    }

    /**
     * Scales an image with a given factor.
     */
    static boolean scale2d(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4) {
        boolean result = Scale2D.scale2D(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue());
        return result;
    }

    /**
     * Scales an image with a given factor.
     */
    static boolean scale2d(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, boolean arg5) {
        boolean result = Scale2D.scale2D(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), arg5);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.Scale3D
    //----------------------------------------------------
    /**
     * Scales an image with a given factor.
     */
    static boolean scale3d(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5) {
        boolean result = Scale3D.scale3D(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue());
        return result;
    }

    /**
     * Scales an image with a given factor.
     */
    static boolean scale3d(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, boolean arg6) {
        boolean result = Scale3D.scale3D(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), arg6);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.Translate2D
    //----------------------------------------------------
    /**
     * Translate an image stack in X and Y.
     */
    static boolean translate2d(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4) {
        boolean result = Translate2D.translate2D(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.Translate3D
    //----------------------------------------------------
    /**
     * Translate an image stack in X, Y and Z.
     */
    static boolean translate3d(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5) {
        boolean result = Translate3D.translate3D(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue());
        return result;
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
    static boolean add_image_and_scalar(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3) {
        boolean result = AddImageAndScalar.addImageAndScalar(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.DetectMinimaBox
    //----------------------------------------------------
    /**
     * Detects local minima in a given square/cubic neighborhood. 
     * 
     * Pixels in the resulting image are set to 1 if there is no other pixel in a given radius which has a 
     * lower intensity, and to 0 otherwise.
     */
    static boolean detect_minima_box(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        boolean result = DetectMinimaBox.detectMinimaBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.DetectMaximaBox
    //----------------------------------------------------
    /**
     * Detects local maxima in a given square/cubic neighborhood. 
     * 
     * Pixels in the resulting image are set to 1 if there is no other pixel in a given radius which has a 
     * higher intensity, and to 0 otherwise.
     */
    static boolean detect_maxima_box(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        boolean result = DetectMaximaBox.detectMaximaBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.DetectMaximaSliceBySliceBox
    //----------------------------------------------------
    /**
     * Detects local maxima in a given square neighborhood of an input image stack. 
     * 
     * The input image stack is processed slice by slice. Pixels in the resulting image are set to 1 if 
     * there is no other pixel in a given radius which has a higher intensity, and to 0 otherwise.
     */
    static boolean detect_maxima_slice_by_slice_box(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4) {
        boolean result = DetectMaximaSliceBySliceBox.detectMaximaSliceBySliceBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.DetectMinimaSliceBySliceBox
    //----------------------------------------------------
    /**
     * Detects local minima in a given square neighborhood of an input image stack. 
     * 
     * The input image stack is processed slice by slice. Pixels in the resulting image are set to 1 if 
     * there is no other pixel in a given radius which has a lower intensity, and to 0 otherwise.
     */
    static boolean detect_minima_slice_by_slice_box(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4) {
        boolean result = DetectMinimaSliceBySliceBox.detectMinimaSliceBySliceBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return result;
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
    static double maximum_of_all_pixels(ClearCLImageInterface source) {
        double result = MaximumOfAllPixels.maximumOfAllPixels(getCLIJ2(), source);
        return result;
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
    static double minimum_of_all_pixels(ClearCLImageInterface source) {
        double result = MinimumOfAllPixels.minimumOfAllPixels(getCLIJ2(), source);
        return result;
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
    static boolean top_hat_octagon_slice_by_slice(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        boolean result = TopHatOctagonSliceBySlice.topHatOctagonSliceBySlice(getCLIJx(), arg1, arg2, new Double (arg3).intValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.SetColumn
    //----------------------------------------------------
    /**
     * Sets all pixel values x of a given column in X to a constant value v.
     */
    static boolean set_column(ClearCLImageInterface arg1, double arg2, double arg3) {
        boolean result = SetColumn.setColumn(getCLIJ2(), arg1, new Double (arg2).intValue(), new Double (arg3).floatValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.SetRow
    //----------------------------------------------------
    /**
     * Sets all pixel values x of a given row in X to a constant value v.
     */
    static boolean set_row(ClearCLImageInterface arg1, double arg2, double arg3) {
        boolean result = SetRow.setRow(getCLIJ2(), arg1, new Double (arg2).intValue(), new Double (arg3).floatValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.SumYProjection
    //----------------------------------------------------
    /**
     * Determines the sum intensity projection of an image along Z.
     */
    static boolean sum_y_projection(ClearCLImageInterface source, ClearCLImageInterface destination) {
        boolean result = SumYProjection.sumYProjection(getCLIJ2(), source, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.AverageDistanceOfTouchingNeighbors
    //----------------------------------------------------
    /**
     * Takes a touch matrix and a distance matrix to determine the average distance of touching neighbors 
     *  for every object.
     */
    static boolean average_distance_of_touching_neighbors(ClearCLBuffer distance_matrix, ClearCLBuffer touch_matrix, ClearCLBuffer average_distancelist_destination) {
        boolean result = AverageDistanceOfTouchingNeighbors.averageDistanceOfTouchingNeighbors(getCLIJ2(), distance_matrix, touch_matrix, average_distancelist_destination);
        return result;
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
    static boolean labelled_spots_to_point_list(ClearCLBuffer input_labelled_spots, ClearCLBuffer destination_pointlist) {
        boolean result = LabelledSpotsToPointList.labelledSpotsToPointList(getCLIJ2(), input_labelled_spots, destination_pointlist);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.LabelSpots
    //----------------------------------------------------
    /**
     * Transforms a binary image with single pixles set to 1 to a labelled spots image. 
     * 
     * Transforms a spots image as resulting from maximum/minimum detection in an image of the same size where every spot has a number 1, 2, ... n.
     */
    static boolean label_spots(ClearCLBuffer input_spots, ClearCLBuffer labelled_spots_destination) {
        boolean result = LabelSpots.labelSpots(getCLIJ2(), input_spots, labelled_spots_destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.MinimumDistanceOfTouchingNeighbors
    //----------------------------------------------------
    /**
     * Takes a touch matrix and a distance matrix to determine the shortest distance of touching neighbors for every object.
     */
    static boolean minimum_distance_of_touching_neighbors(ClearCLBuffer distance_matrix, ClearCLBuffer touch_matrix, ClearCLBuffer minimum_distancelist_destination) {
        boolean result = MinimumDistanceOfTouchingNeighbors.minimumDistanceOfTouchingNeighbors(getCLIJ2(), distance_matrix, touch_matrix, minimum_distancelist_destination);
        return result;
    }


    // net.haesleinhuepf.clijx.io.WriteVTKLineListToDisc
    //----------------------------------------------------
    /**
     * Takes a point list image representing n points (n*2 for 2D points, n*3 for 3D points) and a corresponding touch matrix , sized (n+1)*(n+1), and exports them in VTK format.
     */
    static boolean write_v_t_k_line_list_to_disc(ClearCLBuffer pointlist, ClearCLBuffer touch_matrix, String filename) {
        boolean result = WriteVTKLineListToDisc.writeVTKLineListToDisc(getCLIJx(), pointlist, touch_matrix, filename);
        return result;
    }


    // net.haesleinhuepf.clijx.io.WriteXYZPointListToDisc
    //----------------------------------------------------
    /**
     * Takes a point list image representing n points (n*2 for 2D points, n*3 for 3D points) and exports them in XYZ format.
     */
    static boolean write_xyz_point_list_to_disc(ClearCLBuffer pointlist, String filename) {
        boolean result = WriteXYZPointListToDisc.writeXYZPointListToDisc(getCLIJx(), pointlist, filename);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.SetWhereXgreaterThanY
    //----------------------------------------------------
    /**
     * Sets all pixel values a of a given image A to a constant value v in case its coordinates x > y. 
     * 
     * Otherwise the pixel is not overwritten.
     * If you want to initialize an identity transfrom matrix, set all pixels to 0 first.
     */
    static boolean set_where_xgreater_than_y(ClearCLImageInterface arg1, double arg2) {
        boolean result = SetWhereXgreaterThanY.setWhereXgreaterThanY(getCLIJ2(), arg1, new Double (arg2).floatValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.SetWhereXsmallerThanY
    //----------------------------------------------------
    /**
     * Sets all pixel values a of a given image A to a constant value v in case its coordinates x < y. 
     * 
     * Otherwise the pixel is not overwritten.
     * If you want to initialize an identity transfrom matrix, set all pixels to 0 first.
     */
    static boolean set_where_xsmaller_than_y(ClearCLImageInterface arg1, double arg2) {
        boolean result = SetWhereXsmallerThanY.setWhereXsmallerThanY(getCLIJ2(), arg1, new Double (arg2).floatValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.SetNonZeroPixelsToPixelIndex
    //----------------------------------------------------
    /**
     * Sets all pixels in an image which are not zero to the index of the pixel. 
     * 
     * This can be used for Connected Components Analysis.
     */
    static boolean set_non_zero_pixels_to_pixel_index(ClearCLImageInterface source, ClearCLImageInterface destination) {
        boolean result = SetNonZeroPixelsToPixelIndex.setNonZeroPixelsToPixelIndex(getCLIJ2(), source, destination);
        return result;
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
    static boolean close_index_gaps_in_label_map(ClearCLBuffer labeling_input, ClearCLImageInterface labeling_destination) {
        boolean result = CloseIndexGapsInLabelMap.closeIndexGapsInLabelMap(getCLIJ2(), labeling_input, labeling_destination);
        return result;
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
    static boolean centroids_of_labels(ClearCLBuffer source, ClearCLBuffer pointlist_destination) {
        boolean result = CentroidsOfLabels.centroidsOfLabels(getCLIJ2(), source, pointlist_destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.SetRampX
    //----------------------------------------------------
    /**
     * Sets all pixel values to their X coordinate
     */
    static boolean set_ramp_x(ClearCLImageInterface source) {
        boolean result = SetRampX.setRampX(getCLIJ2(), source);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.SetRampY
    //----------------------------------------------------
    /**
     * Sets all pixel values to their Y coordinate
     */
    static boolean set_ramp_y(ClearCLImageInterface source) {
        boolean result = SetRampY.setRampY(getCLIJ2(), source);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.SetRampZ
    //----------------------------------------------------
    /**
     * Sets all pixel values to their Z coordinate
     */
    static boolean set_ramp_z(ClearCLImageInterface source) {
        boolean result = SetRampZ.setRampZ(getCLIJ2(), source);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.SubtractImageFromScalar
    //----------------------------------------------------
    /**
     * Subtracts one image X from a scalar s pixel wise.
     * 
     * <pre>f(x, s) = s - x</pre>
     */
    static boolean subtract_image_from_scalar(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3) {
        boolean result = SubtractImageFromScalar.subtractImageFromScalar(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.ThresholdDefault
    //----------------------------------------------------
    /**
     * The automatic thresholder utilizes the Default threshold method implemented in ImageJ using a histogram determined on 
     * the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.
     */
    static boolean threshold_default(ClearCLBuffer input, ClearCLBuffer destination) {
        boolean result = ThresholdDefault.thresholdDefault(getCLIJ2(), input, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.ThresholdOtsu
    //----------------------------------------------------
    /**
     * The automatic thresholder utilizes the Otsu threshold method implemented in ImageJ using a histogram determined on 
     * the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.
     */
    static boolean threshold_otsu(ClearCLBuffer input, ClearCLBuffer destination) {
        boolean result = ThresholdOtsu.thresholdOtsu(getCLIJ2(), input, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.ThresholdHuang
    //----------------------------------------------------
    /**
     * The automatic thresholder utilizes the Huang threshold method implemented in ImageJ using a histogram determined on 
     * the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.
     */
    static boolean threshold_huang(ClearCLBuffer input, ClearCLBuffer destination) {
        boolean result = ThresholdHuang.thresholdHuang(getCLIJ2(), input, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.ThresholdIntermodes
    //----------------------------------------------------
    /**
     * The automatic thresholder utilizes the Intermodes threshold method implemented in ImageJ using a histogram determined on 
     * the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.
     */
    static boolean threshold_intermodes(ClearCLBuffer input, ClearCLBuffer destination) {
        boolean result = ThresholdIntermodes.thresholdIntermodes(getCLIJ2(), input, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.ThresholdIsoData
    //----------------------------------------------------
    /**
     * The automatic thresholder utilizes the IsoData threshold method implemented in ImageJ using a histogram determined on 
     * the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.
     */
    static boolean threshold_iso_data(ClearCLBuffer input, ClearCLBuffer destination) {
        boolean result = ThresholdIsoData.thresholdIsoData(getCLIJ2(), input, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.ThresholdIJ_IsoData
    //----------------------------------------------------
    /**
     * The automatic thresholder utilizes the IJ_IsoData threshold method implemented in ImageJ using a histogram determined on 
     * the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.
     */
    static boolean threshold_ij__iso_data(ClearCLBuffer input, ClearCLBuffer destination) {
        boolean result = ThresholdIJ_IsoData.thresholdIJ_IsoData(getCLIJ2(), input, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.ThresholdLi
    //----------------------------------------------------
    /**
     * The automatic thresholder utilizes the Li threshold method implemented in ImageJ using a histogram determined on 
     * the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.
     */
    static boolean threshold_li(ClearCLBuffer input, ClearCLBuffer destination) {
        boolean result = ThresholdLi.thresholdLi(getCLIJ2(), input, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.ThresholdMaxEntropy
    //----------------------------------------------------
    /**
     * The automatic thresholder utilizes the MaxEntropy threshold method implemented in ImageJ using a histogram determined on 
     * the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.
     */
    static boolean threshold_max_entropy(ClearCLBuffer input, ClearCLBuffer destination) {
        boolean result = ThresholdMaxEntropy.thresholdMaxEntropy(getCLIJ2(), input, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.ThresholdMean
    //----------------------------------------------------
    /**
     * The automatic thresholder utilizes the Mean threshold method implemented in ImageJ using a histogram determined on 
     * the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.
     */
    static boolean threshold_mean(ClearCLBuffer input, ClearCLBuffer destination) {
        boolean result = ThresholdMean.thresholdMean(getCLIJ2(), input, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.ThresholdMinError
    //----------------------------------------------------
    /**
     * The automatic thresholder utilizes the MinError threshold method implemented in ImageJ using a histogram determined on 
     * the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.
     */
    static boolean threshold_min_error(ClearCLBuffer input, ClearCLBuffer destination) {
        boolean result = ThresholdMinError.thresholdMinError(getCLIJ2(), input, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.ThresholdMinimum
    //----------------------------------------------------
    /**
     * The automatic thresholder utilizes the Minimum threshold method implemented in ImageJ using a histogram determined on 
     * the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.
     */
    static boolean threshold_minimum(ClearCLBuffer input, ClearCLBuffer destination) {
        boolean result = ThresholdMinimum.thresholdMinimum(getCLIJ2(), input, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.ThresholdMoments
    //----------------------------------------------------
    /**
     * The automatic thresholder utilizes the Moments threshold method implemented in ImageJ using a histogram determined on 
     * the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.
     */
    static boolean threshold_moments(ClearCLBuffer input, ClearCLBuffer destination) {
        boolean result = ThresholdMoments.thresholdMoments(getCLIJ2(), input, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.ThresholdPercentile
    //----------------------------------------------------
    /**
     * The automatic thresholder utilizes the Percentile threshold method implemented in ImageJ using a histogram determined on 
     * the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.
     */
    static boolean threshold_percentile(ClearCLBuffer input, ClearCLBuffer destination) {
        boolean result = ThresholdPercentile.thresholdPercentile(getCLIJ2(), input, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.ThresholdRenyiEntropy
    //----------------------------------------------------
    /**
     * The automatic thresholder utilizes the RenyiEntropy threshold method implemented in ImageJ using a histogram determined on 
     * the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.
     */
    static boolean threshold_renyi_entropy(ClearCLBuffer input, ClearCLBuffer destination) {
        boolean result = ThresholdRenyiEntropy.thresholdRenyiEntropy(getCLIJ2(), input, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.ThresholdShanbhag
    //----------------------------------------------------
    /**
     * The automatic thresholder utilizes the Shanbhag threshold method implemented in ImageJ using a histogram determined on 
     * the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.
     */
    static boolean threshold_shanbhag(ClearCLBuffer input, ClearCLBuffer destination) {
        boolean result = ThresholdShanbhag.thresholdShanbhag(getCLIJ2(), input, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.ThresholdTriangle
    //----------------------------------------------------
    /**
     * The automatic thresholder utilizes the Triangle threshold method implemented in ImageJ using a histogram determined on 
     * the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.
     */
    static boolean threshold_triangle(ClearCLBuffer input, ClearCLBuffer destination) {
        boolean result = ThresholdTriangle.thresholdTriangle(getCLIJ2(), input, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.ThresholdYen
    //----------------------------------------------------
    /**
     * The automatic thresholder utilizes the Yen threshold method implemented in ImageJ using a histogram determined on 
     * the GPU to create binary images as similar as possible to ImageJ 'Apply Threshold' method.
     */
    static boolean threshold_yen(ClearCLBuffer input, ClearCLBuffer destination) {
        boolean result = ThresholdYen.thresholdYen(getCLIJ2(), input, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.ExcludeLabelsSubSurface
    //----------------------------------------------------
    /**
     * This operation follows a ray from a given position towards a label (or opposite direction) and checks if  there is another label between the label an the image border. 
     * 
     * If yes, this label is eliminated from the label map.
     */
    static boolean exclude_labels_sub_surface(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4, double arg5, double arg6) {
        boolean result = ExcludeLabelsSubSurface.excludeLabelsSubSurface(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).floatValue(), new Double (arg5).floatValue(), new Double (arg6).floatValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.ExcludeLabelsOnSurface
    //----------------------------------------------------
    /**
     * This operation follows a ray from a given position towards a label (or opposite direction) and checks if  there is another label between the label an the image border. 
     * 
     * If yes, this label is eliminated from the label map.
     */
    static boolean exclude_labels_on_surface(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4, double arg5, double arg6) {
        boolean result = ExcludeLabelsOnSurface.excludeLabelsOnSurface(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).floatValue(), new Double (arg5).floatValue(), new Double (arg6).floatValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.SetPlane
    //----------------------------------------------------
    /**
     * Sets all pixel values x of a given plane in X to a constant value v.
     */
    static boolean set_plane(ClearCLImageInterface arg1, double arg2, double arg3) {
        boolean result = SetPlane.setPlane(getCLIJ2(), arg1, new Double (arg2).intValue(), new Double (arg3).floatValue());
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.tenengradfusion.TenengradFusion
    //----------------------------------------------------
    /**
     * Fuses #n# image stacks using Tenengrads algorithm.
     */
    static boolean tenengrad_fusion(ClearCLBuffer arg1, float[] arg2, float arg3, ClearCLBuffer[] arg4) {
        boolean result = TenengradFusion.tenengradFusion(getCLIJx(), arg1, arg2, arg3, arg4);
        return result;
    }

    /**
     * Fuses #n# image stacks using Tenengrads algorithm.
     */
    static boolean tenengrad_fusion(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, double arg6, double arg7) {
        boolean result = TenengradFusion.tenengradFusion(getCLIJx(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), new Double (arg6).floatValue(), new Double (arg7).floatValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.ImageToStack
    //----------------------------------------------------
    /**
     * Copies a single slice into a stack a given number of times.
     */
    static boolean image_to_stack(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        boolean result = ImageToStack.imageToStack(getCLIJ2(), arg1, arg2, new Double (arg3).intValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.SumXProjection
    //----------------------------------------------------
    /**
     * Determines the sum intensity projection of an image along Z.
     */
    static boolean sum_x_projection(ClearCLImageInterface source, ClearCLImageInterface destination) {
        boolean result = SumXProjection.sumXProjection(getCLIJ2(), source, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.SumImageSliceBySlice
    //----------------------------------------------------
    /**
     * Sums all pixels slice by slice and returns them in an array.
     */
    static boolean sum_image_slice_by_slice(ClearCLImageInterface source, ClearCLImageInterface destination) {
        boolean result = SumImageSliceBySlice.sumImageSliceBySlice(getCLIJ2(), source, destination);
        return result;
    }

    /**
     * Sums all pixels slice by slice and returns them in an array.
     */
    static double[] sum_image_slice_by_slice(ClearCLImageInterface arg1) {
        double[] result = SumImageSliceBySlice.sumImageSliceBySlice(getCLIJ2(), arg1);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.MultiplyImageStackWithScalars
    //----------------------------------------------------
    /**
     * Multiplies all pixels value x in a given image X with a constant scalar s from a list of scalars.
     * 
     * <pre>f(x, s) = x * s</pre>
     */
    static boolean multiply_image_stack_with_scalars(ClearCLImageInterface arg1, ClearCLImageInterface arg2, float[] arg3) {
        boolean result = MultiplyImageStackWithScalars.multiplyImageStackWithScalars(getCLIJ2(), arg1, arg2, arg3);
        return result;
    }

    /**
     * Multiplies all pixels value x in a given image X with a constant scalar s from a list of scalars.
     * 
     * <pre>f(x, s) = x * s</pre>
     */
    static boolean multiply_image_stack_with_scalars(ClearCLImageInterface arg1, ClearCLImageInterface arg2, ClearCLBuffer arg3) {
        boolean result = MultiplyImageStackWithScalars.multiplyImageStackWithScalars(getCLIJ2(), arg1, arg2, arg3);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.Print
    //----------------------------------------------------
    /**
     * Visualises an image on standard out (console).
     */
    static boolean print(ClearCLImageInterface input) {
        boolean result = Print.print(getCLIJ2(), input);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.VoronoiOctagon
    //----------------------------------------------------
    /**
     * Takes a binary image and dilates the regions using a octagon shape until they touch. 
     * 
     * The pixels where  the regions touched are afterwards returned as binary image which corresponds to the Voronoi diagram.
     */
    static boolean voronoi_octagon(ClearCLBuffer input, ClearCLBuffer destination) {
        boolean result = VoronoiOctagon.voronoiOctagon(getCLIJ2(), input, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.SetImageBorders
    //----------------------------------------------------
    /**
     * Sets all pixel values at the image border to a given value.
     */
    static boolean set_image_borders(ClearCLImageInterface arg1, double arg2) {
        boolean result = SetImageBorders.setImageBorders(getCLIJ2(), arg1, new Double (arg2).floatValue());
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.Skeletonize
    //----------------------------------------------------

    // net.haesleinhuepf.clij2.plugins.FloodFillDiamond
    //----------------------------------------------------
    /**
     * Replaces recursively all pixels of value a with value b if the pixels have a neighbor with value b.
     */
    static boolean flood_fill_diamond(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4) {
        boolean result = FloodFillDiamond.floodFillDiamond(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.BinaryFillHoles
    //----------------------------------------------------
    /**
     * Fills holes (pixels with value 0 surrounded by pixels with value 1) in a binary image.
     */
    static boolean binary_fill_holes(ClearCLImageInterface source, ClearCLImageInterface destination) {
        boolean result = BinaryFillHoles.binaryFillHoles(getCLIJ2(), source, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.ConnectedComponentsLabelingDiamond
    //----------------------------------------------------
    /**
     * Performs connected components analysis inspecting the diamond neighborhood of every pixel to a binary image and generates a label map.
     */
    static boolean connected_components_labeling_diamond(ClearCLImageInterface binary_input, ClearCLImageInterface labeling_destination) {
        boolean result = ConnectedComponentsLabelingDiamond.connectedComponentsLabelingDiamond(getCLIJ2(), binary_input, labeling_destination);
        return result;
    }

    /**
     * Performs connected components analysis inspecting the diamond neighborhood of every pixel to a binary image and generates a label map.
     */
    static boolean connected_components_labeling_diamond(ClearCLImageInterface arg1, ClearCLImageInterface arg2, boolean arg3) {
        boolean result = ConnectedComponentsLabelingDiamond.connectedComponentsLabelingDiamond(getCLIJ2(), arg1, arg2, arg3);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.ConnectedComponentsLabelingBox
    //----------------------------------------------------
    /**
     * Performs connected components analysis inspecting the box neighborhood of every pixel to a binary image and generates a label map.
     */
    static boolean connected_components_labeling_box(ClearCLImageInterface binary_input, ClearCLImageInterface labeling_destination) {
        boolean result = ConnectedComponentsLabelingBox.connectedComponentsLabelingBox(getCLIJ2(), binary_input, labeling_destination);
        return result;
    }

    /**
     * Performs connected components analysis inspecting the box neighborhood of every pixel to a binary image and generates a label map.
     */
    static boolean connected_components_labeling_box(ClearCLImageInterface arg1, ClearCLImageInterface arg2, boolean arg3) {
        boolean result = ConnectedComponentsLabelingBox.connectedComponentsLabelingBox(getCLIJ2(), arg1, arg2, arg3);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.SetRandom
    //----------------------------------------------------
    /**
     * Fills an image or image stack with uniformly distributed random numbers between given minimum and maximum values. 
     * 
     * Recommendation: For the seed, use getTime().
     */
    static boolean set_random(ClearCLBuffer arg1, double arg2, double arg3) {
        boolean result = SetRandom.setRandom(getCLIJ2(), arg1, new Double (arg2).floatValue(), new Double (arg3).floatValue());
        return result;
    }

    /**
     * Fills an image or image stack with uniformly distributed random numbers between given minimum and maximum values. 
     * 
     * Recommendation: For the seed, use getTime().
     */
    static boolean set_random(ClearCLBuffer arg1, double arg2, double arg3, double arg4) {
        boolean result = SetRandom.setRandom(getCLIJ2(), arg1, new Double (arg2).floatValue(), new Double (arg3).floatValue(), new Double (arg4).floatValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.InvalidateKernelCache
    //----------------------------------------------------

    // net.haesleinhuepf.clij2.plugins.EntropyBox
    //----------------------------------------------------
    /**
     * Determines the local entropy in a box with a given radius around every pixel.
     */
    static boolean entropy_box(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5) {
        boolean result = EntropyBox.entropyBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return result;
    }

    /**
     * Determines the local entropy in a box with a given radius around every pixel.
     */
    static boolean entropy_box(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, double arg6, double arg7) {
        boolean result = EntropyBox.entropyBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue(), new Double (arg6).floatValue(), new Double (arg7).floatValue());
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.PushTile
    //----------------------------------------------------
    /**
     * Push a tile in an image specified by its name, position and size to GPU memory in order to process it there later.
     */
    static ClearCLBuffer push_tile(ImagePlus arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8, double arg9, double arg10) {
        ClearCLBuffer result = PushTile.pushTile(getCLIJ2(), arg1, new Double (arg2).intValue(), new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue(), new Double (arg6).intValue(), new Double (arg7).intValue(), new Double (arg8).intValue(), new Double (arg9).intValue(), new Double (arg10).intValue());
        return result;
    }

    /**
     * Push a tile in an image specified by its name, position and size to GPU memory in order to process it there later.
     */
    static ClearCLBuffer push_tile(ClearCLBuffer arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8, double arg9, double arg10) {
        ClearCLBuffer result = PushTile.pushTile(getCLIJ2(), arg1, new Double (arg2).intValue(), new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue(), new Double (arg6).intValue(), new Double (arg7).intValue(), new Double (arg8).intValue(), new Double (arg9).intValue(), new Double (arg10).intValue());
        return result;
    }

    /**
     * Push a tile in an image specified by its name, position and size to GPU memory in order to process it there later.
     */
    static void push_tile(ImagePlus arg1, String arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8, double arg9, double arg10, double arg11) {
        PushTile.pushTile(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue(), new Double (arg6).intValue(), new Double (arg7).intValue(), new Double (arg8).intValue(), new Double (arg9).intValue(), new Double (arg10).intValue(), new Double (arg11).intValue());
    }


    // net.haesleinhuepf.clijx.plugins.PullTile
    //----------------------------------------------------
    /**
     * Pushes a tile in an image specified by its name, position and size from GPU memory.
     */
    static void pull_tile(ImagePlus arg1, String arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8, double arg9, double arg10, double arg11) {
        PullTile.pullTile(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue(), new Double (arg6).intValue(), new Double (arg7).intValue(), new Double (arg8).intValue(), new Double (arg9).intValue(), new Double (arg10).intValue(), new Double (arg11).intValue());
    }

    /**
     * Pushes a tile in an image specified by its name, position and size from GPU memory.
     */
    static void pull_tile(ImagePlus arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8, double arg9, double arg10, double arg11) {
        PullTile.pullTile(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue(), new Double (arg6).intValue(), new Double (arg7).intValue(), new Double (arg8).intValue(), new Double (arg9).intValue(), new Double (arg10).intValue(), new Double (arg11).intValue());
    }

    /**
     * Pushes a tile in an image specified by its name, position and size from GPU memory.
     */
    static void pull_tile(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8, double arg9, double arg10, double arg11) {
        PullTile.pullTile(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue(), new Double (arg6).intValue(), new Double (arg7).intValue(), new Double (arg8).intValue(), new Double (arg9).intValue(), new Double (arg10).intValue(), new Double (arg11).intValue());
    }


    // net.haesleinhuepf.clij2.plugins.ConcatenateStacks
    //----------------------------------------------------
    /**
     * Concatenates two stacks in Z.
     */
    static boolean concatenate_stacks(ClearCLImageInterface stack1, ClearCLImageInterface stack2, ClearCLImageInterface destination) {
        boolean result = ConcatenateStacks.concatenateStacks(getCLIJ2(), stack1, stack2, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.ResultsTableToImage2D
    //----------------------------------------------------

    // net.haesleinhuepf.clij2.plugins.GetAutomaticThreshold
    //----------------------------------------------------
    /**
     * The automatic thresholder utilizes the threshold methods from ImageJ on a histogram determined on 
     * the GPU to determine a threshold value as similar as possible to ImageJ 'Apply Threshold' method. 
     * 
     * Enter one 
     * of these methods in the method text field:
     * [Default, Huang, Intermodes, IsoData, IJ_IsoData, Li, MaxEntropy, Mean, MinError, Minimum, Moments, Otsu, Percentile, RenyiEntropy, Shanbhag, Triangle, Yen]
     */
    static double get_automatic_threshold(ClearCLBuffer arg1, String arg2) {
        double result = GetAutomaticThreshold.getAutomaticThreshold(getCLIJ2(), arg1, arg2);
        return result;
    }

    /**
     * The automatic thresholder utilizes the threshold methods from ImageJ on a histogram determined on 
     * the GPU to determine a threshold value as similar as possible to ImageJ 'Apply Threshold' method. 
     * 
     * Enter one 
     * of these methods in the method text field:
     * [Default, Huang, Intermodes, IsoData, IJ_IsoData, Li, MaxEntropy, Mean, MinError, Minimum, Moments, Otsu, Percentile, RenyiEntropy, Shanbhag, Triangle, Yen]
     */
    static double get_automatic_threshold(ClearCLBuffer arg1, String arg2, double arg3, double arg4, double arg5) {
        double result = GetAutomaticThreshold.getAutomaticThreshold(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).intValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.GetDimensions
    //----------------------------------------------------
    /**
     * Reads out the size of an image [stack] and writes it to the variables 'width', 'height' and 'depth'.
     */
    static long[] get_dimensions(ClearCLBuffer arg1) {
        long[] result = GetDimensions.getDimensions(getCLIJ2(), arg1);
        return result;
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
    static boolean custom_operation(String arg1, String arg2, HashMap arg3) {
        boolean result = CustomOperation.customOperation(getCLIJ2(), arg1, arg2, arg3);
        return result;
    }


    // net.haesleinhuepf.clijx.weka.autocontext.ApplyAutoContextWekaModel
    //----------------------------------------------------
    /**
     * 
     */
    static boolean apply_auto_context_weka_model_with_options(ClearCLBuffer arg1, ClearCLBuffer arg2, String arg3, String arg4, double arg5) {
        boolean result = ApplyAutoContextWekaModel.applyAutoContextWekaModelWithOptions(getCLIJ2(), arg1, arg2, arg3, arg4, new Double (arg5).intValue());
        return result;
    }


    // net.haesleinhuepf.clijx.weka.autocontext.TrainAutoContextWekaModel
    //----------------------------------------------------
    /**
     * 
     */
    static boolean train_auto_context_weka_model_with_options(ClearCLBuffer arg1, ClearCLBuffer arg2, String arg3, String arg4, double arg5, double arg6, double arg7, double arg8) {
        boolean result = TrainAutoContextWekaModel.trainAutoContextWekaModelWithOptions(getCLIJ2(), arg1, arg2, arg3, arg4, new Double (arg5).intValue(), new Double (arg6).intValue(), new Double (arg7).intValue(), new Double (arg8).intValue());
        return result;
    }


    // net.haesleinhuepf.clijx.weka.ApplyWekaModel
    //----------------------------------------------------
    /**
     * Applies a Weka model using functionality of Fijis Trainable Weka Segmentation plugin. 
     * 
     * It takes a 3D feature stack (e.g. first plane original image, second plane blurred, third plane edge image)and applies a pre-trained a Weka model. Take care that the feature stack has been generated in the sameway as for training the model!
     */
    static boolean apply_weka_model(ClearCLBuffer arg1, ClearCLBuffer arg2, CLIJxWeka2 arg3) {
        boolean result = ApplyWekaModel.applyWekaModel(getCLIJ2(), arg1, arg2, arg3);
        return result;
    }

    /**
     * Applies a Weka model using functionality of Fijis Trainable Weka Segmentation plugin. 
     * 
     * It takes a 3D feature stack (e.g. first plane original image, second plane blurred, third plane edge image)and applies a pre-trained a Weka model. Take care that the feature stack has been generated in the sameway as for training the model!
     */
    static CLIJxWeka2 apply_weka_model(ClearCLBuffer featureStack3D, ClearCLBuffer prediction2D_destination, String loadModelFilename) {
        CLIJxWeka2 result = ApplyWekaModel.applyWekaModel(getCLIJ2(), featureStack3D, prediction2D_destination, loadModelFilename);
        return result;
    }


    // net.haesleinhuepf.clijx.weka.ApplyWekaToTable
    //----------------------------------------------------
    /**
     * Applies a Weka model using functionality of Fijis Trainable Weka Segmentation plugin. 
     * 
     * It takes a Results Table, sorts its columns by name alphabetically and uses it as extracted features (rows correspond to feature vectors) and applies a pre-trained a Weka model. Take care that the table has been generated in the sameway as for training the model!
     */
    static CLIJxWeka2 apply_weka_to_table(ResultsTable arg1, String arg2, String arg3) {
        CLIJxWeka2 result = ApplyWekaToTable.applyWekaToTable(getCLIJ2(), arg1, arg2, arg3);
        return result;
    }

    /**
     * Applies a Weka model using functionality of Fijis Trainable Weka Segmentation plugin. 
     * 
     * It takes a Results Table, sorts its columns by name alphabetically and uses it as extracted features (rows correspond to feature vectors) and applies a pre-trained a Weka model. Take care that the table has been generated in the sameway as for training the model!
     */
    static CLIJxWeka2 apply_weka_to_table(ResultsTable arg1, String arg2, CLIJxWeka2 arg3) {
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
    static boolean generate_feature_stack(ClearCLBuffer input, ClearCLBuffer feature_stack_destination, String feature_definitions) {
        boolean result = GenerateFeatureStack.generateFeatureStack(getCLIJ2(), input, feature_stack_destination, feature_definitions);
        return result;
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
    static ClearCLBuffer generate_feature_stack(ClearCLBuffer arg1, String arg2) {
        ClearCLBuffer result = GenerateFeatureStack.generateFeatureStack(getCLIJ2(), arg1, arg2);
        return result;
    }


    // net.haesleinhuepf.clijx.weka.TrainWekaModel
    //----------------------------------------------------
    /**
     * Trains a Weka model using functionality of Fijis Trainable Weka Segmentation plugin. 
     * 
     * It takes a 3D feature stack (e.g. first plane original image, second plane blurred, third plane edge image)and trains a Weka model. This model will be saved to disc.
     * The given groundTruth image is supposed to be a label map where pixels with value 1 represent class 1, pixels with value 2 represent class 2 and so on. Pixels with value 0 will be ignored for training.
     */
    static CLIJxWeka2 train_weka_model(ClearCLBuffer featureStack3D, ClearCLBuffer groundTruth2D, String saveModelFilename) {
        CLIJxWeka2 result = TrainWekaModel.trainWekaModel(getCLIJ2(), featureStack3D, groundTruth2D, saveModelFilename);
        return result;
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
    static CLIJxWeka2 train_weka_from_table(ResultsTable arg1, String arg2, double arg3, double arg4, double arg5) {
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
    static CLIJxWeka2 train_weka_from_table(ResultsTable arg1, String arg2, String arg3, double arg4, double arg5, double arg6) {
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
    static CLIJxWeka2 train_weka_model_with_options(ClearCLBuffer arg1, ClearCLBuffer arg2, String arg3, double arg4, double arg5, double arg6) {
        CLIJxWeka2 result = TrainWekaModelWithOptions.trainWekaModelWithOptions(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).intValue(), new Double (arg5).intValue(), new Double (arg6).intValue());
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.StartContinuousWebcamAcquisition
    //----------------------------------------------------
    /**
     * Starts acquistion of images from a webcam.
     */
    static boolean start_continuous_webcam_acquisition(double arg1, double arg2, double arg3) {
        boolean result = StartContinuousWebcamAcquisition.startContinuousWebcamAcquisition(getCLIJx(), new Double (arg1).intValue(), new Double (arg2).intValue(), new Double (arg3).intValue());
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.StopContinuousWebcamAcquisition
    //----------------------------------------------------
    /**
     * Stops continous acquistion from a webcam.
     */
    static boolean stop_continuous_webcam_acquisition(double arg1) {
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
    static boolean capture_webcam_image(ClearCLBuffer arg1, double arg2, double arg3, double arg4) {
        boolean result = CaptureWebcamImage.captureWebcamImage(getCLIJx(), arg1, new Double (arg2).intValue(), new Double (arg3).intValue(), new Double (arg4).intValue());
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.ConvertRGBStackToGraySlice
    //----------------------------------------------------
    /**
     * Converts a three channel image (stack with three slices) to a single channel image (2D image) by multiplying with factors 0.299, 0.587, 0.114.
     */
    static boolean convert_r_g_b_stack_to_gray_slice(ClearCLBuffer stack_source, ClearCLBuffer slice_destination) {
        boolean result = ConvertRGBStackToGraySlice.convertRGBStackToGraySlice(getCLIJx(), stack_source, slice_destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.PullLabelsToROIList
    //----------------------------------------------------
    /**
     * Pulls all labels in a label map as ROIs to a list. 
     * 
     * From ImageJ macro this list is written to the log 
     * window. From ImageJ macro conside using pullLabelsToROIManager.
     */
    static boolean pull_labels_to_roilist(ClearCLBuffer arg1, List arg2) {
        boolean result = PullLabelsToROIList.pullLabelsToROIList(getCLIJ2(), arg1, arg2);
        return result;
    }

    /**
     * Pulls all labels in a label map as ROIs to a list. 
     * 
     * From ImageJ macro this list is written to the log 
     * window. From ImageJ macro conside using pullLabelsToROIManager.
     */
    static ArrayList pull_labels_to_roilist(ClearCLBuffer labelmap_input) {
        ArrayList result = PullLabelsToROIList.pullLabelsToROIList(getCLIJ2(), labelmap_input);
        return result;
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
    static boolean mean_of_touching_neighbors(ClearCLBuffer values, ClearCLBuffer touch_matrix, ClearCLBuffer mean_values_destination) {
        boolean result = MeanOfTouchingNeighbors.meanOfTouchingNeighbors(getCLIJ2(), values, touch_matrix, mean_values_destination);
        return result;
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
    static boolean minimum_of_touching_neighbors(ClearCLBuffer values, ClearCLBuffer touch_matrix, ClearCLBuffer minimum_values_destination) {
        boolean result = MinimumOfTouchingNeighbors.minimumOfTouchingNeighbors(getCLIJ2(), values, touch_matrix, minimum_values_destination);
        return result;
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
    static boolean maximum_of_touching_neighbors(ClearCLBuffer values, ClearCLBuffer touch_matrix, ClearCLBuffer maximum_values_destination) {
        boolean result = MaximumOfTouchingNeighbors.maximumOfTouchingNeighbors(getCLIJ2(), values, touch_matrix, maximum_values_destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.ResultsTableColumnToImage
    //----------------------------------------------------
    /**
     * Converts a table column to an image. 
     * 
     * The values are stored in x dimension.
     */
    static boolean results_table_column_to_image(ClearCLBuffer arg1, ResultsTable arg2, String arg3) {
        boolean result = ResultsTableColumnToImage.resultsTableColumnToImage(getCLIJ2(), arg1, arg2, arg3);
        return result;
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
    static double[][] statistics_of_background_and_labelled_pixels(ClearCLBuffer input, ClearCLBuffer labelmap) {
        double[][] result = StatisticsOfBackgroundAndLabelledPixels.statisticsOfBackgroundAndLabelledPixels(getCLIJ2(), input, labelmap);
        return result;
    }

    /**
     * Determines bounding box, area (in pixels/voxels), min, max and mean intensity 
     *  of background and labelled objects in a label map and corresponding pixels in the original image.
     * 
     * Instead of a label map, you can also use a binary image as a binary image is a label map with just one label.
     * 
     * This method is executed on the CPU and not on the GPU/OpenCL device.
     */
    static ResultsTable statistics_of_background_and_labelled_pixels(ClearCLBuffer arg1, ClearCLBuffer arg2, ResultsTable arg3) {
        ResultsTable result = StatisticsOfBackgroundAndLabelledPixels.statisticsOfBackgroundAndLabelledPixels(getCLIJ2(), arg1, arg2, arg3);
        return result;
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
    static double get_sum_of_all_pixels(ClearCLImageInterface arg1) {
        double result = GetSumOfAllPixels.getSumOfAllPixels(getCLIJ2(), arg1);
        return result;
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
    static double get_sorensen_dice_coefficient(ClearCLBuffer arg1, ClearCLBuffer arg2) {
        double result = GetSorensenDiceCoefficient.getSorensenDiceCoefficient(getCLIJ2(), arg1, arg2);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.GetMinimumOfAllPixels
    //----------------------------------------------------
    /**
     * Determines the minimum of all pixels in a given image. 
     * 
     * It will be stored in the variable minimum_of_all_pixels.
     */
    static double get_minimum_of_all_pixels(ClearCLImageInterface arg1) {
        double result = GetMinimumOfAllPixels.getMinimumOfAllPixels(getCLIJ2(), arg1);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.GetMaximumOfAllPixels
    //----------------------------------------------------
    /**
     * Determines the maximum of all pixels in a given image. 
     * 
     * It will be stored in the variable maximum_of_all_pixels.
     */
    static double get_maximum_of_all_pixels(ClearCLImageInterface arg1) {
        double result = GetMaximumOfAllPixels.getMaximumOfAllPixels(getCLIJ2(), arg1);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.GetMeanOfAllPixels
    //----------------------------------------------------
    /**
     * Determines the mean of all pixels in a given image. 
     * 
     * It will be stored in the variable mean_of_all_pixels.
     */
    static double get_mean_of_all_pixels(ClearCLImageInterface arg1) {
        double result = GetMeanOfAllPixels.getMeanOfAllPixels(getCLIJ2(), arg1);
        return result;
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
    static double get_jaccard_index(ClearCLBuffer arg1, ClearCLBuffer arg2) {
        double result = GetJaccardIndex.getJaccardIndex(getCLIJ2(), arg1, arg2);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.GetCenterOfMass
    //----------------------------------------------------
    /**
     * Determines the center of mass of an image or image stack.
     * 
     *  It writes the result in the variables
     *  centerOfMassX, centerOfMassY and centerOfMassZ.
     */
    static double[] get_center_of_mass(ClearCLBuffer arg1) {
        double[] result = GetCenterOfMass.getCenterOfMass(getCLIJ2(), arg1);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.GetBoundingBox
    //----------------------------------------------------
    /**
     * Determines the bounding box of all non-zero pixels in a binary image. 
     * 
     * If called from macro, the positions will be stored in the variables 'boundingBoxX', 'boundingBoxY', 'boundingBoxZ', 'boundingBoxWidth', 'boundingBoxHeight' and 'boundingBoxDepth'.In case of 2D images Z and depth will be zero.
     */
    static double[] get_bounding_box(ClearCLBuffer arg1) {
        double[] result = GetBoundingBox.getBoundingBox(getCLIJ2(), arg1);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.PushArray
    //----------------------------------------------------
    /**
     * Converts an array to an image.
     */
    static boolean push_array(ClearCLBuffer arg1, Object arg2) {
        boolean result = PushArray.pushArray(getCLIJ2(), arg1, arg2);
        return result;
    }

    /**
     * Converts an array to an image.
     */
    static ClearCLBuffer push_array(float[] arg1, double arg2, double arg3, double arg4) {
        ClearCLBuffer result = PushArray.pushArray(getCLIJ2(), arg1, new Double (arg2).intValue(), new Double (arg3).intValue(), new Double (arg4).intValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.PullString
    //----------------------------------------------------
    /**
     * Writes an image into a string.
     */
    static String pull_string(ClearCLImageInterface arg1) {
        String result = PullString.pullString(getCLIJ2(), arg1);
        return result;
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
    static boolean push_string(ClearCLBuffer arg1, String arg2) {
        boolean result = PushString.pushString(getCLIJ2(), arg1, arg2);
        return result;
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
    static ClearCLBuffer push_string(String arg1) {
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
    static boolean median_of_touching_neighbors(ClearCLBuffer values, ClearCLBuffer touch_matrix, ClearCLBuffer mean_values_destination) {
        boolean result = MedianOfTouchingNeighbors.medianOfTouchingNeighbors(getCLIJ2(), values, touch_matrix, mean_values_destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.PushResultsTableColumn
    //----------------------------------------------------
    /**
     * Converts a table column to an image. 
     * 
     * The values are stored in x dimension.
     */
    static boolean push_results_table_column(ClearCLBuffer arg1, ResultsTable arg2, String arg3) {
        boolean result = PushResultsTableColumn.pushResultsTableColumn(getCLIJ2(), arg1, arg2, arg3);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.PushResultsTable
    //----------------------------------------------------
    /**
     * Converts a table to an image. 
     * 
     * Rows stay rows, columns stay columns.
     */
    static boolean push_results_table(ClearCLBuffer arg1, ResultsTable arg2) {
        boolean result = PushResultsTable.pushResultsTable(getCLIJ2(), arg1, arg2);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.PullToResultsTable
    //----------------------------------------------------
    /**
     * Converts an image into a table.
     */
    static ResultsTable pull_to_results_table(ClearCLBuffer arg1, ResultsTable arg2) {
        ResultsTable result = PullToResultsTable.pullToResultsTable(getCLIJ2(), arg1, arg2);
        return result;
    }

    /**
     * Converts an image into a table.
     */
    static ResultsTable pull_to_results_table(ClearCLImage arg1, ResultsTable arg2) {
        ResultsTable result = PullToResultsTable.pullToResultsTable(getCLIJ2(), arg1, arg2);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.LabelVoronoiOctagon
    //----------------------------------------------------
    /**
     * Takes a labelled image and dilates the labels using a octagon shape until they touch. 
     * 
     * The pixels where  the regions touched are afterwards returned as binary image which corresponds to the Voronoi diagram.
     */
    static boolean label_voronoi_octagon(ClearCLBuffer label_map, ClearCLBuffer label_voronoi_destination) {
        boolean result = LabelVoronoiOctagon.labelVoronoiOctagon(getCLIJ2(), label_map, label_voronoi_destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.TouchMatrixToAdjacencyMatrix
    //----------------------------------------------------
    /**
     * Converts a touch matrix in an adjacency matrix
     */
    static boolean touch_matrix_to_adjacency_matrix(ClearCLBuffer touch_matrix, ClearCLBuffer adjacency_matrix) {
        boolean result = TouchMatrixToAdjacencyMatrix.touchMatrixToAdjacencyMatrix(getCLIJ2(), touch_matrix, adjacency_matrix);
        return result;
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
    static boolean adjacency_matrix_to_touch_matrix(ClearCLBuffer adjacency_matrix, ClearCLBuffer touch_matrix) {
        boolean result = AdjacencyMatrixToTouchMatrix.adjacencyMatrixToTouchMatrix(getCLIJ2(), adjacency_matrix, touch_matrix);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.PointlistToLabelledSpots
    //----------------------------------------------------
    /**
     * Takes a pointlist with dimensions n times d with n point coordinates in d dimensions and labels corresponding pixels.
     */
    static boolean pointlist_to_labelled_spots(ClearCLBuffer pointlist, ClearCLBuffer spots_destination) {
        boolean result = PointlistToLabelledSpots.pointlistToLabelledSpots(getCLIJ2(), pointlist, spots_destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.StatisticsOfImage
    //----------------------------------------------------
    /**
     * Determines image size (bounding box), area (in pixels/voxels), min, max and mean intensity 
     *  of all pixels in the original image.
     * 
     * This method is executed on the CPU and not on the GPU/OpenCL device.
     */
    static ResultsTable statistics_of_image(ClearCLBuffer arg1, ResultsTable arg2) {
        ResultsTable result = StatisticsOfImage.statisticsOfImage(getCLIJ2(), arg1, arg2);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.NClosestDistances
    //----------------------------------------------------
    /**
     * Determine the n point indices with shortest distance for all points in a distance matrix. 
     * 
     * This corresponds to the n row indices with minimum values for each column of the distance matrix.Returns the n shortest distances in one image and the point indices in another image.
     */
    static boolean n_closest_distances(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3) {
        boolean result = NClosestDistances.nClosestDistances(getCLIJ2(), arg1, arg2, arg3);
        return result;
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
    static boolean exclude_labels(ClearCLBuffer binary_flaglist, ClearCLBuffer label_map_input, ClearCLBuffer label_map_destination) {
        boolean result = ExcludeLabels.excludeLabels(getCLIJ2(), binary_flaglist, label_map_input, label_map_destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.AverageDistanceOfNFarOffPoints
    //----------------------------------------------------
    /**
     * Determines the average of the n far off (most distant) points for every point in a distance matrix.
     * 
     * This corresponds to the average of the n maximum values (rows) for each column of the distance matrix.
     */
    static boolean average_distance_of_n_far_off_points(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        boolean result = AverageDistanceOfNFarOffPoints.averageDistanceOfNFarOffPoints(getCLIJ2(), arg1, arg2, new Double (arg3).intValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.StandardDeviationOfTouchingNeighbors
    //----------------------------------------------------
    /**
     * Takes a touch matrix and a vector of values to determine the standard deviation value among touching neighbors for every object. 
     * 
     * 
     */
    static boolean standard_deviation_of_touching_neighbors(ClearCLBuffer values, ClearCLBuffer touch_matrix, ClearCLBuffer standard_deviation_values_destination) {
        boolean result = StandardDeviationOfTouchingNeighbors.standardDeviationOfTouchingNeighbors(getCLIJ2(), values, touch_matrix, standard_deviation_values_destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.NeighborsOfNeighbors
    //----------------------------------------------------
    /**
     * Determines neighbors of neigbors from touch matrix and saves the result as a new touch matrix.
     */
    static boolean neighbors_of_neighbors(ClearCLBuffer touch_matrix, ClearCLBuffer neighbor_matrix_destination) {
        boolean result = NeighborsOfNeighbors.neighborsOfNeighbors(getCLIJ2(), touch_matrix, neighbor_matrix_destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.GenerateParametricImage
    //----------------------------------------------------
    /**
     * Take a labelmap and a vector of values to replace label 1 with the 1st value in the vector. 
     * 
     * Note that indexing in the vector starts at zero. The 0th entry corresponds to background in the label map.Internally this method just calls ReplaceIntensities.
     * 
     */
    static boolean generate_parametric_image(ClearCLImageInterface label_map, ClearCLImageInterface parameter_value_vector, ClearCLImageInterface parametric_image_destination) {
        boolean result = GenerateParametricImage.generateParametricImage(getCLIJ2(), label_map, parameter_value_vector, parametric_image_destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.GenerateParametricImageFromResultsTableColumn
    //----------------------------------------------------
    /**
     * Take a labelmap and a column from the results table to replace label 1 with the 1st value in the vector. 
     * 
     * Note that indexing in the table column starts at zero. The results table should contain a line at the beginningrepresenting the background.
     * 
     */
    static boolean generate_parametric_image_from_results_table_column(ClearCLImageInterface arg1, ClearCLImageInterface arg2, ResultsTable arg3, String arg4) {
        boolean result = GenerateParametricImageFromResultsTableColumn.generateParametricImageFromResultsTableColumn(getCLIJ2(), arg1, arg2, arg3, arg4);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.ExcludeLabelsWithValuesOutOfRange
    //----------------------------------------------------
    /**
     * This operation removes labels from a labelmap and renumbers the remaining labels. 
     * 
     * Hand over a vector of values and a range specifying which labels with which values are eliminated.
     */
    static boolean exclude_labels_with_values_out_of_range(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4, double arg5) {
        boolean result = ExcludeLabelsWithValuesOutOfRange.excludeLabelsWithValuesOutOfRange(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).floatValue(), new Double (arg5).floatValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.ExcludeLabelsWithValuesWithinRange
    //----------------------------------------------------
    /**
     * This operation removes labels from a labelmap and renumbers the remaining labels. 
     * 
     * Hand over a vector of values and a range specifying which labels with which values are eliminated.
     */
    static boolean exclude_labels_with_values_within_range(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4, double arg5) {
        boolean result = ExcludeLabelsWithValuesWithinRange.excludeLabelsWithValuesWithinRange(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).floatValue(), new Double (arg5).floatValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.CombineVertically
    //----------------------------------------------------
    /**
     * Combines two images or stacks in Y.
     */
    static boolean combine_vertically(ClearCLImageInterface stack1, ClearCLImageInterface stack2, ClearCLImageInterface destination) {
        boolean result = CombineVertically.combineVertically(getCLIJ2(), stack1, stack2, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.CombineHorizontally
    //----------------------------------------------------
    /**
     * Combines two images or stacks in X.
     */
    static boolean combine_horizontally(ClearCLImageInterface stack1, ClearCLImageInterface stack2, ClearCLImageInterface destination) {
        boolean result = CombineHorizontally.combineHorizontally(getCLIJ2(), stack1, stack2, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.ReduceStack
    //----------------------------------------------------
    /**
     * Reduces the number of slices in a stack by a given factor.
     * With the offset you have control which slices stay: 
     * * With factor 3 and offset 0, slices 0, 3, 6,... are kept. * With factor 4 and offset 1, slices 1, 5, 9,... are kept.
     */
    static boolean reduce_stack(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4) {
        boolean result = ReduceStack.reduceStack(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.DetectMinima2DBox
    //----------------------------------------------------
    /**
     * Detects local minima in a given square/cubic neighborhood. 
     * 
     * Pixels in the resulting image are set to 1 if there is no other pixel in a given radius which has a 
     * lower intensity, and to 0 otherwise.
     */
    static boolean detect_minima2d_box(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4) {
        boolean result = DetectMinima2DBox.detectMinima2DBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.DetectMaxima2DBox
    //----------------------------------------------------
    /**
     * Detects local maxima in a given square/cubic neighborhood. 
     * 
     * Pixels in the resulting image are set to 1 if there is no other pixel in a given radius which has a 
     * higher intensity, and to 0 otherwise.
     */
    static boolean detect_maxima2d_box(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4) {
        boolean result = DetectMaxima2DBox.detectMaxima2DBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.DetectMinima3DBox
    //----------------------------------------------------
    /**
     * Detects local minima in a given square/cubic neighborhood. 
     * 
     * Pixels in the resulting image are set to 1 if there is no other pixel in a given radius which has a 
     * lower intensity, and to 0 otherwise.
     */
    static boolean detect_minima3d_box(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5) {
        boolean result = DetectMinima3DBox.detectMinima3DBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.DetectMaxima3DBox
    //----------------------------------------------------
    /**
     * Detects local maxima in a given square/cubic neighborhood. 
     * 
     * Pixels in the resulting image are set to 1 if there is no other pixel in a given radius which has a 
     * higher intensity, and to 0 otherwise.
     */
    static boolean detect_maxima3d_box(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5) {
        boolean result = DetectMaxima3DBox.detectMaxima3DBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.DepthColorProjection
    //----------------------------------------------------
    /**
     * Determines a maximum projection of an image stack and does a color coding of the determined arg Z (position of the found maximum). 
     * 
     * Second parameter is a Lookup-Table in the form of an 8-bit image stack 255 pixels wide, 1 pixel high with 3 planes representing red, green and blue intensities.
     * Resulting image is a 3D image with three Z-planes representing red, green and blue channels.
     */
    static boolean depth_color_projection(ClearCLImageInterface arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4, double arg5) {
        boolean result = DepthColorProjection.depthColorProjection(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).floatValue(), new Double (arg5).floatValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.GenerateBinaryOverlapMatrix
    //----------------------------------------------------
    /**
     * Takes two labelmaps with n and m labels and generates a (n+1)*(m+1) matrix where all pixels are set to 0 exept those where labels overlap between the label maps. 
     * 
     * For example, if labels 3 in labelmap1 and 4 in labelmap2 are touching then the pixel (3,4) in the matrix will be set to 1.
     */
    static boolean generate_binary_overlap_matrix(ClearCLBuffer label_map1, ClearCLBuffer label_map2, ClearCLBuffer touch_matrix_destination) {
        boolean result = GenerateBinaryOverlapMatrix.generateBinaryOverlapMatrix(getCLIJ2(), label_map1, label_map2, touch_matrix_destination);
        return result;
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
    static boolean convolve(ClearCLBuffer source, ClearCLBuffer convolution_kernel, ClearCLBuffer destination) {
        boolean result = Convolve.convolve(getCLIJ2(), source, convolution_kernel, destination);
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.NonLocalMeans
    //----------------------------------------------------
    /**
     * Applies a non-local means filter using a box neighborhood with a Gaussian weight specified with sigma to the input image.
     */
    static boolean non_local_means(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, double arg6) {
        boolean result = NonLocalMeans.nonLocalMeans(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue(), new Double (arg6).floatValue());
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.Bilateral
    //----------------------------------------------------

    // net.haesleinhuepf.clij2.plugins.UndefinedToZero
    //----------------------------------------------------
    /**
     * Copies all pixels instead those which are not a number (NaN) or infinity (inf), which are replaced by 0.
     */
    static boolean undefined_to_zero(ClearCLBuffer source, ClearCLBuffer destination) {
        boolean result = UndefinedToZero.undefinedToZero(getCLIJ2(), source, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.GenerateJaccardIndexMatrix
    //----------------------------------------------------
    /**
     * Takes two labelmaps with n and m labels_2 and generates a (n+1)*(m+1) matrix where all labels_1 are set to 0 exept those where labels_2 overlap between the label maps. 
     * 
     * For the remaining labels_1, the value will be between 0 and 1 indicating the overlap as measured by the Jaccard Index.
     * Major parts of this operation run on the CPU.
     */
    static boolean generate_jaccard_index_matrix(ClearCLBuffer label_map1, ClearCLBuffer label_map2, ClearCLBuffer jaccard_index_matrix_destination) {
        boolean result = GenerateJaccardIndexMatrix.generateJaccardIndexMatrix(getCLIJ2(), label_map1, label_map2, jaccard_index_matrix_destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.GenerateTouchCountMatrix
    //----------------------------------------------------
    /**
     * Takes a label map with n labels and generates a (n+1)*(n+1) matrix where all pixels are set the number of pixels where labels touch (diamond neighborhood). 
     * 
     * Major parts of this operation run on the CPU.
     */
    static boolean generate_touch_count_matrix(ClearCLBuffer label_map, ClearCLBuffer touch_count_matrix_destination) {
        boolean result = GenerateTouchCountMatrix.generateTouchCountMatrix(getCLIJ2(), label_map, touch_count_matrix_destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.MinimumXProjection
    //----------------------------------------------------
    /**
     * Determines the minimum intensity projection of an image along Y.
     */
    static boolean minimum_x_projection(ClearCLImageInterface source, ClearCLImageInterface destination_min) {
        boolean result = MinimumXProjection.minimumXProjection(getCLIJ2(), source, destination_min);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.MinimumYProjection
    //----------------------------------------------------
    /**
     * Determines the minimum intensity projection of an image along Y.
     */
    static boolean minimum_y_projection(ClearCLImageInterface source, ClearCLImageInterface destination_min) {
        boolean result = MinimumYProjection.minimumYProjection(getCLIJ2(), source, destination_min);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.MeanXProjection
    //----------------------------------------------------
    /**
     * Determines the mean average intensity projection of an image along X.
     */
    static boolean mean_x_projection(ClearCLImageInterface source, ClearCLImageInterface destination) {
        boolean result = MeanXProjection.meanXProjection(getCLIJ2(), source, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.MeanYProjection
    //----------------------------------------------------
    /**
     * Determines the mean average intensity projection of an image along Y.
     */
    static boolean mean_y_projection(ClearCLImageInterface source, ClearCLImageInterface destination) {
        boolean result = MeanYProjection.meanYProjection(getCLIJ2(), source, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.SquaredDifference
    //----------------------------------------------------
    /**
     * Determines the squared difference pixel by pixel between two images.
     */
    static boolean squared_difference(ClearCLBuffer source1, ClearCLBuffer source2, ClearCLBuffer destination) {
        boolean result = SquaredDifference.squaredDifference(getCLIJ2(), source1, source2, destination);
        return result;
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
    static boolean absolute_difference(ClearCLBuffer source1, ClearCLBuffer source2, ClearCLBuffer destination) {
        boolean result = AbsoluteDifference.absoluteDifference(getCLIJ2(), source1, source2, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.ReplacePixelsIfZero
    //----------------------------------------------------
    /**
     * Replaces pixel values x with y in case x is zero.
     * 
     * This functionality is comparable to ImageJs image calculator operator 'transparent zero'.
     */
    static boolean replace_pixels_if_zero(ClearCLImageInterface input1, ClearCLImageInterface input2, ClearCLImageInterface destination) {
        boolean result = ReplacePixelsIfZero.replacePixelsIfZero(getCLIJ2(), input1, input2, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.VoronoiLabeling
    //----------------------------------------------------
    /**
     * Takes a binary image, labels connected components and dilates the regions using a octagon shape until they touch. 
     * 
     * The resulting label map is written to the output.
     */
    static boolean voronoi_labeling(ClearCLBuffer input, ClearCLImageInterface destination) {
        boolean result = VoronoiLabeling.voronoiLabeling(getCLIJ2(), input, destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.ExtendLabelingViaVoronoi
    //----------------------------------------------------
    /**
     * Takes a label map image and dilates the regions using a octagon shape until they touch. 
     * 
     * The resulting label map is written to the output.
     */
    static boolean extend_labeling_via_voronoi(ClearCLBuffer input, ClearCLImageInterface destination) {
        boolean result = ExtendLabelingViaVoronoi.extendLabelingViaVoronoi(getCLIJ2(), input, destination);
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.FindMaxima
    //----------------------------------------------------
    /**
     * Finds and labels local maxima with neighboring maxima and background above a given tolerance threshold.
     * 
     * 
     */
    static boolean find_maxima(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        boolean result = FindMaxima.findMaxima(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return result;
    }

    /**
     * 
     */
    static boolean merge_touching_labels_special(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, ClearCLBuffer arg4) {
        boolean result = FindMaxima.mergeTouchingLabelsSpecial(getCLIJ2(), arg1, arg2, arg3, arg4);
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.MergeTouchingLabels
    //----------------------------------------------------
    /**
     * 
     */
    static boolean merge_touching_labels(ClearCLBuffer source, ClearCLBuffer destination) {
        boolean result = MergeTouchingLabels.mergeTouchingLabels(getCLIJ2(), source, destination);
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.AverageNeighborDistanceMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines which labels touch and replaces every label with the average distance to their neighboring labels.
     * 
     * To determine the distances, the centroid of the labels is determined internally.
     */
    static boolean average_neighbor_distance_map(ClearCLBuffer input, ClearCLBuffer destination) {
        boolean result = AverageNeighborDistanceMap.averageNeighborDistanceMap(getCLIJ2(), input, destination);
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.CylinderTransform
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
    static boolean cylinder_transform(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, double arg6) {
        boolean result = CylinderTransform.cylinderTransform(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), new Double (arg6).floatValue());
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.DetectAndLabelMaxima
    //----------------------------------------------------
    /**
     * Determines maximum regions in a Gaussian blurred version of the original image.
     * 
     * The regions do not not necessarily have to be single pixels. 
     * It is also possible to invert the image before determining the maxima.
     */
    static boolean detect_and_label_maxima(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, boolean arg6) {
        boolean result = DetectAndLabelMaxima.detectAndLabelMaxima(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), arg6);
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.DrawDistanceMeshBetweenTouchingLabels
    //----------------------------------------------------
    /**
     * Starting from a label map, draw lines between touching neighbors resulting in a mesh.
     * 
     * The end points of the lines correspond to the centroids of the labels. The intensity of the lines 
     * corresponds to the distance between these labels (in pixels or voxels).
     */
    static boolean draw_distance_mesh_between_touching_labels(ClearCLBuffer input, ClearCLBuffer destination) {
        boolean result = DrawDistanceMeshBetweenTouchingLabels.drawDistanceMeshBetweenTouchingLabels(getCLIJ2(), input, destination);
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.DrawMeshBetweenTouchingLabels
    //----------------------------------------------------
    /**
     * Starting from a label map, draw lines between touching neighbors resulting in a mesh.
     * 
     * The end points of the lines correspond to the centroids of the labels. 
     */
    static boolean draw_mesh_between_touching_labels(ClearCLBuffer input, ClearCLBuffer destination) {
        boolean result = DrawMeshBetweenTouchingLabels.drawMeshBetweenTouchingLabels(getCLIJ2(), input, destination);
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.ExcludeLabelsOutsideSizeRange
    //----------------------------------------------------
    /**
     * Removes labels from a label map which are not within a certain size range.
     * 
     * Size of the labels is given as the number of pixel or voxels per label.
     */
    static boolean exclude_labels_outside_size_range(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4) {
        boolean result = ExcludeLabelsOutsideSizeRange.excludeLabelsOutsideSizeRange(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue());
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.ExtendLabelsWithMaximumRadius
    //----------------------------------------------------
    /**
     * Extend labels with a given radius.
     * 
     * This is actually a local maximum filter applied to a label map which does not overwrite labels.
     */
    static boolean extend_labels_with_maximum_radius(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        boolean result = ExtendLabelsWithMaximumRadius.extendLabelsWithMaximumRadius(getCLIJ2(), arg1, arg2, new Double (arg3).intValue());
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.FindAndLabelMaxima
    //----------------------------------------------------
    /**
     * Determine maxima with a given tolerance to surrounding maxima and background and label them.
     */
    static boolean find_and_label_maxima(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, boolean arg4) {
        boolean result = FindAndLabelMaxima.findAndLabelMaxima(getCLIJx(), arg1, arg2, new Double (arg3).floatValue(), arg4);
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.MakeIsotropic
    //----------------------------------------------------
    /**
     * Applies a scaling operation using linear interpolation to generate an image stack with a given isotropic voxel size.
     */
    static boolean make_isotropic(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, double arg6) {
        boolean result = MakeIsotropic.makeIsotropic(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), new Double (arg6).floatValue());
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.TouchingNeighborCountMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines which labels touch and replaces every label with the number of touching neighboring labels.
     * 
     * 
     */
    static boolean touching_neighbor_count_map(ClearCLBuffer input, ClearCLBuffer destination) {
        boolean result = TouchingNeighborCountMap.touchingNeighborCountMap(getCLIJ2(), input, destination);
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.RigidTransform
    //----------------------------------------------------
    /**
     * Applies a rigid transform using linear interpolation to an image stack.
     */
    static boolean rigid_transform(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8) {
        boolean result = RigidTransform.rigidTransform(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), new Double (arg6).floatValue(), new Double (arg7).floatValue(), new Double (arg8).floatValue());
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.SphereTransform
    //----------------------------------------------------
    /**
     * Turns an image stack in XYZ cartesian coordinate system to an AID polar coordinate system.
     * 
     * A corresponds to azimut,I to inclination and D to the distance from the center.Thus, going in virtual Z direction (actually D) in the resulting stack, you go from the center to theperiphery.
     */
    static boolean sphere_transform(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, double arg6, double arg7) {
        boolean result = SphereTransform.sphereTransform(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), new Double (arg6).floatValue(), new Double (arg7).floatValue());
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.SubtractGaussianBackground
    //----------------------------------------------------
    /**
     * Applies Gaussian blur to the input image and subtracts the result from the original image.
     * 
     * Deprecated: Use differenceOfGaussian() instead.
     */
    static boolean subtract_gaussian_background(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        boolean result = SubtractGaussianBackground.subtractGaussianBackground(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue());
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.ThresholdDoG
    //----------------------------------------------------
    /**
     * Applies a Difference-of-Gaussian filter to an image and thresholds it with given sigma and threshold values.
     */
    static boolean threshold_dog(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, boolean arg6) {
        boolean result = ThresholdDoG.thresholdDoG(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), arg6);
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.DriftCorrectionByCenterOfMassFixation
    //----------------------------------------------------
    /**
     * Determines the centerOfMass of the image stack and translates it so that it stays in a defined position.
     */
    static boolean drift_correction_by_center_of_mass_fixation(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5) {
        boolean result = DriftCorrectionByCenterOfMassFixation.driftCorrectionByCenterOfMassFixation(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue());
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.DriftCorrectionByCentroidFixation
    //----------------------------------------------------
    /**
     * Threshold the image stack, determines the centroid of the resulting binary image and 
     * translates the image stack so that its centroid sits in a defined position.
     */
    static boolean drift_correction_by_centroid_fixation(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5, double arg6) {
        boolean result = DriftCorrectionByCentroidFixation.driftCorrectionByCentroidFixation(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue(), new Double (arg6).floatValue());
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.IntensityCorrection
    //----------------------------------------------------
    /**
     * Determines the mean intensity of the image stack and multiplies it with a factor so that the mean intensity becomes equal to a given value.
     */
    static boolean intensity_correction(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        boolean result = IntensityCorrection.intensityCorrection(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.IntensityCorrectionAboveThresholdOtsu
    //----------------------------------------------------
    /**
     * Determines the mean intensity of all pixel the image stack which are above a determined Threshold (Otsu et al. 1979) and multiplies it with a factor so that the mean intensity becomes equal to a given value.
     */
    static boolean intensity_correction_above_threshold_otsu(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        boolean result = IntensityCorrectionAboveThresholdOtsu.intensityCorrectionAboveThresholdOtsu(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LabelMeanIntensityMap
    //----------------------------------------------------
    /**
     * Takes an image and a corresponding label map, determines the mean intensity per label and replaces every label with the that number.
     * 
     * This results in a parametric image expressing mean object intensity.
     */
    static boolean label_mean_intensity_map(ClearCLBuffer input, ClearCLBuffer label_map, ClearCLBuffer destination) {
        boolean result = LabelMeanIntensityMap.labelMeanIntensityMap(getCLIJ2(), input, label_map, destination);
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LabelStandardDeviationIntensityMap
    //----------------------------------------------------
    /**
     * Takes an image and a corresponding label map, determines the standard deviation of the intensity per label and replaces every label with the that number.
     * 
     * This results in a parametric image expressing standard deviation of object intensity.
     */
    static boolean label_standard_deviation_intensity_map(ClearCLBuffer input, ClearCLBuffer label_map, ClearCLBuffer destination) {
        boolean result = LabelStandardDeviationIntensityMap.labelStandardDeviationIntensityMap(getCLIJ2(), input, label_map, destination);
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LabelPixelCountMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines the number of pixels per label and replaces every label with the that number.
     * 
     * This results in a parametric image expressing area or volume.
     */
    static boolean label_pixel_count_map(ClearCLBuffer input, ClearCLBuffer destination) {
        boolean result = LabelPixelCountMap.labelPixelCountMap(getCLIJ2(), input, destination);
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.ParametricWatershed
    //----------------------------------------------------
    /**
     * Apply a binary watershed to a binary image and introduce black pixels between objects.
     * 
     * To have control about where objects are cut, the sigma parameters allow to control a Gaussian blur filter applied to the internally used distance map.
     */
    static boolean parametric_watershed(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5) {
        boolean result = ParametricWatershed.parametricWatershed(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue());
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.MeanZProjectionAboveThreshold
    //----------------------------------------------------
    /**
     * Determines the mean average intensity projection of an image along Z but only for pixels above a given threshold.
     */
    static boolean mean_z_projection_above_threshold(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3) {
        boolean result = MeanZProjectionAboveThreshold.meanZProjectionAboveThreshold(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.CentroidsOfBackgroundAndLabels
    //----------------------------------------------------
    /**
     * Determines the centroids of the background and all labels in a label image or image stack. 
     * 
     * It writes the resulting  coordinates in a pointlist image. Depending on the dimensionality d of the labelmap and the number  of labels n, the pointlist image will have n*d pixels.
     */
    static boolean centroids_of_background_and_labels(ClearCLBuffer source, ClearCLBuffer pointlist_destination) {
        boolean result = CentroidsOfBackgroundAndLabels.centroidsOfBackgroundAndLabels(getCLIJ2(), source, pointlist_destination);
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.SeededWatershed
    //----------------------------------------------------
    /**
     * Takes a label map (seeds) and an input image with gray values to apply the watershed algorithm and split the image above a given threshold in labels.
     */
    static boolean seeded_watershed(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3, double arg4) {
        boolean result = SeededWatershed.seededWatershed(getCLIJ2(), arg1, arg2, arg3, new Double (arg4).floatValue());
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.PushMetaData
    //----------------------------------------------------

    // net.haesleinhuepf.clijx.plugins.PopMetaData
    //----------------------------------------------------

    // net.haesleinhuepf.clijx.plugins.ResetMetaData
    //----------------------------------------------------

    // net.haesleinhuepf.clijx.plugins.AverageDistanceOfNClosestNeighborsMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines distances between all centroids and replaces every label with the average distance to the n closest neighboring labels.
     */
    static boolean average_distance_of_n_closest_neighbors_map(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        boolean result = AverageDistanceOfNClosestNeighborsMap.averageDistanceOfNClosestNeighborsMap(getCLIJ2(), arg1, arg2, new Double (arg3).intValue());
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.DrawTouchCountMeshBetweenTouchingLabels
    //----------------------------------------------------
    /**
     * Starting from a label map, draw lines between touching neighbors resulting in a mesh.
     * 
     * The end points of the lines correspond to the centroids of the labels. The intensity of the lines 
     * corresponds to the touch count between these labels.
     */
    static boolean draw_touch_count_mesh_between_touching_labels(ClearCLBuffer input, ClearCLBuffer destination) {
        boolean result = DrawTouchCountMeshBetweenTouchingLabels.drawTouchCountMeshBetweenTouchingLabels(getCLIJ2(), input, destination);
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LocalMaximumAverageDistanceOfNClosestNeighborsMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines distances between all centroids, the mean distance of the n closest points for every point
     *  and replaces every label with the maximum distance of touching labels.
     */
    static boolean local_maximum_average_distance_of_n_closest_neighbors_map(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        boolean result = LocalMaximumAverageDistanceOfNClosestNeighborsMap.localMaximumAverageDistanceOfNClosestNeighborsMap(getCLIJ2(), arg1, arg2, new Double (arg3).intValue());
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LocalMaximumAverageNeighborDistanceMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines which labels touch, the distance between their centroids and the maximum distancebetween touching neighbors. It then replaces every label with the that value.
     */
    static boolean local_maximum_average_neighbor_distance_map(ClearCLBuffer input, ClearCLBuffer destination) {
        boolean result = LocalMaximumAverageNeighborDistanceMap.localMaximumAverageNeighborDistanceMap(getCLIJ2(), input, destination);
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LocalMaximumTouchingNeighborCountMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines which labels touch, determines for every label with the number of touching 
     * neighboring labels and replaces the label index with the local maximum of this count.
     * 
     * 
     */
    static boolean local_maximum_touching_neighbor_count_map(ClearCLBuffer input, ClearCLBuffer destination) {
        boolean result = LocalMaximumTouchingNeighborCountMap.localMaximumTouchingNeighborCountMap(getCLIJ2(), input, destination);
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LocalMeanAverageDistanceOfNClosestNeighborsMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines distances between all centroids, the mean distance of the n closest points for every point
     *  and replaces every label with the mean distance of touching labels.
     */
    static boolean local_mean_average_distance_of_n_closest_neighbors_map(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        boolean result = LocalMeanAverageDistanceOfNClosestNeighborsMap.localMeanAverageDistanceOfNClosestNeighborsMap(getCLIJ2(), arg1, arg2, new Double (arg3).intValue());
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LocalMeanAverageNeighborDistanceMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines which labels touch, the distance between their centroids and the mean distancebetween touching neighbors. It then replaces every label with the that value.
     */
    static boolean local_mean_average_neighbor_distance_map(ClearCLBuffer input, ClearCLBuffer destination) {
        boolean result = LocalMeanAverageNeighborDistanceMap.localMeanAverageNeighborDistanceMap(getCLIJ2(), input, destination);
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LocalMeanTouchingNeighborCountMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines which labels touch, determines for every label with the number of touching 
     * neighboring labels and replaces the label index with the local mean of this count.
     * 
     * 
     */
    static boolean local_mean_touching_neighbor_count_map(ClearCLBuffer input, ClearCLBuffer destination) {
        boolean result = LocalMeanTouchingNeighborCountMap.localMeanTouchingNeighborCountMap(getCLIJ2(), input, destination);
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LocalMeanTouchPortionMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines which labels touch and how much, relatively taking the whole outline of 
     * each label into account, and determines for every label with the mean of this value and replaces the 
     * label index with that value.
     * 
     * 
     */
    static boolean local_mean_touch_portion_map(ClearCLBuffer input, ClearCLBuffer destination) {
        boolean result = LocalMeanTouchPortionMap.localMeanTouchPortionMap(getCLIJ2(), input, destination);
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LocalMedianAverageDistanceOfNClosestNeighborsMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines distances between all centroids, the mean distance of the n closest points for every point
     *  and replaces every label with the median distance of touching labels.
     */
    static boolean local_median_average_distance_of_n_closest_neighbors_map(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        boolean result = LocalMedianAverageDistanceOfNClosestNeighborsMap.localMedianAverageDistanceOfNClosestNeighborsMap(getCLIJ2(), arg1, arg2, new Double (arg3).intValue());
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LocalMedianAverageNeighborDistanceMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines which labels touch, the distance between their centroids and the median distancebetween touching neighbors. It then replaces every label with the that value.
     */
    static boolean local_median_average_neighbor_distance_map(ClearCLBuffer input, ClearCLBuffer destination) {
        boolean result = LocalMedianAverageNeighborDistanceMap.localMedianAverageNeighborDistanceMap(getCLIJ2(), input, destination);
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LocalMedianTouchingNeighborCountMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines which labels touch, determines for every label with the number of touching 
     * neighboring labels and replaces the label index with the local median of this count.
     * 
     * 
     */
    static boolean local_median_touching_neighbor_count_map(ClearCLBuffer input, ClearCLBuffer destination) {
        boolean result = LocalMedianTouchingNeighborCountMap.localMedianTouchingNeighborCountMap(getCLIJ2(), input, destination);
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LocalMinimumAverageDistanceOfNClosestNeighborsMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines distances between all centroids, the mean distance of the n closest points for every point
     *  and replaces every label with the minimum distance of touching labels.
     */
    static boolean local_minimum_average_distance_of_n_closest_neighbors_map(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        boolean result = LocalMinimumAverageDistanceOfNClosestNeighborsMap.localMinimumAverageDistanceOfNClosestNeighborsMap(getCLIJ2(), arg1, arg2, new Double (arg3).intValue());
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LocalMinimumAverageNeighborDistanceMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines which labels touch, the distance between their centroids and the minimum distancebetween touching neighbors. It then replaces every label with the that value.
     */
    static boolean local_minimum_average_neighbor_distance_map(ClearCLBuffer input, ClearCLBuffer destination) {
        boolean result = LocalMinimumAverageNeighborDistanceMap.localMinimumAverageNeighborDistanceMap(getCLIJ2(), input, destination);
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LocalMinimumTouchingNeighborCountMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines which labels touch, determines for every label with the number of touching 
     * neighboring labels and replaces the label index with the local minimum of this count.
     * 
     * 
     */
    static boolean local_minimum_touching_neighbor_count_map(ClearCLBuffer input, ClearCLBuffer destination) {
        boolean result = LocalMinimumTouchingNeighborCountMap.localMinimumTouchingNeighborCountMap(getCLIJ2(), input, destination);
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LocalStandardDeviationAverageDistanceOfNClosestNeighborsMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines distances between all centroids, the mean distance of the n closest points for every point
     *  and replaces every label with the standard deviation distance of touching labels.
     */
    static boolean local_standard_deviation_average_distance_of_n_closest_neighbors_map(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        boolean result = LocalStandardDeviationAverageDistanceOfNClosestNeighborsMap.localStandardDeviationAverageDistanceOfNClosestNeighborsMap(getCLIJ2(), arg1, arg2, new Double (arg3).intValue());
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LocalStandardDeviationAverageNeighborDistanceMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines which labels touch, the distance between their centroids and the standard deviation distancebetween touching neighbors. It then replaces every label with the that value.
     */
    static boolean local_standard_deviation_average_neighbor_distance_map(ClearCLBuffer input, ClearCLBuffer destination) {
        boolean result = LocalStandardDeviationAverageNeighborDistanceMap.localStandardDeviationAverageNeighborDistanceMap(getCLIJ2(), input, destination);
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LocalStandardDeviationTouchingNeighborCountMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines which labels touch, determines for every label with the number of touching 
     * neighboring labels and replaces the label index with the local standard deviation of this count.
     * 
     * 
     */
    static boolean local_standard_deviation_touching_neighbor_count_map(ClearCLBuffer input, ClearCLBuffer destination) {
        boolean result = LocalStandardDeviationTouchingNeighborCountMap.localStandardDeviationTouchingNeighborCountMap(getCLIJ2(), input, destination);
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LabelMinimumIntensityMap
    //----------------------------------------------------
    /**
     * 
     */
    static boolean label_minimum_intensity_map(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3) {
        boolean result = LabelMinimumIntensityMap.labelMinimumIntensityMap(getCLIJ2(), arg1, arg2, arg3);
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LabelMaximumIntensityMap
    //----------------------------------------------------
    /**
     * 
     */
    static boolean label_maximum_intensity_map(ClearCLBuffer arg1, ClearCLBuffer arg2, ClearCLBuffer arg3) {
        boolean result = LabelMaximumIntensityMap.labelMaximumIntensityMap(getCLIJ2(), arg1, arg2, arg3);
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LabelMaximumExtensionRatioMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines for every label the maximum distance of any pixel to the centroid and replaces every label with the that number.
     * 
     * 
     */
    static boolean label_maximum_extension_ratio_map(ClearCLBuffer input, ClearCLBuffer destination) {
        boolean result = LabelMaximumExtensionRatioMap.labelMaximumExtensionRatioMap(getCLIJ2(), input, destination);
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LabelMaximumExtensionMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines for every label the maximum distance of any pixel to the centroid and replaces every label with the that number.
     * 
     * 
     */
    static boolean label_maximum_extension_map(ClearCLBuffer input, ClearCLBuffer destination) {
        boolean result = LabelMaximumExtensionMap.labelMaximumExtensionMap(getCLIJ2(), input, destination);
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.GenerateIntegerGreyValueCooccurrenceCountMatrixHalfBox
    //----------------------------------------------------
    /**
     * Takes an image and assumes its grey values are integers. It builds up a grey-level co-occurrence matrix of neighboring (west, south-west, south, south-east, in 3D 9 pixels on the next plane) pixel intensities. 
     * 
     * Major parts of this operation run on the CPU.
     */
    static boolean generate_integer_grey_value_cooccurrence_count_matrix_half_box(ClearCLBuffer integer_image, ClearCLBuffer grey_value_cooccurrence_matrix_destination) {
        boolean result = GenerateIntegerGreyValueCooccurrenceCountMatrixHalfBox.generateIntegerGreyValueCooccurrenceCountMatrixHalfBox(getCLIJ2(), integer_image, grey_value_cooccurrence_matrix_destination);
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.GenerateIntegerGreyValueCooccurrenceCountMatrixHalfDiamond
    //----------------------------------------------------
    /**
     * Takes an image and assumes its grey values are integers. It builds up a grey-level co-occurrence matrix of neighboring (left, bottom, back) pixel intensities. 
     * 
     * Major parts of this operation run on the CPU.
     */
    static boolean generate_integer_grey_value_cooccurrence_count_matrix_half_diamond(ClearCLBuffer integer_image, ClearCLBuffer grey_value_cooccurrence_matrix_destination) {
        boolean result = GenerateIntegerGreyValueCooccurrenceCountMatrixHalfDiamond.generateIntegerGreyValueCooccurrenceCountMatrixHalfDiamond(getCLIJ2(), integer_image, grey_value_cooccurrence_matrix_destination);
        return result;
    }


    // net.haesleinhuepf.clij2.plugins.GetMeanOfMaskedPixels
    //----------------------------------------------------
    /**
     * Determines the mean of all pixels in a given image which have non-zero value in a corresponding mask image. 
     * 
     * It will be stored in the variable mean_of_masked_pixels.
     */
    static double get_mean_of_masked_pixels(ClearCLBuffer arg1, ClearCLBuffer arg2) {
        double result = GetMeanOfMaskedPixels.getMeanOfMaskedPixels(getCLIJ2(), arg1, arg2);
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.DivideByGaussianBackground
    //----------------------------------------------------
    /**
     * Applies Gaussian blur to the input image and divides the original by the result.
     */
    static boolean divide_by_gaussian_background(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        boolean result = DivideByGaussianBackground.divideByGaussianBackground(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue());
        return result;
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
    static boolean generate_grey_value_cooccurrence_matrix_box(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4) {
        boolean result = GenerateGreyValueCooccurrenceMatrixBox.generateGreyValueCooccurrenceMatrixBox(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue());
        return result;
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
    static boolean grey_level_atttribute_filtering(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4) {
        boolean result = GreyLevelAtttributeFiltering.greyLevelAtttributeFiltering(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.BinaryFillHolesSliceBySlice
    //----------------------------------------------------
    /**
     * Fills holes (pixels with value 0 surrounded by pixels with value 1) in a binary image stack slice by slice.
     */
    static boolean binary_fill_holes_slice_by_slice(ClearCLImageInterface source, ClearCLImageInterface destination) {
        boolean result = BinaryFillHolesSliceBySlice.binaryFillHolesSliceBySlice(getCLIJ2(), source, destination);
        return result;
    }


    // net.haesleinhuepf.clijx.weka.BinaryWekaPixelClassifier
    //----------------------------------------------------
    /**
     * Applies a pre-trained CLIJx-Weka model to a 2D image. 
     * 
     * You can train your own model using menu Plugins > Segmentation > CLIJx Binary Weka Pixel ClassifierMake sure that the handed over feature list is the same used while training the model.
     */
    static boolean binary_weka_pixel_classifier(ClearCLBuffer input, ClearCLBuffer destination, String features, String modelfilename) {
        boolean result = BinaryWekaPixelClassifier.binaryWekaPixelClassifier(getCLIJ2(), input, destination, features, modelfilename);
        return result;
    }


    // net.haesleinhuepf.clijx.weka.WekaLabelClassifier
    //----------------------------------------------------
    /**
     * Applies a pre-trained CLIJx-Weka model to an image and a corresponding label map. 
     * 
     * Make sure that the handed over feature list is the same used while training the model.
     */
    static boolean weka_label_classifier(ClearCLBuffer input, ClearCLBuffer label_map, ClearCLBuffer destination, String features, String modelfilename) {
        boolean result = WekaLabelClassifier.wekaLabelClassifier(getCLIJ2(), input, label_map, destination, features, modelfilename);
        return result;
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
     * * local_mean_average_distance_of_touching_neighbors
     * * local_maximum_average_distance_of_touching_neighbors
     * * count_touching_neighbors
     * * local_minimum_average_distance_of_touching_neighbors
     * * average_touch_pixel_count
     * * local_minimum_count_touching_neighbors
     * * average_distance_n_closest_neighbors
     * * average_distance_of_touching_neighbors
     * * local_mean_count_touching_neighbors
     * * local_mean_average_distance_n_closest_neighbors
     * * local_maximum_average_distance_n_closest_neighbors
     * * local_standard_deviation_average_distance_of_touching_neighbors
     * * local_maximum_count_touching_neighbors
     * * local_standard_deviation_count_touching_neighbors
     * * local_standard_deviation_average_distance_n_closest_neighbors
     * * local_minimum_average_distance_n_closest_neighbors
     * 
     * Example: "MEAN_INTENSITY count_touching_neighbors"
     */
    static boolean generate_label_feature_image(ClearCLBuffer input, ClearCLBuffer label_map, ClearCLBuffer label_feature_image_destination, String feature_definitions) {
        boolean result = GenerateLabelFeatureImage.generateLabelFeatureImage(getCLIJ2(), input, label_map, label_feature_image_destination, feature_definitions);
        return result;
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
     * * local_mean_average_distance_of_touching_neighbors
     * * local_maximum_average_distance_of_touching_neighbors
     * * count_touching_neighbors
     * * local_minimum_average_distance_of_touching_neighbors
     * * average_touch_pixel_count
     * * local_minimum_count_touching_neighbors
     * * average_distance_n_closest_neighbors
     * * average_distance_of_touching_neighbors
     * * local_mean_count_touching_neighbors
     * * local_mean_average_distance_n_closest_neighbors
     * * local_maximum_average_distance_n_closest_neighbors
     * * local_standard_deviation_average_distance_of_touching_neighbors
     * * local_maximum_count_touching_neighbors
     * * local_standard_deviation_count_touching_neighbors
     * * local_standard_deviation_average_distance_n_closest_neighbors
     * * local_minimum_average_distance_n_closest_neighbors
     * 
     * Example: "MEAN_INTENSITY count_touching_neighbors"
     */
    static ClearCLBuffer generate_label_feature_image(ClearCLBuffer arg1, ClearCLBuffer arg2, String arg3) {
        ClearCLBuffer result = GenerateLabelFeatureImage.generateLabelFeatureImage(getCLIJ2(), arg1, arg2, arg3);
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LabelSurface
    //----------------------------------------------------
    /**
     * Takes a label map and excludes all labels which are not on the surface.
     * 
     * For each label, a ray from a given center towards the label. If the ray crosses another label, the labelin question is not at the surface and thus, removed.
     */
    static boolean label_surface(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3, double arg4, double arg5) {
        boolean result = LabelSurface.labelSurface(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue(), new Double (arg4).floatValue(), new Double (arg5).floatValue());
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.ReduceLabelsToLabelledSpots
    //----------------------------------------------------
    /**
     * Takes a label map and reduces all labels to their center spots. Label IDs stay and background will be zero.
     */
    static boolean reduce_labels_to_labelled_spots(ClearCLBuffer input_labels, ClearCLBuffer destination_labels) {
        boolean result = ReduceLabelsToLabelledSpots.reduceLabelsToLabelledSpots(getCLIJ2(), input_labels, destination_labels);
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.LabelMeanExtensionMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines for every label the mean distance of any pixel to the centroid and replaces every label with the that number.
     * 
     * 
     */
    static boolean label_mean_extension_map(ClearCLBuffer input, ClearCLBuffer destination) {
        boolean result = LabelMeanExtensionMap.labelMeanExtensionMap(getCLIJ2(), input, destination);
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.MeanZProjectionBelowThreshold
    //----------------------------------------------------
    /**
     * Determines the mean average intensity projection of an image along Z but only for pixels below a given threshold.
     */
    static boolean mean_z_projection_below_threshold(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3) {
        boolean result = MeanZProjectionBelowThreshold.meanZProjectionBelowThreshold(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.EuclideanDistanceFromLabelCentroidMap
    //----------------------------------------------------
    /**
     * Takes a label map, determines the centroids of all labels and writes the distance of all labelled pixels to their centroid in the result image.
     * Background pixels stay zero.
     */
    static boolean euclidean_distance_from_label_centroid_map(ClearCLBuffer labelmap_input, ClearCLBuffer destination) {
        boolean result = EuclideanDistanceFromLabelCentroidMap.euclideanDistanceFromLabelCentroidMap(getCLIJ2(), labelmap_input, destination);
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.GammaCorrection
    //----------------------------------------------------
    /**
     * Applies a gamma correction to an image.
     * 
     * Therefore, all pixels x of the Image X are normalized and the power to gamma g is computed, before normlization is reversed (^ is the power operator):f(x) = (x / max(X)) ^ gamma * max(X)
     */
    static boolean gamma_correction(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        boolean result = GammaCorrection.gammaCorrection(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.ZPositionOfMaximumZProjection
    //----------------------------------------------------
    /**
     * Determines a Z-position of the maximum intensity along Z and writes it into the resulting image.
     */
    static boolean z_position_of_maximum_z_projection(ClearCLImageInterface source, ClearCLImageInterface destination) {
        boolean result = ZPositionOfMaximumZProjection.zPositionOfMaximumZProjection(getCLIJ2(), source, destination);
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.ZPositionProjection
    //----------------------------------------------------
    /**
     * Project a defined Z-slice of a 3D stack into a 2D image.
     * 
     * The slice is determined using a separate 2D image.
     */
    static boolean z_position_projection(ClearCLImageInterface source_stack, ClearCLImageInterface z_position, ClearCLImageInterface destination) {
        boolean result = ZPositionProjection.zPositionProjection(getCLIJ2(), source_stack, z_position, destination);
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.ZPositionRangeProjection
    //----------------------------------------------------
    /**
     * Project multiple Z-slices of a 3D stack into a new 3D stack.
     * 
     * The slices are defined using a separate 2D image containing z-positions and two numbers defining the range.
     */
    static boolean z_position_range_projection(ClearCLImageInterface arg1, ClearCLImageInterface arg2, ClearCLImageInterface arg3, int arg4, int arg5) {
        boolean result = ZPositionRangeProjection.zPositionRangeProjection(getCLIJ2(), arg1, arg2, arg3, arg4, arg5);
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.VarianceSphere
    //----------------------------------------------------
    /**
     * Computes the local variance of a pixels spherical neighborhood. 
     * 
     * The spheres size is specified by 
     * its half-width, half-height and half-depth (radius). If 2D images are given, radius_z will be ignored. 
     */
    static boolean variance_sphere(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        boolean result = VarianceSphere.varianceSphere(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.StandardDeviationSphere
    //----------------------------------------------------
    /**
     * Computes the local standard deviation of a pixels spherical neighborhood. 
     * 
     * The spheres size is specified by 
     * its half-width, half-height and half-depth (radius). If 2D images are given, radius_z will be ignored. 
     */
    static boolean standard_deviation_sphere(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        boolean result = StandardDeviationSphere.standardDeviationSphere(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.VarianceBox
    //----------------------------------------------------
    /**
     * Computes the local variance of a pixels box neighborhood. 
     * 
     * The box size is specified by 
     * its half-width, half-height and half-depth (radius). If 2D images are given, radius_z will be ignored. 
     */
    static boolean variance_box(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        boolean result = VarianceBox.varianceBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.StandardDeviationBox
    //----------------------------------------------------
    /**
     * Computes the local standard deviation of a pixels box neighborhood. 
     * 
     * The box size is specified by 
     * its half-width, half-height and half-depth (radius). If 2D images are given, radius_z will be ignored. 
     */
    static boolean standard_deviation_box(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        boolean result = StandardDeviationBox.standardDeviationBox(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).intValue());
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.Tenengrad
    //----------------------------------------------------
    /**
     * Convolve the image with the Tenengrad kernel slice by slice.
     */
    static boolean tenengrad(ClearCLImageInterface source, ClearCLImageInterface destination) {
        boolean result = Tenengrad.tenengrad(getCLIJ2(), source, destination);
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.TenengradSliceBySlice
    //----------------------------------------------------
    /**
     * Convolve the image with the Tenengrad kernel slice by slice.
     */
    static boolean tenengrad_slice_by_slice(ClearCLImageInterface source, ClearCLImageInterface destination) {
        boolean result = TenengradSliceBySlice.tenengradSliceBySlice(getCLIJ2(), source, destination);
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.SobelSliceBySlice
    //----------------------------------------------------
    /**
     * Convolve the image with the Sobel kernel slice by slice.
     */
    static boolean sobel_slice_by_slice(ClearCLImageInterface source, ClearCLImageInterface destination) {
        boolean result = SobelSliceBySlice.sobelSliceBySlice(getCLIJ2(), source, destination);
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.ExtendedDepthOfFocusSobelProjection
    //----------------------------------------------------
    /**
     * Extended depth of focus projection maximizing local pixel intensity variance.
     * 
     * The sigma parameter allows controlling an Gaussian blur which should smooth the altitude map.
     */
    static boolean extended_depth_of_focus_variance_projection(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3) {
        boolean result = ExtendedDepthOfFocusSobelProjection.extendedDepthOfFocusVarianceProjection(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.ExtendedDepthOfFocusTenengradProjection
    //----------------------------------------------------
    /**
     * Extended depth of focus projection maximizing intensity in the local sobel image.
     * 
     * The sigma parameter allows controlling an Gaussian blur which should smooth the altitude map.
     */
    static boolean extended_depth_of_focus_tenengrad_projection(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3) {
        boolean result = ExtendedDepthOfFocusTenengradProjection.extendedDepthOfFocusTenengradProjection(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.ExtendedDepthOfFocusVarianceProjection
    //----------------------------------------------------
    /**
     * Extended depth of focus projection maximizing local pixel intensity variance.
     * 
     * The sigma parameter allows controlling an Gaussian blur which should smooth the altitude map.
     */
    static boolean extended_depth_of_focus_variance_projection(ClearCLImageInterface arg1, ClearCLImageInterface arg2, double arg3, double arg4, double arg5) {
        boolean result = ExtendedDepthOfFocusVarianceProjection.extendedDepthOfFocusVarianceProjection(getCLIJ2(), arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue(), new Double (arg5).floatValue());
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.DrawMeshBetweenNClosestLabels
    //----------------------------------------------------
    /**
     * Starting from a label map, draw lines between n closest labels for each label resulting in a mesh.
     * 
     * The end points of the lines correspond to the centroids of the labels. 
     */
    static boolean draw_mesh_between_n_closest_labels(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        boolean result = DrawMeshBetweenNClosestLabels.drawMeshBetweenNClosestLabels(getCLIJ2(), arg1, arg2, new Double (arg3).intValue());
        return result;
    }


    // net.haesleinhuepf.clijx.plugins.DrawMeshBetweenProximalLabels
    //----------------------------------------------------
    /**
     * Starting from a label map, draw lines between labels that are closer than a given distance resulting in a mesh.
     * 
     * The end points of the lines correspond to the centroids of the labels.
     */
    static boolean draw_mesh_between_proximal_labels(ClearCLBuffer arg1, ClearCLBuffer arg2, double arg3) {
        boolean result = DrawMeshBetweenProximalLabels.drawMeshBetweenProximalLabels(getCLIJ2(), arg1, arg2, new Double (arg3).floatValue());
        return result;
    }

}
// 527 methods generated.
