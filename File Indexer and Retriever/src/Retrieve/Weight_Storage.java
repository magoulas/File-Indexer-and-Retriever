package Retrieve;

public class Weight_Storage<R,Q,W> {
		private  R word;
	    private  Q doc;
	    private  W weight;
	    
	    public Weight_Storage(R word, Q doc, W weight) {
	        this.word = word;
	        this.doc = doc;
	        this.weight = weight;
	    }

	    public Weight_Storage() {
			// TODO Auto-generated constructor stub
		}

		public R getWord() { return word; }
	    public Q getDoc() { return doc; }
	    public W getWeight() {return weight;}
	    public void setDoc(Q doc)	{	this.doc=doc;}
	    public void setTf(W weight)	{	this.weight=weight;}
	    public void setWord(R word)	{	this.word=word;}
}
