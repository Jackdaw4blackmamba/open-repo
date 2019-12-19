package Initializers;

import java.util.Random;

public class Gaussian implements Initializer {
    public double random(int numPrevNeurons){
        return new Random().nextGaussian();
    }
}
