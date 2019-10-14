package cn.edu.buaa.act.data.service;

import cn.buaa.act.crowd.common.entity.AnswerEntity;
import cn.buaa.act.crowd.common.entity.AnswerStatRepresentation;
import cn.buaa.act.crowd.common.entity.Label;
import cn.buaa.act.datacore.repository.AnswerReposiory;
import cn.buaa.act.datacore.service.api.IAnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnswerServiceImpl implements IAnswerService {

    @Autowired
    AnswerReposiory answerReposiory;
    @Override
    public AnswerEntity insertAnswerEntity(AnswerEntity answerEntity) {
        return answerReposiory.save(answerEntity);
    }

    @Override
    public Page<AnswerEntity> queryPageByUser(String userId, Pageable pageable) {
        Page<AnswerEntity> result= answerReposiory.findAnswerEntitiesByUserId(userId,pageable);
        return result;
    }

    @Override
    public List<AnswerEntity> queryAllByUser(String userId) {
        return answerReposiory.findAnswerEntitiesByUserId(userId);
    }
    @Override
    public String queryAnswerEntityType(String answerEntityId) {
        return answerReposiory.findOne(answerEntityId).getDataType();
    }

    public void deleteAnswerEntity(String id){
        answerReposiory.delete(id);
    }

    public AnswerEntity queryById(String id){
        return  answerReposiory.findOne(id);
    }

    public AnswerEntity queryByName(String name){
        return  answerReposiory.findAnswerEntityByName(name);
    }

    public AnswerStatRepresentation insertDataStat(String dataId){
        AnswerEntity answerEntity = this.queryById(dataId);

        AnswerStatRepresentation answerStatRepresentation = new AnswerStatRepresentation();
        Set<String> workers = new HashSet<>();
        Set<String> items = new HashSet<>();
        Map<String,Integer> classes = new HashMap<>();
        List<Integer> workerPerTask=new ArrayList<>();
        List<Integer> taskPerWorker=new ArrayList<>();
        List<Double> taskConsistency =new ArrayList<>();
        // log.info("111");
        if(answerEntity.getDataType().equals("continuous")){
            answerEntity.getLabelList().forEach(label -> {
                workers.add(label.getWorker());
                items.add(label.getItem());
            });

            items.forEach(item->{
                List<Label> answerOfItem = answerEntity.getLabelList().stream().filter(ml -> ml.getItem().equals(item)).collect(Collectors.toList());
                List<Double> answerValue = new ArrayList<>();
                for(Label label : answerOfItem){
                    answerValue.add(Double.parseDouble(label.getAnswer()));
                }
                double consistency = 0.0;
                int size = answerOfItem.size();


                double mid = 0;
                Collections.sort(answerValue);
                if(size%2==0) mid = (answerValue.get(size/2)+answerValue.get(size/2+1))/2; else mid = answerValue.get(size/2);

                for (int i = 0;i<answerValue.size();i++){
                    consistency += Math.pow((answerValue.get(i)-mid),2);
                }
                taskConsistency.add(Math.sqrt(consistency/size));
                workerPerTask.add(size);
            });
            System.out.println(taskConsistency.stream().mapToDouble(x->x).average());
        }
        else
        {
            // log.info("222");
            answerEntity.getLabelList().forEach(label -> {
                workers.add(label.getWorker());
                items.add(label.getItem());
                if(!classes.containsKey(label.getAnswer()))
                    classes.put(label.getAnswer(), classes.size());
            });

            items.forEach(item->{
                List<Label> answerOfItem = answerEntity.getLabelList().stream().filter(ml -> ml.getItem().equals(item)).collect(Collectors.toList());
                int [] count =new int[classes.size()];
                for(Label label : answerOfItem){
                    for(String labelClass:classes.keySet()){
                        if(label.getAnswer().equals(labelClass)){
                            count[classes.get(labelClass)] ++;
                            break;
                        }
                    }
                }
                double consistency = 0.0;
                int size = answerOfItem.size();
                for (int i = 0;i<count.length;i++){
                    if(count[i]>0){
                        // System.out.print(((double)count[i]/size)+ " ");
                        // System.out.print(Math.log((double) count[i]/size) / Math.log(size));
                        consistency = consistency + ((double)count[i]/size)*(Math.log((double)count[i]/size) / Math.log(size));
                    }
                }
                // System.out.println(size);
                if(consistency!=0)
                    consistency=-consistency;
                taskConsistency.add(consistency);
                workerPerTask.add(size);
            });
            // .info("333");
            answerStatRepresentation.setClassTotal(classes.size());
        }
        answerStatRepresentation.setTaskConsistency(taskConsistency);
        List<Double> quality =new ArrayList<>();
        workers.forEach(worker->{
            List<Label> answerOfWorker = answerEntity.getLabelList().stream().filter(ml -> ml.getWorker().equals(worker)).collect(Collectors.toList());
            int size = answerOfWorker.size();
            taskPerWorker.add(size);
            if(answerEntity.getHasGold()){
                if (answerEntity.getDataType().equals("continuous")){
                    //List<Double> answerValue = new ArrayList<>();
                    //List<Double> groundTruth = new ArrayList<>();
                    double value = 0.0;
                    for(Label label : answerOfWorker){
                        // answerValue.add(Double.parseDouble(label.getAnswer()));
                        //groundTruth.add(Double.parseDouble(answerEntity.getGoldLabels().get(label.getItem())));
                        double answerValue =Double.parseDouble(label.getAnswer());
                        double groundTruth = Double.parseDouble(answerEntity.getGoldLabels().get(label.getItem()));
                        value +=Math.pow(answerValue-groundTruth,2);
                    }
                    value = Math.sqrt(value/size);
                    quality.add(value);
                }
                else {
                    int correct =0;
                    for(Label label : answerOfWorker){
                        if(label.getAnswer().equals(answerEntity.getGoldLabels().get(label.getItem())))
                            correct++;
                    }
                    quality.add((double)correct/size);
                }
            }
        });

        answerStatRepresentation.setWorkerQuality(quality);
        answerStatRepresentation.setTaskPerWorker(taskPerWorker);
        answerStatRepresentation.setWorkerPerTask(workerPerTask);
        answerStatRepresentation.setAnswerTotal(answerEntity.getLabelList().size());
        answerStatRepresentation.setWorkerTotal(workers.size());
        answerStatRepresentation.setTaskTotal(items.size());
        answerStatRepresentation.setDataName(answerEntity.getName());
        answerStatRepresentation.setDataType(answerEntity.getDataType());
        answerStatRepresentation.setDataId(answerEntity.getId());
        answerStatRepresentation.setWorkerId(new ArrayList<>(workers));
        answerStatRepresentation.setTaskId(new ArrayList<>(items));


        // answerEntity.setAnswerStatRepresentation(answerStatRepresentation);
        // this.insertAnswerEntity(answerEntity);
        return answerStatRepresentation;
    }
}
