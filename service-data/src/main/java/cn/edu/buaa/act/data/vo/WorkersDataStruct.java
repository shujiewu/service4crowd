package cn.edu.buaa.act.data.vo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;


/**
 * @author wsj
 */
public class WorkersDataStruct<TypeQ, TypeR> {
    private Map<TypeQ, List<TypeR>> responses = new HashMap<>();
    private static Logger log = LogManager.getLogger(WorkersDataStruct.class);
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
            ((List) this.responses.get(question)).add(response);
            if (log.isDebugEnabled()) {
                log.debug("Adding a worker who has answered the same question multiple times!!!");
            }
        } else {
            ++this.numResponses;
            this.responses.put(question, new ArrayList());
            ((List) this.responses.get(question)).add(response);
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

        while (var1.hasNext()) {
            TypeQ key = (TypeQ) var1.next();
            List<TypeR> repeatResponse = (List) this.responses.get(key);
            String repeats = " Responses:";

            Object keyInner;
            for (Iterator var5 = repeatResponse.iterator(); var5.hasNext(); repeats = repeats + "  " + keyInner) {
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

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof WorkersDataStruct)) {
            return false;
        } else {
            WorkersDataStruct<?, ?> other = (WorkersDataStruct) obj;
            return this.responses.equals(other.getWorkerResponses());
        }
    }

    @Override
    public int hashCode() {
        return this.responses.hashCode() + this.numQuestions;
    }
}