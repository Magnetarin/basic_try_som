package som;

import java.util.ArrayList;

/**
 * Created by Maxima on 15.03.2017.
 */
public class Neuron_v1 {
    int id = 0;
    double input = 0;
    double aktivierung = 0;
    double output = 0;
    boolean isInput = false;
    double zentrum = 0;
    ArrayList<Neuron_v1> connectionList = new ArrayList();

    public Neuron_v1(){
        zentrum = (int)(Math.random()*100);
    }

    public Neuron_v1(int id, boolean isInput){
        this.id = id;
        this.isInput = isInput;
        zentrum = (int)(Math.random()*10);
    }


    public double calcInput(double[] inputValues, double[] wieghtValues){
        double input = 0;
        if(inputValues.length == wieghtValues.length){
            for (int i = 0; i<inputValues.length;i++){
                input+=inputValues[i]*wieghtValues[i];
            }
        }
        return input;
    }

    public double getInput(){
        return input;
    }

    public void setInput(double input){
        this.input = input;
    }

    public double getOutput(){
        if(isInput){
            return this.input;
        } else {
            return this.aktivierung;
        }
    }

    public int getId() {
        return id;
    }

    public double getAktivierung() {
        return aktivierung;
    }

    public void setAktivierung(double aktivierung) {
        this.aktivierung = aktivierung;
    }

    public double getZentrum() {
        return zentrum;
    }

    public void setZentrum(double zentrum) {
        this.zentrum = zentrum;
    }
}
