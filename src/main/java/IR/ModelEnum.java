package IR;

public enum ModelEnum {
    TF_IDF, BM25, Likelihood, Luence;

    @Override
    public String toString() {
        switch(this) {
            case TF_IDF: return "tf-idf";
            case BM25: return "BM25";
            case Likelihood: return "Smoothed Query Likelihood";
            case Luence: return "Luence";
            default: throw new IllegalArgumentException();
        }
    }
}
