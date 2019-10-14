package cn.edu.buaa.act.figureeight.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * This is class is analogous to Channels in CrowdFlower
 * You can set or get enabled and available channels
 *
 */
public class Channel {

    private Set<String> availableChannels;
    private Set<String> enabledChannels;
    private String theJobID;

    public Channel(String jobID)
    {
        this(jobID, new HashSet<>(),new HashSet<>());
    }

    private Channel(String aJobId, Set<String> aAvailableChannels, Set<String> aEnabledChannels)
    {
        theJobID = aJobId;
        availableChannels = aAvailableChannels;
        enabledChannels = aEnabledChannels;
    }

    public List<String> getEnabledChannels()
    {
        return new ArrayList<>(enabledChannels);
    }


    public List<String> getAvailableChannels()
    {
        return new ArrayList<>(availableChannels);
    }

    public void setChannels(List<String> available, List<String> enabled) {
        availableChannels = new HashSet<>(available);
        enabledChannels = new HashSet<>(enabled);
    }

    public String getJobID()
    {
        return theJobID;
    }
}
