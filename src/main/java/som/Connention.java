package som;

/**
 * Created by Maxima on 19.03.2017.
 */
public class Connention {
    Neuron_v1 left;
    Neuron_v1 right;
    double weight = 0;

    public Connention(){}

    public Connention(Neuron_v1 l, Neuron_v1 r){
        left = l;
        right = r;
        weight = Math.random();
    }

    public Connention(Neuron_v1 l, Neuron_v1 r, double w){
        left = l;
        right = r;
        weight = w;
    }

    public void setWeight(double weight){
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }

    public Neuron_v1 getLeft() {
        return left;
    }

    public void setLeft(Neuron_v1 left) {
        this.left = left;
    }

    public Neuron_v1 getRight() {
        return right;
    }

    public void setRight(Neuron_v1 right) {
        this.right = right;
    }
}
