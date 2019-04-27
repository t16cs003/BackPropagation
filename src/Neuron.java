import java.util.ArrayList;
import java.util.List;

public class Neuron{
    public static enum FunctionType{
        Sigmoid,
        ReLu,
    };
    /** 属するLayer */
    protected Layer layer;
    /** 層内の何番目か */
    protected int index;
    /** 前層のニューロンとの結合のリスト */
    protected List<SynapticConnection> presynapticConnections;
    /** 後層のニューロンとの結合のリスト */
    protected List<SynapticConnection> postsynapticConnections;
    /** 活性化関数 */
    private ActivationFunction function;
    /** 入力値 */
    protected double input;
    /** 出力値 */
    protected double output;
    /** 修正量に比例する変数 δ^m */
    protected double delta_k;

    /**
     * ニューロンの初期化を行う.シグモイド関数のゲインは Sigmoid.EPSILON を使う.
     * 
     * @param layer 属する層
     * @param index 層内の何番目か
     * @param fType 活性化関数の種類. FunctionType.Sigmoid, ReLu
     */
    public Neuron(Layer layer,int index,FunctionType fType){
        this(Sigmoid.EPSILON,layer,index,fType);
    }

    /**
     * ニューロンの初期化を行う.
     * 
     * @param epsilon シグモイド関数のゲイン
     * @param layer 属する層
     * @param index 層内の何番目か
     * @param fType 活性化関数の種類 FunctionType.Sigmoid, ReLu
     */
    public Neuron(double epsilon,Layer layer,int index,FunctionType fType){
        this.layer = layer;
        this.index = index;
        if(fType == FunctionType.Sigmoid){
            function = new Sigmoid(epsilon);
        }else if(fType == FunctionType.ReLu){
            function = new ReLu();
        }
        presynapticConnections = new ArrayList<>();
        postsynapticConnections = new ArrayList<>();
    }
    
    /**
     * @return output 出力値
     */
    public double getOutput() {
        return output;
    }

    /**
     * @return the input
     */
    public double getInput() {
        return input;
    }

    /**
     * @return the function
     */
    public ActivationFunction getFunction() {
        return function;
    }

    /**
     * @return the delta_m
     */
    public double getDelta_k() {
        return delta_k;
    }
    /**
     * addInput で加算された入力値をもとに出力値を計算する.
     * 入力層のこの関数を呼び出すことで,出力層までの出力値を順に計算していきます.
     * 入力層でこの関数を呼び出す前に,関数 addInput(double input) を実行して入力データを流してください.
     * 
     * @param debug 各ニューロンの入出力値を表示するとき true.
     * @return output 出力値
     */
    public double output(boolean debug){
        // 出力値を 0 クリア
        output = 0;
        if(layer.getIndex() == 0){
            // 入力層の場合,入力値をそのまま出力する.
            output = input;
        }else{
            // それ以外の層では,活性化関数 function に応じて入力値から出力値を計算する.
            output = function.output(input);
        }
        for(SynapticConnection postsynapticConnection:postsynapticConnections){
            // ニューロンから伸びているシナプス結合に出力を流す.
            postsynapticConnection.output(output);
        }
        
        if(debug){
            /************************
            * ネットワークの入出力描画  *
            ************************/
            String tab = "   ";
            String indent = "   ";
            for(int i = 0; i < layer.getIndex(); i++){
                indent += tab;
            }
            System.out.println(indent+"#Layer "+layer.getIndex()+" Neuron "+index+":");
            System.out.println(indent+tab+"input "+input);
            System.out.println(indent+tab+"output "+output);
            /***********************/
        }
        return output;
    }

    public void calc_delta(double[] trainingOutputs){
        if(presynapticConnections.size() == 0){
            return;
        }
        double differentialValue = function.getDifferentialValue(input);
        if(postsynapticConnections.size() == 0){
            double outputDifference = trainingOutputs[index]-output;
            delta_k = outputDifference * differentialValue;
        }else{
            double total = 0;
            for(SynapticConnection postsynapticConnection: postsynapticConnections){
                Neuron postsynapticNeuron = postsynapticConnection.getPostsyanapticNeuron();
                double delta_k1 = postsynapticNeuron.getDelta_k();
                double weight = postsynapticConnection.getWeight();
                total += delta_k1*weight; 
            }
            delta_k = differentialValue*total;
        }
        for(SynapticConnection presynapticConnection:presynapticConnections){
            presynapticConnection.calc_delta();
        }
    }

    public void fit(){
        for(SynapticConnection presynapticConnection:presynapticConnections){
           presynapticConnection.fit(); 
        }    
    }

    /**
     * 与えられたシナプス結合 presynapticConnection の
     * シナプス後神経細胞がインスタンス自身だった場合,
     * presynapticConnections に追加する.
     * 
     * @param presynapticConnection シナプス前結合
     */
    public void addPresynapticConnection(SynapticConnection presynapticConnection){
        if(presynapticConnection.getPostsyanapticNeuron() == this){
            presynapticConnections.add(presynapticConnection);
        }
    }

    /**
     * 与えられたシナプス結合 postsynapticConnection の
     * シナプス前神経細胞がインスタンス自身だった場合,
     * postsynapticConnections に追加する
     * 
     * @param postsynapticConnection シナプス後結合
     */
    public void addPostsynapticConnection(SynapticConnection postsynapticConnection){
        if(postsynapticConnection.getPresynapticNeuron() == this){
            postsynapticConnections.add(postsynapticConnection);
        }
    }
    
    /**
     * 出力をする前に前回の入力値を 0 クリアーする.
     */
    public void resetInput(){
        input = 0;
    }

    /**
     * 与えられた入力値をニューロンの入力値に足す.
     * 
     * @param input
     */
    public void addInput(double input){
        this.input += input;
    }

    @Override
    public String toString() {
        String separator = System.getProperty("line.separator");
        StringBuilder s = new StringBuilder(separator+"    NumOfPresynapticConnection:"+presynapticConnections.size()+separator);
        for(int i = 0; i < presynapticConnections.size();i++){
            s.append("      ¥"+i+":"+presynapticConnections.get(i).toString()+separator);
        }
        s.append(separator+"    NumOfPostsynapticConnection:"+postsynapticConnections.size()+separator);
        for(int i = 0; i < postsynapticConnections.size();i++){
            s.append("      ¥"+i+":"+postsynapticConnections.get(i).toString()+separator);
        }
        return s.toString();
    }
}