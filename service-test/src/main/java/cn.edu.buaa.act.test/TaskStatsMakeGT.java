package cn.edu.buaa.act.test;

import cn.edu.buaa.act.fastwash.data.Annotation;
import cn.edu.buaa.act.fastwash.data.Tag;
import cn.edu.buaa.act.fastwash.data.TaskItemEntity;
import cn.edu.buaa.act.fastwash.data.TrainingItem;
import cn.edu.buaa.act.test.algorithm.MultiLabel;
import cn.edu.buaa.act.test.model.TaskAndGT;
import cn.edu.buaa.act.test.task.Stats;
import cn.edu.buaa.act.test.util.IoU;
import cn.edu.buaa.act.test.util.ReadJsonFile;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.util.*;


@SpringBootApplication
public class TaskStatsMakeGT implements CommandLineRunner {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(TaskStatsMakeGT.class, args);
    }
    @Autowired
    private Stats stats;

    @Override
    public void run(String... args) throws Exception {
//        Set<String> imageId= new HashSet<>();
//        List<TaskItemEntity> taskItemEntities = readDataFile();
//        List<TrainingItem> trainingItems = readGTFile();
//
//        Map<String,List<Tag>> groundTruthMap = new HashMap<>();
//        trainingItems.forEach(taskItemEntity -> {
//            groundTruthMap.put(taskItemEntity.getImageId(),taskItemEntity.getTagList());
//        });
//        List<TaskAndGT> taskAndGTS = new ArrayList<>();
//
//        taskItemEntities.forEach(taskItemEntity -> {
//            List<Annotation> annotations = new ArrayList<>();
//            groundTruthMap.get(taskItemEntity.getImageId()).forEach(tag -> {
//                if(tag.getClassification().getId().equals(taskItemEntity.getClassId())){
//                    Annotation annotation = new Annotation();
//                    annotation.setBox(tag.getBox());
//                    annotation.setClassification(tag.getClassification());
//                    annotations.add(annotation);
//                }
//            });
//            TaskAndGT taskAndGT = new TaskAndGT();
//            taskAndGT.setGT(annotations);
//            taskAndGT.setTaskItemEntity(taskItemEntity);
//            if(taskItemEntity.getIterations()>=2){
//                taskAndGTS.add(taskAndGT);
//            }
//        });
        // computeRECAndACC(taskAndGTS);
//        makeMultiLabel(taskAndGTS);
//        GLAD(taskAndGTS);

    }

    public void GLAD(List<TaskAndGT> taskAndGTS) {
        List<MultiLabel> labels = new ArrayList<MultiLabel>();
        Map<String, Integer> workers = new HashMap<String, Integer>();
        Map<String, Integer> items = new HashMap<String, Integer>();
        Map<String, Integer> classes = new HashMap<String, Integer>();
        Map<String, String> goldLabels = new HashMap<String, String>();

        JSONObject jsonObject = ReadJsonFile.ReadFile("D:/config_file_em.json");
        System.out.println(jsonObject);

        List<TaskAndGT> taskAndGTS1 = new ArrayList<>();

        classes.put("0",0);
        classes.put("1",1);
        int itemId = 0;
        int pos = 0;
        for(TaskAndGT taskAndGT:taskAndGTS){
            int[][] matrix = taskAndGT.getMultiTask();
            for(int i = 0;i<taskAndGT.getMultiTaskRight();i++){
                itemId++;
                for(int j = 0;j<matrix.length;j++){
                    if(taskAndGT.getTaskItemEntity().getWorkerList().get(j).equals("baseModel")){
                        taskAndGT.getTaskItemEntity().getWorkerList().set(j,"baseModel"+taskAndGT.getTaskItemEntity().getClassId());
                    }
                    String workerId = taskAndGT.getTaskItemEntity().getWorkerList().get(j);
                    String cls = matrix[j][i]==0?String.valueOf(0):String.valueOf(1);

                    if(!workers.containsKey(workerId))
                        workers.put(workerId, workers.size());

                    if(!items.containsKey(String.valueOf(itemId)))
                        items.put(String.valueOf(itemId), items.size());

                    labels.add(new MultiLabel(workers.getOrDefault(workerId, -1),
                            items.getOrDefault(String.valueOf(itemId), -1),
                            classes.getOrDefault(cls, -1)));
                }
            }

//            for(int i = 0;i<matrix.length;i++){
//                for(int j = 0;j<taskAndGT.getMultiTaskRight();j++){
//                    System.out.print(matrix[i][j]+"      ");
//                }
//                System.out.println();
//            }
            List<Annotation> annotations = new ArrayList<>();

            boolean add =true;
            for(int j = 0;j<taskAndGT.getMultiTaskRight();j++){
                JSONObject res = jsonObject.getJSONObject(String.valueOf(pos));
                //System.out.print(res.get("1")+"   ");
                if((Double.parseDouble(res.get("1").toString()))>0.8){
                    int row = matrix.length-1;
                    for(int i = matrix.length-1;i>=0;i--){
                        if(matrix[i][j]==1){
                            row = matrix.length-1;
                            break;
                        }
                    }
                    for(Annotation annotation:taskAndGT.getResult().get(row)){
                        if(annotation.getFold()==j){
                            annotations.add(annotation);
                        }
                    }
                }
//                if(Math.abs(Double.parseDouble(res.get("1").toString()))-(Double.parseDouble(res.get("0").toString()))<0.3){
//                    add = false;
//                    // System.out.println(11111);
//                }
                pos++;
            }
            taskAndGT.setInferenceResult(annotations);
            if(add)
                taskAndGTS1.add(taskAndGT);
//            System.out.println();
//            System.out.println(taskAndGT.getAcc());
//            System.out.println(taskAndGT.getRec());
//            System.out.println(taskAndGT.getMeanIoU());
//            System.out.println("====================");
        }
        computeRECAndACCByInference(taskAndGTS1);
//        System.out.println(items.size());
//        System.out.println(workers.size());
//        System.out.println(labels.size());
    }

    private void writeMultiLabel(List<MultiLabel> labels){
        try {
            File csv = new File("D:/writers.csv"); // CSV数据文件

            BufferedWriter bw = new BufferedWriter(new FileWriter(csv, false)); // 附加
            bw.write("question" + "," + "worker" + "," + "answer");
            bw.newLine();
            labels.forEach(multiLabel -> {
                try {
                    bw.write(multiLabel.getJ() + "," + multiLabel.getI() + "," + multiLabel.getLij());
                    bw.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            bw.close();
        } catch (FileNotFoundException e) {
            // File对象的创建过程中的异常捕获
            e.printStackTrace();
        } catch (IOException e) {
            // BufferedWriter在关闭对象捕捉异常
            e.printStackTrace();
        }
    }

    private void makeMultiLabel(List<TaskAndGT> taskAndGTS){
        for(TaskAndGT taskAndGT:taskAndGTS){
            // System.out.println("up:"+taskAndGT.getTaskItemEntity().getUpdateTime().size()+"iter:"+taskAndGT.getTaskItemEntity().getIterations());
            List<List<Annotation>> annotations = new ArrayList<>();
            taskAndGT.getTaskItemEntity().getUpdateTime().forEach(time->{
                annotations.add(taskAndGT.getTaskItemEntity().getAnnotations().get(time));
            });
            // annotations.add(taskAndGT.getGT());

            taskAndGT.setResult(annotations);
            for (Iterator<Annotation> iterator= annotations.get(0).iterator();iterator.hasNext();) {
                if(iterator.next().getBox().getScore()<0.5)
                   iterator.remove();
            }

            long allBoxSize = annotations.stream().flatMap(annotation->annotation.stream()).count();
            int[][] matrix = new int[annotations.size()][(int)allBoxSize];

            int right = annotations.get(0).size();
            for(int i = 0;i<annotations.size();i++){
                for(int j = 0;j<annotations.get(i).size();j++){
                    if(i==0){
                        matrix[i][j]=1;
                        annotations.get(0).get(j).setFold(j);
                    }else{
                        Annotation annotation = annotations.get(i).get(j);
                        for(int m = i-1;m>=0;m--){
                            double[] iou = new double[annotations.get(m).size()];
                            for(int k=0;k<annotations.get(m).size();k++){
                                iou[k]= IoU.box_iou(annotation.getBox(), annotations.get(m).get(k).getBox());
                            }
                            if(annotations.get(m).size()>0){
                                double max = Arrays.stream(iou).max().getAsDouble();
                                if(max>0.5){
                                    for(int k=0;k<annotations.get(m).size();k++){
                                        if(Math.abs(max-iou[k])<0.000001){
                                            matrix[i][annotations.get(m).get(k).getFold()]=1;
                                            annotation.setFold(annotations.get(m).get(k).getFold());
                                        }
                                    }
                                    break;
                                }else{
                                    if(m==0){
                                        matrix[i][right]=1;
                                        annotation.setFold(right);
                                        right++;
                                    }
                                }
                            }else {
                                if(m==0){
                                    matrix[i][right]=1;
                                    annotation.setFold(right);
                                    right++;
                                }
                            }
                        }
                    }
                }
            }
            taskAndGT.setMultiTask(matrix);
            taskAndGT.setMultiTaskRight(right);
        }
    }

    private void computeRECAndACCByInference(List<TaskAndGT> taskAndGTS){
        int count =0 ;
        double accCount =0.0;
        double recCount = 0.0;
        double IoUCount = 0.0;
        for(TaskAndGT taskAndGT:taskAndGTS) {
            TaskItemEntity taskItemEntity = taskAndGT.getTaskItemEntity();
            List<Annotation> gt = taskAndGT.getGT();
            List<Annotation> result = taskAndGT.getInferenceResult();
            double[][] matrix = new double[gt.size()][result.size()];
            for (int i = 0; i < gt.size(); i++) {
                for (int j = 0; j < result.size(); j++) {
                    matrix[i][j] = IoU.box_iou(gt.get(i).getBox(), result.get(j).getBox());
                }
            }
            int[] gtFlag = new int[gt.size()];
            double[] resFlag = new double[result.size()];
            for (int m = 0; m < result.size(); m++) {
                double max = Arrays.stream(matrix)
                        .flatMapToDouble(a -> Arrays.stream(a))
                        .max().getAsDouble();
                for (int i = 0; i < gt.size(); i++) {
                    for (int j = 0; j < result.size(); j++) {
                        if (Math.abs(matrix[i][j] - (max)) < 0.000001) {
                            if (gtFlag[i] == 0 && matrix[i][j] > 0.5) {
                                gtFlag[i] = 1;
                                resFlag[j] = matrix[i][j];//找到了
                            } else {
                                if (gtFlag[i] == 1) {
                                    resFlag[j] = -1.0;//被别的占用
                                } else {
                                    resFlag[j] = -2.0;//iou太小了                                }
                                }
                            }
                            for (int k = 0; k < gt.size(); k++) {
                                matrix[k][j] = -1.0;
                            }
                        }
                    }
                }
                int tp = 0;
                double iou = 0.0;
                for (int i = 0; i < resFlag.length; i++) {
                    if (resFlag[i] > 0) {
                        tp++;
                        iou+=resFlag[i];
                    }
                    if(resFlag[i]==-1.0){
                        // System.out.println("111");
                    }
                    if(resFlag[i]==-2.0){
                        // System.out.println("222");
                    }
                }
                double meanIoU = tp > 0 ?(iou/(double) tp):0;
                double acc = result.size() > 0 ? ((double) tp / (double)result.size()) : 0;
                double rec = (double)tp / (double)gt.size();
                taskAndGT.setAcc(acc);
                taskAndGT.setRec(rec);
                taskAndGT.setMeanIoU(meanIoU);
                //System.out.println("SIZE=" +  result.size()+";"+"TP="+tp);
                //System.out.println("ACC=" + acc + ";" + "REC=" + rec);
            }
            accCount+=taskAndGT.getAcc();
            recCount+=taskAndGT.getRec();
            IoUCount+=taskAndGT.getMeanIoU();
            // taskAndGTS.stream().flatMap(taskAndGT1 -> taskAndGT1.getAcc()).
            //System.out.println(imageId.size());
            //System.out.println(trainingItems.size());
            // writeGTToFile(new ArrayList<>(imageId));
            // System.out.println(imageId.size());
        }
        System.out.println(accCount/taskAndGTS.size());
        System.out.println(recCount/taskAndGTS.size());
        System.out.println(IoUCount/taskAndGTS.size());

        System.out.println(2.0/((1.0/(accCount/taskAndGTS.size()))+(1.0/(recCount/taskAndGTS.size()))));
    }

    private void computeRECAndACC(List<TaskAndGT> taskAndGTS){
        int count =0 ;
        double accCount =0.0;
        double recCount = 0.0;
        double IoUCount = 0.0;
        for(TaskAndGT taskAndGT:taskAndGTS) {
            TaskItemEntity taskItemEntity = taskAndGT.getTaskItemEntity();
            List<Annotation> gt = taskAndGT.getGT();
            List<Annotation> result = taskItemEntity.getAnnotations().get(taskItemEntity.getLastUpdateTime());
            double[][] matrix = new double[gt.size()][result.size()];
            for (int i = 0; i < gt.size(); i++) {
                for (int j = 0; j < result.size(); j++) {
                    matrix[i][j] = IoU.box_iou(gt.get(i).getBox(), result.get(j).getBox());
                }
            }
            int[] gtFlag = new int[gt.size()];
            double[] resFlag = new double[result.size()];
            for (int m = 0; m < result.size(); m++) {
                double max = Arrays.stream(matrix)
                        .flatMapToDouble(a -> Arrays.stream(a))
                        .max().getAsDouble();
                for (int i = 0; i < gt.size(); i++) {
                    for (int j = 0; j < result.size(); j++) {
                        if (Math.abs(matrix[i][j] - (max)) < 0.000001) {
                            if (gtFlag[i] == 0 && matrix[i][j] > 0.5) {
                                gtFlag[i] = 1;
                                resFlag[j] = matrix[i][j];//找到了
                            } else {
                                if (gtFlag[i] == 1) {
                                    resFlag[j] = -1.0;//被别的占用
                                } else {
                                    resFlag[j] = -2.0;//iou太小了                                }
                                }
                            }
                            for (int k = 0; k < gt.size(); k++) {
                                matrix[k][j] = -1.0;
                            }
                        }
                    }
                }
                int tp = 0;
                double iou = 0.0;
                for (int i = 0; i < resFlag.length; i++) {
                    if (resFlag[i] > 0) {
                        tp++;
                        iou+=resFlag[i];
                    }
                    if(resFlag[i]==-1.0){
                        // System.out.println("111");
                    }
                    if(resFlag[i]==-2.0){
                        // System.out.println("222");
                    }
                }
                double meanIoU = tp > 0 ?(iou/(double) tp):0;
                double acc = result.size() > 0 ? ((double) tp / (double)result.size()) : 0;
                double rec = (double)tp / (double)gt.size();
                taskAndGT.setAcc(acc);
                taskAndGT.setRec(rec);
                taskAndGT.setMeanIoU(meanIoU);
                //System.out.println("SIZE=" +  result.size()+";"+"TP="+tp);
                //System.out.println("ACC=" + acc + ";" + "REC=" + rec);
            }
            accCount+=taskAndGT.getAcc();
            recCount+=taskAndGT.getRec();
            IoUCount+=taskAndGT.getMeanIoU();
            // taskAndGTS.stream().flatMap(taskAndGT1 -> taskAndGT1.getAcc()).
            //System.out.println(imageId.size());
            //System.out.println(trainingItems.size());
            // writeGTToFile(new ArrayList<>(imageId));
            // System.out.println(imageId.size());
        }
        System.out.println(accCount/taskAndGTS.size());
        System.out.println(recCount/taskAndGTS.size());
        System.out.println(IoUCount/taskAndGTS.size());

        System.out.println(2.0/((1.0/(accCount/taskAndGTS.size()))+(1.0/(recCount/taskAndGTS.size()))));
    }

    private void writeDataToFile() throws Exception{
        FileOutputStream fos=new FileOutputStream(new File("D:/data.json"));
        OutputStreamWriter osw=new OutputStreamWriter(fos, "UTF-8");
        BufferedWriter bw=new BufferedWriter(osw);
        //注意关闭的先后顺序，先打开的后关闭，后打开的先关闭
        stats.getTaskItem("test10_5").forEach(taskItemEntity -> {
            try {
                bw.write(JSON.toJSONString(taskItemEntity)+"\t\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        bw.close();
        osw.close();
        fos.close();
    }
    private void writeGTToFile(List<String> imageIds) throws Exception{
        FileOutputStream fos=new FileOutputStream(new File("D:/gt.json"));
        OutputStreamWriter osw=new OutputStreamWriter(fos, "UTF-8");
        BufferedWriter bw=new BufferedWriter(osw);
        //注意关闭的先后顺序，先打开的后关闭，后打开的先关闭
        stats.findGroundTruth("test10_5",imageIds).forEach(trainingItem -> {
            try {
                bw.write(JSON.toJSONString(trainingItem)+"\t\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        bw.close();
        osw.close();
        fos.close();
    }
    private List<TrainingItem> readGTFile() throws Exception{
        FileInputStream fis=new FileInputStream("D:/gt.json");
        InputStreamReader isr=new InputStreamReader(fis, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        String line="";
        List<TrainingItem> trainingItems = new ArrayList<>();
        while ((line=br.readLine())!=null) {
            TrainingItem trainingItem = JSON.parseObject(line,TrainingItem.class);
            trainingItems.add(trainingItem);
        }
        br.close();
        isr.close();
        fis.close();
        return trainingItems;
    }
    private List<TaskItemEntity> readDataFile() throws Exception{
        FileInputStream fis=new FileInputStream("D:/data.json");
        InputStreamReader isr=new InputStreamReader(fis, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        String line="";
        List<TaskItemEntity> taskItemEntityList = new ArrayList<>();
        while ((line=br.readLine())!=null) {
            TaskItemEntity taskItemEntity = JSON.parseObject(line,TaskItemEntity.class);
            taskItemEntityList.add(taskItemEntity);
        }
        br.close();
        isr.close();
        fis.close();
        return taskItemEntityList;
    }
}
