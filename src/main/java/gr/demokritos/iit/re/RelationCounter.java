/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.demokritos.iit.re;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * A RelationCounter contains grouping of relations according to some 
 * a certain characteristic. Currently the label is used. This structure class
 * uses a HashMap to provide fast lookups for groups (RelationList) as well
 * as a TreeMap for fast lookup of groups with top counts. The rationale 
 * for using a TreeMap in addition to the HashMap is that we would like to not
 * have to sort each time a query is performed - it is supposed that querying
 * will be an often operation
 *
 * @author grv
 */
public class RelationCounter {

	private static GsonBuilder gsonBuilder =  registerGSonBuilder();
	private Map <String, RelationList> groups;
	private TreeMap <Integer, Set<String>> counts;

	public RelationCounter() {
		groups = new HashMap();
		counts = new TreeMap();
	}
	
	public RelationCounter(Collection<? extends RelationList> c) {
		groups = new HashMap();
		counts = new TreeMap();
		for(RelationList rel: c){
			add(rel);
		}
	}

	public void addAll(Collection<? extends RelationList> c) {
		for(RelationList rel: c){
			add(rel);
		}
	}

	public void addAll(RelationCounter rl) {
		Iterator<String> keyIterator = rl.groups.keySet().iterator();
		while(keyIterator.hasNext()){
			String label = keyIterator.next();
			RelationList update = rl.groups.get(label);
			// if label already exists, we need to add to that group 
			// not create a new entry
			if (groups.containsKey(label)){
				// get list to update
				RelationList toUpdate = groups.get(label);
				// remember what the size was before adding new entries
				Integer count = toUpdate.size();
				// how many we added
				Integer toAdd = update.size();
				// new place to add label is count + length of update
				Integer newCount = count + toAdd;
				// make update
				toUpdate.addAll(update);
				// we also need to update the counter
				incrementLabelFromTo(label, count, newCount);
			} 
			// otherwise we create a whole new entry
			else {
				this.groups.put(label, update);
				// also need to update counter
				Integer count = update.size();
				addLabelAtIndex(label, count);
			}
		}
	}

	/**
	 * Add a relation list to the counter. For each relation - 
	 * If its label already exists, 
	 * this method will append the relation to the corresponding RelationList.
	 * If not, it will create a RelationList and insert a new entry in the
	 * HashMap.
	 * @param reList - The relation list to be added...
	 *  
	 **/
	public void add(RelationList reList) {
		for (Relation rel : reList){
			add(rel);
		}
	}
	
	/**
	 * Add a relation to the counter. If its label already exists, 
	 * this method will append the relation to the corresponding RelationList.
	 * If not, it will create a RelationList and insert a new entry in the
	 * HashMap.
	 * @param rel - The relation to be added...
	 *  
	 **/
	public void add(Relation rel) {
		String label = rel.getLabel();
		if(groups.containsKey(label)){
			// if key exists, there are already members in the group
			// so we need to get the list and append to it
			RelationList group = groups.get(label);
			// get size before adding member
			Integer count = group.size();
			// add new relation
			group.add(rel);
			// also update counts, map label from counts to counts + 1 key
			incrementLabelFromTo(label, count, count + 1);
		}
		else {
			// otherwise we simply add it to existing labels
			RelationList rl = new RelationList();
			rl.add(rel);
			groups.put(label, rl);
			addLabelAtIndex(label, 1);
		}
	}

	/**
	 * Remove a relation from the grouping if it exists.
	 * Basically decrement relations counter unless counter
	 * is 1, in which case that grouping is dropped from the map.
	 * @param rel the relation to remove
	 */
	public void remove(Relation rel) {
		String label = rel.getLabel();
		if(groups.containsKey(label)){
			RelationList rl = groups.get(label);
			int numRelations = rl.size();
			// if there are more than one representative of this relation
			// just remove it from the list
			if(numRelations > 1){
				rl.remove(rel);
				// we need to update counts
				incrementLabelFromTo(label, numRelations, numRelations - 1);
			}
			// else remove the whole entry
			else{
				groups.remove(rel.getLabel());
				// remove the entry where count is 1
				removeLabelFromIndex(label, 1);
			}
		}
	}

