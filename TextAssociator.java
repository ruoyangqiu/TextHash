import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/* CSE 373 Starter Code
 * @Author Kevin Quinn 
 * 
 * TextAssociator represents a collection of associations between words.
 * See write-up for implementation details and hints
 * 
 */

/* @Author Ruoyang Qiu(1133074)
 * 
*/
public class TextAssociator {
	private WordInfoSeparateChain[] table;
	private int size;
	private int count; // point the position of the table size
	
	/*  I choose 11 as the initial table size and expand to roughly double size prime number except 373
	 *  until 800011
	 */
	private final int[] PRIME_TABLE_SIZE = { 11, 23, 47, 97, 193, 373, 797, 1583, 3001, 6007, 12011, 24023,  48049,
											96097, 200003, 400009, 800011}; 
	private final double LOAD_FACTOR = 0.75; //load factor
			
	
	/* INNER CLASS
	 * Represents a separate chain in your implementation of your hash table.
	 * A WordInfoSeparateChain is a list of WordInfo objects that have all
	 * been hashed to the same index of the TextAssociator.
	 */
	private class WordInfoSeparateChain {
		private List<WordInfo> chain;
		
		/* Creates an empty WordInfoSeparateChain without any WordInfo.
		 */
		public WordInfoSeparateChain() {
			this.chain = new ArrayList<WordInfo>();
		}
		
		/* Adds a WordInfo object to the SeparateCahin.
		 * Returns true if the WordInfo was successfully added, false otherwise.
		 */
		public boolean add(WordInfo wi) {
			if(!chain.contains(wi)){
				chain.add(wi);
			}
			return false;
		}
		
		/* Removes the given WordInfo object from the separate chain.
		 * Returns true if the WordInfo was successfully removed, false otherwise.
		 */
		public boolean remove(WordInfo wi) {
			if(chain.contains(wi)){
				chain.remove(wi);
				return true;
			}
			return false;
		}
		
		// Returns the size of this separate chain.
		public int size() {
			return chain.size();
		}
		
		// Returns the String representation of this separate chain.
		public String toString() {
			return chain.toString();
		}
		
		// Returns the list of WordInfo objects in this chain.
		public List<WordInfo> getElements() {
			return chain;
		}
	}
	
	
	/* Creates a new TextAssociator without any associations.
	 */
	public TextAssociator() {
		//TODO: Implement as explained in spec
		count = 0;
		size = 0;
		table = new WordInfoSeparateChain[(PRIME_TABLE_SIZE[count])];
	}
	
	
	/* Adds a word with no associations to the TextAssociator.
	 * Returns False if this word is already contained in your TextAssociator,
	 * Returns True if this word is successfully added.
	 */
	public boolean addNewWord(String word) {
		int hash = getHash(word);
		if((double)size / table.length >= LOAD_FACTOR ){
			resize();
		}
		if(!containsKey(hash, word)){
			if(table[hash] == null){
				table[hash] = new WordInfoSeparateChain();
				table[hash].add(new WordInfo(word));
			} else {
				table[hash].add(new WordInfo(word));
			}
			size ++;
			return true;
		}
		return false;
	}
	
	
	/* Adds an association between the given words. Returns true if association correctly added, 
	 * returns false if first parameter does not already exist in the TextAssociator or if 
	 * the association between the two words already exists.
	 */
	public boolean addAssociation(String word, String association) {
		int hash = getHash(word);
		if(containsKey(hash, word)){
			return getWord(word, hash).addAssociation(association);
		}
		return false;
		//TODO: Implement as explained in spec
	}
	
	
	/* Remove the given word from the TextAssociator, returns false if word 
	 * was not contained, returns true if the word was successfully removed.
	 * Note that only a source word can be removed by this method, not an association.
	 */
	public boolean remove(String word) {
		int hash = getHash(word);
		if(containsKey(hash, word)){
			size --;
			return table[hash].remove(getWord(word, hash));
		}
		return false;
	}
	
	
	/* Returns a set of all the words associated with the given String.
	 * Returns null if the given String does not exist in the TextAssociator.
	 */
	public Set<String> getAssociations(String word) {
		int hash = getHash(word);
		if(containsKey(hash, word)){
			return getWord(word, hash).getAssociations();
		}
		return null;
	}
	
	
	/* Prints the current associations between words being stored
	 * to System.out
	 */
	public void prettyPrint() {
		System.out.println("Current number of elements : " + size);
		System.out.println("Current table size: " + table.length);
		
		// Walk through every possible index in the table.
		for (int i = 0; i < table.length; i++) {
			if (table[i] != null) {
				WordInfoSeparateChain bucket = table[i];
				
				// For each separate chain, grab each individual WordInfo.
				for (WordInfo curr : bucket.getElements()) {
					System.out.println("\tin table index, " + i + ": " + curr);
				}
			}
		}
		System.out.println();
	}
	
	/*  return the hashcode (the position where the given word should located in the table) 
	 *  of the given word by the hashCode function of string
	*/
	private int getHash(String s){
		if (s == null) {
			throw new IllegalArgumentException();
		}
		int hash = Math.abs(s.hashCode());
		return hash % PRIME_TABLE_SIZE[count];
	}
	
	/*  check if the given word is in the give bucket of the table
	 *  return true if it is in the bucket
	 *  return false if it is not in the table or the give bucket is empty
	 */
	private boolean containsKey(int hash, String s){
		if(table[hash] == null){
			return false;
		}
		for(WordInfo wi : table[hash].getElements()){
			if(wi.getWord().equalsIgnoreCase(s)){
				return true;
			}
		}
		return false;
	}
	
	/*  resize the table when it reach the load factor: 0.75
	 *  expand the table to a larger prime number size
	 */
	private void resize(){
		count ++;
		WordInfoSeparateChain[] newTable = new WordInfoSeparateChain[PRIME_TABLE_SIZE[count]];
		for(WordInfoSeparateChain bucket : table){
			if(bucket != null){
				for(WordInfo wi: bucket.getElements()){
					int hash = getHash(wi.getWord());
					if(newTable[hash] == null){
						newTable[hash] = new WordInfoSeparateChain();
					}
					newTable[hash].add(wi);
				}
			}
		}
		table = newTable;
	}
	
	/*  find and return the WordInfo of the given word in the given bucket
	 *  return null if there's no such WordInfo 
	 */
	private WordInfo getWord(String s, int hash){
		for(WordInfo wi : table[hash].getElements()){
			if(wi.getWord().equalsIgnoreCase(s)){
				return wi;
			}
		}
		return null;
	}
}
