package cn.edu.buaa.act.figureeight.service.impl;

import cn.edu.buaa.act.figureeight.constant.JobAttribute;
import cn.edu.buaa.act.figureeight.exception.MalformedCrowdURLException;
import cn.edu.buaa.act.figureeight.exception.NullAPIKeyException;
import cn.edu.buaa.act.figureeight.model.Channel;
import cn.edu.buaa.act.figureeight.model.Job;
import cn.edu.buaa.act.figureeight.model.Judgment;
import cn.edu.buaa.act.figureeight.model.Unit;
import cn.edu.buaa.act.figureeight.service.JobService;
import cn.edu.buaa.act.figureeight.service.JudgementService;
import cn.edu.buaa.act.figureeight.service.UnitService;
import cn.edu.buaa.act.figureeight.weblayer.WebCall;
import cn.edu.buaa.act.figureeight.weblayer.WebUtil;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static cn.edu.buaa.act.figureeight.constant.Constants.URL;
import static cn.edu.buaa.act.figureeight.weblayer.WebUtil.convertAttributesToNameValuePair;

/**
 * JobServiceImpl
 *
 * @author wsj
 * @date 2018/10/18
 */
@Service
public class JobServiceImpl implements JobService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobServiceImpl.class);

    @Autowired
    private WebUtil theWebUtil;
    @Autowired
    private WebCall theWebJobCall;

    @Autowired
    private UnitService unitService;

    @Autowired
    private JudgementService judgementService;

    private AtomicInteger id = new AtomicInteger(0);

    private ConcurrentHashMap<String,Job> hasJob = new ConcurrentHashMap<>();

    @Override
    public Job create(Job aJob, String apiKey) {
        JSONObject json = new JSONObject();
        try {
            json.put("id",String.valueOf(id.addAndGet(1)));
            json.put(JobAttribute.JUDGMENTS_PER_UNIT.toString(),aJob.getAttribute(JobAttribute.JUDGMENTS_PER_UNIT));
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        try {
//            LOGGER.info("Create the job with id  - {} ", aJob.getId());
//            String augURL = theWebUtil.urlTransform(URL, ".json?key=" + apiKey);
//            json = new JSONObject(theWebJobCall.createJob(augURL, convertAttributesToNameValuePair(aJob.getAttributes())));
//        } catch (JSONException e) {
//            LOGGER.error(e.toString());
//        }
        Job job = new Job(json);
        System.out.println(job.getAttribute(JobAttribute.JUDGMENTS_PER_UNIT)+"JUDPERunit");
        System.out.println(job.getId()+"ID");
        try {
            hasJob.put(json.getString("id"),job);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return job;
    }

    @Override
    public void upload(Job aJob, String apiKey, String absolutePath, String contentType) throws NullAPIKeyException {
        if (!apiKey.isEmpty()) {
            String augURL = theWebUtil.urlTransform(URL, "/" + aJob.getId() + "/upload.json?key=" + apiKey + "&force=true");
            LOGGER.info("Uploading data in job with id  - {} from path {}", aJob.getId(), absolutePath);
            theWebJobCall.upload(absolutePath, augURL, contentType);
        } else {
            throw new NullAPIKeyException();
        }
    }

    @Override
    public void upload(String aJobId, String apiKey, String absolutePath, String contentType, Map<String, String> param) {
        LOGGER.info("Upload with parameters for job id  - " + aJobId);
        String augURL = theWebUtil.urlTransform(theWebUtil.urlTransform(URL, "/" + aJobId + "/upload.json?key=" + apiKey), param);
        theWebJobCall.upload(absolutePath, augURL, contentType);
    }

    @Override
    public void upload(Job aJob, String apiKey, String url) {
        aJob.addProperty("uri", url);
        String augURL = theWebUtil.urlTransform(URL, "/" + aJob.getId() + "/upload.json?key=" + apiKey);
        LOGGER.info("Uploading data from url {} the job with id  - {}", url, aJob.getId());
        theWebJobCall.uploadURI(augURL, convertAttributesToNameValuePair(aJob.getAttributes()));
    }

    @Override
    public void upload(Job aJob, String apiKey, String absolutePath, String contentType, boolean async) throws NullAPIKeyException {
//        if (async) {
//            ExecutorService myExecutorService = Executors.newSingleThreadExecutor();
//            myExecutorService.submit(new AsynchronousUploadTask(this, aJob, absolutePath, contentType));
//        } else {
//            upload(aJob, absolutePath, contentType);
//        }
    }

    @Override
    public void update(Job aJob, String apiKey) throws NullAPIKeyException {
        if (!apiKey.isEmpty()) {
            String augURL = theWebUtil.urlTransform(URL, "/" + aJob.getId() + ".json?key=" + apiKey);
            LOGGER.info("Updating the job with id  - {}", aJob.getId());
            theWebJobCall.update(augURL, convertAttributesToNameValuePair(aJob.getAttributes()));

        } else {
            throw new NullAPIKeyException();
        }
    }

    @Override
    public List<Unit> getJobUnits(String aJobId, String apiKey) {
        List<Unit> units = new ArrayList<>();
        String augURL = theWebUtil.urlTransform(URL, "/" + aJobId + "/units.json?key=" + apiKey);
        LOGGER.info("Obtaining units for job with id  - {} ", aJobId);

        JSONObject json = theWebJobCall.getUnits(augURL);
        Iterator iterate = json.keys();
        while (iterate.hasNext()) {
            String key = (String) iterate.next();
            units.add(unitService.getUnit(aJobId, key, apiKey));
        }
        return units;
    }

    @Override
    public Unit getUnit(String aJobId, String aUnitId, String apiKey) throws JSONException {
        return unitService.getUnit(aJobId, aUnitId, apiKey);
    }

    @Override
    public void addRemoveGold(String aJobId, String apiKey, Map<String, String> param) {
        String url = theWebUtil.urlTransform(theWebUtil.urlTransform(URL, "/" + aJobId + "/gold?key=" + apiKey), param);
        theWebJobCall.put(url);
    }

    @Override
    public List<Job> getJobs(String apiKey) {
        try {
            JSONObject json;
            List<Job> myJobs = new ArrayList<>();
            String augUrl = theWebUtil.urlTransform(URL, ".json?key=" + apiKey);
            LOGGER.debug("Web call @ URL - " + augUrl);
            JSONArray jsonArray = theWebJobCall.getJobs(augUrl);
            LOGGER.info("Creating list of Jobs");
            for (int i = 0; i < jsonArray.length(); i++) {
                json = jsonArray.getJSONObject(i);
                myJobs.add(new Job(json));
                LOGGER.info("{} Job added ", i);
            }
            return myJobs;
        } catch (JSONException e) {
            LOGGER.error(e.toString());
        }
        return null;
    }

    @Override
    public Job getJob(String aJobId, String apiKey) throws NullAPIKeyException {
//        try {
//            LOGGER.info("Reading job with id  - {}", aJobId);
//            String augURL = theWebUtil.urlTransform(URL, "/" + aJobId + ".json?key=" + apiKey);
//            if (!(aJobId.isEmpty() || apiKey.isEmpty())) {
//                if (!augURL.isEmpty()) {
//                    return new Job(theWebJobCall.getJob(augURL));
//                }
//            } else {
//                throw new NullAPIKeyException();
//            }
//        } catch (MalformedCrowdURLException e) {
//            LOGGER.error(e.toString());
//        }
//        return null;
        System.out.println(aJobId+"ID2");
        return hasJob.get(aJobId);
    }

    private List<String> getListOfJudgments(final JSONObject aJudgJson) throws JSONException {
        String ids = aJudgJson.get("_ids").toString();
        return new ArrayList<String>(Arrays.asList(ids.substring(1, ids.length() - 1).split(",\\s*")));
    }

    @Override
    public List<Judgment> getJudgments(String aJobId, String apiKey) {
        try {
            String augURL = theWebUtil.urlTransform(URL, "/" + aJobId + "/judgments.json?key=" + apiKey);
            LOGGER.info("Getting judgments for job id  - {}", aJobId);
            JSONObject json = new JSONObject(theWebJobCall.get(augURL));
            List<Judgment> judgments = new ArrayList<>();
            Iterator iterate = json.keys();
            while (iterate.hasNext()) {
                String key = (String) iterate.next();

                JSONObject judgJson = new JSONObject(json.get(key).toString());

                List<String> judglist = getListOfJudgments(judgJson);
                for (String jId : judglist) {
                    String url = theWebUtil.urlTransform(URL, "/" + aJobId + "/judgments/" + jId + ".json?key=" + apiKey);
                    JSONObject myRawJudgment = new JSONObject(theWebJobCall.get(url));
                    judgments.add(new Judgment(myRawJudgment));
                }
            }
            return judgments;
        } catch (JSONException e) {
            LOGGER.error(e.toString());
        }

        return null;
    }

    @Override
    public Judgment getJudgment(String aJobId, String aJudgmentId, String apiKey) {
        return judgementService.getJudgment(aJobId, aJudgmentId, apiKey);
    }


    @Override
    public JSONObject getUnitsStatus(String aJobId, String apiKey) {
        LOGGER.info("Obtaining the status of the job with id  - {}", aJobId);
        return theWebJobCall.getUnits(theWebUtil.urlTransform(URL, "/" + aJobId + "/units/ping.json?key=" + apiKey));
    }

    @Override
    public String status(String aJobId, String apiKey) {
        LOGGER.info("Status of job with id  - " + aJobId);
        return theWebJobCall.get(theWebUtil.urlTransform(URL, "/" + aJobId + "/ping.json?key=" + apiKey));
    }

    @Override
    public void pause(String aJobId, String apiKey) {
        LOGGER.info("Pausing job with id  - {}",
                aJobId);
        theWebJobCall.get(theWebUtil.urlTransform(URL, "/" + aJobId + "/pause.json?key=" + apiKey));
    }

    @Override
    public void resume(String aJobId, String apiKey) {
        LOGGER.info("Resuming job with id  - " + aJobId);
        theWebJobCall.get(theWebUtil.urlTransform(URL, "/" + aJobId + "/resume.json?key=" + apiKey));
    }

    @Override
    public void cancel(String aJobId, String apiKey) {
        LOGGER.info("Cancelling job with id  - " + aJobId);
        theWebJobCall.get(theWebUtil.urlTransform(URL, "/" + aJobId + "/cancel.json?key=" + apiKey));
    }

    @Override
    public void delete(String aJobId, String apiKey) {
        LOGGER.info("Deleting job with id  - " + aJobId);
        theWebJobCall.delete(theWebUtil.urlTransform(URL, "/" + aJobId + ".json?key=" + apiKey));
    }

    @Override
    public Channel getChannels(String aJobId, String apiKey) {
        Channel channel = new Channel(aJobId);
        String augURL = theWebUtil.urlTransform(URL, "/" + aJobId + "/channels?key=" + apiKey);
        List<String> available;
        List<String> enabled;

        LOGGER.info("Getting channel for job id  - " + aJobId);

        JSONObject json;
        try {
            json = new JSONObject(theWebJobCall.get(augURL));
            enabled = addEnabledChannel(json);
            available = addAvailableChannels(json);
            channel.setChannels(available, enabled);
            return channel;
        } catch (JSONException e) {
            LOGGER.error(e.toString());
        }

        return null;
    }

    private List<String> addEnabledChannel(JSONObject json) throws JSONException {
        List<String> enabled = new ArrayList<String>();
        if (!json.get("enabled_channels").toString().isEmpty()) {
            JSONArray arr = new JSONArray(json.get("enabled_channels").toString());
            for (int i = 0; i < arr.length(); i++) {
                enabled.add(arr.getString(i));
            }
        }
        return enabled;
    }

    private List<String> addAvailableChannels(JSONObject json) throws JSONException {
        List<String> available = new ArrayList<String>();
        if (!json.get("available_channels").toString().isEmpty()) {
            JSONArray arrAv = new JSONArray(json.get("available_channels").toString());
            for (int i = 0; i < arrAv.length(); i++) {
                available.add(arrAv.getString(i));
            }
        }
        return available;
    }


    @Override
    public void setChannels(String aJobId, String apiKey, List<String> channels) {
        System.out.println("Setting channel");
        System.out.println(channels);
        String url = theWebUtil.urlTransform(URL, "/" + aJobId + "/channels?key=" + apiKey);

        // Locally declaring a new attribute for channels
        List<NameValuePair> attributes = new ArrayList<NameValuePair>();
        for (String channel : channels) {
            attributes.add(new BasicNameValuePair("channels[]", channel));
        }
        theWebJobCall.update(url, attributes);
    }

    @Override
    public void bulkSplit(String aJobId, String on, String with, String apiKey) {
        LOGGER.info("Bulk splitting job id  - " + aJobId);
        System.out.println("Bulk Splitting..");
        String augURL = theWebUtil.urlTransform(URL, "/" + aJobId + "/units/split?key=" + apiKey + "&on=" + on + "&with=" + with);
        theWebJobCall.put(augURL);
    }

    @Override
    public JSONObject legend(String aJobId, String apiKey) {
        LOGGER.info("Fetching Legends of job id  - " + aJobId);

        String url = theWebUtil.urlTransform(URL, "/" + aJobId + "/legend.json?key=" + apiKey);

        try {
            return new JSONObject(theWebJobCall.get(url));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void setPayPerAssignment(Job aJob, String pay, String apiKey) {
        LOGGER.info("Adding for job id  - " + aJob.getId());
        aJob.addProperty("payment_cent", pay);
        String augURL = theWebUtil.urlTransform(URL, "/" + aJob.getId() + ".json?key=" + apiKey);
        theWebJobCall.update(augURL, convertAttributesToNameValuePair(aJob.getAttributes()));
    }
}
