package cn.edu.buaa.act.data.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;


public class WorkersDataStruct<TypeQ, TypeR> {
    private Map<TypeQ, List<TypeR>> responses = new HashMap();
    private static Logger log = LogManager.getLogger(org.square.qa.utilities.constructs.workersDataStruct.class);
    private int numQuestions = 0;
    private int numResponses = 0;
    private double trust;

    public double getTrust() {
        return trust;
    }

    public void setTrust(double trust) {
        this.trust = trust;
    }

    public WorkersDataStruct() {
    }

    public void insertWorkerResponse(TypeQ question, TypeR response) {
        if (this.responses.containsKey(question)) {
            ++this.numResponses;
            ((List)this.responses.get(question)).add(response);
            if (log.isDebugEnabled()) {
                log.debug("Adding a worker who has answered the same question multiple times!!!");
            }
        } else {
            ++this.numResponses;
            this.responses.put(question, new ArrayList());
            ((List)this.responses.get(question)).add(response);
        }

        ++this.numQuestions;
    }

    public Map<TypeQ, List<TypeR>> getWorkerResponses() {
        assert this.responses != null : "Attemped to retrieve responses from null object";

        return this.responses;
    }

    public void printWorkerResponses() {
        assert this.responses != null : "Attemped to retrieve responses from null object";

        Iterator var1 = this.responses.keySet().iterator();

        while(var1.hasNext()) {
            TypeQ key = (TypeQ)var1.next();
            List<TypeR> repeatResponse = (List)this.responses.get(key);
            String repeats = " Responses:";

            Object keyInner;
            for(Iterator var5 = repeatResponse.iterator(); var5.hasNext(); repeats = repeats + "  " + keyInner) {
                keyInner = var5.next();
            }

            System.out.println("\t\tQuestion: " + key + repeats);
        }

    }

    public int getNumQuestionsAnswered() {
        return this.numQuestions;
    }

    public int getNumResponses() {
        return this.numResponses;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof org.square.qa.utilities.constructs.workersDataStruct)) {
            return false;
        } else {
            org.square.qa.utilities.constructs.workersDataStruct<?, ?> other = (org.square.qa.utilities.constructs.workersDataStruct)obj;
            return this.responses.equals(other.getWorkerResponses());
        }
    }

    public int hashCode() {
        return this.responses.hashCode() + this.numQuestions;
    }
}