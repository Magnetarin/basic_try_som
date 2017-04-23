package som;

import org.apache.commons.codec.binary.Hex;
import utils.FileUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Maxima on 19.03.2017.
 */
public class SelfOrganisingMap {
    Neuron[][] neurons;
    int rowCount=0;
    int colCount=0;
    int rowMax=0;
    int colMax=0;
    int baisValue = 1;
    double[][] trainInput;
    double[][] trainOuput;
    double[] lineNumber;
    int[][] zugehoerigkeit;

//    Ziffer 0 checked 5923 times and 0 have been correct.
//    Ziffer 1 checked 6742 times and 6725 have been correct.
//    Ziffer 2 checked 5958 times and 4343 have been correct.
//    Ziffer 3 checked 6131 times and 3747 have been correct.
//    Ziffer 4 checked 5842 times and 626 have been correct.
//    Ziffer 5 checked 5421 times and 1348 have been correct.
//    Ziffer 6 checked 5918 times and 3175 have been correct.
//    Ziffer 7 checked 6265 times and 49 have been correct.
//    Ziffer 8 checked 5851 times and 1 have been correct.
//    Ziffer 9 checked 5949 times and 0 have been correct.

    public static void main(String[] args){
        //Funkt nicht!!!!!
        //Problem in getMax vermutlich
        int winnerIDBeforeTraining = 0;
        FileUtils fu = new FileUtils();
        SelfOrganisingMap som = new SelfOrganisingMap();
        som.init(10,10,28,28);
//        som.setCenterRandomExample(som.trainInput,som.trainOuput,5);
//        som.setCenterRandom();
//        som.setCenter();
//        som.trainMapWithSample(0.5);
        som.setCenterRandomExamples1(som.trainInput, som.trainOuput);
        fu.writeText(som.printAllNeurons(),"C:\\Users\\Maxima\\Desktop\\som_ntStart.out");
        double[][] testInput = som.changeArray(som.trainInput[11]);
        Neuron winner = som.getMax(testInput);
//        Neuron winner = som.neurons[0][0];
        System.out.println("Output: "+som.numberTheOutputRepresents(som.trainOuput[11]));
        System.out.println("Winner ID: "+winner.getId());

        System.out.println("Output: "+som.numberTheOutputRepresents(som.trainOuput[10]));
        System.out.println("Winner ID: "+som.getMax(som.changeArray(som.trainInput[10])).getId());
//        System.out.println(winner.toString());
        String s = "";
//        do{
//            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//            System.out.println("Enter String");
//            try {
//                s = br.readLine();
//                if(s.matches("[0-9]*")) {
//                    int num = Integer.parseInt(s);
//                    int output = som.numberTheOutputRepresents(som.trainOuput[num]);
//                    int winnerID = som.getMax(som.changeArray(som.trainInput[num])).getId();
//                    System.out.println("Output: " + output);
//                    System.out.println("Winner ID: " + winnerID);
//                    System.out.println("Is ok? "+(output+1 == winnerID));
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }while(!s.equalsIgnoreCase("end"));
        int countTrue = 0;
        int countFalse = 0;
        for(int i = 0; i<som.trainInput.length;i++){
            int output = som.numberTheOutputRepresents(som.trainOuput[i]);
            int winnerID = som.getMax(som.changeArray(som.trainInput[i])).getId();
            if(output == 0 || output == 1) {
                if (output == winnerID) {
                    countTrue++;
                } else {
                    if(output == 1 && (winnerID==som.neurons.length-1 || winnerID==som.neurons.length-2 || winnerID==som.neurons.length-4 || winnerID==som.neurons.length-6
                            || winnerID==som.neurons.length-8 || winnerID==som.neurons.length-10 || winnerID==som.neurons.length-12 || winnerID==som.neurons.length-14)){
                        countTrue++;
                    } else if(output==0 && (winnerID==som.neurons.length-3 || winnerID==som.neurons.length-5 || winnerID==som.neurons.length-7 || winnerID==som.neurons.length-9
                            || winnerID==som.neurons.length-11 || winnerID==som.neurons.length-13)){
                        countTrue++;
                    }else{
                        countFalse++;
                    }
                }
            }

        }
//        som.neuronenZuGehörigkeit(som.trainInput,som.trainOuput);
//        System.out.println("true count:"+countTrue);
//        System.out.println("false count:"+countFalse);
        int time = 10;
        som.trainMapWithSample(0.5,time);
        som.neuronenZuGehoerigkeit(som.trainInput,som.trainOuput);
        fu.writeText(som.printAllNeurons(),"C:\\Users\\Maxima\\Desktop\\som_nt1.out");

//        som.printTrueFalseCount(0.5);
        som.trainMapWithSample(0.4,time);
        som.neuronenZuGehoerigkeit(som.trainInput,som.trainOuput);
        fu.writeText(som.printAllNeurons(),"C:\\Users\\Maxima\\Desktop\\som_nt2.out");

