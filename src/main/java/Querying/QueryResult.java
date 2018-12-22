package Querying;

import java.util.List;

public class QueryResult {

    private int queryNum;
    private List<String> relevantDocs;

    public QueryResult(int queryNum, List<String> relevantDocs) {
        this.queryNum = queryNum;
        this.relevantDocs = relevantDocs;
    }

    public int getQueryNum() {
        return queryNum;
    }

    public List<String> getRelevantDocs() {
        return relevantDocs;
    }

    public void setQueryNum(int queryNum) {
        this.queryNum = queryNum;
    }

    public void setRelevantDocs(List<String> relevantDocs) {
        this.relevantDocs = relevantDocs;
    }
}
