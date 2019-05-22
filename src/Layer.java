import java.util.ArrayList;
import java.util.List;

public class Layer{
    /** 属するNN */
    private NeuralNetwork neuralNetwork;
    /** NNの何層目か */
    private int index;
    /** バイアスを含めたニューロン数 */
    private int numOfNeurons;
    /** Neuron インスタンスのリスト */
    private List<Neuron> neurons;
    /** 1つ前の層 */
    private Layer anteriorLayer;
    /**  1つ後ろの層 */
    private Layer posteriorLayer;
    
    /**
     * 変数の初期化.
     * 層内のニューロンの初期化.
     * anteriorLayer の posteriorLayer に自身を代入.
     * @param index 
     * @param numOfNeurons
     * @param anteriorLayer
     */
    public Layer(NeuralNetwork neuralNetwork,int index,int numOfNeurons,Layer anteriorLayer,Neuron.FunctionType fType){
        this.neuralNetwork = neuralNetwork;
        this.index = index;
        this.numOfNeurons = numOfNeurons;
        this.neurons = new ArrayList<>();
        this.anteriorLayer = anteriorLayer;
        initializeNeurons(fType);
        if(index != 0){
            this.anteriorLayer.setPosteriorLayer(this);
        }
    }

    /**
     * この層が全体の中で何層目かを返す.
     * @return index 何層目か
     */
    public int getIndex() {
        return index;
    }

    /**
     * この層の持つバイアスを含めたニューロンの数を返す.
     * @return numOfNeurons ニューロン数
     */
    public int getNumOfNeurons() {
        return numOfNeurons;
    }

    /**
     * この層の持つ Neuron インスタンスが格納されたリストを返す.
     * @return neurons ニューロンのリスト
     */
    public List<Neuron> getNeurons() {
        return neurons;
    }
    
    /**
     * @return anteriorLayer 1つ前の層
     */
    public Layer getAnteriorLayer() {
        return anteriorLayer;
    }

    /**
     * @return posteriorLayer 1つ後ろの層
     */
    public Layer getPosteriorLayer() {
        return posteriorLayer;
    }

    /**
     * @param posteriorLayer 1つ後ろの層
     */
    private void setPosteriorLayer(Layer posteriorLayer) {
        this.posteriorLayer = posteriorLayer;
    }

    /**
     * 層の持つニューロンの初期化.
     * neurons の初期化と numOfNeurons 個のニューロンの追加.
     */
    private void initializeNeurons(Neuron.FunctionType fType){
        neurons = new ArrayList<>();
        Neuron neuron;
        // numOfNeurons 番目までニューロンの初期化を行う.
        for(int i = 0; i < numOfNeurons; i++){
            neuron = new Neuron(this,i,fType);
            if(anteriorLayer != null){
                for(Neuron presynapticNeuron:anteriorLayer.getNeurons()){
                    new SynapticConnection(presynapticNeuron, neuron);
                }
            }
            neurons.add(neuron);
        }
        /*
        * 入力層,中間層において
        * numOfNeurons + 1 番目のニューロンはバイアスとして追加.
        */
        if(index != neuralNetwork.getNumOfLayers()-1){
            neuron = new Bias(this,numOfNeurons);
            neurons.add(neuron);
        }
    }

    /**
     * 層の持つニューロンをすべて出力させていく.
     * 引数は入力層のみ使用.
     * 
     * @param inputs 入力データ
     * @param debug 各ニューロンの入出力値を表示するとき true.
     */
    public void output(LearningData data,boolean debug){
        if(index == 0){
            // 入力層のとき
            for(int i = 0;i < neurons.size()-1; i++){
                // 各ニューロンに対応した入力値を加算する.
                neurons.get(i).addInput(data,data.getInputs()[i]);
            }
        }
        /* 
         * 他の層のニューロンは前の層の output(data,debug) によって入力値を
         * すでに加算されているので省く.
         */

        for(int i = 0; i < neurons.size(); i++){
            // 層の持つすべてのニューロンから出力する.
            neurons.get(i).output(data,debug);
        }
    }

    public void calcDelta(double[] trainingOutputs,LearningData data){
        for(Neuron neuron : neurons){
            neuron.calcDelta(trainingOutputs,data);
        }
    }
    
    public void fit(){
        for(Neuron neuron:neurons){
            neuron.fit();
        }
    }

    /**
     * 層の持つニューロンの入力値をすべて 0 クリアーする.
     */
    public void resetInput(LearningData data){
        for(Neuron neuron:neurons){
            neuron.resetInput(data);
        }
    }

    @Override
    public String toString() {
        String separator = System.getProperty("line.separator");
        StringBuilder s = new StringBuilder("******************"+separator+"Layer: "+index+separator+"******************"+separator);
        s.append("NumOfNeurons:"+numOfNeurons+separator);
        for(int i = 0; i < neurons.size(); i++){
            Neuron neuron = neurons.get(i);
            if(neuron instanceof Bias){
                s.append("  # Bias"+separator);
            }else{
               s.append("  # Neuron "+i+separator);
            }
            s.append(neuron.toString()+separator);
        }
        return s.toString();
    }
}