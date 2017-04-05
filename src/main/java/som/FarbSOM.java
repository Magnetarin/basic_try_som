package som;

/**
 * Created by Maxima on 05.04.2017.
 */
public class FarbSOM {
    Neuron[][] neurons;

    public static void main(String[] args){
        SelfOrganisingMap som = new SelfOrganisingMap();
        som.init(10,10,3,1);
        som.setCenterRandom();
    }
}
