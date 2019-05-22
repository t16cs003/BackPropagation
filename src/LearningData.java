import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

class LearningData{
    /** 入力データ */
    private double[] inputs;
    /** 教師出力 */
    private double[] teacherOutputs;

    public LearningData(double[] inputs,double[] teacherOutputs){
        this.inputs = inputs;
        this.teacherOutputs = teacherOutputs;
    }

    /**
     * @return inputs 入力データ
     */
    public double[] getInputs() {
        return inputs;
    }

    /**
     * @return teacherOutputs 教師出力
     */
    public double[] getTeacherOutputs() {
        return teacherOutputs;
    }

    /**
     * 学習データのリストを訓練データと検証データに分ける.
     * 
     * @param learningDatas 訓練データと検証データに分割したい学習データのリスト
     * @param ration        学習データを検証データにする比率.
     * @return 訓練データ,学習データの順に格納されたリスト.
     */
    public static List<List<LearningData>> splitLearningDatas(List<LearningData> learningDatas, double ration) {
        Random rand = new Random(System.currentTimeMillis());
        int size = (int) (learningDatas.size() * ration);
        ArrayList<LearningData> verificationDatas = new ArrayList<>(size);
        ArrayList<LearningData> trainingDatas = new ArrayList<>(learningDatas.size() - size);
        for (int i = 0; i < size; i++) {
            int tmp = rand.nextInt(learningDatas.size());
            LearningData verificationData = learningDatas.get(tmp);
            learningDatas.remove(tmp);
            verificationDatas.add(verificationData);
        }
        for (LearningData data : learningDatas) {
            trainingDatas.add(data);
        }
        System.out.println("trainingDatas.size() : "+trainingDatas.size());
        System.out.println("verificationDatas.size() : "+verificationDatas.size());
        ArrayList<List<LearningData>> datasList = new ArrayList<>();
        datasList.add(trainingDatas);
        datasList.add(verificationDatas);
        return datasList;
    }

    /**
     * 2乗誤差を求める. 誤差 0 (実際の出力と教師出力の一致)と Null (次元が異なり計算できない)を 区別するため戻り値の型は Double を使用.
     * 
     * @param outputs 出力データ
     * @return e_pattern 入力データに対する誤差関数の値
     */
    public Double calcSquareError(double[] outputs) {
        if (teacherOutputs.length != outputs.length){ 
            System.out.println("教師データと出力データの次元が異なっています.");
            return null;
        }
        double e_pattern = 0;
        for (int i = 0; i < teacherOutputs.length; i++) {
            e_pattern += (teacherOutputs[i] - outputs[i]) * (teacherOutputs[i] - outputs[i]);
        }
        return e_pattern;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("inputs : " + Arrays.toString(inputs) + System.getProperty("line.separator"));
        s.append("outputs : " + Arrays.toString(teacherOutputs));

        return s.toString();
    }
}