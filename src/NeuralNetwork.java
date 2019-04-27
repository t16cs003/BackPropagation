import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NeuralNetwork{
    /** 学習回数上限 */
    public final static int EPHOCS = 500000;
    /** 学習率 */
    private static double ETA = 0.0001;
    /** NNの持つ層のリスト */
    private List<Layer> layers;
    /** 層数 */
    private int numOfLayers;
    /** 入力ベクトルの次元数 */
    private int dimensionOfInput;
    /** 出力ベクトルの次元数 */
    private int dimensionOfOutput;
    /**
     * 
     * @param numOfNeurons 可変長引数 各層のニューロン数を入力層から順に入力.(バイアスを除く.)
     */
    public NeuralNetwork(Neuron.FunctionType fType, int... numOfNeurons){
        System.out.println("used random seed : " + SynapticConnection.getRandomSeed());
        // NN の持つ層の数を引数の数に設定.
        this.numOfLayers = numOfNeurons.length;
        layers = new ArrayList<>(this.numOfLayers);

        this.dimensionOfInput = numOfNeurons[0];
        this.dimensionOfOutput = numOfNeurons[numOfNeurons.length-1];
        // 各 Layer インスタンスの作成.
        // 入力層のみ前の層がないため,for文の外で作成.
        layers.add(new Layer(this, 0, numOfNeurons[0], null, fType));
        // 中間層.
        for(int i = 1; i < numOfLayers-1; i++){
            /* 
             * i 番目の Layer インスタンスを作成.
             * AnteriorLayer に i-1 番目の Layer インスタンスを入れる.
             */
            layers.add(new Layer(this, i, numOfNeurons[i], layers.get(i-1), fType));
        }
        layers.add(new Layer(this, numOfLayers-1, numOfNeurons[numOfLayers-1],layers.get(numOfLayers-2), Neuron.FunctionType.Sigmoid));
    }
    /**
     * @return layers NNの持つ層のリスト
     */
    public List<Layer> getLayers() {
        return layers;
    }

    /**
     * @return numOfLayers NNの持つ層の数
     */
    public int getNumOfLayers() {
        return numOfLayers;
    }

    /**
     * @return the ephocs
     */
    public static int getEphocs() {
        return EPHOCS;
    }

    /**
     * @return 学習率
     */
    public static double getETA() {
        return ETA;
    }

    /**
     * @param eta 学習率
     */
    public static void setETA(double eta) {
        if(eta > 0 && eta <= 1){
            ETA = eta;
        }
    }
    /**
     * 入力データをもとに出力層の出力を計算する.
     * 入力データの次元は, dimensionOfInput と一致している必要がある.
     * 
     * @param inputs 入力データ
     * @param debug 各ニューロンの入出力値を表示するとき true.
     * @return outputs 入力データの次元が間違っているときは null,正しいときは出力層の出力
     */
    public double[] output(double[] inputs,boolean debug){
        if(inputs.length != dimensionOfInput){
            // 入力層のニューロン数と入力データの次元が一致しているか確認.
            System.out.println("入力データの次元が異なります.");
            return null;
        }
        
        for(Layer layer:layers){
            // 全ての層について,全ニューロンの入力値を 0 クリアーしておく.
            // これがないと前回の出力の時の入力値が加算されてしまう.
            layer.resetInput();
        }
        for(Layer layer:layers){
            // 各層のニューロンを入力層から出力させていく.
            if(layer.equals(layers.get(0))){
                // 入力層のみ入力データを流す.
                layer.output(inputs,debug);
            }else{
                layer.output(null,debug);
            }
        }
        
        // 出力層の出力を取り出す.
        double[] outputs = new double[dimensionOfOutput];
        Layer outputLayer = layers.get(layers.size()-1);
        List<Neuron> outputNeurons = outputLayer.getNeurons();
        for(int i = 0; i < outputs.length; i++){
            outputs[i] = outputNeurons.get(i).getOutput();
        }
        return outputs;
    }

    public void fit(List<LearningData> trainingDatas,List<LearningData> verificationDatas,boolean verify){
        int epoch = 0;
        double upperLimit = 0.05;
        String indent = "   ";
        if(trainingDatas.size() == 0 || trainingDatas == null) {
            System.out.println("訓練データがありません.");
        }
        if (verify && (verificationDatas.size() == 0 || verificationDatas == null)) {
            System.out.println("検証データがありません.");
        }
        double previousEpochError = 0;
        for (; epoch < EPHOCS; epoch++) {
            for (LearningData data : trainingDatas) {
                output(data.getInputs(), false);
                for (int j = layers.size() - 1; j >= 1; j--) {
                    layers.get(j).calc_delta(data.getTeacherOutputs());
                }
            }
            for (int j = 1; j < layers.size(); j++) {
                layers.get(j).fit();
            }
            double epochError = 0;
            for (LearningData data : trainingDatas) {
                epochError += data.calcSquareError(output(data.getInputs(), false));
            }
            epochError /= trainingDatas.size();
            if(verify && ((epoch+1) % 10 == 0)){
                System.out.println("epoch "+ (epoch+1) +" : ");
                System.out.println(indent+"training precision : "+verify(trainingDatas,false));
                System.out.println(indent+"verification precision : "+verify(verificationDatas,false));
                System.out.println("mean error : "+epochError);
            }
            if(epoch != 0){
                if(previousEpochError-epochError < 0.000000001){
                    break;
                }
            }
            previousEpochError = epochError;
        }
        if(epoch == EPHOCS){
            System.out.println("学習が収束しませんでした.");
        }else{
            System.out.println("学習が完了しました.");
            System.out.println("epochs : "+epoch);
        }
    }

    public double verify(List<LearningData> verificationDatas,boolean debug){
        double precision = 0;
        int numOfCorrect = 0;
        String indent1 = "  ";
        String indent2 = "     ";
        String indent3 = "          ";
        System.out.println(indent1+"Start verification!");
        
        for(int i = 0; i < verificationDatas.size(); i++){
            LearningData learningData = verificationDatas.get(i);
            if(debug){
                System.out.println(indent2+"verificationData "+i+" :");
                System.out.println(indent3+"inputs : "+Arrays.toString(learningData.getInputs()));
                System.out.println(indent3+"teacherOutputs : "+Arrays.toString(learningData.getTeacherOutputs()));
            }
            double[] outputs = output(learningData.getInputs(), false);
            if(debug){
                System.out.println(indent3+"outputs : "+Arrays.toString(outputs));
            }
            int correctClass = 0;
            int outputClass = 0;
            double max = -1;
            for(int j= 0; j < outputs.length; j++){
                if(max < outputs[j]){
                    max = outputs[j];
                }
                if(learningData.getTeacherOutputs()[j] == 0.9){
                    correctClass = j;
                }
            }
            for(int j = 0; j < outputs.length; j++){
                if(max == outputs[j]){
                    outputClass = j;
                 }
            }
            if(outputClass == correctClass){
                numOfCorrect += 1;
                if(debug){
                    System.out.println(indent3+"Correct!!");
                    System.out.println();
                }
            }else if(debug){
                System.out.println(indent3+"Incorrect..."); 
                System.out.println();
            }
        }
        precision = (double)numOfCorrect/verificationDatas.size();
        return precision;
    }
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        String separator = System.getProperty("line.separator");
        for(Layer layer:layers){
            s.append(layer.toString());
            s.append(separator);
        }
        return s.toString();
    }
    
}