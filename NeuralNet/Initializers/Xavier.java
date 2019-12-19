package Initializers;

import java.util.Random;

public class Xavier implements Initializer {
    public double random(int numPrevNeurons){
        return new Random().nextGaussian() * 1 / Math.sqrt(numPrevNeurons);
    }
}
