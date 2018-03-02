package IR.task1.engine;

import IR.ModelEnum;

import java.util.Map;

public interface SearchEngine {
    Map<String, Double> getScore();
    void search(String query);
    ModelEnum getType();
}
