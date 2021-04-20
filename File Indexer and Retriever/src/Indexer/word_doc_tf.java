package Indexer;

public class word_doc_tf<R,Q,W> {
	

	    private  R word;
	    private  Q doc;
	    private  W tf;
	    
	    public word_doc_tf(R word, Q doc, W tf) {
	        this.word = word;
	        this.doc = doc;
	        this.tf = tf;
	    }

	    public word_doc_tf() {
			// TODO Auto-generated constructor stub
		}

		public R getWord() { return word; }
	    public Q getDoc() { return doc; }
	    public W getTf() {return tf;}
	    public void setDoc(Q doc)	{	this.doc=doc;}
	    public void setTf(W tf)	{	this.tf=tf;}
	    public void setWord(R word)	{	this.word=word;}


	}

