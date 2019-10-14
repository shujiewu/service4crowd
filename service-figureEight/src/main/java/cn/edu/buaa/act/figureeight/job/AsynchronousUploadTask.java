package cn.edu.buaa.act.figureeight.job;


import cn.edu.buaa.act.figureeight.exception.NullAPIKeyException;
import cn.edu.buaa.act.figureeight.model.Job;
import cn.edu.buaa.act.figureeight.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;

class AsynchronousUploadTask implements Runnable {

    @Autowired
    JobService jobService;

    private final Job theJob;
    private final String theAbsolutePath;
    private final String theContentType;

    public AsynchronousUploadTask(final Job aJob, final String aAAbsolutePath, final String aContentType) {
        theJob = aJob;
        theAbsolutePath = aAAbsolutePath;
        theContentType = aContentType;
    }

    @Override
    public void run()
    {
        jobService.upload(theJob, theAbsolutePath, theContentType);
    }
}
