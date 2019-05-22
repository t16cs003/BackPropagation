import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BackPropagation{
    
    /**
     * IrisDatasetを読み込み,学習データのリストを返す.
     * @return 学習データのリスト
     */
    public static List<LearningData> readIris(){
        ArrayList<LearningData> learningDatas = new ArrayList<>();
        ArrayList<ArrayList<String>> columnList = new ArrayList<>();
        Path irisData = Paths.get("./iris.csv");
        try (BufferedReader reader = Files.newBufferedReader(irisData,StandardCharsets.UTF_8)){
            String line = new String();
            Map<String,Integer> classMap = new HashMap<>();
            for(int i = 0;(line = reader.readLine()) != null;i++){
                String[] splits = line.split(",");
                if(i != 0){
                    for(int j = 0; j < splits.length; j++){
                        if(j == splits.length - 1){
                            if(!classMap.containsKey(splits[j])){
                                classMap.put(splits[j], classMap.size());
                            }
                            columnList.get(j).add(classMap.get(splits[j]).toString());
                        }else{
                            columnList.get(j).add(splits[j]);
                        }
                    }
                }else{
                    for(int j = 0; j < splits.length; j++){
                        ArrayList<String> column = new ArrayList<>();
                        columnList.add(column);
                    }
                }
            }

            for(int i = 0; i < columnList.get(0).size(); i++){
                double[] inputs = new double[columnList.size()-1];
                for(int j = 0; j < columnList.size()-1; j++){
                    inputs[j] = Double.parseDouble(columnList.get(j).get(i));
                }
                double[] outputs = new double[classMap.size()];
                outputs[Integer.parseInt(columnList.get(columnList.size()-1).get(i))] = 0.9;
                learningDatas.add(new LearningData(inputs,outputs));
            }
            
        } catch (IOException e) {
            System.err.println(e);
        }
        return learningDatas;     
    }

    public static void main(String[] args) {
        List<List<LearningData>> datasList = LearningData.splitLearningDatas(readIris(),0.5);
        List<LearningData> trainingDatas = datasList.get(0);
        List<LearningData> verificationDatas = datasList.get(1);

        NeuralNetwork nn = new NeuralNetwork(Neuron.FunctionType.ReLu, 4, 50, 50, 50, 3);
        // System.out.println("NNの初期状態.");
        // System.out.println(nn);
        // System.out.println();
        
        // NNを訓練データで学習させる.
        nn.fit(trainingDatas,verificationDatas,true);
        System.out.println();

        // System.out.println("学習後のNNの状態.");
        // System.out.println(nn);
        
        System.out.println("*******************************");
        System.out.println("verify by trainingDatas.");
        System.out.println("training precision:"+nn.verify(trainingDatas,false));
        System.out.println();

        System.out.println("*******************************");
        System.out.println("verify by verificationDatas.");
        System.out.println("verification precision:"+nn.verify(verificationDatas,false));
    }
}