        /*
//        som.printTrueFalseCount(0.4);
        som.trainMapWithSample(0.3,time);
        som.neuronenZuGehoerigkeit(som.trainInput,som.trainOuput);
        fu.writeText(som.printAllNeurons(),"C:\\Users\\Maxima\\Desktop\\som_nt3.out");

//        som.printTrueFalseCount(0.3);
        som.trainMapWithSample(0.2,time);
        som.neuronenZuGehoerigkeit(som.trainInput,som.trainOuput);
        fu.writeText(som.printAllNeurons(),"C:\\Users\\Maxima\\Desktop\\som_nt4.out");

//        som.printTrueFalseCount(0.2);
        som.trainMapWithSample(0.1,time);
        som.neuronenZuGehoerigkeit(som.trainInput,som.trainOuput);
        fu.writeText(som.printAllNeurons(),"C:\\Users\\Maxima\\Desktop\\som_nt5.out");
*/
//        som.printTrueFalseCount(0.1);

//        System.out.println("trueIndex for 0 is: "+som.findTrueNum(0));
//        System.out.println("falseIndex for 0 is: "+som.nextFalse(0));
//        System.out.println("trueIndex for 1 is: "+som.findTrueNum(1));
//        System.out.println("falseIndex for 1 is: "+som.nextFalse(1));

        fu = new FileUtils();
        fu.writeText(som.printAllNeurons(),"C:\\Users\\Maxima\\Desktop\\som_new.out");

        som.neuronenZuGehoerigkeit(som.trainInput,som.trainOuput);

