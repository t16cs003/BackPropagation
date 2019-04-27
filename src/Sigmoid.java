class Sigmoid implements ActivationFunction{
    /** シグモイド関数のゲインの規定値 */
    public final static double EPSILON = 1.0;
    /** ゲイン */
    private double epsilon;
    
    public Sigmoid(double epsilon){
        this.epsilon = epsilon;
    }
    /**
     * @return epsilon シグモイド関数のゲイン
     */
    public double getEpsilon() {
        return epsilon;
    }
    @Override
    public double output(double x) {
        return 1.0 / (1.0 + Math.exp(-1.0 * epsilon * x));
    }
    @Override
    public double getDifferentialValue(double x) {
        double y = output(x);
        return (1.0 - y) * y;
    }
}