package Initializers;

import java.util.Random;

public class ZeroToOne implements Initializer {
    public double random(int numPrevNeurons){
        return new Random().nextDouble();
    }
}
