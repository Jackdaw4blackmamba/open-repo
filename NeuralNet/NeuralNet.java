import ActivationLayers.*;
import Basics.*;
import Basics.Dictionary;
import Basics.Vector;
import CommonLayers.*;
import Initializers.*;
import LastLayers.LastLayer;
import LastLayers.LastLayerType;
import LastLayers.SoftmaxWithLossLayer;
import Optimizers.*;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class NeuralNet {
    private Dictionary<Layer> layers;
    private LastLayer lastLayer;

    private int inputSize;
    private List<Integer> hiddenSizeList;
    private int outputSize;
    private List<ActivationType> actTypeList;

    private Dictionary<Tensor> params;
    private List<Double> lossList;

    private Optimizer optimizer;
    private OptimizerType optimizerType;

    private Initializer initializer;
    private InitializerType initializerType;

    private boolean useBatchNorm;
    private boolean useDropout;
    private double dropoutRatio;
    private double weightDecayLambda;

    private int batchSize;

    private boolean echoable;

    public NeuralNet(int inputSize, int outputSize){
        layers = new Dictionary<>();
        lastLayer = null;
        params = new Dictionary<>();
        lossList = new ArrayList<>();
        optimizer = null;
        optimizerType = OptimizerType.SGD;
        initializer = null;
        initializerType = InitializerType.ZERO_TO_ONE;
        useBatchNorm = false;
        useDropout = false;
        dropoutRatio = 0.5;
        weightDecayLambda = 0.0;
        batchSize = 1;
        echoable = false;

        this.inputSize = inputSize;
        this.outputSize = outputSize;
        hiddenSizeList = new ArrayList<>();
        actTypeList = new ArrayList<>();
    }

    public void addLayer(int hiddenSize, ActivationType actType){
        hiddenSizeList.add(hiddenSize);
        actTypeList.add(actType);
    }

    public void setLastLayer(LastLayerType type){
        lastLayer = getLastLayer(type);
    }

    private Tensor predict(Tensor x, boolean isTraining){
        Tensor tmp = x;
        for(String key : layers.orderedKeys()){
            if(key.contains("Dropout"))
                ((DropoutLayer) layers.get(key)).isTraining = isTraining;
            else if(key.contains("BatchNorm"))
                ((BatchNormalization) layers.get(key)).isTraining = isTraining;
            tmp = layers.get(key).forward(tmp);
        }
        return tmp;
    }

    private double loss(Tensor x, Tensor t, boolean isTraining){
        Tensor y = predict(x, isTraining);
        double weightDecay = 0;
        for(int i = 0; i < hiddenSizeList.size() + 1; i++){
            Tensor W = params.get("W" + (i + 1));
            weightDecay += 0.5 * weightDecayLambda * TensorUtil.sum(W.pow(2));
        }
        return lastLayer.forward(y, t) + weightDecay;
    }

    public Dictionary<Tensor> numericalGradient(Tensor x, Tensor t){
        Function<Tensor, Double> lossW = W -> loss(x, t, true);
        Dictionary<Tensor> grads = new Dictionary<>();

        for(int i = 0; i < hiddenSizeList.size() + 1; i++){
            grads.put("W" + (i + 1), __numericalGradient(lossW, params.get("W" + (i + 1))));
            grads.put("b" + (i + 1), __numericalGradient(lossW, params.get("b" + (i + 1))));

            if(useBatchNorm && i != hiddenSizeList.size()){
                grads.put("gamma" + (i + 1), __numericalGradient(lossW, params.get("gamma" + (i + 1))));
                grads.put("beta" + (i + 1), __numericalGradient(lossW, params.get("beta" + (i + 1))));
            }
        }
        return grads;
    }

    private Tensor __numericalGradient(Function<Tensor, Double> f, Tensor x){
        final double h = 1e-4;
        Tensor grad = new Matrix(x.shape());

        for(int i = 0; i < x.shape().shapes[0]; i++)
            for(int j = 0; j < x.shape().shapes[1]; j++){
                double tmp = x.get(i, j);
                double fxh1, fxh2;

                x.set(tmp + h, i, j);
                fxh1 = f.apply(x);

                x.set(tmp - h, i, j);
                fxh2 = f.apply(x);

                grad.set((fxh1 - fxh2) / (2 * h), i, j);

                x.set(tmp, i, j);
            }
        return grad;
    }

    private Dictionary<Tensor> gradient(Tensor x, Tensor t){
        Tensor dout;
        List<Layer> layers;
        Dictionary<Tensor> grads;

        // propagate forward
        loss(x, t, true);

        // propagate backward
        dout = lastLayer.backward(1);

        layers = this.layers.orderedValues();
        for(int i = layers.size() - 1; i >= 0; i--)
            dout = layers.get(i).backward(dout);

        grads = new Dictionary<>();
        for(int i = 0; i < hiddenSizeList.size() + 1; i++){
            AffineLayer affineLayer = (AffineLayer)this.layers.get("Affine" + (i + 1));
            grads.put("W" + (i + 1), affineLayer.dW.add(params.get("W" + (i + 1)).scale(weightDecayLambda)));
            grads.put("b" + (i + 1), affineLayer.db);

            if(useBatchNorm && i != hiddenSizeList.size()){
                BatchNormalization batchLayer = (BatchNormalization)this.layers.get("BatchNorm" + (i + 1));
                grads.put("gamma" + (i + 1), batchLayer.dgamma);
                grads.put("beta" + (i + 1), batchLayer.dbeta);
            }
        }

        return grads;
    }

    public void train(List<Tensor> xs, List<Tensor> ts, int numEpochs, double learningRate){
        int numIterations;
        int itrPerEpoch;

        optimizer = getOptimizer(learningRate);
        initializer = getInitializer();
        initWeights();
        initLayers();
        lossList.clear();

        numIterations = xs.size() * numEpochs;
        itrPerEpoch = xs.size() / batchSize;
        for(int itr = 0; itr < numIterations; itr++){
            List<Integer> batchMask = chooseRandomIndices(xs.size(), batchSize);
            Tensor xBatch = getChosenData(xs, batchMask);
            Tensor tBatch = getChosenData(ts, batchMask);
            Dictionary<Tensor> grads;
            double loss;

            grads = gradient(xBatch, tBatch);

            optimizer.update(params, grads);

            loss = loss(xBatch, tBatch, true);
            lossList.add(loss);

            if(itr % itrPerEpoch == 0){
                if(echoable){
                    System.out.println("Itr " + itr + "; Loss=" + loss);

                    System.out.println("=== Params ===");
                    for(String key : params.orderedKeys()){
                        System.out.println(key);
                        params.get(key).print();
                        System.out.println();
                    }

                    Dictionary<Tensor> g = gradient(xBatch, tBatch);
                    System.out.println("=== Grads ===");
                    for(String key : g.orderedKeys()){
                        System.out.println(key);
                        g.get(key).print();
                        System.out.println();
                    }
                }
            }
        }
    }

    public List<Tensor> test(List<Tensor> xs){
        List<Tensor> res = new ArrayList<>();
        for(int i = 0; i < xs.size(); i++){
            Tensor tmp = xs.get(i);
            tmp = predict(tmp, false);

            res.add(TensorUtil.row(tmp, 0));
        }
        return res;
    }

    public static void printResults(List<Tensor> ys, List<Tensor> ts, boolean useSoftmax, BiPredicate<Tensor, Tensor> predicate){
        int correct = 0;
        int cnt = 0;
        for(int i = 0; i < ys.size(); i++){
            Tensor y = ys.get(i);
            if(useSoftmax) y = FunctionUtil.softmax(y);
            Tensor t = ts.get(i);
            for(int j = 0; j < y.shape().shapes[0]; j++)
                System.out.print(String.format("%.2f ", y.get(j)));
            System.out.print("( ");
            for(int j = 0; j < t.shape().shapes[0]; j++)
                System.out.print(String.format("%.2f ", t.get(j)));
            System.out.print(")");
            System.out.print(": ");
            if(predicate.test(ys.get(i), ts.get(i))){
                System.out.print("T");
                correct++;
            }
            else
                System.out.print("F");
            System.out.println();
            cnt++;
        }
        System.out.println();
        System.out.println("Accuracy: " + String.format("%.3f", (double)correct / cnt));
    }

    public List<Double> getLossList(){
        return lossList;
    }

    public void setOptimizer(OptimizerType type){
        optimizerType = type;
    }

    public void setInitializer(InitializerType type){
        initializerType = type;
    }

    public void applyBatchNorm(boolean useBatchNorm){
        this.useBatchNorm = useBatchNorm;
    }

    public void setDropoutRatio(double dropoutRatio){
        this.dropoutRatio = dropoutRatio;
        useDropout = dropoutRatio != 0.0;
    }

    public void setBatchSize(int batchSize){
        this.batchSize = batchSize;
    }

    public void setEchoable(boolean echoable){
        this.echoable = echoable;
    }

    private List<Integer> chooseRandomIndices(int ceiling, int numIndices){
        List<Integer> indices = new ArrayList<>();
        Random rand = new Random();
        for(int i = 0; i < numIndices; i++) {
            int index = rand.nextInt(ceiling);
            while(indices.contains(index))
                index = rand.nextInt(ceiling);
            indices.add(index);
        }
        return indices;
    }

    private Tensor getChosenData(List<Tensor> data, List<Integer> indices){
        Tensor m = new Matrix(indices.size(), data.get(0).shape().shapes[0]);
        for(int i = 0; i < m.shape().shapes[0]; i++) {
            Tensor row = TensorUtil.row(data.get(indices.get(i)), 0);
            for(int j = 0; j < m.shape().shapes[1]; j++)
                m.set(row.get(j), i, j);
        }
        return m;
    }

    private void initWeights(){
        List<Integer> allSizeList = new ArrayList<>();
        allSizeList.add(inputSize);
        for(int i = 0; i < hiddenSizeList.size(); i++)
            allSizeList.add(hiddenSizeList.get(i));
        allSizeList.add(outputSize);
        for(int i = 0; i < allSizeList.size() - 1; i++){
            int numPrevNeurons = allSizeList.get(i);
            int numNextNeurons = allSizeList.get(i + 1);
            Tensor W = new Matrix(numPrevNeurons, numNextNeurons);
            W = W.forEach(c -> initializer.random(numPrevNeurons));
            Tensor b = Vector.getInstanceFilledWith(0, numNextNeurons);
            params.put("W" + (i + 1), W);
            params.put("b" + (i + 1), b);
        }
    }

    private void initLayers(){
        List<Integer> allSizeList = new ArrayList<>();
        allSizeList.add(inputSize);
        for(int i = 0; i < hiddenSizeList.size(); i++)
            allSizeList.add(hiddenSizeList.get(i));
        allSizeList.add(outputSize);
        for(int i = 0; i < allSizeList.size() - 2; i++){
            //int numPrevNeurons = allSizeList.get(i);
            int numNextNeurons = allSizeList.get(i + 1);

            layers.put("Affine" + (i + 1), (new AffineLayer(params.get("W" + (i + 1)), params.get("b" + (i + 1)))));

            if(useBatchNorm){
                params.put("gamma" + (i + 1), Vector.getInstanceFilledWith(1, numNextNeurons));
                params.put("beta" + (i + 1), Vector.getInstanceFilledWith(0, numNextNeurons));
                layers.put("BatchNorm" + (i + 1), new BatchNormalization(params.get("gamma" + (i + 1)), params.get("beta" + (i + 1))));
            }

            layers.put("Activation" + (i + 1), getActivationLayer(actTypeList.get(i)));

            if(useDropout)
                layers.put("Dropout" + (i + 1), new DropoutLayer(dropoutRatio));
        }
        int idx = allSizeList.size() - 1;
        layers.put("Affine" + (idx), new AffineLayer(params.get("W" + idx), params.get("b" + idx)));
    }

    private Optimizer getOptimizer(double learningRate){
        return
            optimizerType == OptimizerType.MOMENTUM ? new Momentum(learningRate) :
            optimizerType == OptimizerType.ADAGRAD  ? new AdaGrad(learningRate)  :
            optimizerType == OptimizerType.NESTEROV ? new Nesterov(learningRate) :
            optimizerType == OptimizerType.RMSPROP  ? new RMSprop(learningRate)  :
            optimizerType == OptimizerType.ADAM     ? new Adam(learningRate)     :
                                                      new SGD(learningRate);
    }

    private Initializer getInitializer(){
        return
            initializerType == InitializerType.GAUSSIAN ? new Gaussian() :
            initializerType == InitializerType.XAVIER   ? new Xavier()   :
            initializerType == InitializerType.HE       ? new He()       :
                                                          new ZeroToOne();
    }

    private ActivationLayer getActivationLayer(ActivationType type){
        return
            type == ActivationType.RELU ? new ReLULayer() :
            type == ActivationType.TANH ? new TanhLayer() :
                                          new SigmoidLayer();
    }

    private LastLayer getLastLayer(LastLayerType type){
        return new SoftmaxWithLossLayer();
    }
}
