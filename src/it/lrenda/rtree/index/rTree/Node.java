package it.lrenda.rtree.index.rTree;

import it.lrenda.rtree.objects.MBR.BoundingBox;
import it.lrenda.rtree.objects.MBR.BoundingBoxes;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Luigi
 */
public class Node {
    
    private List<Entry> entries;
    private Entry parentEntry;
    private boolean leaf, multipleModification;
    private int level;

    public Node(Entry parent) {
        entries = new LinkedList<>();
        parentEntry = parent;
        leaf = true;
        multipleModification = false;
    }

    public int level() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public List<Entry> entries() {
        return entries;
    }

    public void setEntries(List<Entry> list) {
        multipleModification = true;
        entries.clear();
        for (Entry e : list){
            addEntry(e);
        }
        multipleModification = false;
        updateMBR();
    }
    
    public boolean isLeaf(){
        return leaf;
    }

    public Entry parentEntry() {
        return parentEntry;
    }
    
    public Node parentNode(){
        return parentEntry.belongingNode();
    }
    
    public void setParentEntry(Entry parentEntry) {
        this.parentEntry = parentEntry;
    }
    
    public void addEntry(Entry e){
        if (leaf && e.child() != null){
            leaf = false;
        }
        e.setBelongingNode(this);
        entries.add(e);
        if (!multipleModification) updateMBR();
    }
    
    public void deleteEntry(Entry e){
        if (!entries.contains(e))
            throw new IllegalArgumentException();
        entries.remove(e);
        updateMBR();
    }

    public BoundingBox getMBR() {
        if (parentEntry == null){
            BoundingBox[] boxes = new BoundingBox[size()];
            for (int i=0;i<size();i++){
                boxes[i] = entries.get(i).getMBR();
            }
            return BoundingBoxes.union(boxes);
        }
        else return parentEntry.getMBR();
    }
    
    public int size(){
        return entries.size();
    }
    
    public void clear(){
        entries.clear();
        updateMBR();
    }
    
    @Override
    public String toString() {
        return "<"+entries.toString()+">";
    }
    
    private void updateMBR(){
        if (parentEntry == null)
            return;
        BoundingBox[] boxes = new BoundingBox[size()];
        for (int i=0;i<size();i++){
            boxes[i] = entries.get(i).getMBR();
        }
        parentEntry.setMBR(BoundingBoxes.union(boxes));
        if (parentNode() == null) 
            return;
        parentNode().updateMBR();
    }
    
}
