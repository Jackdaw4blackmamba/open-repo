package Initializers;

import java.util.Random;

public class He implements Initializer {
    public double random(int numPrevNeurons){
        return new Random().nextGaussian() * Math.sqrt(2.0 / numPrevNeurons);
    }
}
