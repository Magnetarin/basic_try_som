package som;

import java.text.DecimalFormat;

/**
 * Created by Maxima on 24.03.2017.
 */
public class Neuron {
    int id = 0;
    double[][] zentrum;
    int x = 0;
    int y = 0;
    Neuron nN;
    Neuron nO;
    Neuron nS;
    Neuron nW;

    public Neuron(){

    }

    public Neuron(int id, int x, int y){
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

    public double[][] getZentrum() {
        return zentrum;
    }

    public void setZentrum(double[][] zentrum) {
        this.zentrum = zentrum;
    }

    public void setZentrumRandom(int rows, int cols) {
        zentrum = new double[rows][cols];
        for(int i = 0; i<rows; i++){
            for(int j = 0; j<cols; j++){
                zentrum[i][j]=((int)(Math.random()*255+1))/255;
            }
        }
    }

    public void setZentrumZero(int rows, int cols) {
        zentrum = new double[rows][cols];
        for(int i = 0; i<rows; i++){
            for(int j = 0; j<cols; j++){
                zentrum[i][j]=0;
            }
        }
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
            for(int j = 0; j< zentrum[i].length;j++){
                erg+=(zentrum[i][j]>0? "x":" ");
            }
            erg+="\n";
        }
        return erg;
    }

    public String printZentrum(){
        DecimalFormat decimalFormat = new DecimalFormat("##.##");
        String erg ="";
        for(int i = 0; i< zentrum.length;i++){
            for(int j = 0; j< zentrum[i].length;j++){
                erg+="\t["+decimalFormat.format(zentrum[i][j])+"] ";
            }
            erg+="\n";
        }
        return erg;
    }
}