        do{
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Enter String");
            try {
                s = br.readLine();
                if(s.matches("[0-9]*")) {
                    int num = Integer.parseInt(s);
                    int output = som.numberTheOutputRepresents(som.trainOuput[num]);
                    int winnerID = som.getMax(som.changeArray(som.trainInput[num])).getId();
                    System.out.println("should Be: " + output);
                    System.out.println("Winner ID: " + winnerID);
                    System.out.println("Is ok? "+(output == winnerID));
                    if(!(output == winnerID)){
                        if(!(output == 1 && (winnerID==som.neurons.length-1||winnerID==som.neurons.length-2))) {
                            System.out.println(som.printInputHex(som.changeArray(som.trainInput[num])));
//                            System.out.println("linenum: " + som.lineNumber[num]);
                            System.out.println(som.getMax(som.changeArray(som.trainInput[num])).printZentrumHex());
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }while(!s.equalsIgnoreCase("end"));

        //Zentren mit random ziffern zu setzen
        //zieht dann hoffentlich nach einiger zeit die anderen zu sich
        //siehe Farb Kohonenmap

        //Center mit 5 Random Ziffernbilder setzten
        //Center mit Features setzen die einer Zahl zu gehörig sind -> sample features heraus lesen??? ->
        //  und diese dann in die Map füttern und schauen wo welche Zahl landet
        //sample 0 und 5000


        // initate Center with all next false
        // remove empty lines and cols

        // Netz trainiern, dann händisch die nueronen den eingängen zuordenen und dann prozent satz ermitteln
    }

    public void neuronenZuGehoerigkeit(double[][] input, double[][] output){
    	zugehoerigkeit = new int[colCount*rowCount][10];
        for (int i = 0; i < input.length; i++) {
            Neuron g = getMax(changeArray(input[i]));
            double pos = numberTheOutputRepresents(output[i]);
            zugehoerigkeit[g.getId()][(int)pos]++;
        }

        for(int i = 0; i<zugehoerigkeit.length; i++){
            System.out.println("Neuron "+i);
            for(int j = 0; j<zugehoerigkeit[i].length;j++){
                int anz = zugehoerigkeit[i][j];
                if(anz > 0) {
                    System.out.println("\tDie Ziffer " + j + " wählt " + anz + " mal dieses Neuron.");
                }
            }
        }

    }

    public int nextFalse(int num){
        int falseIndex = -1;
        for(int i = 0; i<trainInput.length;i++){
            int output = numberTheOutputRepresents(trainOuput[i]);
            int winnerID = getMax(changeArray(trainInput[i])).getId();
            if(output != winnerID && output == num){
                falseIndex = i;
                /*if(num == 1) {
                    if(winnerID!=neurons.length-1 && winnerID!=neurons.length-2 && winnerID!=neurons.length-4 && winnerID!=neurons.length-6
                            && winnerID!=neurons.length-8 && winnerID!=neurons.length-10&& winnerID!=neurons.length-12 && winnerID!=neurons.length-14){
                        falseIndex = i;
                        break;
                    }
                } else if(num == 0){
                    if(winnerID!=neurons.length-3 && winnerID!=neurons.length-5 && winnerID!=neurons.length-7
                            && winnerID!=neurons.length-9 && winnerID!=neurons.length-11 && winnerID!=neurons.length-13){
                        falseIndex = i;
                        break;
                    }
                } else {
                    falseIndex = i;
                    break;
                }*/
            }

        }
        return falseIndex;

    }

    public String printInput(double[][] input){
        DecimalFormat decimalFormat = new DecimalFormat("##.##");
        String erg ="";
        for(int i = 0; i< input.length;i++){
            for(int j = 0; j< input[i].length;j++){
                erg+="\t["+decimalFormat.format(input[i][j])+"] ";
            }
            erg+="\n";
        }
        return erg;
    }

    public String printInputHex(double[][] input){
        String erg ="";
        for(int i = 0; i< input.length;i++){
            for(int j = 0; j< input[i].length;j++){
            	String hexStr = ((int)(input[i][j]*255) == 0 ? "  " : Integer.toHexString((int)(input[i][j]*255)).toUpperCase());
            	hexStr = (hexStr.length()==1 ? "0"+hexStr : hexStr); 
                erg+=" "+hexStr;
            }
            erg+="\n";
        }
        return erg;
    }

    public int findTrueNum(int num){
        int trueIndex = -1;
        boolean isFirstTime = true;
        for(int i = 0; i<trainInput.length;i++){
            int output = numberTheOutputRepresents(trainOuput[i]);
            int winnerID = getMax(changeArray(trainInput[i])).getId();
            if(output == winnerID && output == num){
                if(!isFirstTime) {
                    trueIndex = i;
                    break;
                }
                isFirstTime=false;
            }

        }
        return trueIndex;
    }

    public void printTrueFalseCount(double lernRate){
        int[] counts = countTrueFalse();
        int countTrue = counts[0];
        int countFalse = counts[1];

        System.out.println("lernRate: "+lernRate);
        System.out.println("true count: "+countTrue);
        System.out.println("false count: "+countFalse);
    }

    public int[] countTrueFalse(){
        int[] count = new int[2];
        int countTrue = 0;
        int countFalse = 0;
        for(int i = 0; i<trainInput.length;i++){
            int output = numberTheOutputRepresents(trainOuput[i]);
            int winnerID = getMax(changeArray(trainInput[i])).getId();
            if(output == winnerID){
                countTrue++;
            }else{
                countFalse++;
            }

        }
        count[0]=countTrue;
        count[1]=countFalse;
        return count;
    }

    public void setCenterRandom(){
        for(int i = 0; i < neurons.length; i++){
            for(int j = 0; j < neurons[i].length; j++){
                neurons[i][j].setZentrumRandom(28,28);
                //neurons[i][j].setZentrumRandom(3,1);
            }
        }
    }
    
    public void setCenterRandomExamples1(double[][] input, double[][] output){
    	for(int i=0; i<neurons.length; i++){
    		for(int j=0; j<neurons[i].length; j++){
    			Neuron n = new Neuron();
    			int x = (int)(Math.random()*input.length+1);
    			double[][] zentrum = changeArray(input[x]);
    			n.setZentrum(zentrum);
    			n.setId(i+(j*rowCount));
    			neurons[i][j] = n;
    		}
    	}
    }

    public void setCenterRandomExample(double[][] input, double[][] output,int anz){
        //besser wenn ich die Example zu nachbern mache
        //jetzt habe ich sie reihen weiße
        int need = neurons.length;
        int[] x = new int[anz];
        int[] countNum = new int[10];
        int[] neuronZenterSet = new int[neurons.length];
        for(int i = 0; i < x.length; i++){
            x[i] = (int)(Math.random()*5000+1);
        }

        for(int i = 0; i < output.length; i++){
            switch(numberTheOutputRepresents(output[i])){
                case 0:
                    countNum[0]++;
                    if(containsNumber(x,countNum[0])){
                        neurons[0][0+neuronZenterSet[0]].setZentrum(changeArray(input[i]));
                        neuronZenterSet[0]++;
                    }
                    break;
                case 1:
                    countNum[1]++;
                    if(containsNumber(x,countNum[1])){
                        neurons[1][neuronZenterSet[1]].setZentrum(changeArray(input[i]));
                        neuronZenterSet[1]++;
                    }
                    break;
                case 2:
                    countNum[2]++;
                    if(containsNumber(x,countNum[2])){
                        neurons[2][0+neuronZenterSet[2]].setZentrum(changeArray(input[i]));
                        neuronZenterSet[2]++;
                    }
                    break;
                case 3:
                    countNum[3]++;
                    if(containsNumber(x,countNum[3])){
                        neurons[3][0+neuronZenterSet[3]].setZentrum(changeArray(input[i]));
                        neuronZenterSet[3]++;
                    }
                    break;
                case 4:
                    countNum[4]++;
                    if(containsNumber(x,countNum[4])){
                        neurons[4][0+neuronZenterSet[4]].setZentrum(changeArray(input[i]));
                        neuronZenterSet[4]++;
                    }
                    break;
                case 5:
                    countNum[5]++;
                    if(containsNumber(x,countNum[5])){
                        neurons[5][0+neuronZenterSet[5]].setZentrum(changeArray(input[i]));
                        neuronZenterSet[5]++;
                    }
                    break;
                case 6:
                    countNum[6]++;
                    if(containsNumber(x,countNum[6])){
                        neurons[6][0+neuronZenterSet[6]].setZentrum(changeArray(input[i]));
                        neuronZenterSet[6]++;
                    }
                    break;
                case 7:
                    countNum[7]++;
                    if(containsNumber(x,countNum[7])){
                        neurons[7][0+neuronZenterSet[7]].setZentrum(changeArray(input[i]));
                        neuronZenterSet[7]++;
                    }
                    break;
                case 8:
                    countNum[8]++;
                    if(containsNumber(x,countNum[8])){
                        neurons[8][0+neuronZenterSet[8]].setZentrum(changeArray(input[i]));
                        neuronZenterSet[8]++;
                    }
                    break;
                case 9:
                    countNum[9]++;
                    if(containsNumber(x,countNum[9])){
                        neurons[9][0+neuronZenterSet[9]].setZentrum(changeArray(input[i]));
                        neuronZenterSet[9]++;
                    }
                    break;
            }
        }
    }

    public boolean containsNumber(int[] x, int num){
        boolean erg = false;
        for(int i = 0; i < x.length; i++){
            if(num == x[i]){
                erg = true;
            }
        }
        return erg;
    }

    public int[] getIndexes(int anz){
        int[] indexes = new int[anz];

        return indexes;
    }

    public void setCenter(){
        int[] counter = new int[10];
        for(int i = 0; i < trainInput.length; i++){
            int outputLabel = numberTheOutputRepresents(trainOuput[i]);
            if(counter[outputLabel] == 0){
                neurons[outputLabel][0].setZentrum(changeArray(trainInput[i]));
                counter[outputLabel] = 1;
                System.out.println("should be: "+outputLabel);
                System.out.println(neurons[outputLabel][0].printZentrum());
            }
            if(i == 5000){
                neurons[neurons.length-1][0].setZentrum(changeArray(trainInput[i]));
            }
            if(i == 10){
                neurons[neurons.length-2][0].setZentrum(changeArray(trainInput[i]));
            }
            if(i == 16){
                neurons[neurons.length-3][0].setZentrum(changeArray(trainInput[i]));
            }
            if(i == 28){
                neurons[neurons.length-4][0].setZentrum(changeArray(trainInput[i]));
            }
            if(i == 53){
                neurons[neurons.length-5][0].setZentrum(changeArray(trainInput[i]));
            }
            if(i == 67){
                neurons[neurons.length-6][0].setZentrum(changeArray(trainInput[i]));
            }
            if(i == 59){
                neurons[neurons.length-7][0].setZentrum(changeArray(trainInput[i]));
            }
            if(i == 138){
                neurons[neurons.length-8][0].setZentrum(changeArray(trainInput[i]));
            }
            if(i == 65){
                neurons[neurons.length-9][0].setZentrum(changeArray(trainInput[i]));
            }
            if(i == 6626){
                neurons[neurons.length-10][0].setZentrum(changeArray(trainInput[i]));
            }
            if(i == 122){
                neurons[neurons.length-11][0].setZentrum(changeArray(trainInput[i]));
            }
            if(i == 198){
                neurons[neurons.length-12][0].setZentrum(changeArray(trainInput[i]));
            }
            if(i == 226){
                neurons[neurons.length-13][0].setZentrum(changeArray(trainInput[i]));
            }
            if(i == 214){
                neurons[neurons.length-14][0].setZentrum(changeArray(trainInput[i]));
            }
        }
    }

    public void trainMapWithSample(double zeitkoeffizient){
        trainMapWithSample(zeitkoeffizient,1);
    }

    public void trainMapWithSample(double zeitkoeffizient,int time){
        System.out.println("Training Map with: "+zeitkoeffizient+" and "+time);
        for(int j = 0; j<time;j++) {
            for (int i = 0; i < trainInput.length; i++) {
                double[][] input = changeArray(trainInput[i]);
                Neuron winner = getMax(input);
                lernen(zeitkoeffizient, winner, input,1);
                lernen(zeitkoeffizient, winner.getnN(), input,0.5);
                lernen(zeitkoeffizient, winner.getnO(), input,0.5);
                lernen(zeitkoeffizient, winner.getnS(), input,0.5);
                lernen(zeitkoeffizient, winner.getnW(), input,0.5);
            }
        }
//        nE.input_train = al.get(0);
//        nE.output_train = al.get(1);
//        al.clear();
//        al = nE.getSample("C:\\Users\\Maxima\\FH\\bac\\basic_try\\src\\main\\resources\\t10k-images-idx3-ubyte",
//                "C:\\Users\\Maxima\\FH\\bac\\basic_try\\src\\main\\resources\\t10k-labels-idx1-ubyte",
//                nE.input_test,
//                nE.output_test);
//        nE.input_test = al.get(0);
//        nE.output_test = al.get(1);
    }

    public double[][] changeArray(double[] toChange){
        double[][] erg = new double[28][28];
        for(int i = 0; i<erg.length; i++){
            for (int j = 0; j<erg[i].length; j++){
                erg[i][j]=toChange[i*erg.length+j];
            }
        }
        return erg;
    }

    public void init(int rows,int col,int cRows, int cCols){
        rowCount = rows;
        colCount = col;
        int mult = (rows>col? col :rows);
        neurons = new Neuron[rows][col];
        for(int i = 0; i<rows;i++){
            for(int j = 0; j<col;j++){
                neurons[i][j] = new Neuron(i+j*mult,i,j);
                neurons[i][j].setZentrumRandom(cRows,cCols);
//                neurons[i][j].setZentrumZero(cRows,cCols);
            }
        }
        zugehoerigkeit = new int[rows*col][10];
        registerNeighbor();
        getSample("..\\basic_try\\src\\main\\resources\\train-images-idx3-ubyte",
                "..\\basic_try\\src\\main\\resources\\train-labels-idx1-ubyte");
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


    public Neuron lernen(double zeitkoeffizient, Neuron winner, double[][] input, double nachbarschaftsZeugs){
        if(winner!=null) {
            double[][] wCenter = winner.getZentrum();
            for (int i = 0; i < wCenter.length; i++) {
                for (int j = 0; j < wCenter[i].length; j++) {
                    double c = wCenter[i][j];
                    double lernrate = getLernrate(zeitkoeffizient, winner.getId(), winner.getId(), input[i][j], c);
                    wCenter[i][j] = c + lernrate*nachbarschaftsZeugs;
                }
            }
            winner.setZentrum(wCenter);
        }
        return winner;
    }

    public double getLernrate(double zeitkoeffizient, int iId, int kId, double input, double z){
        double lernrate = 0;
        double magic = 0;
        if(iId==kId){
            magic = 1;
        } else {
            if((iId-kId) == 1 || (iId-kId) == -1){
                magic = 1;
            }
        }
        lernrate = zeitkoeffizient * magic * (input-z);
        return lernrate;
    }

    public SelfOrganisingMap(){}

    public Neuron getMax(double[][] input){
        double[][] currentMax = null;
        Neuron maxN =null;
        boolean isFirst=true;
        for(int i = 0; i<rowCount;i++){
            for(int j = 0; j<colCount;j++){
                double[][] c = neurons[i][j].getZentrum();
                if(isFirst){
                    currentMax = c;
                }
                if(isFirst || checkIfNearer(28,28,input,c,currentMax)){
                    currentMax = c;
                    maxN = neurons[i][j];
                }
                isFirst=false;
            }
        }
        return maxN;
    }

    public boolean checkIfNearer(int rows, int cols, double[][] input,double[][] zentrum,double[][] currentMax){
        boolean isNearer=false;
        ArrayList<Integer> diffs = new ArrayList<Integer>();
        int newMax = 0;

        for(int i = 0; i<rows; i++){
            for(int j = 0; j<cols; j++){
                double diffIM = Math.abs(input[i][j]-currentMax[i][j]);
                double diffIZ = Math.abs(input[i][j]-zentrum[i][j]);
                if (diffIZ < diffIM) {
                    newMax++;
                } else {
                    newMax--;
                }
            }
        }
        if(newMax>0){
            isNearer = true;
        }
//        int occurrencesOld = Collections.frequency(diffs, 0);
//        int occurrencesNew = Collections.frequency(diffs, 1);
//        if(occurrencesNew > occurrencesOld){
//            isNearer=true;
//        }
        return isNearer;
    }

    private String printAllNeurons(){
        String erg = "";
        for(int i = 0; i<neurons.length;i++){
            for(int j = 0; j<neurons[i].length;j++){
                erg+=neurons[i][j].toString();
            }
        }
        return erg;
    }

    public void setNeuronCenter(int x, int y, double[][] center){
        neurons[x][y].setZentrum(center);
    }


    public void getSample(String exampleFile, String labelFile){
        File f_train = new File(exampleFile);
        File f_label = new File(labelFile);
        FileInputStream fin_train = null;
        FileInputStream fin_label = null;
        int anzbsp = 0, rows = 0, columns = 0;
        int[] labelCount = new int[10];

        try {
            fin_train = new FileInputStream(f_train);
            fin_label = new FileInputStream(f_label);

            byte[] info = new byte[4];
            fin_train.read(info,0,4);
            System.out.println("File Info: ");
            ByteBuffer wrapped = ByteBuffer.wrap(info);
            System.out.println("\tmagic Number: "+wrapped.getInt());
            fin_train.read(info,0,4);
            wrapped = ByteBuffer.wrap(info);
            anzbsp= wrapped.getInt();
            System.out.println("\tanz Bsp: "+anzbsp);
            fin_train.read(info,0,4);
            wrapped = ByteBuffer.wrap(info);
            rows= wrapped.getInt();
            System.out.println("\tnum rows: "+rows);
            fin_train.read(info,0,4);
            wrapped = ByteBuffer.wrap(info);
            columns= wrapped.getInt();
            System.out.println("\tnum columns: "+columns);

            System.out.println("Label File Info: ");
            fin_label.read(info,0,4);
            wrapped = ByteBuffer.wrap(info);
            System.out.println("\tmagic Number: "+wrapped.getInt());
            fin_label.read(info,0,4);
            wrapped = ByteBuffer.wrap(info);
            if(anzbsp != wrapped.getInt()){
                System.out.println("Error, nicht die selbe anzahl an Beispielen in Label("+wrapped.getInt()+") and Image("+anzbsp+") file!");
            }
            System.out.println("\tanz Bsp: "+anzbsp);

            byte[] data = new byte[(rows*columns)];
            byte[] label = new byte[1];
            trainInput  = new double[anzbsp][(rows*columns)];
            trainOuput = new double[anzbsp][10];
//            trainInput  = new double[5923+6742][(rows*columns)];
//            trainOuput = new double[5923+6742][10];
//            lineNumber = new double[5923+6742];
            int anz = 0;
            int countLineNum =0;
            for(int i=0; i<anzbsp;i++){
//            for(int i=0; i<(5923+6742);i++){
                //for(int i=0; i<1;i++){
                fin_train.read(data,0,(rows*columns));
                fin_label.read(label,0,1);
//                if(label[0] == 1 || label[0] == 0) {
                    trainInput[i] = toDoubleArray(data);
                    double[] labelNum = getLabelNumber(label[0]);
                    trainOuput[i] = labelNum;
                    labelCount[label[0]] = labelCount[label[0]] + 1;
                    anz++;
//                    lineNumber[i]=countLineNum;
//                  i++;
//                }
//                i--
                countLineNum++;
            }
            System.out.println("Test anz: "+anz);
            //System.out.println("Test ausgabe: "+input_train[0][153]);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fin_train != null) {
                    fin_train.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        for(int i = 0; i<labelCount.length;i++){
            System.out.println("Ziffer "+i+" checked "+labelCount[i]+" times.");
        }

        ArrayList<double[][]> al = new ArrayList<double[][]>();
        al.add(trainInput);
        al.add(trainOuput);
    }

    public double[] getLabelNumber(byte label){
        double[] labelNum = {0,0,0,0,0,0,0,0,0,0};
        switch (label){
            case 0:
                labelNum = OutputNumErk.zero.shwoNeuronNum();
                break;
            case 1:
                labelNum = OutputNumErk.one.shwoNeuronNum();
                break;
            case 2:
                labelNum = OutputNumErk.two.shwoNeuronNum();
                break;
            case 3:
                labelNum = OutputNumErk.three.shwoNeuronNum();
                break;
            case 4:
                labelNum = OutputNumErk.four.shwoNeuronNum();
                break;
            case 5:
                labelNum = OutputNumErk.five.shwoNeuronNum();
                break;
            case 6:
                labelNum = OutputNumErk.six.shwoNeuronNum();
                break;
            case 7:
                labelNum = OutputNumErk.seven.shwoNeuronNum();
                break;
            case 8:
                labelNum = OutputNumErk.eight.shwoNeuronNum();
                break;
            case 9:
                labelNum = OutputNumErk.nine.shwoNeuronNum();
                break;
        }
        return labelNum;
    }

    public static double[] toDoubleArray(byte[] byteArray){
        double[] doubles = new double[byteArray.length];
        for(int i=0;i<doubles.length;i++){
            //doubles[i] = ByteBuffer.wrap(byteArray, i*times, times).getDouble();
            char[] charAr = Hex.encodeHex(new byte[]{byteArray[i]});
            String s = new String(charAr);
            int number = Integer.parseInt(s,16);
            doubles[i] = (double)number/255;
        }
        return doubles;
    }

    private int numberTheOutputRepresents(double[] output){
        int max = 0;
        for(int i = 0; i<output.length;i++){
            if(output[i]>max){
                max=i;
            }
        }
        return max;
    }

}

/*


    public static void main(String[] args){
        int winnerIDBeforeTraining = 0;
        SelfOrganisingMap som = new SelfOrganisingMap();
        som.init(1,2,5,5);
        double[][] input = {
                {5,5,5,5,5},
                {5,5,5,5,5},
                {0,0,0,0,0},
                {0,0,0,0,0},
                {0,0,0,0,0}};
        Neuron n = som.getMax(input);
        System.out.println(som.printAllNeurons());
        System.out.println("Winner ID: "+n.getId());
        double[][] center1 = {
                {5,5,5,5,5},
                {5,5,5,5,5},
                {0,0,0,0,0},
                {0,0,0,0,0},
                {0,0,5,0,0}};
        double[][] center2 = {
                {0,0,5,0,0},
                {0,0,0,0,0},
                {0,0,0,0,0},
                {5,5,5,5,5},
                {5,5,5,5,5}};
//        som.setNeuronCenter(0,0,center1);
//        som.setNeuronCenter(0,1,center2);
        System.out.print(n.toString());
        som.lernen(0.5,n,input);
        System.out.println("Winner ID after train: "+n.getId());
        System.out.print(n.toString());
    }
 */

/*
    public static void main(String[] args){
        Neuron_v1 n1 = new Neuron_v1();
        Neuron_v1 n2 = new Neuron_v1();
        Neuron_v1 n3 = new Neuron_v1();
        Neuron_v1 n4 = new Neuron_v1();
        Neuron_v1 n5 = new Neuron_v1();
        Neuron_v1[] Neuron_v1s = {n1,n2,n3,n4,n5};

        //getMax
        int max = -1;
        double minDiff=0;
        int input = 33;
        boolean isFirst = true;
        for(int i = 0; i<neurons.length; i++){
            double z = neurons[i].getZentrum();
            double diff = ((z-input)<0? (z-input)*-1:z-input);
            if(isFirst || minDiff > diff){
                minDiff = diff;
                max = i;
            }
            isFirst=false;
            System.out.println(i+". center: "+z);
        }
        System.out.println("And the winner is "+max+" with the center "+neurons[max].getZentrum());

        //lernen
        double zeitkoeffizient = 0.5;
        int magic = 0;// i = k ? 1 :0
        int diff = 0;
        SelfOrganisingMap som = new SelfOrganisingMap();
        Neuron_v1[] ns = {neurons[max]};
        double z = neurons[max].getZentrum();
        double cDiff = ((z-input)<0? (z-input)*-1:z-input);
        int anz = 1;
        while (cDiff > 1){
            System.out.println(anz+".Step:");
            z = neurons[max].getZentrum();
            som.lernen(neurons,zeitkoeffizient,max,input);
            cDiff = ((z-input)<0? (z-input)*-1:z-input);
            System.out.println("cdiff: "+cDiff+"\n");
            anz++;
        }
        System.out.println("Winner: "+max);
        //double o = 0.5;
        //i gewinner, k anderes neuron
        //e(-((||ci-ck||)^2)/2*o^2
    }

    public Neuron_v1[] lernen(Neuron_v1[] neuronen, double zeitkoeffizient, int i, int input){
        Neuron_v1[] neurons = neuronen;
        for(int k = 0; k<neurons.length;k++){
            double z = neurons[k].getZentrum();
            double lernrate = getLernrate(zeitkoeffizient,i,k,input,z);
            neurons[k].setZentrum(z+lernrate);
            System.out.println("lernrate: "+lernrate+"\t neues Zentrum: "+neurons[k].getZentrum());
        }
        return neurons;
    }

    public double getLernrate(double zeitkoeffizient, int i, int k, int input, double z){
        double lernrate = 0;
        double magic = 0;
        if(i==k){
            magic = 1;
        } else {
            if((i-k) == 1 || (i-k) == -1){
                magic = 1;
            }
        }
        lernrate = zeitkoeffizient * magic * (input-z);
        return lernrate;
    }
 */
