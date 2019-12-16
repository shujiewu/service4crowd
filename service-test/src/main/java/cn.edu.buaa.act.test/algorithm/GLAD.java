package cn.edu.buaa.act.test.algorithm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GLAD {
    static class AdvancedMath {

        public static double logProbL(int l, int z, double alpha, double beta, int numClasses) {
            if (z == l)
                return getLogSigma(alpha, beta);
            else
                return -Math.log(numClasses - 1) + getLogOneMinusSigma(alpha, beta);
        }

        public static double gaussPDF(double x) {
            return 1.0 / Math.sqrt(2.0 * Math.PI) * Math.exp(-Math.pow(x, 2.0) / 2.0);
        }

        public static double getSigma(double alpha, double beta) {
            return 1.0 / (1.0 + Math.exp(-Math.exp(beta) * alpha));
        }

        public static double getLogSigma(double alpha_i, double beta_j) {
            double logSigma = Math.log(getSigma(alpha_i, beta_j));
            /* NOTE: "WHY THE IF" (No longer gets tripped though)
	         * http://www.wolframalpha.com/input/?i=exp%28-exp%28x%29*y%29+with+x+from+0+to+10%2C+y+from+-10+to+1
	         */
            if (logSigma == Double.NEGATIVE_INFINITY) {
                logSigma = -Math.exp(beta_j) * alpha_i;
            }
            return logSigma;
        }

        public static double getLogOneMinusSigma(double alpha_i, double beta_j) {
            double logOneMinusSigma = Math.log(1 - getSigma(alpha_i, beta_j));
            if (logOneMinusSigma == Double.NEGATIVE_INFINITY) {
                logOneMinusSigma = -Math.exp(beta_j) * alpha_i;
            }
            return logOneMinusSigma;
        }

    }

    private static Logger log = LogManager.getLogger(GLAD.class);

    //read in from main of file

    private List<MultiLabel> labels;
    private Map<String, Integer> workers;
    private Map<String, Integer> items;
    private Map<String, Integer> classes;

    //read in from gold file if exists
    private Map<String, String> goldLabels;

    //set according to data read in
    private int numLabels = 0;
    private int numLabelers = 0;
    private int numItems = 0;
    private int numClasses = 0;
    private double priorZk = 0.0;    // set as 1/K in main()
	
	/* arrays sized according to above */

    //set as 1
    private double[] priorAlpha;
    private double[] priorBeta;

    //from E-step
    private double[][] probZX;

    //from M-step
    private double[] alpha;
    private double[] beta;
    private double[] dQdAlpha;
    private double[] dQdBeta;

    // statistics
    private int correct = 0;
    private int incorrect = 0;

    public void eStep() {

        for (MultiLabel lbl : labels) {
            for (int categ = 0; categ < numClasses; categ++) {
                // taskj属于categ类别的概率
                probZX[lbl.j][categ] = Math.log(priorZk) + AdvancedMath.logProbL(lbl.lij, categ, alpha[lbl.i], beta[lbl.j], numClasses);
            }
			
			/* "Exponentiate and re-normalize" */
            for (int j = 0; j < numItems; j++) {
                for (int i = 0; i < probZX[j].length; i++) {
                    double newval = Math.exp(probZX[j][i]);
                    probZX[j][i] = newval;
                }
                double sum = Arrays.stream(probZX[j]).sum();
                for (int i = 0; i < probZX[j].length; i++) {
                    double newval = probZX[j][i] / sum;
                    probZX[j][i] = newval;
                }
            }

        }
    }

    public double computeQ() {
		/* formula given as "Q = ..." on pg. 3 */
        double Q = 0.0;
        for (double[] arr : probZX) {
            for (double val : arr) {
                Q += val * Math.log(priorZk);
            }
        }

        for (MultiLabel label : labels) {
            for (int k = 0; k < numClasses; k++) {
                //System.out.println(label.i);
                //System.out.println(label.j);
                //System.out.println(label.lij);
                Q += probZX[label.j][k] * AdvancedMath.logProbL(label.lij, k, alpha[label.i], beta[label.j], numClasses);
            }
        }
		
		/* Add Gaussian (standard normal) prior for alpha and beta*/
        for (int i = 0; i < numLabelers; i++) {
            Q += Math.log(AdvancedMath.gaussPDF(alpha[i] - priorAlpha[i]));
        }

        for (int j = 0; j < numItems; j++) {
            Q += Math.log(AdvancedMath.gaussPDF(beta[j] - priorBeta[j]));  // had bug in original code
        }

        return Q;
    }


    public void ascend(double stepSize) {
        for (int i = 0; i < numLabelers; i++) {
            alpha[i] += stepSize * dQdAlpha[i];
        }
        for (int j = 0; j < numItems; j++) {
            beta[j] += stepSize * dQdBeta[j];
        }
    }


    public void doGradientAscent(int iterations, double stepSize, double tolerance) {
        int iteration = 0;
        double oldQ = computeQ();
        double Q = oldQ;
        Pair<double[], double[]> cloneTuple = new Pair<double[], double[]>(alpha.clone(), beta.clone());
        do {
            oldQ = Q;
            cloneTuple = new Pair<double[], double[]>(alpha.clone(), beta.clone());
            calcGradient();
            ascend(stepSize);
            Q = computeQ();
            iteration++;
        } while (iteration < iterations && (Math.abs(Q - oldQ) / oldQ) > tolerance && Q > oldQ);
        if (Q < oldQ) {
            alpha = cloneTuple.getFirst();
            beta = cloneTuple.getSecond();
            log.info("\nAfter " + iteration + " iterations of M-step, Q-score fell");
        }
    }

    // One must make the step size very small in order for every iteration
    // to actually increase the Q-score. This may be because there are so many
    // many parameters adjusted on each iteration that they cumulatively have a large effect
    // on the Q-score.

    public void mStep() {
        doGradientAscent(25, 1E-2, 1E-2);
    }


    public void calcGradient() {
		/* NOTE: dQdBeta is in terms of the REAL beta,
        whereas the array is in terms of LOG beta */

        // Original had this part
        for (int i = 0; i < numLabelers; i++) {
            dQdAlpha[i] = -(alpha[i] - priorAlpha[i]);
        }

        for (int j = 0; j < numItems; j++) {
            dQdBeta[j] = -(beta[j] - priorBeta[j]);
        }

        for (MultiLabel lbl : labels) {
            double sigma = AdvancedMath.getSigma(beta[lbl.j], alpha[lbl.i]);
            for (int k = 0, delta = lbl.delta(k); k < numClasses; k++) {
                dQdAlpha[lbl.i] += probZX[lbl.j][k] * ((delta - sigma) * Math.exp(beta[lbl.j]) +
                        (1.0 - delta) * Math.log(numClasses - 1.0));
                dQdBeta[lbl.j] += probZX[lbl.j][k] * ((delta - sigma) * alpha[lbl.i] +
                        (1.0 - delta) * Math.log(numClasses - 1.0));
            }
        }
    }

    public Map<Integer, Integer> fullGoldData() {
        Map<Integer, Integer> gold = new HashMap<Integer, Integer>();
        HashMap<Integer, String> flippedItems = (HashMap<Integer, String>) items.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
        for (int j = 0; j < numItems; j++) {
            for (int k = 0; k < numClasses; k++) {
                String theItem = flippedItems.getOrDefault(j, "ERROR");
                if (probZX[j][k] == Arrays.stream(probZX[j]).max().getAsDouble()) {
                    if (goldLabels.size() > 0) {
                        String goldLabel = goldLabels.getOrDefault(theItem, "?");
                        if (goldLabel == "?")
                            gold.put(j, k);
                        else {
                            gold.put(j, classes.get(goldLabel));
                        }
                    } else {
                        gold.put(j, k);
                    }
                }
            }
        }
        return gold;
    }

    public void resultsInfo() {
        HashMap<Integer, String> flippedWorkers = (HashMap<Integer, String>) workers.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
        HashMap<Integer, String> flippedItems = (HashMap<Integer, String>) items.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
        HashMap<Integer, String> flippedClasses = (HashMap<Integer, String>) classes.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
        for (int i = 0; i < numLabelers; i++) {
            log.info("Alpha[" + i + "] = " + alpha[i] + ": " + flippedWorkers.getOrDefault(i, "ERROR"));
        }

        for (int j = 0; j < numItems; j++) {
            log.info("Beta[" + j + "] = " + Math.exp(beta[j]) + ": " + flippedItems.getOrDefault(j, "ERROR"));
        }

        for (int j = 0; j < numItems; j++) {
            for (int k = 0; k < numClasses; k++) {
                String theClass = flippedClasses.getOrDefault(k, "ERROR");
                String theItem = flippedItems.getOrDefault(j, "ERROR");
                log.info("P(" + theItem + " = " + theClass + ") = " + probZX[j][k]);
            }
        }

        for (int j = 0; j < numItems; j++) {
            for (int k = 0; k < numClasses; k++) {
                String theClass = flippedClasses.getOrDefault(k, "ERROR");
                String theItem = flippedItems.getOrDefault(j, "ERROR");
                if (probZX[j][k] == Arrays.stream(probZX[j]).max().getAsDouble()) {
					/* print gold label info and calculate accuracy */
                    if (goldLabels.size() > 0) {
                        String goldLabel = goldLabels.getOrDefault(theItem, "?");
                        if (goldLabel == "?")
                            log.info(theClass + ": " + theItem + " : No gold label");
                        else if (goldLabel == theClass) {
                            log.info(theClass + ": " + theItem + " : Correct");
                            correct++;
                        } else {
                            log.info(theClass + ": " + theItem + ": Incorrect");
                            incorrect++;
                        }
                    } else {
                        log.info(theClass + ": " + theItem);
                    }
                }
            }
        }
		/* print Accuracy */
        if (goldLabels.size() > 0) {
            log.info("#Correct = " + correct);
            log.info("#Incorrect = " + incorrect);
            double accuracy = (double) correct / (correct + incorrect);
            log.info("Accuracy = " + accuracy);
        }
    }


    public void EM() {
        log.info("Beginning EM");
        double Q = computeQ();
        double lastQ = Q;
        double change = 0.0;
        do {
            lastQ = Q;
			/* Estimate P(Z|L,alpha,beta) */
            eStep();
            log.info("After E-Step: Q = " + computeQ());
			/* Estimate alpha and beta */
            mStep();
            Q = computeQ();
            change = Math.abs(((Q - lastQ) / lastQ));
            log.info("After M-Step: Q = " + Q);
            log.info("change-ratio is" + change);
        } while (change > 1E-10);
        eStep();
        log.info("Final Q = " + computeQ());
    }


    public GLAD(List<MultiLabel> labels,
                Map<String, Integer> workers,
                Map<String, Integer> items,
                Map<String, Integer> classes,
                Map<String, String> goldLabels) {
        this.labels = labels;
        this.workers = workers;
        this.items = items;
        this.classes = classes;
        this.goldLabels = goldLabels;

        numLabels = labels.size();
        numLabelers = workers.size();
        numItems = items.size();
        numClasses = classes.size();

        //System.out.println(numLabelers);

        // initializations
        priorAlpha = new double[numLabels];
        Arrays.fill(priorAlpha, 1.0);
        priorBeta = new double[numItems];
        Arrays.fill(priorBeta, 1.0);
        priorZk = 1.0 / numClasses;
        probZX = new double[numItems][];
        for (int i = 0; i < numItems; i++) {
            probZX[i] = new double[numClasses];
        }
        alpha = priorAlpha.clone();
        beta = priorBeta.clone();
        dQdAlpha = new double[numLabelers];
        dQdBeta = new double[numItems];

    }

    public double getAlpha(int i) {
        return alpha[i];
    }

    public double getBeta(int i) {
        return beta[i];
    }
}
