package som;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

/**
 * Created by Maxima on 05.04.2017.
 */
public class FarbSOM {
    FarbNeuron[][] neurons;
    int rowCount=0;
    int colCount=0;
    int[][] trainInput;
    private double glockenRadius = 1;
    public JFrame frame = null;

    public static void main(String[] args){
        FarbSOM som = new FarbSOM();
        int length = 10;
        som.init(length,length,3,1);
        som.setCenterRandom(true);

//        {255,255,255},{0,0,0},
        int[][] tInput = {{255,0,0},{255,255,0},{0,255,0},{0,255,255},{0,0,255},{255,0,255}};//{255,0,0},{0,255,0},{0,0,255},
//        int[][] tInput ={{255,0,0},{0,255,0},{0,0,255}};
//        {226, 66, 244},{65, 187, 244},{149, 244, 65},{152, 183, 166},
//        tInput = som.getInput();
        som.setTrainInput(tInput);
        som.frame = new JFrame();
        JPanel panel = new JPanel(new GridLayout(length, length)) {//, 5, 5
            private static final long serialVersionUID = 1L;

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(500, 500);
            }
        };

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                JPanel p2 = new JPanel();
                p2.setBackground(Color.red);

                panel.add(p2);
            }
        }

        som.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        som.frame.setTitle("Maze Game");
        som.frame.setContentPane(panel);
        som.frame.pack();
        som.frame.setLocationRelativeTo(null);
        som.frame.setVisible(true);

//        for(int i = 0;i < som.neurons.length; i++){
//            for(int j = 0; j < som.neurons[i].length; j++){
//                int x = i;
//                int y = j;
//                int r = som.neurons[x][y].getZentrum()[0];
//                int g = som.neurons[x][y].getZentrum()[1];
//                int b = som.neurons[x][y].getZentrum()[2];
////                frame.getContentPane().setBackground(new Color(r,g,b));
////                frame.getContentPane().getComponent(0).setForeground(new Color(r,g,b));
//
//                som.frame.getContentPane().getComponents()[(x+y*(som.neurons.length))].setBackground(new Color(r,g,b));
//
//            }
//        }
        som.reColor();

        int r = 0;
        int g = 255;
        int b = 0;
