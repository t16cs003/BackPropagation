class Bias extends Neuron{
    
    public Bias(Layer layer,int index){
        super(layer,index,null);
    }

    @Override
    public double getOutput(LearningData data) {
        return 1;
    }
    @Override
    public double output(LearningData data,boolean debug) {
        double output = 1.0;
        outputs.put(data, 1.0);
        for(SynapticConnection postsynapticConnection:postsynapticConnections){
            postsynapticConnection.output(output,data);
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
            System.out.println(indent+"#Layer "+layer.getIndex()+" Bias "+":");
            System.out.println(indent+tab+"input "+inputs.get(data));
            System.out.println(indent+tab+"output "+output);
            /***********************/
        }
        return output;
    }
}