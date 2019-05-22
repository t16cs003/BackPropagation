interface ActivationFunction{
    /**
     * 入力 x における関数の出力.
     * @param input 入力値
     * @return 出力値
     */
    public double output(double input);
    /**
     * 入力 x における関数の微分値を求める.
     * @param input
     * @return 入力値 input における微分値
     */
    public double getDifferentialValue(double input);
}