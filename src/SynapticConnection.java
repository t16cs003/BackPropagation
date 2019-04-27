import java.util.HashMap;
import java.util.Map;
import java.util.Random;

class SynapticConnection{
    
    private static long randomSeed = System.currentTimeMillis();
    
    private static final Random rand = new Random(randomSeed);
    
    /** シナプス前神経細胞 */
    private Neuron presynapticNeuron;
    /** シナプス後神経細胞 */
    private Neuron postsyanapticNeuron;
    /** シナプス結合荷重 */
    private double weight;
    /** シナプス結合荷重の修正量 */
    private Map<LearningData, Double> deltaWeights;

    private Double previousDeltaWeights;

    private double epochDeltaWeight;
    /**
     * @param presynapticNeuron シナプス前神経細胞
     * @param postsynapticNeuron シナプス後神経細胞
     */
    public SynapticConnection(Neuron presynapticNeuron,Neuron postsynapticNeuron){
        this.presynapticNeuron = presynapticNeuron;
        this.postsyanapticNeuron = postsynapticNeuron;
        weight = (rand.nextInt(2)==0)?rand.nextDouble()*0.01:-rand.nextDouble()*0.01;
        deltaWeights = new HashMap<>();
        previousDeltaWeights = null;
        epochDeltaWeight = 0;
        this.presynapticNeuron.addPostsynapticConnection(this);
        this.postsyanapticNeuron.addPresynapticConnection(this);
    }

    /**
     * @return presynapticNeuron シナプス前神経細胞
     */
    public Neuron getPresynapticNeuron() {
        return presynapticNeuron;
    }

    /**
     * @return postsyanapticNeuron シナプス後神経細胞
     */
    public Neuron getPostsyanapticNeuron() {
        return postsyanapticNeuron;
    }

    /**
     * @return the weight
     */
    public double getWeight() {
        return weight;
    }

    /**
     * @return the randomSeed
     */
    public static long getRandomSeed() {
        return randomSeed;
    }

    /**
     * 今再現できていないので使用中止. NN 再現のために {@value randomSeed} を固定する. NN インスタンスを作成する前に使用する.
     * 
     * @param randomSeed randomSeed
     */
    @Deprecated
    public static void setRandomSeed(long randomSeed) {
        SynapticConnection.randomSeed = randomSeed;
    }

    /**
     * シナプス結合による出力
     * 
     * @param input シナプス前ニューロンの出力.
     * @return output シナプス前ニューロンからシナプス後ニューロンへの出力
     */
    public double output(double input, LearningData data) {
        double output = weight * input;
        postsyanapticNeuron.addInput(data, output);
        return output;
    }

    public void calc_delta(LearningData data) {
        double input = presynapticNeuron.getOutput(data);
        double delta_k = postsyanapticNeuron.getDeltaK();
        double deltaWeight = NeuralNetwork.getETA() * delta_k * input;
        deltaWeights.put(data, Double.valueOf(deltaWeight));
        epochDeltaWeight += deltaWeights.get(data).doubleValue();
    }

    public void fit(){
        if(previousDeltaWeights == null){
            weight += epochDeltaWeight;
            previousDeltaWeights = new Double(epochDeltaWeight);
        }else{
            weight += epochDeltaWeight + NeuralNetwork.getALPHA()*previousDeltaWeights.doubleValue();
        }
        previousDeltaWeights = Double.valueOf(epochDeltaWeight);
        resetEpochDeltaWeight();
    }

    public void resetEpochDeltaWeight() {
        epochDeltaWeight = 0;
    }

    @Override
    public String toString() {
        String s = "weight:"+weight;
        return s;
    }
}