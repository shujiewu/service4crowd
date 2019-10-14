package cn.edu.buaa.act.figureeight.service.impl;

import cn.edu.buaa.act.figureeight.constant.Constants;
import cn.edu.buaa.act.figureeight.constant.UnitAttribute;
import cn.edu.buaa.act.figureeight.exception.MalformedCrowdURLException;
import cn.edu.buaa.act.figureeight.model.Unit;
import cn.edu.buaa.act.figureeight.service.UnitService;
import cn.edu.buaa.act.figureeight.weblayer.WebCall;
import cn.edu.buaa.act.figureeight.weblayer.WebUtil;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static cn.edu.buaa.act.figureeight.constant.Constants.URL;

/**
 * UnitServiceImpl
 *
 * @author wsj
 * @date 2018/10/27
 */
@Service
public class UnitServiceImpl implements UnitService {

    @Autowired
    private WebUtil theWebUtil;

    @Autowired
    private WebCall theWebCall;

    private final Logger logger = LoggerFactory.getLogger(UnitServiceImpl.class);
    @Override
    public Unit getUnit(String aJobId, String aUnitId, String apiKey) {
        try {
            logger.info("Refreshing the units for job with id  - {}", aJobId);
            String url = theWebUtil.urlTransform(URL, "/" + aJobId + "/units/" + aUnitId + ".json?key=" + apiKey);
            return new Unit(theWebCall.getMeta(url));
        }
        catch (MalformedCrowdURLException e) {
            logger.error(e.toString());
        }
        return null;
    }

    @Override
    public Unit create(Unit aUnit, String apiKey) {
        String augURL = URL + "/" + aUnit.getJobId() + "/" + "units.json?key=" + apiKey;
        try {
            if (!apiKey.isEmpty()) {
                logger.info("Creating units for job with id  - " + aUnit.getJobId());
                return new Unit(new JSONObject(theWebCall.create(augURL, WebUtil.convertAttributesToNameValuePair(aUnit.getAttributes()))));
            }
        }
        catch (JSONException e) {
            logger.error(e.toString());
        }
        return null;
    }


    @Override
    public void update(Unit aUnit, String apiKey) {
        List<NameValuePair> myAttributes = WebUtil.convertAttributesToNameValuePair(aUnit.getAttributes());
        if (!(myAttributes.size() == 0)) {
            String augURL = URL + "/" + aUnit.getJobId() + "/" + "units/" + aUnit.getUnitId() + ".json?key=" + apiKey;
            logger.info("Updating unit - " + aUnit.getUnitId());
            theWebCall.update(augURL, myAttributes);
        }
    }

    @Override
    public void delete(String aJobId, String aUnitId, String apiKey) {
        logger.info("Deleting unit - " + aUnitId);
        String myURL = theWebUtil.urlTransform(URL, "/" + aJobId + "/" + aUnitId + ".json?key=" + apiKey);
        theWebCall.delete(myURL);
    }

    @Override
    public void addGold(Unit aUnit, String legend, String value, String reason, String apiKey) {
        String url = theWebUtil.urlTransform(URL, "/" + aUnit.getJobId() + "/units/" + aUnit.getUnitId() + ".json?key=" + apiKey);
        try {
            String data = aUnit.getAttribute(UnitAttribute.DATA);
            JSONObject json = new JSONObject(data);
            Iterator iterate = json.keys();
            List<NameValuePair> myAttributes = new ArrayList<>();
            while (iterate.hasNext())
            {
                String key = iterate.next().toString();
                myAttributes.add(new BasicNameValuePair("unit[data][" + key + "]", json.get(key).toString()));
            }
            myAttributes.add(new BasicNameValuePair("unit[golden]", "true"));
            myAttributes.add(new BasicNameValuePair("unit[data][" + legend + "_gold][]", value));
            myAttributes.add(new BasicNameValuePair("unit[data][" + legend + "_gold_reason][]", reason));
            theWebCall.update(url, myAttributes);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeGold(String aJobId, String aUnitId, String apiKey) {
        String url = theWebUtil.urlTransform(URL, "/" + aJobId + "/units/" + aUnitId + ".json?key=" + apiKey);
        List<NameValuePair> myAttributes = new ArrayList<>();
        myAttributes.add(new BasicNameValuePair("unit[golden]", "false"));
        theWebCall.update(url, myAttributes);
    }
}
