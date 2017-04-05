package som;

import java.text.DecimalFormat;

/**
 * Created by Maxima on 05.04.2017.
 */
public class FarbNeuron {
    int id = 0;
    int[] zentrum;
    int x = 0;
    int y = 0;
    Neuron nN;
    Neuron nO;
    Neuron nS;
    Neuron nW;

    public FarbNeuron(){

    }

    public FarbNeuron(int id, int x, int y){
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int[] getZentrum() {
        return zentrum;
    }

    public void setZentrum(int[] zentrum) {
        this.zentrum = zentrum;
    }

    public void setZentrumRandom() {
        zentrum[0] = ((int)(Math.random()*255+1));
        zentrum[1] = ((int)(Math.random()*255+1));
        zentrum[2] = ((int)(Math.random()*255+1));
    }

    public Neuron[] getAllNeighbor(){
        return new Neuron[]{nN,nO,nS,nW};
    }

    public Neuron getnN() {
        return nN;
    }

    public void setnN(Neuron nN) {
        this.nN = nN;
    }

    public Neuron getnO() {
        return nO;
    }

    public void setnO(Neuron nO) {
        this.nO = nO;
    }

    public Neuron getnS() {
        return nS;
    }

    public void setnS(Neuron nS) {
        this.nS = nS;
    }

    public Neuron getnW() {
        return nW;
    }

    public void setnW(Neuron nW) {
        this.nW = nW;
    }

    public String toString(){
        String erg = "id: "+id+"\n";
        erg+="\t Zentrum: \n"+printZentrum();
        return erg;
    }
    private String printZentrumX(){
        DecimalFormat decimalFormat = new DecimalFormat("##.##");
        String erg ="";
        for(int i = 0; i< zentrum.length;i++){
            erg+=(zentrum[i]>0? "x":" ");
            erg+="\n";
        }
        return erg;
    }

    public String printZentrum(){
        DecimalFormat decimalFormat = new DecimalFormat("##.##");
        String erg ="";
        for(int i = 0; i< zentrum.length;i++){
            erg+="\t["+decimalFormat.format(zentrum[i])+"] ";
            erg+="\n";
        }
        return erg;
    }

}