	private void addLabelAtIndex(String label, int index) {
		// get current labels
		Set<String> labels = counts.get(index);
		// if no current labels, we create a list
		if (labels == null) {
			Set <String> group = new HashSet();
			group.add(label);
			// and add that entry
			counts.put(index, group);
		}
		else {
			labels.add(label);
		}
	}

	private void removeLabelFromIndex(String label, int index) {
		Set<String> labels = counts.get(index);
		labels.remove(label);
	}

	private void incrementLabelFromTo(String label, Integer from, Integer to) {
		// remove the label from the previous index
		removeLabelFromIndex(label, from);
		// add it to the count we want - may need to create the entry
		addLabelAtIndex(label, to);
	}

	public Set<String> getLabels(){
		return groups.keySet();
	}

	public Set<String> getTopLabels(int count){
		SortedMap <Integer, Set<String>> top = counts.tailMap(count);
		Set<String> aggregate = new HashSet();
		for (Set<String> labelsSameCount : top.values()){
			aggregate.addAll(labelsSameCount);
		}
		return aggregate;
	}
	
	public Map<String, RelationList> getGroups() {
		return groups;
	}

	public RelationList getGroup(String label){
		return groups.get(label);
	}

	public int getCount(String label){
		RelationList found = groups.get(label);
		if (found == null){
			return 0;
		}
		else {
			return found.size();
		}
	}

	public int getCount(Relation target){
		String label = target.getLabel();
		return getCount(label);
	}

	/**
	 * Get a pretty representation of this class. 
	 */
	public void prettyPrint() {
		System.out.println("---- Relation Groupings ------");
		for (Map.Entry<Integer, Set<String>> entrySet : counts.entrySet()) {
			Set<String> labels = entrySet.getValue();
			if (labels.isEmpty()){
				continue;
			}
			System.out.println("Relations that showed up " + entrySet.getKey() + " times:");
			for (String label: labels) {
				System.out.println("\t" + label);
			}
		}
		System.out.println("---- End Relations ------");
	}

	/**
	 * Get this class representation as JSON.
	 * @return String this class representation.
	 */
	public String toJSON() {
		return gsonBuilder.create().toJson(this);
	}

	private static GsonBuilder registerGSonBuilder(){
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(RelationCounter.class, new RelationBatchSerializer());
		return builder;
	}

	// TODO: fix reveal json to work after changes
	private static class RelationBatchSerializer implements JsonSerializer<RelationCounter> {
		  @Override
  		public JsonElement serialize(RelationCounter src,
									 Type typeOfSrc,
									 JsonSerializationContext context) {
			// removed array of relations from output  <----
			// JsonObject wrapper = new JsonObject();
			// JsonObject relationsList = new JsonObject();
			Gson gson = new Gson();
			JsonObject grouping = new JsonObject();
			/*
			JsonArray relations = new JsonArray();
			for(RelationList relation: src.relations){
				// relations.add(relation.toJSONElement());
			}
			*/
			for (Map.Entry<String, RelationList> entrySet : src.groups.entrySet()){
				JsonObject group = new JsonObject();
				RelationList relationGroups = entrySet.getValue();
				int count = relationGroups.size();
				// Use the first relation in the list as a representative
				String subject = relationGroups.get(0).getSubjectText();
				String argument = relationGroups.get(0).getArgumentText();
				group.addProperty("entity1", subject);
				group.addProperty("entity2", argument);
				group.addProperty("count", count);
				grouping.add(entrySet.getKey(), group);
			}
			// relationsList.add("relations", relations);
			// relationsList.add("groups", grouping);
			// wrapper.add("relations", relationsList);
			// return wrapper;
			return grouping;
  		}
	}
}