//        frame.getContentPane().setBackground(new Color(r,g,b));

        System.out.println(som.getMax(new int[]{r,g,b}).getId());

        int time = 1;
        try {
            som.trainMapWithSample(1,time);
            som.trainMapWithSample(2,time);
            som.trainMapWithSample(3,time);
            som.trainMapWithSample(4,time);
            som.trainMapWithSample(5,time);
            som.trainMapWithSample(6,time);
            som.trainMapWithSample(7,time);
            som.trainMapWithSample(8,time);
            som.trainMapWithSample(9,time);
            som.trainMapWithSample(10,time);
//            som.trainMapWithSample(11,time);
//            som.trainMapWithSample(13,time);
//            som.trainMapWithSample(14,time);
//            som.trainMapWithSample(15,time);
//            som.trainMapWithSample(16,time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String s = "";

        do{
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Enter String");
            try {
                s = br.readLine();
                if(s.trim().length() > 0 && s.matches("[0-9]*")) {
                    r = Integer.parseInt(s);
                    s = br.readLine();
                    if(s.trim().length() > 0 && s.matches("[0-9]*")) {
                        g = Integer.parseInt(s);
                        s = br.readLine();
                        if(s.trim().length() > 0 && s.matches("[0-9]*")) {
                            b = Integer.parseInt(s);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }while(!s.equalsIgnoreCase("end"));

    }

    public void reColor(){
        for(int i = 0;i < neurons.length; i++){
            for(int j = 0; j < neurons[i].length; j++){
                int x = i;
                int y = j;
                int r = neurons[x][y].getZentrum()[0];
                int g = neurons[x][y].getZentrum()[1];
                int b = neurons[x][y].getZentrum()[2];
//                frame.getContentPane().setBackground(new Color(r,g,b));
//                frame.getContentPane().getComponent(0).setForeground(new Color(r,g,b));

                frame.getContentPane().getComponents()[(x+y*(neurons.length))].setBackground(new Color(r,g,b));

            }
        }
    }

    public void init(int rows,int col,int cRows, int cCols){
        rowCount = rows;
        colCount = col;
        int mult = (rows>col? col :rows);
        neurons = new FarbNeuron[rows][col];
        for(int i = 0; i<rows;i++){
            for(int j = 0; j<col;j++){
                neurons[i][j] = new FarbNeuron(i+j*mult,i,j);
            }
        }
        registerNeighbor();
    }

    public void registerNeighbor(){
        for(int i = 0; i<rowCount;i++){
            for(int j = 0; j<colCount;j++){
                neurons[i][j].setnN((i-1) >  -1        ? neurons[i-1][j] : null);
                neurons[i][j].setnO((j+1) < colCount  ? neurons[i][j+1] : null);
                neurons[i][j].setnS((i+1) < rowCount  ? neurons[i+1][j] : null);
                neurons[i][j].setnW((j-1) >  -1        ? neurons[i][j-1] : null);
            }
        }
    }

    public void setCenterRandom(boolean isFarb){
        for(int i = 0; i < neurons.length; i++){
            for(int j = 0; j < neurons[i].length; j++){
                neurons[i][j].setZentrumRandom();
            }
        }
    }


    //Nachbarn kleiner machen
    public void trainMapWithSample(double zeitkoeffizient,int time) throws InterruptedException {
        System.out.println("Training Map with: " + zeitkoeffizient + " and " + time);
        for (int j = 0; j < time; j++) {
            for (int i = 0; i < trainInput.length; i++) {
//                TimeUnit.MICROSECONDS.sleep(500);
                int[] input = trainInput[i];
                FarbNeuron winner = getMax(input);
                for(int k = 0; k < neurons.length; k++){
                    for (int l =0; l <neurons[k].length;l++){
                        FarbNeuron n = neurons[k][l];
                        int distance = getDistance(winner.x,n.x,winner.y,n.y);
                        if(distance > glockenRadius){
                            continue;
                        }
//                        (lernrate im laufe der zeit) · h(i, k, t) · (p − ck)
//                        h(i, k, t) = e(−(|gi−gk|^2)/(2·o(t)^2))
//                        o ... breite der glocke
                        double a = glockenRadius - (zeitkoeffizient)/ glockenRadius;
//                        double a1 = zeitkoeffizient/100;
//                        a1 = (a1 >= 1 ? 0.99 : a1);
//                        double a2 = 10 - zeitkoeffizient/10;
//                        a2 = (a2 <= 0 ? 0.1 : a2);
//                        double gaussian = (1.0 - a1) * Math.exp(-getDistance(winner.x, winner.y, n.x, n.y) / a2);
                        a = (a<0 ? 0.1:a);
                        lernen(n,input,(1-(zeitkoeffizient/100))*Math.exp(-distance/(a<0 ? 0.1:a)));
                        double h = Math.exp(-(distance^2)/(2*a*a));
//                        lernen(n,input,zeitkoeffizient*h);
//                        lernen(n,input,gaussian);
                    }
                }
                reColor();
            }
        }
    }

    public static int getDistance(int x1, int x2, int y1, int y2){
        return (int)Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));
    }

    public void lernenNachbarn(FarbNeuron winner, int[] input, double nachbarschaftsZeugs){

    }


        public FarbNeuron lernen(FarbNeuron winner, int[] input, double adjustment){
            if(winner!=null) {
                int[] wCenter = winner.getZentrum();
                for (int i = 0; i < wCenter.length; i++) {
                    double c = wCenter[i];
                    int lernrate = getLernrate(input[i], c, adjustment);
                    int nC = (int)(c + (lernrate*adjustment));
                    wCenter[i] =  (nC > 255 ? 255 : (nC < 0 ? 0 : nC));
                }
                winner.setZentrum(wCenter);
            }
            return winner;
        }

        public int getLernrate(double input, double z, double adjustment){
            int lernrate = 0;
            int magic = 1;
            lernrate = (int) (magic * (input-z));
            return lernrate;
        }

    public FarbNeuron getMax(int[] input){
        int[] currentMax = null;
        FarbNeuron maxN =null;
        boolean isFirst=true;
        for(int i = 0; i<rowCount;i++){
            for(int j = 0; j<colCount;j++){
                int[] c = neurons[i][j].getZentrum();
                if(isFirst){
                    currentMax = c;
                }
//                if(isFirst || checkIfNearer(c.length,input,c,currentMax)){
                if(isFirst || checkIfNearerNew(c.length,input,c,currentMax)){
                    currentMax = c;
                    maxN = neurons[i][j];
                }
                isFirst=false;
            }
        }
        return maxN;
    }

    public boolean checkIfNearerNew(int rows, int[] input,int[] zentrum,int[] currentMax){
// distance = sqrt((pow(map[i][j].getR() - r, 2.0))
//       + (pow(map[i][j].getG() - g, 2.0))
//       + (pow(map[i][j].getB() - b, 2.0)));
        boolean isNearer=false;
        double newDis = Math.sqrt(Math.pow(zentrum[0]-input[0],2)+Math.pow(zentrum[1]-input[1],2)+Math.pow(zentrum[2]-input[2],2));
        double oldDis = Math.sqrt(Math.pow(currentMax[0]-input[0],2)+Math.pow(currentMax[1]-input[1],2)+Math.pow(currentMax[2]-input[2],2));
        if(newDis < oldDis){
            isNearer = true;
        }
        if(newDis == oldDis){
            int dice = (int)Math.random()*2;
            if(dice==1){
                isNearer=true;
            }
        }
        return isNearer;
    }

    public boolean checkIfNearer(int rows, int[] input,int[] zentrum,int[] currentMax){
        boolean isNearer=false;
        int newMax = 0;
        for(int i = 0; i<rows; i++){
                int diffIM = Math.abs(input[i]-currentMax[i]);
                int diffIZ = Math.abs(input[i]-zentrum[i]);
                if (diffIZ < diffIM) {
                    newMax++;
                } else {
                    newMax--;
                }
        }
        if(newMax>0){
            isNearer = true;
        }
        return isNearer;
    }

    public int[][] getTrainInput() {
        return trainInput;
    }

    public void setTrainInput(int[][] trainInput) {
        this.trainInput = trainInput;
    }

    public int[][] getInput(){
        int[][] input = new int[48][3];
        input[0][0] = 255;
        input[0][1] = 0;
        input[0][2] = 0;

        input[1][0] = 0;
        input[1][1] = 255;
        input[1][2] = 0;

        input[2][0] = 0;
        input[2][1] = 0;
        input[2][2] = 255;

        input[3][0] = 255;
        input[3][1] = 255;
        input[3][2] = 0;

        input[4][0] = 255;
        input[4][1] = 0;
        input[4][2] = 255;

        input[5][0] = 0;
        input[5][1] = 255;
        input[5][2] = 255;

        input[6][0] = 255;
        input[6][1] = 128;
        input[6][2] = 0;

        input[7][0] = 255;
        input[7][1] = 0;
        input[7][2] = 128;

        input[8][0] = 128;
        input[8][1] = 255;
        input[8][2] = 0;

        input[9][0] = 0;
        input[9][1] = 255;
        input[9][2] = 128;

        input[10][0] = 128;
        input[10][1] = 0;
        input[10][2] = 255;

        input[11][0] = 0;
        input[11][1] = 128;
        input[11][2] = 255;

        // Bright
        input[12][0] = 192;
        input[12][1] = 0;
        input[12][2] = 0;

        input[13][0] = 0;
        input[13][1] = 192;
        input[13][2] = 0;

        input[14][0] = 0;
        input[14][1] = 0;
        input[14][2] = 192;

        input[15][0] = 192;
        input[15][1] = 192;
        input[15][2] = 0;

        input[16][0] = 192;
        input[16][1] = 0;
        input[16][2] = 192;

        input[17][0] = 0;
        input[17][1] = 192;
        input[17][2] = 192;

        input[18][0] = 192;
        input[18][1] = 92;
        input[18][2] = 0;

        input[19][0] = 192;
        input[19][1] = 0;
        input[19][2] = 92;

        input[20][0] = 92;
        input[20][1] = 192;
        input[20][2] = 0;

        input[21][0] = 0;
        input[21][1] = 192;
        input[21][2] = 92;

        input[22][0] = 92;
        input[22][1] = 0;
        input[22][2] = 192;

        input[23][0] = 0;
        input[23][1] = 92;
        input[23][2] = 192;

        // Medium
        input[24][0] = 128;
        input[24][1] = 0;
        input[24][2] = 0;

        input[25][0] = 0;
        input[25][1] = 128;
        input[25][2] = 0;

        input[26][0] = 0;
        input[26][1] = 0;
        input[26][2] = 128;

        input[27][0] = 128;
        input[27][1] = 128;
        input[27][2] = 0;

        input[28][0] = 128;
        input[28][1] = 0;
        input[28][2] = 128;

        input[29][0] = 0;
        input[29][1] = 128;
        input[29][2] = 128;

        input[30][0] = 128;
        input[30][1] = 64;
        input[30][2] = 0;

        input[31][0] = 128;
        input[31][1] = 0;
        input[31][2] = 64;

        input[32][0] = 64;
        input[32][1] = 128;
        input[32][2] = 0;

        input[33][0] = 0;
        input[33][1] = 128;
        input[33][2] = 64;

        input[34][0] = 64;
        input[34][1] = 0;
        input[34][2] = 128;

        input[35][0] = 0;
        input[35][1] = 64;
        input[35][2] = 128;

        // Dark
        input[36][0] = 64;
        input[36][1] = 0;
        input[36][2] = 0;

        input[37][0] = 0;
        input[37][1] = 64;
        input[37][2] = 0;

        input[38][0] = 0;
        input[38][1] = 0;
        input[38][2] = 64;

        input[39][0] = 64;
        input[39][1] = 64;
        input[39][2] = 0;

        input[40][0] = 64;
        input[40][1] = 0;
        input[40][2] = 64;

        input[41][0] = 0;
        input[41][1] = 64;
        input[41][2] = 64;

        input[42][0] = 64;
        input[42][1] = 32;
        input[42][2] = 0;

        input[43][0] = 64;
        input[43][1] = 0;
        input[43][2] = 32;

        input[44][0] = 32;
        input[44][1] = 64;
        input[44][2] = 0;

        input[45][0] = 0;
        input[45][1] = 64;
        input[45][2] = 32;

        input[46][0] = 32;
        input[46][1] = 0;
        input[46][2] = 64;

        input[47][0] = 0;
        input[47][1] = 32;
        input[47][2] = 64;

        return input;
    }
}
