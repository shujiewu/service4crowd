package cn.edu.buaa.act.data.processor.sink;

//import cn.edu.buaa.act.common.constant.NotifyChannelConstants;
//import cn.edu.buaa.act.common.msg.PlayLoadMessage;
//import cn.edu.buaa.act.data.processor.channel.DataProcessorNotifyChannel;
//import cn.edu.buaa.act.data.processor.service.MetaProcessorService;
//import cn.edu.buaa.act.data.processor.service.UnitDataProcessorService;
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.stream.annotation.EnableBinding;
//import org.springframework.cloud.stream.annotation.StreamListener;
//import org.springframework.messaging.Message;

import java.util.Map;

/**
 * DataProcessor
 *
 * @author wsj
 * @date 2018/10/25
 */
//@Slf4j
//@EnableBinding({DataProcessorNotifyChannel.class})
//public class DataOperation {
//
//    @Autowired
//    private DataProcessorNotifyChannel dataProcessorNotifyChannel;
//
//    @Autowired
//    private MetaProcessorService metaProcessorService;
//
//    @Autowired
//    private UnitDataProcessorService unitDataProcessorService;
//    @StreamListener(NotifyChannelConstants.DATA_PROCESSOR_CHANNEL)
//    public void receiverPdfNotify(Message<PlayLoadMessage> message) {
//        PlayLoadMessage<Map> playLoadMessage= message.getPayload();
//        if (playLoadMessage.getServiceName().equals("service-data-processor:MetaDataJoin")){
//            try {
//                metaProcessorService.doJoinLabel(playLoadMessage);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        else if(playLoadMessage.getServiceName().equals("service-data-processor:MetaDataFilter")){
//            try {
//                metaProcessorService.doFilter(playLoadMessage);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        else if(playLoadMessage.getServiceName().equals("service-data-processor:GetAnswerAndTruth")){
//            try {
//                unitDataProcessorService.doGetAnswerAndTruth(playLoadMessage);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        else if(playLoadMessage.getServiceName().equals("service-data-processor:UpdateState")){
//            try {
//                unitDataProcessorService.doUpdateState(playLoadMessage);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        else if(playLoadMessage.getServiceName().equals("service-data-processor:TaskSelection")){
//            try {
//                unitDataProcessorService.doTaskSelection(playLoadMessage);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }else if(playLoadMessage.getServiceName().equals("service-data-processor:AddTruthToState")){
//            try {
//                unitDataProcessorService.doAddTruthToState(playLoadMessage);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        log.info("频道"+NotifyChannelConstants.DATA_PROCESSOR_CHANNEL+"监听信息为:"+  message.getPayload().getActivityId());
//    }
//}
