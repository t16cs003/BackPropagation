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
    private double delta_weight;

    private double epochc_delta_weight;
    /**
     * @param presynapticNeuron シナプス前神経細胞
     * @param postsynapticNeuron シナプス後神経細胞
     */
    public SynapticConnection(Neuron presynapticNeuron,Neuron postsynapticNeuron){
        this.presynapticNeuron = presynapticNeuron;
        this.postsyanapticNeuron = postsynapticNeuron;
        weight = (rand.nextInt(2)==0)?rand.nextDouble():-rand.nextDouble();
        delta_weight = 0;
        epochc_delta_weight = 0;
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
     * 今再現できていないので使用中止.
     * NN 再現のために {@value randomSeed} を固定する.
     * NN インスタンスを作成する前に使用する.
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
    public double output(double input){
        double output = weight * input;
        postsyanapticNeuron.addInput(output);
        return output;
    }

    public void calc_delta(){
        double input = presynapticNeuron.getOutput();
        double delta_k = postsyanapticNeuron.getDelta_k();
        delta_weight = NeuralNetwork.getETA()*delta_k*input;
        epochc_delta_weight += delta_weight;
    }

    public void fit(){
        weight += epochc_delta_weight;
        resetEpochsDeltaWeight();
    }

    public void resetEpochsDeltaWeight(){
        epochc_delta_weight = 0;
    }
    @Override
    public String toString() {
        String s = "weight:"+weight;
        return s;
    }
}