package Indexer;

public class Triplet<T, U, V, I> {

    private  T tag;
    private  U word;
    private  V occurance;
    private  I pmcid;
    
    public Triplet(T tag, U word, V occurance, I pmcid) {
        this.tag = tag;
        this.word = word;
        this.occurance = occurance;
        this.pmcid = pmcid;
    }

    public T getTag() { return tag; }
    public U getWord() { return word; }
    public V getOccurance() { return occurance; }
    public I getPmcid() {return pmcid;}
    public void setOccurance(V occurance)	{	this.occurance=occurance;}
    public void setPmcid(I pmcid)	{	this.pmcid=pmcid;}

}