package Indexer;

public class Freq<B,A,C> {

    private  A word;
    private  B pmcid;
    private  C tf;
    
    public Freq( B pmcid,A word, C tf) {
        this.word = word;
        this.pmcid = pmcid;
        this.tf = tf;
    }

    public Freq() {
		// TODO Auto-generated constructor stub
	}

	public A getWord() { return word; }
    public B getPmcid() {return pmcid;}
    public C gettf() {return tf;}
    public void setPmcid(B pmcid)	{	this.pmcid=pmcid;}
    public void setWord(A word)	{	this.word=word;}
    public void setTf(C tf) {this.tf=tf;}
}
