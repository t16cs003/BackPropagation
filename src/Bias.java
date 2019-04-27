class Bias extends Neuron{
    
    public Bias(Layer layer,int index){
        super(layer,index,null);
    }

    @Override
    public double output(boolean debug) {
        output = 1.0;
        for(SynapticConnection postsynapticConnection:postsynapticConnections){
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
            System.out.println(indent+"#Layer "+layer.getIndex()+" Bias "+":");
            System.out.println(indent+tab+"input "+input);
            System.out.println(indent+tab+"output "+output);
            /***********************/
        }
        return output;
    }
}