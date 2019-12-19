import ActivationLayers.*;
import Basics.*;
import Basics.Vector;
import Initializers.*;
import LastLayers.*;
import Optimizers.*;

import java.util.*;
import java.io.*;

public class Main {
    public static void main(String[] args){
        NeuralNet net;
        List<Tensor> x; // data to learn
        List<Tensor> t; // expected outputs
        List<Tensor> y;

        x = getLearningData();
        t = getTestData();

        net = new NeuralNet(3, 2);
        net.addLayer(5, ActivationType.SIGMOID);
        net.addLayer(7, ActivationType.SIGMOID);
        net.setLastLayer(LastLayerType.SOFTMAX);

        net.setOptimizer(OptimizerType.ADAM);
        net.setInitializer(InitializerType.XAVIER);

        net.train(x, t, 50, 0.1);
        y = net.test(x);

        NeuralNet.printResults(y, t, true, (yT, tT)->maxIndexOf(yT).equals(maxIndexOf(tT)));
    }

    private static List<Tensor> getLearningData(){
        List<Tensor> list;
        double[][] rawData;

        list = new ArrayList<>();
        rawData = new double[][]{
                {0, 0, 0},
                {0, 0, 1},
                {0, 1, 0},
                {0, 1, 1},
                {1, 0, 0},
                {1, 0, 1},
                {1, 1, 0},
                {1, 1, 1}
        };
        for(int i = 0; i < rawData.length; i++)
            list.add(new Vector(rawData[i]));

        return list;
    }

    private static List<Tensor> getTestData(){
        List<Tensor> list;
        double[][] rawData;

        list = new ArrayList<>();
        rawData = new double[][]{
                {0, 1},
                {0, 1},
                {0, 1},
                {1, 0},
                {0, 1},
                {1, 0},
                {1, 0},
                {1, 0}
        };
        for(int i = 0; i < rawData.length; i++)
            list.add(new Vector(rawData[i]));

        return list;
    }

    
    private static Shape maxIndexOf(Tensor t){
        Shape maxIdx = null;
        if(t.shape().metaShape == 1){
            maxIdx = new Shape(0);
            for(int i = 0; i < t.shape().shapes[0]; i++)
                if(t.get(maxIdx.shapes[0]) < t.get(i))
                    maxIdx.shapes[0] = i;
        }
        else if(t.shape().metaShape == 2) {
            maxIdx = new Shape(0, 0);
            for (int i = 0; i < t.shape().shapes[0]; i++)
                for(int j = 0; j < t.shape().shapes[1]; j++)
                    if (t.get(maxIdx.shapes[0], maxIdx.shapes[1]) < t.get(i, j)){
                        maxIdx.shapes[0] = i;
                        maxIdx.shapes[1] = j;
                    }
        }
        return maxIdx;
    }

    
