package cn.edu.buaa.act.fastwash.estimate;


import cn.edu.buaa.act.fastwash.data.Annotation;
import cn.edu.buaa.act.fastwash.data.Tag;
import cn.edu.buaa.act.fastwash.data.TaskItemEntity;
import cn.edu.buaa.act.fastwash.data.TrainingItem;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
public class EstimateService {


    public JSONArray estimateResult(List<TaskItemEntity> taskItemEntityList,String taskId) throws Exception {

        List<TaskAndGT> taskAndGTS = getTaskAndGt(taskItemEntityList);
        makeMultiLabel(taskAndGTS);
        makeMutiLabelFile(taskAndGTS,taskId);
        String command = "python D:/fastwashdata/method_no_agg.py "+taskId;
        try {
            exeCmd(command);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        computeDifficult(taskAndGTS,taskId);
        JSONArray jsonArray = makeTask(taskAndGTS);
        return jsonArray;
    }
    public static void exeCmd(String commandStr) {
        BufferedReader br = null;
        try {
            System.out.println("exec python");
            Process p = Runtime.getRuntime().exec(commandStr);
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            System.out.println(sb.toString());
            System.out.println("exec python complete");
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
            if (br != null)
            {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<TaskAndGT> getTaskAndGt(List<TaskItemEntity> taskItemEntityList) throws Exception{
        List<TaskAndGT> taskAndGTS = new ArrayList<>();
        taskItemEntityList.forEach(taskItemEntity -> {
            TaskAndGT taskAndGT = new TaskAndGT();
            taskAndGT.setTaskItemEntity(taskItemEntity);
            if(taskItemEntity.getIterations()>=1){
                taskAndGTS.add(taskAndGT);
            }
        });
        return taskAndGTS;
    }
    private static double MAX_OVERLAP = 0.5;

    private void makeMultiLabel(List<TaskAndGT> taskAndGTS){
        for(TaskAndGT taskAndGT:taskAndGTS){
            List<List<Annotation>> annotations = new ArrayList<>();
            taskAndGT.getTaskItemEntity().getUpdateTime().forEach(time->{
                annotations.add(taskAndGT.getTaskItemEntity().getAnnotations().get(time));
            });
            taskAndGT.setResult(annotations);
            for (Iterator<Annotation> iterator= annotations.get(0).iterator();iterator.hasNext();) {
                if(iterator.next().getBox().getScore()<0.5)
                    iterator.remove();
            }
            long allBoxSize = annotations.stream().flatMap(annotation->annotation.stream()).count();
            int[][] matrix = new int[annotations.size()][(int)allBoxSize];

            int right = annotations.get(0).size();
            for(int i = 0;i<annotations.size();i++){
                if(annotations.get(i).size()==0){
                    //默认为0
                }
                for(int j = 0;j<annotations.get(i).size();j++){
                    if(i==0){
                        matrix[i][j]=1;
                        annotations.get(0).get(j).setFold(j);
                    }else if(i<=annotations.size()-1){
                        Annotation annotation = annotations.get(i).get(j);
                        for(int m = i-1;m>=0;m--){
                            double[] iou = new double[annotations.get(m).size()];
                            for(int k=0;k<annotations.get(m).size();k++){
                                iou[k]= IoU.box_iou(annotation.getBox(), annotations.get(m).get(k).getBox());
                            }
                            // 如果这一行有值
                            if(annotations.get(m).size()>0){
                                double max = Arrays.stream(iou).max().getAsDouble();
                                if(max>MAX_OVERLAP){
                                    for(int k=0;k<annotations.get(m).size();k++){
                                        if(Math.abs(max-iou[k])<0.000001){
                                            // 设置位置
                                            matrix[i][annotations.get(m).get(k).getFold()]=1;
                                            annotation.setFold(annotations.get(m).get(k).getFold());
                                        }
                                    }
                                    break;
                                }else{
                                    // 如果已经找到了最上面一行，还没找到，那么就设置为新的box
                                    if(m==0){
                                        matrix[i][right]=1;
                                        annotation.setFold(right);
                                        right++;
                                    }
                                }
                            }else {
                                // 如果第一行都没有，那么就设置为新的box
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
            taskAndGT.setMultiTaskRight(right);
            taskAndGT.setMultiTask(matrix);
        }
    }

    public void makeMutiLabelFile(List<TaskAndGT> taskAndGTS,String taskId){
        List<MultiLabel> labels = new ArrayList<MultiLabel>();
        Map<String, Integer> workers = new HashMap<String, Integer>();
        Map<String, Integer> items = new HashMap<String, Integer>();
        Map<String, Integer> classes = new HashMap<String, Integer>();
        classes.put("0",0);
        classes.put("1",1);
        int itemId = 0;
        for(TaskAndGT taskAndGT:taskAndGTS) {
            int[][] matrix = taskAndGT.getMultiTask();
            for (int i = 0; i < taskAndGT.getMultiTaskRight(); i++) {
                itemId++;
                for (int j = 0; j < matrix.length; j++) {
                    if (taskAndGT.getTaskItemEntity().getWorkerList().get(j).equals("baseModel")) {
                        taskAndGT.getTaskItemEntity().getWorkerList().set(j, "10" + taskAndGT.getTaskItemEntity().getClassId());
                    }
                    String workerId = taskAndGT.getTaskItemEntity().getWorkerList().get(j);
                    String cls = matrix[j][i] == 0 ? String.valueOf(0) : String.valueOf(1);
                    if (!workers.containsKey(workerId))
                        workers.put(workerId, Integer.parseInt(workerId));
                    if (!items.containsKey(String.valueOf(itemId)))
                        items.put(String.valueOf(itemId), items.size());
                    labels.add(new MultiLabel(workers.getOrDefault(workerId, -1),
                            items.getOrDefault(String.valueOf(itemId), -1),
                            classes.getOrDefault(cls, -1)));
                }
            }
        }
        writeMultiLabel(labels,taskId);
    }

    public void writeMultiLabel(List<MultiLabel> labels,String taskId){
        try {
            File csv = new File("D:/fastwashdata/writers_"+taskId+".csv"); // CSV数据文件
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

    private void computeDifficult(List<TaskAndGT> taskAndGTS,String taskId){
        JSONObject jsonObject = ReadJsonFile.ReadFile("D:/fastwashdata/weight_file_zc_compare_"+taskId+".json");
        int pos = 0;
        for(TaskAndGT taskAndGT:taskAndGTS){
            int[][] matrix = taskAndGT.getMultiTask();
            List<Annotation> annotations = new ArrayList<>();
            boolean add =true;
            int iterSize = taskAndGT.getMultiTask().length-1;
            double[][] greatProb = new double[iterSize][2];
            double last = 0.0;
            for(int it = 1;it<=iterSize;it++){
                // 代表第几个子任务
                int start = pos;
                int right = taskAndGT.getMultiTaskRight()-1;
                for(int x = taskAndGT.getMultiTaskRight()-1;x>=0;x--){
                    if(taskAndGT.getMultiTask()[it][x]==0){
                        right--;
                    }else{
                        break;
                    }
                }
                double first = 1.0;
                double second = 1.0;
                //迭代寻找下一个任务
                for(int j = 0;j<=right;j++){
                    JSONArray iter = jsonObject.getJSONArray(String.valueOf(start));
                    // pos位置的第迭代行
                    JSONObject res = iter.getJSONObject(it-1);

                    //如果工人回答0
                    if(taskAndGT.getMultiTask()[it-1][j]==0){
                        first = first* Double.parseDouble(res.get("0").toString());
                    }else{
                        first = first* Double.parseDouble(res.get("1").toString());
                    }
                    //如果工人回答1
                    if(taskAndGT.getMultiTask()[it][j]==0){
                        second = second* Double.parseDouble(res.get("0").toString());
                    }else{
                        second = second* Double.parseDouble(res.get("1").toString());
                    }
                    start++;
                }
                double left = 1.0;
                double r = 1.0;
                if(taskAndGT.getDifficultList() ==null){
                    List<Double> difficult = new ArrayList<>();
                    taskAndGT.setDifficultList(difficult);
                }
                if(it==1){
                    greatProb[it-1][0]=first*left;
                    greatProb[it-1][1]=second*r;
                    // 第一个人用
                    taskAndGT.getDifficultList().add(1-greatProb[it-1][0]);
                    // 第二个人用
                    taskAndGT.getDifficultList().add(1-greatProb[it-1][1]);
                }else{
                    greatProb[it-1][0]=first*left;
                    greatProb[it-1][1]=second*r;
                    taskAndGT.getDifficultList().add(1-greatProb[it-1][1]);
                }
                // taskAndGT.setDifficult(1-greatProb[it-1][0]);
                // System.out.println(taskAndGT.getDifficult());
                // System.out.println(pos);
            }
            pos = pos + taskAndGT.getMultiTaskRight();
        }
    }

    private JSONArray makeTask(List<TaskAndGT> taskAndGTS){
        JSONArray jsonArray = new JSONArray();
        for(TaskAndGT taskAndGT:taskAndGTS) {
            JSONObject jsonObject = new JSONObject();
            JSONArray workerList = new JSONArray();
            int pos = 0;
            for(String worker: taskAndGT.getTaskItemEntity().getWorkerList()){
                JSONObject w = new JSONObject();

                if (worker.equals("baseModel")) {
                    w.put("workerId","10" + taskAndGT.getTaskItemEntity().getClassId());
                }else{
                    w.put("workerId", worker);
                }
                // w.put("quality",taskAndGT.getTaskItemEntity().getQuality().get(pos).getQuality());
                if (worker.equals("baseModel")){
                    w.put("change",false);
                }else{
                    if(!taskAndGT.getTaskItemEntity().getChange().get(pos)){
                        w.put("change",false);
                    }
                    else{
                        w.put("change",true);
                    }
                }
                // 难度给下一个人
                w.put("difficult",taskAndGT.getDifficultList().get(pos));
                workerList.add(w);
                pos++;
            }
            jsonObject.put(taskAndGT.getTaskItemEntity().getId(),workerList);
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }
}
