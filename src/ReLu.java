class ReLu implements ActivationFunction{
    @Override
    public double output(double input) {
        if(input <= 0){
            return 0;
        }else{
            return input;
        }
    }
    @Override
    public double getDifferentialValue(double input) {
        if(input <= 0){
            return 0;
        }else{
            return 1;
        }
    }
}