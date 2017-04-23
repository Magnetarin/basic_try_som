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
    FarbNeuron nN;
    FarbNeuron nO;
    FarbNeuron nS;
    FarbNeuron nW;

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
        zentrum = new int[3];
        zentrum[0] = ((int)(Math.random()*255+1));
        zentrum[1] = ((int)(Math.random()*255+1));
        zentrum[2] = ((int)(Math.random()*255+1));
    }

    public FarbNeuron[] getAllNeighbor(){
        return new FarbNeuron[]{nN,nO,nS,nW};
    }

    public FarbNeuron getnN() {
        return nN;
    }

    public void setnN(FarbNeuron nN) {
        this.nN = nN;
    }

    public FarbNeuron getnO() {
        return nO;
    }

    public void setnO(FarbNeuron nO) {
        this.nO = nO;
    }

    public FarbNeuron getnS() {
        return nS;
    }

    public void setnS(FarbNeuron nS) {
        this.nS = nS;
    }

    public FarbNeuron getnW() {
        return nW;
    }

    public void setnW(FarbNeuron nW) {
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
