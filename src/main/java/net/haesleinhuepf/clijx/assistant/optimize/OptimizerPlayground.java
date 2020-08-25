package net.haesleinhuepf.clijx.assistant.optimize;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.plugin.frame.RoiManager;
import net.haesleinhuepf.clijx.assistant.AssistantGUIStartingPoint;

class OptimizerPlayground {
    public static void main(String[] args) {

        new ImageJ();
        ImagePlus inputImp = IJ.openImage("C:\\structure\\data\\opt\\blobs.tif");
        IJ.run(inputImp, "32-bit", "");

        RoiManager rm = new RoiManager();
        rm.runCommand("open", "C:\\structure\\data\\opt\\RoiSet.zip");

        inputImp.show();
        rm.runCommand(inputImp, "Show all");

        new AssistantGUIStartingPoint().run("");

/*
        CLIJ2 clij2 = CLIJ2.getInstance();

        ClearCLBuffer ground_truth = OptimizationUtilities.makeGroundTruth(clij2, inputImp.getWidth(), inputImp.getHeight(), inputImp.getBitDepth(), rm);
        ClearCLBuffer mask = clij2.create(ground_truth);

        clij2.greaterConstant(ground_truth, mask, 0);

        ClearCLBuffer input = clij2.push(inputImp);
        ClearCLBuffer output = clij2.create(input);

        Workflow workflow = new Workflow(clij2, input, output);

        System.out.println(Arrays.toString(workflow.getNumericParameterNames()));
        System.out.println(Arrays.toString(workflow.getPluginIndices()));
        System.out.println(Arrays.toString(workflow.getParameterIndices()));


        int[] parameter_index_map = OptimizationUtilities.getParameterIndexMap(workflow, true);
        if (parameter_index_map == null) {
            System.out.println("Optimization cancelled");
            return;
        }
        System.out.println(parameter_index_map);

        //if (true) return;


        BinaryImageFitnessFunction f = new BinaryImageFitnessFunction(clij2, workflow,
                parameter_index_map,
                ground_truth,
                mask
        );

        SimplexOptimizer optimizer = new SimplexOptimizer(-1, 1e-5);

        NelderMeadSimplex simplex = new NelderMeadSimplex(f.getNumDimensions());
        double[] initialSolution = f.getCurrent();
        PointValuePair solution = optimizer.optimize(new MaxEval(1000), new InitialGuess(initialSolution), simplex, new ObjectiveFunction(f), GoalType.MINIMIZE);

        // compute optimum again
        System.out.println("Optimum: ");
        f.value(solution.getKey());

        //UnivariatePointValuePair next =  new UnivariatePointValuePair(solution.getPointRef()[0], solution.getValue());

        System.out.println("Bye");
*/



/*                new MultivariateRealFunction() {
                    public double value(double[] x) {
                        ++count;
                        double a = x[0] + 10 * x[1];
                        double b = x[2] - x[3];
                        double c = x[1] - 2 * x[2];
                        double d = x[0] - x[3];
                        return a * a + 5 * b * b + c * c * c * c + 10 * d * d * d * d;
                    }
                };
*/





        //count = 0;

     //   SimplexSolver solver = new SimplexSolver();

       // ArrayList<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
        /*
        for(int i=0; i<A.length; i++) {
            double[] Av = new double[A[i].length];
            for(int j=0; j<A[i].length; j++) {
                Av[j] = A[i][j];
            }
            constraints.add(new LinearConstraint(Av, Relationship.LEQ, b[i]));
        }*/



    //    PointValuePair optSolution = solver.optimize(new MaxIter(100), f, new LinearConstraintSet(constraints),
      //          GoalType.MAXIMIZE, new NonNegativeConstraint(true));


/*
        SimplexOptimizer optimizer = new SimplexOptimizer(1, 1e-1);
        optimizer.


        //optimizer.setSimplex(new MultiDirectionalSimplex(4));
        optimum = optimizer.optimize(new MaxIter(1000), f, GoalType.MINIMIZE, new double[] { 3, -1, 0, 1 });
        Assert.assertEquals(count, optimizer.getEvaluations());
        Assert.assertTrue(optimizer.getEvaluations() > 800);
        Assert.assertTrue(optimizer.getEvaluations() < 900);
        Assert.assertTrue(optimum.getValue() > 1e-2);*/

    }

}
